package com.example.app_quan_ly_do_an.ui.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_quan_ly_do_an.data.model.*
import com.example.app_quan_ly_do_an.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel : ViewModel() {
    private val repository = InventoryRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. Lấy dữ liệu sản phẩm và lô hàng
            val products = mutableListOf<Product>()
            repository.getAllProducts { products.addAll(it) }
            val lots = repository.getAllInventoryLots()

            // 2. Lấy dữ liệu hóa đơn xuất và nhập (để tính doanh thu, lợi nhuận)
            combine(
                repository.getExportBillsFlow(),
                repository.getImportBillsFlow()
            ) { exportBills, importBills ->
                exportBills to importBills
            }.collect { (exportBills, importBills) ->
                // Lấy chi tiết các hóa đơn xuất để tính toán sản phẩm bán chạy
                val allExportDetails = mutableListOf<ExportBillDetail>()
                for (bill in exportBills) {
                    allExportDetails.addAll(repository.getExportBillDetails(bill.exportBillId))
                }

                calculateStats(products, lots, exportBills, importBills, allExportDetails)
            }
        }
    }

    private fun calculateStats(
        products: List<Product>,
        lots: List<InventoryLot>,
        exportBills: List<ExportBill>,
        importBills: List<ImportBill>,
        exportDetails: List<ExportBillDetail>
    ) {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val thirtyDaysFromNow = calendar.time

        // A. Tổng quan
        val totalRevenue = exportBills.sumOf { it.totalAmount }
        val totalImportAmount = importBills.sumOf { it.totalAmount }
        val totalBills = exportBills.size
        val totalProductsInStock = products.size
        val totalStockQuantity = products.sumOf { it.totalStock }

        // B. Lợi nhuận = Tổng tiền xuất - Tổng tiền nhập
        val totalProfit = totalRevenue - totalImportAmount

        // C. Biểu đồ doanh thu (Gom nhóm theo ngày trong tháng hiện tại)
        val revenueByDay = exportBills
            .filter { it.date != null }
            .groupBy { 
                val cal = Calendar.getInstance()
                cal.time = it.date!!
                cal.get(Calendar.DAY_OF_MONTH)
            }
            .mapValues { entry -> entry.value.sumOf { it.totalAmount } }
        
        val chartData = mutableListOf<Float>()
        for (i in 1..31) {
            chartData.add(revenueByDay[i]?.toFloat() ?: 0f)
        }

        // D. Sản phẩm bán chạy
        val topProducts = exportDetails
            .groupBy { it.productId }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .mapNotNull { (pid, qty) ->
                val p = products.find { it.productId == pid }
                p?.let {
                    DashboardProductData(
                        id = it.productId,
                        name = it.productName,
                        image = it.productImage,
                        subInfo = "${it.totalStock} hàng hóa",
                        topInfo = "%,.0f".format(qty * it.sellPrice),
                        bottomInfo = "Đã bán: $qty"
                    )
                }
            }

        // E. Cảnh báo - Lô hàng hết hạn
        val expiringLots = lots.filter { it.expiryDate != null && it.expiryDate!!.before(thirtyDaysFromNow) && it.currentQuantity > 0 }
            .mapNotNull { lot ->
                val p = products.find { it.productId == lot.productId }
                p?.let {
                    DashboardProductData(
                        id = it.productId,
                        name = it.productName,
                        image = it.productImage,
                        subInfo = "${lot.lotCode} - Hết hạn: ${formatDate(lot.expiryDate!!)}",
                        topInfo = "SL: ${lot.currentQuantity}",
                        bottomInfo = "",
                        extraId = lot.lotId // ID của lô hàng để điều hướng
                    )
                }
            }.take(5)

        // F. Cảnh báo - Tồn kho thấp
        val lowStockProducts = products.filter { it.totalStock <= it.minStock }
            .map {
                DashboardProductData(
                    id = it.productId,
                    name = it.productName,
                    image = it.productImage,
                    subInfo = "Tồn kho: ${it.totalStock} / Min: ${it.minStock}",
                    topInfo = "Cần nhập",
                    bottomInfo = ""
                )
            }.take(5)

        _uiState.value = HomeUiState(
            isLoading = false,
            totalBills = totalBills,
            totalRevenue = totalRevenue,
            totalProfit = totalProfit,
            totalProducts = totalProductsInStock,
            totalStockCount = totalStockQuantity,
            revenueChartData = chartData,
            bestSellers = topProducts,
            expiringProducts = expiringLots,
            lowStockProducts = lowStockProducts
        )
    }

    private fun formatDate(date: Date): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val totalBills: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalProducts: Int = 0,
    val totalStockCount: Int = 0,
    val revenueChartData: List<Float> = emptyList(),
    val bestSellers: List<DashboardProductData> = emptyList(),
    val expiringProducts: List<DashboardProductData> = emptyList(),
    val lowStockProducts: List<DashboardProductData> = emptyList()
)

data class DashboardProductData(
    val id: String,          // Thường là productId
    val name: String,
    val image: String,
    val subInfo: String,
    val topInfo: String,
    val bottomInfo: String,
    val extraId: String = "" // Dùng để lưu lotId hoặc các ID bổ sung
)

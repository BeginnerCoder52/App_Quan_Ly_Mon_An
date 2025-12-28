package com.example.app_quan_ly_do_an.data.repository

import com.example.app_quan_ly_do_an.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersRef = db.collection("users")

    /**
     * Đăng ký tài khoản mới
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            // 1. Tạo tài khoản Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Không thể tạo tài khoản")

            // 2. Cập nhật display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 3. Tạo document User trong Firestore
            val user = User(
                userId = firebaseUser.uid,
                email = email,
                displayName = fullName,
                createdAt = java.util.Date(),
                role = "user"
            )
            usersRef.document(firebaseUser.uid).set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Đăng nhập
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            // 1. Đăng nhập Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Không thể đăng nhập")

            // 2. Lấy thông tin User từ Firestore
            val userDoc = usersRef.document(firebaseUser.uid).get().await()
            val user = userDoc.toObject(User::class.java)
                ?: User(
                    userId = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    createdAt = java.util.Date()
                )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Đăng xuất
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Lấy thông tin user hiện tại
     */
    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null

        return try {
            val userDoc = usersRef.document(firebaseUser.uid).get().await()
            userDoc.toObject(User::class.java) ?: User(
                userId = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: "",
                createdAt = java.util.Date()
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Kiểm tra user đã đăng nhập chưa
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Lấy Firebase Auth instance (nếu cần)
     */
    fun getAuth(): FirebaseAuth {
        return auth
    }
}


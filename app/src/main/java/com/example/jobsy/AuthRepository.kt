package com.example.jobsy

import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val supabase: SupabaseClient) {

    /*suspend fun registerUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                return@withContext response.user != null
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }*/

    suspend fun authenticateUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val user = supabase.from("users")
                    .select(columns = Columns.list("email, password_hash"))
                    .decodeSingle<User>()
                Log.d("Auth", user.toString())

                if (user != null) {
                    val result = BCrypt.verifyer().verify(password.toCharArray(), user.password)
                    return@withContext result.verified
                } else {
                    return@withContext false
                }
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            supabase.auth.signOut()
        }
    }
}
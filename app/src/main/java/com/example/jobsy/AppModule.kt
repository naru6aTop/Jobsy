package com.example.jobsy

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

object AppModule {
    lateinit var supabase: SupabaseClient
        private set

    fun initSupabase(context: Context) {
        supabase = createSupabaseClient(
            supabaseUrl = "https://yppchvnkdaetfspsvnid.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlwcGNodm5rZGFldGZzcHN2bmlkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzg4OTY3ODUsImV4cCI6MjA1NDQ3Mjc4NX0.Y6ed8aZKAepo3ZA5t6TuLGAYKerOgzHFeul2hp263UU"
        ) {
            install(Postgrest)
            install(Auth)
        }
    }
}
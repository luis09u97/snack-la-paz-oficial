package com.snacklapaz.app.data

import com.snacklapaz.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Cliente único do Supabase, usado no app inteiro. A URL e a chave
 * vêm do BuildConfig, que por sua vez lê o local.properties — assim
 * elas nunca ficam escritas direto no código-fonte.
 */
object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}
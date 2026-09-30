package com.snacklapaz.app.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snacklapaz.app.data.SupabaseClientProvider
import com.snacklapaz.app.data.dto.ClienteDto
import com.snacklapaz.app.data.dto.NovoClienteDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

/**
 * Controla o estado de autenticação do app, agora usando o Supabase Auth
 * de verdade (login/cadastro reais, não mais simulados).
 */
class AuthViewModel : ViewModel() {

    private val client = SupabaseClientProvider.client

    var isLoggedIn by mutableStateOf(false)
        private set

    var userName by mutableStateOf("")
        private set

    var userEmail by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAdmin by mutableStateOf(false)
        private set

    init {
        restoreSavedSession()
    }

    private fun restoreSavedSession() {
        isLoading = true
        viewModelScope.launch {
            try {
                client.auth.awaitInitialization()
                client.auth.loadFromStorage(autoRefresh = true)
                val user = client.auth.currentUserOrNull()
                if (user != null) {
                    val email = user.email.orEmpty()
                    onLoggedIn(
                        email = email,
                        name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun login(email: String, password: String) {
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            try {
                client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                onLoggedIn(email, email.substringBefore("@").replaceFirstChar { it.uppercase() })
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "E-mail ou senha inválidos."
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(fullName: String, email: String, password: String) {
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            try {
                client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                val userId = client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    ensureClienteProfile(userId)
                }
                onLoggedIn(email, fullName)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = signUpErrorMessage(e)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun ensureClienteProfile(userId: String): ClienteDto? {
        val existing = client.postgrest["clientes"]
            .select { filter { eq("auth_id", userId) } }
            .decodeSingleOrNull<ClienteDto>()

        if (existing != null) return existing

        return client.postgrest["clientes"]
            .insert(NovoClienteDto(authId = userId)) { select() }
            .decodeSingleOrNull<ClienteDto>()
    }

    private suspend fun onLoggedIn(email: String, name: String) {
        isLoggedIn = true
        userEmail = email
        userName = name
        val userId = client.auth.currentUserOrNull()?.id
        if (userId != null) {
            val cliente = ensureClienteProfile(userId)
            isAdmin = cliente?.isAdmin ?: false
        }
    }

    private fun signUpErrorMessage(error: Exception): String {
        val text = listOfNotNull(error.message, error.cause?.message)
            .joinToString(" ")
            .lowercase()
        return when {
            "already registered" in text || "already exists" in text || "user already" in text ->
                "Esse e-mail já está cadastrado. Tente entrar com sua senha."
            "row level security" in text || "violates row-level" in text ->
                "Sua conta foi criada, mas o perfil não pôde ser salvo por uma regra de segurança do banco."
            "network" in text || "timeout" in text ->
                "Falha de conexão. Verifique a internet e tente novamente."
            else -> "Não foi possível criar a conta. Tente novamente."
        }
    }

    fun logout() {
        viewModelScope.launch {
            client.auth.signOut()
            isLoggedIn = false
            userName = ""
            userEmail = ""
            isAdmin = false
        }
    }
}

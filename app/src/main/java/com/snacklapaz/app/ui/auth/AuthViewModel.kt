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
                    onLoggedIn(email = email)
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
                onLoggedIn(email = email)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "E-mail ou senha inválidos."
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(fullName: String, email: String, phone: String, password: String) {
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
                    ensureClienteProfile(
                        userId = userId,
                        name = fullName,
                        email = email,
                        phone = phone
                    )
                }
                onLoggedIn(email = email, fallbackName = fullName)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = signUpErrorMessage(e)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun ensureClienteProfile(
        userId: String,
        name: String? = null,
        email: String? = null,
        phone: String? = null
    ): ClienteDto? {
        val existing = client.postgrest["clientes"]
            .select { filter { eq("auth_id", userId) } }
            .decodeSingleOrNull<ClienteDto>()

        if (existing != null) {
            updateClienteProfileIfNeeded(existing, name, email, phone)
            return existing.copy(
                nome = existing.nome.ifBlankOrNull(name),
                email = existing.email.ifBlankOrNull(email),
                telefone = existing.telefone.ifBlankOrNull(phone)
            )
        }

        return runCatching {
            client.postgrest["clientes"]
                .insert(
                    NovoClienteDto(
                        authId = userId,
                        nome = name?.takeIf { it.isNotBlank() },
                        email = email?.takeIf { it.isNotBlank() },
                        telefone = phone?.takeIf { it.isNotBlank() }
                    )
                ) { select() }
                .decodeSingleOrNull<ClienteDto>()
        }.getOrElse {
            client.postgrest["clientes"]
                .insert(mapOf("auth_id" to userId)) { select() }
                .decodeSingleOrNull<ClienteDto>()
        }
    }

    private suspend fun updateClienteProfileIfNeeded(
        cliente: ClienteDto,
        name: String?,
        email: String?,
        phone: String?
    ) {
        val updates = buildMap {
            if (cliente.nome.isNullOrBlank() && !name.isNullOrBlank()) put("nome", name)
            if (cliente.email.isNullOrBlank() && !email.isNullOrBlank()) put("email", email)
            if (cliente.telefone.isNullOrBlank() && !phone.isNullOrBlank()) put("telefone", phone)
        }
        if (updates.isNotEmpty()) {
            runCatching {
                client.postgrest["clientes"].update(updates) {
                    filter { eq("id_cliente", cliente.idCliente) }
                }
            }
        }
    }

    private suspend fun onLoggedIn(email: String, fallbackName: String? = null) {
        isLoggedIn = true
        userEmail = email
        val userId = client.auth.currentUserOrNull()?.id
        if (userId != null) {
            val cliente = ensureClienteProfile(userId, fallbackName, email)
            userName = cliente?.nome
                ?.takeIf { it.isNotBlank() }
                ?: fallbackName
                    ?.takeIf { it.isNotBlank() }
                ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
            isAdmin = cliente?.isAdmin ?: false
        } else {
            userName = fallbackName
                ?.takeIf { it.isNotBlank() }
                ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
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

private fun String?.ifBlankOrNull(fallback: String?): String? {
    return takeIf { !it.isNullOrBlank() } ?: fallback?.takeIf { it.isNotBlank() }
}

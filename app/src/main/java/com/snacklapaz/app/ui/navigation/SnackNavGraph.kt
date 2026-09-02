package com.snacklapaz.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.snacklapaz.app.ui.admin.AdminDashboardScreen
import com.snacklapaz.app.ui.admin.AdminSectionScreen
import com.snacklapaz.app.ui.admin.model.adminSections
import com.snacklapaz.app.ui.auth.AuthViewModel
import com.snacklapaz.app.ui.auth.LoginScreen
import com.snacklapaz.app.ui.auth.SignUpScreen
import com.snacklapaz.app.ui.cart.CartScreen
import com.snacklapaz.app.ui.cart.CartViewModel
import com.snacklapaz.app.ui.checkout.AddressScreen
import com.snacklapaz.app.ui.checkout.OrderConfirmationScreen
import com.snacklapaz.app.ui.home.HomeScreen
import com.snacklapaz.app.ui.orders.OrdersScreen
import com.snacklapaz.app.ui.orders.OrderTrackingScreen
import com.snacklapaz.app.ui.profile.ProfileScreen
import com.snacklapaz.app.ui.receipt.ReceiptScreen
import com.snacklapaz.app.ui.search.SearchScreen

// Rotas que fazem parte das 5 abas principais (mostram a bottom bar).
// Fora delas (endereço, confirmação, etc.) a barra fica escondida.
private val mainTabRoutes = setOf(
    Routes.HOME, Routes.SEARCH, Routes.CART, Routes.ORDERS, Routes.PROFILE
)

/**
 * Tela raiz pós-splash: contém a bottom bar (só nas 5 abas principais)
 * + o NavHost com todas as telas do app.
 */
@Composable
fun SnackNavGraph() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Uma única instância, compartilhada entre Home, Carrinho e Checkout.
    val cartViewModel: CartViewModel = viewModel()

    // Uma única instância, compartilhada entre Perfil, Login e Cadastro.
    val authViewModel: AuthViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME
            ) {
                composable(Routes.HOME) { HomeScreen(cartViewModel = cartViewModel) }
                composable(Routes.SEARCH) { SearchScreen(cartViewModel = cartViewModel) }
                composable(Routes.CART) {
                    CartScreen(
                        cartViewModel = cartViewModel,
                        onGoToHomeClick = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onContinueClick = {
                            navController.navigate(Routes.ADDRESS)
                        }
                    )
                }
                composable(Routes.ORDERS) {
                    OrdersScreen(
                        cartViewModel = cartViewModel,
                        onTrackOrderClick = { navController.navigate(Routes.ORDER_TRACKING) }
                    )
                }
                composable(Routes.PROFILE) {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onLoginClick = { navController.navigate(Routes.LOGIN) },
                        onSignUpClick = { navController.navigate(Routes.SIGNUP) },
                        onAdminPanelClick = { navController.navigate(Routes.ADMIN_DASHBOARD) }
                    )
                }

                composable(Routes.LOGIN) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { navController.popBackStack() },
                        onGoToSignUp = {
                            navController.navigate(Routes.SIGNUP) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.SIGNUP) {
                    SignUpScreen(
                        authViewModel = authViewModel,
                        onBackClick = { navController.popBackStack() },
                        onSignUpSuccess = {
                            navController.navigate(Routes.PROFILE) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Routes.ADMIN_DASHBOARD) {
                    // Segunda camada de proteção: mesmo que alguém consiga
                    // disparar a navegação pra essa rota sem passar pelo
                    // botão do Perfil, a tela recusa a mostrar o conteúdo.
                    if (authViewModel.isAdmin) {
                        AdminDashboardScreen(
                            onBackClick = { navController.popBackStack() },
                            onSectionClick = { section ->
                                navController.navigate(Routes.adminSectionRoute(section.id))
                            }
                        )
                    } else {
                        androidx.compose.runtime.LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }
                }

                composable(
                    route = Routes.ADMIN_SECTION,
                    arguments = listOf(navArgument("sectionId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val sectionId = backStackEntry.arguments?.getString("sectionId").orEmpty()
                    val section = adminSections.find { it.id == sectionId }

                    if (section != null) {
                        AdminSectionScreen(
                            title = section.title,
                            icon = section.icon,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }

                composable(Routes.ADDRESS) {
                    AddressScreen(
                        cartViewModel = cartViewModel,
                        onBackClick = { navController.popBackStack() },
                        onOrderConfirmed = { orderNumber, total ->
                            navController.navigate(
                                Routes.orderConfirmationRoute(orderNumber, "%.2f".format(total))
                            ) {
                                // Remove Endereço e Carrinho do histórico, pra "voltar"
                                // não levar de novo pro checkout de um pedido já feito.
                                popUpTo(Routes.CART) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Routes.ORDER_CONFIRMATION,
                    arguments = listOf(
                        navArgument("orderNumber") { type = NavType.StringType },
                        navArgument("total") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val orderNumber = backStackEntry.arguments?.getString("orderNumber").orEmpty()
                    val total = backStackEntry.arguments?.getString("total")?.toDoubleOrNull() ?: 0.0

                    OrderConfirmationScreen(
                        orderNumber = orderNumber,
                        total = total,
                        onViewReceiptClick = { navController.navigate(Routes.RECEIPT) },
                        onTrackOrderClick = {
                            navController.navigate(Routes.ORDER_TRACKING) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Routes.RECEIPT) {
                    ReceiptScreen(
                        order = cartViewModel.lastOrder,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Routes.ORDER_TRACKING) {
                    OrderTrackingScreen(
                        order = cartViewModel.lastOrder,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }

        if (currentRoute in mainTabRoutes) {
            SnackBottomBar(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
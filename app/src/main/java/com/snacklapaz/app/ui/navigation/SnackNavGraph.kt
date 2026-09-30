package com.snacklapaz.app.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
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
import com.snacklapaz.app.ui.checkout.CashPaymentScreen
import com.snacklapaz.app.ui.checkout.OrderConfirmationScreen
import com.snacklapaz.app.ui.checkout.PaymentScreen
import com.snacklapaz.app.ui.checkout.PixPaymentScreen
import com.snacklapaz.app.ui.components.EmptyState
import com.snacklapaz.app.ui.home.HomeScreen
import com.snacklapaz.app.ui.orders.OrdersScreen
import com.snacklapaz.app.ui.orders.OrdersViewModel
import com.snacklapaz.app.ui.orders.OrderTrackingScreen
import com.snacklapaz.app.ui.profile.ProfileDetailScreen
import com.snacklapaz.app.ui.profile.ProfileScreen
import com.snacklapaz.app.ui.receipt.ReceiptScreen
import com.snacklapaz.app.ui.search.SearchScreen
import java.util.Locale

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
    val ordersViewModel: OrdersViewModel = viewModel()
    var pendingCheckoutAfterLogin by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.HOME,
                    enterTransition = { screenEnterTransition() },
                    exitTransition = { screenExitTransition() },
                    popEnterTransition = { screenPopEnterTransition() },
                    popExitTransition = { screenPopExitTransition() }
                ) {
                    composable(Routes.HOME) { HomeScreen(cartViewModel = cartViewModel) }
                    composable(Routes.SEARCH) { SearchScreen(cartViewModel = cartViewModel) }
                    composable(Routes.CART) {
                        CartScreen(
                            cartViewModel = cartViewModel,
                            isLoggedIn = authViewModel.isLoggedIn,
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
                                if (authViewModel.isLoggedIn) {
                                    navController.navigate(Routes.ADDRESS)
                                } else {
                                    pendingCheckoutAfterLogin = true
                                    navController.navigate(Routes.LOGIN)
                                }
                            }
                        )
                    }
                    composable(Routes.ORDERS) {
                        OrdersScreen(
                            ordersViewModel = ordersViewModel,
                            isLoggedIn = authViewModel.isLoggedIn,
                            onLoginClick = { navController.navigate(Routes.LOGIN) },
                            onTrackOrderClick = { order ->
                                ordersViewModel.selectOrder(order)
                                navController.navigate(Routes.ORDER_TRACKING)
                            }
                        )
                    }
                    composable(Routes.PROFILE) {
                        ProfileScreen(
                            authViewModel = authViewModel,
                            cartViewModel = cartViewModel,
                            ordersViewModel = ordersViewModel,
                            onLoginClick = { navController.navigate(Routes.LOGIN) },
                            onSignUpClick = { navController.navigate(Routes.SIGNUP) },
                            onAdminPanelClick = { navController.navigate(Routes.ADMIN_DASHBOARD) },
                            onOrdersClick = { navController.navigate(Routes.ORDERS) },
                            onAddressClick = { navController.navigate(Routes.ADDRESS) },
                            onPaymentClick = { navController.navigate(Routes.PAYMENT) },
                            onCartClick = { navController.navigate(Routes.CART) },
                            onProfileDetailClick = { sectionId ->
                                navController.navigate(Routes.profileDetailRoute(sectionId))
                            }
                        )
                    }

                    composable(
                        route = Routes.PROFILE_DETAIL,
                        arguments = listOf(navArgument("sectionId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val sectionId = backStackEntry.arguments?.getString("sectionId").orEmpty()
                        ProfileDetailScreen(
                            sectionId = sectionId,
                            authViewModel = authViewModel,
                            cartViewModel = cartViewModel,
                            ordersViewModel = ordersViewModel,
                            onBackClick = { navController.popBackStack() },
                            onOrdersClick = { navController.navigate(Routes.ORDERS) },
                            onAddressClick = { navController.navigate(Routes.ADDRESS) },
                            onPaymentClick = { navController.navigate(Routes.PAYMENT) },
                            onCartClick = { navController.navigate(Routes.CART) }
                        )
                    }

                    composable(Routes.LOGIN) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                if (pendingCheckoutAfterLogin) {
                                    pendingCheckoutAfterLogin = false
                                    navController.navigate(Routes.ADDRESS) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                } else {
                                    navController.popBackStack()
                                }
                            },
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
                                if (pendingCheckoutAfterLogin) {
                                    pendingCheckoutAfterLogin = false
                                    navController.navigate(Routes.ADDRESS) {
                                        popUpTo(Routes.SIGNUP) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Routes.PROFILE) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }
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
                            AdminAccessDenied(onBackClick = { navController.popBackStack() })
                        }
                    }

                    composable(
                        route = Routes.ADMIN_SECTION,
                        arguments = listOf(navArgument("sectionId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val sectionId = backStackEntry.arguments?.getString("sectionId").orEmpty()
                        val section = adminSections.find { it.id == sectionId }

                        if (!authViewModel.isAdmin) {
                            AdminAccessDenied(onBackClick = { navController.popBackStack() })
                        } else if (section != null) {
                            AdminSectionScreen(
                                sectionId = section.id,
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
                            onContinueToPayment = { navController.navigate(Routes.PAYMENT) }
                        )
                    }

                    composable(Routes.PAYMENT) {
                        PaymentScreen(
                            cartViewModel = cartViewModel,
                            onBackClick = { navController.popBackStack() },
                            onPixClick = { navController.navigate(Routes.PIX_PAYMENT) },
                            onCashClick = { navController.navigate(Routes.CASH_PAYMENT) },
                            onOrderConfirmed = { orderNumber, total ->
                                navController.navigate(
                                    Routes.orderConfirmationRoute(
                                        orderNumber,
                                        String.format(Locale.US, "%.2f", total)
                                    )
                                ) {
                                    // Remove pagamento, endereço e carrinho do histórico, pra "voltar"
                                    // não levar de novo pro checkout de um pedido já feito.
                                    popUpTo(Routes.CART) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Routes.PIX_PAYMENT) {
                        PixPaymentScreen(
                            cartViewModel = cartViewModel,
                            onBackClick = { navController.popBackStack() },
                            onOrderConfirmed = { orderNumber, total ->
                                navController.navigate(
                                    Routes.orderConfirmationRoute(
                                        orderNumber,
                                        String.format(Locale.US, "%.2f", total)
                                    )
                                ) {
                                    popUpTo(Routes.CART) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Routes.CASH_PAYMENT) {
                        CashPaymentScreen(
                            cartViewModel = cartViewModel,
                            onBackClick = { navController.popBackStack() },
                            onOrderConfirmed = { orderNumber, total ->
                                navController.navigate(
                                    Routes.orderConfirmationRoute(
                                        orderNumber,
                                        String.format(Locale.US, "%.2f", total)
                                    )
                                ) {
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
                            order = ordersViewModel.selectedOrder ?: cartViewModel.lastOrder,
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

        CartAddFeedback(
            animationKey = cartViewModel.addToCartAnimationKey,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AdminAccessDenied(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        EmptyState(
            icon = Icons.Filled.Security,
            title = "Acesso negado",
            description = "Somente contas administrativas podem abrir o painel interno.",
            actionLabel = "Voltar",
            onActionClick = onBackClick
        )
    }
}

private fun screenEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(180)) +
            slideInHorizontally(
                initialOffsetX = { it / 12 },
                animationSpec = tween(220)
            )
}

private fun screenExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(140)) +
            slideOutHorizontally(
                targetOffsetX = { -it / 18 },
                animationSpec = tween(180)
            )
}

private fun screenPopEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(160)) +
            slideInVertically(
                initialOffsetY = { -it / 24 },
                animationSpec = tween(200)
            )
}

private fun screenPopExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(140)) +
            slideOutVertically(
                targetOffsetY = { it / 24 },
                animationSpec = tween(180)
            )
}

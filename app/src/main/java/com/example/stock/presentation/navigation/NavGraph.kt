package com.example.stock.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stock.presentation.home.HomeScreen
import com.example.stock.presentation.inbound.AddInboundScreen
import com.example.stock.presentation.inbound.InboundDetailsScreen
import com.example.stock.presentation.inbound.InboundScreen
import com.example.stock.presentation.outbound.AddOutboundScreen
import com.example.stock.presentation.outbound.OutboundDetailsScreen
import com.example.stock.presentation.outbound.OutboundScreen
import com.example.stock.presentation.stock.ItemMovementScreen
import com.example.stock.presentation.stock.StockListScreen
import com.example.stock.presentation.customer.CustomerListScreen
import com.example.stock.presentation.returned.ReturnedListScreen
import com.example.stock.presentation.returned.AddReturnedScreen
import com.example.stock.presentation.payment.PaymentListScreen
import com.example.stock.presentation.payment.AddPaymentScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Outbound : Screen("outbound")
    object OutboundDetails : Screen("outbound_details")
    object AddOutbound : Screen("add_outbound")
    object Inbound : Screen("inbound")
    object InboundDetails : Screen("inbound_details")
    object AddInbound : Screen("add_inbound")
    object Stock : Screen("stock")
    object ItemMovement : Screen("item_movement/{itemId}/{itemName}") {
        fun createRoute(itemId: Int, itemName: String) = "item_movement/$itemId/$itemName"
    }
    object Customer : Screen("customer")
    object Returned : Screen("returned")
    object AddReturned : Screen("add_returned")
    object Payment : Screen("payment")
    object AddPayment : Screen("add_payment")
}

@Composable
fun StockNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToOutbound = { navController.navigate(Screen.Outbound.route) },
                onNavigateToInbound = { navController.navigate(Screen.Inbound.route) },
                onNavigateToStock = { navController.navigate(Screen.Stock.route) },
                onNavigateToCustomer = { navController.navigate(Screen.Customer.route) },
                onNavigateToReturned = { navController.navigate(Screen.Returned.route) },
                onNavigateToPayment = { navController.navigate(Screen.Payment.route) }
            )
        }
        
        // Outbound
        composable(Screen.Outbound.route) {
            OutboundScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { navController.navigate(Screen.OutboundDetails.route) },
                onNavigateToAddOutbound = { navController.navigate(Screen.AddOutbound.route) }
            )
        }
        composable(Screen.OutboundDetails.route) {
            OutboundDetailsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AddOutbound.route) {
            AddOutboundScreen(onBack = { navController.popBackStack() })
        }

        // Inbound
        composable(Screen.Inbound.route) {
            InboundScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { navController.navigate(Screen.InboundDetails.route) },
                onNavigateToAddInbound = { navController.navigate(Screen.AddInbound.route) }
            )
        }
        composable(Screen.InboundDetails.route) {
            InboundDetailsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AddInbound.route) {
            AddInboundScreen(onBack = { navController.popBackStack() })
        }

        // Stock
        composable(Screen.Stock.route) {
            StockListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToMovement = { id, name -> navController.navigate(Screen.ItemMovement.createRoute(id, name)) }
            )
        }
        composable(
            route = Screen.ItemMovement.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.IntType },
                navArgument("itemName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            ItemMovementScreen(itemId = itemId, itemName = itemName, onBack = { navController.popBackStack() })
        }

        // Customer
        composable(Screen.Customer.route) {
            CustomerListScreen(onBack = { navController.popBackStack() })
        }

        // Returned
        composable(Screen.Returned.route) {
            ReturnedListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { /* TODO */ },
                onNavigateToAdd = { navController.navigate(Screen.AddReturned.route) }
            )
        }
        composable(Screen.AddReturned.route) {
            AddReturnedScreen(onBack = { navController.popBackStack() })
        }

        // Payment
        composable(Screen.Payment.route) {
            PaymentListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Screen.AddPayment.route) }
            )
        }
        composable(Screen.AddPayment.route) {
            AddPaymentScreen(onBack = { navController.popBackStack() })
        }
    }
}

package com.ebf.voyara.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ebf.voyara.ViewModels.LoginViewModel
import com.ebf.voyara.ViewModels.SignupViewModel
import com.ebf.voyara.ViewModels.CreateTripViewModel
import com.ebf.voyara.ViewModels.TripsListViewModel
import com.ebf.voyara.network.AuthService
import com.ebf.voyara.network.TripService
import com.ebf.voyara.ui.*
import com.ebf.voyara.ui.components.BottomNavigationBar
import com.ebf.voyara.utils.rememberTokenManager


sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object CreateTrip : Screen("create_trip")
    data object MyTrips : Screen("my_trips")
    data object ItineraryBuilder : Screen("itinerary_builder")
    data object AskVegaAI : Screen("ask_vega_ai")
    data object ViewDrafts : Screen("view_drafts")
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authService = AuthService()
    val tripService = TripService()
    val tokenManager = rememberTokenManager()
    val draftManager = com.ebf.voyara.utils.rememberDraftManager()

    // Track current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if user is logged in
    val isLoggedIn = tokenManager.hasValidToken()

    // State for login prompt
    var showLoginPrompt by remember { mutableStateOf(false) }

    // State for draft to edit
    var draftToEdit by remember { mutableStateOf<com.ebf.voyara.data.TripDraft?>(null) }

    // Screens that should show bottom nav
    val screensWithBottomNav = listOf(
        Screen.Home.route,
        Screen.MyTrips.route,
        Screen.ItineraryBuilder.route,
        Screen.CreateTrip.route,
        Screen.Profile.route
    )

    val shouldShowBottomNav = currentRoute in screensWithBottomNav

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav) {
                BottomNavigationBar(
                    selectedRoute = currentRoute ?: Screen.Home.route,
                    isLoggedIn = isLoggedIn,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onShowLoginPrompt = {
                        showLoginPrompt = true
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Login.route) {
                    val loginViewModel: LoginViewModel = viewModel {
                        LoginViewModel(authService, tokenManager)
                    }

                    LoginScreen(
                        viewModel = loginViewModel,
                        onNavigateToSignup = {
                            navController.navigate(Screen.Signup.route)
                        },
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Signup.route) {
                    val signupViewModel: SignupViewModel = viewModel {
                        SignupViewModel(authService)
                    }

                    SignupScreen(
                        viewModel = signupViewModel,
                        onNavigateToLogin = {
                            navController.popBackStack()
                        },
                        onSignupSuccess = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Signup.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        tokenManager = tokenManager,
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route)
                        },
                        onNavigateToCreateTrip = {
                            navController.navigate(Screen.CreateTrip.route)
                        },
                        onNavigateToAskVegaAI = {
                            navController.navigate(Screen.AskVegaAI.route)
                        },
                        onNavigateToViewDrafts = {
                            navController.navigate(Screen.ViewDrafts.route)
                        },
                        showLoginPrompt = showLoginPrompt,
                        onDismissLoginPrompt = {
                            showLoginPrompt = false
                        }
                    )
                }

                composable(Screen.MyTrips.route) {
                    val tripsListViewModel: TripsListViewModel = viewModel {
                        TripsListViewModel(tripService, tokenManager)
                    }

                    MyTripsScreen(
                        viewModel = tripsListViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onTripClick = { tripId ->
                            println("Navigate to trip detail: $tripId")
                        }
                    )
                }

                composable(Screen.ItineraryBuilder.route) {
                    ItineraryBuilderScreen()
                }

                composable(Screen.CreateTrip.route) {
                    val createTripViewModel: CreateTripViewModel = viewModel {
                        CreateTripViewModel(tripService, tokenManager)
                    }

                    CreateTripScreen(
                        viewModel = createTripViewModel,
                        onNavigateBack = {
                            draftToEdit = null
                            navController.popBackStack()
                        },
                        onTripCreated = {
                            draftToEdit = null
                            navController.navigate(Screen.MyTrips.route) {
                                popUpTo(Screen.CreateTrip.route) { inclusive = true }
                            }
                        },
                        onNavigateToViewDrafts = {
                            draftToEdit = null
                            navController.navigate(Screen.ViewDrafts.route)
                        },
                        draftToEdit = draftToEdit
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        tokenManager = tokenManager,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onLogout = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.AskVegaAI.route) {
                    AskVegaAIScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.ViewDrafts.route) {
                    ViewDraftsScreen(
                        draftManager = draftManager,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onDraftClick = { draft ->
                            // Set draft to edit and navigate to CreateTrip screen
                            draftToEdit = draft
                            navController.navigate(Screen.CreateTrip.route) {
                                popUpTo(Screen.ViewDrafts.route) { inclusive = false }
                            }
                        },
                        onDeleteDraft = { draftId ->
                            println("Draft deleted: $draftId")
                        }
                    )
                }
            }
        }
    }
}


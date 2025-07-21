package com.david.cityapp.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.david.cityapp.presentation.common.components.Message
import com.david.cityapp.presentation.ui.screens.citylist.CityListScreen
import com.david.cityapp.presentation.ui.screens.citylist.CityListViewModel
import com.david.cityapp.presentation.ui.screens.citymap.CityMapScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = NavigationRoute.CityList.route,
    viewModel: CityListViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Track current screen type and city ID using rememberSaveable to survive configuration changes
    var currentScreen by rememberSaveable { mutableStateOf<ScreenType?>(null) }
    var currentCityId by rememberSaveable { mutableStateOf<Long?>(null) }

    if (isLandscape) {
        // In landscape mode, show list on the left and map on the right
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Left side - City List (40% of screen)
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                // Update selected city when it changes
                LaunchedEffect(Unit) {
                    viewModel.selectedCity.collect { city ->
                        city?.let { currentCityId = it.id }
                    }
                }

                CityListScreen(
                    onCityClick = { city ->
                        currentScreen = ScreenType.MAP
                        currentCityId = city.id
                        viewModel.selectCity(city)
                    },
                    onClickToDetails = { city ->
                        currentScreen = ScreenType.DETAIL
                        currentCityId = city.id
                        viewModel.selectCity(city)
                    },
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 4.dp)
                )
            }

            // Right side - Content (60% of screen)
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                val selectedCity by viewModel.selectedCity.collectAsState()

                when {
                    currentScreen == ScreenType.DETAIL -> {
                        // Vista de detalle
                    }
                    currentScreen == ScreenType.MAP -> {
                        CityMapScreen(
                            cityId = selectedCity?.id ?: 0,
                            onBackClick = { },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 4.dp)
                        )
                    }
                    else -> {
                        Message("Selecciona una ciudad para ver el mapa")
                    }
                }
            }
        }
    } else {
        // En vista vertical se muestra el modo de navegación tradicional
        NavHost(
            navController = navController,
            startDestination = NavigationRoute.Splash.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // Splash Screen
            composable(NavigationRoute.Splash.route) {
                var isPreloading by remember { mutableStateOf(true) }
                val context = LocalContext.current

                val appNavigationHelper = hiltViewModel<AppNavigationViewModel>().appNavigationHelper

                LaunchedEffect(Unit) {
                    appNavigationHelper.preloadCitiesIfNeeded(context)
                    isPreloading = false
                }

                if (!isPreloading) {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationRoute.CityList.route) {
                            popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                        }
                    }
                }
            }
            composable(NavigationRoute.CityList.route) {
                LaunchedEffect(Unit) {
                    currentScreen = ScreenType.LIST
                }
                CityListScreen(
                    onCityClick = { city ->
                        currentScreen = ScreenType.MAP
                        navController.navigate(NavigationRoute.CityMap.createRoute(city.id))
                    },
                    onClickToDetails = { city ->
                        currentScreen = ScreenType.DETAIL
                        navController.navigate(NavigationRoute.CityDetail.createRoute(city.id))
                    },
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(
                route = NavigationRoute.CityMap.route,
                arguments = listOf(navArgument("cityId") { type = NavType.LongType })
            ) { backStackEntry ->
                LaunchedEffect(Unit) {
                    currentScreen = ScreenType.MAP
                }
                val cityId = backStackEntry.arguments?.getLong("cityId") ?: return@composable
                currentCityId = cityId
                CityMapScreen(
                    cityId = cityId,
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable(
                route = NavigationRoute.CityDetail.route,
                arguments = listOf(
                    navArgument("cityId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                LaunchedEffect(Unit) {
                    currentScreen = ScreenType.DETAIL
                }
                val cityId = backStackEntry.arguments?.getLong("cityId") ?: return@composable
                currentCityId = cityId
                // Navegacion a details
            }
        }
    }
}

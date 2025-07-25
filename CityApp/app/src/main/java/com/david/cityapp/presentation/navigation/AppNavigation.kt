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
import com.david.cityapp.presentation.common.components.SplashLottie
import com.david.cityapp.presentation.navigation.components.ScreenType
import com.david.cityapp.presentation.ui.screens.citydetail.CityDetailScreen
import com.david.cityapp.presentation.ui.screens.citylist.CityListScreen
import com.david.cityapp.presentation.ui.screens.citylist.CityListViewModel
import com.david.cityapp.presentation.ui.screens.citymap.CityMapScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = NavigationRoute.CityList.route,
    viewModel: CityListViewModel = hiltViewModel()
) {
    // Maneja el estado de la orientación del dispositivo
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Estado para la pantalla actual y la ciudad seleccionada
    var currentScreen by rememberSaveable { mutableStateOf<ScreenType?>(null) }
    var currentCityId by rememberSaveable { mutableStateOf<Long?>(null) }

    val selectedCity by viewModel.selectedCity.collectAsState()

    // Actualiza la pantalla cuando se selecciona una ciudad
    LaunchedEffect(selectedCity) {
        selectedCity?.let { city ->
            if (currentScreen == null || currentCityId == city.id) {
                currentScreen = if (currentScreen == ScreenType.MAP) ScreenType.MAP else ScreenType.DETAIL
                currentCityId = city.id
            }
        } ?: run {
            currentScreen = ScreenType.LIST
            currentCityId = null
        }
    }

    // Vista en modo horizontal (pantalla dividida)
    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Panel izquierdo: Lista de ciudades
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                LaunchedEffect(Unit) {
                    viewModel.selectedCity.collect { city ->
                        city?.let { currentCityId = it.id }
                    }
                }

                CityListScreen(
                    onCityClick = { city ->
                        currentScreen = ScreenType.MAP
                        viewModel.selectCity(city)
                    },
                    onClickToDetails = { city ->
                        currentScreen = ScreenType.DETAIL
                        viewModel.selectCity(city)
                    },
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 4.dp)
                )
            }

            // Panel derecho: Mapa o Detalle de la ciudad
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {

                when {
                    currentScreen == ScreenType.DETAIL -> {
                        // Vista de detalle
                        CityDetailScreen(
                            cityId = currentCityId?: 0,
                            onBackClick = { },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 4.dp)
                        )
                    }
                    currentScreen == ScreenType.MAP -> {
                        CityMapScreen(
                            cityId = currentCityId?: 0,
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
        // Navegación estándar en modo vertical
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
                SplashLottie(
                    onFinished = { },
                    duration = if (isPreloading) null else 1000L
                )
            }
            composable(NavigationRoute.CityList.route) {
                LaunchedEffect(Unit) {
                    currentScreen = ScreenType.LIST
                }
                CityListScreen(
                    onCityClick = { city ->
                        currentScreen = ScreenType.MAP
                        currentCityId = city.id
                        navController.navigate(NavigationRoute.CityMap.createRoute(city.id))
                    },
                    onClickToDetails = { city ->
                        currentScreen = ScreenType.DETAIL
                        currentCityId = city.id
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
                CityDetailScreen(
                    cityId = cityId,
                    onBackClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

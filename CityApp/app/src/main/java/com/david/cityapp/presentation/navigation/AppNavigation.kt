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
    startDestination: String,
    viewModel: CityListViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
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
            CityListScreen(
                onCityClick = { city ->
                    navController.navigate(NavigationRoute.CityMap.createRoute(city.id))
                },
                onClickToDetails = { city ->
                    navController.navigate(NavigationRoute.CityDetail.createRoute(city.id))
                },
                viewModel = viewModel,
                isLandscape = isLandscape,
                modifier = Modifier
            )
        }

        composable(
            route = NavigationRoute.CityMap.route,
            arguments = listOf(navArgument("cityId") { type = NavType.LongType })
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getLong("cityId") ?: return@composable
            if (isLandscape) {
                CityListScreen(
                    onCityClick = { city ->
                        navController.navigate(NavigationRoute.CityMap.createRoute(city.id))
                    },
                    onClickToDetails = { city ->
                        navController.navigate(NavigationRoute.CityDetail.createRoute(city.id))
                    },
                    viewModel = viewModel,
                    isLandscape = isLandscape,
                    modifier = Modifier
                )
            } else {
                CityMapScreen(
                    cityId = cityId,
                    onBackClick = { navController.navigate(NavigationRoute.CityList.route) },
                    isLandscape = isLandscape,
                )
            }
        }

        composable(
            route = NavigationRoute.CityDetail.route,
            arguments = listOf(
                navArgument("cityId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getLong("cityId") ?: return@composable
            if (isLandscape) {
                CityListScreen(
                    onCityClick = { city ->
                        navController.navigate(NavigationRoute.CityMap.createRoute(city.id))
                    },
                    onClickToDetails = { city ->
                        navController.navigate(NavigationRoute.CityDetail.createRoute(city.id))
                    },
                    viewModel = viewModel,
                    isLandscape = isLandscape,
                    modifier = Modifier
                )
            } else {
                CityDetailScreen(
                    cityId = cityId,
                    onBackClick = { navController.navigate(NavigationRoute.CityList.route) },
                    isLandscape = isLandscape,
                )
            }
        }
    }
}

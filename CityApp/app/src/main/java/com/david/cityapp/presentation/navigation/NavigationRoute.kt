package com.david.cityapp.presentation.navigation

sealed class NavigationRoute(val route: String) {
    object Splash : NavigationRoute("splash")
    object CityList : NavigationRoute("cities")
    object CityMap : NavigationRoute("cityMap/{cityId}") {
        fun createRoute(cityId: Long) = "cityMap/$cityId"
    }
    object CityDetail : NavigationRoute("cityDetail/{cityId}") {
        fun createRoute(cityId: Long) = "cityDetail/$cityId"
    }
}
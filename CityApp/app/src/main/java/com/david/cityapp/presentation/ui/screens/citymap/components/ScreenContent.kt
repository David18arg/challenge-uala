package com.david.cityapp.presentation.ui.screens.citymap.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.david.cityapp.domain.model.City
import org.osmdroid.util.GeoPoint

@Composable
fun ScreenContent(
    city: City,
    mapCenter: GeoPoint,
    mapZoom: Double,
    onMapMoved: (GeoPoint, Double) -> Unit,
    isLandscape: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = if (isLandscape) 4.dp else 88.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("CityMapContent"),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AndroidView(
                center = mapCenter,
                zoom = mapZoom,
                city = city,
                onMapMoved = onMapMoved,
                context = context,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
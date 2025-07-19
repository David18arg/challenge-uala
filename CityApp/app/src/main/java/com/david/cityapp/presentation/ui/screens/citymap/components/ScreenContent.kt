package com.david.cityapp.presentation.ui.screens.citymap.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
    context: Context,
    modifier: Modifier = Modifier.testTag("CityMapContent")
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .testTag("CardContent"),
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
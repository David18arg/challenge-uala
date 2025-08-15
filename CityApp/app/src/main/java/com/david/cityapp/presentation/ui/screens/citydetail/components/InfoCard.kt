package com.david.cityapp.presentation.ui.screens.citydetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.david.cityapp.domain.model.Weather

@Composable
fun InfoCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding( 26.dp)
        ) {
            Text(
                "Detalles del clima",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                // Left column
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    InfoRow(
                        imageUrl = WeatherIcon.FEELS_LIKE.url,
                        label = "Sensación térmica",
                        value = "${weather.main.feelsLike.toInt()}°C"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(
                        imageUrl = WeatherIcon.HUMIDITY.url,
                        label = "Humedad",
                        value = "${weather.main.humidity}%"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(
                        imageUrl = WeatherIcon.PRESSURE.url,
                        label = "Presión",
                        value = "${weather.main.pressure} hPa"
                    )
                }

                // Right column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    InfoRow(
                        imageUrl = WeatherIcon.WIND.url,
                        label = "Viento",
                        value = "${weather.wind.speed} m/s"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(
                        imageUrl = WeatherIcon.VISIBILITY.url,
                        label = "Visibilidad",
                        value = "${weather.visibility / 1000} km"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(
                        imageUrl = WeatherIcon.CLOUDS.url,
                        label = "Nubosidad",
                        value = "${weather.clouds.all}%"
                    )
                }
            }
        }
    }
}
package com.david.cityapp.presentation.ui.screens.citymap.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.david.cityapp.R
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.components.Message
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun AndroidView(
    center: GeoPoint,
    zoom: Double,
    city: City,
    onMapMoved: (GeoPoint, Double) -> Unit,
    context: Context?,
    modifier: Modifier = Modifier
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var marker by remember { mutableStateOf<Marker?>(null) }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(city, center) {
        try {
            Configuration.getInstance().load(
                context,
                context?.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            )
            val currentMapView = mapView ?: return@LaunchedEffect
            marker?.let { currentMapView.overlays.remove(it) }
            val newMarker = Marker(currentMapView).apply {
                position = GeoPoint(city.lat.toDouble(), city.lon.toDouble())
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = city.name
                snippet = city.country
                context?.let { icon = ContextCompat.getDrawable(it, R.drawable.location_pin) }
            }
            currentMapView.overlays.add(newMarker)
            marker = newMarker
            currentMapView.invalidate()
            showError = false
        } catch (e: Exception) {
            showError = true
        }
    }

    if (showError) {
        Message("Error cargando el mapa")
    }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                tag = "MapView"

                addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        onMapMoved(
                            GeoPoint(
                                mapView?.mapCenter?.latitude ?: 0.0,
                                mapView?.mapCenter?.longitude ?: 0.0
                            ),
                            mapView?.zoomLevelDouble ?: zoom
                        )
                        return true
                    }

                    override fun onZoom(event: ZoomEvent?): Boolean {
                        onMapMoved(
                            GeoPoint(
                                mapView?.mapCenter?.latitude ?: 0.0,
                                mapView?.mapCenter?.longitude ?: 0.0
                            ),
                            mapView?.zoomLevelDouble ?: zoom
                        )
                        return true
                    }
                })
                mapView = this
            }
        },
        modifier = modifier.testTag("CityMapView"),
        update = { view ->
            if (view.mapCenter.latitude != center.latitude ||
                view.mapCenter.longitude != center.longitude ||
                view.zoomLevelDouble != zoom
            ) {
                view.controller.animateTo(center)
                view.controller.setZoom(zoom)
            }
        }
    )
}
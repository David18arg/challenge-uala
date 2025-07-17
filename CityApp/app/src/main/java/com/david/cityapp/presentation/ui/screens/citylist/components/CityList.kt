package com.david.cityapp.presentation.ui.screens.citylist.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.components.Loading

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun CityList(
    cities: LazyPagingItems<City>,
    selectedCityId: Long?,
    onCityClick: (City) -> Unit,
    onClickToDetails: (City) -> Unit,
    onToggleFavorite: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        items(
            count = cities.itemCount,
            key = { index ->
                val city = cities[index]
                city?.id ?: index.toLong()
            }
        ) { index ->
            val city = cities[index]
            if (city != null) {
                CityItem(
                    city = city,
                    isSelected = city.id == selectedCityId,
                    onClick = { onCityClick(city) },
                    onClickToDetails = { onClickToDetails(city) },
                    onToggleFavorite = { onToggleFavorite(city) },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        // Show loading indicator when loading more items
        if (cities.loadState.append is LoadState.Loading) {
            item { Loading() }
        }
    }
}
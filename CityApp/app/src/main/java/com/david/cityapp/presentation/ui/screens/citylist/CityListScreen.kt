package com.david.cityapp.presentation.ui.screens.citylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import com.david.cityapp.presentation.common.components.Loading
import com.david.cityapp.presentation.common.components.Message
import kotlin.collections.get

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    viewModel: CityListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cities = viewModel.citiesFlow.collectAsLazyPagingItems()
    val isLoading = cities.loadState.refresh is LoadState.Loading || uiState.isLoading
    val error = when (cities.loadState.refresh) {
        is LoadState.Error -> (cities.loadState.refresh as LoadState.Error).error.message
        else -> uiState.error
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ciudades",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = modifier.testTag("searchTextField"),
                    label = { Text("Buscar ciudades...") },
                    placeholder = { Text("Buscar ciudades...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Start),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading && cities.itemCount == 0) {
                    Loading()
                } else if (error == null && !isLoading && cities.itemCount == 0) {
                    Message("No hay ciudades disponibles")
                } else if (error != null && cities.itemCount == 0) {
                    Message(message = error)
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = modifier.fillMaxSize()
                    ) {
                        items(
                            count = cities.itemCount,
                            key = { index ->
                                val city = cities.getOrNull(index)
                                city?.id ?: index.toLong()
                            }
                        ) { index ->
                            val city = cities[index]
                            if (city != null) {
                                Card(
                                    modifier = modifier.padding(vertical = 4.dp).testTag("CityItem"),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "${city.name}, ${city.country}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                text = "Lat: ${city.lat}, Lon: ${city.lon}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Show loading indicator when loading more items
                        if (cities.loadState.append is LoadState.Loading) {
                            item { Loading() }
                        }
                    }
                }
            }
        }
    }
}
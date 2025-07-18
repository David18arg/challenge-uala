package com.david.cityapp.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.cityapp.presentation.common.theme.CityAppTheme
import com.david.cityapp.presentation.ui.screens.citylist.CityListScreen
import com.david.cityapp.presentation.ui.screens.citylist.CityListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityAppTheme {
                CityListScreen(
                    modifier = Modifier,
                    onCityClick = {},
                    onClickToDetails = {},
                    viewModel = viewModel<CityListViewModel>()
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CityAppTheme {
        Greeting("Android")
    }
}
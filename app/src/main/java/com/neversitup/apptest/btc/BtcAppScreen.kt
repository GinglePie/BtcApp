package com.neversitup.apptest.btc

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neversitup.apptest.btc.price.PriceScreen
import com.neversitup.apptest.btc.ui.NavRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BtcAppScreen() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var textTopBar by remember { mutableStateOf(R.string.app_name) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(textTopBar),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.Black),
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val navController = rememberNavController()
            val viewModel: BtcViewModel = viewModel()
            NavHost(navController, NavRoute.priceRate) {
                composable(NavRoute.priceRate) { PriceScreen(viewModel) }
            }
            navController.addOnDestinationChangedListener { _, navDestination, _ ->
                when (navDestination.route) {
                    null -> R.string.app_name
                    NavRoute.priceRate -> R.string.btc_price
                    NavRoute.exchange -> R.string.exchange
                    else -> R.string.app_name
                }?.let { title ->
                    if (textTopBar != title) textTopBar = title
                }
            }

        }
    }
}
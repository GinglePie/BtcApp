package com.neversitup.apptest.btc.price

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.xaxis.XAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.line.renderer.point.NoPointDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.himanshoe.charty.line.CurveLineChart
import com.neversitup.apptest.btc.BtcUiState
import com.neversitup.apptest.btc.BtcViewModel
import com.neversitup.apptest.btc.GraphUiState

import com.neversitup.apptest.btc.ui.component.DefaultOutlinedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceScreen (vm: BtcViewModel) {
    val contentPadding = WindowInsets
        .systemBars
        .add(WindowInsets(left = 16.dp, top = 32.dp, right = 16.dp, bottom = 16.dp))
        .asPaddingValues()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
//        val btcUiState = vm.btcUiState
//        when (btcUiState) {
//            is BtcUiState.Loading -> {}
//            is BtcUiState.Error -> {}
//            is BtcUiState.Success -> {
//            }
//        }

        val graphUiState = vm.graphUiState
        when (graphUiState) {
            is GraphUiState.Loading -> {}
            is GraphUiState.Success -> {
                item {
                    LineChart(
//                linesChartData = listOf(
//                    LineChartData(points = vm.graphDataList.collectAsState().value[0], lineDrawer = SolidLineDrawer(color = Color.Red)),
//                    LineChartData(points = vm.graphDataList.collectAsState().value[1], lineDrawer = SolidLineDrawer(color = Color.Cyan)),
//                    LineChartData(points = vm.graphDataList.collectAsState().value[2], lineDrawer = SolidLineDrawer(color = Color.Blue))
//                ),
                        linesChartData = listOf(
                            LineChartData(points = vm.graphPointList.collectAsState().value,
                                lineDrawer = SolidLineDrawer(color = Color.Red)),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(start = 5.dp),
                        horizontalOffset = 5f,
                        pointDrawer = NoPointDrawer
                    )
                }
            }
        }



        item {
            Row() {
                for (i in 0..2) {
                    Column(modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        vm.currencyList.collectAsState().value.getOrNull(i)?.let {
                            Text(it.rate)
                            DefaultOutlinedButton(onClick = { vm.setCurrency(i) }) {
                                Text(it.code.uppercase())
                            }
                        }
                    }
                }
            }
        }



        item {
            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {

                val focusManager = LocalFocusManager.current
                TextField(value = vm.priceInput.collectAsState().value ,
                    onValueChange = {
                        vm.updatePrice(it)
                                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    leadingIcon = { Text(vm.currencyDisplayItem.collectAsState().value?.symbol ?: "$") },
                    trailingIcon = { Text(vm.currencyDisplayItem.collectAsState().value?.text ?: "USD") },
                    label = { Text("Amount") },
                    placeholder = { Text("0.00") }
                )
                Text("to", Modifier.padding(12.dp))
                Text(vm.btcCoin.collectAsState().value.toString() + " BTC",
                    fontSize = 22.sp,
                maxLines = 3)
            }

        }


    }
}
package com.neversitup.apptest.btc

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tehras.charts.line.LineChartData
import com.neversitup.apptest.btc.core.model.CurrencyDisplayItem
import com.neversitup.apptest.btc.core.model.PriceListItem
import com.neversitup.apptest.btc.core.network.BtcApi
import com.neversitup.apptest.btc.core.network.model.NetworkCurrencyRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import retrofit2.HttpException
import java.io.IOException

sealed interface BtcUiState {
    data class Success(val data: PriceListItem) : BtcUiState
    object Error : BtcUiState
    object Loading : BtcUiState
}

sealed interface GraphUiState {
    object Loading : GraphUiState
    object Success : GraphUiState
}

class BtcViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var btcUiState: BtcUiState by mutableStateOf(BtcUiState.Loading)
        private set
    var graphUiState: GraphUiState by mutableStateOf(GraphUiState.Loading)
        private set

    var priceList: MutableList<PriceListItem> = mutableListOf()

    private val _graphDataList: MutableStateFlow<List<MutableList<LineChartData.Point>>>
        = MutableStateFlow(listOf( mutableListOf(), mutableListOf(), mutableListOf()))
    val graphDataList = _graphDataList.asStateFlow()

    private val _graphPointList: MutableStateFlow<MutableList<LineChartData.Point>>
            = MutableStateFlow(mutableListOf())
    val graphPointList = _graphPointList.asStateFlow()

    private val _currencyList: MutableStateFlow<List<NetworkCurrencyRate>> = MutableStateFlow(listOf())
    val currencyList = _currencyList.asStateFlow()

    private val _currency: MutableStateFlow<Int> = MutableStateFlow(0)
    val currency = _currency.asStateFlow()

    private val _currencyDisplayItem: MutableStateFlow<CurrencyDisplayItem?> = MutableStateFlow(null)
    val currencyDisplayItem = _currencyDisplayItem.asStateFlow()

    private val _priceInput: MutableStateFlow<String> = MutableStateFlow("")
    val priceInput = _priceInput.asStateFlow()

    private val _btcCoin: MutableStateFlow<Float> = MutableStateFlow(0f)
    val btcCoin = _btcCoin.asStateFlow()

    val iconSymbol = listOf(
        CurrencyDisplayItem("USD", "$"),
        CurrencyDisplayItem("GBP", "£"),
        CurrencyDisplayItem("EUR", "€"))

    init {
        autoGetRemotePrice()
    }

    fun autoGetRemotePrice() {
        getCurrentPrice()
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 100ms
            autoGetRemotePrice()
        }, 60000)
    }

    fun getCurrentPrice() {
        viewModelScope.launch {
            btcUiState = BtcUiState.Loading
            btcUiState = try {
                val result = BtcApi.retrofitService.getBtcPrice()
                val newPrice = PriceListItem(
                    time = result.time.updatedISO,
                    currencyList = listOf(result.bpi.usd,
                        result.bpi.gbp,
                        result.bpi.eur)
                )
                priceList.add(newPrice)


                _currencyList.value = newPrice.currencyList
                setCurrency(0)
                calculatePrice()
                updateGraph(newPrice)
                BtcUiState.Success(
                    newPrice
                )
            } catch (e: IOException) {
                BtcUiState.Error
            } catch (e: HttpException) {
                BtcUiState.Error
            }
        }
    }

    fun setCurrency(id: Int) {
        _currency.value = id
        _currencyDisplayItem.value = iconSymbol[currency.value]
        calculatePrice()
        updateGraph()
    }

    fun updatePrice(price: String) {
        val splitDot = price.replace(",", "").split(".")

        val floatingText = splitDot.getOrNull(1)?.let {
            "." + it.substring(0, minOf(2, it.length))
        } ?: ""
//        _priceInput.value = NumberFormat.getInstance().format(splitDot[0].toBigInteger() ?: 0) + floatingText
        _priceInput.value = splitDot[0] + floatingText

        calculatePrice()
    }

    fun calculatePrice() {
        val rate = priceList.last().currencyList[currency.value].rateFloat
        _btcCoin.value = (_priceInput.value.replace(",", "").toFloatOrNull() ?: 0f) / rate
    }

    fun updateGraph(newItem: PriceListItem) {

        when (graphDataList.value[0].size) {
            0 -> for (i in 0..2) {
                _graphDataList.value[i].add(
                    LineChartData.Point(
                        value = newItem.currencyList[i].rateFloat-1f,
                        label = ""
                    )
                )
            }
            else -> for (i in 0..2) {
                _graphDataList.value[i].clear()
            }
        }

        val rangeLabel = when {
            priceList.size % 2 == 0 -> priceList.size / 3
            else -> priceList.size/2
        }
        for (i in 0 until priceList.size){
            val item = priceList[i]
            val time = item.time.toLocalDateTime(TimeZone.currentSystemDefault())
            val label = when {
                priceList.size < 5 || i == 0 || i == priceList.size-1 || i % rangeLabel == 0->
                    time.hour.toString().padStart(2, '0') + ":" + time.minute.toString().padStart(2, '0')
                else -> ""
            }

            for (j in 0..2) {
                _graphDataList.value[j].add(
                    LineChartData.Point(
                        value = item.currencyList[j].rateFloat,
                        label =  label
                    )
                )
            }
        }

        Log.d("testapp", "usd ${newItem.currencyList.get(0).rateFloat} - ${newItem.currencyList.get(1).rateFloat} - ${newItem.currencyList.get(2).rateFloat}")

        graphUiState = GraphUiState.Loading
        _graphPointList.value.clear()
        _graphPointList.value.addAll(graphDataList.value[currency.value])
        graphUiState = GraphUiState.Success
    }

    fun updateGraph() {
        graphUiState = GraphUiState.Loading
        _graphPointList.value.clear()
        _graphPointList.value.addAll(graphDataList.value[currency.value])
        graphUiState = GraphUiState.Success
    }

}
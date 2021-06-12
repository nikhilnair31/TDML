package com.appnamenull.mlscheduler.Utils

import com.workday.insights.timeseries.arima.Arima
import com.workday.insights.timeseries.arima.struct.ArimaParams
import java.util.*

object Forecasting {
    fun arima(usageList: DoubleArray, forecastSize : Int): DoubleArray{
        val p = 3//0
        val d = 0//1
        val q = 3//2
        val P = 0//2
        val D = 1//0
        val Q = 1//2
        val m = 18
        val ap = ArimaParams(p, d, q, P, D, Q, m)
        val forecastResult = Arima.forecast_arima(usageList, forecastSize, ap)
        val forecastData : DoubleArray = forecastResult.forecast
        val rmse = forecastResult.rmse
        val maxNormalizedVariance = forecastResult.maxNormalizedVariance
        val log = forecastResult.log
        println("FORECASTING arima forecastData.size : ${forecastData.size}\trmse : $rmse\tmaxNormalizedVariance : $maxNormalizedVariance\tlog : $log\t")
        val forecastList = forecastData.toMutableList()
        forecastList.add(0.0)

        return forecastData
    }
}
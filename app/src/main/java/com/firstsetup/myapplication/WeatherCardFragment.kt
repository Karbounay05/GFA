package com.firstsetup.myapplication.weather

import android.graphics.Color
import android.view.Gravity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.firstsetup.myapplication.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class WeatherCardFragment : Fragment() {

    private lateinit var lottieWeather: LottieAnimationView
    private lateinit var spinnerCities: Spinner
    private lateinit var hourSeekBar: SeekBar
    private lateinit var dayButtonsLayout: LinearLayout
    private lateinit var hourlyChart: LineChart
    private lateinit var barChart: BarChart

    private var selectedCity = "Marrakech"
    private var forecastByDay = mutableListOf<List<JSONObject>>()
    private var currentDayIndex = 0

    companion object {
        private const val API_KEY = "e8fb962960b460360ed7c6080e38db24"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_card, container, false)

        lottieWeather = view.findViewById(R.id.lottieWeather)
        spinnerCities = view.findViewById(R.id.spinnerCities)
        hourSeekBar = view.findViewById(R.id.hourSeekBar)
        dayButtonsLayout = view.findViewById(R.id.dayButtonsLayout)
        hourlyChart = view.findViewById(R.id.hourlyChart)
        barChart = view.findViewById(R.id.barChart)

        val villes = listOf("Rabat", "Casablanca", "Marrakech", "Fès", "Tanger", "Kenitra")
        spinnerCities.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, villes)

        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCity = villes[position]
                fetchForecast()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        hourSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (forecastByDay.isNotEmpty() &&
                    currentDayIndex < forecastByDay.size &&
                    progress < forecastByDay[currentDayIndex].size) {

                    val forecast = forecastByDay[currentDayIndex][progress]
                    displayForecast(forecast)
                    drawHourlyChart(forecastByDay[currentDayIndex])
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return view
    }

    private fun fetchForecast() {
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=$selectedCity&units=metric&lang=fr&appid=$API_KEY"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val list = response.optJSONArray("list")
                val daysMap = TreeMap<String, MutableList<JSONObject>>()
                for (i in 0 until list.length()) {
                    val item = list.getJSONObject(i)
                    val timestamp = item.getLong("dt") * 1000
                    val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
                    daysMap.getOrPut(dateKey) { mutableListOf() }.add(item)
                }
                forecastByDay = daysMap.values.toMutableList()
                currentDayIndex = 0
                drawDayCards()
                updateHourSeekBar()
                drawBarChart()
            },
            { error ->
                Log.e("FORECAST", "Erreur: ${error.message}")
                Toast.makeText(requireContext(), "Erreur réseau prévisions", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun drawDayCards() {
        dayButtonsLayout.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        forecastByDay.forEachIndexed { index, dayList ->
            val timestamp = dayList.first().getLong("dt") * 1000
            val tempMax = dayList.maxOf { it.getJSONObject("main").getDouble("temp_max") }
            val tempMin = dayList.minOf { it.getJSONObject("main").getDouble("temp_min") }
            val desc = dayList[0].getJSONArray("weather").getJSONObject(0).getString("description")

            val cardView = inflater.inflate(R.layout.item_forecast_day, dayButtonsLayout, false)

            cardView.findViewById<TextView>(R.id.dayLabel).text = SimpleDateFormat("EEE d", Locale.ENGLISH).format(Date(timestamp))
            cardView.findViewById<TextView>(R.id.maxTemp).text = "${tempMax.toInt()}°"
            cardView.findViewById<TextView>(R.id.minTemp).text = "${tempMin.toInt()}°"

            val iconView = cardView.findViewById<LottieAnimationView>(R.id.weatherIcon)
            iconView.setAnimation(getWeatherIconRes(tempMax, 60, desc))
            iconView.playAnimation()

            cardView.setOnClickListener {
                currentDayIndex = index
                updateHourSeekBar()
                displayForecast(dayList[0])
                drawHourlyChart(dayList)
            }

            dayButtonsLayout.addView(cardView)
        }
    }

    private fun updateHourSeekBar() {
        val hoursList = forecastByDay[currentDayIndex].map {
            val timestamp = it.getLong("dt") * 1000
            SimpleDateFormat("ha", Locale.ENGLISH).format(Date(timestamp)).lowercase()
        }

        hourSeekBar.max = forecastByDay[currentDayIndex].size - 1
        hourSeekBar.progress = 0
        updateHourLabels(hoursList)
    }

    private fun updateHourLabels(hours: List<String>) {
        val hourLabelsLayout = view?.findViewById<LinearLayout>(R.id.hourLabelsLayout)
        hourLabelsLayout?.removeAllViews()

        val layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

        for (label in hours) {
            val textView = TextView(requireContext())
            textView.text = label
            textView.setTextColor(Color.WHITE)
            textView.textSize = 12f
            textView.gravity = Gravity.CENTER
            hourLabelsLayout?.addView(textView, layoutParams)
        }
    }


    private fun displayForecast(obj: JSONObject) {
        val main = obj.getJSONObject("main")
        val temp = main.getDouble("temp")
        val tempMax = main.getDouble("temp_max")
        val tempMin = main.getDouble("temp_min")
        val humidity = main.getInt("humidity")
        val desc = obj.getJSONArray("weather").getJSONObject(0).getString("description")
        val wind = obj.getJSONObject("wind").getDouble("speed")

        val lottieRes = getWeatherIconRes(temp, humidity, desc)
        lottieWeather.setAnimation(lottieRes)
        lottieWeather.playAnimation()

        view?.findViewById<TextView>(R.id.temperatureText)?.text = "${temp.toInt()}°C"
        view?.findViewById<TextView>(R.id.tempRangeText)?.text = "${tempMax.toInt()}° / ${tempMin.toInt()}°"
        view?.findViewById<TextView>(R.id.precipitationText)?.text = "Precipitation: 4%"
        view?.findViewById<TextView>(R.id.windText)?.text = "Wind: ${wind.toInt()} KMPH"
        view?.findViewById<TextView>(R.id.humidityText)?.text = "Humidity: ${humidity}%"
    }

    private fun drawHourlyChart(data: List<JSONObject>) {
        if (data.isEmpty()) return

        val entries = data.mapIndexed { i, obj ->
            val temp = obj.getJSONObject("main").getDouble("temp")
            Entry(i.toFloat(), temp.toFloat())
        }

        val dataSet = LineDataSet(entries, "Température horaire").apply {
            color = Color.parseColor("#2979FF")
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            setDrawCircles(false)
            setDrawFilled(true)
            fillColor = Color.parseColor("#B3D5FF")
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 2f
        }

        hourlyChart.apply {
            val lineData = LineData(dataSet)
            hourlyChart.data = lineData
            setTouchEnabled(true)
            setPinchZoom(false)
            setScaleEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.textColor = Color.BLACK
            axisRight.isEnabled = false
            animateX(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun drawBarChart() {
        val maxEntries = ArrayList<BarEntry>()
        val minEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        forecastByDay.forEachIndexed { index, dayList ->
            val temps = dayList.map { it.getJSONObject("main") }
            val max = temps.maxOf { it.getDouble("temp_max") }
            val min = temps.minOf { it.getDouble("temp_min") }

            maxEntries.add(BarEntry(index.toFloat(), max.toFloat()))
            minEntries.add(BarEntry(index.toFloat(), min.toFloat()))

            val date = dayList[0].getLong("dt") * 1000
            labels.add(SimpleDateFormat("EEE", Locale.getDefault()).format(Date(date)))
        }

        val maxDataSet = BarDataSet(maxEntries, "Temp Max").apply {
            color = Color.parseColor("#FFA726")
        }

        val minDataSet = BarDataSet(minEntries, "Temp Min").apply {
            color = Color.parseColor("#42A5F5")
        }

        val barData = BarData(maxDataSet, minDataSet).apply {
            barWidth = 0.4f
        }

        barChart.apply {
            data = barData
            xAxis.apply {
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            groupBars(0f, 0.2f, 0.02f)
            invalidate()
        }
    }

    private fun getWeatherIconRes(temp: Double, humidity: Int, desc: String): Int {
        return when {
            humidity > 80 -> R.raw.rain
            temp >= 20 -> R.raw.sun
            temp in 15.0..19.9 -> R.raw.cloud
            temp < 15 -> R.raw.night
            desc.contains("storm", true) -> R.raw.storm
            else -> R.raw.night
        }
    }
}

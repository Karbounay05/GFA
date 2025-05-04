package com.firstsetup.myapplication

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.animation.Easing
import com.firstsetup.myapplication.R
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class
SuivreParcelleActivity : AppCompatActivity() {

    private lateinit var progressCircle: ProgressBar
    private lateinit var timerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suivre_parcelle)

        val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        val animationView2 = findViewById<LottieAnimationView>(R.id.lottieAnimationView2)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.REVERSE
        animationView.playAnimation()

        animationView2.repeatCount = LottieDrawable.INFINITE
        animationView2.playAnimation()

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        progressCircle = findViewById(R.id.progress_circle)
        timerText = findViewById(R.id.timer_text)

        val entries = listOf(
            PieEntry(40f, "Blé"),
            PieEntry(25f, "Maïs"),
            PieEntry(20f, "Tomates"),
            PieEntry(15f, "Autres")
        )

        val dataSet = PieDataSet(entries, "Cultures")
        dataSet.colors = listOf(
            Color.parseColor("#FFA726"),
            Color.parseColor("#66BB6A"),
            Color.parseColor("#29B6F6"),
            Color.parseColor("#AB47BC")
        )
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.centerText = "Répartition des cultures"
        pieChart.setCenterTextSize(8f)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(800, Easing.EaseInOutQuad)

        // Spin animation
        pieChart.spin(
            15000,
            0f,
            360f,
            Easing.Linear
        )

        val legend = pieChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.textSize = 8f
        legend.form = Legend.LegendForm.CIRCLE
        legend.formSize = 8f

        // Timer circulaire de 3000 secondes (50 minutes)
        val totalSeconds = 3000
        progressCircle.max = totalSeconds
        progressCircle.progress = totalSeconds

        val timer = object : CountDownTimer(totalSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                progressCircle.progress = secondsLeft

                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                timerText.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                progressCircle.progress = 0
                timerText.text = "00:00"
            }
        }

        timer.start()
        val barChart = findViewById<BarChart>(R.id.barChart)

        val barEntries = listOf(
            BarEntry(0f, 10f),
            BarEntry(1f, 20f),
            BarEntry(2f, 30f),
            BarEntry(3f, 40f)
        )

        val barDataSet = BarDataSet(barEntries, "Cultures")
        barDataSet.color = Color.parseColor("#4CAF50") // green color

        val barData = BarData(barDataSet)
        barData.barWidth = 0.9f

        barChart.data = barData
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }
}

package updated.mysterium.vpn.ui.monitoring

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import network.mysterium.ui.UnitFormatter
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityMonitoringBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.DataUtil
import updated.mysterium.vpn.common.date.DateUtil
import updated.mysterium.vpn.common.extensions.toIntWithoutRounding
import updated.mysterium.vpn.model.session.Session
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import java.util.*

class MonitoringActivity : BaseActivity() {

    private companion object {
        const val TAG = "MonitoringActivity"
        const val LINE_WIDTH = 2f
        const val CIRCLE_RADIUS = 4f
        const val TEXT_SIZE = 12f
        const val LINE_LENGTH = 5f
        const val SPACE_LENGTH = 10f
        const val SPACE_PHASE_LENGTH = 50f
        const val YAXIS_SIZE = 6
    }

    private lateinit var binding: ActivityMonitoringBinding
    private val viewModel: MonitoringViewModel by inject()
    private val sessionsAdapter = SessionsAdapter()
    private var dataType = "B" // Bytes - smaller data type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        initSessionsRecycler()
        getLastSessions()
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
    }

    private fun setUpLineChart(sessions: List<Session>) {
        val entries = mutableListOf<Entry>()
        sessions.forEachIndexed { index, session ->
            entries.add(
                Entry(
                    index.toFloat(),
                    DataUtil.convertDataToDataType(session.dataReceived, dataType)
                )
            )
        }
        inflateXAxis(binding.sessionChart.xAxis, sessions)
        inflateAxisLeft(binding.sessionChart.axisLeft)
        inflateAxisRight(binding.sessionChart.axisRight)
        binding.sessionChart.apply {
            data = LineData(getDataSet(entries))
            legend.isEnabled = false
            setNoDataText(getString(R.string.monitoring_empty_line_chart))
            setNoDataTextColor(getColor(R.color.manual_connect_value_white))
            setDrawGridBackground(false)
            setDrawBorders(false)
            description = Description().apply {
                text = ""
            }
            marker = MarkerImage(context, R.drawable.marker)
            setTouchEnabled(false)
            isClickable = false
            invalidate()
        }

    }

    private fun initSessionsRecycler() {
        binding.lastSessionsRecycler.isNestedScrollingEnabled = false
        binding.lastSessionsRecycler.apply {
            layoutManager = LinearLayoutManager(this@MonitoringActivity)
            adapter = sessionsAdapter
        }
    }

    private fun getLastSessions() {
        viewModel.getLastSessions().observe(this, { result ->
            result.onSuccess {
                sessionsAdapter.replaceAll(it)
                groupSessions(it)
            }
            result.onFailure {
                Log.i(TAG, it.localizedMessage ?: it.toString())
                // TODO("Implement error handling")
            }
        })
    }

    private fun getDataSet(entries: List<Entry>): LineDataSet {
        val dataSet = LineDataSet(entries, "")
        dataSet.axisDependency = AxisDependency.LEFT
        dataSet.color = getColor(R.color.primary)
        dataSet.setCircleColor(getColor(R.color.menu_subtitle_light_pink))
        dataSet.lineWidth = LINE_WIDTH
        dataSet.circleRadius = CIRCLE_RADIUS
        dataSet.valueTextColor = Color.TRANSPARENT
        dataSet.setDrawCircleHole(false)
        return dataSet
    }

    private fun inflateXAxis(xAxis: XAxis, sessions: List<Session>) {
        xAxis.mAxisMaximum = sessions.size.toFloat() - 1
        xAxis.mAxisMinimum = 0f
        if (sessions.size == 1) {
            xAxis.mAxisRange = sessions.size.toFloat() - 1
            xAxis.setLabelCount(sessions.size, false)
        } else {
            xAxis.mAxisRange = sessions.size.toFloat()
            xAxis.setLabelCount(sessions.size, true)
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getAxisLabel(value: Float, axis: AxisBase?) = sessions.map {
                DateUtil.formatDate(it.createdAt)
            }.getOrNull(value.toIntWithoutRounding()) ?: ""
        }
        xAxis.setDrawGridLines(false)
        xAxis.textColor = getColor(R.color.manual_connect_value_white)
        xAxis.axisLineColor = Color.TRANSPARENT
        xAxis.textSize = TEXT_SIZE
    }

    private fun inflateAxisLeft(axisLeft: YAxis) {
        axisLeft.setLabelCount(YAXIS_SIZE, false)
        axisLeft.textColor = getColor(R.color.manual_connect_value_white)
        axisLeft.axisLineColor = Color.TRANSPARENT
        axisLeft.gridColor = getColor(R.color.manual_connect_value_white)
        axisLeft.enableGridDashedLine(LINE_LENGTH, SPACE_LENGTH, SPACE_PHASE_LENGTH)
        axisLeft.textSize = TEXT_SIZE
    }

    private fun inflateAxisRight(axisRight: YAxis) {
        axisRight.setDrawGridLines(false)
        axisRight.axisLineColor = Color.TRANSPARENT
        axisRight.setDrawLabels(false)
        axisRight.isEnabled = true
    }

    private fun groupSessions(sessions: List<Session>) {
        val groupedSessionsList = mutableListOf<Session>()
        sessions.forEach { session ->
            val sessionWithSameDate = groupedSessionsList.find {
                DateUtil.formatDate(it.createdAt) == DateUtil.formatDate(session.createdAt)
            }
            if (sessionWithSameDate != null) {
                groupedSessionsList.remove(sessionWithSameDate)
                groupedSessionsList.add(sessionWithSameDate.copy(
                    dataReceived = sessionWithSameDate.dataReceived + session.dataReceived
                ))
            } else {
                groupedSessionsList.add(session)
            }
        }
        setBiggestDataType(groupedSessionsList)
        setUpLineChart(groupedSessionsList.sortedBy { it.createdAt })
    }

    private fun setBiggestDataType(sessions: List<Session>) {
        var dataAmount = sessions.first().dataReceived
        dataType = UnitFormatter.bytesDisplay(sessions.first().dataReceived).units
        sessions.forEach {
            if (it.dataReceived > dataAmount) {
                dataType = UnitFormatter.bytesDisplay(it.dataReceived).units
                dataAmount = it.dataReceived
            }
        }
        binding.dataTypeTextView.text = dataType
    }
}

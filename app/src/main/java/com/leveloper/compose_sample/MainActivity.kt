package com.leveloper.compose_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*
import kotlin.concurrent.timer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = viewModel<MainViewModel>()

            MainScreen(
                sec = viewModel.sec.value,
                milli = viewModel.milli.value,
                isRunning = viewModel.isRunning.value,
                lapTimes = viewModel.lapTimes.value,
                onReset = viewModel::reset,
                onToggle = { isRunning ->
                    if (isRunning) viewModel.pause() else viewModel.start()
                },
                onLapTime = viewModel::recodeLapTime
            )
        }
    }
}

@Composable
fun MainScreen(
    sec: Int,
    milli: Int,
    isRunning: Boolean,
    lapTimes: List<String>,
    onReset: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onLapTime: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "스탑워치") })
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = "$sec", fontSize = 100.sp)
                Text(text = "$milli")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                lapTimes.forEach { lapTime ->
                    Text(text = lapTime)
                }
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { onReset() },
                    backgroundColor = Color.Red
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_refresh_24),
                        contentDescription = "reset"
                    )
                }

                FloatingActionButton(
                    onClick = { onToggle(isRunning) },
                    backgroundColor = Color.Green
                ) {
                    Image(
                        painter = painterResource(id = if (isRunning)
                            R.drawable.ic_baseline_pause_24
                        else
                            R.drawable.ic_baseline_play_arrow_24
                        ),
                        contentDescription = "reset"
                    )
                }

                Button(onClick = { onLapTime() }) {
                    Text(text = "랩 타임")
                }
            }
        }
    }
}

@Preview
@Composable
fun abc() {
    MainScreen(
        sec = 20,
        milli = 98,
        isRunning = true,
        lapTimes = emptyList(),
        onReset = { /*TODO*/ },
        onToggle = {}
    ) {

    }
}

class MainViewModel : ViewModel() {
    private var time = 0
    private var timerTask: Timer? = null

    private val _sec = mutableStateOf(0)
    val sec: State<Int> get() = _sec

    private val _milli = mutableStateOf(0)
    val milli: State<Int> get() = _milli

    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> get() = _isRunning

    private val _lapTimes = mutableStateOf(mutableListOf<String>())
    val lapTimes: State<List<String>> = _lapTimes

    private var lap = 1

    fun start() {
        _isRunning.value = true

        timerTask = timer(period = 10) {
            time++
            _sec.value = time / 100
            _milli.value = time % 100
        }
    }

    fun pause() {
        _isRunning.value = false
        timerTask?.cancel()
    }

    fun reset() {
        _isRunning.value = false
        timerTask?.cancel()

        time = 0
        _sec.value = 0
        _milli.value = 0
        _lapTimes.value.clear()
        lap = 1
    }

    fun recodeLapTime() {
        _lapTimes.value.add(0, "$lap LAP : ${sec.value}.${milli.value}")
        lap++
    }

}
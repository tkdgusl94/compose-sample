package com.leveloper.compose_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.math.pow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = viewModel<BmiViewModel>()
            val navController = rememberNavController()

            val bmi = viewModel.bmi.value

            NavHost(navController = navController, startDestination = "home") {
                composable(route = "home") {
                    HomeScreen { height, weight ->
                        viewModel.calculateBmi(height, weight)

                        navController.navigate("result")
                    }
                }
                composable(route = "result") {
                    ResultScreen(navController, bmi)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onClickResult: (Double, Double) -> Unit
) {
    val height = rememberSaveable { mutableStateOf("") }
    val weight = rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "비만도 계산기") }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = height.value,
                onValueChange = { height.value = it },
                label = { Text(text = "키") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = weight.value,
                onValueChange = { weight.value = it },
                label = { Text(text = "몸무게") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (height.value.isNotEmpty() && weight.value.isNotEmpty()) {
                        onClickResult(height.value.toDouble(), weight.value.toDouble())
                    }

                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "결과")
            }
        }
    }
}

@Composable
fun ResultScreen(
    navController: NavController,
    bmi: Double
) {
    val text = when {
        bmi >= 35 -> "고도비만"
        bmi >= 30 -> "2단계 비만"
        bmi >= 25 -> "1단계 비만"
        bmi >= 23 -> "과체중"
        bmi >= 18.5 -> "정상"
        else -> "저체중"
    }

    val imageRes = when {
        bmi >= 23 -> R.drawable.ic_baseline_sentiment_very_dissatisfied_24
        bmi >= 18.5 -> R.drawable.ic_baseline_sentiment_satisfied_24
        else -> R.drawable.ic_baseline_sentiment_dissatisfied_24
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "비만도 계산기") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "home",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }
}

class BmiViewModel : ViewModel() {

    private val _bmi = mutableStateOf(0.0)
    val bmi: State<Double> = _bmi

    fun calculateBmi(
        height: Double,
        weight: Double
    ) {
        _bmi.value = weight / (height / 100.0).pow(2.0)
    }
}

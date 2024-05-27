package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.Components.InputField
import com.example.tipcalculator.Utils.calculateTotalPerPerson
import com.example.tipcalculator.Utils.calculateTotalTip
import com.example.tipcalculator.Widget.RoundIconButton
import com.example.tipcalculator.ui.theme.JetTipAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson:Double = 134.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))), color = Color(0xFFE9D7F7)
    ) {
        Column(modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person", style = MaterialTheme.typography.body2)
            Text(text = "$$total",
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}


@Composable
fun MainContent(){
    BillForm {

    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange:(String) -> Unit){
    var totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()

    }
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()
    val splitStateValue = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val tottalPersonState = remember {
        mutableStateOf(0.0)
    }
    val range =  IntRange(start = 1, endInclusive = 100)

    val keyboardController = LocalSoftwareKeyboardController.current
    TopHeader()
    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            InputField(valueState = totalBillState, labelId = "Entered Bill", enabled = true, isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            //if (validState){
            Row(modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start) {
                Text(text = "Split",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(start = 9.dp))
                Spacer(modifier = Modifier.width(120.dp))
                Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End) {
                    RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                        splitStateValue.value = if (splitStateValue.value > 1) splitStateValue.value - 1 else 1

                    })
                    Text(text = "${splitStateValue.value}", modifier = Modifier
                        .align(CenterVertically)
                        .padding(start = 9.dp, end = 9.dp))
                    RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                        if (splitStateValue.value < range.last)
                        {
                            splitStateValue.value = splitStateValue.value + 1
                        }
                    })

                }
            }

            Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                Text(text = "Tip", modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(start = 9.dp))
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "$  ${tipAmountState.value}",modifier = Modifier.align(alignment = Alignment.CenterVertically))
            }
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$tipPercentage %")

                Spacer(modifier = Modifier.height(14.dp))

                Slider(value = sliderPositionState.value, onValueChange = {newVal ->
                    sliderPositionState.value = newVal
                    tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)
                    tottalPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitStateValue.value, tipPercentage = tipPercentage)
                },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 5,
                    onValueChangeFinished = {

                    }
                )

            }
        }

    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MainContent()
    }
}
package com.dairymaster.composetip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dairymaster.composetip.components.InputField
import com.dairymaster.composetip.ui.theme.ComposeTipTheme
import com.dairymaster.composetip.util.calculateTotal
import com.dairymaster.composetip.util.calculateTotalTip
import com.dairymaster.composetip.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun MyApp(context: @Composable () -> Unit) {
    ComposeTipTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column() {
                MainContent()
            }

        }
    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(all = 16.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(16.dp))),
        color = Color(0xFFE9CFFF)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {
    val splitByState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }

    val range = IntRange(start = 1, endInclusive = 10)
    BillForm(
        range = range,
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPerson = totalPerPerson
    ) { }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPerson: MutableState<Double>,
    onValChanged: (String) -> Unit = {}
) {
    val sliderPositionState = remember {
        mutableStateOf(0.0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    TopHeader(totalPerPerson = totalPerPerson.value)

    Surface(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) {
                        return@KeyboardActions
                    }
                    onValChanged(totalBillState.value.trim())
                    keyboardController?.hide()
                }, modifier = Modifier.fillMaxWidth()
            )
            if (validState) {


                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {

                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByState.value > range.first) {
                                    splitByState.value = splitByState.value - 1

                                    totalPerPerson.value = calculateTotal(
                                        totalBill = totalBillState.value.trim().toDouble(),
                                        splitBy = splitByState.value,
                                        percent = tipPercentage
                                    )
                                } else {
                                    splitByState.value = 1
                                }
                            })

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(text = splitByState.value.toString())

                        Spacer(modifier = Modifier.width(20.dp))

                        RoundIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value = splitByState.value + 1

                                    totalPerPerson.value = calculateTotal(
                                        totalBill = totalBillState.value.trim().toDouble(),
                                        splitBy = splitByState.value,
                                        percent = tipPercentage
                                    )
                                }
                            })
                    }
                }

                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "$${tipAmountState.value}", modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(alignment = Alignment.CenterVertically)
                    )
                }


                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage%")
                    Spacer(modifier = Modifier.height(32.dp))
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal

                            tipAmountState.value = calculateTotalTip(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = tipPercentage
                            )

                            totalPerPerson.value = calculateTotal(
                                totalBill = totalBillState.value.trim().toDouble(),
                                splitBy = splitByState.value,
                                percent = tipPercentage
                            )

                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        steps = 5
                    )
                }

            } else {
                Box {}
            }
        }
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTipTheme {
        MyApp {
        }

    }
}
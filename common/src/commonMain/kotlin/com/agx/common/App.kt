package com.agx.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.agx.common.db.external.InfluxDBClientSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val platformName = getPlatformName()


    Column {
        Button(onClick = {
            text = "Hello, ${platformName}all"
        }) {
            Text(text)
        }

        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                InfluxDBClientSingleton.testing()
            }
        }) {
            Text("Connect to FluxDB")
        }
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                //InfluxDBClientSingleton.writeData()
            }
        }) {
            Text("Write to FluxDB")
        }
    }


}

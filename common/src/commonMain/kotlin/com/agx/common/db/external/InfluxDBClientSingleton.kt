package com.agx.common.db.external

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.runBlocking
import java.time.Instant


object InfluxDBClientSingleton {
    private const val bucket = "TestingBucket"
    private const val org = "Testing"
    private const val token = "PZsLjd85Yv3DDY-JrWbdjsaaL7o0_m4Iney7VxPIB9-W084LI9LPbPUD6msCQhGelPhs5LQQGxtD_esyqigMNA=="

    fun testing() {
        runBlocking {


            //
            // Initialize client
            //
            val client = InfluxDBClientKotlinFactory
                .create("http://localhost:8086", token.toCharArray(), org, bucket)

            client.use {

                val writeApi = client.getWriteKotlinApi()

                //
                // Write by Data Point
                //
                val point = Point.measurement("temperature")
                    .addTag("location", "west")
                    .addField("value", 55.0)
                    .time(Instant.now().toEpochMilli(), WritePrecision.MS)

                writeApi.writePoint(point)

                //
                // Write by LineProtocol
                //
                writeApi.writeRecord("temperature,location=north value=60.0", WritePrecision.NS)

                //
                // Write by DataClass
                //
                val temperature = Temperature("south", 62.0, Instant.now())

                writeApi.writeMeasurement(temperature, WritePrecision.NS)

                //
                // Query results
                //
                val fluxQuery =
                    """from(bucket: "$bucket") |> range(start: 0) |> filter(fn: (r) => (r["_measurement"] == "temperature"))"""

                client
                    .getQueryKotlinApi()
                    .query(fluxQuery)
                    .consumeAsFlow()
                    .collect { println("Measurement: ${it.measurement}, value: ${it.value}") }
            }
        }


    }
    @Measurement(name = "temperature")
    data class Temperature(
        @Column(tag = true) val location: String,
        @Column val value: Double,
        @Column(timestamp = true) val time: Instant
    )
}
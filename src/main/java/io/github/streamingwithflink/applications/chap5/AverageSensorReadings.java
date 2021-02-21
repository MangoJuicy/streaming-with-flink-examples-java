/*
 * Copyright 2015 Fabian Hueske / Vasia Kalavri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.streamingwithflink.applications.chap5;

import io.github.streamingwithflink.function.TemperatureAverager;
import io.github.streamingwithflink.model.SensorReading;
import io.github.streamingwithflink.function.source.SensorSource;
import io.github.streamingwithflink.function.SensorTimeExtractor;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * chap 5 example
 * 求取每个 sensor 每 2 秒钟的温度平均值。允许 record 有 5 秒延迟到达。
 */
public class AverageSensorReadings {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000L); // configure watermark interval

        DataStream<SensorReading> sensorData = env
                .addSource(new SensorSource())
                // assign timestamps and watermarks which are required for event time
                .assignTimestampsAndWatermarks(new SensorTimeExtractor());

        DataStream<SensorReading> avgTemp = sensorData
                // convert Fahrenheit to Celsius using and inlined map function
                .map(r -> new SensorReading(r.id, r.timestamp, (r.temperature - 32) * (5.0 / 9.0)))
                // organize stream by sensor
                .keyBy(r -> r.id)
                // group readings in 1 second windows
                .timeWindow(Time.seconds(2))
                // compute average temperature using a user-defined function
                .apply(new TemperatureAverager());

        avgTemp.print();

        env.execute("Compute average sensor temperature");
    }

}

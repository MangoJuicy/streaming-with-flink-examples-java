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

import io.github.streamingwithflink.function.transform.multistream.RaiseAlertFlatMap;
import io.github.streamingwithflink.model.Alert;
import io.github.streamingwithflink.model.SmokeLevel;
import io.github.streamingwithflink.function.source.SmokeLevelSource;
import io.github.streamingwithflink.model.SensorReading;
import io.github.streamingwithflink.function.source.SensorSource;
import io.github.streamingwithflink.function.SensorTimeExtractor;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * chap 5, MultiStream
 * <p>
 * A simple application that outputs an alert whenever there is a high risk of fire.
 * The application receives the stream of temperature sensor readings and a stream of smoke level measurements.
 * When the temperature is over a given threshold and the smoke level is high, we emit a fire alert.
 */
public class MultiStreamTransformations {

    public void execute(StreamExecutionEnvironment configuredEnv) throws Exception {

        // ingest sensor stream, group sensor readings by sensor id
        KeyedStream<SensorReading, String> keyedTempReadings = configuredEnv
                .addSource(new SensorSource())
                .assignTimestampsAndWatermarks(new SensorTimeExtractor())
                .keyBy(r -> r.id);

        // ingest smoke level stream
        DataStream<SmokeLevel> smokeReadings = configuredEnv
                .addSource(new SmokeLevelSource())
                .setParallelism(1);

        // connect the two streams and raise an alert if the temperature and smoke levels are high
        DataStream<Alert> alerts = keyedTempReadings
                .connect(smokeReadings.broadcast()) // NOTE: None-keyed stream, broadcast to copy events to all operators
                .flatMap(new RaiseAlertFlatMap());  // Interface CoFlatMapFunction<IN1, IN2, R>


        alerts.print();
        configuredEnv.execute("Multi-Stream Transformations Example");
    }

}

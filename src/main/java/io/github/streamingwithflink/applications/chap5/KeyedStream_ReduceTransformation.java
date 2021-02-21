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

import io.github.streamingwithflink.model.SensorReading;
import io.github.streamingwithflink.function.source.SensorSource;
import io.github.streamingwithflink.function.SensorTimeExtractor;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * chap 5 example, KeyedStream reduce.
 *
 * Reduce transformation is a generalization of the rolling aggregation.
 * 可以使用 reduce with ReduceFunction，来代替 sum/min/max 等 KeyedStream transformation.
 */
public class KeyedStream_ReduceTransformation {

    /* 求取每个 sensor 的当前最大温度值 */
    public void execute(StreamExecutionEnvironment configuredEnv) throws Exception {

        DataStream<SensorReading> readings = configuredEnv
                .addSource(new SensorSource())
                .assignTimestampsAndWatermarks(new SensorTimeExtractor());

        KeyedStream<SensorReading, String> keyedStream = readings
                .keyBy(r -> r.id);  // Interface KeySelector<T, K>: K getKey(T var)

        // a rolling reduce that computes the highest temperature of each sensor and the corresponding timestamp
        DataStream<SensorReading> maxTempPerSensor = keyedStream
                .reduce(    // Interface ReduceFunction<T>: T reduce(T r1, T r2)
                        (r1, r2) -> (r1.temperature > r2.temperature) ? r1 : r2
                );

        // NOTE: 每个 intput record 都会返回一个 new output record with 当前最高温度


        maxTempPerSensor.print();
        configuredEnv.execute("Keyed Transformations Example");
    }
}

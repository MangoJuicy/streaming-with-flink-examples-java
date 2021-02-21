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

import io.github.streamingwithflink.function.transform.basic.IdExtractor;
import io.github.streamingwithflink.function.transform.basic.IdSplitter;
import io.github.streamingwithflink.function.transform.basic.TemperatureFilter;
import io.github.streamingwithflink.model.SensorReading;
import io.github.streamingwithflink.function.source.SensorSource;
import io.github.streamingwithflink.function.SensorTimeExtractor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * chap 5 example
 * Example program to demonstrate simple transformation functions: filter, map, and flatMap.
 */
public class BasicTransformations {

    public void execute(StreamExecutionEnvironment configuredEnv) throws Exception {

        DataStream<SensorReading> readings = configuredEnv
                .addSource(new SensorSource())
                .assignTimestampsAndWatermarks(new SensorTimeExtractor());

        DataStream<SensorReading> readings2 = configuredEnv
                .addSource(new SensorSource())
                .assignTimestampsAndWatermarks(new SensorTimeExtractor());

        /* --------------------------- FilterFunction ----------------------------------- */

        DataStream<SensorReading> filteredReadings = readings
                .filter(r -> r.temperature >= 25);

        DataStream<SensorReading> filteredReadings2 = readings2
                .filter(new TemperatureFilter(25));

        /* --------------------------- MapFunction ----------------------------------- */

        DataStream<String> sensorIds = filteredReadings
                .map(r -> r.id);

        DataStream<String> sensorIds2 = filteredReadings2
                .map(new IdExtractor());

        /* --------------------------- FlatMapFunction ----------------------------------- */

        DataStream<String> splitIds = sensorIds
                .flatMap((FlatMapFunction<String, String>)
                        (id, out) -> {
                            for (String s : id.split("_")) {
                                out.collect(s);
                            }
                        })
                .returns(Types.STRING); // provide result type because Java cannot infer return type of lambda function

        /*
        NOTE:

            Functions such as flatMap() with a signature void flatMap(IN value, Collector<OUT> out) are
            compiled into void flatMap(IN value, Collector out) by the Java compiler.
            This makes it impossible for Flink to infer the type information for the output type automatically.

            In this case, the type information needs to be specified explicitly, otherwise the output will be
            treated as type Object which leads to inefficient serialization.
         */

        DataStream<String> splitIds2 = sensorIds2
                .flatMap(new IdSplitter())
                // .returns(Types.STRING) // TODO: needed or not here? me manually added.
                ;


        splitIds.print();
        splitIds2.print();
        configuredEnv.execute("Basic Transformations Example");
    }
}

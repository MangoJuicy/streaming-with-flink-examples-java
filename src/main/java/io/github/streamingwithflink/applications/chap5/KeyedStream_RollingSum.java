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

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * chap 5 example，KeyedStream rolling sum.
 */
public class KeyedStream_RollingSum {

    public void execute(StreamExecutionEnvironment env) throws Exception {

        DataStream<Tuple3<Integer, Integer, Integer>> inputStream =
                env.fromElements(
                        Tuple3.of(1, 2, 2),
                        Tuple3.of(2, 3, 1),
                        Tuple3.of(2, 2, 4),
                        Tuple3.of(1, 5, 3)
                );

        DataStream<Tuple3<Integer, Integer, Integer>> resultStream = inputStream
                .keyBy(0)       // key on first field of tuples
                .sum(1);   // sum the second field of the tuple

        // 每一个 record 都会对应计算新的sum，并输出一个 sum record

        resultStream.print();
        env.execute();
    }
}

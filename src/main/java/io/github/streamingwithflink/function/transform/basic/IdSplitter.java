package io.github.streamingwithflink.function.transform.basic;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * User-defined FlatMapFunction that splits a sensor's id String into a prefix and a number.
 */
public class IdSplitter implements FlatMapFunction<String, String> {

    @Override
    public void flatMap(String id, Collector<String> out) {

        String[] splits = id.split("_");

        for (String split : splits) {
            out.collect(split);
        }
    }
}

package io.github.streamingwithflink.function.transform.basic;

import io.github.streamingwithflink.model.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * User-defined MapFunction to extract a reading's sensor id.
 */
public class IdExtractor implements MapFunction<SensorReading, String> {

    @Override
    public String map(SensorReading r) throws Exception {
        return r.id;
    }
}

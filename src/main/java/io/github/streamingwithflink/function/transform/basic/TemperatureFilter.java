package io.github.streamingwithflink.function.transform.basic;

import io.github.streamingwithflink.model.SensorReading;
import org.apache.flink.api.common.functions.FilterFunction;

/**
 * User-defined FilterFunction to filter out SensorReading with temperature below the threshold.
 */
public class TemperatureFilter implements FilterFunction<SensorReading> {

    private final double threshold;

    public TemperatureFilter(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean filter(SensorReading r) {
        return r.temperature >= threshold;
    }
}

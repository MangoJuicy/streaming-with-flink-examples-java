package io.github.streamingwithflink.function.transform.multistream;

import io.github.streamingwithflink.model.Alert;
import io.github.streamingwithflink.model.SensorReading;
import io.github.streamingwithflink.model.SmokeLevel;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * A CoFlatMapFunction that processes a stream of temperature readings ans a control stream
 * of smoke level events. The control stream updates a shared variable with the current smoke level.
 * For every event in the sensor stream, if the temperature reading is above 100 degrees
 * and the smoke level is high, a "Risk of fire" alert is generated.
 */
public class RaiseAlertFlatMap implements CoFlatMapFunction<SensorReading, SmokeLevel, Alert> {

    private SmokeLevel smokeLevel = SmokeLevel.LOW;

    @Override
    public void flatMap1(SensorReading tempReading, Collector<Alert> out) throws Exception {
        if (this.smokeLevel == SmokeLevel.HIGH && tempReading.temperature > 100) {
            out.collect(new Alert("Risk of fire! " + tempReading, tempReading.timestamp));
        }
    }

    @Override
    public void flatMap2(SmokeLevel smokeLevel, Collector<Alert> out) {
        this.smokeLevel = smokeLevel;
    }
}

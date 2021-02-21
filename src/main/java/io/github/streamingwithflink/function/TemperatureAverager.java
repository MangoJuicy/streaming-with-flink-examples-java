package io.github.streamingwithflink.function;

import io.github.streamingwithflink.model.SensorReading;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * User-defined WindowFunction to compute the average temperature of SensorReadings
 */
public class TemperatureAverager
        implements WindowFunction<SensorReading/*in*/, SensorReading/*out*/, String/*key*/, TimeWindow/*window*/> {

    /**
     * apply() is invoked once for each window.
     */
    @Override
    public void apply(
            String sensorId,                    // the key (sensorId) of the window
            TimeWindow window,                  // meta data for the window
            Iterable<SensorReading> input,      // an iterable over the collected sensor readings that were assigned to the window
            Collector<SensorReading> out        // a collector to emit results from the function
    ) {

        // compute the average temperature
        int cnt = 0;
        double sum = 0.0;
        for (SensorReading r : input) {
            cnt++;
            sum += r.temperature;
        }
        double avgTemp = sum / cnt;

        // emit a SensorReading with the average temperature
        out.collect(new SensorReading(sensorId, window.getEnd(), avgTemp));
    }
}

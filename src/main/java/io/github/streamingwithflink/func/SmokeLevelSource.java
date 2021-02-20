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
package io.github.streamingwithflink.func;

import io.github.streamingwithflink.model.SmokeLevel;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Random;

/**
 * Flink SourceFunction to generate random SmokeLevel events.
 */
public class SmokeLevelSource implements SourceFunction<SmokeLevel> {

    private static Random RAND = new Random();
    private boolean running = true;

    /**
     * Continuously emit one smoke level event per second.
     */
    @Override
    public void run(SourceContext<SmokeLevel> srcCtx) throws Exception {

        while (running) {
            if (RAND.nextGaussian() > 0.8) {
                srcCtx.collect(SmokeLevel.HIGH);
            } else {
                srcCtx.collect(SmokeLevel.LOW);
            }
            Thread.sleep(1000);
        }
    }

    /**
     * Cancel the emission of smoke level events.
     */
    @Override
    public void cancel() {
        this.running = false;

    }
}

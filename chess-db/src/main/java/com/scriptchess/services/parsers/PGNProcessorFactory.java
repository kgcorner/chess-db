package com.scriptchess.services.parsers;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 18/09/22
 */

public class PGNProcessorFactory {
    private static List<PgnProcessor> processors = new ArrayList<>();

    static {
        ServiceLoader<PgnProcessor> pgnProcessorsServiceLoader = ServiceLoader.load(PgnProcessor.class);
        for(PgnProcessor processor : pgnProcessorsServiceLoader) {
            processors.add(processor);
        }
    }

    public static PgnProcessor getProcessor(String pgn) {
        for(PgnProcessor processor : processors) {
            if(processor.supports(pgn))
                return processor;
        }
        return null;
    }

    public static PgnProcessor getDefaultProcessor() {
        return new ByteWisePGNProcessor();
    }
}
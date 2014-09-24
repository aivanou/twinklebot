package org.tbot.validation;

import org.tbot.log.LoggerCreator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/**
 *
 */
public final class Validator {

    private static Validator instance;
    private static final Map<String, Object> syncFetcherObjects = new ConcurrentHashMap<>();
    private final Logger logger = LoggerCreator.getLogger();

}

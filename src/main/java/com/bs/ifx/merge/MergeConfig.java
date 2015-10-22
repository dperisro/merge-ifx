package com.bs.ifx.merge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MergeConfig {

    private final String pathOrigin;
    private final String pathDestination;
    private final String keys;

    @Autowired
    public MergeConfig(@Value("${pathOrigin}") String pathOriginV,
                       @Value("${pathDestination}") String pathDestinationV,
                       @Value("${keys}") String keysV) {
        this.pathOrigin = pathOriginV;
        this.pathDestination = pathDestinationV;
        this.keys = keysV;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + NEW_LINE);
        result.append(" pathOrigin: " + getPathOrigin() + NEW_LINE);
        result.append(" pathDestination: " + getPathDestination() + NEW_LINE);
        result.append(" keys: " + getKeys() + NEW_LINE);
        result.append("}");
        return result.toString();
    }

    public String getPathDestination() {
        return pathDestination;
    }

    public String getPathOrigin() {
        return pathOrigin;
    }

    public String getKeys() {
        return keys;
    }

}
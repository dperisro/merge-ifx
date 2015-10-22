package com.bs.ifx.merge.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MergeConfig {

    public static final String VERSION = "1.0";
    public static final String ENCODING = "UTF-8";
    public static final String COMMON_XSD = "ifx-commons.xsd";

    private final String pathOrigin;
    private final String pathDestination;
    private final String keys;

    @Autowired
    public MergeConfig(@Value("${pathOrigin}") final String pathOriginV,
                       @Value("${pathDestination}") final String pathDestinationV,
                       @Value("${keys}") final String keysV) {
        this.pathOrigin = pathOriginV;
        this.pathDestination = pathDestinationV;
        this.keys = keysV;
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" pathOrigin: " + getPathOrigin() + newLine);
        result.append(" pathDestination: " + getPathDestination() + newLine);
        result.append(" keys: " + getKeys() + newLine);
        result.append("}");
        return result.toString();
    }

}
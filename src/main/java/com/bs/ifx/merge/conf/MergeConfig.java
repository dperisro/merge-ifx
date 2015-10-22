package com.bs.ifx.merge.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MergeConfig {

    public static final String VERSION = "1.0";
    public static final String ENCODING = "UTF-8";
    public static final String COMMON_XSD = "ifx-commons.xsd";

    private final String inputPath;
    private final String outputPath;
    private final String[] keys;

    @Autowired
    public MergeConfig(@Value("${inputPath}") final String inputPath,
                       @Value("${outputPath}") final String outputPath,
                       @Value("${keys}") final String[] keysV) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.keys = keysV;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" pathOrigin: " + getInputPath() + newLine);
        result.append(" pathDestination: " + getOutputPath() + newLine);
        result.append(" keys: " + getKeys() + newLine);
        result.append("}");
        return result.toString();
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public List<String> getKeys() {
        return Arrays.asList(keys);
    }

}
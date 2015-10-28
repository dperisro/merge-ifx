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
    public static final String COMMON_XSD = "ifx-commons";
    public static final String EXT_XSD = ".xsd";
    public static final String BASE_XSD = "base";
    public static final String BASE_NS = "http://www.ifxforum.org/IFX_2X";

    public static final boolean NS = false;

    private final String inputPath;
    private final String outputPath;
    private final List<String> keys;
    private final List<String> base;

    @Autowired
    public MergeConfig(@Value("${inputPath}") final String inputPathV,
                       @Value("${outputPath}") final String outputPathV,
                       @Value("${keys}") final String[] keysV,
                       @Value("${base}") final String[] baseV) {
        this.inputPath = inputPathV;
        this.outputPath = outputPathV;
        this.keys = Arrays.asList(keysV);
        this.base = Arrays.asList(baseV);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" pathOrigin: " + getInputPath() + newLine);
        result.append(" pathDestination: " + getOutputPath() + newLine);
        result.append(" keys: " + getKeys() + newLine);
        result.append(" base: " + getBase() + newLine);
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
        return keys;
    }

    public List<String> getBase() {
        return base;
    }

}
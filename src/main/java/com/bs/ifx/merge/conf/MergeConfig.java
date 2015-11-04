package com.bs.ifx.merge.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class MergeConfig {

    public static final String VERSION = "1.0";
    public static final String ENCODING = "UTF-8";
    public static final String COMMON_XSD = "common";
    public static final String EXT_XSD = ".xsd";
    public static final String DATATYPE_XSD = "data-types";
    public static final String BASE_NS = "http://www.ifxforum.org/ifx/";

    private static final String[] DATA_BASE_TYPES = {"C", "NC", "Name", "Name_Type", "Desc", "Desc_Type",
            "OpenEnum_Type", "ClosedEnum_Type", "DayOfWeek", "DayOfMonth", "DayOfMonth_Type", "Month",
            "DayOfWeek_Type", "TimeOfDay", "TimeOfDay", "Time_Type", "DateTime_Type", "Date_Type", "Decimal_Type",
            "Long_Type", "Boolean_Type", "URL_Type", "HexBinary", "UUID_Type", "Identifier_Type", "Timestamp_Type",
            "PhoneNumber_Type", "IfxPath_Type", "BinData_Type"};

    public static final boolean NS = false;

    private final boolean downloadIFX;
    private final String inputPath;
    private final String outputPath;
    private final Set<String> keys;
    private final Set<String> base;

    @Autowired
    public MergeConfig(@Value("${downloadIFX}") final boolean downloadIFXV,
                       @Value("${inputPath}") final String inputPathV,
                       @Value("${outputPath}") final String outputPathV,
                       @Value("${keys}") final String[] keysV) {
        this.downloadIFX = downloadIFXV;
        this.inputPath = inputPathV;
        this.outputPath = outputPathV;
        this.keys = new LinkedHashSet<>(Arrays.asList(keysV));
        this.base = new LinkedHashSet<>(Arrays.asList(DATA_BASE_TYPES));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        final String newLine = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " {" + newLine);
        result.append(" downloadIFX: " + isDownloadIFX() + newLine);
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

    public Set<String> getKeys() {
        return keys;
    }

    public Set<String> getBase() {
        return base;
    }

    public boolean isDownloadIFX() {
        return downloadIFX;
    }


}
package com.bs.ifx.merge.conf;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MergeConfig {

    private static final String FILE_URL = "application.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(MergeConfig.class);

    public static final String VERSION = "1.0";
    public static final String ENCODING = "UTF-8";
    public static final String DATATYPE_XSD = "data-types";
    public static final String COMMON_XSD = "common-types";
    public static final String HEADERS_XSD = "header-types";
    public static final String EXT_XSD = ".xsd";

    public static final String BASE_NS = "http://www.ifxforum.org/ifx/";

    private static final String[] DATA_BASE_TYPES = {"C", "NC", "Name", "Name_Type", "Desc", "Desc_Type",
            "OpenEnum_Type", "ClosedEnum_Type", "DayOfWeek", "DayOfMonth", "DayOfMonth_Type", "Month",
            "DayOfWeek_Type", "TimeOfDay", "TimeOfDay", "Time_Type", "DateTime_Type", "Date_Type", "Decimal_Type",
            "Long_Type", "Boolean_Type", "URL_Type", "HexBinary", "UUID_Type", "Identifier_Type", "Timestamp_Type",
            "PhoneNumber_Type", "IfxPath_Type", "BinData_Type"};

    public static final boolean NS = true;
    public static final String PATH_NONS = "no_ns";

    private final boolean downloadIFX;
    private final String inputPath;
    private final String outputPath;
    private final Set<String> keys;
    private final Set<String> base;
    private final Map<String, Set<String>> exceptions;

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
        this.exceptions = initExceptions();
    }

    //CHECKSTYLE:OFF
    private Map<String, Set<String>> initExceptions() {
        final Map<String, String> mapTemp = new HashMap<String, String>();
        Map<String, Set<String>> exceptionsValue = new LinkedHashMap<String, Set<String>>();
        try {
            InputStream fis = this.getClass().getClassLoader().getResourceAsStream(FILE_URL);
            ResourceBundle resources = new PropertyResourceBundle(fis);
            Enumeration<String> keysV = resources.getKeys();
            while (keysV.hasMoreElements()) {
                String key = keysV.nextElement();
                mapTemp.put(key, resources.getString(key));
            }

            exceptionsValue = mapTemp.entrySet()
                    .stream()
                    .filter(p -> p.getKey().startsWith("exception.key"))
                    .collect(Collectors.toMap(
                            p -> prepareKeyHeader(p.getKey()),
                            p -> prepareValue(p.getValue())));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return exceptionsValue;
    }
    //CHECKSTYLE:ON

    private static String prepareKeyHeader(final String key) {
        return key.replaceAll("exception.key.", "");
    }

    private static Set<String> prepareValue(final String keysV) {
        String deleteWhitespace = StringUtils.deleteWhitespace(keysV);
        return new LinkedHashSet<>(Arrays.asList(deleteWhitespace.split(",")));
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
        result.append(" exceptions: " + getExceptions() + newLine);
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

    public Map<String, Set<String>> getExceptions() {
        return exceptions;
    }


}
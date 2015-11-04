package com.bs.ifx.merge.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class DownloadProperties {

    private static final String FILE_URL = "download.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadProperties.class);
    private static final Map<String, String> MAP_URLS = new HashMap<String, String>();

    private static DownloadProperties instance = null;

    private DownloadProperties() {
        try {
            InputStream fis = DownloadProperties.class.getClassLoader().getResourceAsStream(FILE_URL);
            ResourceBundle resources = new PropertyResourceBundle(fis);
            Enumeration<String> keys = resources.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                MAP_URLS.put(key, resources.getString(key));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static Map<String, String> getDownloadProperties() {
        if (instance == null) {
            instance = new DownloadProperties();
        }
        return instance.MAP_URLS;
    }

    //CHECKSTYLE:OFF
    public static Map getIFXObjects() {
        return
                getDownloadProperties().entrySet()
                        .stream()
                        .filter(p -> p.getKey().startsWith("ifx.object"))
                        .collect(Collectors.toMap(
                                p -> prepareKeyHeader(p.getKey()),
                                p -> prepareValue(p.getKey(), p.getValue())));

    }
    //CHECKSTYLE:ON

    private static String prepareKeyHeader(final String key) {
        return key.replaceAll("ifx.object.", "");
    }

    private static String prepareValue(final String key, final String value) {
        return getUrlDownload() + prepareKeyHeader(key) + "&objid=" + value + "&handleAbstract=" + getHandleAbstract();
    }

    public static String getUrlDownload() {
        return getDownloadProperties().get("ifx.urlDownload");
    }

    public static String getHandleAbstract() {
        return getDownloadProperties().get("ifx.handleAbstract");
    }

    public static String getUser() {
        return getDownloadProperties().get("ifx.user");
    }

    public static String getPassword() {
        return getDownloadProperties().get("ifx.password");
    }

    public static String getUrlLogin() {
        return getDownloadProperties().get("ifx.urlLogin");
    }

}



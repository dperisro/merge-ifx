package com.bs.ifx.merge;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@EnableAutoConfiguration
public class MergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);

    @Value("${pathOrigin}")
    private String pathOrigin;

    @Value("${pathDestination}")
    private String pathDestination;

    @Value("${keys}")
    private List<String> keys;

    @PostConstruct
    public void init() throws IOException {
        LOGGER.info("pathOrigin: " + pathOrigin);
        LOGGER.info("pathDestination: " + pathDestination);
        LOGGER.info("keys: " + keys);
        preparePathDestination();
    }

    public void preparePathDestination() throws IOException {
        File destination = new File(pathDestination);
        if (destination.exists()) {
            LOGGER.info("Deleting....");
            FileUtils.deleteDirectory(destination);
        }
        destination.mkdirs();
    }

    public void merge() {
    }


}
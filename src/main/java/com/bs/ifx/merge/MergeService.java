package com.bs.ifx.merge;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
public class MergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);

    @Autowired
    private MergeConfig config;

    @PostConstruct
    public void init() throws IOException {
        LOGGER.info(config.toString());
        preparePathDestination();
    }

    public void preparePathDestination() throws IOException {
        File destination = new File(config.getPathDestination());
        if (destination.exists()) {
            LOGGER.info("Deleting....");
            FileUtils.deleteDirectory(destination);
        }
        destination.mkdirs();
    }

    public void merge() {
/*        try {
            mergeUtil.createSkeletonFile();
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }*/
    }


}
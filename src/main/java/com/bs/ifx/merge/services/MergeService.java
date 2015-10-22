package com.bs.ifx.merge.services;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.util.MergeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class MergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);

    @Autowired
    private MergeConfig config;
    @Autowired
    private MergeUtil mergeUtil;

    @PostConstruct
    public void init() throws IOException {
        LOGGER.info(config.toString());
        mergeUtil.preparePathDestination(config.getPathDestination());
    }

    public void merge() throws Exception {
        Document doc = mergeUtil.createSkeletonFile();
        mergeUtil.writeNode(config.getPathDestination(), MergeConfig.COMMON_XSD, doc);
    }


}
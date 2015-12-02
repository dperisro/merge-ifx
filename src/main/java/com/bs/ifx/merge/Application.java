package com.bs.ifx.merge;

import com.bs.ifx.merge.services.DownLoadService;
import com.bs.ifx.merge.services.MergeService;
import com.bs.ifx.merge.services.ReduceCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * .
 * SpringApplication run
 */
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    /**
     * .
     * Injects
     */
    @Autowired
    private DownLoadService downLoadService;

    @Autowired
    private MergeService mergeService;

    @Autowired
    private ReduceCommons reduceCommons;

    /**
     * .
     * SpringApplication run
     *
     * @param args Nothing
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * .
     * SpringApplication run
     *
     * @param args Nothing
     */
    @Override
    public void run(final String... args) throws Exception {
        this.downLoadService.doDownLoad();
        this.mergeService.doMerge();
        this.reduceCommons.doAnalysis();
    }


}

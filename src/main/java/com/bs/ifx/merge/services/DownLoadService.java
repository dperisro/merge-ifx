package com.bs.ifx.merge.services;

import com.bs.ifx.merge.conf.MergeConfig;
import com.bs.ifx.merge.util.DownloadProperties;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * .
 * Download IFX Model with Selenium-Firefox
 */
@Component
public class DownLoadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownLoadService.class);

    @Autowired
    private MergeConfig config;

    /**
     * .
     * Principal method
     */
    public void doDownLoad() throws Exception {
        LOGGER.info("isDownloadIFX: " + config.isDownloadIFX());
        if (config.isDownloadIFX()) {
            prepareInputDownload();
            donwLoadIFXObject();
            unzipDownloadFiles();
        }
    }

    /**
     * .
     * Unzip IFX Model
     */
    private void unzipDownloadFiles() throws Exception {
        String[] filesZip = new File(config.getInputPath()).list();
        for (String zip : filesZip) {
            File fileZip = new File(config.getInputPath(), zip);
            ZipFile zipExtract = new ZipFile(fileZip);
            zipExtract.extractAll(config.getInputPath());
            FileUtils.forceDeleteOnExit(fileZip);
        }
    }

    /**
     * .
     * Prepare directory to download
     */
    private void prepareInputDownload() throws Exception {
        File inputPathDown = new File(config.getInputPath());
        if (inputPathDown.exists()) {
            LOGGER.info("Deleting....");
            FileUtils.deleteDirectory(inputPathDown);
        }
        if (!inputPathDown.mkdirs()) {
            throw new IOException("OutputPath is not create!!");
        }
    }

    /**
     * .
     * Download IFX Model
     */
    private void donwLoadIFXObject() {

        String path = "C:\\development\\bs\\merge-ifx\\dist";
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", config.getInputPath());
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/msword, application/csv, "
                + "application/ris, text/csv, image/png, application/pdf, text/html, text/plain, application/zip, "
                + "application/x-zip, application/x-zip-compressed, application/download, application/octet-stream");
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.useWindow", false);
        profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
        profile.setPreference("pdfjs.disabled", true);
        WebDriver driver = new FirefoxDriver(profile);

        //Login IFX
        driver.get(DownloadProperties.getUrlLogin());
        WebElement elementUser = driver.findElement(By.name("j_username"));
        elementUser.sendKeys(DownloadProperties.getUser());
        WebElement elementPass = driver.findElement(By.name("j_password"));
        elementPass.sendKeys(DownloadProperties.getPassword());
        elementPass.submit();

        //Download XSD (ZipFiles)
        Map<String, String> mapIFXObjects = DownloadProperties.getIFXObjects();
        for (Map.Entry<String, String> objectIFX : mapIFXObjects.entrySet()) {
            driver.get(objectIFX.getValue());
        }

        driver.quit();
    }
}

package com.eliassen.crucible.web.drivers;

import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.common.helpers.Logger;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.web.helpers.TestHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class CrucibleFirefoxWebdriver extends CrucibleWebdriver
{
    WebDriver driver;
    public CrucibleFirefoxWebdriver(DriverName driverName, boolean useProxy)
    {
        driver = null;
        FirefoxOptions options = new FirefoxOptions();
        if(driverName.equals(DriverName.firefox_headless))
        {
            options.addArguments("--headless");
        }
        options.addArguments("--window-size=1600,900");

        try
        {
            setupDriver(options);
        }
        catch(IllegalStateException ise)
        {
            try
            {
                String firefoxDriverName = System.getProperty(DriverFactory.FIREFOX_DRIVER_PROPERTY);
                DriverFactory.extractDriver(firefoxDriverName);

                setupDriver(options);
            }
            catch(IllegalStateException ise2)
            {
                Logger.logError("Failed to instantiate Firefox driver!");
                throw ise;
            }
        }

        setInstance(driver);
    }

    private void setupDriver(FirefoxOptions options)
    {
        options.addPreference("browser.download.folderList", 2); // 0 for desktop; 1 for default directory; 2 for custom path
        options.addPreference("browser.download.dir", getDownloadFilePath());
        options.addPreference("browser.download.useDownloadDir", true);

        checkForOverrideProfileOptions(options);

        driver = new FirefoxDriver(options);
        driver.manage().deleteAllCookies();

        driver.manage().window().setSize(new Dimension(1600, 900));
        driver.manage().window().maximize();
    }

    public static void checkForOverrideProfileOptions(FirefoxOptions options){
        JSONArray optionsArray = SystemHelper.getApplicationSettingArray("webDriverProfileOptions.geckoDriver");

        if(optionsArray != null && !optionsArray.isEmpty())
        {
            optionsArray.forEach(o -> {
                if(o.toString().contains("=")) {
                    String[] parts = o.toString().split("=");
                    String key = parts[0];
                    Object value = TestHelper.inferDataType(parts[1]);

                    options.addPreference(key,value);
                } else {
                    options.addArguments(o.toString());
                }
            });
        }
    }
}

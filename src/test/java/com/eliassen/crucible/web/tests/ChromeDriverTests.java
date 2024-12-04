package com.eliassen.crucible.web.tests;

import com.eliassen.crucible.web.drivers.CrucibleChromeWebdriver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChromeDriverTests {
    @Test
    void canSeeChromeDriverOptionOverrides(){
        ChromeOptions options = new ChromeOptions();
        Map<String,Object> chromePrefs = new HashMap<>();
        CrucibleChromeWebdriver.checkForOverrideOptions(options, chromePrefs);
        assertTrue(options.asMap().size() > 1);
    }
}

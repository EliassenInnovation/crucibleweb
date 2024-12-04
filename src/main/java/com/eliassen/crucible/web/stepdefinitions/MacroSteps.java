package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import com.eliassen.crucible.web.helpers.NavHelper;
import com.eliassen.crucible.web.helpers.ScreenShotter;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;

import com.eliassen.crucible.core.pageobjects.PageObjectResolver;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MacroSteps
{
    /**
     * This should be run BEFORE anything else runs
     * @param scenario
     */

    @Before(order=0)
    public void before(Scenario scenario)
    {
        String FEATURE_NAME = "featurename";

        String name = CurrentPage.retrievePersisted(FEATURE_NAME);
        String featureNameRaw = scenario.getUri().toString();
        String[] parts = featureNameRaw.split("/");
        String featureName = parts[parts.length - 1].split(":")[0];
        if(name == null)
        {
            CurrentPage.storePersisted(FEATURE_NAME, featureName);
        }
        else if(!name.equals(featureName))
        {
            CrucibleWebdriver crucibleWebdriver = null;

            if(CurrentPage.getDriver() != null && CurrentPage.getDriver().driverReusable())
            {
                crucibleWebdriver = CurrentPage.getDriver();
            }
            CurrentPage.getCurrentThreadObjects().clear();

            CurrentPage.storePersisted(FEATURE_NAME, featureName);

            if(crucibleWebdriver != null) {
                CurrentPage.setDevice(crucibleWebdriver);
            }
        }

        CurrentPage.setScenario(scenario);
    }

    /**
     * This should be run AFTER everything else
     */
    @After(order=0)
    public void closeTheBrowser()
    {
        if( CurrentPage.getDriver() != null && !CurrentPage.getDriver().driverReusable())
        {
            CurrentPage.getDriver().quit();
        }
    }

    /**
     * This should run BEFORE all the other after steps
     * @param scenario
     */
    @After(order=99999)
    public void takeScreenshot(Scenario scenario)
    {
        if (scenario.isFailed() && CurrentPage.getDriver() != null)
        {
            new ScreenShotter().safeAttachScreenshot(scenario);
        }
    }

    @Before(order = 1)
    public void grabFunctionalTags() {
        Collection<String> tags = CurrentPage.getScenario().getSourceTagNames();
        Map<String, String> tagMap = new HashMap<>();

        for (String tag : tags) {
            if (tag.contains("=")) {
                String[] tagParts = tag.split("=");
                tagMap.put(tagParts[0].replace("@", "") + "_tag", tagParts[1]);
            }
        }
        for (Map.Entry<String, String> pair : tagMap.entrySet()) {
            CurrentPage.storePersisted(pair.getKey(), pair.getValue());
        }
    }

    @Before(order = 10001)
    public void launchBrowserAndNavigateToPage() {
        if(CurrentPage.isPersisted("pageObject_tag")) {
            String pageObjectName = CurrentPage.retrievePersisted("pageObject_tag");
            CurrentPage.setPageObject(new PageObjectResolver().getPageObjectByName(pageObjectName));
            try{
                NavHelper.createBrowser();
                CurrentPage.goTo();
            }
            catch(NullPointerException n){
                //we do not care
            }
        }
    }
}

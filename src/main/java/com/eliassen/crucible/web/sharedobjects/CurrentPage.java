package com.eliassen.crucible.web.sharedobjects;

import com.eliassen.crucible.core.sharedobjects.CurrentObjectBase;
import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import com.eliassen.crucible.web.drivers.DriverFactory;
import com.eliassen.crucible.web.drivers.DriverName;
import com.eliassen.crucible.web.helpers.NavHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CurrentPage extends CurrentObjectBase
{
    public static String actualURL()
    {
        return getDriver().getCurrentUrl();
    }

    public static CrucibleWebdriver getDriver()
    {
        return (CrucibleWebdriver)getCurrentThreadObjects().get(DRIVER);
    }

    public static WebElement element(String elementName, boolean checkForVisibility)
    {
        String xpath = getPageObjectItem(elementName);
        return getElementByXpath(xpath, checkForVisibility);
    }

    public static WebElement element(String elementName)
    {
        String xpath = getPageObjectItem(elementName);
        return getElementByXpath(xpath);
    }

    public static WebElement getElementByXpath(String xpath)
    {
        boolean checkForVisibility = true;
        return getElementByXpath(xpath, checkForVisibility);
    }

    public static WebElement getElementByXpath(String xpath, boolean checkForVisibility)
    {
        checkProgress();
        if(checkForVisibility)
        {
            NavHelper.waitForElementToBeVisible(xpath);
        }
        WebElement element = getDriver().findElement(By.xpath(xpath));
        return element;
    }

    public static List<WebElement> getElementsByXpath(String xpath)
    {
        checkProgress();
        List<WebElement> elements = getDriver().findElements(By.xpath(xpath));
        return elements;
    }

    public static void goTo()
    {
        String url = getPageURL();
        getDriver().goTo(url);
    }

    public static void clickOn(String elementName)
    {
        NavHelper.clickOn(elementName);
    }
    
    public static void enterText(String elementName, String text)
    {
    	NavHelper.enterText(elementName, text);
    }

    public static void setDevice(String deviceName)
    {
        setDevice(deviceName, false);
    }

    public static void setDevice(String deviceName, boolean useProxy)
    {
    	DriverFactory df = new DriverFactory();
        DriverName driverName = DriverName.valueOf(deviceName.toLowerCase());
        getCurrentThreadObjects().put(DRIVER, df.createDriver(driverName, useProxy));
    }

    public static void setDevice(CrucibleWebdriver crucibleWebdriver)
    {
        getCurrentThreadObjects().put(DRIVER, crucibleWebdriver);
    }

	public static void ScrollIntoView(String elementName) 
	{
		NavHelper.ScrollElementIntoView(elementName);		
	}

	public static void ScrollIntoView(WebElement element)
    {
        NavHelper.ScrollElementIntoView(element);
    }

    public static Object executeJavascript(String javascript)
    {
        JavascriptExecutor executor = (JavascriptExecutor) getDriver().getInstance();
        Object result = executor.executeScript(javascript);
        return result;
    }
}

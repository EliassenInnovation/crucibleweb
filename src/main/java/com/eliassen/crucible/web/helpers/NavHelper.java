package com.eliassen.crucible.web.helpers;

import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.core.pageobjects.PageObjectResolver;
import com.eliassen.crucible.web.drivers.mocks.MockWebElement;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavHelper 
{
    public static void clickOn(String elementName)
    {
         WebElement element = CurrentPage.element(elementName);
         String elementPath = CurrentPage.getPageObjectItem(elementName);

         clickOn(element, elementPath);
    }

    public static void clickOn(WebElement element, String elementPath)
	 {
		 try
         {
             waitForElementToBeVisible (element);
             waitForElementToBeClickable(element);
             element.click();
         }
		 catch(StaleElementReferenceException stere)
         {
             element = CurrentPage.getDriver().findElement(By.xpath(elementPath));
             clickOn(element, elementPath);
         }
         catch (Exception e)
         {
             boolean success = clickTheOldFashionedWay(elementPath, element);
             if (!success)
             {
                 throw e;
             }
         }
	 }

     public static boolean clickTheOldFashionedWay(String elementPath, WebElement element)
     {
         if (element.isDisplayed())
         {
             String jsString = "xPathResult = document.evaluate(\"" + elementPath + "\", document);" +
                 "if(xPathResult){element = xPathResult.iterateNext();}" +
                 "element.click();";
             CurrentPage.executeJavascript(jsString);
             return true;
         }
         else
         {
             return false;
         }
     }

     public static WebElement GetElement(String elementPath)
     {
         return CurrentPage.element(elementPath);
     }

     public static void MoveMouse(int down, int right)
     {
         Actions actions = new Actions(CurrentPage.getDriver().getInstance());
         actions.moveByOffset(right, down).build().perform();
     }

     public static void MoveMouseToElement(String elementName)
     {
         Actions actions = new Actions(CurrentPage.getDriver().getInstance());
         WebElement element = GetElement(elementName);
         actions.moveToElement(element).build().perform();
     }
     
     public static void ScrollElementIntoView(String elementName)
     {
         WebElement element = CurrentPage.element(elementName, false);
         ScrollElementIntoView(element);
     }

     public static void ScrollElementIntoView(WebElement element)
     {
         Coordinates coordinate = ((Locatable) element).getCoordinates();
         coordinate.onPage();
         coordinate.inViewPort();
     }
     
     public static void enterText(String elementName, String text)
     {
    	 WebElement element = GetElement(elementName);
    	 CurrentPage.getDriver().enterText(element, text);
         waitForElementToContainText(element,text);
     }
     
     public static void createBrowser()
     {
         if(CurrentPage.getDriver() == null ||
                 CurrentPage.getDriver().hasQuit() ||
                 CurrentPage.getDriver().isClosed() ||
            !CurrentPage.getDriver().driverReusable())
         {
             String browserName = System.getProperty("browser");
             String driverName;

             if(CurrentPage.isPersisted("browser_tag")) {
                browserName = CurrentPage.retrievePersisted("browser_tag");
             }

             if (browserName != null && !browserName.isEmpty())
             {
                 driverName = browserName;
             } else
             {
                 driverName = CrucibleWebdriver.CHROME;
             }

             CurrentPage.setDevice(driverName);
         }
     }

    public static void createBrowserWithProxy()
    {
        String browserName = System.getProperty("browser");
        String driverName;
        if(browserName != null && !browserName.isEmpty())
        {
            driverName = browserName;
        }
        else
        {
            driverName = CrucibleWebdriver.CHROME;
        }

        CurrentPage.setDevice(driverName, true);
    }

    public static void waitForElementToBeClickable(WebElement element)
    {
        WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(), Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void waitForElementToBeVisible(WebElement element)
    {
        WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(), Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForElementToContainText(WebElement element, String text)
    {
        if(!(element instanceof MockWebElement)) {
            WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(), Duration.ofSeconds(15));
            if (element.getTagName().equals("input") || element.getTagName().equals("textarea")) {
                wait.until(ExpectedConditions.textToBePresentInElementValue(element, text));
            } else {
                wait.until((ExpectedConditions.textToBePresentInElement(element, text)));
            }
        }
    }

    public static void waitForElementToBeVisible(String xpath)
    {
        int tries = 0;
        boolean isVisible = false;
        do
        {
            try
            {
                WebElement element = CurrentPage.getDriver().findElement(By.xpath(xpath));
                waitForElementToBeVisible(element);
                isVisible = true;
            }
            catch (NoSuchElementException nsee)
            {
                TestHelper.waitSilently(1);
            }
            catch(StaleElementReferenceException sere)
            {
                TestHelper.waitSilently(1);
            }
            tries++;
        }
        while(tries < 10 && !isVisible);
        if(tries > 1)
        {
            Logger.logError("Tried to find " + xpath + " " + tries + " times");
        }
    }

    /**
     * Waits up to 10 seconds for the element to be enabled
     * @param element
     */
    public static void waitForElementToBeEnabled(WebElement element)
    {
        int tries = 0;
        while(!element.isEnabled() && tries < 10)
        {
            tries++;
            TestHelper.waitSilently(1);
        }

        if(tries >= 10 && !element.isEnabled())
        {
            Assertions.assertTrue(false,"I waited at least 10 seconds and the element was not enabled!");
        }
    }

    /**
     * Uses WebElement.clear() to clear an element. Includes a wait.
     * @param elementName
     */
    public static void clear(String elementName){
        WebElement element = CurrentPage.element(elementName);
        NavHelper.waitForElementToBeClickable(element);
        element.clear();
    }
}

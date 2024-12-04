package com.eliassen.crucible.web.helpers;

import com.eliassen.crucible.core.helpers.AlphabeticalOrder;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.core.helpers.TestHelperBase;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHelper extends TestHelperBase {
    public static String[] getBreadcrumbPieces() {
        String breadcrumbPath = CurrentPage.getPageObjectItem("breadcrumb");
        List<WebElement> breadcrumbItems = CurrentPage.getDriver().findElements(By.xpath(breadcrumbPath + "/li/a"));

        ArrayList<String> breadcrumbPieces = new ArrayList<String>();
        for (WebElement a : breadcrumbItems) {
            String text = a.getText();
            if (!text.isEmpty()) {
                breadcrumbPieces.add(text);
            }
        }

        String[] breadcrumbPiecesArray = breadcrumbPieces.toArray(new String[breadcrumbPieces.size()]);

        return breadcrumbPiecesArray;
    }

    /*
     * For use when page object doesn't apply
     * Like login
     */
    public static void findElementAndEnterText(String elementPath, String text) {
        WebElement element = CurrentPage.getDriver().findElement(elementPath);
        CurrentPage.getDriver().enterText(element, text);
    }

    public static void findElementAndClickOn(String elementXPath) {
        WebElement element = CurrentPage.getElementByXpath(elementXPath);
        NavHelper.clickOn(element, elementXPath);
    }

    public static void checkSortOrderOfAListOfElements(String columnElement, AlphabeticalOrder orderType) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

        if (rows.size() < 2) {
            assertTrue(true);
        }

        Hashtable<Integer, String> textFromRows = new Hashtable<>();
        int key = 0;
        String cellText = "";
        for (WebElement element : rows) {
            cellText = element.getText();
            if (cellText.equals("")) {
                CurrentPage.ScrollIntoView(element);
                cellText = element.getText();
            }
            textFromRows.put(key++, cellText);
        }

        for (int i = 0; i < rows.size() - 1; i++) {
            String text1 = textFromRows.get(i).toLowerCase();
            String text2 = textFromRows.get(i + 1).toLowerCase();
            try {
                switch (orderType) {
                    case forward:
                        assertTrue(text1.compareTo(text2) <= 0, text1 + " does not come before " + text2);
                        break;
                    case reverse:
                        assertTrue(text1.compareTo(text2) >= 0, text1 + " does not come after " + text2);
                        break;
                }

            } catch (StringIndexOutOfBoundsException siobe) {
                Logger.log("Problem with row: " + i + ", text1 = '" + text1 + "', text2 = '" + text2 + "'");
            }
        }
    }

    public static void waitForElementToContainText(WebElement element, String text) {
        WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(), Duration.ofSeconds(15));
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public static String getTextFromElement(String elementName) {
        return getTextFromElement(CurrentPage.element(elementName));
    }

    public static String getTextFromElement(WebElement element) {
        String elementText = element.getText();
        if (elementText.equals("")) {
            elementText = element.getAttribute("value");
        }
        if (elementText.equals("")) {
            elementText = element.getAttribute("innerText");
        }

        return elementText;
    }

    public static Object inferDataType(String objectAsString) {
        if (objectAsString.matches("-?\\d+")) {
            return Integer.parseInt(objectAsString);
        }
        else if (objectAsString.matches("-?\\d*\\.\\d+")) {
            return Float.parseFloat(objectAsString);
        }
        else {
            return objectAsString;
        }
    }
}

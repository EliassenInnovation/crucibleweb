package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.core.helpers.AlphabeticalOrder;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.core.helpers.RegexValidator;
import com.eliassen.crucible.web.helpers.NavHelper;
import com.eliassen.crucible.web.helpers.TableHelper;
import com.eliassen.crucible.web.helpers.TestHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssertionSteps 
{
	@And("^(?:The )?\"([^\"]*)\" is not visible$")
	@Then("^I check that (?:the )?\"([^\"]*)\" is not visible$")
    public void iCheckThatIsNotVisible(String elementName)
    {
        try
        {
        	//using direct methods since CurrentPage.Element searches FOR visibility
        	String xpath = CurrentPage.getPageObjectItem(elementName);
            WebElement element = CurrentPage.getElementByXpath(xpath, false);
            assertFalse(element.isDisplayed());
        }
        catch(NoSuchElementException nse)
        {
            assertTrue(true);
        }
    }
	
	@Then("^I check that (?:the )?\"([^\"]*)\" is visible$")
    public void iCheckThatIsVisible(String elementName)
    {
        try
        {
            WebElement element = NavHelper.GetElement(elementName);
            assertTrue(element.isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            assertTrue(false, "Element not found!");
        }
    }

	@Then("^(?:The )?\"([^\"]*)\" is visible$")
	public void isVisible(String elementName)
	{
		try
		{
			WebElement element = NavHelper.GetElement(elementName);
			assertTrue(element.isDisplayed());
		}
		catch (NoSuchElementException nse)
		{
			assertTrue(false, "Element not found!");
		}
	}
	
	@Then("^The breadcrumb says \"(.*)\"$")
	public void theBreadcrumbSays(String breadcrumb)
	{
		String[] breadCrumbArray = breadcrumb.split("/");
		String[] breadcrumbPieces = TestHelper.getBreadcrumbPieces();
		
		assertArrayEquals(breadCrumbArray, breadcrumbPieces);
	}
	
	@Then("^(?:The )?\"([^\"]*)\" exists$")
	public void exists(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		assertNotNull(element);
	}
	
	@Then("^(?:The )?\"([^\"]*)\" text is \"(.*)\"$")
	public void textIs(String elementName, String expectedOption)
	{
		WebElement element = CurrentPage.element(elementName);
		assertEquals(expectedOption, element.getText());
	}
	
	@Then("^(?:The )?\"([^\"]*)\" is empty$")
	public void isEmpty(String elementName)
	{
		WebElement element = CurrentPage.element(elementName, false);
		NavHelper.ScrollElementIntoView(element);
		assertTrue(element.getText().isEmpty());
	}
	
	@Then("^(?:The )?\"([^\"]*)\" is disabled$")
	public void isDisabled(String elementName)
	{
		WebElement element = CurrentPage.element(elementName, false);
		NavHelper.ScrollElementIntoView(element);
		assertFalse(element.isEnabled());
	}
	
	@Then("^(?:The )?\"([^\"]*)\" is enabled$")
	public void isEnabled(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		assertTrue(element.isEnabled());
	}

	@Then("^(?:The )?\"([^\"]*)\" text equals my remembered \"([^\"]*)\"$")
	public void theTextEqualsMyRemembered(String elementName, String key) 
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getText();
		String retrievedText = CurrentPage.retrieve(key);
		assertEquals(elementText,retrievedText);
	}

	@Then("^(?:The )?\"([^\"]*)\" field is empty$")
	public void fieldIsEmpty(String elementName)
	{
		WebElement element = CurrentPage.element(elementName, false);
		NavHelper.ScrollElementIntoView(element);
		String elementText = element.getAttribute("value");
		assertTrue(elementText.isEmpty());
	}

	@Then("^(?:The )?\"([^\"]*)\" field is not empty$")
	public void fieldIsNotEmpty(String elementName)
	{
		WebElement element = CurrentPage.element(elementName,false);
		NavHelper.ScrollElementIntoView(element);
		String elementText = element.getAttribute("value");
		assertTrue(!elementText.isEmpty());
	}

	@Then("^(?:The )?\"([^\"]*)\" text in the field equals \"(.*)\"$")
	public void theTextInTheFieldEquals(String elementName, String expectedOption)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementValue = element.getAttribute("value");
		assertEquals(expectedOption, elementValue);
	}

	@Then("^(?:The )?\"([^\"]*)\" comes before (?:the )?\"([^\"]*)\" alphabetically$")
	public void isListedBefore(String firstElement, String secondElement)
	{
		String text1 = CurrentPage.element(firstElement).getText();
		String text2 = CurrentPage.element(secondElement).getText();
		assertTrue(text1.substring(0,1).compareTo(text2.substring(0,1)) <= 0);
	}

	@Then("^(?:The )?\"([^\"]*)\" column of the table is in alphabetical order$")
	public void theColumnOfTableIsInAlphabeticalOrder(String columnElement)
	{
		TestHelper.checkSortOrderOfAListOfElements(columnElement, AlphabeticalOrder.forward);
	}

	@Then("^(?:The )?\"([^\"]*)\" column of the table is in reverse alphabetical order$")
	public void theColumnOfTableIsInReverseAlphabeticalOrder(String columnElement)
	{
		TestHelper.checkSortOrderOfAListOfElements(columnElement, AlphabeticalOrder.reverse);
	}

	@Then("(?:The )?\"([^\"]*)\" is not empty$")
	public void isNotEmpty(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		assertFalse(element.getText().isEmpty());
	}

	@Then("^(?:The )?\"([^\"]*)\" text is not empty$")
	public void textIsNotEmpty(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		assertFalse(element.getText().isEmpty());
	}

	@Then("^(?:The )?\"([^\"]*)\" text contains \"(.*)\"$")
	public void theTextContains(String elementName, String expectedOption)
	{
		WebElement element = CurrentPage.element(elementName);
		String text = element.getText();
		if(text.equals(""))
		{
			text = element.getAttribute("value");
		}
		assertTrue(text.contains(expectedOption));
	}

	@Then("(?:The )?\"([^\"]*)\" text contains (?:the )?\"([^\"]*)\" text$")
	public void theTextContainsText(String elementName, String elementName2)
	{
		WebElement element = CurrentPage.element(elementName);
		WebElement element2 = CurrentPage.element((elementName2));
		assertTrue(element.getText().contains(element2.getText()));
	}

	@Then("^My remembered \"(.*)\" does not contain the text from (?:the )?\"([^\"]*)\"$")
	public void myRememberedDoesNotContainTheTextFrom(String key, String elementName)
	{
		String retrievedText = CurrentPage.retrieve(key);
		WebElement element = CurrentPage.element(elementName);
		String text = element.getText();
		assertFalse(retrievedText.contains(text));
	}

	@Then("^My remembered \"(.*)\" contains the text from (?:the )?\"([^\"]*)\"$")
	public void myRememberedContainsTextFrom(String key, String elementName)
	{
		TestHelper.wait(2);
		String retrievedText = CurrentPage.retrieve(key);
		Logger.log("Remembered: " + retrievedText);
		WebElement element = CurrentPage.element(elementName);
		String text = element.getText();
		Logger.log("Expected: " + text);
		assertTrue(retrievedText.contains(text));
	}

	@Then("^(?:The )?\"([^\"]*)\" field text equals my remembered \"([^\"]*)\"$")
	public void theFieldTextEqualsMyRemembered(String elementName, String key)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getAttribute("value");
		String retrievedText = CurrentPage.retrieve(key);
		assertEquals(elementText, retrievedText);
	}

	@Then("^(?:The )?\"([^\"]*)\" text content equals my remembered \"([^\"]*)\"$")
	public void theTextContentEqualsMyRemembered(String elementName, String key)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getAttribute("textContent");
		String retrievedText = CurrentPage.retrieve(key);
		assertEquals(elementText, retrievedText);
	}

	@Then("^The state of (?:the )?\"([^\"]*)\" checkbox is the opposite of my remembered \"([^\"]*)\"$")
	public void theStateOfTheCheckboxIsTheOppositeOfMyRemembered(String elementName, String key)
	{
		WebElement element = CurrentPage.element(elementName);
		String state = element.getAttribute("aria-checked");
		String retrievedText = CurrentPage.retrieve(key);
		boolean previousState = Boolean.parseBoolean(retrievedText);
		boolean currentState = Boolean.parseBoolean(state);
		assertTrue(currentState == !previousState);
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" text is an email address$")
	public void iCheckThatTextIsAnEmailAddress(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getAttribute("innerText");
		assertTrue(RegexValidator.validateEmail(elementText));
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" is an email address$")
	public void iCheckThatIsAnEmailAddress(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getAttribute("value");
		assertTrue(RegexValidator.validateEmail(elementText));
	}

	@And("^If (?:the )?\"([^\"]*)\" field is empty I enter \"([^\"]*)\"$")
	public void ifFieldIsEmptyIEnter(String elementName, String text)
	{
		WebElement element = CurrentPage.element(elementName);
		if (element.getAttribute("value").isEmpty()) {
			CurrentPage.enterText(elementName, text);
		}
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" text is a date$")
	public void iCheckThatTextIsADate(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getText();
		Assertions.assertTrue(RegexValidator.validateDate(elementText));
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" is a date$")
	public void iCheckThatIsADate(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getAttribute("value");
		Assertions.assertTrue(RegexValidator.validateDate(elementText));
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" is a \"([^\"]*)\"$")
	public void iCheckThatIsA(String elementName, String regexKey)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getAttribute("value");
		Assertions.assertTrue(RegexValidator.validate(elementText, regexKey));
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" text is a \"([^\"]*)\"$")
	public void iCheckThatTextIsA(String elementName, String regexKey)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getText();
		Assertions.assertTrue(RegexValidator.validate(elementText, regexKey));
	}

	@Then("^(?:The )?\"([^\"]*)\" date column is in ascending order$")
	public void theDateColumnIsInAscendingOrder(String columnElement)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

		if (rows.size() < 2) {
			assertTrue(true);
		}
//refactor to work with dates
		for (int i = 0; i < rows.size() - 1; i++) {
			String date1 = rows.get(i).getText();
			String date2 = rows.get(i + 1).getText();
			assertTrue(date1.substring(0, 1).compareTo(date2.substring(0, 1)) <= 0);
		}
	}

	@Then("^(?:The )?\"([^\"]*)\" column texts all contain the text from (?:the )?\"([^\"]*)\"$")
	public void theColumnTextsAllContainTheTextFrom(String columnElement, String selectionElement)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
		String selection = CurrentPage.element(selectionElement).getText();

		if (rows.size() < 1) {
			assertTrue(true);
		}
		for (int i = 0; i < rows.size() - 1; i++) {
			String rowText = rows.get(i).getText();
			assertTrue(selection.contains(rowText));
		}
	}

	@Then("^The dates in (?:the )?\"([^\"]*)\" are within the current month$")
	public void theDatesInAreWithinTheCurrentMonth(String columnElement)
	{
		LocalDate currentDate = LocalDate.now();
		int currentMonth = currentDate.getMonthValue();
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate listedDate = null;

		for (int i = 0; i < rows.size() - 1; i++)
		{
			String elementText = "";
			try
			{
				elementText = rows.get(i).getText();
			}
			//TODO Need specific exception
			catch (Exception x)
			{
				System.out.println(x.getMessage());
			}

			try
			{
				listedDate = LocalDate.parse(elementText,formatter);
			}
			catch (DateTimeParseException e)
			{
				System.out.println(e.getMessage());
			}

			int month = listedDate.getMonthValue();
			assertEquals(month, currentMonth);
		}
	}

	@Then("^The list only contains items where (?:the )?\"([^\"]*)\" column equals \"([^\"]*)\"$")
	public void theListOnlyContainsItemsWhereColumnEquals(String columnElement, String text)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

		if (rows.size() < 1) {
			assertTrue(true);
		} else {
			for (int i = 0; i < rows.size(); i++) {
				String elementText = "";
				try {
					elementText = rows.get(i).getText().trim();
				} catch (Exception x) {
					System.out.println(x.getMessage());
				}
				assertTrue(elementText.equals(text));
			}
		}
	}

	@Then("^(?:The )?\"([^\"]*)\" column contains the remembered, space separated with comma \"(.*)\" \"(.*)\"$")
	public void theColumnContainsTheRememberedSpaceSeparatedWithComma(String columnElement, String first, String second) {
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
		String retrievedFirst = CurrentPage.retrieve(first);
		String retrievedSecond = CurrentPage.retrieve(second);
		String spaceSeparatedName = retrievedFirst +"," + " " + retrievedSecond;
		Boolean found = false;
		for (WebElement row : rows)
		{
			String rowName = row.getText();
			if (rowName.contains(spaceSeparatedName))
			{
				found = true;
			}
		}
		assertTrue(found);
	}

	@Then("^(?:The )?\"([^\"]*)\" column contains \"(.*)\"$")
	public void theColumnContains(String columnElement, String text) {
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

		Boolean found = false;
		for (WebElement row : rows)
		{
			String rowName = row.getText();
			if (rowName.contains(text))
			{
				found = true;
			}
		}
		assertTrue(found);
	}

	@Then("^(?:The )?\"([^\"]*)\" text equals \"(.*)\"$")
	public void theTextEquals(String elementName, String expectedText)
	{
		String elementText = TestHelper.getTextFromElement(CurrentPage.element(elementName));

		assertEquals(expectedText, elementText);
	}

	@Then("(?:The )?\"([^\"]*)\" does not contain my persisted \"(.*)\"")
	public void doesNotContainMyPersisted(String elementName, String key)
	{
		String persistedValue = CurrentPage.retrievePersisted(key);
		String elementText = TestHelper.getTextFromElement(elementName);

		assertFalse(elementText.contains(persistedValue));
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" is disabled")
	public void iCheckThatIsDisabled(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String attribute = element.getAttribute("disabled");
		assertTrue(Boolean.parseBoolean(attribute));
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" is not disabled")
	public void iCheckThatIsNotDisabled(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String attribute = element.getAttribute("disabled");
		assertFalse(Boolean.parseBoolean(attribute));
	}

	@Then("^I check that I am not on (?:the )?\"([^\"]*)\"$")
	public void iCheckThatIAmNotOnThe(String expectedPage)
	{
		String expectedPageURL = CurrentPage.getPageObjectItem(expectedPage.toLowerCase());
		String actualPage = CurrentPage.actualURL();

		assertFalse(actualPage.contains(expectedPageURL));
	}

	@Then("^I check (?:the )?\"([^\"]*)\" content is a number$")
	public void iCheckTheContentIsANumber(String elementName)
	{
		WebElement element = CurrentPage.element((elementName));
		String elementText = element.getText();

		assertTrue(TestHelper.isNumeric(elementText));
	}

	@Then("^(?:The )?\"([^\"]*)\" column texts all equal the text \"(.*)\"$")
	public void theColumnTextsAllEqualTheText(String columnElement, String expectedText)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

		if (rows.size() < 1)
		{
			assertTrue(true);
		}
		for (int i = 0; i < rows.size(); i++)
		{
			String rowText = rows.get(i).getText();
			assertTrue(rowText.equals(expectedText));
		}
	}

	@Then("^(?:The )?\"([^\"]*)\" column texts do not contain duplicate values$")
	public void theColumnTextsDoesNotContainDuplicateValues(String columnElement)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
		if (rows.size() < 1)
		{
			assertTrue(true);
		}

		boolean isDuplicate = false;
		for (int i = 0; i < rows.size(); i++)
		{
			for (int j = i + 1; j < rows.size(); j++)
			{
				if (rows.get(i).equals(rows.get(j)))
				{
					isDuplicate = true;
				}
			}
			assertFalse(isDuplicate);
		}
	}

	@Then("^I verify there are \"(.*)\" rows where the \"(.*)\" column text is \"(.*)\"$")
	public void iVerifyThereAreRowsWhereTheColumnTextIs(int expectedAmount, String columnElement, String expectedText)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
		int foundRows = 0;

		for (int i = 0; i < rows.size(); i++)
		{
			String rowText = rows.get(i).getText();
			if (rowText.equals(expectedText))
			{
				foundRows++;
			}
		}

		assertEquals(expectedAmount, foundRows);
	}
@Deprecated
	@Then("^I verify the number of rows where the \"(.*)\" column text is \"(.*)\" equals my remembered value \"(.*)\"$")
	public void iVerifyTheNumberOfRowsWhereTheColumnTextIsEqualsMyRememberedValue(String columnElement, String expectedText, String rememberedValueKey)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
		int foundRows = 0;

		for (int i = 0; i < rows.size(); i++)
		{
			String rowText = rows.get(i).getText();
			if (rowText.equals(expectedText))
			{
				foundRows++;
			}
		}

		String rememberedValueAsString = CurrentPage.retrieve(rememberedValueKey);
		int rememberedValue = Integer.parseInt(rememberedValueAsString);

		assertEquals(rememberedValue, foundRows);
	}

	@Then("^I verify the number of rows where the \"(.*)\" column text is \"(.*)\" equals my stored value \"(.*)\"$")
	public void iVerifyTheNumberOfRowsWhereTheColumnTextIsEqualsMyStoredValue(String columnElement, String expectedText, String rememberedValueKey)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
		int foundRows = 0;

		for (int i = 0; i < rows.size(); i++)
		{
			String rowText = rows.get(i).getText();
			if (rowText.equals(expectedText))
			{
				foundRows++;
			}
		}

		String rememberedValueAsString = CurrentPage.retrievePersisted(rememberedValueKey);
		int rememberedValue = Integer.parseInt(rememberedValueAsString);

		assertEquals(rememberedValue, foundRows);
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" radio button is selected$")
	public void iCheckThatRadioButtonIsSelected(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String class1 = element.getAttribute("class");
		assertTrue(class1.contains("checked"));
	}

	@Then("^(?:The )?\"([^\"]*)\" text contains my remembered \"([^\"]*)\"$")
	public void theTextContainsMyRemembered(String elementName, String key)
	{
		WebElement element = CurrentPage.element(elementName);
		String retrievedText = CurrentPage.retrieve(key);
		TestHelper.waitForElementToContainText(element, retrievedText);
		String elementText = element.getText();
		assertTrue(elementText.contains(retrievedText));
	}

	@Then("^(?:The )?\"([^\"]*)\" text content does not equal my remembered \"([^\"]*)\"$")
	public void theTextContentNotEqualsMyRemembered(String elementName, String key)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = element.getAttribute("textContent").trim();
		String retrievedText = CurrentPage.retrieve(key);

		int currentValue = Integer.parseInt(elementText);
		int rememberedValue = Integer.parseInt(retrievedText);

		Boolean diff = true;
		if (currentValue != rememberedValue)
		{
			diff = true;
		} else diff = false;

		assertTrue(diff);
	}

	@Then("^(?:The )?\"([^\"]*)\" text does not contain \"(.*)\"$")
	public void theTextNotContain(String elementName, String expectedOption)
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = TestHelper.getTextFromElement(element);

		assertFalse(elementText.contains(expectedOption));
	}

	@Then("^(?:The )?\"([^\"]*)\" text equals (?:the )?\"([^\"]*)\" text$")
	public void theTextEqualsTheText(String elementName, String elementName2) throws ParseException
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = TestHelper.getTextFromElement(element);

		WebElement element2 = CurrentPage.element(elementName2);
		String elementText2 = TestHelper.getTextFromElement(element2);

		Assertions.assertEquals(elementText, elementText2);
	}

	@Then("^(?:The )?\"([^\"]*)\" text does not equal (?:the )?\"([^\"]*)\" text$")
	public void theTextNotEqualsTheText(String elementName, String elementName2) throws ParseException
	{
		WebElement element = CurrentPage.element(elementName);
		String elementText = TestHelper.getTextFromElement(element);

		WebElement element2 = CurrentPage.element(elementName2);
		String elementText2 = TestHelper.getTextFromElement(element2);

		Assertions.assertNotEquals(elementText, elementText2);
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" is colored \"([^\"]*)\"")
	public void iCheckThatIsColored(String elementName, String colorCode)
	{
		WebElement element = CurrentPage.element(elementName);
		String color = element.getCssValue("color");
		String hexcolor = Color.fromString(color).asHex();
		assertEquals(colorCode, hexcolor);
	}

	@Then("^(?:The )?\"([^\"]*)\" text is empty$")
	public void textIsEmpty(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		Assertions.assertTrue(element.getText().isEmpty());
	}
	@Then("^(?:The )?\"([^\"]*)\" row contains text \"(.*)\"$")
	public void theRowContainsText(String rowElement, String expectedText)
	{
		List<WebElement> cells = TableHelper.GetColumnValues(rowElement);

		if (cells.size() < 1)
		{
			assertTrue(true);
		}
		for (WebElement cell : cells)
		{
			String cellText = cell.getText();
			assertTrue(cellText.contains(expectedText));
		}
	}

	@Then("^I check that (?:the )?\"([^\"]*)\" is active")
	public void iCheckThatIsActive(String elementName)
	{
		WebElement element = CurrentPage.element(elementName);
		String class1 = element.getAttribute("class");
		assertTrue(class1.contains("active"));
	}

	@Then("^(?:The )?\"([^\"]*)\" column texts all contain the text \"(.*)\"$")
	public void theColumnTextsAllContainTheText(String columnElement, String expectedText)
	{
		List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

		if (rows.size() < 1)
		{
			assertTrue(true);
		}
		for (int i = 0; i < rows.size(); i++)
		{
			String rowText = rows.get(i).getText();
			assertTrue(rowText.contains(expectedText));
		}
	}
}

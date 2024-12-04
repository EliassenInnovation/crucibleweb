package com.eliassen.crucible.web.drivers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eliassen.crucible.web.helpers.ScreenShotter;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.sun.javafx.PlatformUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

public abstract class CrucibleWebdriver implements WebDriver
{
	public static final String CHROME = "chrome";
	public static final String IMPLICIT_WAIT = "IMPLICIT_WAIT";
	public static final String PAGE_LOAD_TIMEOUT = "PAGE_LOAD_TIMEOUT";

	protected MutableCapabilities options;
	private Logger logger;
	private WebDriver instance;

	public CrucibleWebdriver() {
		logger = Logger.getLogger(CrucibleWebdriver.class.getName());
	}
	public WebDriver getInstance()
	{
		return instance;
	}
	protected void setInstance(WebDriver instance)
	{
		 this.instance = instance;
	}

	public boolean hasQuit()
	{
		boolean _hasQuit = true;
		if(getInstance() != null)
		{
			String instanceAsString = getInstance().toString();
			_hasQuit = instanceAsString.contains("null");
		}

		return _hasQuit;
	}

	public boolean isClosed()
	{
		boolean _isClosed = true;
		if(getInstance() != null)
		{
			try
			{
				getInstance().getCurrentUrl();
				_isClosed = false;
			}
			catch(Exception e)
			{
				//do nothing
			}
		}

		return _isClosed;
	}
	
	@Override
	public void get(String url) 
	{
		instance.get(url);
	}

	@Override
	public String getCurrentUrl() 
	{
		return instance.getCurrentUrl();
	}

	@Override
	public String getTitle() 
	{
		return instance.getTitle();
	}

	@Override
	public List<WebElement> findElements(By _by) 
	{
		return instance.findElements(_by);
	}

	@Override
	public WebElement findElement(By _by) 
	{
		try {
			return instance.findElement(_by);
		} catch (NoSuchElementException nse){
			if(SystemHelper.getApplicationSettingBoolean("noSuchElementScreenShots")) {
				new ScreenShotter().safeAttachScreenshot(CurrentPage.getScenario(), "Point of failure");
			}
			throw nse;
		}
	}
	
	public WebElement findElement(String elementXPath)
	{
		return instance.findElement(By.xpath(elementXPath));
	}

	@Override
	public String getPageSource() 
	{
		return instance.getPageSource();
	}

	@Override
	public void close() 
	{
		instance.close();
	}

	@Override
	public void quit() 
	{
		try {
			instance.close();
			instance.quit();
		}
	    catch(org.openqa.selenium.WebDriverException e) {
			logger.log(Level.WARNING, "There was an error quitting the webdriver. " + e.getMessage());
		}
	}

	@Override
	public Set<String> getWindowHandles() 
	{
		return instance.getWindowHandles();
	}

	@Override
	public String getWindowHandle() 
	{
		return instance.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() 
	{
		return instance.switchTo();
	}

	@Override
	public Navigation navigate() 
	{
		return instance.navigate();
	}

	@Override
	public Options manage() 
	{
		return instance.manage();
	}
	
	public void goTo(String _url)
	{
		instance.navigate().to(_url);
	}
	
	private WebDriver resolveDriver(DriverName driverName)
	{
		return resolveDriver(driverName, false);
	}

	private WebDriver resolveDriver(DriverName driverName, boolean useProxy)
	{
		WebDriver _driver = null;
		switch(driverName)
		{
			case firefox:
			case firefox_headless:
				_driver = new FirefoxDriver();
				_driver.manage().window().setSize(new Dimension(1600,900));
				_driver.manage().deleteAllCookies();
				_driver.manage().window().maximize();
				break;
			case remote:
				String hubAddress = SystemHelper.getConfigSetting("selenium_hub.address");
				URL hubUrl = null;
				try
				{
					hubUrl = new URL(hubAddress);
				} catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
				Collection<String> tagNames = CurrentPage.getScenario().getSourceTagNames();
				if(tagNames.contains("@" + DriverName.chrome.toString()))
				{
					options = CrucibleChromeWebdriver.getChromeOptions();
					options.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANY);
					options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
					options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				}

				_driver = new RemoteWebDriver(hubUrl, options);

				break;

			case edge:
			case edge_headless:
				options = CrucibleEdgeWebdriver.getEdgeOptions();

				if(driverName.equals(DriverName.edge_headless))
				{
					((EdgeOptions)options).addArguments("headless");
					((EdgeOptions)options).addArguments("window-size=1600,900");
				}

				try
				{
					_driver = new EdgeDriver((EdgeOptions) options);
				}
				catch(IllegalStateException ise)
				{
					String edgeDriverName = System.getProperty("webdriver.edge.driver");

					FileHelper fileHelper = new FileHelper();
					fileHelper.ExtractFile(edgeDriverName, "a+rwx");

					_driver = new EdgeDriver((EdgeOptions)options);
				}

				_driver.manage().window().setSize(new Dimension(1600,900));
				_driver.manage().window().maximize();
				break;

			case chrome:
			case chrome_headless:
			case chrome_incognito:
			case chrome_incognito_headless:
			default:
				options = CrucibleChromeWebdriver.getChromeOptions();

				if(driverName.equals(DriverName.chrome_headless) ||
				   driverName.equals(DriverName.chrome_incognito_headless))
				{
					((ChromeOptions)options).addArguments("--headless");
					((ChromeOptions)options).addArguments("--window-size=1600,900");
				}

				if(driverName.equals(DriverName.chrome_incognito) ||
						driverName.equals(DriverName.chrome_incognito_headless))
				{
					((ChromeOptions)options).addArguments("--incognito");
				}

				try
				{
					_driver = new ChromeDriver((ChromeOptions)options);
				}
				catch(IllegalStateException ise)
				{
					String chromeDriverName = System.getProperty("webdriver.chrome.driver");

					FileHelper fileHelper = new FileHelper();

					if(!PlatformUtil.isMac())
					{
						fileHelper.ExtractFile(chromeDriverName, "a+rwx");
					}
					else
					{
						fileHelper.ExtractFile(chromeDriverName, "755", "." + File.separator);
					}


					_driver = new ChromeDriver((ChromeOptions)options);
				}

				break;
		}

		return _driver;
	}

	public void enterText(WebElement element, String text) 
	{
   	 	element.sendKeys(text);
	}

	public boolean driverReusable()
	{
		String allowDriverReuseKey = "allowDriverReuse";
		String allowDriverReuse = SystemHelper.getApplicationSetting(allowDriverReuseKey);

		boolean allowDriverReuseFlag = Boolean.parseBoolean(allowDriverReuse);
		return allowDriverReuseFlag;
	}

	public void setImplicitTimeout()
	{
		String implicitWaitString = SystemHelper.getEnvironmentVariable(IMPLICIT_WAIT);
		int implicitWait = 5;
		if(implicitWaitString != null)
		{
			try
			{
				implicitWait = Integer.parseInt(implicitWaitString);
			}
			catch(NumberFormatException nfe)
			{
				//do nothing, use the pre-defined value
			}
		}

		setImplicitTimeoutInSeconds(implicitWait);
	}

	public void setImplicitTimeoutInSeconds(int interval)
	{
		setImplicitTimeout(interval * 1000);
	}

	public void setImplicitTimeoutInMilliseconds(int interval)
	{
		setImplicitTimeout(interval);
	}

	public void setImplicitTimeout(int interval)
	{
		getInstance().manage().timeouts().implicitlyWait(Duration.ofMillis(interval));
	}

	public void setPageLoadTTimeout()
	{
		String pageLoadTimeoutString = SystemHelper.getEnvironmentVariable(PAGE_LOAD_TIMEOUT);
		int pageLoadTimeout = 5;
		if(pageLoadTimeoutString != null)
		{
			try
			{
				pageLoadTimeout = Integer.parseInt(pageLoadTimeoutString);
			}
			catch(NumberFormatException nfe)
			{
				//do nothing, use the pre-defined value
			}
		}

		setPageLoadTTimeoutInSeconds(pageLoadTimeout);
	}

	public void setPageLoadTTimeoutInSeconds(int interval)
	{
		setPageLoadTTimeout(interval, TimeUnit.SECONDS);
	}

	public void setPageLoadTTimeoutInMilliseconds(int interval)
	{
		setPageLoadTTimeout(interval, TimeUnit.MILLISECONDS);
	}

	public void setPageLoadTTimeout(int interval, TimeUnit unit)
	{
		getInstance().manage().timeouts().pageLoadTimeout(interval, unit);
	}

	/**
	 * Checks first to see if there is an @downloadPath tag and returns that
	 * It will then check to see if the JENKINS_HOME environment variable is set. If so it returns $JENKINS_HOME/userContent/downloads
	 * Finally it will return $user.home/downloads if the previous two conditions are not met.
	 * @return
	 */
	public static String getDownloadFilePath(){
		String downloadFilepath = null;
		String jenkins_home = SystemHelper.getEnvironmentVariable("JENKINS_HOME");

		if(CurrentPage.isPersisted("downloadPath_tag")){
			downloadFilepath = CurrentPage.retrievePersisted("downloadPath_tag");
		} else if(jenkins_home != null && !jenkins_home.isEmpty()) {
			downloadFilepath = jenkins_home + File.separator + "userContent" + File.separator + "downloads";
		} else {
			downloadFilepath = System.getProperty("user.home") + File.separator + "Downloads";
		}

		return downloadFilepath;
	}

	public MutableCapabilities getOptions() {
		return options;
	}
}

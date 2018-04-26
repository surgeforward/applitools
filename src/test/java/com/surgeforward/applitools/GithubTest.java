package com.surgeforward.applitools;

import java.util.Arrays;
import java.util.Collection;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.Eyes;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.open;

interface UIStateChangeable{
  public void FireUIStateChanged(String tag);
}

@RunWith(Parameterized.class)
public class GithubTest extends TestCase implements UIStateChangeable
{
  private WebDriver driver;
  
  private int width;
  
  private int height;

  @Rule
  public EyesWatcher eyesWatcher = new EyesWatcher();

  @Parameters
  public static Collection<Integer[]> data() {
    return Arrays.asList(new Integer[][] {
        {1366,1024},
        {800, 600},
    });
  }

  public GithubTest(Integer width, Integer height) {
    this.width = width;
    this.height = height;
  }

  @Before
  public void setUp() throws Exception {
    //ChromeOptions options = new ChromeOptions();
    //options.addArguments("--headless");
    Configuration.browser = "chrome";
    Configuration.headless = true;
    driver = WebDriverRunner.getAndCheckWebDriver();
    
    // set the viewport here to avoid potential instabilities.
    Eyes.setViewportSize(driver, new RectangleSize(width, height));
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }

  @Test
  public void testResponsiveness() {
    open("https://github.com");

    HomePage homePage = new HomePage(driver, this);
    eyesWatcher.eyesCheck(homePage.getName());
    FeaturesPage featuresPage = homePage.goToFeaturesPage();
    eyesWatcher.eyesCheck(featuresPage.getName());
    ExplorePage explorePage = featuresPage.goToExplorePage();
    eyesWatcher.eyesCheck(explorePage.getName());
  }


  public abstract class BasePage {

    protected WebDriver driver;
    private UIStateChangeable uiStateChangedHandler;

    protected final By navMenuLocator = By.className("octicon-three-bars");
    protected final By featuresLocator = By.cssSelector("a[href='/features']");
    protected final By exploreLocator = By.cssSelector("a[href='/explore']");

    private String name;


    public BasePage(String pageName, WebDriver driver, UIStateChangeable uiStateChangedHandler) {
      this.driver = driver;
      this.name = pageName;
      this.uiStateChangedHandler = uiStateChangedHandler;
    }

    protected void clickNavButton(By locator) {
      WebElement navMenu = driver.findElement(navMenuLocator);
      if (navMenu.isDisplayed()) {
        navMenu.click();
        this.uiStateChangedHandler.FireUIStateChanged("Navigation Menu");
      }

      driver.findElement(locator).click();
    }

    public String getName()
    {
      return this.name;
    }

    public FeaturesPage goToFeaturesPage() {
      clickNavButton(featuresLocator);
      return new FeaturesPage(this.driver, this.uiStateChangedHandler);
    }

    public ExplorePage goToExplorePage() {
      clickNavButton(exploreLocator);

      return new ExplorePage(this.driver, this.uiStateChangedHandler);
    }
  }

  public class HomePage extends BasePage {

    public HomePage(WebDriver driver, UIStateChangeable uiStateChangedHandler) {
      super("Home", driver, uiStateChangedHandler);
    }
  }

  public class FeaturesPage extends BasePage {

    public FeaturesPage(WebDriver driver, UIStateChangeable uiStateChangedHandler) {
      super("Features", driver, uiStateChangedHandler);
    }
  }

  public class ExplorePage extends BasePage {

    public ExplorePage(WebDriver driver, UIStateChangeable uiStateChangedHandler) {
      super("Explore", driver, uiStateChangedHandler);
    }
  }

  public void FireUIStateChanged(String tag) {
    eyesWatcher.eyesCheck(tag);
  }
}

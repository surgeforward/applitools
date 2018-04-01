package com.surgeforward.applitools;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.WrapsDriver;

public class EyesWatcher
    extends TestWatcher
{
  private int height;

  private int width;

  public Eyes eyes = new Eyes();

  public EyesWatcher(int width, int height) {
    this.height = height;
    this.width = width;
  }

  private String testName;

  private static BatchInfo batch;

  private static final String APPLITOOLS_KEY = System.getProperty("applitoolsKey");

  private static final String APPLICATION_NAME = System.getProperty("applicationName", "Applitools Test App");

  private void initializeApplitools() {
    String localBranchName = System.getProperty("branchName", System.getenv("bamboo_planRepository_branchName"));
    eyes.setIsDisabled(APPLITOOLS_KEY == null);

    if (!eyes.getIsDisabled() && localBranchName != null) {
      String buildNumber = System.getenv("bamboo_buildNumber");
      batch = new BatchInfo(localBranchName + (buildNumber != null ? " #" + buildNumber : ""));

      // Aggregates tests under the same batch when tests are run in different processes (e.g. split tests in bamboo).
      if (buildNumber != null) {
        batch.setId(batch.getName());
      }
      eyes.setApiKey(APPLITOOLS_KEY);
      eyes.setBatch(batch);

      eyes.setBranchName(localBranchName);

      // set the default parent branch to master if the parent branch is not specified
      eyes.setParentBranchName(System.getProperty("parentBranchName", "master"));

      eyes.setIgnoreCaret(true);
    }
  }

  @Override
  protected void starting(Description description) {
    initializeApplitools();
    if (!eyes.getIsDisabled() && eyes.getBatch() == null) {
      throw new IllegalArgumentException(
          "The branchName parameter or the Bamboo environment variables are required if visual testing is enabled " +
              "(the applitoolsKey property is provided).");
    }
    testName = description.getTestClass().getSimpleName() + "." + description.getMethodName();
  }

  @Override
  protected void finished(Description description) {
    try {
      // End visual testing. Validate visual correctness.
      if (eyes.getIsOpen()) {
        eyes.close(true);
      }
    }
    finally {
      testName = null;
      // Abort test in case of an unexpected error.
      eyes.abortIfNotClosed();
    }
  }

  public void eyesCheck() {
    eyesCheck(null);
  }

  /**
   * Convenience method for performing the Applitools validation.
   *
   * @param tag or step name of the validation
   */
  public void eyesCheck(String tag) {
    if (!eyes.getIsOpen()) {
      WebDriver remoteDriver = WebDriverRunner.getAndCheckWebDriver();

      if (remoteDriver instanceof WrapsDriver) {
        remoteDriver = ((WrapsDriver) remoteDriver).getWrappedDriver();
      }

      eyes.open(remoteDriver, APPLICATION_NAME, testName, new RectangleSize(width, height));
    }
    eyes.check(tag, Target.window());
  }
}

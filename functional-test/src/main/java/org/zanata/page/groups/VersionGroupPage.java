/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.page.groups;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.zanata.page.BasePage;
import org.zanata.page.projects.ProjectVersionsPage;
import org.zanata.page.projectversion.VersionLanguagesPage;
import org.zanata.util.TableRow;
import org.zanata.util.WebElementUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Slf4j
public class VersionGroupPage extends BasePage {

    private By versionsInGroupTable = By.id("projects-project_list");
    private By projectForm = By.id("projects-project_form");
    private By projectSearchField = By
            .id("settings-projects-form:newVersionField:newVersionInput");
    private By projectAddButton = By
            .id("settings-projects-form:group-add-new-project-button");
    private final By newVersionList = By
            .id("settings-projects-form:newVersionField:newVersionItems");
    private By groupNameLabel = By.id("group-info");

    private By languagesTab = By.id("languages_tab");
    private By projectsTab = By.id("projects_tab");
    private By maintainersTab = By.id("maintainers_tab");
    private By settingsTab = By.id("settings_tab");

    private By languagesTabBody = By.id("languages");
    private By projectsTabBody = By.id("projects");
    private By maintainersTabBody = By.id("maintainers");
    private By settingsTabBody = By.id("settings");

    private By settingsLanguagesTab = By.id("settings-languages_tab");

    public VersionGroupPage(final WebDriver driver) {
        super(driver);
    }

    public String getGroupName() {
        return waitForWebElement(groupNameLabel)
                .findElement(By.tagName("h1")).getText();
    }

    public List<WebElement> searchProject(final String projectName,
            final int expectedResultNum) {
        waitForWebElement(projectSearchField).sendKeys(projectName);

        return refreshPageUntil(this,
                new Function<WebDriver, List<WebElement>>() {
                    @Override
                    public List<WebElement> apply(WebDriver driver) {
                        // we want to wait until search result comes back. There
                        // is no way we can tell whether search result has come
                        // back and table refreshed.
                        // To avoid the
                        // org.openqa.selenium.StaleElementReferenceException
                        // (http://seleniumhq.org/exceptions/stale_element_reference.html),
                        // we have to set expected result num

                        List<WebElement> listItems =
                                WebElementUtil.getListItems(getDriver(),
                                        newVersionList);

                        if (listItems.size() != expectedResultNum) {
                            log.debug("waiting for search result refresh...");
                            return null;
                        }
                        return listItems;
                    }
                });
    }

    public VersionGroupPage addToGroup(int rowIndex) {
        WebElementUtil.getListItems(getDriver(), newVersionList)
                .get(rowIndex).click();
        waitForWebElement(projectAddButton).click();
        return new VersionGroupPage(getDriver());
    }

    /**
     * Get the list of project versions attached to the group
     * @return a list of version group identifiers in the format
     *         "$projectID $version"
     */
    public List<String> getProjectVersionsInGroup() {
        log.info("Query Group project versions");
        List<WebElement> elements = WebElementUtil
                .getListItems(getDriver(), versionsInGroupTable);

        List<String> result = new ArrayList<String>();

        for (WebElement element : elements) {
            result.add(element
                    .findElement(By.className("list__item__info"))
                    .getText());
        }
        return result;
    }

    public ProjectVersionsPage clickOnProjectLinkOnRow(int row) {
        List<TableRow> tableRows =
                WebElementUtil
                        .getTableRows(getDriver(), versionsInGroupTable);
        WebElement projectLink =
                tableRows.get(row).getCells().get(0)
                        .findElement(By.tagName("a"));
        projectLink.click();
        return new ProjectVersionsPage(getDriver());
    }

    public VersionLanguagesPage clickOnProjectVersionLinkOnRow(int row) {
        List<TableRow> tableRows =
                WebElementUtil
                        .getTableRows(getDriver(), versionsInGroupTable);
        WebElement versionLink =
                tableRows.get(row).getCells().get(1)
                        .findElement(By.tagName("a"));
        versionLink.click();
        return new VersionLanguagesPage(getDriver());
    }

    public VersionGroupPage clickAddProjectVersionsButton() {
        log.info("Click Add Project Version");
        // parent
        waitForWebElement(waitForElementExists(projectForm),
                By.className("button--primary")).click();
        return new VersionGroupPage(getDriver());
    }

    public VersionGroupPage clickLanguagesTab() {
        log.info("Click Languages tab");
        waitForElementExists(languagesTabBody);
        clickWhenTabEnabled(waitForWebElement(languagesTab));
        return new VersionGroupPage(getDriver());
    }

    public VersionGroupPage clickProjectsTab() {
        log.info("Click Projects tab");
        waitForElementExists(projectsTabBody);
        clickWhenTabEnabled(waitForWebElement(projectsTab));
        return new VersionGroupPage(getDriver());
    }

    public VersionGroupPage clickMaintainersTab() {
        log.info("Click Maintainers tab");
        waitForElementExists(maintainersTabBody);
        clickWhenTabEnabled(waitForWebElement(maintainersTab));
        return new VersionGroupPage(getDriver());
    }

    public VersionGroupPage clickSettingsTab() {
        log.info("Click Settings tab");
        waitForElementExists(settingsTabBody);
        clickWhenTabEnabled(waitForWebElement(settingsTab));
        return new VersionGroupPage(getDriver());
    }

    public VersionGroupPage clickLanguagesSettingsTab() {
        clickSettingsTab();
        waitForWebElement(settingsLanguagesTab).click();
        return new VersionGroupPage(getDriver());
    }

    public Boolean isLanguagesTabActive() {
        log.info("Query is languages tab displayed");
        final WebElement languagesTab = waitForWebElement(By.id("languages"));
        waitForAMoment().until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver webDriver) {
                return languagesTab.getAttribute("class").contains("is-active");
            }
        });
        return languagesTab.getAttribute("class").contains("is-active");
    }

    public Boolean isProjectsTabActive() {
        final WebElement languagesTab = waitForElementExists(By.id("projects"));
        waitForAMoment().until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver webDriver) {
                return languagesTab.getAttribute("class").contains("is-active");
            }
        });
        return languagesTab.getAttribute("class").contains("is-active");
    }

    /**
     * Enter a project version identifier
     * @param projectVersion identifier in format "$projectID $version"
     * @return new VersionGroupPage
     */
    public VersionGroupPage enterProjectVersion(String projectVersion) {
        log.info("Enter project version {}", projectVersion);
        waitForWebElement(By.id("versionAutocomplete-autocomplete__input"))
                .sendKeys(projectVersion);
        return new VersionGroupPage(getDriver());
    }

    public VersionGroupPage selectProjectVersion(final String searchEntry) {
        log.info("Click project version {}", searchEntry);
        waitForAMoment().until(
                new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(WebDriver driver) {
                        List<WebElement> items = WebElementUtil
                                .getSearchAutocompleteResults(driver,
                                        "settings-projects-form",
                                        "versionAutocomplete");
                        for (WebElement item : items) {
                            if (item.getText().equals(searchEntry)) {
                                item.click();
                                return true;
                            }
                        }
                        return false;
                    }
                });
        return new VersionGroupPage(getDriver());
    }

    public VersionGroupPage confirmAddProject() {
        new Actions(getDriver()).sendKeys(Keys.ENTER);
        return new VersionGroupPage(getDriver());
    }

}

/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.page.utility;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.zanata.page.BasePage;
import org.zanata.page.administration.EditHomeCodePage;
import org.zanata.page.administration.EditHomeContentPage;

@Slf4j
public class HomePage extends BasePage {

    public static final String SIGNUP_SUCCESS_MESSAGE =
            "You will soon receive an email " +
            "with a link to activate your account.";
    public static final String EMAILCHANGED_MESSAGE = "Email updated.";

    private By mainBodyContent = By.id("main_body_content");
    private By editPageContentButton = By.linkText("Edit Page Content");
    private By editPageCodeButton = By.linkText("Edit Page Code");

    public HomePage(final WebDriver driver) {
        super(driver);
    }

    public EditHomeContentPage goToEditPageContent() {
        log.info("Click Edit Page Content");
        waitForWebElement(editPageContentButton).click();
        return new EditHomeContentPage(getDriver());
    }

    public EditHomeCodePage goToEditPageCode() {
        log.info("Click Edit Page Code");
        waitForWebElement(editPageCodeButton).click();
        return new EditHomeCodePage(getDriver());
    }

    public String getMainBodyContent() {
        log.info("Query homepage content");
        return waitForWebElement(mainBodyContent).getText();
    }
}

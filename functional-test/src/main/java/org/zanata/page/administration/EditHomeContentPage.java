/*
 * Copyright 2013, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.page.administration;

import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.zanata.page.BasePage;
import org.zanata.page.utility.HomePage;

/**
 * @author Damian Jansen <a
 *         href="mailto:djansen@redhat.com">djansen@redhat.com</a>
 */
@Slf4j
public class EditHomeContentPage extends BasePage {

    private By updateButton = By.id("homeContentForm:update");
    private By cancelButton = By.id("homeContentForm:cancel");

    public EditHomeContentPage(final WebDriver driver) {
        super(driver);
    }

    public EditHomeContentPage enterText(String text) {
        log.info("Enter homepage code\n{}", text);
        // Switch to the CKEditor frame
        getDriver().switchTo().frame(waitForWebElement(By
                .id("cke_homeContentForm:homeContent:inp"))
                .findElement(By.tagName("iframe")));
        waitForWebElement(By.tagName("body")).sendKeys(text);
        // Switch back!
        getDriver().switchTo().defaultContent();
        return new EditHomeContentPage(getDriver());
    }

    public HomePage update() {
        log.info("Click Update");
        waitForWebElement(updateButton).click();
        return new HomePage(getDriver());
    }

    public HomePage cancelUpdate() {
        log.info("Click Cancel");
        waitForWebElement(cancelButton).click();
        return new HomePage(getDriver());
    }
}

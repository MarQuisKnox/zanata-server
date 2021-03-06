/*
 * Copyright 2014, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
public abstract class ImmutableDbunitJpaTest extends ZanataDbunitJpaTest {

    @BeforeClass
    public void setupDatabase() throws Exception {
        super.setupEM();
        super.prepareDataBeforeTest();
    }

    @Override
    protected void setupEM() {
        // do nothing
    }

    @BeforeMethod
    @Override
    protected void prepareDataBeforeTest() {
        // do nothing
    }

    @AfterClass
    public void tearDownDatabase() {
        super.cleanDataAfterTest();
        super.shutdownEM();
    }

    @Override
    protected void shutdownEM() {
        // do nothing
    }

    @AfterMethod
    @Override
    public void cleanDataAfterTest() {
        // do nothing
    }

}

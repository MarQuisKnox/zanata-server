/*
 * Copyright 2012, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.service;

import java.util.List;

import org.zanata.email.EmailStrategy;
import org.zanata.model.HLocale;
import org.zanata.model.HPerson;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public interface EmailService {

    /**
     * send account activation email to register user
     *
     * @param toName
     * @param toEmailAddr
     * @param activationKey
     * @return
     */
    String sendActivationEmail(String toName,
            String toEmailAddr, String activationKey);

    String sendEmailValidationEmail(String toName,
            String toEmailAddr, String activationKey);

    String sendPasswordResetEmail(HPerson person, String key);

    /**
     * sends emails to configured admin emails for server, or admin users if no
     * server emails are configured.
     */
    String sendToAdmins(EmailStrategy strategy);

    /**
     * sends emails to version group maintainers -> admin -> admin users
     */
    String sendToVersionGroupMaintainers(List<HPerson> maintainers,
            EmailStrategy strategy);

    /**
     * sends emails to language coordinators -> admin -> admin users
     *
     */
    String sendToLanguageCoordinators(HLocale locale,
            EmailStrategy strategy);

    String sendUsernameChangedEmail(String email, String newUsername);

}

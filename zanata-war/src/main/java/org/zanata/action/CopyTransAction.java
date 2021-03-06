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
package org.zanata.action;

import java.io.Serializable;
import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.zanata.async.handle.CopyTransTaskHandle;
import org.zanata.dao.ProjectIterationDAO;
import org.zanata.i18n.Messages;
import org.zanata.model.HCopyTransOptions;
import org.zanata.model.HProjectIteration;
import org.zanata.seam.scope.ConversationScopeMessages;
import org.zanata.service.impl.CopyTransOptionFactory;
import org.zanata.ui.ProgressBar;
import org.zanata.util.DateUtil;
import com.google.common.base.Optional;

import lombok.Getter;
import lombok.Setter;

/**
 * Copy Trans page action bean.
 *
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Name("copyTransAction")
@Scope(ScopeType.CONVERSATION)
public class CopyTransAction implements Serializable, ProgressBar {
    private static final long serialVersionUID = 1L;

    @In
    private ProjectIterationDAO projectIterationDAO;

    @In
    private CopyTransManager copyTransManager;

    @In
    private ConversationScopeMessages conversationScopeMessages;

    @In
    private Messages msgs;

    @In
    private CopyTransOptionsModel copyTransOptionsModel;

    @Getter
    @Setter
    private String iterationSlug;

    @Getter
    @Setter
    private String projectSlug;

    private HProjectIteration projectIteration;

    @Create
    public void onCreate() {
        copyTransOptionsModel.setInstance(CopyTransOptionFactory
                .getExplicitOptions());
    }

    @Override
    public boolean isInProgress() {
        return copyTransManager.isCopyTransRunning(getProjectIteration());
    }

    @Begin(join = true)
    public void updateCopyTrans(String action, String value) {
        copyTransOptionsModel.update(action, value);
    }

    @Override
    public String getCompletedPercentage() {
       CopyTransTaskHandle handle =
                copyTransManager
                        .getCopyTransProcessHandle(getProjectIteration());
        if (handle != null) {
            double completedPercent =
                    (double) handle.getCurrentProgress() / (double) handle
                            .getMaxProgress() * 100;
            if (Double.compare(completedPercent, 100) == 0) {
                conversationScopeMessages
                        .setMessage(
                                FacesMessage.SEVERITY_INFO,
                                msgs.get("jsf.iteration.CopyTrans.Completed"));
            }
            return PERCENT_FORMAT.format(completedPercent);
        } else {
            return "0";
        }
    }

    public HProjectIteration getProjectIteration() {
        if (projectIteration == null) {
            projectIteration =
                    projectIterationDAO.getBySlug(projectSlug, iterationSlug);
        }
        return projectIteration;
    }

    @Restrict("#{s:hasPermission(copyTransAction.projectIteration, 'copy-trans')}")
    public
            void startCopyTrans() {
        if (isInProgress()) {
            return;
        } else if (getProjectIteration().getDocuments().size() <= 0) {
            conversationScopeMessages.setMessage(FacesMessage.SEVERITY_INFO,
                    msgs.get("jsf.iteration.CopyTrans.NoDocuments"));
            return;
        }

        HCopyTransOptions options = copyTransOptionsModel.getInstance();

        copyTransManager.startCopyTrans(getProjectIteration(), options);
        conversationScopeMessages.setMessage(FacesMessage.SEVERITY_INFO,
                msgs.get("jsf.iteration.CopyTrans.Started"));
    }

    public void cancel() {
        copyTransManager.cancelCopyTrans(getProjectIteration());
        conversationScopeMessages.setMessage(FacesMessage.SEVERITY_INFO,
                msgs.get("jsf.iteration.CopyTrans.Cancelled"));
    }

    public String getCurrentProgress() {
        CopyTransTaskHandle handle =
                copyTransManager
                        .getCopyTransProcessHandle(getProjectIteration());
        if (handle != null) {
            return String.valueOf(handle.getCurrentProgress());
        }
        return "";
    }

    public String getMaxProgress() {
        CopyTransTaskHandle handle =
                copyTransManager
                        .getCopyTransProcessHandle(getProjectIteration());
        if (handle != null) {
            return String.valueOf(handle.getMaxProgress());
        }
        return "";
    }

    public String getCopyTransEstimatedTimeLeft() {
        CopyTransTaskHandle handle =
                copyTransManager
                        .getCopyTransProcessHandle(getProjectIteration());
        if (handle != null) {
            Optional<Long> estimatedTimeRemaining =
                    handle.getEstimatedTimeRemaining();
            if (estimatedTimeRemaining.isPresent()) {
                return DateUtil
                        .getTimeRemainingDescription(estimatedTimeRemaining
                                .get());
            }
        }
        return "";
    }
}

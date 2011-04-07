/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.issuekicker.actions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.issuekicker.ActiveReports;
import org.netbeans.modules.issuekicker.Report;
import org.netbeans.modules.issuekicker.ReportTask;
import org.netbeans.modules.issuekicker.jsoup.JsoupIssueDetail;
import org.netbeans.modules.issuekicker.ui.ReportSelectorPanelController;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;

@ActionID(category = "Tools",
id = "org.netbeans.modules.issuekicker.AddReportAnalyzerAction")
@ActionRegistration(iconBase = "org/netbeans/modules/issuekicker/resources/toolbar-icon.png",
displayName = "#CTL_ReportAnalyzerAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 140, separatorAfter = 160),
    @ActionReference(path = "Toolbars/IssueKicker", position = 1000),
    @ActionReference(path = "Shortcuts", name = "DO-I")
})
@Messages("CTL_ReportAnalyzerAction=Analyze Exception Report")
/**
 * @author Martin Fousek
 */
public final class AddReportAnalyzerAction implements ActionListener {

    private RequestProcessor rp = new RequestProcessor("Bugzilla query", 1, true);  // NOI18N

    @Override
    public void actionPerformed(ActionEvent e) {
        final ReportSelectorPanelController controller = new ReportSelectorPanelController();
        DialogDescriptor descriptor = new DialogDescriptor(controller.getPanel(),
                NbBundle.getMessage(AddReportAnalyzerAction.class, "MSG_DIALOG_TITLE_SELECT_REPORT")); //NOI18N
        descriptor.setValid(false);
        controller.getPanel().setError(NbBundle.getMessage(
                ReportSelectorPanelController.class, "ERR_EMPTY_FIELDS")); //NOI18N);
        controller.setDialogDescriptor(descriptor);
        Object returnButton = DialogDisplayer.getDefault().notify(descriptor);

        if (returnButton == DialogDescriptor.OK_OPTION) {
            
            final Task[] t = new Task[1];
            Cancellable c = new Cancellable() {
                @Override
                public boolean cancel() {
                    if(t[0] != null) {
                        return t[0].cancel();
                    }
                    return true;
                }
            };
            
            final String msgPopulating = NbBundle.getMessage(AddReportAnalyzerAction.class, 
                    "MSG_WAIT_FOR_REPORT_NUMBER", controller.getPanel().getExceptionNumber());  // NOI18N
            final ProgressHandle handle = ProgressHandleFactory.createHandle(msgPopulating, c);
            
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    handle.start();
                }
            });
            
            t[0] = rp.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        // get entered number
                        Integer reportNumber = getReportNumberFromDialog(controller);
                        assert (reportNumber == null || reportNumber != -1) : "Number wasn't valid : RN=" + controller.getPanel().getReportNumber() + ", "
                                + "EN=" + controller.getPanel().getExceptionNumber(); //NOI18N

                        if (reportNumber == null) 
                            return;

                        Logger.getLogger(AddReportAnalyzerAction.class.getName()).log(
                                Level.INFO, "New Report Analyzer Action (report #{0}) is adding to the active list.", reportNumber); //NOI18N
                        ActiveReports.getDefault().addReportTask(new ReportTask(new Report(reportNumber)));
                    } finally {
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                handle.finish();
                            }
                        });
                    }
                }
            });
        }
    }

    private Integer getReportNumberFromDialog(ReportSelectorPanelController controller) {
        Integer number = -1;
        String reportNumber = controller.getPanel().getReportNumber();
        String exceptionNumber = controller.getPanel().getExceptionNumber();

        if (ReportSelectorPanelController.isNumber(reportNumber)) {
            number = Integer.parseInt(reportNumber);
        } else if (ReportSelectorPanelController.isNumber(exceptionNumber)) {
            // get report number from the issue zilla
            number = JsoupIssueDetail.getExceptionReportNumber(Integer.parseInt(exceptionNumber));
        }

        return number;
    }
}

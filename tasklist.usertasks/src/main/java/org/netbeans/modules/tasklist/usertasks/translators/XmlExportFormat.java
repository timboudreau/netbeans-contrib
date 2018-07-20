/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.tasklist.usertasks.translators;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.netbeans.modules.tasklist.export.ExportImportFormat;
import org.netbeans.modules.tasklist.export.ExportImportProvider;
import org.netbeans.modules.tasklist.export.SaveFilePanel;
import org.netbeans.modules.tasklist.export.SimpleWizardPanel;


import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.LineResource;
import org.netbeans.modules.tasklist.usertasks.model.URLResource;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskResource;
import org.netbeans.modules.tasklist.usertasks.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Export to XML
 */
public class XmlExportFormat implements ExportImportFormat {
    protected final static String 
        CHOOSE_FILE_PANEL_PROP = "ChooseFilePanel"; // NOI18N
    
    private static String LINE_SEPARATOR = 
        System.getProperty("line.separator"); // NOI18N
    private static DateFormat TIME_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ssZ"); // NOI18N
    
    private static final String[] PRIORITIES =  {
        "high", // NOI18N
        "medium-high", // NOI18N
        "medium", // NOI18N
        "medium-low", // NOI18N
        "low" // NOI18N
    };
    
    /**
     * Creates a new instance of XmlExportFormat
     */
    public XmlExportFormat() {
    }
    
    public String getName() {
        return NbBundle.getMessage(
            XmlExportFormat.class, "XML"); // NOI18N
    }
    
    public org.openide.WizardDescriptor getWizard() {
        SaveFilePanel chooseFilePanel = new SaveFilePanel();
        SimpleWizardPanel chooseFileWP = new SimpleWizardPanel(chooseFilePanel);
        chooseFilePanel.setWizardPanel(chooseFileWP);
        chooseFilePanel.getFileChooser().addChoosableFileFilter(
            new ExtensionFileFilter(
                NbBundle.getMessage(XmlExportFormat.class, 
                    "XmlFilter"), // NOI18N
                new String[] {".xml"})); // NOI18N
        chooseFilePanel.setFile(new File(
                Settings.getDefault().getLastUsedExportFolder(), 
                "tasklist.xml")); // NOI18N
        
        // create the wizard
        WizardDescriptor.Iterator iterator = 
            new WizardDescriptor.ArrayIterator(new WizardDescriptor.Panel[] {
                chooseFileWP
        });
        WizardDescriptor d = new WizardDescriptor(iterator);
        d.putProperty("WizardPanel_contentData", // NOI18N
            new String[] {
                NbBundle.getMessage(
                    XmlExportFormat.class, "ChooseDestination"), // NOI18N
            }
        ); // NOI18N
        d.setTitle(NbBundle.getMessage(XmlExportFormat.class,
            "ExportToXml")); // NOI18N
        d.putProperty(CHOOSE_FILE_PANEL_PROP, chooseFilePanel);
        d.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        d.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        d.putProperty("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        d.setTitleFormat(new java.text.MessageFormat("{0}")); // NOI18N todo
        return d;
    }
    
    /**
     * Creates a transformer
     *
     * @return created transformer
     */
    protected Transformer createTransformer() {
        try {
            return TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e);
            return null;
        }
    }
    
    public void doExportImport(ExportImportProvider provider, WizardDescriptor wd) {
        SaveFilePanel panel = 
            (SaveFilePanel) wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        try {
            UserTaskList list = UserTaskViewRegistry.getInstance().
                    getCurrent().getUserTaskList();
            Document doc = createXml(list);
            Transformer t = createTransformer();
            Source source = new DOMSource(doc);
            StreamResult result = new StreamResult(panel.getFile());
            try {
                t.transform(source, result);
            } finally {
                if (result.getOutputStream() != null) {
                    try {
                        result.getOutputStream().close();
                    } catch (IOException ex) {
                        UTUtils.LOGGER.log(Level.WARNING, "", ex);
                    }
                }
            }
            Settings.getDefault().setLastUsedExportFolder(
                    panel.getFile().getParentFile());
        } catch (TransformerException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e);
        } catch (ParserConfigurationException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e);
        } catch (SAXException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     * Creates xml for the specified task list
     *
     * @param list task list
     * @return created XML
     */
    public Document createXml(UserTaskList list) 
    throws ParserConfigurationException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();
        Element tasks = doc.createElement("tasks"); // NOI18N
        doc.appendChild(tasks);

        Iterator it = list.getSubtasks().iterator();
        while (it.hasNext()) {
            task(tasks, (UserTask) it.next());
        }
        return doc;
    }
    
    /**
     * Process one task
     *
     * @param el parent node
     * @param task a task to process
     */
    private void task(Element el, UserTask task) throws SAXException {
        Document doc = el.getOwnerDocument();
        Element node = doc.createElement("task"); // NOI18N
        el.appendChild(node);
        
        node.setAttribute("priority", // NOI18N
            PRIORITIES[task.getPriority() - 1]);
        
        if (task.getCategory().length() != 0) {
            node.setAttribute("category", // NOI18N
                task.getCategory());
        }
        
        node.setAttribute("progress", // NOI18N
            String.valueOf(task.getPercentComplete()));
        
        if (task.isValuesComputed()) {
            // for backward compatibility only
            node.setAttribute("progress-computed", "yes"); // NOI18N
            node.setAttribute("effort-computed", "yes"); // NOI18N
            node.setAttribute("spent-time-computed", "yes"); // NOI18N
            
            node.setAttribute("values-computed", "yes"); // NOI18N
        }

        node.setAttribute("effort", String.valueOf(task.getEffort())); // NOI18N
        
        node.setAttribute("spent-time", // NOI18N
            String.valueOf(task.getSpentTime()));
        
        if (task.getDueDate() != null) {
            node.setAttribute("due", dateToString(task.getDueDate())); // NOI18N
        }
        
        node.setAttribute("created", // NOI18N
            dateToString(new Date(task.getCreatedDate())));

        node.setAttribute("modified", // NOI18N
            dateToString(new Date(task.getLastEditedDate())));
        
        if (task.getCompletedDate() != 0)
            node.setAttribute("completed", // NOI18N
                dateToString(new Date(task.getCompletedDate())));
        
        if (task.getOwner().length() != 0)
            node.setAttribute("owner", task.getOwner()); // NOI18N
        
        if (task.getStart() != -1)
            node.setAttribute("start", // NOI18N
                dateToString(new Date(task.getStart())));

        Element summary = doc.createElement("summary"); // NOI18N
        node.appendChild(summary);
        summary.appendChild(doc.createTextNode(task.getSummary()));
        
        if (task.getDetails().length() > 0) {
            Element details = doc.createElement("details"); // NOI18N
            node.appendChild(details);
            details.appendChild(doc.createTextNode(task.getDetails()));
        }
        
        node.appendChild(doc.createTextNode(LINE_SEPARATOR));
        
        ObjectList wps = task.getWorkPeriods();
        if (wps.size() > 0) {
            Element workPeriods = doc.createElement("work-periods"); // NOI18N
            node.appendChild(workPeriods);
            for (int i = 0; i < wps.size(); i++) {
                UserTask.WorkPeriod wp = (UserTask.WorkPeriod) wps.get(i);
                Element period = doc.createElement("period"); // NOI18N
                period.setAttribute("start", // NOI18N
                    dateToString(new Date(task.getCompletedDate())));
                period.setAttribute("duration", // NOI18N
                    Integer.toString(wp.getDuration()));
                workPeriods.appendChild(period);
            }
        }
        
        ObjectList<UserTaskResource> ress = task.getResources();
        if (ress.size() > 0) {
            Element resources = doc.createElement("resources"); // NOI18N
            node.appendChild(resources);
            for (UserTaskResource r: ress) {
                if (r instanceof URLResource) {
                    Element urlResource = doc.createElement(
                            "url-resource"); // NOI18N
                    urlResource.setAttribute("url", // NOI18N
                            ((URLResource) r).getUrl().toExternalForm());
                    resources.appendChild(urlResource);
                } else if (r instanceof LineResource) {
                    Element lineResource = doc.createElement(
                            "line-resource"); // NOI18N
                    lineResource.setAttribute("url", // NOI18N
                            ((LineResource) r).getURL().toExternalForm());
                    lineResource.setAttribute("line-number", // NOI18N
                            Integer.toString(((LineResource) r).getLineNumber()));
                    resources.appendChild(lineResource);
                } else {
                    UTUtils.LOGGER.warning("Unknown resource type"); // NOI18N
                }
            }
        }

        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            task(node, (UserTask) it.next());
        }
    }
    
    /**
     * Converts a date to a string according to 
     * http://www.w3.org/TR/NOTE-datetime 
     *
     * @param d a date
     * @return string in format YYYY-MM-DDThh:mm:ssTZD
     */
    private String dateToString(Date d) {
        String s = TIME_FORMAT.format(d);
        return s.substring(0, s.length() - 2) + ":" + // NOI18N
            s.substring(s.length() - 2, s.length());
    }
}

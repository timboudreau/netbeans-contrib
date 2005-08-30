/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.translators;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.filechooser.FileSystemView;
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

import org.netbeans.modules.tasklist.core.export.ExportImportFormat;
import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.export.SaveFilePanel;
import org.netbeans.modules.tasklist.core.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.core.util.ObjectList;
import org.netbeans.modules.tasklist.core.util.SimpleWizardPanel;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.ErrorManager;
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
            FileSystemView.getFileSystemView().
            getDefaultDirectory(), "tasklist.xml")); // NOI18N
        
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
            ErrorManager.getDefault().notify(e);
            return null;
        }
    }
    
    public void doExportImport(ExportImportProvider provider, WizardDescriptor wd) {
        SaveFilePanel panel = 
            (SaveFilePanel) wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        try {
            UserTaskList list = (UserTaskList) UserTaskView.getCurrent().getUserTaskList();
            Document doc = createXml(list);
            Transformer t = createTransformer();
            Source source = new DOMSource(doc);
            Result result = new StreamResult(panel.getFile());
            t.transform(source, result);
        } catch (TransformerException e) {
            ErrorManager.getDefault().notify(e);
        } catch (ParserConfigurationException e) {
            ErrorManager.getDefault().notify(e);
        } catch (SAXException e) {
            ErrorManager.getDefault().notify(e);
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
        
        if (task.isProgressComputed()) {
            node.setAttribute("progress-computed", "yes"); // NOI18N
        }

        node.setAttribute("effort", String.valueOf(task.getEffort())); // NOI18N
        
        if (task.isEffortComputed()) {
            node.setAttribute("effort-computed", "yes"); // NOI18N
        }
        
        node.setAttribute("spent-time", // NOI18N
            String.valueOf(task.getSpentTime()));
        
        if (task.isSpentTimeComputed()) {
            node.setAttribute("spent-time-computed", "yes"); // NOI18N
        }
        
        if (task.getDueDate() != null) {
            node.setAttribute("due", dateToString(task.getDueDate())); // NOI18N
        }
        
        URL url = task.getUrl();
        if (url != null) {
            node.setAttribute("file", // NOI18N
                url.toExternalForm());
            node.setAttribute("line", // NOI18N
                String.valueOf(task.getLineNumber() + 1));
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

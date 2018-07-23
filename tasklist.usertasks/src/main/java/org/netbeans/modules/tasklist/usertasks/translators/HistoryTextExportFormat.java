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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.netbeans.modules.tasklist.export.ExportImportFormat;
import org.netbeans.modules.tasklist.export.ExportImportProvider;
import org.netbeans.modules.tasklist.export.SaveFilePanel;
import org.netbeans.modules.tasklist.export.SimpleWizardPanel;

import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;
import org.netbeans.modules.tasklist.usertasks.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.usertasks.util.TreeAbstraction;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Export spent times for a time period.
 *
 * @author tl
 */
public class HistoryTextExportFormat implements ExportImportFormat {
    private static DurationFormat DURATION_FORMAT = 
            new DurationFormat(DurationFormat.Type.SHORT);

    /**
     * Localizes a string.
     *
     * @param key a key
     * @return localized string
     */
    private static String loc(String key) {
        return NbBundle.getMessage(HistoryTextExportFormat.class, key);
    }
    
    public String getName() {
        return loc("SpentTimes"); // NOI18N
    }

    public WizardDescriptor getWizard() {
        HistoryOptionsPanel hop = new HistoryOptionsPanel();
        SimpleWizardPanel hopwp = new SimpleWizardPanel(hop);
        hop.setName(loc("ExportOptions")); // NOI18N
        hopwp.setContentHighlightedIndex(0);
        
        SaveFilePanel chooseFilePanel = new SaveFilePanel();
        SimpleWizardPanel chooseFileWP = new SimpleWizardPanel(chooseFilePanel);
        chooseFilePanel.setWizardPanel(chooseFileWP);
        chooseFilePanel.getFileChooser().addChoosableFileFilter(
            new ExtensionFileFilter(loc("HtmlFilter"), // NOI18N
                new String[] {".html"})); // NOI18N
        chooseFilePanel.setFile(new File(
                Settings.getDefault().getLastUsedExportFolder(),
                "tasklist.html")); // NOI18N
        chooseFilePanel.setOpenFileCheckBoxVisible(true);
        chooseFileWP.setContentHighlightedIndex(1);

        // create wizard descriptor
        WizardDescriptor.Iterator iterator = 
            new WizardDescriptor.ArrayIterator(
                new WizardDescriptor.Panel[] {hopwp, chooseFileWP});
        WizardDescriptor wd = new WizardDescriptor(iterator);
        wd.putProperty("WizardPanel_contentData", // NOI18N
            new String[] {
                hop.getName(),
                chooseFilePanel.getName()
            }
        ); 
        wd.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        wd.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        wd.putProperty("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        wd.setTitle(loc("ExportText")); // NOI18N
        wd.putProperty("chooseFilePanel", chooseFilePanel); // NOI18N
        wd.putProperty("historyOptionsPanel", hop); // NOI18N
        wd.setTitleFormat(new java.text.MessageFormat("{0}")); // NOI18N
        
        return wd;
    }

    public void doExportImport(ExportImportProvider provider, 
            WizardDescriptor wd) {
        HistoryOptionsPanel hop = (HistoryOptionsPanel) 
                wd.getProperty("historyOptionsPanel"); // NOI18N
        SaveFilePanel panel = (SaveFilePanel) 
                wd.getProperty("chooseFilePanel"); // NOI18N
        
        Date from = hop.getFrom();
        Date to = hop.getTo();
        if (from.compareTo(to) > 0) {
            Date tmp = from;
            from = to;
            to = tmp;
        }
        
        Calendar c = Calendar.getInstance();
        c.setTime(to);
        c.add(Calendar.DAY_OF_YEAR, 1);
        HistoryOptionsPanel.Group g = hop.getGroup();
        final int minDur = hop.getMinimumDuration();
        
        SpentTimesUserTaskProcessor p = 
                new SpentTimesUserTaskProcessor(from, 
                c.getTime(), g, minDur);
        
        UserTaskInfo info = createInfos();
        TreeAbstraction<UserTaskInfo> tree = info.createTreeInterface();
        UTUtils.processDepthFirst(tree, p);
        UTUtils.processDepthFirst(tree, new UnaryFunction() {
            public Object compute(Object obj) {
                UserTaskInfo info = (UserTaskInfo) obj;
                
                int i = 0;
                while (i < info.children.size()) {
                    UserTaskInfo ch = info.children.get(i);
                    if (UTUtils.sum(ch.spentTimes) < minDur * 60 * 1000)
                        info.children.remove(i);
                    else
                        i++;
                }
                
                return null;
            }
        });
        
        // Here are some i18n problems, but it is probably not necessary
        // to localize these strings.
        String fs;
        switch (g) {
            case DAILY:
                fs = "{0,date,short}"; // NOI18N
                break;
            case WEEKLY:
                fs = "{0,date,short} - {1,date,short} ({0,date,w})"; // NOI18N
                break;
            case MONTHLY:
                fs = "{0,date,MMM yyyy}"; // NOI18N
                break;
            case QUARTERLY:
                fs = "{0,date,MMM} - {1,date,MMM yyyy}"; // NOI18N
                break;
            case YEARLY:
                fs = "{0,date,yyyy}"; // NOI18N
                break;
            default:
                fs = "{0} - {1}"; // NOI18N
        }
        MessageFormat mf = new MessageFormat(fs);
        
        Transformer tr;
        try {
            tr = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            UTUtils.LOGGER.log(Level.SEVERE, "", ex); // NOI18N
            return;
        } catch (TransformerFactoryConfigurationError ex) {
            UTUtils.LOGGER.log(Level.SEVERE, "", ex); // NOI18N
            return;
        }
        
        tr.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, 
                "-//W3C//DTD XHTML 1.0 Strict//EN"); // NOI18N
        tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, 
                "http://www.w3.org/TR/xhtml1" + // NOI18N
                "/DTD/xhtml1-strict.dtd"); // NOI18N
        tr.setOutputProperty(OutputKeys.METHOD, "xml"); // NOI18N
        tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); // NOI18N

        Document html;
        try {
            html = export(info, p.getPeriods(), mf);
        } catch (ParserConfigurationException ex) {
            UTUtils.LOGGER.log(Level.SEVERE, "", ex); // NOI18N
            return;
        }
        
        DOMSource source = new DOMSource(html);
        StreamResult result = new StreamResult(panel.getFile());
        try {
            tr.transform(source, result);
            Settings.getDefault().setLastUsedExportFolder(
                    panel.getFile().getParentFile());
        } catch (TransformerException ex) {
            String msg = NbBundle.getMessage(HistoryTextExportFormat.class, 
                    "CannotWriteFile",  // NOI18N
                    panel.getFile().getAbsolutePath(), ex.getMessage());
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
        if (panel.getOpenExportedFile())
            openFileInBrowser(panel.getFile());
    }

    /**
     * Creates an UTInfo for the current task list.
     *
     * @return UTInfo
     */
    private UserTaskInfo createInfos() {
        return createInfo(UserTaskViewRegistry.getInstance().getCurrent().
                getUserTaskList());
    }
    
    /**
     * Creates UTInfo for a user task list.
     *
     * @param utl a user task list
     * @return UTInfo
     */
    private UserTaskInfo createInfo(UserTaskList utl) {
        UserTaskInfo res = new UserTaskInfo();
        res.object = utl;
        addChildren(res, utl.getSubtasks());
        return res;
    }
    
    /**
     * Adds UTInfos for all subtasks of info.
     *
     * @param info UTInfo for a task
     * @param list list of user tasks
     */
    private void addChildren(UserTaskInfo info, UserTaskObjectList list) {
        for (int i = 0; i < list.size(); i++) {
            UserTaskInfo c = new UserTaskInfo();
            c.object = list.get(i);
            info.children.add(c);
            addChildren(c, list.get(i).getSubtasks());
        }
    }
    
    /**
     * Opens the specified file in the IDE.
     *
     * @param file file to be opened
     */
    private static void openFileInBrowser(File file) {
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            // Can't show URL
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            return;
        }

        HtmlBrowser.URLDisplayer.getDefault().showURL(url);
    }
    
    /**
     * Exports the tasks.
     *
     * @param info an information node
     * @param periods time periods
     * @param format format for periods {0} and {1} as java.util.Date
     * @return HTML
     */
    private Document export(UserTaskInfo info, List<Date> periods, 
            MessageFormat format) throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().
                newDocumentBuilder().newDocument();
        
        Element html = doc.createElement("html"); // NOI18N
        doc.appendChild(html);
        Element head = UTUtils.appendElement(html, "head"); // NOI18N
        Element meta = UTUtils.appendElement(head, "meta"); // NOI18N
        meta.setAttribute("http-equiv", "Content-Type"); // NOI18N
        meta.setAttribute("content", "text/html; charset=UTF-8"); // NOI18N
        UTUtils.appendElement(head, "title", loc("SpentTimes")); // NOI18N
        createStyle(head);
        Element body = UTUtils.appendElement(html, "body"); // NOI18N
        body.setAttribute("style", "font-family: sans-serif"); // NOI18N
        UTUtils.appendElement(body,"h1", loc("SpentTimes")); // NOI18N
                
        DateFormat datef = DateFormat.getDateInstance(DateFormat.SHORT);

        Element table = UTUtils.appendElement(body, "table"); // NOI18N
        table.setAttribute("border", "0"); // NOI18N
        table.setAttribute("cellspacing", "1");
        table.setAttribute("cellpadding", "0"); // NOI18N
        Element tbody = UTUtils.appendElement(table, "tbody"); // NOI18N

        Element header = UTUtils.appendElement(tbody, "tr"); // NOI18N
        header.setAttribute("class", "header"); // NOI18N
        Element td = UTUtils.appendElement(header, "td", // NOI18N
                loc("Summary")); // NOI18N
        td.setAttribute("class", "summary"); // NOI18N
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < periods.size() - 1; i++) {
            c.setTime(periods.get(i + 1));
            c.add(Calendar.SECOND, -1);
            Element td2 = UTUtils.appendElement(header,  "td", // NOI18N
                    format.format(new Object[] { // NOI18N
                    periods.get(i), c.getTime()}, new StringBuffer(), 
                    null).toString());
            td2.setAttribute("class", "data"); // NOI18N
        }

        int[] row = new int[1];
        for (int i = 0; i < info.children.size(); i++) {
            createTableRow(info.children.get(i), tbody, row, 0);
        }

        UTUtils.appendElement(body, "hr"); // NOI18N
        createFooter(body);
        return doc;
    }

    /**
     * Creates a table row for a UserTaskInfo.
     *
     * @return created tag
     * @param tbody tbody tag
     * @param rowNumber row number [1]
     * @param level nesting level
     */
    private void createTableRow(UserTaskInfo info, Element tbody, int[] rowNumber,
            int level) {
        Element row = UTUtils.appendElement(tbody, "tr"); // NOI18N
        if (rowNumber[0] % 2 == 0)
            row.setAttribute("class", "even"); // NOI18N
        Element td = UTUtils.appendElement(row, "td"); // NOI18N
        td.setAttribute("class", "summary"); // NOI18N
        if (info.object instanceof UserTask) {
            fillDataCell(td, ((UserTask) info.object).getSummary(), level);
        } else if (info.object instanceof UserTaskList) {
            /* TODO fillDataCell(td, FileUtil.getFileDisplayName(
                    ((UserTaskList) info.object).getFile()),
                    level);*/
        } else {
            fillDataCell(td, loc("AllTaskLists"), level); // NOI18N
        }
        for (int j = 0; j < info.spentTimes.length; j++) {
            Duration d = new Duration((int) (info.spentTimes[j] / (1000 * 60)), 
                    24, 5);
            Element td2 = UTUtils.appendElement(row, "td", // NOI18N
                    DURATION_FORMAT.format(d));
            td2.setAttribute("class", "data"); // NOI18N
        }
        rowNumber[0]++;
        
        for (int i = 0; i < info.children.size(); i++) {
            createTableRow(info.children.get(i), tbody, rowNumber, level + 1);
        }
    }
    
    /**
     * Creates the footer.
     *
     * @param parent parent node
     * @return footer
     */
    private static Element createFooter(Element parent) {
        // small i18n problems here
        Element p = UTUtils.appendElement(parent, "p"); // NOI18N
        Element a = UTUtils.appendElement(p, "a", loc("ValidXHTML"));
        a.setAttribute("href", // NOI18N
                "http://validator.w3.org/check/referer"); // NOI18N
        UTUtils.appendText(p, loc("CreatedBy") + " "); // NOI18N
        UTUtils.appendElement(p, "a", "NetBeans"). // NOI18N
                setAttribute("href", "http://www.netbeans.org"); // NOI18N
        
        // &nbsp;
        UTUtils.appendText(p, "\u00a0"); // NOI18N
        
        UTUtils.appendElement(p, "a", "User Tasks Module"). // NOI18N
                setAttribute("href", // NOI18N 
                "http://tasklist.netbeans.org"); // NOI18N
        return p;
    }
    
    /**
     * Creates <style> tag.
     *
     * @param parent parent tag
     * @return created tag
     */
    private static Element createStyle(Element parent) {
        Element style = UTUtils.appendElement(parent, "style"); // NOI18N
        style.setAttribute("type", "text/css"); // NOI18N
        UTUtils.appendText(style, 
                "tr.header { background-color: #222288; " + // NOI18N
                "color: white; font-weight: bold }\n"); // NOI18N
        UTUtils.appendText(style, 
                "tr.even { background-color: #eeeeee }\n"); // NOI18N
        UTUtils.appendText(style,
                "td.summary { text-align: left; width: 400px; nowrap; }\n"); // NOI18N
        UTUtils.appendText(style,
                "td.data { text-align: center }"); // NOI18N
        return style;
    }
    
    /**
     * Fills text about a UserTaskInfo.
     *
     * @param td table cell
     * @param v task description
     * @param level nesting level
     */
    private static void fillDataCell(Element td, String v, int level) {
        Element table = UTUtils.appendElement(td, "table");
        table.setAttribute("style", "width: 100%"); // NOI18N
        Element tbody = UTUtils.appendElement(table, "tbody");
        Element tr = UTUtils.appendElement(tbody, "tr"); // NOI18N
        Element td_ = UTUtils.appendElement(tr, "td");
        td_.setAttribute("style", "width: " + (level * 30 + 1) + "px"); // NOI18N
        Element td2 = UTUtils.appendElement(tr, "td"); // NOI18N
        UTUtils.appendText(td2, "\u2022"); // NOI18N
        Element span = UTUtils.appendElement(td2, "span", v); // NOI18N
        span.setAttribute("title", v); // NOI18N
    }
}

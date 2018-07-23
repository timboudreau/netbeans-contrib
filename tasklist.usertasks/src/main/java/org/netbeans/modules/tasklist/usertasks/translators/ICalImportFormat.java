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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.swing.filechooser.FileSystemView;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CategoryList;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.netbeans.modules.tasklist.export.ExportImportFormat;
import org.netbeans.modules.tasklist.export.ExportImportProvider;
import org.netbeans.modules.tasklist.export.OpenFilePanel;
import org.netbeans.modules.tasklist.export.SimpleWizardPanel;


import org.netbeans.modules.tasklist.usertasks.util.TreeAbstraction;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.Dependency;
import org.netbeans.modules.tasklist.usertasks.model.LineResource;
import org.netbeans.modules.tasklist.usertasks.model.URLResource;
import org.netbeans.modules.tasklist.usertasks.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * This class provides import/export capabilities for the iCalendar calendar
 * format (used by for example KDE's Konqueror calendar/todoitem tool)
 * as specified in RFC 2445 with the following exceptions:
 *
 * @todo Store the alarm-part of the associated time as an VALARM field (but
 *       I guess I must hardcode some of the fields (the alarm action etc);)
 *
 * @todo Trond: I have left traces after a class named AssociatedTime in this
 *       file. I might need some of it again when we decide we want to
 *       event support.
 *
 * The iCalendar supports other "tags" for a VTODO item than we do. In
 * order to avoid loosing such information, these unknown tags are stored
 * inside the UserTaskList object.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public class ICalImportFormat implements ExportImportFormat {
    protected final static String 
        CHOOSE_FILE_PANEL_PROP = "ChooseFilePanel"; // NOI18N
    
    private static final String DATEFORMATZ = "yyyyMMdd'T'HHmmss'Z'"; // NOI18N
    private static final SimpleDateFormat formatter = 
            new SimpleDateFormat(DATEFORMATZ);
    
    /** Used to read in dependencies */
    private static class Dep {
        /** Dependency type. */
        public int type;
        
        /** this task depends on another one */
        public UserTask ut;
        
        /** ut depends on the task with this UID */
        public String dependsOn;
    }
    
    /**
     * Converts a stream to RFC 2445 line endings.
     *
     * @param r default system line endings
     * @param w \r\n terminated strings
     */
    private static void convertToRFC2445(Reader r, Writer w) throws
    IOException {
        BufferedReader br = new BufferedReader(r);
        String line;
        while ((line = br.readLine()) != null) {
            w.write(line);
            w.write("\r\n");
        }
    }
    
    /**
     * Reads an .ics file from the specified stream.
     *
     * @param utl a task list
     * @param is .ics
     */
    public static void read(final UserTaskList utl, InputStream is) throws 
        IOException, ParserException {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_PARSING, true);
        CalendarBuilder cb = new MyCalendarBuilder();
        
        // <Dep> used for reading dependencies
        List<Dep> dependencies = new ArrayList<Dep>();
    
        Reader r = new InputStreamReader(is, "UTF-8");
        StringWriter w = new StringWriter();
        convertToRFC2445(r, w);
        r = new StringReader(w.getBuffer().toString());
        
        Calendar cal = cb.build(r);
        for (Iterator i = cal.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            if (component.getName().equals(Component.VTODO)) {
                readVTODO(utl, component, dependencies);
            }
        }

        // Dependencies
        for (int i = 0; i < dependencies.size(); i++) {
            Dep d = (Dep) dependencies.get(i);
            UserTask ut = utl.findItem(
                utl.getSubtasks().iterator(), d.dependsOn);
            if (ut != null) {
                d.ut.getDependencies().add(new Dependency(ut, d.type));
            }
        }

        dependencies.clear();

        TreeAbstraction tree = new TreeAbstraction() {
            public Object getChild(Object obj, int index) {
                if (obj instanceof UserTaskList) {
                    return ((UserTaskList) obj).getSubtasks().get(index);
                } else {
                    return ((UserTask) obj).getSubtasks().get(index);
                }
            }
            public int getChildCount(Object obj) {
                if (obj instanceof UserTaskList) {
                    return ((UserTaskList) obj).getSubtasks().size();
                } else {
                    return ((UserTask) obj).getSubtasks().size();
                }
            }
            public Object getRoot() {
                return utl;
            }
        };
        UTUtils.processDepthFirst(tree, new UnaryFunction() {
            public Object compute(Object obj) {
                if (obj instanceof UserTask) {
                    ((UserTask) obj).setUpdateLastModified(true);
                }
                return null;
            }
        });
        utl.userObject = cal;
    }
    
    public void doExportImport(ExportImportProvider provider, WizardDescriptor wd) {
        OpenFilePanel panel = 
            (OpenFilePanel) wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        File p = panel.getFile();
        if (p == null || !p.exists()) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                NbBundle.getMessage(ICalImportFormat.class, 
                    "FileDoesNotExist"), // NOI18N
                    NotifyDescriptor.ERROR_MESSAGE));
            return;
        }
        
        UserTaskView view = (UserTaskView) provider;
        UserTaskList utl = (UserTaskList) view.getUserTaskList();
        
        InputStream is;
        try {
            is = new FileInputStream(panel.getFile());
        } catch (IOException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            return;
        }
        
        CalendarBuilder cb = new MyCalendarBuilder();
        
        try {
            read(utl, is);
        } catch (ParserException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            return;
        } catch (IOException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            return;
        }
    }

    /**
     * Reads one VTODO.
     *
     * @param utl a user task list
     * @param cmp VTODO
     * @param dependencies <Dep> container for dependencies
     */
    private static void readVTODO(UserTaskList utl, Component cmp,
        List<Dep> dependencies) {
        PropertyList pl = cmp.getProperties();
        Property prop = pl.getProperty(Property.SUMMARY);
        String summary = (prop == null) ? "" : prop.getValue(); // NOI18N
        UserTask ut = new UserTask(summary, utl);
        ut.setUpdateLastModified(false);
        
        prop = pl.getProperty(Property.CREATED);
        if (prop != null)
            ut.setCreatedDate(((DateProperty) prop).getDate().getTime());
            
        prop = pl.getProperty(Property.UID);
        if (prop != null)
            ut.setUID(prop.getValue());
        
        prop = pl.getProperty(Property.DTSTART);
        if (prop != null)
            ut.setStartDate(((DateProperty) prop).getDate());
        
        prop = pl.getProperty(Property.PERCENT_COMPLETE);
        if (prop != null)
            ut.setPercentComplete(((PercentComplete) prop).getPercentage());
        
        prop = pl.getProperty("X-NETBEANS-OWNER"); // NOI18N
        if (prop != null)
            ut.setOwner(prop.getValue());
        
        prop = pl.getProperty(Property.PRIORITY);
        if (prop != null) {
            int level = ((Priority) prop).getLevel();
            if (level < 0) 
                level = UserTask.MEDIUM; // An error.
            else if (level == 0)
                level = UserTask.MEDIUM;
            else if (level > UserTask.LOW)
                level = UserTask.LOW;
            ut.setPriority(level);
        }
        
        prop = pl.getProperty("X-NETBEANS-EFFORT"); // NOI18N
        if (prop != null) {
            try {
                ut.setEffort(Integer.parseInt(prop.getValue()));
            } catch (NumberFormatException e) {
                UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            }
        }
        
        prop = pl.getProperty("X-NETBEANS-SPENT-TIME"); // NOI18N
        if (prop != null) {
            try {
                ut.setSpentTime(Integer.parseInt(prop.getValue()));
            } catch (NumberFormatException e) {
                UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            }
        }
        
        prop = pl.getProperty(Property.CATEGORIES);
        if (prop != null) {
            CategoryList cl = ((Categories) prop).getCategories();
            Iterator it = cl.iterator();
            StringBuilder category = new StringBuilder(ut.getCategory());
            while (it.hasNext()) {
                if (category.length() > 0)
                    category.append(", "); // NOI18N
                category.append((String) it.next());
            }
            ut.setCategory(category.toString());
        }
        
        prop = pl.getProperty(Property.DESCRIPTION);
        if (prop != null)
            ut.setDetails(prop.getValue());
        
        String related = null;
        prop = pl.getProperty(Property.RELATED_TO);
        if (prop != null)
            related = prop.getValue();
        
        PropertyList deps = pl.getProperties("X-NETBEANS-DEPENDENCY"); // NOI18N
        for (int i = 0; i < deps.size(); i++) {
            prop = (Property) deps.get(i);
            Dep d = new Dep();
            d.type = Dependency.END_BEGIN;
            d.ut = ut;
            d.dependsOn = prop.getValue();
            ParameterList parl = prop.getParameters();
            Parameter p = parl.getParameter("X-NETBEANS-TYPE");
            if (p != null) {
                String t = p.getValue();
                if (t.equals("BEGIN_BEGIN")) // NOI18N
                    d.type = Dependency.BEGIN_BEGIN;
            }
            dependencies.add(d);
        }
        
        PropertyList wps = pl.getProperties("X-NETBEANS-WORK-PERIOD"); // NOI18N
        for (int i = 0; i < wps.size(); i++) {
            prop = (Property) wps.get(i);
            ParameterList parl = prop.getParameters();
            Parameter p = parl.getParameter("X-NETBEANS-START");
            if (p != null) {
                try {
                    int dur = Integer.parseInt(prop.getValue());
                    String v = p.getValue();
                    long start = formatter.parse(v).getTime();
                    UserTask.WorkPeriod wp = new UserTask.WorkPeriod(start, dur);
                    ut.getWorkPeriods().add(wp);
                } catch (ParseException e) {
                    UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
                } catch (NumberFormatException e) {
                    UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
                }
            }
        }
        
        if (pl.getProperty("X-NETBEANS-VALUES-COMPUTED") == null &&
                pl.getProperty("X-NETBEANS-PROGRESS-COMPUTED") !=   // NOI18N
                null &&
                pl.getProperty("X-NETBEANS-PROGRESS-COMPUTED"). // NOI18N
                getValue().equals("yes") &&  // NOI18N
                pl.getProperty("X-NETBEANS-EFFORT-COMPUTED") !=  // NOI18N
                null &&
                pl.getProperty("X-NETBEANS-EFFORT-COMPUTED"). // NOI18N
                getValue().equals("yes") &&  // NOI18N
                pl.getProperty("X-NETBEANS-SPENT-TIME-COMPUTED") !=  // NOI18N
                null &&
                pl.getProperty("X-NETBEANS-SPENT-TIME-COMPUTED"). // NOI18N
                getValue().equals("yes")  // NOI18N
                ) {
            ut.setValuesComputed(true);
        } else {
            prop = pl.getProperty("X-NETBEANS-VALUES-COMPUTED"); // NOI18N
            if (prop != null)
                ut.setValuesComputed(prop.getValue().equals("yes")); // NOI18N
            else
                ut.setValuesComputed(false);
        }
//            } else if ("X-NETBEANS-STARTTIME".equals(name)) { // NOI18N  
//                long start = Long.MAX_VALUE;
//                try {
//                    start = Long.parseLong(value);
//                } catch (NumberFormatException e) {
//                    ErrorManager.getDefault().notify(e);
//                }
//
//                if (start != Long.MAX_VALUE) {
//                    if (associatedTime == null) {
//                        associatedTime = new AssociatedTime();
//                    }
//
//                    associatedTime.setStartTime(new java.util.Date(start));
//                }
//            } else if ("X-NETBEANS-ENDTIME".equals(name)) { // NOI18N
//                long end = Long.MAX_VALUE;
//                try {
//                    end = Long.parseLong(value);
//                } catch (NumberFormatException e) {
//                    ErrorManager.getDefault().notify(e);
//                }
//                if (end != Long.MAX_VALUE) {
//                    if (associatedTime == null) {
//                        associatedTime = new AssociatedTime();
//                    }
//                    
//                    associatedTime.setEndTime(new java.util.Date(end));
//                }
        
        prop = pl.getProperty("X-NETBEANS-DUETIME"); // NOI18N
        if (prop != null) {
            java.util.Date d = null;
            try {
                d = new java.util.Date(Long.parseLong(prop.getValue()));
                ut.setDueDate(d);
            } catch (NumberFormatException e) {
                UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
            }
        } 
        
        prop = pl.getProperty(Property.DUE);
        if (prop != null) {
            ut.setDueDate(((DateProperty) prop).getDate());
        } 
        
        prop = pl.getProperty("X-NETBEANS-DUE-SIGNALED"); // NOI18N
        if (prop != null) 
            ut.setDueAlarmSent(true);
            
//            } else if ("X-NETBEANS-DUERECURRENT-INTERVAL".equals(name)) { // NOI18N
//                int interval = 0;
//                try {
//                    interval = Integer.parseInt(value);
//                } catch (NumberFormatException e) {
//                    ErrorManager.getDefault().notify(e);
//                }
//                
//                if (associatedTime == null) {
//                    associatedTime = new AssociatedTime();
//                }
//                
//                associatedTime.setInterval(interval);
//            } else if ("X-NETBEANS-DUERECURRENT-MEASUREMENT".equals(name)) { // NOI18N
//                int measurement = AssociatedTime.DAY;
//                
//                if ("DAY".equals(value)) { // NOI18N
//                    measurement = AssociatedTime.DAY;
//                } else if ("WEEK".equals(value)) { // NOI18N
//                    measurement = AssociatedTime.WEEK;
//                } else if ("MONTH".equals(value)) { // NOI18N
//                    measurement = AssociatedTime.MONTH;
//                } else if ("YEAR".equals(value)) { // NOI18N
//                    measurement = AssociatedTime.YEAR;   
//                } 
//
//                if (associatedTime == null) {
//                    associatedTime = new AssociatedTime();
//                }
//                associatedTime.setMeasurement(measurement);
        
        prop = pl.getProperty(Property.COMPLETED);
        if (prop != null)
            ut.setCompletedDate(((DateProperty) prop).getDate().getTime());
        
        readResources(ut, pl);        
        
//        if (associatedTime != null) {
//            task.setAssociatedTime(associatedTime);
//        }
        
        UserTask alreadyExists = utl.findItem(
            utl.getSubtasks().iterator(), ut.getUID());
        UserTask parent = null;
        if (alreadyExists != null) {
            // I should replace alreadyexists with task...
            parent = alreadyExists.getParent();
            if (parent != null) {
                parent.getSubtasks().remove(alreadyExists);
            } else {
                utl.getSubtasks().remove(alreadyExists);
            }
            
            Iterator li = alreadyExists.getSubtasks().iterator();
            while (li.hasNext()) {
                UserTask c = (UserTask)li.next();
                alreadyExists.getSubtasks().remove(c);
                ut.getSubtasks().add(c);
            }
        } else if (related != null) {
            // the parent setting !!
            parent = utl.findItem(utl.getSubtasks().iterator(), related);
        }
            
        prop = pl.getProperty(Property.LAST_MODIFIED);
        if (prop != null)
            ut.setLastEditedDate(((DateProperty) prop).getDate().getTime());
        
        if (parent != null)
            parent.getSubtasks().add(ut);
        else
            utl.getSubtasks().add(ut);
    }
    
    public String getName() {
        return NbBundle.getMessage(ICalImportFormat.class, "iCalImp"); // NOI18N
    }
    
    public org.openide.WizardDescriptor getWizard() {
        OpenFilePanel chooseFilePanel = new OpenFilePanel();
        SimpleWizardPanel chooseFileWP = new SimpleWizardPanel(chooseFilePanel);
        chooseFilePanel.setWizardPanel(chooseFileWP);
        chooseFilePanel.getFileChooser().addChoosableFileFilter(
            new ExtensionFileFilter(
                NbBundle.getMessage(XmlExportFormat.class, 
                    "IcsFilter"), // NOI18N
                new String[] {".ics"})); // NOI18N
        chooseFilePanel.setFile(new File(
            FileSystemView.getFileSystemView().
            getDefaultDirectory(), "tasklist.ics")); // NOI18N
        
        // create the wizard
        WizardDescriptor.Iterator iterator = 
            new WizardDescriptor.ArrayIterator(new WizardDescriptor.Panel[] {
                chooseFileWP
        });
        WizardDescriptor d = new WizardDescriptor(iterator);
        d.putProperty("WizardPanel_contentData", // NOI18N
            new String[] {
                NbBundle.getMessage(
                    ICalImportFormat.class, "ChooseSource"), // NOI18N
            }
        );
        
        String title;
        title = NbBundle.getMessage(ICalImportFormat.class, "ImportICAL"); // NOI18N
        d.setTitle(title); // NOI18N
        d.putProperty(CHOOSE_FILE_PANEL_PROP, chooseFilePanel);
        d.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        d.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        d.putProperty("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        d.setTitleFormat(new java.text.MessageFormat("{0}")); // NOI18N 
        return d;
    }
    
    /**
     * Reads references to associated resources.
     * 
     * @param ut user task
     * @param pl list of properties 
     */
    private static void readResources(UserTask ut, PropertyList pl) {
        PropertyList ress = pl.getProperties("X-NETBEANS-RESOURCE"); // NOI18N
        if (ress.size() > 0) {
            for (int i = 0; i < ress.size(); i++) {
                Property prop = (Property) ress.get(i);
                ParameterList parl = prop.getParameters();
                Parameter type = parl.getParameter(
                        "X-NETBEANS-RESOURCE-TYPE"); // NOI18N
                if (type != null) {
                    if ("url".equals(type.getValue())) { // NOI18N
                        try {
                            String urlText = parl.getParameter(
                                    "X-NETBEANS-URL").getValue(); // NOI18N
                            URL rurl = new URL(urlText);
                            ut.getResources().add(new URLResource(rurl));
                        } catch (MalformedURLException ex) {
                            UTUtils.LOGGER.warning(ex.getMessage());
                        }
                    } else if ("line".equals(type.getValue())) { // NOI18N
                        try {
                            URL url = new URL(parl.getParameter(
                                    "X-NETBEANS-URL").getValue()); // NOI18N
                            int line;
                            try {
                                line = Integer.parseInt(parl.getParameter(
                                        "X-NETBEANS-LINE").getValue()); // NOI18N
                            } catch (java.lang.NumberFormatException e) {
                                UTUtils.LOGGER.warning(e.getMessage());
                                line = 0;
                            }
                            ut.getResources().add(new LineResource(url, line));
                        } catch (MalformedURLException ex) {
                            UTUtils.LOGGER.warning(ex.getMessage());
                        }
                    } else {
                        // ignore
                    }
                }
            }
        } else {
            String filename = null;
            Property prop = pl.getProperty("X-NETBEANS-FILENAME"); // NOI18N
            if (prop != null)
                filename = prop.getValue();

            String lineNumber = null;
            prop = pl.getProperty("X-NETBEANS-LINE"); // NOI18N
            if (prop != null)
                lineNumber = prop.getValue();

            String url = null;
            prop = pl.getProperty(Property.URL);
            if (prop != null)
                url = prop.getValue();

            int lineno = 1;
            if (lineNumber != null) {
                try {
                    lineno = Integer.parseInt(lineNumber);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            if (lineno < 1)
                lineno = 1;

            FileObject fo = null;
            if (url != null) {
                try {
                    fo = URLMapper.findFileObject(new URL(url));
                } catch (MalformedURLException e) {
                    // ignore
                }
            }

            if (fo == null && filename != null) {
                fo = UTUtils.getFileObjectForFile(filename);
            }
            if (fo != null) {
                Line line = UTUtils.getLineByFile(fo, lineno - 1);
                if (line == null)
                    line = UTUtils.getLineByFile(fo, 0);

                if (line != null) {
                    ut.getResources().add(new LineResource(line));
                }
            } else if (url != null) {
                try {
                    ut.getResources().add(new LineResource(new URL(url), 
                            lineno - 1));
                } catch (MalformedURLException e) {
                    // ignore
                }
            }
        }
    }
}

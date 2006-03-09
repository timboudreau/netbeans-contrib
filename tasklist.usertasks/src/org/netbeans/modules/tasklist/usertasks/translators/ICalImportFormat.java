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

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import javax.swing.filechooser.FileSystemView;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CategoryList;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;

import org.netbeans.modules.tasklist.core.export.ExportImportFormat;
import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.export.OpenFilePanel;
import org.netbeans.modules.tasklist.core.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.core.util.SimpleWizardPanel;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.Dependency;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.text.Line;
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
 * The iCalendar supports other "tags" for a VTODO item than Tasklist. In
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
    private static final SimpleDateFormat formatter = new SimpleDateFormat(DATEFORMATZ);
    
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
     * Constructor
     */
    public ICalImportFormat() {
    }
    
    /**
     * Reads an .ics file from the specified stream.
     *
     * @param utl a task list
     * @param is .ics
     */
    public static void read(UserTaskList utl, InputStream is) throws 
        IOException, ParserException {
        CalendarBuilder cb = new MyCalendarBuilder();
        
        // <Dep> used for reading dependencies
        List dependencies = new ArrayList();
    
        UTUtils.LOGGER.fine("building calendar"); // NOI18N
        
        Reader r = new InputStreamReader(is, "UTF-8");
        StringWriter w = new StringWriter();
        convertToRFC2445(r, w);
        r = new StringReader(w.getBuffer().toString());
        
        Calendar cal = cb.build(r);
        for (Iterator i = cal.getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            // UTUtils.LOGGER.fine("component.name = " + component.getName()); // NOI18N
            if (component.getName().equals(Component.VTODO)) {
                readVTODO(utl, component, dependencies);
            }
        }

        // Dependencies
        UTUtils.LOGGER.finer("processing dependencies: " + dependencies.size()); // NOI18N
        for (int i = 0; i < dependencies.size(); i++) {
            Dep d = (Dep) dependencies.get(i);
            UserTask ut = utl.findItem(
                utl.getSubtasks().iterator(), d.dependsOn);
            UTUtils.LOGGER.finer("found task " + ut); // NOI18N
            if (ut != null) {
                d.ut.getDependencies().add(new Dependency(ut, d.type));
            }
        }

        dependencies.clear();

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
            ErrorManager.getDefault().notify(e);
            return;
        }
        
        System.setProperty("ical4j.unfolding.relaxed", "true"); // NOI18N
        CalendarBuilder cb = new MyCalendarBuilder();
        
        try {
            read(utl, is);
        } catch (ParserException e) {
            ErrorManager.getDefault().notify(e);
            return;
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
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
        List dependencies) {
        PropertyList pl = cmp.getProperties();
        Property prop = pl.getProperty(Property.SUMMARY);
        String summary = (prop == null) ? "" : prop.getValue(); // NOI18N
        UserTask ut = new UserTask(summary, utl);
        
        prop = pl.getProperty(Property.CREATED);
        if (prop != null)
            ut.setCreatedDate(((DateProperty) prop).getDate().getTime());
            
        prop = pl.getProperty(Property.UID);
        if (prop != null)
            ut.setUID(prop.getValue());
        
        prop = pl.getProperty(Property.LAST_MODIFIED);
        if (prop != null)
            ut.setLastEditedDate(((DateProperty) prop).getDate().getTime());
        
        prop = pl.getProperty(Property.DTSTART);
        if (prop != null)
            ut.setStartDate(((DateProperty) prop).getDate());
        
        prop = pl.getProperty(Property.PERCENT_COMPLETE);
        if (prop != null)
            ut.setPercentComplete(((PercentComplete) prop).getPercentage());
        
        prop = pl.getProperty("X-NETBEANS-PROGRESS-COMPUTED"); // NOI18N
        if (prop != null)
            ut.setProgressComputed(prop.getValue().equals("yes"));
        
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
                ErrorManager.getDefault().notify(e);
            }
        }
        
        prop = pl.getProperty("X-NETBEANS-EFFORT-COMPUTED"); // NOI18N
        if (prop != null)
            ut.setEffortComputed(prop.getValue().equals("yes")); // NOI18N

        prop = pl.getProperty("X-NETBEANS-SPENT-TIME"); // NOI18N
        if (prop != null) {
            try {
                ut.setSpentTime(Integer.parseInt(prop.getValue()));
            } catch (NumberFormatException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        
        prop = pl.getProperty("X-NETBEANS-SPENT-TIME-COMPUTED"); // NOI18N
        if (prop != null)
            ut.setSpentTimeComputed(prop.getValue().equals("yes")); // NOI18N
        
        prop = pl.getProperty(Property.CATEGORIES);
        if (prop != null) {
            CategoryList cl = ((Categories) prop).getCategories();
            Iterator it = cl.iterator();
            StringBuffer category = new StringBuffer(ut.getCategory());
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
        
        String filename = null;
        prop = pl.getProperty("X-NETBEANS-FILENAME"); // NOI18N
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
                    long start = formatter.parse(p.getValue()).getTime();
                    UserTask.WorkPeriod wp = new UserTask.WorkPeriod(start, dur);
                    ut.getWorkPeriods().add(wp);
                } catch (ParseException e) {
                    ErrorManager.getDefault().notify(e);
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
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
            Date d = null;
            try {
                d = new Date(Long.parseLong(prop.getValue()));
                ut.setDueDate(d);
            } catch (NumberFormatException e) {
                ErrorManager.getDefault().notify(e);
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
                ut.setLine(line);
            }
        } else if (url != null) {
            try {
                UTUtils.LOGGER.fine("setting url to " + url + " for " + ut); // NOI18N
                ut.setUrl(new URL(url));
                ut.setLineNumber(lineno - 1);
            } catch (MalformedURLException e) {
                // ignore
            }
        }
        
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
}

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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RelatedTo;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.TimeZones;
import org.netbeans.modules.tasklist.export.ExportImportFormat;
import org.netbeans.modules.tasklist.export.ExportImportProvider;
import org.netbeans.modules.tasklist.export.SaveFilePanel;
import org.netbeans.modules.tasklist.export.SimpleWizardPanel;
import org.netbeans.modules.tasklist.usertasks.model.LineResource;


import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.model.Dependency;
import org.netbeans.modules.tasklist.usertasks.model.URLResource;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskResource;
import org.netbeans.modules.tasklist.usertasks.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.loaders.DataObject;
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
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public class ICalExportFormat implements ExportImportFormat {
    /**
     * Speed optimization.
     * See http://sourceforge.net/tracker/index.php?func=detail&aid=1722243&group_id=107024&atid=646395
     * for details. 
     */
    private static final String UTC_PATTERN = "yyyyMMdd'T'HHmmss'Z'";

    /**
     * Speed optimization.
     * See http://sourceforge.net/tracker/index.php?func=detail&aid=1722243&group_id=107024&atid=646395
     * for details. 
     *
     * Used for parsing times in a UTC date-time representation.
     */
    private static final DateFormat UTC_FORMAT = new SimpleDateFormat(
            UTC_PATTERN);
    
    /*
     * Speed optimization.
     * See http://sourceforge.net/tracker/index.php?func=detail&aid=1722243&group_id=107024&atid=646395
     * for details. 
     */
    static {
        UTC_FORMAT.setTimeZone(TimeZone.getTimeZone(TimeZones.UTC_ID));
        UTC_FORMAT.setLenient(false);
    }
   
    private final static String PRODID = 
        "-//NetBeans User Tasks//NONSGML 1.0//EN"; // NOI18N
    private final static String 
        CHOOSE_FILE_PANEL_PROP = "ChooseFilePanel"; // NOI18N
    
    // Format which includes the timezone at the end. This is the format
    // used by the tasklist's own written files for example.
    private static final String DATEFORMATZ = "yyyyMMdd'T'HHmmss'Z'"; // NOI18N
    private static final SimpleDateFormat DATEFORMAT = 
        new SimpleDateFormat(DATEFORMATZ);

    /**
     * Converts a stream to default system line endings.
     *
     * @param r \r\n terminated strings
     * @param w default system line endings
     */
    private static void convertToSystem(Reader r, Writer w) throws
    IOException {
        BufferedReader br = new BufferedReader(r);
        final String EOL = System.getProperty("line.separator"); // NOI18N
        String line;
        while ((line = br.readLine()) != null) {
            w.write(line);
            w.write(EOL);
        }
    }
    
    /**
     * Constructor
     */
    public ICalExportFormat() {
    }
    
    public void doExportImport(ExportImportProvider provider, WizardDescriptor wd) {
        SaveFilePanel panel = 
            (SaveFilePanel) wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        try {
            UserTaskList list = UserTaskViewRegistry.getInstance().
                    getCurrent().getUserTaskList();
            Writer w = new OutputStreamWriter(
                    new BufferedOutputStream(
                    new FileOutputStream(panel.getFile())), "UTF-8");
            try {
                writeList(list, w, true);
                Settings.getDefault().setLastUsedExportFolder(
                        panel.getFile().getParentFile());
            } finally {
                try {
                    w.close();
                } catch (IOException e) {
                    UTUtils.LOGGER.log(Level.WARNING, 
                            "Error closing file", e); // NOI18N
                }
            }
        } catch (ParseException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
        } catch (URISyntaxException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
        } catch (ValidationException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
        } catch (IOException e) {
            UTUtils.LOGGER.log(Level.SEVERE, "", e); // NOI18N
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ICalExportFormat.class, "iCalExp"); // NOI18N
    }
    
    // Extends AbstractTranslator
    
    public org.openide.WizardDescriptor getWizard() {
        SaveFilePanel chooseFilePanel = new SaveFilePanel();
        SimpleWizardPanel chooseFileWP = new SimpleWizardPanel(chooseFilePanel);
        chooseFilePanel.setWizardPanel(chooseFileWP);
        chooseFilePanel.getFileChooser().addChoosableFileFilter(
            new ExtensionFileFilter(
                NbBundle.getMessage(XmlExportFormat.class, 
                    "IcsFilter"), // NOI18N
                new String[] {".ics"})); // NOI18N
        chooseFilePanel.setFile(new File(
                Settings.getDefault().getLastUsedExportFolder(), 
                "tasklist.ics")); // NOI18N
        
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
        String title;
        title = NbBundle.getMessage(ICalExportFormat.class, "ExportICAL"); // NOI18N
        d.setTitle(title); // NOI18N
        d.putProperty(CHOOSE_FILE_PANEL_PROP, chooseFilePanel);
        d.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); // NOI18N
        d.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); // NOI18N
        d.putProperty("WizardPanel_contentNumbered", Boolean.TRUE); // NOI18N
        d.setTitleFormat(new java.text.MessageFormat("{0}")); // NOI18N todo
        return d;
    }

    /**
     * Retrieves calender from a task list.
     * 
     * @param list a task list
     * @return calendar for the task list 
     */
    private Calendar getCalendar(UserTaskList list) {
        Calendar cal = (Calendar) list.userObject;
        if (cal == null)
            cal = new Calendar();
        return cal;
    }
    
    /**
     * Do the actual export of the list into the stream
     *
     * @param list The tasklist to store
     * @param out The output stream object to use
     * @param std true = use RFC 2445 line endings, false = system default
     * line ending
     */
    public void writeList(UserTaskList list, Writer out, boolean std) 
    throws IOException, ValidationException, URISyntaxException, ParseException {
        Calendar cal = getCalendar(list);
        
        Property prop = getProperty(cal.getProperties(), Property.PRODID); 
        if (prop == null) {
            prop = new ProdId(PRODID);
            cal.getProperties().add(prop);
        } else {
            prop.setValue(PRODID);
        }

        prop = cal.getProperties().getProperty(Property.VERSION);
        if (prop != null)
            cal.getProperties().remove(prop);
        cal.getProperties().add(Version.VERSION_2_0);
        
        Iterator it = list.getSubtasks().iterator();
        Map<String, Integer> uid2index = createUId2IndexMap(cal);
        while (it.hasNext()) {
            UserTask item = (UserTask) it.next();
            writeTask(cal, item, uid2index);
        }
        
        final List<String> uids = new ArrayList<String>();
        UserTaskList.processDepthFirst(
            new UserTaskList.UserTaskProcessor() {
                public void process(UserTask ut) {
                    uids.add(ut.getUID());
                }
            }, list.getSubtasks()
        );
        removeUnusedVToDos(cal, uids);

        // an .ics file cannot be empty
        if (cal.getComponents().size() == 0) {
            VToDo td = new VToDo();
            td.getProperties().add(new Summary(
                    NbBundle.getMessage(ICalExportFormat.class, 
                    "Welcome"))); // NOI18N

            cal.getComponents().add(td);
        }

        CalendarOutputter co = new CalendarOutputter();
        co.setValidating(false);
        if (std) {
            co.output(cal, out);
        } else {
            StringWriter sw = new StringWriter();
            co.output(cal, sw);
            StringReader sr = new StringReader(sw.getBuffer().toString());
            convertToSystem(sr, out);
        }
    }
    
    /**
     * Creates a map from UID to index in the cal.getComponents() list.
     * 
     * @param cal a calendar
     * @return map from UID to index in cal.getComponents() 
     */
    private static Map<String, Integer> createUId2IndexMap(Calendar cal) {
        Map<String, Integer> r = new HashMap<String, Integer>();
        ComponentList cl = cal.getComponents();
        for (int i = 0; i < cl.size(); i++) {
            if (((Component) cl.get(i)).getName().equals(Component.VTODO)) {
                VToDo c = (VToDo) cl.get(i);
                PropertyList pl = c.getProperties();
                for (int j = 0; j < pl.size(); j++) {
                    Property p = (Property) pl.get(j);
                    if (p instanceof Uid) {
                        r.put(p.getValue(), i);
                    }
                }
            }
        }
        return r;
    }
    
    /**
     * Removes all VTODOs from the calendar if their IDs are not in the list.
     *
     * @param cal a calendar
     * @param uids &lt;String&gt; list of used uids.
     */
    private static void removeUnusedVToDos(Calendar cal, List uids) {
        ComponentList cl = cal.getComponents();
        Iterator it = cl.iterator();
        while (it.hasNext()) {
            Component c = (Component) it.next();
            if (c.getName().equals(Component.VTODO)) {
                Uid p = (Uid) getProperty(c.getProperties(), Property.UID);
                if (p == null || uids.indexOf(p.getValue()) < 0)
                    it.remove();
            }
        }
    }
    
    /**
     * Creates ical4j objects for a task and all of its subtasks.
     * 
     * @param cal calendar
     * @param task a task
     * @param uid2index UID -> index in cal.getComponents() 
     */
    private void writeTask(Calendar cal, UserTask task, 
            Map<String, Integer> uid2index) 
    throws IOException, URISyntaxException, ParseException, ValidationException {
        writeTask0(cal, task, uid2index);

        // Recurse over subtasks
        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask subtask = (UserTask)it.next();
            writeTask(cal, subtask, uid2index);
        }
    }
    
    /**
     * Write out the given todo item to the given writer.
     *
     * @param cal calendar object
     * @param task The task/todo item to use
     * @param uid2index UID -> index in cal.getComponents() 
     */
    @SuppressWarnings("unchecked")
    private void writeTask0(Calendar cal, UserTask task, 
            Map<String, Integer> uid2index) 
    throws IOException, URISyntaxException, ParseException, ValidationException {
        VToDo vtodo;
        Integer index = uid2index.get(task.getUID());
        if (index == null) {
            vtodo = new VToDo();
            vtodo.getProperties().add(new Uid(task.getUID()));
            cal.getComponents().add(vtodo);
        } else {
            vtodo = (VToDo) cal.getComponents().get(index);
        }

        PropertyList pl = vtodo.getProperties();
        Property prop = getProperty(pl, Property.CREATED);
        long created = task.getCreatedDate();
        setUtcProperty(pl, Property.CREATED, new java.util.Date(created));
        // DEBUG: prop.validate();
            
        // DTSTAMP
        setUtcProperty(pl, Property.DTSTAMP, new java.util.Date(created));
        
        prop = getProperty(pl, Property.DTSTART);
        if (task.getStart() != -1) {
            setUtcProperty(pl, Property.DTSTART, task.getStartDate());
        } else {
            if (prop != null)
                pl.remove(prop);
        }

        // summary: (Description)
        String desc = task.getSummary();
        prop = getProperty(pl, Property.SUMMARY);
        if (desc != null && desc.length() > 0) {
            if (prop == null) {
                prop = new Summary();
                pl.add(prop);
            }
            prop.setValue(desc);
        } else {
            if (prop != null)
                pl.remove(prop);
        }

        // description (details)
        String details = task.getDetails();
        prop = getProperty(pl, Property.DESCRIPTION);
        if (details != null && details.length() > 0) {
            if (prop == null) {
                prop = new Description();
                pl.add(prop);
            }
            prop.setValue(details);
        } else {
            if (prop != null)
                pl.remove(prop);
        }

        // Priority
        prop = getProperty(pl, Property.PRIORITY);
        if (prop != null)
            pl.remove(prop);
        if (task.getPriority() != UserTask.MEDIUM) {
            prop = new Priority(task.getPriority());
            pl.add(prop);
        }

        // Class -- not implemented (always PRIVATE, right?) Also allowed:
        // PRIVATE, CONFIDENTIAL
        /* Don't bother with this yet... waste of diskspace
           and parsing time -- only needed when we either export
           to XCS, or directly interoperate. There's too much
           missing yet to add partial support
        // For now, hardcode to private such that others don't get access
        writer.write("CLASS:PRIVATE\r\n"); // NOI18N
         */

        // attendee -- not implemented

        // Others not implemented:
        // geo, location, organizer, percent, recurid, seq, status,
        // due, duration (both cannot occur)

        // Optional ones not implemented:
        // attach, attendee, categories, comment, contact, exdate, exrule,
        // rstatus, related, resources, rdate, rrule, x-prop (actually,
        // xprop is special, we will have those)


        prop = getProperty(pl, Property.PERCENT_COMPLETE);
        if (prop == null) {
            prop = new PercentComplete();
            pl.add(prop);
        }
        ((PercentComplete) prop).setPercentage(task.getPercentComplete());

        setXProperty(pl, "X-NETBEANS-VALUES-COMPUTED", "yes",  // NOI18N
            task.isValuesComputed());
        
        
        // these 3 will be set for backwards compatibility only
        setXProperty(pl, "X-NETBEANS-PROGRESS-COMPUTED", "yes",  // NOI18N
            task.isValuesComputed());
        setXProperty(pl, "X-NETBEANS-EFFORT-COMPUTED", "yes",  // NOI18N
            task.isValuesComputed());
        setXProperty(pl, "X-NETBEANS-SPENT-TIME-COMPUTED", "yes",  // NOI18N
            task.isValuesComputed());
        
        setXProperty(pl, "X-NETBEANS-EFFORT",  // NOI18N
            Integer.toString(task.getEffort()), true);

        setXProperty(pl, "X-NETBEANS-SPENT-TIME",  // NOI18N
            Integer.toString(task.getSpentTime()), true);

        // Category (XXX standard allows MULTIPLE categories, I must handle
        // that when I parse back)
        String category = task.getCategory();
        prop = getProperty(pl, Property.CATEGORIES);
        if (category != null && category.length() > 0) {
            // TODO Write out multiple CATEGORIES lines instead
            // of a combined comma separated list which is what we're
            // doing here
            if (prop == null) {
                prop = new Categories();
                pl.add(prop);
            }
            ((Categories) prop).setValue(category); // NOI18N
        } else {
            if (prop != null)
                pl.remove(prop);
        }

        // Last modified
        // Last Edited Date, if different than created
        long edited = task.getLastEditedDate();
        prop = getProperty(pl, Property.LAST_MODIFIED);
        if (edited != created) {
            // They differ
            setUtcProperty(pl, Property.LAST_MODIFIED, new java.util.Date(edited));
        } else {
            if (prop != null)
                pl.remove(pl);
        }

        // completion date
        long completed = task.getCompletedDate();
        prop = getProperty(pl, Property.COMPLETED);
        if (completed != 0) {
            setUtcProperty(pl, Property.COMPLETED, new java.util.Date(
                    task.getCompletedDate()));
        } else {
            if (prop != null)
                pl.remove(prop);
        }

        setXProperty(pl, "X-NETBEANS-OWNER", task.getOwner(), // NOI18N
            task.getOwner().length() != 0);

        // Parent item
        // attribute reltype for related-to defaults to "PARENT" so we
        // don't need to specify it
        prop = getProperty(pl, Property.RELATED_TO);
        if (task.getParent() != null) {
            if (prop == null) {
                prop = new RelatedTo();
                pl.add(prop);
            }
            String parentuid = ((UserTask)task.getParent()).getUID();
            prop.setValue(parentuid);
        } else {
            if (prop != null)
                pl.remove(prop);
        }
        
        List dep = task.getDependencies();
        removeAll(pl, "X-NETBEANS-DEPENDENCY"); // NOI18N
        for (int i = 0; i < dep.size(); i++) {
            Dependency d = (Dependency) dep.get(i);
            prop = new XProperty("X-NETBEANS-DEPENDENCY"); // NOI18N
            prop.setValue(d.getDependsOn().getUID());
            String t = (d.getType() == Dependency.BEGIN_BEGIN) ?
                "BEGIN_BEGIN" : "END_BEGIN"; // NOI18N
            prop.getParameters().add(new XParameter("X-NETBEANS-TYPE", t)); // NOI18N
            pl.add(prop);
        }

        ObjectList wks = task.getWorkPeriods();
        removeAll(pl, "X-NETBEANS-WORK-PERIOD"); // NOI18N
        for (int i = 0; i < wks.size(); i++) {
            UserTask.WorkPeriod wk = (UserTask.WorkPeriod) wks.get(i);
            prop = new XProperty("X-NETBEANS-WORK-PERIOD"); // NOI18N
            String v = DATEFORMAT.format(new java.util.Date(wk.getStart()));
            prop.getParameters().add(new XParameter(
                    "X-NETBEANS-START", // NOI18N
                    v));
            prop.setValue(Integer.toString(wk.getDuration()));
            pl.add(prop);
        }

        exportResources(task, pl);

        java.util.Date d = task.getDueDate();
        if (d != null)
            setXProperty(pl, "X-NETBEANS-DUETIME", Long.toString(d.getTime()), // NOI18N
                true); 
        else
            setXProperty(pl, "X-NETBEANS-DUETIME", "", // NOI18N
                false);
        
        setXProperty(pl, "X-NETBEANS-DUE-SIGNALED", "yes",  // NOI18N
            task.isDueAlarmSent());
        
//            AssociatedTime associatedTime = task.getAssociatedTime();
//            if (associatedTime != null) {
//                Date d = associatedTime.getStartTime();
//                if (d != null) {
//                    writer.write("X-NETBEANS-STARTTIME:"); // NOI18N
//                    writer.write(Long.toString(d.getTime()));
//                    writer.write("\r\n"); // NOI18N
//                }
//                d = associatedTime.getEndTime();
//                if (d != null) {
//                    writer.write("X-NETBEANS-ENDTIME:"); // NOI18N
//                    writer.write(Long.toString(d.getTime()));
//                    writer.write("\r\n"); // NOI18N
//                }
//                d = associatedTime.getDueDate();
//                if (d != null) {
//                    writer.write("X-NETBEANS-DUETIME:"); // NOI18N
//                    writer.write(Long.toString(d.getTime()));
//                    writer.write("\r\n"); // NOI18N
//                }
//                
//                if (associatedTime.isRecurrent()) {
//                    writer.write("X-NETBEANS-DUERECURRENT-INTERVAL:"); // NOI18N
//                    writer.write(Integer.toString(associatedTime.getInterval()));
//                    writer.write("\r\nX-NETBEANS-DUERECURRENT-MEASUREMENT:"); // NOI18N
//                    switch (associatedTime.getMeasurement()) {
//                        case AssociatedTime.DAY :
//                            writer.write("DAY\r\n"); // NOI18N
//                            break;
//                        case AssociatedTime.WEEK :
//                            writer.write("WEEK\r\n"); // NOI18N
//                            break;
//                        case AssociatedTime.MONTH :
//                            writer.write("MONTH\r\n"); // NOI18N
//                            break;
//                        case AssociatedTime.YEAR :
//                            writer.write("YEAR\r\n"); // NOI18N
//                            break;
//                        default :
//                            System.err.println("EINVAL"); //NOI18N
//                    }
//                }
//            }
    }
    
    /**
     * Changes value of an X-property.
     *
     * @param pl a list of properties
     * @param name name for a X-property
     * @param value new value for the property
     * @param set true = the property will be created, false = the property
     * will be deleted
     */
    private static void setXProperty(PropertyList pl, 
        String name, String value, boolean set) throws IOException, 
        URISyntaxException, ParseException {
        Property prop = getProperty(pl, name);
        if (set) {
            if (prop == null) {
                prop = new XProperty(name);
                pl.add(prop);
            }
            prop.setValue(value);
        } else {
            if (prop != null)
                pl.remove(prop);
        }    
    }
    
    /**
     * Sets an UTC date/time property.
     * Speed optimization.
     * See http://sourceforge.net/tracker/index.php?func=detail&aid=1722243&group_id=107024&atid=646395
     * for details. 
     * 
     * @param pl a list of properties
     * @param name nam for the property
     * @param value value for the property
     */
    private static void setUtcProperty(PropertyList pl, String name, 
            java.util.Date value) throws IOException, ParseException, URISyntaxException {
        int index = -1;
        for (int i = 0; i < pl.size(); i++) {
            Property p = (Property) pl.get(i);
            if (p.getName().equalsIgnoreCase(name)) {
                index = i;
                pl.remove(i);
                break;
            }
        }
        
        XProperty prop = new XProperty(name);
        if (index >= 0)
            pl.add(index, prop);
        else
            pl.add(prop);

        prop.setValue(UTC_FORMAT.format(value));
    }
    
    /**
     * Replacement for PropertyList.getProperty(String)
     * Speed optimization.
     * See http://sourceforge.net/tracker/index.php?func=detail&aid=1722243&group_id=107024&atid=646395
     * for details. 
     * 
     * @param pl a list of properties
     * @param name nam for the property
     * @return property or null
     */
    private static Property getProperty(PropertyList pl, String name) {
        for (int i = 0; i < pl.size(); i++) {
            Property p = (Property) pl.get(i);
            if (p.getName().equalsIgnoreCase(name))
                return p;
        }
        return null;
    }
    
    /**
     * Deletes all properties with the specified name.
     * Implemented for performance reasons.
     * 
     * @param pl list of properties
     * @param name the property name 
     */
    private static void removeAll(PropertyList pl, String name) {
        for (int i = 0; i < pl.size();) {
            Property p = (Property) pl.get(i);
            if (p.getName().equalsIgnoreCase(name))
                pl.remove(i);
            else
                i++;
        }
    }    
    
    /**
     * Exports resources associated with a task.
     * 
     * @param task a task
     * @param pl list of properties 
     */
    private void exportResources(UserTask task, PropertyList pl) {
        ObjectList<UserTaskResource> ress = task.getResources();
        removeAll(pl, "X-NETBEANS-RESOURCE"); // NOI18N
        for (int i = 0; i < ress.size(); i++) {
            UserTaskResource res = (UserTaskResource) ress.get(i);
            Property prop = new XProperty("X-NETBEANS-RESOURCE"); // NOI18N
            if (res instanceof URLResource) {
                prop.getParameters().add(new XParameter(
                        "X-NETBEANS-URL", // NOI18N
                        ((URLResource) res).getUrl().toExternalForm()));
                prop.getParameters().add(new XParameter(
                        "X-NETBEANS-RESOURCE-TYPE", // NOI18N
                        "url")); // NOI18N                
            } else if (res instanceof LineResource) {
                URL url_ = ((LineResource) res).getURL();
                int lineno = ((LineResource) res).getLineNumber();
                prop.getParameters().add(new XParameter(
                        "X-NETBEANS-URL", // NOI18N
                        url_.toExternalForm()));
                prop.getParameters().add(new XParameter(
                        "X-NETBEANS-LINE", // NOI18N
                        String.valueOf(lineno)));
                prop.getParameters().add(new XParameter(
                        "X-NETBEANS-RESOURCE-TYPE", // NOI18N
                        "line")); // NOI18N                
            } else {
                UTUtils.LOGGER.warning("Unknown resource type"); // NOI18N
            }
            pl.add(prop);
        }
    }
}

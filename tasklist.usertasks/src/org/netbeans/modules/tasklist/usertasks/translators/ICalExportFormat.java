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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.SimpleTimeZone;

import javax.swing.filechooser.FileSystemView;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.export.ExportImportFormat;
import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.export.SaveFilePanel;
import org.netbeans.modules.tasklist.core.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.core.util.SimpleWizardPanel;
import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * This class provides import/export capabilities for the iCalendar calendar
 * format (used by for example KDE's Konqueror calendar/todoitem tool)
 * as specified in RFC 2445 with the following exceptions:
 * @todo Write the exceptions to the RFC here!!
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
 */
public class ICalExportFormat implements ExportImportFormat {
    protected final static String 
        CHOOSE_FILE_PANEL_PROP = "ChooseFilePanel"; // NOI18N
    
    // Format which includes the timezone at the end. This is the format
    // used by the tasklist's own written files for example.
    private static final String DATEFORMATZ = "yyyyMMdd'T'HHmmss'Z'"; // NOI18N
    
    // Format used when the timezone is specified separetly, e.g. with TZ:PST
    private static final String DATEFORMAT = "yyyyMMdd'T'HHmmss"; // NOI18N
    
    /**
     * Constructor
     */
    public ICalExportFormat() {
    }
    
    public void doExportImport(ExportImportProvider provider, WizardDescriptor wd) {
        SaveFilePanel panel = 
            (SaveFilePanel) wd.getProperty(CHOOSE_FILE_PANEL_PROP);
        try {
            UserTaskList list = (UserTaskList) UserTaskView.getCurrent().getList();
            FileOutputStream fos = new FileOutputStream(panel.getFile());
            try {
                writeList(list, fos);
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
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
     * Do the actual export of the list into the stream
     *
     * @param list The tasklist to store
     * @param out The output stream object to use
     */
    public void writeList(UserTaskList list, OutputStream out) throws IOException {
        // http://www.ietf.org/rfc/rfc2445.txt 4.1.4:
        // There is not a property parameter to declare the character set used
        // in a property value. The default character set for an iCalendar
        // object is UTF-8 as defined in [RFC 2279].
        Writer writer = new OutputStreamWriter(out, "UTF-8"); // NOI18N 

        // Write header
        writer.write("BEGIN:VCALENDAR\r\n" + // NOI18N
            "PRODID:-//NetBeans tasklist//NONSGML 1.0//EN\r\n" + // NOI18N
            "VERSION:2.0\r\n"); // NOI18N
        
        //writer.write("TZ:GMT\r\n"); // NOI18N
        
        SimpleDateFormat formatter = new SimpleDateFormat(DATEFORMATZ);
        // Dates in UTC
        formatter.setTimeZone(new SimpleTimeZone(0, "GMT")); // NOI18N
        
        // Write out todo items
        Iterator it = list.getSubtasks().iterator();
        while (it.hasNext()) {
            // Note: The previous try/catch block was superfluous (?) since
            // no exceptions will we thrown inside this block (unless
            // the listiterator contains something else than UserTask ;-)
            UserTask item = (UserTask)it.next();
            writeTask(writer, item, formatter);
        }

        // Store all non-vtodo's
        if (list.userObject != null) {
            // This might not be an elegant way to do this, but instead of
            // having to restore everything, I have stored all other items
            // in a (folded) string..
            writer.write("\r\n" + list.userObject); // NOI18N
        }

        writer.write("\r\nEND:VCALENDAR\r\n"); // NOI18N
        writer.flush();
    }
    
    /**
     * Write out the given todo item to the given writer.
     * @param writer The writer object to use
     * @param task The task/todo item to use
     * @param sdf A "SimpleDateFormat-formatter" used to convert a date to string
     * @todo Finish all the unused fields
     */
    private void writeTask(Writer writer, UserTask task, SimpleDateFormat sdf) 
    throws IOException {
        // Catch errors locally so that we don't botch the whole
        // list if you run into an I/O error
        writer.write("\r\nBEGIN:VTODO\r\n"); // NOI18N

        // UID (Unique Identifier)  (see RFC 822 and RFC 2445)
        writer.write("UID:"); // NOI18N
        writer.write(task.getUID());
        writer.write("\r\n"); // NOI18N

        // Created date
        long created = task.getCreatedDate();
        String datestring = sdf.format(new Date(created));
        writer.write("CREATED:"); // NOI18N
        writer.write(datestring);
        writer.write("\r\n"); // NOI18N

        // dtstart -- not yet implemented

        // due -- not yet implemented

        // organizer -- not yet implemented

        // summary: (Description)
        String desc = task.getSummary();
        if (desc != null && desc.length() > 0) {
            writeEscaped(writer, "SUMMARY", null, desc); // NOI18N
            writer.write("\r\n"); // NOI18N
        }

        // description (details)
        String details = task.getDetails();
        if (details != null && details.length() > 0) {
            writeEscaped(writer, "DESCRIPTION", null, details); // NOI18N
            writer.write("\r\n"); // NOI18N
        }

        // Priority
        if (task.getPriority() != SuggestionPriority.MEDIUM) {
            writer.write("PRIORITY:"); // NOI18N
            writer.write(Integer.toString(task.getPriority().intValue()));
            writer.write("\r\n"); // NOI18N
        }

        // Class -- not implemented (always PRIVATE, right?) Also allowed:
        // PRIVATE, CONFIDENTIAL
        /* XXX Don't bother with this yet... waste of diskspace
           and parsing time -- only needed when we either export
           to XCS, or directly interoperate. There's too much
           missing yet to add partial support
        // For now, hardcode to private such that others don't get access
        writer.write("CLASS:PRIVATE\r\n"); // NOI18N
         */

        // attendee -- not implemented

        // Others not implemented:
        // dtstart, geo, location, organizer, percent, recurid, seq, status,
        // due, duration (both cannot occur)

        // Optional ones not implemented:
        // attach, attendee, categories, comment, contact, exdate, exrule,
        // rstatus, related, resources, rdate, rrule, x-prop (actually,
        // xprop is special, we will have those)


        writer.write("PERCENT-COMPLETE:"); // NOI18N
        writer.write(Integer.toString(task.getPercentComplete()));
        writer.write("\r\n"); // NOI18N

        boolean computed = task.isProgressComputed();
        if (computed) {
            writeEscaped(writer, "X-NETBEANS-PROGRESS-COMPUTED",  // NOI18N
                         null, "yes"); // NOI18N
            writer.write("\r\n"); // NOI18N
        }

        writer.write("X-NETBEANS-EFFORT:"); // NOI18N
        writer.write(Integer.toString(task.getEffort()));
        writer.write("\r\n"); // NOI18N

        computed = task.isEffortComputed();
        if (computed) {
            writeEscaped(writer, "X-NETBEANS-EFFORT-COMPUTED",  // NOI18N
                         null, "yes"); // NOI18N
            writer.write("\r\n"); // NOI18N
        }

        writer.write("X-NETBEANS-SPENT-TIME:"); // NOI18N
        writer.write(Integer.toString(task.getSpentTime()));
        writer.write("\r\n"); // NOI18N

        computed = task.isSpentTimeComputed();
        if (computed) {
            writeEscaped(writer, "X-NETBEANS-SPENT-TIME-COMPUTED",  // NOI18N
                         null, "yes"); // NOI18N
            writer.write("\r\n"); // NOI18N
        }

        // Category (XXX standard allows MULTIPLE categories, I must handle
        // that when I parse back)
        String category = task.getCategory();
        if (category != null && category.length() > 0) {
            // TODO Write out multiple CATEGORIES lines instead
            // of a combined comma separated list which is what we're
            // doing here
            writeEscaped(writer, "CATEGORIES", null, category); // NOI18N
            writer.write("\r\n"); // NOI18N
        }

        // Last modified
        // Last Edited Date, if different than created
        long edited = task.getLastEditedDate();

        if (edited != created) {
            // They differ
            datestring = sdf.format(new Date(edited));
            writer.write("LAST-MODIFIED:"); // NOI18N
            writer.write(datestring);
            writer.write("\r\n"); // NOI18N
        }


        // URL
        URL url = task.getUrl();
        if (url != null) {
            writeEscaped(writer, "URL",  // NOI18N
                null, url.toExternalForm());
            writer.write("\r\n"); // NOI18N
        }
        
        // Line number
        int lineno = task.getLineNumber();
        if (lineno >= 0) {
            writer.write("X-NETBEANS-LINE:"); // NOI18N
            writer.write(Integer.toString(lineno + 1));
            writer.write("\r\n"); // NOI18N
        }

        // Parent item
        // attribute reltype for related-to defaults to "PARENT" so we
        // don't need to specify it
        if (task.getParent() != null) {
            String parentuid = ((UserTask)task.getParent()).getUID();
            writer.write("RELATED-TO:"); // NOI18N
            // XXX does it need to be escaped?
            // Certainly my uids don't need to be, but other tools
            // may be generating UIDs with characters that need to
            // be escaped. Or does the spec forbid that?
            writer.write(parentuid);
            writer.write("\r\n"); // NOI18N
        }

        Date d = task.getDueDate();
        if (d != null) {
            writer.write("X-NETBEANS-DUETIME:"); // NOI18N
            writer.write(Long.toString(d.getTime()));
            writer.write("\r\n"); // NOI18N

            if (task.isDueAlarmSent()) {
                writer.write("X-NETBEANS-DUE-SIGNALED:true\r\n"); // NOI18N                    
            }                
        }
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

        // Write out unsupported tags on this VTODO
        if (task.userObject != null) {
            // The string is stored in folded format!
            writer.write(task.userObject.toString());
        }

        writer.write("END:VTODO\r\n"); // NOI18N

        // Recurse over subtasks
        // XXX do the other tags here...
        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask subtask = (UserTask)it.next();
            writeTask(writer, subtask, sdf);
        }
    }
    
    /**
     * Write out a content line escaped according to the spec:
     * break at 75 chars, add escapes to certain characters, etc.
     *
     * @param writer the writer used to write the data
     * @param name the name of the tag to write (without the ':')
     * @param param the param of the field
     * @param value the value to write
     */
    private void writeEscaped(Writer writer, String name, String param, String value) throws IOException {
        int col = name.length();
        writer.write(name);
        
        if (param != null) {
            col += param.length() + 1; // NOI18N
            writer.write(" "); // NOI18N
            writer.write(param);
        }
        ++col;
        writer.write(":"); // NOI18N
        
        int n = value.length();
        for (int i = 0; i < n; i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\n':
                    writer.write("\\n"); // NOI18N
                    col++; // One extra char expansion
                    break;
                case ';':
                case ',':
                case '\\':
                    // Escape the character by preceding it by a "\"
                    writer.write('\\');
                    col++; // One extra char expansion
                    // NOTE FALL THROUGH!
                default:
                    writer.write(c);
                    break;
            }
            
            col++;
            if (col >= 75) {
                col = 1; // for the space on the next line
                writer.write("\r\n "); // NOI18N   note the space - important
            }
        }
    }
}

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.swing.filechooser.FileSystemView;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.export.ExportImportFormat;
import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.export.OpenFilePanel;
import org.netbeans.modules.tasklist.core.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.core.util.SimpleWizardPanel;
import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
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
 * TODO: review this class after the "export/import framework" rewrite
 *
 * The iCalendar supports other "tags" for a VTODO item than Tasklist. In
 * order to avoid loosing such information, these unknown tags are stored
 * inside the UserTask object.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public class ICalImportFormat implements ExportImportFormat {
    protected final static String 
        CHOOSE_FILE_PANEL_PROP = "ChooseFilePanel"; // NOI18N
    
    // Format which includes the timezone at the end. This is the format
    // used by the tasklist's own written files for example.
    private static final String DATEFORMATZ = "yyyyMMdd'T'HHmmss'Z'"; // NOI18N
    
    // Format used when the timezone is specified separetly, e.g. with TZ:PST
    private static final String DATEFORMAT = "yyyyMMdd'T'HHmmss"; // NOI18N
    
    private Reader reader = null;
    private int lineno = 0;
    private int prevChar = -1;
    
    private StringBuffer nsb = new StringBuffer(400); // Name
    private StringBuffer psb = new StringBuffer(400); // Param
    private StringBuffer vsb = new StringBuffer(400); // Value
    
    /**
     * Constructor
     */
    public ICalImportFormat() {
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
        UserTaskList utl = (UserTaskList) view.getList();
        
        InputStream is;
        try {
            is = new FileInputStream(panel.getFile());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return;
        }
        
        try {
            this.read(utl, is);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
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
        ); // NOI18N
        
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

    /** Return most recently parsed value nextContentLine */
    private String getValue() {
        return vsb.toString();
    }
    
    /** Return most recently parsed value nextContentLine */
    private String getParamName() {
        String name = nsb.toString();
        if (name.length() == 0) {
            return null;
        } else {
            return name;
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
    
    /**
     * Stash away a bulk of data in the writer
     * @param writer where to store data
     * @param name the last name read from the stream
     * @param param the last param read from the stream
     * @param value the last value read from the stream
     * @throws IOException if anything goes wrong...
     */
    private void stashBulk(Writer writer, String name, String param, 
    String value) throws IOException {
        int stack = 0;
        
        writeEscaped(writer, name, param, value);
        writer.write("\r\n"); // NOI18N
        
        boolean done = false;
        while (!done) {
            processContentLine();
            name = getName();
            if (name == null) {
                break;
            }
            value = getValue();
            param = getParam();
            
            if (name.equals("BEGIN")) { // NOI18N
                ++stack;
            } else if (name.equals("END")) { // NOI18N
                if (stack == 0) {
                    done = true;
                } else {
                    --stack;
                }
            }
            
            writeEscaped(writer, name, param, value);
            writer.write("\r\n"); // NOI18N
        }
    }
    
    /**
     * Get the most recently parsed parameter
     */
    private String getParam() {
        String param = psb.toString();
        if (param.length() == 0) {
            return null;
        } else {
            return param;
        }
    }
    
    /** Read (doing all the ical unfolding) the next content line.
     * Side effects the reader object and the lineno.
     * @return The next content line, or null if there is some
     * I/O problem preventing us from continuing (e.g. EOF).
     */
    private void processContentLine() throws IOException {
        // ignore it - and locate the next field
        
        // Reuse string buffers for improved efficiency
        nsb.setLength(0);
        psb.setLength(0);
        vsb.setLength(0);
        
        if (prevChar != -1) {
            nsb.append((char)prevChar);
        }
        
        // Read in characters, doing substitutions as necessary
        boolean escape = (prevChar == '\\');
        prevChar = -1;
        StringBuffer sb = nsb; // Processing name
        boolean processingName = true; // may not need these flags anymore, use sb
        boolean processingValue = false;
        
        while (true) {
            int ci = reader.read();
            if (ci == -1) {
                // End of stream
                return;
            }
            char c = (char)ci;
            // See section 4.3.11 in rfc 2445
            if (escape) {
                escape = false;
                switch (c) {
                    case '\\':
                        sb.append('\\');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'N':
                        sb.append('N');
                        break;
                    case ';':
                        sb.append(';');
                        break;
                    case ',':
                        sb.append(',');
                        break;
                    default:
                        // Error - illegal input. For now I guess
                        // we'll just pass the escape through...
                        sb.append('\\');
                        sb.append(c);
                }
            } else {
                switch (c) {
                    case '\\':
                        escape = true;
                        break;
                    case ' ':
                        if (processingName) {
                            processingName = false;
                            sb = psb;
                        } else if (processingValue) {
                            sb.append(c);
                        }
                        break;
                    case ';':
                        if (processingName) {
                            processingName = false;
                            sb = psb;
                        } else if (processingValue) {
                            sb.append(c);
                        }
                        break;
                    case ':':
                        if (processingValue) {
                            // Error in input - I've seen Korganizer do this;
                            // they're supposed to escape : but they didn't
                            sb.append(c);
                        } else {
                            sb = vsb;
                            processingValue = true;
                            processingName = false;
                        }
                        break;
                    case '\r':
                        // The spec calls for lines to be terminated with \r\n
                        // but internally we don't want \r's
                        break;
                    case '\n':
                        // New line
                        lineno++;
                        prevChar = reader.read();
                        while (prevChar == '\n') {
                            // Skip blank lines
                            prevChar = reader.read();
                            lineno++;
                        }
                        
                        // @TODO TROND: Si meg... dette stemmer vel ikke helt??
                        // jeg skal jo ogs? godta HTAB (ASCII 9!!!)
                        if (prevChar == ' ' || prevChar == '\t') {
                            // Aha! Line continuation -- we've just
                            // unfolded a line, keep processing
                            break;
                        } else { // includes case where prevChar==-1: EOF
                            // No, this is a new content line so
                            // consider ourselves done with this line
                            return;
                        }
                    default:
                        sb.append(c);
                }
            }
        }
    }
    
    /**
     * Read and parse a single VTODO entry.
     * @param list The list of usertasks
     * @param formatter A date formatter object used to parse dates.
     * @return the complete VTODO entry or null
     */
    private UserTask readVTODO(UserTaskList list, UserTask prev, SimpleDateFormat formatter) throws IOException {
        UserTask task = new UserTask("", list); // NOI18N
        // TODO task.setSilentUpdate(true, false);
        task.setLastEditedDate(System.currentTimeMillis());
        StringWriter writer = null;
        String related = null;
        
        while (true) {
            processContentLine();
            String name = getParamName();
            if (name == null) {
                // incomplete entry, throw it away!!! @@@
                // but what happens to the stream????
                return null;
            }
            String value = getValue();
            String param = getParam();
            
            if (name.equals("BEGIN")) { // NOI18N
                if (writer == null) {
                    writer = new StringWriter();
                }
                stashBulk(writer, name, param, value);
            }
            
            // UTUtils.LOGGER.fine("processing " + name); // NOI18N
            
            if (name.equals("END")) { // NOI18N
                break;  // @@@ Should I verify that this is the end of a VTODO???
            } else if (name.equals("CREATED")) { // NOI18N
                try {
                    Date created = formatter.parse(value);
                    task.setCreatedDate(created.getTime());
                } catch (ParseException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if (name.equals("UID")) { // NOI18N
                task.setUID(value);
            } else if (name.equals("LAST-MODIFIED")) { // NOI18N
                try {
                    Date edited = formatter.parse(value);
                    task.setLastEditedDate(edited.getTime());
                } catch (ParseException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if (name.equals("PERCENT-COMPLETE")) { // NOI18N
                try {
                    int complete = Integer.parseInt(value);
                    task.setPercentComplete(complete);
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if (name.equals("X-NETBEANS-PROGRESS-COMPUTED")) { // NOI18N
                if (value.equals("yes")) // NOI18N
                    task.setProgressComputed(true);
            } else if (name.equals("PRIORITY")) { // NOI18N
                try {
                    int prio = Integer.parseInt(value);
                    task.setPriority(SuggestionPriority.getPriority(prio));
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if (name.equals("X-NETBEANS-EFFORT-COMPUTED")) { // NOI18N
                if (value.equals("yes")) // NOI18N
                    task.setEffortComputed(true);
            } else if (name.equals("X-NETBEANS-EFFORT")) { // NOI18N
                try {
                    int e = Integer.parseInt(value);
                    task.setEffort(e);
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if (name.equals("X-NETBEANS-SPENT-TIME-COMPUTED")) { // NOI18N
                if (value.equals("yes")) // NOI18N
                    task.setSpentTimeComputed(true);
            } else if (name.equals("X-NETBEANS-SPENT-TIME")) { // NOI18N
                try {
                    int e = Integer.parseInt(value);
                    task.setSpentTime(e);
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if (name.equals("CATEGORIES")) { // NOI18N
                String cat = value;
                String oldcat = task.getCategory();
                if ((oldcat != null) && (oldcat.length() > 0)) {
                    // Multiple categories.
                    // Just append
                    cat = oldcat + "," + cat; // NOI18N
                }
                task.setCategory(cat);
            } else if ("DESCRIPTION".equals(name)) { // NOI18N
                task.setDetails(value);
            } else if ("SUMMARY".equals(name)) { // NOI18N
                task.setSummary(value);
            } else if ("X-NETBEANS-FILENAME".equals(name)) { // NOI18N
                task.setFilename(value);
            } else if ("X-NETBEANS-LINE".equals(name)) { // NOI18N
                int lineno = 0;
                try {
                    lineno = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(e);
                }
                task.setLineNumber(lineno);
            } else if ("RELATED-TO".equals(name)) { // NOI18N
                related = value;
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
            } else if ("X-NETBEANS-DUETIME".equals(name)) { // NOI18N
                Date d = null;
                try {
                    d = new Date(Long.parseLong(value));
                    task.setDueDate(d);
                } catch (NumberFormatException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if ("DUE".equals(name)) { // NOI18N
                try {
                    Date due = formatter.parse(value);
                    task.setDueDate(due);
                } catch (ParseException e) {
                    ErrorManager.getDefault().notify(e);
                }
            } else if ("X-NETBEANS-DUE-SIGNALED".equals(name)) { // NOI18N
                task.setDueAlarmSent(true);                
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
            } else {
                // stash away the line!!!
                if (writer == null) {
                    writer = new StringWriter();
                }
                
                writeEscaped(writer, name, param, value);
                writer.write("\r\n"); // NOI18N
            }
        }
        
//        if (associatedTime != null) {
//            task.setAssociatedTime(associatedTime);
//        }
        
        if (writer != null) {
            task.userObject = writer.getBuffer();
        }
        
        UserTask alreadyExists = list.findItem(list.getSubtasks().iterator(), task.getUID());
        if (alreadyExists != null) {
            // I should replace alreadyexists with task...
            UserTask parent = alreadyExists.getParent();
            if (parent != null) {
                parent.getSubtasks().remove(alreadyExists);
                parent.getSubtasks().add(task);
            } else {
                list.getSubtasks().remove(alreadyExists);
                list.getSubtasks().add(task);
            }
            
            Iterator li = alreadyExists.getSubtasks().iterator();
            while (li.hasNext()) {
                UserTask c = (UserTask)li.next();
                alreadyExists.getSubtasks().remove(c);
                task.getSubtasks().add(c);
            }
        } else if (related != null) {
            // the parent setting !!
            UserTask parent;
            if (prev != null && prev.getUID().equals(related)) {
                parent = prev;
            } else {
                parent = list.findItem(list.getSubtasks().iterator(), related);
            }
            
            if (parent != null) {
                parent.getSubtasks().add(task);
            }
        }
        
        return task;
    }
    
    /**
     * Read an iCalendar stream, and store all of the VTODOs inside the tasklist.
     * Keep all unrecognized lines in otherItems...
     * @param list where to store the list
     * @param reader the reader to use on the input stream
     * @throws IOException if a read error occurs
     * @throws UnknownFileFormatException if I somehow believes that this is no
     *         iCalendar format...
     * @return true if success
     */
    public void read(UserTaskList list, InputStream reader) throws IOException 
    {
        this.reader = new InputStreamReader(reader, "UTF-8"); // NOI18N
        UserTaskList ulist = (UserTaskList)list;
        UserTask prev = null;
        
        StringWriter writer = new StringWriter();
        
        SimpleDateFormat formatter = null;
        formatter = new SimpleDateFormat(DATEFORMATZ);
        formatter.setTimeZone(new SimpleTimeZone(0, "GMT")); // NOI18N
        
        do {
            processContentLine();
            String name = getParamName();
            if (name == null) {
                break;
            } else if (name.length() == 0 || name.equals("\r")) { // NOI18N
                continue; // skip empty lines....
            }
            
            String value = getValue();
            String param = getParam();
            
            if (name.equals("BEGIN")) { // NOI18N
                if (value == null) {
                    // SYNTAX ERROR!! XXX What to do??
                    throw new IOException(org.openide.util.NbBundle.getMessage(
                        ICalImportFormat.class, "BeginExpected")); // NOI18N
                }
                
                if (value.equals("VTODO")) { // NOI18N
                    // Call a sub-function to process this line!!!
                    UserTask task = readVTODO(ulist, prev, formatter);
                    
                    if (task != null) {
                        if (task.getParent() == null) {
                            ulist.getSubtasks().add(task);
                        }
                        // TODO task.setSilentUpdate(false, false);
                        prev = (UserTask)task;
                    }
                } else if (value.equals("VCALENDAR")) { // NOI18N
                    // Just swallow
                } else {
                    // Stash away everything up to the corresponding END
                    stashBulk(writer, name, param, value);
                }
            } else if (name.equals("PRODID")) { // NOI18N
                // just swallow
            } else if (name.equals("VERSION")) { // NOI18N
                // just swallow
            } else if (name.equals("END")) { // NOI18N
                // Just swallow
            } else if (name.equals("CALSCALE")) { // NOI18N
                // Evolution (if not others) adds CALSCALE:GREGORIAN near
                // the top of the file.
                // Just swallow
                // ...or make sure that value=GREGORIAN and if not, warn user?
            } else if (name.equals("TZ")) { // NOI18N
                formatter = new SimpleDateFormat(DATEFORMAT);
                if (!value.equals("GMT")) { // NOI18N
                    // Use a date format without a timezone at the end
                    // of it, since they're probably not included now
                    // that the timezone has been reported once and for
                    // all. GnomeCal writes tasklists in this format.
                    TimeZone tz = TimeZone.getTimeZone(value);
                    if (tz != null) {
                        formatter.setTimeZone(tz);
                    } else {
                        ErrorManager.getDefault().log(
                            org.openide.util.NbBundle.getMessage(ICalImportFormat.class, "TimezoneUnknown")); // NOI18N
                    } 
                }
            } else {
                if (lineno <= 1) {
                    // XXX Hmmm I should probably read the RFC and see
                    // what I could XXX expect.. For now, just treat
                    // it as an incorrect file format.
                    //
                    // See RFC 2446 chapter 5 - it specifies which
                    // entries must be handled and which can be
                    // ignored.
                    //
                    String msg = NbBundle.getMessage(ICalImportFormat.class, "ProbablyNotiCalFormat"); // NOI18N
                    throw new IOException(msg);
                } else {
                    // Error on some other line: probably an
                    // unsupported tag (For example, I discovered that
                    // it claimed evolution-task files aren't in ics
                    // format because it came across the tag CALSCALE
                    // and bailed.)
                    if (name.startsWith("X-")) { // NOI18N
                        ErrorManager.getDefault().log(
                           ErrorManager.WARNING, "WARNING: " + // NOI18N
                           "Ignoring nonstandard entry (line " + lineno + // NOI18N
                           "): name=" + name + ", value=" + value + ", param=" + // NOI18N
                           param);
                    } else {
                        ErrorManager.getDefault().log(
                           ErrorManager.WARNING, "WARNING: " + // NOI18N
                           "Unsupported iCalendar file entry (line " + lineno + // NOI18N
                           "): name=" + name + ", value=" + value + ", param=" + // NOI18N
                           param);
                    }
                }
            }
        } while (true);
        
        String otherItems = writer.getBuffer().toString();
        if (otherItems.length() == 0) {
            otherItems = null;
        }
        list.userObject = otherItems;
    }
}

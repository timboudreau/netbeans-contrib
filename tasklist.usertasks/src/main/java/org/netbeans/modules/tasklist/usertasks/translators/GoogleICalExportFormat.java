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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.logging.Level;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.netbeans.modules.tasklist.export.ExportImportFormat;
import org.netbeans.modules.tasklist.export.ExportImportProvider;
import org.netbeans.modules.tasklist.export.SaveFilePanel;
import org.netbeans.modules.tasklist.export.SimpleWizardPanel;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.ExtensionFileFilter;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * Export for Google Calendar.
 *
 * @author tl
 */
public class GoogleICalExportFormat implements ExportImportFormat {
    private final static String PRODID = 
        "-//NetBeans User Tasks//NONSGML 1.0//EN"; // NOI18N
    protected final static String 
        CHOOSE_FILE_PANEL_PROP = "ChooseFilePanel"; // NOI18N

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
                writeList(list, w);
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
        return NbBundle.getMessage(GoogleICalExportFormat.class, "GoogleICalExp"); // NOI18N
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
        title = NbBundle.getMessage(GoogleICalExportFormat.class, "ExportICAL"); // NOI18N
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
     * @param std true = use RFC 2445 line endings, false = system default
     * line ending
     */
    public void writeList(UserTaskList list, Writer out) 
    throws IOException, ValidationException, URISyntaxException, ParseException {
        Calendar cal = new Calendar();
        
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
        while (it.hasNext()) {
            UserTask item = (UserTask) it.next();
            writeTask(cal, item);
        }
        
        // an .ics file cannot be empty
        if (cal.getComponents().size() == 0) {
            VToDo td = new VToDo();
            td.getProperties().add(new Summary(
                    NbBundle.getMessage(GoogleICalExportFormat.class, 
                    "Welcome"))); // NOI18N

            cal.getComponents().add(td);
        }

        CalendarOutputter co = new CalendarOutputter();

        co.output(cal, out);
    }
    
    /**
     * Creates ical4j objects for a task and all of its subtasks.
     * 
     * @param cal calendar
     * @param task a task
     */
    private void writeTask(Calendar cal, UserTask task) 
    throws IOException, URISyntaxException, ParseException, ValidationException {
        writeTask0(cal, task);

        // Recurse over subtasks
        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask subtask = (UserTask)it.next();
            writeTask(cal, subtask);
        }
    }
    
    /**
     * Write out the given todo item to the given writer.
     *
     * @param cal calendar object
     * @param task The task/todo item to use
     */
    @SuppressWarnings("unchecked")
    protected void writeTask0(Calendar cal, UserTask task) 
    throws IOException, URISyntaxException, ParseException, ValidationException {
        if (task.isValuesComputed())
            return;
        if (task.getStartDate() == null)
            return;
        
        VEvent vevent;
        vevent = new VEvent();
        vevent.getProperties().add(new Uid(task.getUID()));
        cal.getComponents().add(vevent);

        PropertyList pl = vevent.getProperties();
        Property prop = getProperty(pl, Property.CREATED);
        long created = task.getCreatedDate();
        prop = getProperty(pl, Property.CREATED);
        if (prop == null) {
            prop = new Created();
            pl.add(prop);
        }
        ((Created) prop).setUtc(true);
        ((Created) prop).setDateTime(new DateTime(created));
        // DEBUG: prop.validate();
            
        // DTSTAMP
        prop = getProperty(pl, Property.DTSTAMP);
        if (prop == null) {
            prop = new DtStamp();
            pl.add(prop);
        }
        ((DtStamp) prop).setUtc(true);
        ((DtStamp) prop).setDateTime(new DateTime(created));
        
        pl.add(new DtStart(new DateTime(task.getStart())));

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
            if (prop == null) {
                prop = new LastModified();
                pl.add(prop);
            }
            ((LastModified) prop).setUtc(true);
            ((LastModified) prop).setDateTime(new DateTime(edited));
       } else {
            if (prop != null)
                pl.remove(pl);
        }
        
        // Duration
        pl.add(new Duration(new Dur(0, 0, task.getEffort(), 0)));
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
}

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


package org.netbeans.modules.tasklist.docscan;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.docscan.TaskTag;
import org.openide.nodes.BeanNode;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;


/** Settings for the tasklist module.
 */

public final class Settings  {
    private static final Settings INSTANCE = new Settings();
    public static final String PROP_SCAN_SKIP = "skipComments";	//NOI18N
    public static final String PROP_SCAN_TAGS = "taskTags";		//NOI18N
    static final String PROP_MODIFICATION_TIME = "modificationTime";  // NOI18N
    /** Defines how many suggestions make sence. */
    public static final String PROP_USABILITY_LIMIT = "usabilityLimit";  // NOI18N
    private final static int DEFAULT_USABILITY_LIMIT = 300;

    private TaskTags tags = null;
    
    private Settings() {}

    /** Return the signleton */
    public static Settings getDefault() {
        return INSTANCE;
    }

    private  static Preferences getPreferences() {
        return NbPreferences.forModule(Settings.class);
    }    

    /**
     * Get the display name.
     *
     *  @return value of OPTION_TASK_SETTINGS_NAME
     */
    public String displayName() {
        return NbBundle.getMessage(Settings.class,
                "OPTION_TASK_SETTINGS_NAME"); //NOI18N
    }

    public HelpCtx getHelpCtx () {
	return new HelpCtx (org.netbeans.modules.tasklist.docscan.Settings.class);	        //NOI18N
    }


    /**
     * @return true iff the user wants to skip all tasks tokens
     * appear outside of comment sections.  The default value
     * is true.
     */
    public boolean getSkipComments() {
        // XXX I did a spectacularly poor job naming this method.
        return getPreferences().getBoolean(PROP_SCAN_SKIP, false);
    }

    /** Sets the skip-outside-of-comments property
     * @param doSkip True iff you want to skip tasks outside of comments
     */
    public void setSkipComments(boolean doSkip) {
        getPreferences().putBoolean(PROP_SCAN_SKIP, doSkip);
        modified();
    }


    public void setUsabilityLimit(int limit) {
        if (limit > 1000) limit = 1000;
        if (limit <=0) limit = DEFAULT_USABILITY_LIMIT;
        getPreferences().putInt(PROP_USABILITY_LIMIT, limit);
    }

    public int getUsabilityLimit() {
        return getPreferences().getInt(PROP_USABILITY_LIMIT, DEFAULT_USABILITY_LIMIT);
    }

    public TaskTags getTaskTags() {
        if (tags == null) {
            tags = initTaskTags();
        }
        return tags;
    }
    
    /** Sets the skip-outside-of-comments property
     * @param doSkip True iff you want to skip tasks outside of comments
     */
    public void setTaskTags(TaskTags scanTasks) {
        tags = scanTasks;
        storeTaskTags(scanTasks);
        modified();
    }
    
    private static TaskTags initTaskTags() {
        TaskTags retval = new TaskTags();
        try {
            Preferences pNode = getPreferences();
            String[] keys = pNode.keys();
            List l = new ArrayList();
            for (int i = 0; i < keys.length; i++) {
                String k = keys[i];
                if (k != null && k.startsWith("Tag")) {//NOI18N
                    l.add(new TaskTag(k.substring("Tag".length()),//NOI18N
                            SuggestionPriority.getPriority(pNode.getInt(k, 3))));
                }
            }
            retval.setTags((TaskTag[])l.toArray(new TaskTag[l.size()]));
        } catch (BackingStoreException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.INFO, null, ex);
        }
        if (retval.getTags().length == 0) {
            retval.setTags(new TaskTag[]{
                new TaskTag("@todo", SuggestionPriority.MEDIUM),
                        new TaskTag("TODO", SuggestionPriority.MEDIUM),
                        new TaskTag("FIXME", SuggestionPriority.MEDIUM),
                        new TaskTag("XXX", SuggestionPriority.MEDIUM),
                        new TaskTag("PENDING", SuggestionPriority.MEDIUM),
                        // XXX CVS merge conflict: overlaps with skipNonComments settings
                        new TaskTag("<<<<<<<", SuggestionPriority.HIGH),                        
                        // Additional candidates: HACK, WORKAROUND, REMOVE, OLD
            });
        }
        return retval;
    }
    
    private static void storeTaskTags(TaskTags tags) {
        removeTaskTags();
        Preferences pNode = getPreferences();
        TaskTag[] tts = tags.getTags();
        for (int i = 0; i < tts.length; i++) {
            TaskTag taskTag = tts[i];
            getPreferences().putInt("Tag"+taskTag.getToken(),taskTag.getPriority().intValue());//NOI18N
        }
    }
    
    private static void removeTaskTags() {
        Preferences pNode = getPreferences();        
        try {
            String[] keys = pNode.keys();            
            for (int i = 0; i < keys.length; i++) {
                String k = keys[i];                
                if (k != null && k.startsWith("Tag")) {//NOI18N
                    getPreferences().remove(k);
                }
            }            
        } catch (BackingStoreException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.INFO, null, ex);        
        }
    }
    
    /**
     * Last modification time is stored as hidden property.
     */
    public long getModificationTime() {
        return getPreferences().getLong(PROP_MODIFICATION_TIME,0);
    }

    /** for deserialization purposes only */
    public void setModificationTime(long time) {
        getPreferences().putLong(PROP_MODIFICATION_TIME,time);
    }

    // update modification time
    private void modified() {
        getPreferences().putLong(PROP_MODIFICATION_TIME, System.currentTimeMillis());
    }
    
    private static BeanNode createViewNode() throws java.beans.IntrospectionException {
        return new BeanNode(Settings.getDefault());
    }                 
}

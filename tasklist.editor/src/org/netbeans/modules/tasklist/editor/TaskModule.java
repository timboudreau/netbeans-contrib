/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.tasklist.editor;

import org.netbeans.editor.LocaleSupport;
import org.netbeans.modules.editor.NbLocalizer;
import org.openide.modules.ModuleInstall;





/** Module installation class for the tasklist module
 * I hate having to do this, but I need to add a Localizer
 * to the editor; otherwise my "New Task" action in the
 * editor margin won't get translated. I was hoping I could
 * do it in the constructor for the TextAction, but that's too
 * late; the name getString'ed in the constructor.
 * @author Tor Norbye */
public class TaskModule extends ModuleInstall {

    static final long serialVersionUID = -3935204626992817943L;
    
    /** Called at startup; adds my own localizer for translation 
        @todo Use lookup instead of the above addTaskListener approach
     */    
    public void restored() {
        LocaleSupport.addLocalizer(new NbLocalizer(NewTaskEditorAction.class));
    }
}

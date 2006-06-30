/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
 *
 * @author Tor Norbye
 */
public class TaskModule extends ModuleInstall {

    static final long serialVersionUID = -3935204626992817943L;
    
    /** 
     * Called at startup; adds my own localizer for translation 
     */    
    public void restored() {
        LocaleSupport.addLocalizer(new NbLocalizer(NewTaskEditorAction.class));
    }
}

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
/*
 * CheckOutTask.java
 *
 * Created on March 8, 2007, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.saw.palette.items;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.NbBundle;

/**
 *
 * @author Vihang
 */



public class CheckoutTask implements ActiveEditorDrop {
    private static Logger logger = Logger.getLogger("SAW_Logger");
    /** Creates a new instance of SecurityDomain */
    public CheckoutTask() {
    }

    public boolean handleTransfer(JTextComponent targetComponent) {
       
        BaseDocument document = (BaseDocument) targetComponent.getDocument();
        FileObject fObject = NbEditorUtilities.getFileObject(document);
        logger.log(Level.INFO,this.getClass().getName() + ":",fObject.toString());
        
        ClassPath cp = ClassPath.getClassPath(fObject, ClassPath.COMPILE);
        if (cp != null) {
            logger.log(Level.INFO,this.getClass().getName() + ":","Classpath is:" + cp.toString());
        }
        String className = cp.getResourceName(fObject, '.', false);
        logger.log(Level.INFO,this.getClass().getName() + ":","Class Name is:" + className);
        Class clazz = null;
        boolean flag = false;
        return RefactoringUtil.addMethod(fObject,NbBundle.getBundle(CheckoutTask.class).getString("CheckOut_tasks"),false);
        
        
    }
}

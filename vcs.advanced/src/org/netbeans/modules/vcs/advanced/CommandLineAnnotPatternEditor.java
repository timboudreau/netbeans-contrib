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

package org.netbeans.modules.vcs.advanced;

import java.beans.*;
import javax.swing.*;

import org.openide.util.NbBundle;
import org.netbeans.modules.vcscore.annotation.*;
import org.netbeans.modules.vcscore.caching.RefreshCommandSupport;

/** Property editor for annotation pattern properties
*
* @author Milos Kleint, Martin Entlicher

*/
public class CommandLineAnnotPatternEditor extends AnnotationPatternPropertyEditor {

    public CommandLineAnnotPatternEditor() {
    }
    
    
    public String[] getPatternDisplaNames() {
        return new String[]
        {AnnotationSupport.ANNOTATION_PATTERN_FILE_NAME,
         AnnotationSupport.ANNOTATION_PATTERN_STATUS,
         AnnotationSupport.ANNOTATION_PATTERN_REVISION,
         AnnotationSupport.ANNOTATION_PATTERN_STICKY,
         AnnotationSupport.ANNOTATION_PATTERN_LOCKER,
         AnnotationSupport.ANNOTATION_PATTERN_SIZE,
         AnnotationSupport.ANNOTATION_PATTERN_ATTR,
         AnnotationSupport.ANNOTATION_PATTERN_DATE,
         AnnotationSupport.ANNOTATION_PATTERN_TIME 
        };
    }
    
    public String[] getPatterns() {
         return new String[]
        {AnnotationSupport.ANNOTATION_PATTERN_FILE_NAME,
         AnnotationSupport.ANNOTATION_PATTERN_STATUS,
         AnnotationSupport.ANNOTATION_PATTERN_REVISION,
         AnnotationSupport.ANNOTATION_PATTERN_STICKY,
         AnnotationSupport.ANNOTATION_PATTERN_LOCKER,
         AnnotationSupport.ANNOTATION_PATTERN_SIZE,
         AnnotationSupport.ANNOTATION_PATTERN_ATTR,
         AnnotationSupport.ANNOTATION_PATTERN_DATE,
         AnnotationSupport.ANNOTATION_PATTERN_TIME 
         };
    }
    
    public String getDefaultAnnotationPattern() {
       return RefreshCommandSupport.DEFAULT_ANNOTATION_PATTERN;   
    }

}

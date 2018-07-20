/*
 * ToolsAnotation.java
 *
 * Created on 9. leden 2006, 21:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.toolsintegration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;

/**
 *
 * @author Owner
 */
public class ToolsAnnotation extends Annotation {
    
    private String annotationType;
    private String description;
    
    /** Creates a new instance of ToolsAnotation */
    private ToolsAnnotation (
        String annotationType,
        String description
    ) {
        this.annotationType = annotationType;
        this.description = description;
    }

    /** Returns name of the file which describes the annotation type.
     * The file must be defined in module installation layer in the
     * directory "Editors/AnnotationTypes"
     * @return  name of the anotation type
     */
    public String getAnnotationType () {
        return annotationType;
    }

    /** Returns the tooltip text for this annotation.
     * @return  tooltip for this annotation
     */
    public String getShortDescription () {
        return description;
    }
    
    static Map annotations = new HashMap ();
    
    static void addAnnotation (
        Annotatable annotatable,
        String key, 
        int type, 
        String description
    ) {
        System.out.println("addAnnotation " + key + " : " + type + " : " + description);
        String annotationType = null;
        switch (type) {
            case ExternalTool.TASK_ANNOTATION: 
                annotationType = "Task";break;
            case ExternalTool.SUGGESTION_ANNOTATION: 
                annotationType = "Suggestion";break;
            case ExternalTool.ERROR_ANNOTATION: 
                annotationType = "org-netbeans-modules-java-parser_annotation_err";break;
            case ExternalTool.WARNING_ANNOTATION: 
                annotationType = "org-netbeans-modules-java-parser_annotation_warn";break;
        }
        Annotation annotation = new ToolsAnnotation (
            annotationType, description
        );
        annotation.attach (annotatable);
        List l = (List) annotations.get (key);
        if (l == null) {
            l = new ArrayList ();
            annotations.put (key, l);
        }
        l.add (annotation);
    }
    
    static void removeAllAnnotations (String key) {
        System.out.println("removeAllAnnotations " + key);
        List l = (List) annotations.remove (key);
        if (l == null) return;
        Iterator it = l.iterator ();
        while (it.hasNext ())
            ((Annotation) it.next ()).detach ();
    }
}

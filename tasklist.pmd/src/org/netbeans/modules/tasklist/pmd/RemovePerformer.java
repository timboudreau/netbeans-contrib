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

package org.netbeans.modules.tasklist.pmd;

import net.sourceforge.pmd.RuleViolation;
import pmd.*;
import org.netbeans.api.tasklist.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import org.openide.text.NbDocument;
import org.openide.cookies.SourceCookie;
import org.openide.explorer.view.*;
import org.openide.nodes.*;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.DataEditorSupport;
import org.openide.util.NbBundle;
import org.openide.src.*;


import org.netbeans.modules.tasklist.core.TLUtils;
//import org.netbeans.modules.tasklist.core.ConfPanel;

/**
 * Perform method removal confirmation and execution
 * <p>
 * @author Tor Norbye
 */


public class RemovePerformer implements SuggestionPerformer {
    private Line line;
    private RuleViolation violation;
    private boolean field;

    /** "comment" parameter not yet implemented (this will allow
        you to comment out rather than just delete some code
        @param field when true, remove a field instead of a method
    */
    RemovePerformer(boolean field, Line line, RuleViolation violation,
                    boolean comment) {
        this.line = line;
        this.violation = violation;
        this.field = field;
    }

    public void perform(Suggestion s) {
        if (field) {
            FieldElement el = findField();
            if (el != null) {
                ClassElement cl = el.getDeclaringClass();
                try {
                    cl.removeField(el);
                } catch (SourceException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                }
            }
        } else {
            MethodElement el = findMethod();
            if (el != null) {
                ClassElement cl = el.getDeclaringClass();
                try {
                    cl.removeMethod(el);
                } catch (SourceException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                }
            }
        }
    }

    private MethodElement findMethod() {
        String desc = violation.getDescription();
        // HACK - use the name of the method as reported by PMD
        // to limit the class search. If we fail to find it,
        // the search will only be line number based, which may not
        // be as accurate (in case there are multiple elements on the
        // same line.
        int idx = desc.indexOf("such as '");
        String method = null;
        if (idx != -1) {
            int edix = desc.indexOf('\'', idx+9);
            if (edix != -1) {
                method = desc.substring(idx+9, edix);
            } else {
                method = desc.substring(idx+9);
            }
        }

        DataObject dobj = DataEditorSupport.findDataObject(line);
        SourceCookie sc = (SourceCookie)dobj.getCookie(SourceCookie.class);
        if (sc == null) {
            return null; // shouldn't happen
        }
        MethodElement m = null;
        SourceElement se = sc.getSource();
        if ( se != null ) {
            ClassElement[] ces = se.getAllClasses();
            for( int j = 0; j < ces.length; j++ ) {
                m = findMethod(method, ces[j], dobj);
                if (m != null) {
                    break;
                }
                ClassElement[] inner = ces[j].getClasses();
                for( int k = 0; k < inner.length; k++ ) {
                    m = findMethod(method, inner[k], dobj);
                    if (m != null) {
                        break;
                    }
                }
                if (m != null) {
                    break;
                }
            }
        }
        return m;
    }
    
    private MethodElement findMethod(String method, ClassElement cl,
                                     DataObject dobj) {
        MethodElement[] methods = cl.getMethods();
        for (int i = 0; i < methods.length; i++) {
            // If I've got a method name extracted from the rule,
            // try to match it, otherwise just look for source position
            if ((method == null) || 
                (methods[i].getName().getName().equals(method))) {
                // Possible candidate. Check for source position
                Line l = getLine(methods[i], dobj);
                if ((l != null) && (l == line)) {
                    return methods[i];
                } // else : Found the method, but not the right line.
            }
        }
        return null;
    }
    
    private FieldElement findField() {
        String desc = violation.getDescription();
        // HACK - use the name of the field as reported by PMD
        // to limit the class search. If we fail to find it,
        // the search will only be line number based, which may not
        // be as accurate (in case there are multiple elements on the
        // same line.
        int idx = desc.indexOf("such as '");
        String field = null;
        if (idx != -1) {
            int edix = desc.indexOf('\'', idx+9);
            if (edix != -1) {
                field = desc.substring(idx+9, edix);
            } else {
                field = desc.substring(idx+9);
            }
        }

        DataObject dobj = DataEditorSupport.findDataObject(line);
        SourceCookie sc = (SourceCookie)dobj.getCookie(SourceCookie.class);
        if (sc == null) {
            return null; // shouldn't happen
        }
        FieldElement m = null;
        SourceElement se = sc.getSource();
        if ( se != null ) {
            ClassElement[] ces = se.getAllClasses();
            for( int j = 0; j < ces.length; j++ ) {
                m = findField(field, ces[j], dobj);
                if (m != null) {
                    break;
                }
                ClassElement[] inner = ces[j].getClasses();
                for( int k = 0; k < inner.length; k++ ) {
                    m = findField(field, inner[k], dobj);
                    if (m != null) {
                        break;
                    }
                }
                if (m != null) {
                    break;
                }
            }
        }
        return m;
    }
    
    private FieldElement findField(String field, ClassElement cl,
                                     DataObject dobj) {
        FieldElement[] fields = cl.getFields();
        for (int i = 0; i < fields.length; i++) {
            // If I've got a field name extracted from the rule,
            // try to match it, otherwise just look for source position
            if ((field == null) || 
                (fields[i].getName().getName().equals(field))) {
                // Possible candidate. Check for source position
                Line l = getLine(fields[i], dobj);
                if ((l != null) && (l == line)) {
                    return fields[i];
                } // else : Found the field, but not the right line.
            }
        }
        return null;
    }
    
    private Line getLine(MethodElement el, DataObject dobj) {
        SourceCookie.Editor editor = 
            (SourceCookie.Editor)dobj.getCookie(SourceCookie.Editor.class);
        javax.swing.text.Element textElement = editor.sourceToText(el);
        if (textElement != null) {
            StyledDocument document = editor.getDocument();
            if (document != null) {
                int offset = textElement.getStartOffset();
                
                // If a method has javadoc, we get the position of the
                // javadoc, not the beginning of the method declaration -
                // and it's this second number that PMD is reporting to
                // us. So we have to compute where the method really begins.
                int bias = 0;
                JavaDoc.Method jdm = el.getJavaDoc();
                if (jdm != null) {
                    String raw = jdm.getRawText();
                    if (raw != null) {
                        bias++;
                        for (int i = 0; i < raw.length(); i++) {
                            if (raw.charAt(i) == '\n') {
                                bias++;
                            }
                        }
                    }
                }
                
                int lineNumber = NbDocument.findLineNumber(document, offset)+bias;
                Line line = editor.getLineSet().getCurrent(lineNumber);
                return line;
            }
        }
        return null;
    }


    private String getMethodText() {
        MethodElement el = findMethod();
        if (el == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        ElementPrinter p = new DefaultElementPrinter(new PrintWriter(sw));
        try {
            el.print(p);
        } catch (ElementPrinterInterruptException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
        String source = sw.toString();
        return source;
    }


    private Line getLine(FieldElement el, DataObject dobj) {
        SourceCookie.Editor editor = 
            (SourceCookie.Editor)dobj.getCookie(SourceCookie.Editor.class);
        javax.swing.text.Element textElement = editor.sourceToText(el);
        if (textElement != null) {
            StyledDocument document = editor.getDocument();
            if (document != null) {
                int offset = textElement.getStartOffset();
                
                // If a field has javadoc, we get the position of the
                // javadoc, not the beginning of the field declaration -
                // and it's this second number that PMD is reporting to
                // us. So we have to compute where the field really begins.
                int bias = 0;
                JavaDoc.Field jdm = el.getJavaDoc();
                if (jdm != null) {
                    String raw = jdm.getRawText();
                    if (raw != null) {
                        bias++;
                        for (int i = 0; i < raw.length(); i++) {
                            if (raw.charAt(i) == '\n') {
                                bias++;
                            }
                        }
                    }
                }
                
                int lineNumber = NbDocument.findLineNumber(document, offset)+bias;
                Line line = editor.getLineSet().getCurrent(lineNumber);
                return line;
            }
        }
        return null;
    }


    private String getFieldText() {
        FieldElement el = findField();
        if (el == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        ElementPrinter p = new DefaultElementPrinter(new PrintWriter(sw));
        try {
            el.print(p);
        } catch (ElementPrinterInterruptException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
        String source = sw.toString();
        return source;
    }


    public boolean hasConfirmation() {
        return true;
    }
    public Object getConfirmation(Suggestion s) {
        DataObject dao = DataEditorSupport.findDataObject(line);
        int linenumber = line.getLineNumber();
        String filename = dao.getPrimaryFile().getNameExt();
        String ruleDesc = violation.getRule().getDescription();
        String ruleExample = violation.getRule().getExample();
        StringBuffer sb = new StringBuffer(2000);
        String beforeContents = null;
        String afterContents = null;
        String afterDesc = null;
        String beforeDesc = null;
        //if (comment) {
            // Not yet implemented
        //} else {
            beforeDesc = NbBundle.getMessage(RemovePerformer.class,
                                "RemoveUnusedMethod"); // NOI18N
            sb.append("<html>"); // NOI18N

            // HACK: I also noticed that "/** Javadoc here"
            // wouldn't correctly draw the first line, so
            // hack around it by putting some useless
            // attributes in there.
            sb.append("<b></b>");

            if (field) {
                TLUtils.appendHTMLString(sb, getFieldText());
            } else {
                TLUtils.appendHTMLString(sb, getMethodText());
            }
            sb.append("</html>"); // NOI18N
            beforeContents = sb.toString();
        //}
        
        return new ConfPanel(beforeDesc, 
                             beforeContents, afterDesc, 
                             afterContents,
                             filename, linenumber);
        /*
                             ViolationProvider.getBottomPanel(ruleDesc, 
                                                              ruleExample));
        */
    }
}

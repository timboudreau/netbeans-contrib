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

package org.netbeans.modules.portalpack.commons.palette.jsp;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseDocument;
// not for Nb6 anmore import org.netbeans.editor.Formatter;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.TopComponent;

/**
 *
 * @author Satyaranjan
 */
public final class PaletteUtilities {
    
    public static DataObject getDataObject( JTextComponent target){
        
        Component c =  target.getParent();
        while ((c!=null) && (!(c instanceof TopComponent))){
            c= c.getParent();
        }
        
        TopComponent tc = (TopComponent)c;

        
        DataObject dos = (DataObject)tc.getLookup().lookup(DataObject.class);
       
        return dos;
    }
    
    public static String readResource(InputStream is, String encoding) throws IOException {
        // read the config from resource first
        StringBuffer sb = new StringBuffer();
        String lineSep = System.getProperty("line.separator");//NOI18N
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            sb.append(lineSep);
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }    
    
    public static void createFile(FileObject target, String content, String encoding) throws IOException{            
        FileLock lock = target.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), encoding));
            bw.write(content);
            bw.close();

        }
        finally {
            lock.releaseLock();
        }
    }
    public static String getKitClassName(JTextComponent target){
        String n="";
        try{
            n= ((BaseDocument)target.getDocument()).getKitClass().getName();
        }catch (Exception e){
            ErrorManager.getDefault().log(ErrorManager.ERROR, "Cannot get a good kitName for "+target); //NOI18N
            n="";
        }
        return n;
    }
    
    public static String getDocumentExtensionName(JTextComponent target){
        String ext="";
        try{
            DataObject dob= getDataObject(target);
            ext= dob.getPrimaryFile().getExt();
        }catch (Exception e){
            ErrorManager.getDefault().log(ErrorManager.ERROR, "Cannot get a good extension name for "+target); //NOI18N
        }
        return ext;
    }
    
    public static void insert(String s, JTextComponent target)
    throws BadLocationException {
        insert(s, target, true);
    }
    
    public static void insert(String s, JTextComponent target, boolean reformat)
    throws BadLocationException {
        Document doc = target.getDocument();
        if (doc == null)
            return;
        
        //check whether we are not in a scriptlet
//        JspSyntaxSupport sup = (JspSyntaxSupport)(doc.getSyntaxSupport().get(JspSyntaxSupport.class));
//        int start = target.getCaret().getDot();
//        TokenItem token = sup.getTokenChain(start, start + 1);
//        if (token != null && token.getTokenContextPath().contains(JavaTokenContext.contextPath)) // we are in a scriptlet
//            return false;
        
        if (s == null)
            s = "";
        
        if (doc instanceof BaseDocument)
            ((BaseDocument)doc).atomicLock();
        
        int start = insert(s, target, doc);
        
//        if (reformat && start >= 0 && doc instanceof BaseDocument) {  // format the inserted text
//            BaseDocument d = (BaseDocument) doc;
//            int end = start + s.length();
//            Formatter f = d.getFormatter();
//            
//            //f.reformat(d, start, end);
//            f.reformat(d, 0,d.getLength());
//        }
        
//        if (select && start >= 0) { // select the inserted text
//            Caret caret = target.getCaret();
//            int current = caret.getDot();
//            caret.setDot(start);
//            caret.moveDot(current);
//            caret.setSelectionVisible(true);
//        }
        
        if (doc instanceof BaseDocument)
            ((BaseDocument)doc).atomicUnlock();
        
        Component c =  target.getParent();
        //     System.out.println(""+c.getClass().getName());
        while ((c!=null) && (!(c instanceof TopComponent))){
            c= c.getParent();
        }
        
        TopComponent tc = (TopComponent)c;
        tc.requestActive();
        
        DataObject dos = (DataObject)tc.getLookup().lookup(DataObject.class);

    }
    
    private static int insert(String s, JTextComponent target, Document doc)
    throws BadLocationException {
        
        int start = -1;
        try {
            //at first, find selected text range
            Caret caret = target.getCaret();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());
            doc.remove(p0, p1 - p0);
            
            //replace selected text by the inserted one
            start = caret.getDot();
            doc.insertString(start, s, null);
        } catch (BadLocationException ble) {}
        
        return start;
    }
    
    
    public  static void insertLibraryDefinition(String libName, JTextComponent target)
    throws BadLocationException {
        Document doc = target.getDocument();
        if (doc == null)
            return;
        if (doc instanceof BaseDocument){
            BaseDocument bd=  (BaseDocument)doc;
            
            bd.atomicLock();
            Caret caret = target.getCaret();
            char c[]= bd.getChars(0,bd.getLength());
            
            String s = new String(c);
            // getting substring -7 to allow for the JSF taglib def to work 
            String lName = libName;
            if (libName.length() > 7) lName = libName.substring(0, libName.length() -7);
            if (s.indexOf(lName) ==-1){
              bd.insertString(0, libName+"\n", null);                
            }
            
            bd.atomicUnlock();
        }
        return ;
    }    
}

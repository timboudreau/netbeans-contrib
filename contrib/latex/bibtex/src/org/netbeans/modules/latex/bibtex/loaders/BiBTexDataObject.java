/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.loaders;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.StringTokenizer;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

import org.openide.util.Lookup;

/** Represents a My object in the Repository.
 *
 * @author Jan Lahoda
 */
public class BiBTexDataObject extends MultiDataObject {
    
    public static final String ENCODING_ATTRIBUTE_NAME = "Content-Encoding";
    public static final String ENCODING_PROPERTY_NAME  = "encoding";
    public static final String LOCALE_PROPERTY_NAME    = "locale";
    public static final String LOCALE_ATTRIBUTE        = "locale-attribute";
    
    public BiBTexDataObject(FileObject pf, MyDataLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        init();
    }
    
    private void init() {
        CookieSet cookies = getCookieSet();
        cookies.add(new BiBTexEditorSupport(this));
        // Add whatever capabilities you need, e.g.:
        
//        cookies.add(new TexExecutionSupport(getPrimaryEntry()));
        
        /* // See Editor Support template in Editor API:
        cookies.add(new MyEditorSupport(this));
        cookies.add(new CompilerSupport.Compile(getPrimaryEntry()));
        cookies.add(new CompilerSupport.Build(getPrimaryEntry()));
        cookies.add(new CompilerSupport.Clean(getPrimaryEntry()));
        cookies.add(new OpenCookie() {
            public void open() {
                // do something...but usually you want to use OpenSupport instead
            }
        });
         */
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you add context help, change to:
        // return new HelpCtx(MyDataObject.class);
    }
    
    protected Node createNodeDelegate() {
        return new MyDataNode(this);
    }
    
    /* If you made an Editor Support you will want to add these methods:
     
    final void addSaveCookie(SaveCookie save) {
        getCookieSet().add(save);
    }
     
    final void removeSaveCookie(SaveCookie save) {
        getCookieSet().remove(save);
    }
     
     */
    
    public String getCharSet() {
        String encoding = (String ) getPrimaryFile().getAttribute(ENCODING_ATTRIBUTE_NAME);
        
        if (encoding == null) {
            encoding = "";
        }
        
        return encoding;
    }
    
    public void setCharSet(String nue) throws IOException {
        if (!Charset.isSupported(nue)) {
            UnsupportedEncodingException e =  new UnsupportedEncodingException("Encoding " + nue + " is not supported.");
            
            ErrorManager.getDefault().annotate(e, ErrorManager.USER, null, null, null, null);
            
            throw e;
        }

        String old  = getCharSet();
        
        getPrimaryFile().setAttribute(ENCODING_ATTRIBUTE_NAME, nue);
        
        firePropertyChange(ENCODING_PROPERTY_NAME, old, nue);
    }

    public Locale getLocaleImpl() {
        return (Locale) getPrimaryFile().getAttribute(LOCALE_ATTRIBUTE);
    }
    
    public String getLocale() {
        Locale locale = getLocaleImpl();
        
        if (locale == null)
            return "";
        
        return locale.toString();
    }
    
    public void setLocale(String nue) throws IOException {
        String language = "";
        String country = "";
        String variant = "";
        
        StringTokenizer stok = new StringTokenizer(nue, "_");
        
        language = stok.nextToken();
        
        if (stok.hasMoreTokens()) {
            country = stok.nextToken();
            
            if (stok.hasMoreTokens())
                variant = stok.nextToken();
        }
        
        Locale nueLocale = new Locale(language, country, variant);
        Locale old  = getLocaleImpl();
        
        getPrimaryFile().setAttribute(LOCALE_ATTRIBUTE, nueLocale);
        
        firePropertyChange(ENCODING_PROPERTY_NAME, old, nueLocale);
    }

    public void setLocaleImpl(Locale nueLocale) throws IOException {
        Locale old  = getLocaleImpl();
        
        getPrimaryFile().setAttribute(LOCALE_ATTRIBUTE, nueLocale);
        
        firePropertyChange(ENCODING_PROPERTY_NAME, old, nueLocale);
    }

    
    /*package*/ void addSaveCookie(Node.Cookie cookie) {
        getCookieSet().add(cookie);
    }
    
    /*package*/ void removeSaveCookie(Node.Cookie cookie) {
        getCookieSet().remove(cookie);
    }
    
//    private static class TexExecutionSupport extends ExecutionSupport {
//        
//        public TexExecutionSupport(Entry entry) {
//            super(entry);
//        }
//        
//        /**Tries to find the TexExecutor and return it as the default executor for TeX DataObjects.
//         *
//         * @returns default executor (ideally TexExecutor).
//         */
//        protected Executor defaultExecutor() {
//            Executor texExecutor = (Executor) Lookup.getDefault().lookup(TexExecutor.class);
//            
//            if (texExecutor == null)
//                return super.defaultExecutor();
//            else
//                return texExecutor;
//        }
//        
//    }
    
}

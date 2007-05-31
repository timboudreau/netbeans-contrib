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

package org.netbeans.core.registry;

import org.netbeans.spi.registry.BasicContext;
import org.netbeans.core.registry.ConvertorBinding;
import org.netbeans.core.registry.InstanceBinding;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.lang.ref.WeakReference;

/**
 * <P>
 * Uses subclasses of ObjectBinding.Reader and ObjectBinding.Writer: <UL>
 * <LI>ConvertorBinding</LI>
 * <LI>InstanceBinding</LI>
 * <LI>ShadowBinding</LI>
 * <LI>ObjectRefBinding</LI>
 * <LI>SettingBinding</LI>
 * </UL>
 * </P>
 * 
 * (copy & pasted & refactored from original org.netbeans.core.registry.ObjectBinding)   
 */ 
abstract class ObjectBinding {
    private static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.core.registry"); // NOI18N    
    private static final Reader[] READERS =
            new Reader[]{InstanceBinding.READER, ShadowBinding.READER,
                         ObjectRefBinding.READER, SettingBinding.READER,
                         ConvertorBinding.READER, XMLBasedBinding.READER};

    private static final Writer[] WRITERS =
            new Writer[]{ObjectRefBinding.WRITER, ConvertorBinding.WRITER,
                         SettingBinding.WRITER};

    private FileObject file;
    private WeakReference cachedInstance = null;


    ObjectBinding(FileObject fo) {
        this.file = fo;
    }

    /**
     * @return may return null
     */ 
    BasicContext getContext () {
        return null;
    }
    
    public static Collection getFileExtensions() {
        Set s = new HashSet(READERS.length);
        for (int i = 0; i < READERS.length; i++) {
            Reader reader = READERS[i];
            s.add(reader.getFileExtension());
        }
        return Collections.unmodifiableCollection(s);
    }

    public static ObjectBinding get(BasicContext ctx, FileObject fo) {
        if (fo == null || !fo.isValid() || fo.isFolder()) return null;
        
        Reader r = getReader(fo);
        
        if (r == null) {
            ErrorManager errorManager = getErrorManager();
            if (errorManager.isLoggable(ErrorManager.INFORMATIONAL)) {
                errorManager.log("File " + fo + " was parsed, but it is not a .settings file nor namespace-aware XML. It will be ignored.");
            }
        }
        
        return (r != null) ? r.getObjectBinding(ctx, fo) : null;
    }
    
    public void delete () throws IOException {
        FileObject file = getFile();
        if (file != null)
            file.delete();
    }

    public ObjectBinding write(final Object object) throws IOException {
        Writer w = getWriter(object);
        if (w != null) {
            w.write(getFile(), object);
        }
        
        Reader r = (w != null) ? getReader(getFile()) : null;        
        ObjectBinding retVal = null;
        if (r != null) {
            retVal = r.getObjectBinding(getContext(),getFile ());
            retVal.cachedInstance = new WeakReference(object);            
        }
        return retVal;
    }

    public static ObjectBinding write(BasicContext ctx, FileObject folder, String bindingName, Object object) throws IOException {
        Writer w = getWriter(object);
        FileObject fo = null;
        if (w != null) {
            fo = w.write(folder, bindingName, object);
        }

        Reader r = (fo != null) ? getReader(fo) : null;        
        ObjectBinding retVal = null;
        if (r != null) {
            retVal = r.getObjectBinding(ctx,fo);
            retVal.cachedInstance = new WeakReference(object);            
        }
        return retVal;
    }

    /**
     * Provides module descriptor that just implements method equals.  
     * Method hashCode may NOT BE PROPERLY IMPLEMENTED !!!
     * @return module descriptor 
     */ 
    public Object getModuleDescriptor() {
        return getDefaultModuleDescriptor();
    }

    private static Object getDefaultModuleDescriptor() {
        return new Object() {
            public boolean equals(Object obj) {
                return false;
            }
        };
    }


    private static Reader getReader(FileObject fo) {
        for (int i = 0; i < READERS.length; i++) {
            if (READERS[i].canRead(fo)) {
                return READERS[i];
            }
        }
        return null;
    }

    private static Writer getWriter(Object obj) {
        for (int i = 0; i < WRITERS.length; i++) {
            if (WRITERS[i].canWrite(obj)) {
                return WRITERS[i];
            }

        }
        return null;
    }

    /**
     * @return may return null
     */     
    public final FileObject getFile() {
        return file;
    }


    public String getBindingName() {
        return file.getName();
    }

    public final Object getObject() throws IOException{
        Object retVal = null;
        if (cachedInstance == null || (retVal = cachedInstance.get()) == null) {
            retVal = createInstance();
            cachedInstance = new WeakReference(retVal);
        }

        return retVal;
    }


    public boolean isBinding(String bindingName) {
        return getBindingName().equals(bindingName);
    }

    abstract public Object createInstance() throws IOException;

    abstract public boolean isEnabled();

    public static final ErrorManager getErrorManager() {
        return err;
    }

    abstract static class Reader {
        abstract boolean canRead(FileObject fo);

        abstract String getFileExtension();

        abstract ObjectBinding getObjectBinding(BasicContext ctx, FileObject fo);
    }

    abstract static class Writer {
        abstract boolean canWrite(Object obj);

        abstract void write(FileObject fo, Object obj) throws IOException;

        abstract String getFileExtension();

        final FileObject write(FileObject folder, String bindingName, Object obj) throws IOException {
            FileObject fo;
            synchronized (this) {
                fo = folder.getFileObject(bindingName, getFileExtension());
                if (fo == null) {
                    fo = folder.createData(bindingName, getFileExtension());
                }
            }
            this.write(fo, obj);
            return fo;
        }
    }
}

package com.sun.jbi.sapbc.sapwsdlgenerator.explorer;

import com.sun.jbi.sapbc.sapwsdlgenerator.explorer.util.FilePropertyEditor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.Node;

/**
 * Bean Property representation of a SAP Components Library file.
 */
class LibraryPathProperty
    extends Node.Property
    implements PropertyChangeListener {
        
    public LibraryPathProperty(File file) {
        super(File.class);
        
        if (file == null) {
            throw new NullPointerException("file");
        }
        
        if (!file.isFile()) {
            throw new IllegalArgumentException(
                "cannot access file " + file.getAbsolutePath());
        }
        
        editor = new FilePropertyEditor();
        editor.setValue(file);
        editor.addPropertyChangeListener(this);
        this.path = file;
    }

    public boolean canRead() {
        return true;
    }

    public Object getValue()
        throws IllegalAccessException,
               InvocationTargetException {
        return editor.getValue();
    }

    public boolean canWrite() {
        return true;
    }

    public void setValue(Object object)
        throws IllegalAccessException,
               IllegalArgumentException,
               InvocationTargetException {

        if (object == null || !(object instanceof File)) {
            throw new IllegalArgumentException("value not a File object");
        }

        File file = (File) object;

        if (!file.isFile()) {
            throw new IllegalAccessException(
                "cannot access file " + file.getAbsolutePath());
        }

        editor.setValue(file);
        this.path = file;
    }

    public PropertyEditor getPropertyEditor() {
        return editor;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        File value = (File) editor.getValue();
        File oldValue = this.path;
        SAPComponentsNotifier.notifyLibraryRenamed(oldValue, value);
    }

    protected void finalize() throws Throwable {
        editor.removePropertyChangeListener(this);
    }
    
    private final FilePropertyEditor editor;
    private File path;
}

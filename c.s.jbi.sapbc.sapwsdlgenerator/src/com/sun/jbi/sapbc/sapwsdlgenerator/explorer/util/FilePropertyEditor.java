package com.sun.jbi.sapbc.sapwsdlgenerator.explorer.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.util.ResourceBundle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 * PropertyEditor implementation for File property values that uses a
 * file chooser as its editor.
 */
public class FilePropertyEditor
    extends PropertyEditorSupport
    implements ActionListener {

    public FilePropertyEditor() {
        editor = new FileSelector();
    }

    public void setValue(Object value) {
        if (value instanceof File) {
            this.value = (File) value;
            editor.setLibraryPath(this.value);
        } else {
            this.value = new File(value != null ? value.toString() : "");
            editor.setLibraryPath(this.value);
        }
    }

    public Object getValue() {
        return value;
    }

    public boolean isPaintable() {
        return false;
    }

    public void paintValue(Graphics gfx, Rectangle box) {
        // NO-OP; not paintable
    }

    public String getJavaInitializationString() {
        String path = (value != null ? value.getPath() : "");
        StringBuffer buffer = new StringBuffer();
        buffer.append("new File(\"");
        buffer.append(path);
        buffer.append("\")");
        return buffer.toString();
    }

    public String getAsText() {
        return editor.getLibraryPath().getAbsolutePath();
    }

    public void setAsText(String text) throws IllegalArgumentException {
        setValue(text);
    }

    public String[] getTags() {
        return null;
    }

    public Component getCustomEditor() {
        DialogDescriptor desc = new DialogDescriptor(
            editor.getComponent(),
            bundle.getString("FilePropertyEditor.edit_prompt_title"),
            true,
            this);
        
        Dialog dlg = DialogDisplayer.getDefault().createDialog(desc);
        return dlg;
    }

    public boolean supportsCustomEditor() {
        return true;
    }

    public void actionPerformed(ActionEvent evt) {
        if (DialogDescriptor.OK_OPTION.equals(evt.getActionCommand())) {
            value = editor.getLibraryPath();
            firePropertyChange();
        }
    }

    public static final String VALUE_PROP = "FilePropertyEditor.VALUE";
    
    private static final ResourceBundle bundle =
        NbBundle.getBundle(FilePropertyEditor.class);
    
    private File value;
    private final FileSelector editor;
}

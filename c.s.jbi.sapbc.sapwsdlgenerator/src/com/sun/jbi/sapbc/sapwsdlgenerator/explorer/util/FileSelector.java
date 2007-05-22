package com.sun.jbi.sapbc.sapwsdlgenerator.explorer.util;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Represents a mutable file selection.
 */
public class FileSelector implements PropertyChangeListener {
    
    public FileSelector() {
    }

    public File getLibraryPath() {
        return selection;
    }
    
    public void setLibraryPath(File path) {
        if (path.isFile()) {
            if (selectionPanel == null) {
                initPanel();
            }
            selection = path;
            selectionPanel.setInitialTarget(path);
        }
    }

    public Component getComponent() {
        if (selectionPanel == null) {
            initPanel();
        }
        return selectionPanel;
    }
    
    private void initPanel() {
        if (selectionPanel == null) {
            selectionPanel = new FileSelectorPanel();
            selectionPanel.addPropertyChangeListener(
                FileSelectorPanel.SELECTION_PROP, this);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (FileSelectorPanel.SELECTION_PROP.equals(propertyName)) {
            selection = (File) evt.getNewValue();
        }
    }

    private File selection;
    private FileSelectorPanel selectionPanel;
}

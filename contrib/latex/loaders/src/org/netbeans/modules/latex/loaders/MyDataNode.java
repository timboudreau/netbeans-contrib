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
package org.netbeans.modules.latex.loaders;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Locale;
import javax.swing.Icon;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.LaTeXSourceFactory;

import org.openide.ErrorManager;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
//import org.openide.loaders.ExecutionSupport;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.latex.model.structural.Model;
import org.netbeans.modules.latex.model.structural.ModelListener;
import org.netbeans.modules.latex.model.structural.StructuralElement;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/** A node to represent this object.
 *
 * @author Jan Lahoda
 */
public class MyDataNode extends DataNode implements LaTeXSourceFactory.MainFileListener {
    
    public static final String TEXT_SET = "Text";
    
    private LaTeXSourceFactory.MainFileListener factoryListener;
    
    public MyDataNode(TexDataObject obj) {
        this(obj, new TexChildren(obj));
    }
    
    protected MyDataNode(TexDataObject obj, Children ch) {
        super(obj, ch);
        setIconBase("org/netbeans/modules/latex/loaders/MyDataIcon");
        
        LaTeXSourceFactory fact = (LaTeXSourceFactory) Lookup.getDefault().lookup(LaTeXSourceFactory.class);
        
        //TODO: the LaTeXSourceFactory in the lookup may change over time, so listen on the result!
        fact.addPropertyChangeListener(factoryListener = LaTeXSourceFactory.weakMainFileListener(this, fact));
    }
    
    private LaTeXSourceFactory getFactory() {
        return (LaTeXSourceFactory) Lookup.getDefault().lookup(LaTeXSourceFactory.class);
    }
    
    private boolean isMainFile(Object file) {
        return getFactory().isMainFile(file);
    }
    
    private boolean isKnownFile(Object file) {
        return getFactory().isKnownFile(file);
    }
    
    public String getHtmlDisplayName() {
        String dName = super.getHtmlDisplayName();
        
        if (isKnownFile(getDataObject().getPrimaryFile())) {
            if (isMainFile(getDataObject().getPrimaryFile())) {
                return "<html><body><b>" + dName + "</b></body></html>"; //TODO: more efficiently?
            } else {
                return "<html><body><b>" + dName + "</b></body></html>"; //TODO: more efficiently?
            }
        }
        
        return dName;
    }
    
    public Image getIcon(int type) {
        Image sup = super.getIcon(type);
        String badge = null;
            
        if (isKnownFile(getDataObject().getPrimaryFile())) {
            if (isMainFile(getDataObject().getPrimaryFile())) {
                badge = "org/netbeans/modules/latex/loaders/latex_main_file_badge.gif";
            } else {
                badge = "org/netbeans/modules/latex/loaders/latex_incl_file_badge.gif";
            }
        }
        
        if (badge != null)
            sup = org.openide.util.Utilities.mergeImages(sup, org.openide.util.Utilities.loadImage(badge), 16, 0);
        
        return sup;
    }
    
    protected TexDataObject getMyDataObject() {
        return (TexDataObject)getDataObject();
    }
    
//     Example of adding Executor / Debugger / Arguments to node:
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        
        Sheet.Set textSet = sheet.get(TEXT_SET);
        
        if (textSet == null) {
            textSet = new Sheet.Set();
            textSet.setName(TEXT_SET);
            textSet.setDisplayName(NbBundle.getMessage(MyDataNode.class, "LBL_DataNode_Text"));
            textSet.setShortDescription(NbBundle.getMessage(MyDataNode.class, "HINT_DataNode_Text"));
        }
        
        sheet.put(textSet);
        
        addTextProperties(textSet);
        
//        Sheet.Set set = sheet.get(ExecutionSupport.PROP_EXECUTION);
//        if (set == null) {
//            set = new Sheet.Set();
//            set.setName(ExecutionSupport.PROP_EXECUTION);
//            set.setDisplayName(NbBundle.getMessage(MyDataNode.class, "LBL_DataNode_exec_sheet"));
//            set.setShortDescription(NbBundle.getMessage(MyDataNode.class, "HINT_DataNode_exec_sheet"));
//        }
//        ((ExecutionSupport)getCookie(ExecutionSupport.class)).addProperties(set);
        
        // Maybe:
//        ((CompilerSupport)getCookie(CompilerSupport.class)).addProperties(set);
//        sheet.put(set);
        return sheet;
    }

    private void addTextProperties(Sheet.Set textSet) {
        textSet.put(createTextEncodingProperty());
        textSet.put(createLocaleProperty());
    }
    
    private PropertySupport createTextEncodingProperty() {
        return new PropertySupport.ReadWrite(
                  TexDataObject.ENCODING_PROPERTY_NAME,
                  String.class,
                  "Encoding",
                  "Encoding of the document") {
             public Object getValue() {
                 return ((TexDataObject) getDataObject()).getCharSet();
             }
             
             public void setValue(Object value) throws InvocationTargetException {
                 try {
                     ((TexDataObject) getDataObject()).setCharSet((String) value);
                 } catch (IOException e) {
                     throw new InvocationTargetException(e);
                 }
             }
             
             public boolean supportsDefaultValue() {
                 return true;
             }
             
             public void restoreDefaultValue() throws InvocationTargetException {
                 setValue(null);
             }
             
             public boolean canWrite() {
                 return getDataObject().getPrimaryFile().canWrite();
             }
        };
    }

    private PropertySupport createLocaleProperty() {
        return new PropertySupport.ReadWrite(
        TexDataObject.LOCALE_PROPERTY_NAME,
        String.class,
        "Locale",
        "Locale of the document") {
            public Object getValue() {
                return ((TexDataObject) getDataObject()).getLocale();
            }
            
            public void setValue(Object value) throws InvocationTargetException {
                try {
                    ((TexDataObject) getDataObject()).setLocale((String) value);
                } catch (IOException e) {
                    throw new InvocationTargetException(e);
                }
            }
            
            public boolean supportsDefaultValue() {
                return true;
            }
            
            public void restoreDefaultValue() throws InvocationTargetException {
                setValue(Locale.getDefault().toString());
            }
            
            public boolean canWrite() {
                return getDataObject().getPrimaryFile().canWrite();
            }
        };
    }

    public void mainFileChanged(LaTeXSourceFactory.MainFileEvent evt) {
        System.err.println("this=" + this);
        System.err.println("evt=" + evt);
        
        if (evt.getSource() == getFactory() && getDataObject().getPrimaryFile() == /*!!*/ evt.getMainFile()) {
            System.err.println("found, firing display name changes..");
            fireDisplayNameChange(null, null);//TODO: hopefully this updates the DN also from getHtmlDisplayName();
            fireIconChange();
        }
    }

//    public void propertyChange(PropertyChangeEvent evt) {
//        System.err.println("this=" + this);
//        System.err.println("evt=" + evt);
//        if (evt.getSource() == getFactory() && LaTeXSourceFactory.PROP_MAIN_FILES.equals(evt.getPropertyName())) {
//            System.err.println("found, firing display name changes..");
//            firePropertyChange(PROP_DISPLAY_NAME, null, null);
//            firePropertyChange(PROP_ICON, null, null);
//        }
//    }
    
    // Don't use getDefaultAction(); just make that first in the data loader's getActions list
    
    private static class TexChildren extends Children.Keys implements ModelListener {
        
        private DataObject mainFile;
        
        public TexChildren(DataObject mainFile) {
            this.mainFile = mainFile;
        }
        
        private FileObject getFileObject() {
            LaTeXSource source = LaTeXSource.get(mainFile.getPrimaryFile());
            
            if (source != null)
                return (FileObject) source.getMainFile();
            else
                return null;
        }
        
        protected Node[] createNodes(Object key) {
            if (key instanceof StructuralElement) {
                return new Node[] {StructuralNodeFactory.createNode((StructuralElement) key)};
            } else {
                try {
                    return new Node[] {new BeanNode(key)};
                } catch (IntrospectionException e) {
                    ErrorManager.getDefault().notify(e);
                    
                    return new Node[0];
                }
            }
        }
        
        
        private void setDocumentElement(StructuralElement el) {
            setKeys(new StructuralElement[] {el}); //!!!!!
        }
        
        public void addNotify() {
            setKeys(new Object[] {new Waiting()});
            Model.getDefault().addModelListener(getFileObject(), this);
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    StructuralElement el = Model.getDefault().getModel(getFileObject());
                    
                    if (el != null)
                        setDocumentElement(el);
                    //The possibility that the model may be out-of-date is ignored for performance reasons.
                }
            });
        }
        
        public void removeNotify() {
            Model.getDefault().removeModelListener(getFileObject(), this);
            setKeys(Collections.EMPTY_SET);
            //Remove listners.
        }
        
        public void modelChanged(FileObject mainFile) {
//            System.err.println("Model changed, obj=" + obj);
            setDocumentElement(Model.getDefault().getModel(mainFile));
        }
        
        private static class Waiting {
            public String getName() {
                return "Parsing...";
            }
        }
        
    }
    
}

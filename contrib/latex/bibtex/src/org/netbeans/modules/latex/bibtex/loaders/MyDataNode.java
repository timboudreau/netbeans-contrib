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

import org.netbeans.modules.latex.bibtex.BiBEntryNode;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Locale;
import javax.swing.Icon;
import org.netbeans.modules.latex.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.bibtex.BiBTeXModelChangeListener;
import org.netbeans.modules.latex.bibtex.BiBTeXModelChangedEvent;
import org.netbeans.modules.latex.bibtex.Entry;
import org.netbeans.modules.latex.bibtex.PublicationEntry;

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

import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/** A node to represent this object.
 *
 * @author Jan Lahoda
 */
public class MyDataNode extends DataNode {
    
    public static final String TEXT_SET = "Text";//NOI18N
    
    public MyDataNode(BiBTexDataObject obj) {
        this(obj, new Children(obj.getPrimaryFile()));
    }
    
    protected MyDataNode(BiBTexDataObject obj, Children ch) {
        super(obj, ch);
        setIconBase("org/netbeans/modules/latex/bibtex/loaders/bib_file_icon");//NOI18N
    }
    
    protected BiBTexDataObject getMyDataObject() {
        return (BiBTexDataObject)getDataObject();
    }
    
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
        
        return sheet;
    }

    private void addTextProperties(Sheet.Set textSet) {
        textSet.put(createTextEncodingProperty());
    }
    
    private PropertySupport createTextEncodingProperty() {
        return new PropertySupport.ReadWrite(
                  BiBTexDataObject.ENCODING_PROPERTY_NAME,
                  String.class,
                  org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Encoding"),
                  org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Encoding_of_the_document")) {
             public Object getValue() {
                 return ((BiBTexDataObject) getDataObject()).getCharSet();
             }
             
             public void setValue(Object value) throws InvocationTargetException {
                 try {
                     ((BiBTexDataObject) getDataObject()).setCharSet((String) value);
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

    private static class Children extends org.openide.nodes.Children.Keys implements BiBTeXModelChangeListener {
        
        private FileObject source;
        private BiBTeXModelChangeListener listener; 
        private boolean initialized;
        
        public Children(FileObject source) {
//            System.err.println("source = " + source );
            this.source = source;
            initialized = false;
        }
        
        public void addNotify() {
            //TODO: deffer into another thread:
            BiBTeXModel model = BiBTeXModel.getModel(source);
            
            setKeys(model.getEntries());
            model.addBiBTexModelChangeListener(listener = BiBTeXModel.createWeakListeners(this, model));
            initialized = true;
//            System.err.println("addNotify");
        }
        
        public Node[] createNodes(Object key) {
//            System.err.println("createNodes(" + key + ")");
            Node created = null;
            
            if (key instanceof PublicationEntry) {
                created = new PublicationEntryNode((PublicationEntry) key, source);
            }
            
            if (created == null) {
                created = new BiBEntryNode((Entry) key, source);
            }

            return new Node[] {created};
        }
        
        public void removeNotify() {
            setKeys(Collections.EMPTY_LIST);
            //free the listener:
            listener = null;
            initialized = false;
        }
        
        public void entriesRemoved(BiBTeXModelChangedEvent event) {
            if (initialized)
                setKeys(((BiBTeXModel) event.getSource()).getEntries());
        }
        
        public void entriesAdded(BiBTeXModelChangedEvent event) {
            if (initialized)
                setKeys(((BiBTeXModel) event.getSource()).getEntries());
        }

    }
    
    public static class PublicationEntryNode extends BiBEntryNode {
        public PublicationEntryNode(PublicationEntry entry, FileObject source) {
            super(entry, source);
            entry.addPropertyChangeListener(this);
            setName(entry.getTag());
        }
        
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            Sheet.Set properties = sheet.get(Sheet.PROPERTIES);
            
            if (properties == null) {
                sheet.put(properties = Sheet.createPropertiesSet());
            }
            
            try {
                Node.Property name = new PropertySupport.Reflection(getEntry(), String.class, "type");//NOI18N
                
                name.setName("type");//NOI18N
                name.setDisplayName(org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Type"));
                properties.put(name);
                
                Node.Property tag = new PropertySupport.Reflection(getEntry(), String.class, "tag");//NOI18N
                
                tag.setName("tag");//NOI18N
                tag.setDisplayName(org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Tag"));
                properties.put(tag);

                Node.Property title = new PropertySupport.Reflection(getEntry(), String.class, "title");//NOI18N
                
                title.setName("title");//NOI18N
                title.setDisplayName(org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Title"));
                properties.put(title);
                
                Node.Property author = new PropertySupport.Reflection(getEntry(), String.class, "author");//NOI18N
                
                author.setName("author");//NOI18N
                author.setDisplayName(org.openide.util.NbBundle.getBundle(MyDataNode.class).getString("LBL_Author"));
                properties.put(author);
            } catch (NoSuchMethodException e) {
                ErrorManager.getDefault().notify(e);
            }
            
            return sheet;
        }

    }

}

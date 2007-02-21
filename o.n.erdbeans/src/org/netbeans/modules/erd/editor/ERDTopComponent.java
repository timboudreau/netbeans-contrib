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
package org.netbeans.modules.erd.editor;

import org.netbeans.modules.erd.util.LoadingPanel;
import java.awt.BorderLayout;
import java.io.ObjectStreamException;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.erd.*;
import org.netbeans.modules.erd.io.ERDDataObject;
import org.netbeans.modules.erd.model.ERDController;
import org.netbeans.modules.erd.model.ERDDocument;
import org.netbeans.modules.erd.model.ERDDocumentAwareness;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.text.CloneableEditorSupport;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
final public class ERDTopComponent extends CloneableTopComponent implements CloneableEditorSupport.Pane 
                                                                 ,ERDDocumentAwareness{
    
    private static final long serialVersionUID = 1L;
    
    private static ERDTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "ERDTopComponent";
    
    private JComponent view;
    private JScrollPane scroll;
    private SchemaElement se;
    private LoadingPanel loadingPanel;
    private ERDDataObject dataObject;
    private ERDDocument document;
    private ERDEditorSupport editorSupport;
    public ERDTopComponent(ERDDataObject dataObject,ERDEditorSupport editorSupport) {
        this.editorSupport=editorSupport;
        scroll=new JScrollPane(view);
        dataObject.addDesignDocumentAwareness(this);
        this.dataObject=dataObject;
        
               
        setLayout(new BorderLayout());
        setName("");
        setToolTipText("");
        loadingPanel = new LoadingPanel ("Loading Document"); // NOI18N
        add (loadingPanel, BorderLayout.CENTER);
    }
    
   
    public ERDTopComponent(){
        this(null,null);
    }
   
    
     
    
   
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    public void componentOpened() {
        dataObject.getDocumentSerializer ().startLoadingDocument ();
        editorSupport.updateDisplayName();
    }
    
    public void componentClosed() {
       // dataObject.notifyClosed();
        //documentS
    }
    
   
    
    protected String preferredID() {
        return PREFERRED_ID;
    }

    protected void componentShowing() {
        super.componentShowing();
    }
    
    
    @Override
    public boolean canClose(){
        return editorSupport.notifyClosed();
    }
    
    
   public void setDocumentInAWT(ERDDataObject dataObject){
    if(dataObject!=null){
      view=createView();
      scroll.setViewportView (view); 
      removeAll();
      add (scroll, BorderLayout.CENTER);
     
    }
    else{
      view=null;
      removeAll();
      add (loadingPanel, BorderLayout.CENTER);  
    }
    validate();
      
      
   }
   
   
   
   
   public JComponent createView(){
     /*  Scene scene=DocumentLoad.load(dataObject);
       
       
      JComponent view=scene.createView();
      return view;*/
      return view;
      
   }
   
  
   
   public void setERDDocument (final ERDDocument newDesignDocument) {
        Runnable coolTask=new Runnable() {

            public void run() {
                document = newDesignDocument;
                ERDController controller = document != null ? document.getListenerManager().getController()
                                                            : null;

                view = controller != null ? controller.createView()
                                          : null;
                removeAll();
                if (view != null) {
                    JScrollPane scroll = new JScrollPane(view);

                    scroll.getHorizontalScrollBar().setUnitIncrement(64);
                    scroll.getHorizontalScrollBar().setBlockIncrement(256);
                    scroll.getVerticalScrollBar().setUnitIncrement(64);
                    scroll.getVerticalScrollBar().setBlockIncrement(256);
                    add(scroll, BorderLayout.CENTER);
                } else
                    add(new LoadingPanel("Luke"), BorderLayout.CENTER);
                validate();
            }
        };
        
        if (SwingUtilities.isEventDispatchThread ())
            coolTask.run ();
        else
            SwingUtilities.invokeLater (coolTask);
        
   }
   
   protected void componentActivated() {
        super.componentActivated();
   }

    public void ensureVisible() {
    }

    public void updateName() {
    }

    public CloneableTopComponent getComponent() {
        return this;
    }

    public JEditorPane getEditorPane() {
        return null;
    }
    @Override
    public UndoRedo getUndoRedo () {
        
        return dataObject.getDocumentSerializer ().getUndoRedoManager ();
    }
    
    
    
}

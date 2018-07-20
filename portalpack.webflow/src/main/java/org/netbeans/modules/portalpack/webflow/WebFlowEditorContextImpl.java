/*
 * JSFConfigContextImpl.java
 *
 * Created on February 9, 2007, 11:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.webflow;

import java.io.Serializable;
import org.netbeans.modules.portalpack.webflow.api.WebFlowEditorContext;
import org.netbeans.modules.portalpack.webflow.loader.WebFlowDataObject;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.windows.TopComponent;

/**
 *
 * @author petr
 */
public class WebFlowEditorContextImpl implements WebFlowEditorContext, Serializable{
    static final long serialVersionUID = -4802489998350639459L;

    WebFlowDataObject jsfDataObject;
    /** Creates a new instance of JSFConfigContextImpl */
    public WebFlowEditorContextImpl(WebFlowDataObject data) {
        jsfDataObject = data;
    }
    
    public FileObject getFacesConfigFile() {
        return jsfDataObject.getPrimaryFile();
    }

    public UndoRedo getUndoRedo() {
        return jsfDataObject.getEditorSupport().getUndoRedoManager();
    }

    public void setMultiViewTopComponent(TopComponent topComponent) {
        jsfDataObject.getEditorSupport().setMVTC(topComponent);
    }

}

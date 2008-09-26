/*
 * JSFConfigContextImpl.java
 *
 * Created on February 9, 2007, 11:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder;

import java.io.Serializable;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.api.ServiceBuilderEditorContext;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.loader.ServiceBuilderDataObject;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.windows.TopComponent;

/**
 *
 * @author petr
 */
public class ServiceBuilderEditorContextImpl implements ServiceBuilderEditorContext, Serializable{
    static final long serialVersionUID = -4802489998350639459L;

    ServiceBuilderDataObject jsfDataObject;
    /** Creates a new instance of JSFConfigContextImpl */
    public ServiceBuilderEditorContextImpl(ServiceBuilderDataObject data) {
        jsfDataObject = data;
    }
    
    public FileObject getServiceBuilderFile() {
        return jsfDataObject.getPrimaryFile();
    }

    public UndoRedo getUndoRedo() {
        return jsfDataObject.getEditorSupport().getUndoRedoManager();
    }

    public void setMultiViewTopComponent(TopComponent topComponent) {
        jsfDataObject.getEditorSupport().setMVTC(topComponent);
    }
    
    public ServiceBuilderDataObject getDataObject() {
        return jsfDataObject;
    }

}

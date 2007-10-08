/*
 * Util.java
 * 
 * Created on Oct 8, 2007, 10:56:49 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ucla.netbeans.module.bpel.setconsistency;

import org.netbeans.modules.bpel.model.api.BpelModel;
import org.netbeans.modules.bpel.model.spi.BpelModelFactory;
import org.netbeans.modules.xml.retriever.catalog.Utilities;
import org.netbeans.modules.xml.xam.ModelSource;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

/**
 *
 * @author radval
 */
public class Util {

    public static BpelModel getModel(DataObject bpelDataObject) {
        ModelSource modelSource = Utilities.getModelSource(bpelDataObject
                .getPrimaryFile(), true);
        return getModelFactory().getModel(modelSource);
    }
    
    private static BpelModelFactory getModelFactory() {
        BpelModelFactory factory = (BpelModelFactory) Lookup.getDefault()
                .lookup(BpelModelFactory.class);
        return factory;
    }
}

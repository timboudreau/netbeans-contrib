/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ide.analysis.modernize;

import javax.swing.text.Document;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;

/**
 *
 * @author Ilia Gromov
 */
class RequestImpl implements CsmErrorProvider.Request {
    
    private final CsmFile csmFile;

    public RequestImpl(CsmFile csmFile) {
        this.csmFile = csmFile;
    }

    @Override
    public CsmFile getFile() {
        return csmFile;
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Document getDocument() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CsmErrorProvider.EditorEvent getEvent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

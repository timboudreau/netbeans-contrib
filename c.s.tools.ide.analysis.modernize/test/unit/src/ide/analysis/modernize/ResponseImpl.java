/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ide.analysis.modernize;

import com.sun.tools.ide.analysis.modernize.impl.ModernizeErrorInfo;
import com.sun.tools.ide.analysis.modernize.impl.ModernizeFix;
import com.sun.tools.ide.analysis.modernize.impl.YamlParser;
import java.util.List;
import junit.framework.TestCase;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorInfo;
import org.netbeans.modules.cnd.api.model.syntaxerr.CsmErrorProvider;
import org.openide.util.Exceptions;

/**
 *
 * @author Ilia Gromov
 */
class ResponseImpl implements CsmErrorProvider.Response {
    
    private final List<ModernizeFix> fixes;

    public ResponseImpl(List<ModernizeFix> fixes) {
        this.fixes = fixes;
    }

    @Override
    public void addError(CsmErrorInfo info) {
        try {
            TestCase.assertTrue(info instanceof ModernizeErrorInfo);
            final List<YamlParser.Replacement> replacements = ((ModernizeErrorInfo) info).getDiagnostics().getReplacements();
            final String id = ((ModernizeErrorInfo) info).getId();
            ModernizeFix fix = new ModernizeFix(replacements, id);
            fixes.add(fix);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void done() {
    }
    
}

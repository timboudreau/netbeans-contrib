package org.netbeans.modules.cnd.syntaxerr;

import org.netbeans.modules.cnd.syntaxerr.highlighter.ErrorHighlighter;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class SyntaxErrInstall extends ModuleInstall {

    @Override
    public void restored() {
        if( DebugUtils.TRACE ) System.err.printf("SyntaxErrInstall.restored\n");
	super.restored();
        ErrorHighlighter.instance().startup();
    }

    @Override
    public void close() {
        if( DebugUtils.TRACE ) System.err.printf("SyntaxErrInstall.close\n");
        super.close();
        ErrorHighlighter.instance().shutdown();
    }

}
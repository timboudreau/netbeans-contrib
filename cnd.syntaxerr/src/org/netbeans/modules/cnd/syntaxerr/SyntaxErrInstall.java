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
        if( Flags.TRACE ) System.err.printf("SyntaxErrInstall.restored\n");
	super.restored();
        ErrorHighlighter.instance().startup();
    }

    @Override
    public void close() {
        if( Flags.TRACE ) System.err.printf("SyntaxErrInstall.close\n");
        super.close();
        ErrorHighlighter.instance().shutdown();
    }

    @Override
    public void uninstalled() {
        if( Flags.TRACE ) System.err.printf("SyntaxErrInstall.uninstalled\n");
	super.uninstalled();
        ErrorHighlighter.instance().shutdown();
    }
}
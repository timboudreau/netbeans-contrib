package org.netbeans.modules.graphicclassview;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import javax.swing.text.Document;
import org.netbeans.api.java.source.*;
import org.netbeans.modules.graphicclassview.javac.ElementFinder;
import org.netbeans.modules.graphicclassview.javac.UsageFinder;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public class ModelBuilder {

    private static final class T
            implements Task <CompilationController> {
        private final Callback notifier;

        T(Callback notifier) {
            this.notifier = notifier;
        }

        public void run(CompilationController compiler)
                throws Exception {
            Set <SceneElement> result = new HashSet <SceneElement> ();
            System.err.println("Starting parse");
            compiler.toPhase(JavaSource.Phase.RESOLVED);
            List types = compiler.getCompilationUnit().getTypeDecls();
            ElementFinder elementFinder = new ElementFinder(compiler);
            try {
                Tree tree;
                for (Iterator i$ = types.iterator(); i$.hasNext(); tree.accept(elementFinder, result)) {
                    tree = (Tree) i$.next();
                }

                UsageFinder usageFinder = new UsageFinder(compiler, elementFinder.getElementsMap());

                Tree tree2;
                for (Iterator i$ = types.iterator(); i$.hasNext(); tree2.accept(usageFinder, null)) {
                    tree2 = (Tree) i$.next();
                }
                System.err.println((new StringBuilder()).append("Found ").append(result.size()).append(" elements").toString());
                System.err.println("Done, notifying view");
            } catch (RuntimeException e) {
                System.err.println("Done, notifying view");
                notifier.done(result);
                throw e;
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
                notifier.failed(e.getLocalizedMessage());
                System.err.println("Done, notifying view");
            } finally {
                notifier.done(result);
            }
        }
    }

    static interface Callback {

        public abstract void done(Set<SceneElement> set);

        public abstract void failed(String s);
    }

    public ModelBuilder(FileObject file) {
        this.file = file;
        assert file != null;
        doc = null;
    }

    public ModelBuilder(Document doc) {
        this.doc = doc;
        assert doc != null;
        file = null;
    }

    void analyze(Callback callback) {
        try {
            JavaSource src = file != null ? JavaSource.forFileObject(file) : JavaSource.forDocument(doc);
            src.runWhenScanFinished(new T(callback), true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    private final FileObject file;
    private final Document doc;
}

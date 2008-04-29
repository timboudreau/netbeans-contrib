/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.api.javafx.source;

import com.sun.javafx.api.JavafxcTask;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javafx.api.JavafxcTaskImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import org.netbeans.api.lexer.TokenHierarchy;

/**
 *
 * @author nenik
 */
class CompilationInfoImpl {
    static final Logger LOGGER = Logger.getLogger(CompilationInfoImpl.class.getName());
    
    JavaFXSource.Phase phase = JavaFXSource.Phase.MODIFIED;
    private CompilationUnitTree compilationUnit;    

    private JavafxcTaskImpl cTask;
    final JavaFXSource source;
    

    CompilationInfoImpl(JavaFXSource source) {
        assert source != null;
        this.source = source;
    }

    /**
     * Returns the current phase of the {@link JavaFXSource}.
     * @return {@link JavaFXSource.Phase} the state which was reached by the {@link JavaFXSource}.
     */
    JavaFXSource.Phase getPhase() {
        return phase;
    }

    /**
     * Returns the javafxc tree representing the source file.
     * @return {@link CompilationUnitTree} the compilation unit containing
     * the top level classes contained in the, javafx source file.
     * 
     * @throws java.lang.IllegalStateException  when the phase is less than {@link JavaFXSource.Phase#PARSED}
     */
    CompilationUnitTree getCompilationUnit() {
//        if (this.jfo == null) {
//            throw new IllegalStateException ();
//        }
        if (phase.lessThan(JavaFXSource.Phase.PARSED))
            throw new IllegalStateException("Cannot call getCompilationInfo() if current phase < JavaFXSource.Phase.PARSED. You must call toPhase(Phase.PARSED) first.");//NOI18N
        return compilationUnit;
    }
    
    public TokenHierarchy getTokenHierarchy() {
        return source.getTokenHierarchy();
    }

    /**
     * Returns {@link JavaFXSource} for which this {@link CompilationInfoImpl} was created.
     * @return JavaFXSource
     */
    public JavaFXSource getJavaFXSource() {
        return source;
    }
    
    /** Moves the state to required phase. If given state was already reached 
     * the state is not changed. The method will throw exception if a state is 
     * illegal required. Acceptable parameters for thid method are <BR>
     * <LI>{@link org.netbeans.api.java.source.JavaSource.Phase.PARSED}
     * <LI>{@link org.netbeans.api.java.source.JavaSource.Phase.ELEMENTS_RESOLVED}
     * <LI>{@link org.netbeans.api.java.source.JavaSource.Phase.RESOLVED}
     * <LI>{@link org.netbeans.api.java.source.JavaSource.Phase.UP_TO_DATE}   
     * @param phase The required phase
     * @return the reached state
     * @throws IllegalArgumentException in case that given state can not be 
     *         reached using this method
     * @throws IOException when the file cannot be red
     */    
    public JavaFXSource.Phase toPhase(JavaFXSource.Phase phase ) throws IOException {
        if (phase == JavaFXSource.Phase.MODIFIED) {
            throw new IllegalArgumentException( "Invalid phase: " + phase );    //NOI18N
        }
        JavaFXSource.Phase currentPhase = source.moveToPhase(phase, this, false);
            return currentPhase.compareTo (phase) < 0 ? currentPhase : phase;
    }

    /**
     * Sets the current {@link JavaFXSource.Phase}
     * @param phase
     */
    void setPhase(final JavaFXSource.Phase phase) {
        assert phase != null;
        this.phase = phase;
    }

    void setCompilationUnit(CompilationUnitTree compilationUnit) {
        assert this.compilationUnit == null;
        this.compilationUnit = compilationUnit;
    }

    JavafxcTask getJavafxcTask() {
        if (cTask == null) {
            cTask = source.createJavafxcTask(new DiagnosticListenerImpl());
        }
        return cTask;
    }
    
    Context getContext() {
        return cTask.getContext();
    }
    /**
     * Returns the errors in the file represented by the {@link JavaSource}.
     * @return an list of {@link Diagnostic} 
     */
    List<Diagnostic> getDiagnostics() {
        Collection<Diagnostic> errors = ((DiagnosticListenerImpl) cTask.getContext().get(DiagnosticListener.class)).errors.values();
        return new ArrayList<Diagnostic>(errors);
    }
    
    static class DiagnosticListenerImpl implements DiagnosticListener<JavaFileObject> {
        
        private final TreeMap<Integer,Diagnostic> errors;
        
        public DiagnosticListenerImpl() {
            this.errors = new TreeMap<Integer,Diagnostic>();
        }
        
        public void report(Diagnostic<? extends JavaFileObject> message) {            
            LOGGER.info("Error at [" + message.getLineNumber() + ":" + message.getColumnNumber() + "]/" + message.getEndPosition() + " - " + message.getMessage(null));
            errors.put((int)message.getPosition(),message);
        }
    }
}

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.tasklist.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.modules.tasklist.checkstyle.options.CheckstyleSettings;
import org.netbeans.modules.tasklist.client.SuggestionAgent;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.client.SuggestionPriority;

import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.SharedClassObject;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;

/**
 * This class uses the Checkstyle rule checker to provide rule violation
 * suggestions.
 * <p>
 * @todo This version only operates on the disk-versions of
 *   the source files! Either get checkstyle modified to
 *   have a Reader interface, or save files to temporary buffers.
 * @todo Add automatic fixers for some of these rules.
 * @todo Refactor so I can share some code with the pmd bridge
 * <p>
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */


public class ViolationProvider extends DocumentSuggestionProvider
    implements AuditListener {

    /** TODO comment me **/
    final private static String TYPE = "checkstyle-violations"; // NOI18N

    /** **/
    public String getType() {
        return TYPE;
    }

    /** List "owned" by the scan() method and updated by the audit listener
     * methods. */
    private List tasks = null;
    
    /** TODO comment me **/
    public List scan(final SuggestionContext env) {
        tasks = null;
        this.env = env;
        final SuggestionManager manager = SuggestionManager.getDefault();
        if (!manager.isEnabled(TYPE)) {
            return null;
        }

        /* This code is for looking up the dynamic content of the
           document - but see below - we don't need it yet...
        SourceCookie cookie =
            (SourceCookie)dobj.getCookie(SourceCookie.class);

        // The file is not a java file
        if(cookie == null) {
            return null;
        }
        String text = null;
        try {
            int len = doc.getLength();
            text = doc.getText(0, len);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
            return null;
        }
        Reader reader = new StringReader(text);
        //String name = cookie.getSource().getClasses()[0].getName().getFullName();
        */
        
        // Checkstyle doesn't seem to have an API where I can pass in
        // a string reader - it wants to read the files directly!
        final FileObject fo = env.getFileObject();
        try {
            dataobject = DataObject.find(fo);
        } catch (DataObjectNotFoundException e) {
            ErrorManager.getDefault().notify(e);
        }
        final File file = (dataobject != null && !dataobject.isModified()) ? FileUtil.toFile(fo) : null;

        if (file != null) {
            try {
                if (callCheckstyle(file) == false) {
                    return null;
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
                return null;
            }
        } else {
            Writer out = null;
            try {
                final File tmp = File.createTempFile("tl_cs", "tmp"); // NOI18N
                tmp.deleteOnExit();
                out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tmp)));
                final CharSequence chars = env.getCharSequence();
                for (int i = 0; i < chars.length(); i++) {
                    out.write(chars.charAt(i));
                }
                if (callCheckstyle(file) == false) {
                    return null;
                }
                tmp.delete();
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
                return null;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            }

        }
        return tasks;
    }

    /** TODO comment me **/
    private boolean callCheckstyle(final File file) {
        // TODO: this should only be done once, not for each scan!!!
        try {
            final Checker checker = new Checker();
            ModuleFactory moduleFactory = null;
            checker.setModuleFactory(moduleFactory);
            Configuration config = null;
            final Properties props = System.getProperties();
            
            final CheckstyleSettings settings 
                    = (CheckstyleSettings) SharedClassObject.findObject(CheckstyleSettings.class, true);            

            config = ConfigurationLoader.loadConfiguration(settings.getCheckstyle(), new PropertiesExpander(props));
            checker.configure(config);
            checker.addListener(this);
            checker.process(new File[] {file }); // Yuck!
            return true;
        } catch (CheckstyleException e) {
            ErrorManager.getDefault().notify(e);
            return false;
        }
    }

    // Implements AuditListener ~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * notify that the audit is about to start
     * @param aEvt the event details
     */
    public void auditStarted(final AuditEvent aEvt) {
        //System.out.println("audidStarted(" + aEvt + ")");
    }

    /**
     * notify that the audit is finished
     * @param aEvt the event details
     */
    public void auditFinished(final AuditEvent aEvt) {
        //System.out.println("audidFinished(" + aEvt + ")");
    }

    /**
     * notify that audit is about to start on a specific file
     * @param aEvt the event details
     */
    public void fileStarted(final AuditEvent aEvt) {
        //System.out.println("fileStarted(" + aEvt + ")");
    }

    /**
     * notify that audit is finished on a specific file
     * @param aEvt the event details
     */
    public void fileFinished(final AuditEvent aEvt) {
        //System.out.println("fileFinished(" + aEvt + ")");
    }

    /**
     * notify that an exception happened while performing audit
     * @param aEvt the event details
     * @param aThrowable details of the exception
     */
    public void addException(final AuditEvent aEvt, final Throwable aThrowable) {
        ///System.out.println("addException(" + aEvt + "," + aThrowable + ")");
    }
    
    
    /**
     * notify that an audit error was discovered on a specific file
     * @param aEvt the event details
     */
    public void addError(AuditEvent aEvt) {
        //System.out.println("addError(" + aEvt + ")");
        
        try {
            final int lineNo = Math.max(1,   aEvt.getLine());
            final Line line = TLUtils.getLineByNumber(dataobject, lineNo);

            final SuggestionPerformer action = getAction(aEvt);
            final String description = aEvt.getLocalizedMessage().getMessage();
                    
            final SuggestionManager manager = SuggestionManager.getDefault();

            final SuggestionAgent s = manager.createSuggestion(
                        env.getFileObject(),
                        TYPE,
                        description,
                        action,
                        this);

            final SeverityLevel sv = aEvt.getSeverityLevel();
            
            if (sv != SeverityLevel.IGNORE) {
                
                if (sv == SeverityLevel.INFO) {
                    s.setPriority(SuggestionPriority.LOW);
                    
                } else if (sv == SeverityLevel.WARNING) {
                    s.setPriority(SuggestionPriority.MEDIUM_LOW);
                    
                } else if (sv == SeverityLevel.ERROR) {
                    // Even most of the errors seem pretty tame - "line longer than
                    // 80 characters", etc. - so make these medium as well.
                    // Would be nice if Checkstyle would be more careful with
                    // the use of the ERROR level.
                    s.setPriority(SuggestionPriority.MEDIUM);
                }

                s.setLine(line);
                s.setDetails(aEvt.getLocalizedMessage().getKey());

                if (tasks == null) {
                    tasks = new ArrayList(40); // initial guess
                }
                tasks.add(s.getSuggestion());
            }

        } catch (Exception e)  {
            ErrorManager.getDefault().notify(e);
        }
    }

    /** TODO comment me **/
    private SuggestionPerformer getAction(
            final AuditEvent aEvt) {

        final String key = aEvt.getLocalizedMessage().getKey();
        if (key != null) {
            
            
            if (key.contains("trailing")  // FIXME i18n me. problem is that this comes from the checkstyle.xml
                    && key.contains("spaces"))  { // and can be specified in any language with a common key.
                return new TrailingSpacesSuggestionPerformer(env.getDocument(),aEvt.getLine());
                
            }  else if ("import.unused".equals(key) || "import.avoidStar".equals(key)) {
                return new DeleteLineSuggestionPerformer(env.getDocument(),aEvt.getLine());
                
            }  else if ("ws.notPreceded".equals(key) || "ws.notFollowed".equals(key)) {
                return new InsertStringSuggestionPerformer(env.getDocument(),   aEvt.getLine(), aEvt.getColumn())
                        .setString(" ");
                
            }  else if ("ws.preceded".equals(key) || "ws.followed".equals(key)) {
                return new DeleteSpaceSuggestionPerformer(env.getDocument(), aEvt.getLine(), aEvt.getColumn());
                
            }  else if ("final.parameter".equalsIgnoreCase(key)) {
                return new InsertStringSuggestionPerformer(env.getDocument(), aEvt.getLine(), aEvt.getColumn())
                        .setString("final ");
                
            }  else if ("final.variable".equalsIgnoreCase(key))  {
                return new InsertFinalVariableKeywordSuggestionPerformer(env.getDocument(),aEvt.getLine(),aEvt.getColumn());
                
            }  else if ("javadoc.missing".equalsIgnoreCase(key)){
                return new InsertStringSuggestionPerformer(env.getDocument(),aEvt.getLine(),aEvt.getColumn())
                        .setString("/** TODO comment me. **/\n    ");
                
            }  else if ("javadoc.noperiod".equalsIgnoreCase(key)) {
                return new InsertStringSuggestionPerformer(env.getDocument(), aEvt.getLine(), aEvt.getColumn())
                        .setString(".");
                
            }

        }
        return null;
    }
    
    
    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;

    /** **/
    private SuggestionContext env;
    /** TODO comment me **/
    private DataObject dataobject = null;
    /** TODO comment me **/
    private Document document = null;
    /** **/
    private Object request = null;
}


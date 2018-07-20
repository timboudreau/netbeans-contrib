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


package org.netbeans.modules.group;


import org.openide.actions.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;


/**
 * Loader for <code>GroupShadow</code> data object.
 *
 * @author Jaroslav Tulach
 */
public class GroupShadowLoader extends DataLoader {

    /** generated serial version UID */
    static final long serialVersionUID =-2768192459953761627L;

    /**
     * extension of files representing groups.
     * This extension is initially the only item in the list of recognized
     * extensions.
     *
     * @see  #setExtensions
     */
    public static final String GS_EXTENSION = "group"; // NOI18N
    
    /**
     * list of extensions of group shadow files
     *
     * @see  #setExtensions
     */
    private ExtensionList extensions;

    
    /** Creates a new loader. */
    public GroupShadowLoader() {
        super("org.netbeans.modules.group.GroupShadow"); // NOI18N
        
        extensions = new ExtensionList();
        extensions.addExtension(GS_EXTENSION);
    }
    
    
    /** */
    protected String defaultDisplayName() {
        return NbBundle.getMessage(GroupShadowLoader.class,
                                   "PROP_GroupShadowName");             //NOI18N
    }
    
    /** */
    protected SystemAction[] defaultActions() {
        return new SystemAction[] {
            SystemAction.get(OpenLocalExplorerAction.class),
            SystemAction.get(FileSystemAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(SaveAsTemplateAction.class),
            null,
            SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class)
        };
    }

    
    /**
     * @return  {@link GroupShadow} for the given <code>FileObject</code>;
     *          or <code>null</code> if the <code>FileObject</code>
     *          does not have the expected extension
     * @see  #GS_EXTENSION
     */
    protected DataObject handleFindDataObject(
            FileObject fo,
            DataLoader.RecognizedFiles recognized) throws java.io.IOException {
        if (getExtensions().isRegistered(fo)) {
            return new GroupShadow(fo, this);
        }
        return null;
    }

    /**
     * Returns a list of extensions of group shadow files.
     * Files having an extension among the specified extensions are
     * recognized as files representing group shadow files.
     *
     * @return  list of extensions of group shadow files
     */
    public ExtensionList getExtensions() {
        return extensions;
    }

    /**
     * Sets a list of extensions of group shadow files.
     * Files having an extension among the specified extensions will then be
     * recognized as files representing group shadow files.
     *
     * @param  extensions  new list of extensions
     */
    public void setExtensions(ExtensionList extensions) {
        this.extensions = extensions;
    }
}

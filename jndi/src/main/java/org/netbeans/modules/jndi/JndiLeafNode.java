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

package org.netbeans.modules.jndi;

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.openide.actions.CopyAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

/** This class represents Leaf Node (Not context) in JNDI tree
 *
 *  @author Ales Novak, Tomas Zezula
 */
public class JndiLeafNode extends JndiObjectNode {

    /** Offset of this node relative to ctx*/
    protected CompositeName offset;

    /** Constructor
     *  @param ctx  initial context
     *  @param parentOffset offset of parent directory
     *  @param name name of this node
     */ 
    public JndiLeafNode(JndiKey key, CompositeName offset) throws javax.naming.InvalidNameException {
        super(key,Children.LEAF);
        this.offset = (CompositeName) ((CompositeName)offset.clone()).add (key.name.getName());
        setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(key.name.getClassName()));
    }

    /** Generates code for accessing object that is represented by this node
     *  @return String the java source code
     */
    public String createTemplate() throws NamingException {
        return JndiObjectCreator.getLookupCode(((JndiNode)this.getParentNode()).getContext(), this.getOffsetAsString(), this.getKey().name.getClassName());
    }

    /** Returns SystemAction
     *  @return array of SystemAction
     */
    public SystemAction[] createActions() {
        return new SystemAction[] {
                   SystemAction.get(LookupCopyAction.class),
                   null,
                   SystemAction.get(DeleteAction.class),
                   null,
                   SystemAction.get(PropertiesAction.class),
               };
    }


    /** Destroys this node.
    * If this node is root then nothing more is done.
    * If this node is not root then represented Context is destroyed.
    *
    * @exception IOException
    */
    public void destroy() throws IOException {
        try {
            // destroy this context first
            ((JndiNode)this.getParentNode()).getContext().unbind (this.getKey().name.getName());
            super.destroy();
        } catch (NamingException e) {
            JndiRootNode.notifyForeignException(e);
        }
    }

    /** Returns initial directory context
     *  @return Context the dir context  in which the object is defined
     */
    public Context getContext (){
        return ((JndiNode)this.getParentNode()).getContext();
    }

    /** Returns the properties of Initial Context
     *  @return Hashtable properties;
     */
    public Hashtable getInitialDirContextProperties() throws NamingException {
        return ((JndiNode)this.getParentNode()).getContext().getEnvironment();
    }

    /** Returns offset of the node in respect to InitialContext
     *  @return CompositeName the offset
     */
    public CompositeName getOffset(){
        return this.offset;
    }
    
    public String getOffsetAsString () {
	return JndiObjectCreator.stringifyCompositeName (this.offset);
    }
    
    public Sheet createSheet () {
        Sheet sheet = super.createSheet();
        if (this.getContext() instanceof javax.naming.directory.DirContext){
            try{
                Attributes attrs = ((DirContext)this.getContext()).getAttributes(this.getKey().name.getName());
                java.util.Enumeration enu = attrs.getAll();
                while (enu.hasMoreElements()){
                    Attribute attr = (Attribute) enu.nextElement();
                    String attrId = attr.getID();
                    sheet.get(JndiObjectNode.JNDI_PROPERTIES).put( new JndiProperty(attrId,String.class,attrId,null, attrToString(attr),this,true));
                }
            }catch (NamingException ne){}
        }
        return sheet;
    }

    /** Returns class name
     *  @return String class name
     */
    public String getClassName(){
        return this.getKey().name.getClassName();
    }
    
    /** Returns help context for the
     *  JNDI leaf node, the node representing 
     *  the end bound object (e.g. EJB HomeInterface)
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (JndiLeafNode.class.getName());
    }
    
    protected void handleChangeJndiPropertyValue (Attributes attrs) throws NamingException {
        ((DirContext)this.getContext()).modifyAttributes(this.getKey().name.getName(),DirContext.REPLACE_ATTRIBUTE,attrs);
    }

}

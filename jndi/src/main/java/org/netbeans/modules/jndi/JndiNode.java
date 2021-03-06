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

import java.util.Vector;
import java.util.Collection;
import java.util.Hashtable;
import java.io.IOException;
import java.awt.Dialog;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import org.openide.actions.NewAction;
import org.openide.actions.CopyAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.util.HelpCtx;
import org.openide.DialogDescriptor;
import org.netbeans.modules.jndi.utils.Refreshable;
import org.netbeans.modules.jndi.utils.DisconnectCtxCookie;
import org.netbeans.modules.jndi.gui.AttributePanel;


/** This class represents JNDI subdirectory
 *
 *  @author Ales Novak, Tomas Zezula
 */
public final class JndiNode extends JndiObjectNode implements Refreshable, DisconnectCtxCookie, Node.Cookie{

    /** NewType for this node*/
    private NewType[] jndinewtypes;

    /** Needs this node to be refreshed*/
    private boolean needRefresh;

    /** Holder for dialogs*/
    private transient Dialog dlg;
    
    /** Unique index of this node */
    private int index;

    /**Constructor for creation of Top Level Directory
     * @param ctx DirContext which this node represents
     */
    public JndiNode(Context ctx, int index) throws NamingException {
        super ((String)ctx.getEnvironment().get(JndiRootNode.NB_LABEL), new JndiChildren(ctx, new CompositeName()));
        this.index = index;
        this.init ();
    }

    /** Constructor of subdirectory
     *  ctx  DirectoryContext
     *  parent_name offset of parent directory
     *  my_name	name of this directory
     */
    public JndiNode(JndiKey key, CompositeName offset) throws javax.naming.InvalidNameException {
        super (key, new JndiChildren((javax.naming.Context)key.name.getObject(), (CompositeName)((CompositeName)offset.clone()).add(key.name.getName())));
        this.index = -1;
        this.init ();
    }

    public boolean isRoot() {
        return this.index != -1;
    }
    
    public boolean canDestroy () {
        return ! this.isRoot();
    }

    /** This method creates template for accessing this node
     *  @return String java source code
     */ 
    public String createTemplate() throws NamingException {
        return JndiObjectCreator.getLookupCode(((JndiChildren)this.getChildren()).getContext(),this.getOffsetAsString(), this.getClassName());
    }

    /** Returns NewTypes for this node
     *  @return array of NewNode
     */ 
    public NewType[] getNewTypes() {
        if (this.jndinewtypes == null) {
            this.jndinewtypes = new NewType[] {new JndiDataType(this)};
        }
        return this.jndinewtypes;
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
            Context parentCtx = ((JndiNode)this.getParentNode()).getContext();
            parentCtx.destroySubcontext(this.getKey().name.getName());
            // Destroy the node
            super.destroy();
        } catch (NamingException e) {
            JndiRootNode.notifyForeignException(e);
        }
    }

    /** Returns system actions for this node
     * @return array of SystemAction
     */
    public SystemAction[] createActions() {
        return new SystemAction[] {
                   SystemAction.get(LookupCopyAction.class),
                   SystemAction.get(BindingCopyAction.class),
                   null,
                   SystemAction.get(RefreshAction.class),
                   isRoot()?SystemAction.get(DisconnectAction.class):SystemAction.get(DeleteAction.class),
                   null,
                   SystemAction.get(NewAction.class),
                   null,
                   SystemAction.get(ToolsAction.class),
                   SystemAction.get(PropertiesAction.class),
               };
    }
    
    /** Refreshes this node.
     */
    public final void refresh() {
        ((JndiChildren) getChildren()).prepareKeys();
    }

    /** Copy the binding code*/
    public final void bindingCopy () {
        try{
            ExClipboard clipboard = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
            StringSelection code = new StringSelection(JndiObjectCreator.generateBindingCode(((JndiChildren)this.getChildren()).getContext(),this.getOffsetAsString(), this.getClassName()));
            clipboard.setContents(code,code);
            JndiRootNode.showLocalizedStatus("STS_CopyBindingCode");
        }catch (NamingException ne){
            JndiRootNode.notifyForeignException(ne);
            return;
        }
    }

    /** Returns initial directory context
     *  @return DirContext the initial dir context
     */ 
    public Context getContext(){
        return ((JndiChildren)this.getChildren()).getContext();
    }

    /** Returns the properties of InitialDirContext
     *  @return Hashtable properties;
     */
    public Hashtable getInitialDirContextProperties () throws NamingException {
        return ((JndiChildren)this.getChildren()).getContext ().getEnvironment ();
    }


    /** Returns offset of the node in respect to InitialContext
     *  @return CompositeName the offset
     */
    public CompositeName getOffset(){
        return ((JndiChildren)this.getChildren()).getOffset();
    }
    
    public String getOffsetAsString () {
	return ((JndiChildren)this.getChildren()).getOffsetAsString();
    }
    

    /** Returns class name
     *  @return String class name
     */
    public String getClassName(){
        return "javax.naming.Context"; // No I18N
    }


    /** Refresh property Sheets
     */
    public void updateData(){
        createSheet();
    }

    /** Needs the node to be refreshed by Refreshd
     *  @return true if the node has children, that for any reason failed to open
     *  @see org.netbeans.modules.jndi.utils.Refreshd
     *  @see org.netbeans.modules.jndi.JndiChildren
     */
    public boolean needRefresh(){
        return this.needRefresh;
    }

    /** Clears the flag that the node needs to be refreshed
     */
    public void clearRefresh(){
        this.needRefresh = false;
    }

    /** Sets the flag that the node needs to be refreshed
     */
    public void setRefresh(){
        this.needRefresh = true;
    }
    
    /** Disconnects the root Context
     */
    public void disconnect () {
        try {
            ((JndiRootNodeChildren)this.getParentNode().getChildren()).remove (this.index);
            super.destroy ();
        }catch (java.io.IOException ioe) {
            // Should never happen
            JndiRootNode.notifyForeignException (ioe);
        }
    }

    /** The node supports customizer */
    public boolean hasCustomizer(){
        return this.getContext() instanceof javax.naming.directory.DirContext;
    }

    /** Returns Customizer */
    public java.awt.Component getCustomizer(){
        return new AttributePanel((DirContext)this.getContext(),new CompositeName(),this);
    }
    
    
    public Sheet createSheet () {
        Sheet sheet = super.createSheet();
        if (this.getContext() instanceof javax.naming.directory.DirContext){
            try{
                Attributes attrs = ((DirContext)this.getContext()).getAttributes("");  //No I18N
                java.util.Enumeration enu = attrs.getAll();
                while (enu.hasMoreElements()){
                    Attribute attr = (Attribute) enu.nextElement();
                    String attrId = attr.getID();
                    sheet.get(JndiObjectNode.JNDI_PROPERTIES).put ( new JndiProperty (attrId,String.class,attrId,null,attrToString(attr),this,true));
                }
            }catch (NamingException ne){}
        }
        return sheet;
    }
    
    /** Return help context for the JNDI Context Node,
     *  the JNDI directory
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (JndiNode.class.getName());
    }
    
    protected void handleChangeJndiPropertyValue (Attributes attrs) throws NamingException {
        ((DirContext)this.getContext()).modifyAttributes("",DirContext.REPLACE_ATTRIBUTE,attrs); // No I18N
    }
    
    /** Returns index of subtree or -1 if not a root of subtree.
     *  Package private method used by JndiChildren when the failure of
     *  service occures.
     *  @return int
     */
    int getIndex () {
        return this.index;
    }
    
    
    private void init () {
        this.needRefresh = false;
        this.setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName("javax.naming.Context"));      // No I18N
        this.getCookieSet().add(this);
    }

}

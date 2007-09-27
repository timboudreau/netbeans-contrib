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

import java.awt.Dialog;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.util.Hashtable;
import java.net.URL;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import org.openide.DialogDisplayer;

import org.openide.NotifyDescriptor;
import org.openide.actions.NewAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.Children;
import org.openide.nodes.DefaultHandle;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.RequestProcessor;
import org.openide.util.HelpCtx;
import org.netbeans.modules.jndi.settings.JndiSystemOption;

/** Top Level JNDI Node
 *
 * @author Ales Novak, Tomas Zezula
 */
public final class JndiRootNode extends AbstractNode{

    /** Name of property holding the name of context */
    public final static String NB_LABEL="NB_LABEL";
    /** Name of property holding the initial offset */
    public final static String NB_ROOT="HomePath";
    /** SystemActions*/
    protected SystemAction[] jndiactions = null;
    /** NewTypes*/
    protected NewType[] jndinewtypes = null;

    /** The holder of an instance of this class*/
    private static JndiRootNode instance = null;

    /** The asynchronous refresher*/
    RequestProcessor refresher;


    /** Constructor
     */
    public JndiRootNode() {
        super(new JndiRootNodeChildren());
        this.refresher = new RequestProcessor ("Jndi-Browser-Rehresh"); // No I18N
        setName("JNDI");
        setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(JndiRootNode.NB_ROOT));
    }


    public static JndiRootNode getDefault () {
	if (instance == null) {
            instance = new JndiRootNode ();
	}
        return instance;
    }

    /** Returns name of the node
     *  @return Object the name of node
     */
    public Object getValue() {
        return getName();
    }

    /** Sets name of this node
     *  @param name name of the object
     */  
    public void setValue(Object name) {
        if (name instanceof String) {
            setName((String) name);
        }
    }

    /** Disable Destroying
     * @return false
     */
    public boolean canDestroy() {
        return false;
    }

    /** Disable Copy
     * @return false
     */
    public boolean canCopy() {
        return false;
    }

    /** Disable Cut
     * @return false
     */
    public boolean canCut() {
        return false;
    }

    /** Disable Rename
     *  @return false
     */
    public boolean canRename() {
        return false;
    }

    /** No default action
     *  @return null;
     */  
    public org.openide.util.actions.SystemAction getDefaultAction() {
        return null;
    }

    /** Returns actions for this node
     *  @return array of SystemAction
     */
    public org.openide.util.actions.SystemAction[] getActions() {
        if (jndiactions == null) {
            jndiactions = this.createActions();
        }
        return jndiactions;
    }

    /** Creates actions for this node
     *  @return array of SystemAction
     */
    public org.openide.util.actions.SystemAction[] createActions() {
        return new SystemAction[] {
                   SystemAction.get(NewAction.class),
//                   null,
//                   SystemAction.get (PropertiesAction.class)
               };
    }

    /** Creates an JNDI Type
     *  @return array with JndiDataType
     */
    public NewType[] getNewTypes() {
        if (this.jndinewtypes == null)
            this.jndinewtypes = new NewType[]{ new JndiDataType(this)};
        return this.jndinewtypes;
    }

    /** Creates handle
     *  @return Handle 
     */ 
    public Handle getHandle() {
        return DefaultHandle.createHandle(this);
    }


    /** This function adds an Context
     *  @param context adds context from String
     */	
    public void addContext(String context) throws NamingException {
        Properties env = parseStartContext(context);
        ((JndiRootNodeChildren)getChildren()).add(env);
    }

    /** This function adds an Context
     *  @param label name of node
     *  @param factory JndiFactory
     *  @param context starting Context
     *  @param authentification authentification to naming system
     *  @param principals principals for naming system
     *  @param credentials credentials for naming system
     *  @param prop vector type java.lang.String, additional properties in form key=value
     */
    public void addContext(String label, String factory, String context, String root, String authentification, String principal, String credentials, Vector prop) throws NamingException {
        Properties env = createContextProperties(label,factory,context,root, authentification, principal, credentials, prop);
        this.addContext (env);
    }

    /** This method adds new Context
     *  @param Hashtable properties of context
     **/
    void addContext (Hashtable properties) {
        ((JndiRootNodeChildren)this.getChildren()).add (properties);
    }
    
    /** This method adds new Context
     *  @param Hashtable properties of context
     *  @param int index
     **/
    void addContext (Hashtable properties, int index) {
        ((JndiRootNodeChildren)this.getChildren()).add (properties, index);
    }

    /** This method transforms parameters to properties for Context
     *  @param label name of node
     *  @param factory JndiFactory
     *  @param context starting Context
     *  @param authentification authentification to naming system
     *  @param principals principals for naming system
     *  @param credentials credentials for naming system
     *  @param prop vector type java.lang.String, additional properties in form key=value
     *  @exception JndiException
     */
    final Properties createContextProperties (String label, String factory, String context, String root, String authentification, String principal, String credentials, Vector prop) throws JndiException{
        if (label==null || factory==null || label.equals("") || factory.equals("")) throw new JndiException("Arguments missing");
        Properties env = new Properties();
        env.put(JndiRootNode.NB_LABEL,label);
        env.put(Context.INITIAL_CONTEXT_FACTORY,factory);
        env.put(JndiRootNode.NB_ROOT,"");
        if (context != null && context.length() > 0) {
            env.put(Context.PROVIDER_URL,context);
        }
        if (authentification != null && !authentification.equals("")) {
            env.put(Context.SECURITY_AUTHENTICATION, authentification);
        }
        if (principal != null && !principal.equals("")) {
            env.put(Context.SECURITY_PRINCIPAL, principal);
        }
        if (credentials != null && !credentials.equals("")) {
            env.put(Context.SECURITY_CREDENTIALS,credentials);
        }
        if (root != null && !root.equals("")) {
            env.put(NB_ROOT,root);
        }
        for (int i = 0; i < prop.size(); i++) {
            StringTokenizer tk = new StringTokenizer(((String)prop.elementAt(i)),"=");
            if (tk.countTokens() != 2) {
                continue;
            }
            String path = tk.nextToken();
            if (path.equals(NB_ROOT)) {
                env.put(NB_ROOT, tk.nextToken());
            } else {
                env.put(path, tk.nextToken());
            }
        }
        return env;
    }

    /**This function takes a string and converts it to set of properties
     * @return Properties set of properties if Ok, null on error
     */ 
    private Properties parseStartContext(String ident) throws NamingException {
        StringTokenizer tk = new StringTokenizer(ident,"|");
        Properties env = new Properties();

        try {
            env.put(JndiRootNode.NB_LABEL,tk.nextToken());
            env.put(Context.INITIAL_CONTEXT_FACTORY,tk.nextToken());
            env.put(Context.PROVIDER_URL,tk.nextToken());
        } catch(NoSuchElementException nee) {
            // The parameters above are obligatory
            throw new JndiException("Argument missing");
        }
        try {
            env.put(JndiRootNode.NB_ROOT,tk.nextToken());
        } catch(NoSuchElementException nee) {
            //If this parameter is missing set it to empty string.
            env.put(JndiRootNode.NB_ROOT,"");
        }
        try {
            env.put(Context.SECURITY_AUTHENTICATION, tk.nextToken());
            env.put(Context.SECURITY_PRINCIPAL,tk.nextToken());
            env.put(Context.SECURITY_CREDENTIALS,tk.nextToken());
        } catch(NoSuchElementException nee) {
            // no more elements
        }
        return env;
    }


    /** This method adds an disabled Context
     *  @param Hashtable properties of Context
     *
     * Depricated
     *
    public final void addDisabledContext ( Hashtable properties) {
        Node[] nodes = new Node[1];
        nodes[0]= new JndiDisabledNode(properties);
        this.getChildren().add(nodes);
    } */


    /** Set up initial start contexts
    *
    *  Depricated 
    *
    public synchronized void initStartContexts(java.util.ArrayList nodes) {
	Children.Array cld = (Children.Array) this.getChildren();
        Node[] currentNodes = cld.getNodes();
        if (currentNodes.length >1) {
            Node[] filteredNodes = new Node[ currentNodes.length - 1];
            System.arraycopy (currentNodes,1,filteredNodes,0,filteredNodes.length);
            cld.remove (filteredNodes);
        }
        if (nodes!=null){
            for (int i = 0; i < nodes.size(); i++) {
                try{
                    this.addContext((Hashtable)nodes.get(i), false);
                }catch(NamingException ne){
                    this.addDisabledContext((Hashtable)nodes.get(i));
                }
            }
        }
    }*/


    /** Notifies about an exception that was raised in non Netbeans code.
     */
    public static void notifyForeignException(Throwable t) {

        String msg;
		//DEBUG{BEGIN}
		System.out.println ("TLAMA");
		t.printStackTrace(System.out);
		//DEBUG{END}

        if ((t.getMessage() == null) ||
                t.getMessage().equals("")) {
            msg = t.getClass().getName();
        } else {
            msg = t.getClass().getName() + ": " + t.getMessage();
        }

        final NotifyDescriptor nd = new NotifyDescriptor.Exception(t, msg);
        Runnable run = new Runnable() {
                           public void run() {
                               DialogDisplayer.getDefault().notify(nd);
                           }
                       };
        java.awt.EventQueue.invokeLater(run);
    }

    /** Bundle with localizations. */
    private static ResourceBundle bundle;
    /** @return a localized string */
    public static String getLocalizedString(String s) {
        if (bundle == null) {
            bundle = NbBundle.getBundle(JndiRootNode.class);
        }
        return bundle.getString(s);
    }


    /** Shows an status
     *  @param String message
     */
    public static void showStatus (String message) {
        StatusDisplayer.getDefault().setStatusText(message);
    }

    /** shows an localized status
     *  @param String message
     */
    public static void showLocalizedStatus (String message) {
        showStatus(getLocalizedString(message));
    }
    
    /** Returns the help context for the
     *  JNDI root node
     */
    public HelpCtx getHelpCtx () {
        return new HelpCtx (JndiRootNode.class.getName());
    }


}

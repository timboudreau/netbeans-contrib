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

import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.spi.InitialContextFactory;
import org.openide.ErrorManager;
import org.openide.execution.NbClassLoader;

/** This class extends InitialDirContext with methods for timeout handling
 *
 *  @author Ales Novak, Tom Zezula
 */
final class JndiDirContext implements DirContext {

	
	private Context delegate;

    /** Environment used for InitialContext*/
    protected Hashtable envTable;

    /**
     * Constuctor
     * @param env  hashtable of properties for InitialDirContext
     */
    public JndiDirContext(Hashtable env) throws NamingException {
        this.envTable = env;
		try {
			String className = env != null ? (String)env.get(Context.INITIAL_CONTEXT_FACTORY) : null;
			if (className != null) {
				Class clazz = Class.forName (className, true, new NbClassLoader());
				InitialContextFactory factory = (InitialContextFactory) clazz.newInstance();  
				this.delegate = factory.getInitialContext (env);
			}			
		} catch (Exception exception) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exception);
		}
		if (this.delegate == null) {
			throw new NoInitialContextException ();
		}
    }
	
	public Object lookup (String name) throws NamingException {
		return this.delegate.lookup (name);
	}
	
	public Object lookup (Name name) throws NamingException {
		return this.delegate.lookup (name);
	}
	
	public void bind (String name, Object obj) throws NamingException {
		this.delegate.bind (name, obj);
	}
	
	public void bind (Name name, Object obj) throws NamingException {
		this.delegate.bind (name, obj);
	}
	
	public void rebind (String name, Object obj) throws NamingException {
		this.delegate.rebind (name, obj);
	}
	
	public void rebind (Name name, Object obj) throws NamingException {
		this.delegate.rebind (name, obj);
	}
	
	public void unbind (String name) throws NamingException {
		this.delegate.unbind (name);
	}
	
	public void unbind (Name name) throws NamingException {
		this.delegate.unbind (name);
	}
	
	public void rename (String oldName, String newName) throws NamingException {
		this.delegate.rename (oldName, newName);
	}
	
	public void rename (Name oldName, Name newName) throws NamingException {
		this.delegate.rename (oldName, newName);
	}
	
	public NamingEnumeration list (String name) throws NamingException {
		return this.delegate.list (name);
	}
	
	public NamingEnumeration list (Name name) throws NamingException {
		return this.delegate.list (name);
	}
	
	public NamingEnumeration listBindings (String name) throws NamingException {
		return this.delegate.listBindings (name);
	}
	
	public NamingEnumeration listBindings (Name name) throws NamingException {
		return this.delegate.listBindings (name);
	}
	
	public void destroySubcontext (String name) throws NamingException {
		this.delegate.destroySubcontext (name);
	}
	
	public void destroySubcontext (Name name) throws NamingException {
		this.delegate.destroySubcontext (name);
	}
	
	public Context createSubcontext (String name) throws NamingException {
		return this.delegate.createSubcontext (name);
	}
	
	public Context createSubcontext (Name name) throws NamingException {
		return this.delegate.createSubcontext (name);
	}
	
	public Object lookupLink (String name) throws NamingException {
		return this.delegate.lookupLink (name);
	}
	
	public Object lookupLink (Name name) throws NamingException {
		return this.delegate.lookupLink (name);
	}
	
	public NameParser getNameParser (String name) throws NamingException {
		return this.delegate.getNameParser (name);
	}
	
	public NameParser getNameParser (Name name) throws NamingException {
		return this.delegate.getNameParser (name);
	}	
	
	public String composeName (String name, String prefix) throws NamingException {
		return this.delegate.composeName (name, prefix);
	}
	
	public Name composeName (Name name, Name prefix) throws NamingException {
		return this.delegate.composeName (name, prefix);
	}
	
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		return this.delegate.addToEnvironment (propName, propVal);
	}
	
	public Object removeFromEnvironment(String propName) throws NamingException {
		return this.delegate.removeFromEnvironment (propName);
	}
	
    /** Returns environment for which the Context was created
     *  @return Hashtable of key type java.lang.String, value type java.lang.String
     */ 
    public final Hashtable getEnvironment() throws NamingException {
		return this.delegate.getEnvironment ();		
    }
	
	public void close() throws NamingException {
		this.delegate.close ();
	}
	
	public String getNameInNamespace () throws NamingException {
		return this.delegate.getNameInNamespace ();
	}
	
	
	public Attributes getAttributes (String name) throws NamingException {
		return this.getDirContext().getAttributes (name);
	}
	
	public Attributes getAttributes (Name name) throws NamingException {
		return this.getDirContext().getAttributes (name);
	}
	
	public Attributes getAttributes (Name name, String[] attrIds) throws NamingException {
		return this.getDirContext().getAttributes (name, attrIds);
	}
	
	public Attributes getAttributes (String name, String[] attrIds) throws NamingException {
		return this.getDirContext().getAttributes (name, attrIds);
	}
	
	public void modifyAttributes (Name name, int mod_op, Attributes attrs) throws NamingException {
		this.getDirContext().modifyAttributes (name, mod_op, attrs);
	}
	
	public void modifyAttributes (String name, int mod_op, Attributes attrs) throws NamingException {
		this.getDirContext().modifyAttributes (name, mod_op, attrs);
	}
	
	public void modifyAttributes (Name name, ModificationItem[] mods) throws NamingException {
		this.getDirContext().modifyAttributes (name, mods);
	}
	
	public void modifyAttributes (String name, ModificationItem[] mods) throws NamingException {
		this.getDirContext().modifyAttributes (name, mods);
	}
	
	public void bind (Name name, Object obj, Attributes attrs) throws NamingException {
		this.getDirContext().bind (name, obj, attrs);
	}
	
	public void bind (String name, Object obj, Attributes attrs) throws NamingException {
		this.getDirContext().bind (name, obj, attrs);
	}
	
	public void rebind (Name name, Object obj, Attributes attrs) throws NamingException {
		this.getDirContext().rebind (name, obj, attrs);
	}
	
	public void rebind (String name, Object obj, Attributes attrs) throws NamingException {
		this.getDirContext().rebind (name, obj, attrs);	
	}
	
	public DirContext createSubcontext (Name name, Attributes attrs) throws NamingException {
		return this.getDirContext().createSubcontext (name, attrs);
	}
	
	public DirContext createSubcontext (String name, Attributes attrs) throws NamingException {
		return this.getDirContext().createSubcontext (name, attrs);
	}
	
	public DirContext getSchema(Name name) throws NamingException {
		return this.getDirContext().getSchema (name);
	}
	
	public DirContext getSchema (String name) throws NamingException {
		return this.getDirContext().getSchema (name);		
	}
	
	public DirContext getSchemaClassDefinition(Name name) throws NamingException {
		return this.getDirContext().getSchemaClassDefinition (name);
	}
	
	public DirContext getSchemaClassDefinition(String name) throws NamingException {
		return this.getDirContext().getSchemaClassDefinition (name);
	}
	
	public NamingEnumeration search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
		return this.getDirContext().search (name, matchingAttributes, attributesToReturn);
	}
	
	public NamingEnumeration search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
		return this.getDirContext().search (name, matchingAttributes, attributesToReturn);
	}
	

	public NamingEnumeration search(Name name, Attributes matchingAttributes) throws NamingException {
		return this.getDirContext().search (name, matchingAttributes);
	}
	
	public NamingEnumeration search(String name, Attributes matchingAttributes) throws NamingException {
		return this.getDirContext().search (name, matchingAttributes);
	}
	
	public NamingEnumeration search(Name name, String filter, SearchControls cons) throws NamingException {
		return this.getDirContext().search (name, filter, cons);
	}
	
	public NamingEnumeration search(String name, String filter, SearchControls cons) throws NamingException {
		return this.getDirContext().search (name, filter, cons);
	}
	
	public NamingEnumeration search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
		return this.getDirContext().search (name, filterExpr, filterArgs, cons);
	}
	
	public NamingEnumeration search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
		return this.getDirContext().search (name, filterExpr, filterArgs, cons);
	}
	
    /** This method check whether the Context is valid,
     *  if not it simply throws Exception
     *  @param javax.naming.Context context to be checked
     *  @exception NamingException
     */
    public final void checkContext () throws NamingException {
        // We simply call any context operation to see that the
        // context is correct
        String relativeRoot = (String) this.envTable.get (JndiRootNode.NB_ROOT);
        if (relativeRoot == null)
            relativeRoot = "";  // No I18N
        this.list(relativeRoot);
    }
	
	private DirContext getDirContext () throws javax.naming.OperationNotSupportedException {
		if (this.delegate instanceof DirContext) {
			return (DirContext) this.delegate;
		}
		else {
			throw new javax.naming.OperationNotSupportedException ();
		}			
	}

}

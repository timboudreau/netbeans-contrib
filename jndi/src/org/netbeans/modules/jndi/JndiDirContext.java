/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jndi;

import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.spi.InitialContextFactory;
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

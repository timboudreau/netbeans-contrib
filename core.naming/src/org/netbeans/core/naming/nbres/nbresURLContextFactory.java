/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 *
 * Portions of this file are copied from Sun Microsystems documentation
 * and thus following license applies:
 * 
 * Copyright 1997, 1998, 1999 Sun Microsystems, Inc. All Rights
 * Reserved.
 * 
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free,
 * license to use, modify and redistribute this software in source and
 * binary code form, provided that i) this copyright notice and license
 * appear on all copies of the software; and ii) Licensee does not 
 * utilize the software in a manner which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE 
 * HEREBY EXCLUDED.  SUN AND ITS LICENSORS SHALL NOT BE LIABLE 
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN 
 * NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER 
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT 
 * OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line
 * control of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and warrants
 * that it will not use or redistribute the Software for such purposes.  
 */

package org.netbeans.core.naming.nbres;

import javax.naming.spi.*;
import javax.naming.*;
import java.util.Hashtable;
import java.net.URL;

/**
 * URLContextFactory for protocol nbres:
 * @author David Strupl (partialy copied from
 *    http://java.sun.com/products/jndi/tutorial/provider/url/context.html)
 */
public class nbresURLContextFactory implements ObjectFactory {
    
    /** Creates a new instance of nbresURLContextFactory. According
     * to the JNDI spec. this class has to have public default
     * constructor.
     */
    public nbresURLContextFactory() {
    }
    
    /**
     *  The "nbres" url is of the form nbres:/<file name on sfs>.
     */
    public Object getObjectInstance(Object urlInfo, Name name, Context nameCtx,
    Hashtable env) throws Exception {
        
        // Case 1: urlInfo is null
        // This means to create a URL context that can accept
        // arbitrary "nbres" URLs.
        if (urlInfo == null) {
            return createURLContext(env);
        }
        
        // Case 2: urlInfo is a single string
        // This means to create/get the object named by urlInfo
        if (urlInfo instanceof String) {
            Context urlCtx = createURLContext(env);
            try {
                return urlCtx.lookup((String)urlInfo);
            } finally {
                urlCtx.close();
            }
        }
        
        // Case 3: urlInfo is an array of strings
        // This means each entry in array is equal alternative; create/get
        // the object named by one of the URls
        if (urlInfo instanceof String[]) {
            
            // Try each URL until lookup() succeeds for one of them.
            // If all URLs fail, throw one of the exceptions arbitrarily.
            String[] urls = (String[])urlInfo;
            if (urls.length == 0) {
                throw (new ConfigurationException(
                "nbresURLContextFactory: empty URL array"));
            }
            Context urlCtx = createURLContext(env);
            try {
                NamingException ne = null;
                for (int i = 0; i < urls.length; i++) {
                    try {
                        return urlCtx.lookup(urls[i]);
                    } catch (NamingException e) {
                        ne = e;
                    }
                }
                throw ne;
            } finally {
                urlCtx.close();
            }
        }
        
        // Case 4: urlInfo is of an unknown type
        // Provider-specific action: reject input
        
        throw new IllegalArgumentException(
        "argument must be a nbres URL string or an array of them");
    }
    
    protected Context createURLContext(Hashtable env) {
        return new NbresContext(env);
    }
    
    /** Context that delegates to the root context obtained from
     * context factory org.netbeans.core.naming.Jndi
     */
    private static class NbresContext implements Context {
        
        private Hashtable myEnv;
        
        public NbresContext(Hashtable env) {
            this.myEnv = env;
        }
        
        /**
         * Resolves 'name' into a target context with remaining name.
         * For example, with a JNDI URL "jndi://dnsname/rest_name",
         * this method resolves "jndi://dnsname/" to a target context,
         * and returns the target context with "rest_name".
         * The definition of "root URL" and how much of the URL to
         * consume is implementation specific.
         * If rename() is supported for a particular URL scheme,
         * getRootURLContext(), getURLPrefix(), and getURLSuffix()
         * must be in sync wrt how URLs are parsed and returned.
         *
         * For the "nbres" URL, the root URL is "nbres:/".
         */
        protected ResolveResult getRootURLContext(String url, Hashtable env)
        throws NamingException {
            if (!url.startsWith("nbres:/")) {
                throw new IllegalArgumentException(url + " is not a nbres URL");
            }
            
            String objName = url.length() > 7 ? url.substring(7) : null;
            
            // Represent object name as empty or single-component composite name.
            CompositeName remaining = new CompositeName();
            if (objName != null) {
                remaining.add(objName);
            }
            
            // Get handle to the static namespace that we use for testing.
            // In an actual implementation, this might be the root
            // namespace on a particular server.
            Context ctx = getDelegate(env);
            return (new ResolveResult(ctx, remaining));
        }
        
        /**
         * Returns the suffix of the url. The result should be identical to
         * that of calling getRootURLContext().getRemainingName(), but
         * without the overhead of doing anything with the prefix like
         * creating a context.
         *<p>
         * This method returns a Name instead of a String because to give
         * the provider an opportunity to return a Name (for example,
         * for weakly separated naming systems like COS naming).
         *<p>
         * The default implementation uses skips 'prefix', calls
         * UrlUtil.decode() on it, and returns the result as a single component
         * CompositeName.
         * Subclass should override if this is not appropriate.
         * This method is used only by rename().
         * If rename() is supported for a particular URL scheme,
         * getRootURLContext(), getURLPrefix(), and getURLSuffix()
         * must be in sync wrt how URLs are parsed and returned.
         *<p>
         * For many URL schemes, this method is very similar to URL.getFile(),
         * except getFile() will return a leading slash in the
         * 2nd, 3rd, and 4th cases. For schemes like "ldap" and "iiop",
         * the leading slash must be skipped before the name is an acceptable
         * format for operation by the Context methods. For schemes that treat the
         * leading slash as significant (such as "file"),
         * the subclass must override getURLSuffix() to get the correct behavior.
         * Remember, the behavior must match getRootURLContext().
         *
         * URL					Suffix
         * foo://host:port				<empty string>
         * foo://host:port/rest/of/name 		rest/of/name
         * foo:///rest/of/name			rest/of/name
         * foo:/rest/of/name			rest/of/name
         * foo:rest/of/name			rest/of/name
         */
        protected Name getURLSuffix(String prefix, String url) throws NamingException {
            String suffix = url.substring(prefix.length());
            if (suffix.length() == 0) {
                return new CompositeName();
            }
            
            if (suffix.charAt(0) == '/') {
                suffix = suffix.substring(1); // skip leading slash
            }
            
            // Note: Simplified implementation; a real implementation should
            // transform any URL-encoded characters into their Unicode char
            // representation
            return new CompositeName().add(suffix);
        }
        
        /**
         * Finds the prefix of a URL.
         * Default implementation looks for slashes and then extracts
         * prefixes using String.substring().
         * Subclass should override if this is not appropriate.
         * This method is used only by rename().
         * If rename() is supported for a particular URL scheme,
         * getRootURLContext(), getURLPrefix(), and getURLSuffix()
         * must be in sync wrt how URLs are parsed and returned.
         *<p>
         * URL					Prefix
         * foo://host:port				foo://host:port
         * foo://host:port/rest/of/name 		foo://host:port
         * foo:///rest/of/name			foo://
         * foo:/rest/of/name			foo:
         * foo:rest/of/name			foo:
         */
        protected String getURLPrefix(String url) throws NamingException {
            int start = url.indexOf(":");
            
            if (start < 0) {
                throw new OperationNotSupportedException("Invalid URL: " + url);
            }
            ++start; // skip ':'
            
            if (url.startsWith("//", start)) {
                start += 2;  // skip double slash
                
                // find last slash
                int posn = url.indexOf("/", start);
                if (posn >= 0) {
                    start = posn;
                } else {
                    start = url.length();  // rest of URL
                }
            }
            
            // else 0 or 1 initial slashes; start is unchanged
            return url.substring(0, start);
        }
        
        /**
         * Determines whether two URLs are the same.
         * Default implementation uses String.equals().
         * Subclass should override if this is not appropriate.
         * This method is used by rename().
         */
        protected boolean urlEquals(String url1, String url2) {
            return url1.equals(url2);
        }
        
        /**
         * Gets the context in which to continue the operation. This method
         * is called when this context is asked to process a multicomponent
         * Name in which the first component is a URL.
         * Treat the first component like a junction: resolve it and then use
         * NamingManager.getContinuationContext() to get the target context in
         * which to operate on the remainder of the name (n.getSuffix(1)).
         */
        protected Context getContinuationContext(Name n) throws NamingException {
            Object obj = lookup(n.get(0));
            CannotProceedException cpe = new CannotProceedException();
            cpe.setResolvedObj(obj);
            cpe.setEnvironment(myEnv);
            return NamingManager.getContinuationContext(cpe);
        }
        
        public Object lookup(String name) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                return ctx.lookup(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public Object lookup(Name name) throws NamingException {
            if (name.size() == 1) {
                return lookup(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    return ctx.lookup(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public void bind(String name, Object obj) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                ctx.bind(res.getRemainingName(), obj);
            } finally {
                ctx.close();
            }
        }
        
        public void bind(Name name, Object obj) throws NamingException {
            if (name.size() == 1) {
                bind(name.get(0), obj);
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    ctx.bind(name.getSuffix(1), obj);
                } finally {
                    ctx.close();
                }
            }
        }
        
        public void rebind(String name, Object obj) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                ctx.rebind(res.getRemainingName(), obj);
            } finally {
                ctx.close();
            }
        }
        
        public void rebind(Name name, Object obj) throws NamingException {
            if (name.size() == 1) {
                rebind(name.get(0), obj);
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    ctx.rebind(name.getSuffix(1), obj);
                } finally {
                    ctx.close();
                }
            }
        }
        
        public void unbind(String name) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                ctx.unbind(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public void unbind(Name name) throws NamingException {
            if (name.size() == 1) {
                unbind(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    ctx.unbind(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public void rename(String oldName, String newName) throws NamingException {
            String oldPrefix = getURLPrefix(oldName);
            String newPrefix = getURLPrefix(newName);
            if (!urlEquals(oldPrefix, newPrefix)) {
                throw new OperationNotSupportedException(
                "Renaming using different URL prefixes not supported : " +
                oldName + " " + newName);
            }
            
            ResolveResult res = getRootURLContext(oldName, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                ctx.rename(res.getRemainingName(), getURLSuffix(newPrefix, newName));
            } finally {
                ctx.close();
            }
        }
        
        public void rename(Name name, Name newName) throws NamingException {
            if (name.size() == 1) {
                if (newName.size() != 1) {
                    throw new OperationNotSupportedException(
                    "Renaming to a Name with more components not supported: " + newName);
                }
                rename(name.get(0), newName.get(0));
            } else {
                // > 1 component with 1st one being URL
                // URLs must be identical; cannot deal with diff URLs
                if (!urlEquals(name.get(0), newName.get(0))) {
                    throw new OperationNotSupportedException(
                    "Renaming using different URLs as first components not supported: " +
                    name + " " + newName);
                }
                
                Context ctx = getContinuationContext(name);
                try {
                    ctx.rename(name.getSuffix(1), newName.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public NamingEnumeration list(String name)	throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                return ctx.list(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public NamingEnumeration list(Name name) throws NamingException {
            if (name.size() == 1) {
                return list(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    return ctx.list(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public NamingEnumeration listBindings(String name)
        throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                return ctx.listBindings(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public NamingEnumeration listBindings(Name name) throws NamingException {
            if (name.size() == 1) {
                return listBindings(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    return ctx.listBindings(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public void destroySubcontext(String name) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                ctx.destroySubcontext(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public void destroySubcontext(Name name) throws NamingException {
            if (name.size() == 1) {
                destroySubcontext(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    ctx.destroySubcontext(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public Context createSubcontext(String name) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                return ctx.createSubcontext(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public Context createSubcontext(Name name) throws NamingException {
            if (name.size() == 1) {
                return createSubcontext(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    return ctx.createSubcontext(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public Object lookupLink(String name) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                return ctx.lookupLink(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public Object lookupLink(Name name) throws NamingException {
            if (name.size() == 1) {
                return lookupLink(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    return ctx.lookupLink(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public NameParser getNameParser(String name) throws NamingException {
            ResolveResult res = getRootURLContext(name, myEnv);
            Context ctx = (Context)res.getResolvedObj();
            try {
                return ctx.getNameParser(res.getRemainingName());
            } finally {
                ctx.close();
            }
        }
        
        public NameParser getNameParser(Name name) throws NamingException {
            if (name.size() == 1) {
                return getNameParser(name.get(0));
            } else {
                Context ctx = getContinuationContext(name);
                try {
                    return ctx.getNameParser(name.getSuffix(1));
                } finally {
                    ctx.close();
                }
            }
        }
        
        public String composeName(String name, String prefix)
        throws NamingException {
            if (prefix.equals("")) {
                return name;
            } else if (name.equals("")) {
                return prefix;
            } else {
                return (prefix + "/" + name);
            }
        }
        
        public Name composeName(Name name, Name prefix) throws NamingException {
            Name result = (Name)prefix.clone();
            result.addAll(name);
            return result;
        }
        
        public String getNameInNamespace() throws NamingException {
            return ""; // A URL context's name is ""
        }
        
        public Object removeFromEnvironment(String propName)
        throws NamingException {
            if (myEnv == null) {
                return null;
            }
            myEnv = (Hashtable)myEnv.clone();
            return myEnv.remove(propName);
        }
        
        public Object addToEnvironment(String propName, Object propVal)
        throws NamingException {
            myEnv = (myEnv == null) ?
            new Hashtable(11, 0.75f) : (Hashtable)myEnv.clone();
            return myEnv.put(propName, propVal);
        }
        
        public Hashtable getEnvironment() throws NamingException {
            if (myEnv == null) {
                return new Hashtable(5, 0.75f);
            } else {
                return (Hashtable)myEnv.clone();
            }
        }
        
        public void close() throws NamingException {
        }
        /**
         * Returns a delegate that performs almost all the operations
         * on this context.
         */
        private Context getDelegate(Hashtable env) throws NamingException {
            if (env == null) {
                env = new Hashtable();
            }
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.netbeans.core.naming.Jndi"); // NOI18N
            return new InitialContext(env);
        }
    }
}

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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.api.dynactions;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Factory which can produce an array of Actions, presumably given some
 * contextual information with which to look them up.
 *
 * @author Tim Boudreau
 */
public abstract class ActionFactory {
    public abstract Action[] getActions();
    /**
     * Creates a merged ActionFactory that combines several others
     * @param factories Other ActionFactories
     * @return An Action Factory that returns the sum of all actions returned
     * by the passed action factories
     */
    public static ActionFactory create (ActionFactory... factories) {
        return new CompoundActionFactory(factories);
    }
    
    /**
     * Create an ActionFactory over a folder in the system filesystem (or
     * if run in the bare platform, META-INF/services
     * 
     * @param context The path to where actions should be looked up
     * @return An action factory
     */
    public static ActionFactory context(String context) {
        return new SfsActionFactory(context);
    }

    /**
     * Convenience method for getting a list of actions based on 
     * Lookups.forPath().
     * @param context
     * @return
     */
    public static List<Action> getActions (String context) {
        Lookup lkp = Lookups.forPath(context);
        return new ArrayList <Action> (lkp.lookupAll(Action.class));
    }
    
    /**
     * Create an ActionFactory which will find actions based on the contents
     * of a lookup, as follows:  The root folder is passed in, and is the
     * base path to the parent folder for actions.
     * <p/>
     * Underneath that folder are folders which identify fully qualified
     * class names, with .'s replaced with -'s.  So, for example, if you want
     * to register actions for anything containing an instance of com.foo.Bar,
     * you would register a file such as
     * <pre>
     * rootfolder/com-foo-Bar/MyBarAction.instance
     * </pre>
     * <code>ObjectLoader</code> (a class which handles loading expensive-to-load
     * objects on a background thread) is handled specially, with one additional
     * layer of indirection:  You can register objects against <i>the type that
     * an ObjectLoader will load</i>.  Say you have a loader for Foo objects.
     * You want to write actions that can run against com.foo.Foo objects, but which will
     * not trigger the object actually being loaded unless the action is 
     * really invoked.  So you create a file:
     * <pre>
     * rootfolder/org-netbeans-api-ObjectLoader/com-foo-Foo/MyActionThatRunsAgainstFoo.instance
     * </pre>
     * 
     * @param provider A thing which has a getLookup() method;  the lookup's
     * contents will determine the actions available
     * @param rootFolder A folder which is the base folder for finding folders
     * related to specific types
     * @return A LookupActionFactory
     */
    public static LookupActionFactory lookup (Lookup.Provider provider, String rootFolder) {
        return new LookupActionFactory(provider, rootFolder);
    }
}

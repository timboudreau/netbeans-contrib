/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.api.enode;

import java.util.*;
import javax.swing.Action;
import org.netbeans.modules.enode.ExtensibleActionsImpl;
import org.netbeans.modules.enode.TimedSoftReference;

/**
 * This class computes list of actions from the content of the configuration
 * storage (system file system, registry). It is used from ExtensibleNode to
 * keep track of its actions. You can obtain a reference to an initialized
 * object of this class using the static factory methods getInstance.
 * <p>
 * The folders (contexts) where the actions are stored begin with prefix
 * "/ExtensibleNode/Actions/". 
 *
 * @author David Strupl
 */
public abstract class ExtensibleActions {
    /**
     * Maps List<String> --> ExtensibleActions. The key is list of
     * folders passed as paths parameter or computed by
     * ExtensibleNode.computeHierarchicalPaths().
     */
    private static Map cache = new HashMap();
    
    /**
     * It is impossible to create instances of this class. Please use
     * the static factory methods instead.
     */
    protected ExtensibleActions() {
        if (! getClass().equals(ExtensibleActionsImpl.class)) {
            throw new IllegalStateException("You cannot create a subclass of this class. Please read the JavaDoc comment"); // NOI18N
        }
    }
    
    /**
     * This method computes the list (array) of actions configured
     * using the paths (or path) parameter to getInstance.
     * @return array of actions
     */
    public abstract Action[] getActions();

    /**
     * This method finds/creates an instance of ExtensibleActions bound to given
     * path. Please use this method instead of constructor - it allows
     * caching/sharing the instances of this class.
     *
     * @param path Path to the folder where the configured actions
     * are stored.
     * @param recurse If set to true the parent folders contents
     * are also added to the result.
     * @return Fully initialized ExtensibleActions object
     * configured using the parameters provided.
     */
    public static ExtensibleActions getInstance(String path, boolean recurse) {
        String[] paths = null;
        if (recurse) {
            paths = ExtensibleNode.computeHierarchicalPaths(path);
        } else {
            paths = new String[] { path };
        }
        return getInstance(paths);
    }

    /**
     * This method finds/creates an instance of ExtensibleActions bound to given
     * paths. Please use this method instead of constructor - it allows
     * caching/sharing the instances of this class.
     *
     * @param paths Paths to the folder where the configured actions
     * are stored.
     * @return Fully initialized ExtensibleActions object
     * configured using the parameters provided.
     */
    public static ExtensibleActions getInstance(String[] paths) {
        // We use list as the key. It ensures that the hashCode will
        // be same for the arrays of equals String instances.
        Object key = Arrays.asList(paths);
        
        TimedSoftReference ref = null;
        synchronized (cache) {
            ref = (TimedSoftReference)cache.get(key);
        }
        ExtensibleActions instance = null;
        if (ref != null) {
            instance = (ExtensibleActions)ref.get();
        }
        if (instance == null) {
            instance = new ExtensibleActionsImpl(paths);
            synchronized (cache) {
                cache.put(key, new TimedSoftReference(instance, cache, key));
            }
        }
        return instance;
    }
}


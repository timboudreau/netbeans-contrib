/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.spi;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * Metadata for all automatic projects.
 * Stored as string key-value pairs for ease of storage and debugging.
 * Static class because the cache is shared among all projects.
 * (Information collected by one project may well be needed by a nested project etc.)
 */
public class Cache {

    public static final String ENCODING = "#encoding";
    public static final String ACTION = "#action.";

    private static final Logger LOG = Logger.getLogger(Cache.class.getName());

    private static final File CACHE;
    static {
        String userdir = System.getProperty("netbeans.user");
        if (userdir != null) {
            CACHE = new File(userdir, "var/cache/autoprojects.properties");
        } else {
            CACHE = null;
        }
    }

    private static final PropertyChangeSupport pcs = new PropertyChangeSupport(Cache.class);

    private Cache() {}

    private static EditableProperties data = null;
    private static final RequestProcessor.Task writeTask = RequestProcessor.getDefault().create(new Runnable() {
        boolean ok = true;
        public void run() {
            if (!ok) {
                return;
            }
            synchronized (Cache.class) {
                CACHE.getParentFile().mkdirs();
                try {
                    OutputStream os = new FileOutputStream(CACHE);
                    try {
                        data.store(os);
                    } finally {
                        os.close();
                    }
                } catch (IOException x) {
                    Exceptions.printStackTrace(x);
                    ok = false;
                }
            }
        }
    });

    public static synchronized String get(String key) {
        if (data == null) {
            data = new EditableProperties(true);
            if (CACHE.isFile()) {
                try {
                    InputStream is = new FileInputStream(CACHE);
                    try {
                        data.load(is);
                    } finally {
                        is.close();
                    }
                } catch (IOException x) {
                    Exceptions.printStackTrace(x);
                }
            }
        }
        return data.get(key);
    }

    public static synchronized Iterable<Map.Entry<String,String>> pairs() {
        get("");
        return new TreeMap<String,String>(data).entrySet();
    }

    public static void put(String key, String value) {
        Parameters.notNull("key", key);
        LOG.log(Level.FINE, "put({0}, {1})", new Object[] {key, value});
        String oldValue;
        synchronized (Cache.class) {
            if (Utilities.compareObjects(value, oldValue = data.get(key))) {
                return;
            }
            if (value != null) {
                String[] pieces = value.split("(?<=[:;])");
                if (pieces.length > 1) {
                    data.setProperty(key, pieces);
                } else {
                    data.put(key, value);
                }
            } else {
                data.remove(key);
            }
        }
        writeTask.schedule(5000);
        pcs.firePropertyChange(key, oldValue, value);
    }

    public static void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public static void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    // XXX will also want methods in AutomaticProject to get a key, looking in $basedir/netbeans.properties (or whatever) for overrides

}

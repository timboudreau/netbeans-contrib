/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.ErrorManager;

import java.util.*;
import java.io.*;

/**
 * Cache of search results. It caches files that need not to be
 * scanned because have not contained any match.
 *
 * @author Petr Kuzel
 */
final class Cache {

    private static Map cache;

    public static void put(SuggestionContext ctx, List result) {
        FileObject fo = ctx.getFileObject();
        String name = createKey(fo);
        if (name == null) return;
        if (result == null || result.isEmpty()) {
            long timestamp = fo.lastModified().getTime();
            cache().put(name, new long[]{timestamp, System.currentTimeMillis()});
        } else {
            cache().remove(name);
        }
    }

    public static List get(SuggestionContext ctx) {
        FileObject fo = ctx.getFileObject();
        String name = createKey(fo);
        if (name == null) return null;
        Object hit = cache().get(name);
        if (hit != null) {
            if (((long[])hit)[0] >= fo.lastModified().getTime()) {
                return Collections.EMPTY_LIST;
            } else {
                cache().remove(name);
            }
        }
        return null;
    }

    private static String createKey(FileObject fo) {
        File file = FileUtil.toFile(fo);
        if (file != null) {
            try {
                return file.getCanonicalPath();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static Map cache() {
        if (cache == null) {
            load();
        }
        return cache;
    }

    public static void load() {
        ObjectInputStream ois = null;
        try {
            File file = getCacheFile(false);
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            ois = new ObjectInputStream(in);
            if (ois.readInt() == 1) {
                cache = (Map) ois.readObject();
            }
        } catch (IOException io) {
            // null cache
        } catch (ClassNotFoundException e) {
            // null cache
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        if (cache == null) {
            cache = new HashMap(1113);
        }

        // eliminate very old entries and entries older than recent settings
        long staleTime = System.currentTimeMillis() - 1000*60*60*24*17;  //17 days
        if (Settings.getDefault().getModificationTime() > staleTime) {
            staleTime = Settings.getDefault().getModificationTime();
        }

        Iterator it = cache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (((long[])entry.getValue())[1] < staleTime) {
                it.remove();
            }
        }

    }

    // XXX what are the right events to call this?
    // now it uses SourceTasksView events and notifyFinished event (not called)
    public static void store() {
        if (cache == null) return;
        try {
            File file = getCacheFile(true);
            OutputStream os = new FileOutputStream(file);
            os = new BufferedOutputStream(os);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeInt(1);

            // version 1 format
            // map <string (canonical name), long[2] (timestamp, createdOn)>
            oos.writeObject(cache);
            oos.close();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        } finally {
            cache = null;
        }
    }

    private static File getCacheFile(boolean create) throws IOException {
        String loc = System.getProperty("netbeans.user") + // NOI18N
            File.separatorChar + "cache" + File.separatorChar + "all-todos.ser";
        File file = new File(loc);
        if (create) {
            if (!file.exists()) {
                File parent = file.getParentFile();
                parent.mkdirs();
                file.createNewFile();
            }
        }
        return file;
    }

}

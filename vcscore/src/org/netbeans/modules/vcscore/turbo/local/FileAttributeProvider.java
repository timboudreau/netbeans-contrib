/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo.local;

import org.openide.filesystems.FileObject;

import java.util.List;
import java.util.ArrayList;

/**
 * SPI, extension point allowing third parties to redefine
 * attribute reading and writing. Must be registered
 * in default {@link org.openide.util.Lookup}.
 * <p>
 * It must generally deternime that they
 * support given FileObject and attribute pair
 * and if so then perform action.
 * <p>
 * Two providers can theoreticaly clash and support
 * the same attribute for the same fileobject. The
 * key point is that attribute meaning must be precisely
 * defined guaranteeing that two independent providers
 * respond with exactly same value hence making irrelevant
 * which one is actually choosen.
 * <p>
 * Providers are not required to cache results. Any
 * speculative results (per folder batching) can be
 * implemented using FileAttributeQuery.writeAttribute()
 * ignoring this self-initiated write.
 *
 * @author Petr Kuzel
 */
public interface FileAttributeProvider {

    /**
     * Reports if an attribute is supported by the implementation.
     */
    boolean recognizesAttribute(String name);

    /**
     * Reports if the file object is supported by the implementation.
     */
    boolean recognizesFileObject(FileObject fo);

    /**
     * Reads given attribute for given fileobject. No method
     * parameter may be referenced after method execution finishes.
     *
     * @param fo identifies source FileObject, never <code>null</code>
     * @param name identifies requested attribute, never <code>null</code>
     * @param memoryCache can store speculative results
     * @return attribute value or <code>null</code> if it does not exist.
     */
    Object readAttribute(FileObject fo, String name, MemoryCache memoryCache);

    /**
     * Writes given attribute. No method
     * parameter may be referenced after method execution finishes.
     *
     * @param fo identifies target file object, never <code>null</code>
     * @param name identifies attribute, never <code>null</code>
     * @param value actual attribute value that should be stored or <code>null</code> for removing it
     * @return <code>false</code> on write failure if provider denies the value. On I/O error it
     * returns <code>true</code>.
     */
    boolean writeAttribute(FileObject fo, String name, Object value);

    /**
     * Provides direct access to memory layer without
     * delegating to providers layer.
     */
    public static final class MemoryCache {

        private final boolean enabled;

        private final List speculative;

        private MemoryCache(boolean enabled) {
            this.enabled = enabled;
            speculative = null;
        }

        private MemoryCache(List speculative) {
            enabled = true;
            this.speculative = speculative;
        }

        /**
         * Creates instance intercepting speculative results.
         * @param speculative add()s speculative results into it
         */
        static MemoryCache getDefault(List speculative) {
            return new MemoryCache(speculative);
        }

        static MemoryCache getDefault() {
            return new MemoryCache(true);
        }

        /** T9Y entry point. */
        static MemoryCache getTest() {
            return new MemoryCache(false);
        }

        /**
         * Writes speculative entry into memory layer.
         */
        public void cacheAttribute(FileObject fo, String name, Object value) {
            if (enabled == false) return;
            Memory.put(fo, name, value);
            if (speculative != null) speculative.add(new Object[] {fo, name, value});
        }

        /** Return speculative results <code>[]{FileObject,String,Object}</code> silently inserted into memory */
        List getSpeculative() {
            return speculative;
        }
    }
}

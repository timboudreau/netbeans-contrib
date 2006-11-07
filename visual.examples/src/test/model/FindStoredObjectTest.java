/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package test.model;

import org.netbeans.api.visual.model.ObjectScene;

/**
 * @author David Kaspar
 */
public class FindStoredObjectTest {

    public static void main (String[] args) {
        ObjectScene scene = new ObjectScene ();

        scene.addObject (new Obj (1), null);
        scene.addObject (new Obj (2), null);
        scene.addObject (new Obj (3), null);
        scene.addObject (new Obj (4), null);
        scene.addObject (new Obj (5), null);

        System.out.println ("Searching for stored obj 5:");
        System.out.println ("found: " + scene.findStoredObject (new Obj (5)));
        System.out.println ("Searching for stored obj 99999:");
        System.out.println ("found: " + scene.findStoredObject (new Obj (99999)));
    }

    private static class Obj {

        private int value;

        public Obj (int value) {
            this.value = value;
        }

        public int hashCode () {
            return value;
        }

        public boolean equals (Object obj) {
            System.out.println ("Comparing: " + this + " with " + obj);
            return obj instanceof Obj  &&  this.value == ((Obj) obj).value;
        }

        public String toString () {
            return "Obj[" + System.identityHashCode (this) + "|" + value + "]"; // NOI18N
        }

    }

}

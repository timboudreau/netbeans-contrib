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
package test.listeners;

import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import test.SceneSupport;
import test.object.ObjectTest;

import java.util.Set;

/**
 * @author David Kaspar
 */
public class ObjectSceneListenerTest extends ObjectTest implements ObjectSceneListener {

    public static void main (String[] args) {
        ObjectSceneListenerTest scene = new ObjectSceneListenerTest ();
        scene.addNode ("n1");
        scene.addNode ("n2");
        scene.addNode ("n3");
        scene.addNode ("n4");
        scene.moveTo (null);
        SceneSupport.show (scene);
    }

    public ObjectSceneListenerTest () {
        addObjectSceneListener (this, ObjectSceneEventType.values ());
    }

    public void objectAdded (ObjectSceneEvent event, Object addedObject) {
        System.out.println ("addedObject = " + addedObject);
    }

    public void objectRemoved (ObjectSceneEvent event, Object removedObject) {
        System.out.println ("removedObject = " + removedObject);
    }

    public void objectStateChanged (ObjectSceneEvent event, Object changedObject, ObjectState previousState, ObjectState newState) {
        System.out.println ("changedObject = " + changedObject + " | previousState = " + previousState + " | newState = " + newState);
    }

    public void selectionChanged (ObjectSceneEvent event, Set<Object> previousSelection, Set<Object> newSelection) {
        System.out.println ("previousSelection = " + previousSelection + " | newSelection = " + newSelection);
    }

    public void highlightingChanged (ObjectSceneEvent event, Set<Object> previousHighlighting, Set<Object> newHighlighting) {
        System.out.println ("previousHighlighting = " + previousHighlighting + " | newHighlighting = " + newHighlighting);
    }

    public void hoverChanged (ObjectSceneEvent event, Object previousHoveredObject, Object newHoveredObject) {
        System.out.println ("previousHoveredObject = " + previousHoveredObject + " | newHoveredObject = " + newHoveredObject);
    }

    public void focusChanged (ObjectSceneEvent event, Object previousFocusedObject, Object newFocusedObject) {
        System.out.println ("previousFocusedObject = " + previousFocusedObject + " | newFocusedObject = " + newFocusedObject);
    }

}

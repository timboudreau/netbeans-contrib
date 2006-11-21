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
package test.keyboard;

import test.SceneSupport;
import org.netbeans.api.visual.action.ActionFactory;

/**
 * @author David Kaspar
 */
public class KeyboardTest {

    public static void main (String[] args) {
        KeyboardGraphScene scene = new KeyboardGraphScene ();
        scene.getActions ().addAction (ActionFactory.createCycleObjectSceneFocusAction ());

        scene.addNode ("A1");
        scene.addNode ("B2");
        scene.addNode ("C3");
        scene.addNode ("D4");
        scene.addEdge ("A1", "B2");
        scene.addEdge ("A1", "C3");
        scene.addEdge ("C3", "D4");

        SceneSupport.show (scene);
    }

}

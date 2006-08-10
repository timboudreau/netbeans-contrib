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
package test.vmd;

import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;
import java.util.Arrays;

/**
 * @author David Kaspar
 */
public class VMDCollisionTest {

    private static final Image IMAGE_LIST = Utilities.loadImage ("test/resources/list_32.png"); // NOI18N
    private static final Image IMAGE_CANVAS = Utilities.loadImage ("test/resources/custom_displayable_32.png"); // NOI18N
    private static final Image IMAGE_COMMAND = Utilities.loadImage ("test/resources/command_16.png"); // NOI18N
    private static final Image IMAGE_ITEM = Utilities.loadImage ("test/resources/item_16.png"); // NOI18N
    private static final Image GLYPH_PRE_CODE = Utilities.loadImage ("test/resources/preCodeGlyph.png"); // NOI18N
    private static final Image GLYPH_POST_CODE = Utilities.loadImage ("test/resources/postCodeGlyph.png"); // NOI18N
    private static final Image GLYPH_CANCEL = Utilities.loadImage ("test/resources/cancelGlyph.png"); // NOI18N

    private static int nodeID = 1;
    private static int edgeID = 1;

    public static void main (String[] args) {
        VMDGraphScene scene = new VMDGraphScene ();

        String mobile = createNode (scene, 100, 100, IMAGE_LIST, "menu", "List", null);
        createPin (scene, mobile, "start", IMAGE_ITEM, "Start", "Element");

        String game = createNode (scene, 600, 100, IMAGE_CANVAS, "gameCanvas", "MyCanvas", Arrays.asList (GLYPH_PRE_CODE, GLYPH_CANCEL, GLYPH_POST_CODE));
        createPin (scene, game, "ok", IMAGE_COMMAND, "okCommand1", "Command");

        createEdge (scene, "start", game);
        createEdge (scene, "ok", mobile);

        SceneSupport.show (scene);
    }

    private static String createNode (VMDGraphScene scene, int x, int y, Image image, String name, String type, java.util.List<Image> glyphs) {
        String nodeID = "node" + VMDCollisionTest.nodeID ++;
        VMDNodeWidget widget = (VMDNodeWidget) scene.addNode (nodeID);
        widget.setPreferredLocation (new Point (x, y));
        widget.setNodeProperties (image, name, type, glyphs);
        scene.addPin (nodeID, nodeID + VMDGraphScene.PIN_ID_DEFAULT_SUFFIX);
        return nodeID;
    }

    private static void createPin (VMDGraphScene scene, String nodeID, String pinID, Image image, String name, String type) {
        ((VMDPinWidget) scene.addPin (nodeID, pinID)).setProperties (name, null);
    }

    private static void createEdge (VMDGraphScene scene, String sourcePinID, String targetNodeID) {
        String edgeID = "edge" + VMDCollisionTest.edgeID ++;
        scene.addEdge (edgeID);
        scene.setEdgeSource (edgeID, sourcePinID);
        scene.setEdgeTarget (edgeID, targetNodeID + VMDGraphScene.PIN_ID_DEFAULT_SUFFIX);
    }

}

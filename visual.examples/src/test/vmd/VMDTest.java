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

import org.netbeans.api.visual.graph.EdgeController;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeController;
import org.openide.util.Utilities;
import test.SceneSupport;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author David Kaspar
 */
public class VMDTest {

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

        VMDNodeController mobile = createNode (scene, 100, 100, IMAGE_LIST, "menu", "List", null);
        createPin (scene, mobile, "start", IMAGE_ITEM, "Start", "Element");
        createPin (scene, mobile, "resume", IMAGE_ITEM, "Resume", "Element");

        VMDNodeController menu = createNode (scene, 400, 400, IMAGE_LIST, "menu", "List", null);
        createPin (scene, menu, "game", IMAGE_ITEM, "New Game", "Element");
        createPin (scene, menu, "options", IMAGE_ITEM, "Options", "Element");
        createPin (scene, menu, "help", IMAGE_ITEM, "Help", "Element");
        createPin (scene, menu, "exit", IMAGE_ITEM, "Exit", "Element");

        VMDNodeController game = createNode (scene, 600, 100, IMAGE_CANVAS, "gameCanvas", "MyCanvas", Arrays.asList (GLYPH_PRE_CODE, GLYPH_POST_CODE, GLYPH_CANCEL));
        createPin (scene, game, "ok", IMAGE_COMMAND, "okCommand1", "Command");
        createPin (scene, game, "cancel", IMAGE_COMMAND, "cancelCommand1", "Command");

        createEdge (scene, mobile, "start", menu);
        createEdge (scene, mobile, "resume", menu);

        createEdge (scene, menu, "game", game);
        createEdge (scene, menu, "exit", mobile);

        createEdge (scene, game, "ok", menu);
        createEdge (scene, game, "cancel", menu);

        SceneSupport.show (scene);
    }

    private static VMDNodeController createNode (VMDGraphScene scene, int x, int y, Image image, String name, String type, List<Image> glyphs) {
        VMDNodeController nodeController = scene.addNode ("node" + nodeID ++);
        nodeController.getMainWidget ().setPreferredLocation (new Point (x, y));
        nodeController.setNodeProperties (image, name, type, glyphs);
        scene.addPin (nodeController, VMDGraphScene.PIN_ID_DEFAULT);
        return nodeController;
    }

    private static void createPin (VMDGraphScene scene, VMDNodeController nodeController, String pinID, Image image, String name, String type) {
        scene.addPin (nodeController, pinID).setProperties (image, name, type);
    }

    private static void createEdge (VMDGraphScene scene, VMDNodeController sourceNodeController, String sourcePin, VMDNodeController targetNodeController) {
        EdgeController.StringEdge edge = scene.addEdge ("edge" + edgeID ++);
        scene.setEdgeSource (edge, scene.getPinController (sourceNodeController, sourcePin));
        scene.setEdgeTarget (edge, scene.getPinController (targetNodeController, VMDGraphScene.PIN_ID_DEFAULT));
    }

}

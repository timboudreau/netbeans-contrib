/**
 * Project Looking Glass
 *
 * $RCSfile$
 *
 * Copyright (c) 2004, Sun Microsystems, Inc., All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder. *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$A
 * $State$
 */
package org.netbeans.modules.venice.viewer.lg3d;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import org.jdesktop.lg3d.scenemanager.utils.SceneControl;
import org.jdesktop.lg3d.scenemanager.utils.SceneManagerBase;
import org.jdesktop.lg3d.scenemanager.utils.appcontainer.NaturalMotionF3DAnimationFactory;
import org.jdesktop.lg3d.scenemanager.utils.background.Background;
import org.jdesktop.lg3d.scenemanager.utils.background.LayeredImageBackground;
import org.jdesktop.lg3d.scenemanager.utils.background.ModelBackground;
import org.jdesktop.lg3d.scenemanager.utils.background.PanoImageBackground;
import org.jdesktop.lg3d.scenemanager.utils.event.BackgroundChangeRequestEvent;
import org.jdesktop.lg3d.scenemanager.utils.event.ScreenResolutionChangedEvent;
import org.jdesktop.lg3d.sg.Appearance;
import org.jdesktop.lg3d.sg.BoundingBox;
import org.jdesktop.lg3d.utils.action.ActionNoArg;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimationFactory;
import org.jdesktop.lg3d.utils.component.Pseudo3DIcon;
import org.jdesktop.lg3d.utils.eventadapter.MouseClickedEventAdapter;
import org.jdesktop.lg3d.utils.layoutmanager.HorizontalLayout;
import org.jdesktop.lg3d.utils.layoutmanager.HorizontalReorderableLayout;
import org.jdesktop.lg3d.utils.shape.GlassyPanel;
import org.jdesktop.lg3d.utils.shape.PickableRegion;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.event.LgEventConnector;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.LgEventListener;
import org.jdesktop.lg3d.wg.event.LgEventSource;
import org.jdesktop.lg3d.wg.event.MouseButtonEvent3D;
import org.jdesktop.lg3d.scenemanager.utils.taskbar.Taskbar;
import org.jdesktop.lg3d.scenemanager.utils.taskbar.TaskbarItemConfig;
import org.jdesktop.lg3d.scenemanager.utils.taskbar.ThumbnailLayout;


public class VeniceTaskbar extends Taskbar {
    private static float barHeight = 0.034f;
    private static float barDepth = 0.002f;
    private static float barZ = -0.04f;
    private static float thumbnailZ = -0.023f;
    private static float iconSpacing = 0.0025f;
    private static Appearance barApp
	= new SimpleAppearance(
	    0.6f, 1.0f, 0.6f, 1.0f,
	    SimpleAppearance.DISABLE_CULLING);
    
    private SceneControl scenemanager;
    private GlassyPanel bottomBar;
    private Container3D appThumbnails;
    private Container3D shortcuts;
    private Container3D themes;
    
    public VeniceTaskbar() {
    }
    
    public void initialize(final SceneControl scenemanager) {
        super.initialize(scenemanager);
        this.scenemanager = scenemanager;
        setName("GlassyTaskBar");
        setMouseEventSource(MouseButtonEvent3D.class, true);
        
        final float width = scenemanager.getWidth();
        final float height = scenemanager.getHeight();
        
	setPreferredSize(new Vector3f(width, barHeight, barHeight));
	bottomBar
	    = new GlassyPanel(
		width, 
		barHeight,
		barDepth, 
                barDepth * 0.1f,
		barApp);
        
        // Move the bounds of the bottomBar back so that the transparency sorting
        // in Java 3D renders it first and the icons second.
        // This also requires a bug fix which is in Java 3D 1.3.2
        bottomBar.setBoundsAutoCompute(false);
        bottomBar.setBounds(
                new BoundingBox(
                    new Point3f(-0.5f * width, -0.5f * barHeight, -0.5f), 
                    new Point3f( 0.5f * width,  0.5f * barHeight,  0.0f)));

	Component3D bottomBarComp = new Component3D();
	bottomBarComp.addChild(bottomBar);
	bottomBarComp.setRotationAxis(1.0f, 0.0f, 0.0f);
	bottomBarComp.setRotationAngle((float)Math.toRadians(-90));
	bottomBarComp.setTranslation(0.0f, barHeight * -0.51f, barHeight * -0.3f);
        bottomBarComp.setName("BottomBarComp");
	Container3D deco = new Container3D();
	deco.addChild(bottomBarComp);
        deco.setName("TaskbarDeco");
	setDecoration(deco);
        
        // TODO -- the following configurations are to be done via
        // a configuration file.
	shortcuts = new Container3D();
        shortcuts.setPreferredSize(new Vector3f(width, barHeight, barHeight));//FIXME
	shortcuts.setLayout(
            new HorizontalReorderableLayout(
                HorizontalLayout.AlignmentType.LEFT, iconSpacing, 
                new NaturalMotionF3DAnimationFactory(150)));
        
	addChild(shortcuts);
        
        // Listen for Tapps and add them to the toolbar
        LgEventConnector.getLgEventConnector().addListener(
            LgEventSource.ALL_SOURCES,
            new LgEventListener() {
                public void processEvent( final LgEvent evt ) {
                    TaskbarItemConfig config = (TaskbarItemConfig)evt;
                    shortcuts.addChild(config.createItem());
                }
                public Class<LgEvent>[] getTargetEventClasses() {
                    return new Class[] {TaskbarItemConfig.class};
                }
            });
        
	themes = new Container3D();
        themes.setPreferredSize(new Vector3f(width, barHeight, barHeight));//FIXME
	themes.setLayout(
            new HorizontalLayout(
                HorizontalLayout.AlignmentType.RIGHT, iconSpacing,
                new NaturalMotionAnimationFactory(150)));
        
        LgEventConnector.getLgEventConnector().addListener(
            LgEventSource.ALL_SOURCES,
                new LgEventListener() {
                    public void processEvent(final LgEvent event) {
                        BackgroundChangeRequestEvent bgcre = (BackgroundChangeRequestEvent)event;
                        Background bg = bgcre.getBackground();
                        bg.initialize(scenemanager);
                        scenemanager.setBackground(bg);
                    }
                    public Class<LgEvent>[] getTargetEventClasses() {
                        return new Class[] {BackgroundChangeRequestEvent.class};
                    }
                });
                
        // Hack in order to integrate BgManager quickly
        // To be fixed
        if (!initBgManager(themes)) {
            // if no BgManager found (i.e. the incubator is not built together),
            // use the regular set of background selection icons.
            initBackgrounds(themes);
        }
        
        themes.addChild(new QuitIcon("resources/images/icon/JollyRoger.png"));
        
        //addChild(themes);
        
        appThumbnails = new Container3D();
        appThumbnails.setPreferredSize(new Vector3f(width, barHeight, barHeight));//FIXME
	appThumbnails.setLayout(
            new ThumbnailLayout(ThumbnailLayout.AlignmentType.CENTER, 0.002f,
                (float)Math.toRadians(-45), 0.13f, this));
        appThumbnails.setTranslation(0.0f, 0.0f, thumbnailZ);
	addChild(appThumbnails);
        
	setRotationAxis(1.0f, 0.0f, 0.0f);
        setRotationAngle((float)Math.toRadians(-360));
        
        changeRotationAngle((float)Math.toRadians(5));
        
        setTranslation(0.0f, height * -0.6f, 0.0f);
        changeTranslation(0.0f, height * -0.5f + barHeight * 0.5f, barZ, 2000);
        
        // Listen for handling the window size change
        LgEventConnector.getLgEventConnector().addListener(
            LgEventSource.ALL_SOURCES,
            new LgEventListener() {
                public void processEvent(final LgEvent event) {
                    ScreenResolutionChangedEvent csce = (ScreenResolutionChangedEvent)event;
                    changeSize(csce.getWidth(), csce.getHeight());
                }
                public Class<LgEvent>[] getTargetEventClasses() {
                    return new Class[] {ScreenResolutionChangedEvent.class};
                }
            });
            
        initHideEventHandler(height);
    }
    
    private void initBackgrounds(Container3D themes) {
        BackgroundIcon initialBackground;
        
//	themes.addChild(
//            new BackgroundIcon("resources/images/icon/hoover.png") {
//                    protected Background initBackground() {
//                        return new PanoImageBackground(
//                            new String[] {
//                                "resources/images/background/Stanford-0.jpg",
//                                "resources/images/background/Stanford-1.jpg", 
//                                "resources/images/background/Stanford-2.jpg", 
//                                "resources/images/background/Stanford-3.jpg",
//                            },
//                            2);
//                    }
//                });
//	themes.addChild(
//            new BackgroundIcon("resources/images/icon/leaf.png") {
//                    protected Background initBackground() {
//                        return new LayeredImageBackground(
//                            new String[] {
//                                "resources/images/background/Leaves_and_Sky-0.jpg",
//                                "resources/images/background/Leaves_and_Sky-1.png",
//                            },
//                            new float[][] {
//                                {1.2f, -0.1f, -1.5f}, 
//                                {0.9f,  0.1f,  1.0f},
//                            }
//                        );
//                    }
//                });
        themes.addChild(
            initialBackground =
            new BackgroundIcon("resources/images/icon/star.png") {
                    protected Background initBackground() {
                        if (System.getProperty("lg.3dbackground")==null)
                            return new LayeredImageBackground(
                                new String[] {
                                    "resources/images/background/GrandCanyon-0.jpg",
                                    "resources/images/background/GrandCanyon-1.png",
                                },
                                new float[][] {
                                    {1.0f, 0.0f, 1.0f}, 
                                    {0.5f, -0.22f, 1.2f},
                                }
                            );
                        else
                            return new ModelBackground();
                    }
                });
                
        initialBackground.select();
    }
    
    private abstract class BackgroundIcon extends Pseudo3DIcon {
        protected Background background = null;
        private BackgroundIcon(String filename) {
            super(filename);
            addListener(
                new MouseClickedEventAdapter(
                new ActionNoArg() {
                    public void performAction(LgEventSource source) {
                        select();
                    }
            }));
        }
        public void select() {
            if (background == null) {
                // All this complications are for performing background
                // initialization lazily when selected...
                background = initBackground();
                background.initialize(scenemanager);
            }
            scenemanager.setBackground(background);
        }
        protected abstract Background initBackground();
    }
    
    private class QuitIcon extends Pseudo3DIcon {
        private QuitIcon(String filename) {
            super(filename);
            addListener(
                new MouseClickedEventAdapter(
                    new ActionNoArg() {
                        public void performAction(LgEventSource source) {
                            System.exit(0);
                        }
                }));
        }
    }
    
    public void addThumbnail(Component3D thumbnail) {
        if (thumbnail == null) {
            throw new IllegalArgumentException("argument cannot be null");
        }
        appThumbnails.addChild(thumbnail);
    }
    
    public void removeThumbnail(Component3D thumbnail) {
        appThumbnails.removeChild(thumbnail);
    }
    
    private void changeSize(float width, float height) {
        setPreferredSize(new Vector3f(width, barHeight, barHeight));
	bottomBar.setSize(width, barHeight);
        changeTranslation(0.0f, height * -0.5f + barHeight * 0.5f, barZ, 200);
        
        shortcuts.setPreferredSize(new Vector3f(width, barHeight, barHeight));//FIXME
        themes.setPreferredSize(new Vector3f(width, barHeight, barHeight));//FIXME
        appThumbnails.setPreferredSize(new Vector3f(width, barHeight, barHeight));//FIXME
    }
    
    
    // The following class is a short-term hack to integrate 
    // the BgManager quickly.  Will be get rid of.
    public abstract static class BgManagerIconHack extends Pseudo3DIcon {
        /**
         * @deprecated hack to integrate BgManager quickly
         */
        public BgManagerIconHack(String iconFilename) {
            super(iconFilename);
        }
        
        public abstract Background getInitBg();
    }
    
    private boolean initBgManager(Container3D themes) {
        try {
            Class bmihClass = Class.forName("org.jdesktop.lg3d.apps.bgmanager.BgManagerIcon");
            BgManagerIconHack bmih = (BgManagerIconHack)bmihClass.newInstance();
            themes.addChild(bmih);
            Background bg = bmih.getInitBg();
            bg.initialize(scenemanager);
            scenemanager.setBackground(bg);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    private void initHideEventHandler(final float height) {
        // Listen for handling the hide event
        setAnimation(new NaturalMotionAnimation(3000));
        LgEventConnector.getLgEventConnector().addListener(
            LgEventSource.ALL_SOURCES,
            new LgEventListener() {
                public void processEvent(final LgEvent evt) {
                    changeTranslation(0.0f, height * -0.5f - barHeight * 5.0f, barZ);
                }
                public Class<LgEvent>[] getTargetEventClasses() {
                    return new Class[] {HideEvent.class};
                }
            });
    }
            
    public static class HideEvent extends LgEvent {
        // just a tag class
    }
}


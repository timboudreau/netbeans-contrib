/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.desktopsampler.ui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * A simple desktop magnifier. Drag mouse from the magnifier label and
 * release mouse anywhere on desktop to sample an area of 16x16 at
 * that point. The magnification scale of 1:1 to 1:16 is supported.
 *
 * Note: Uses java.awt.Robot.
 *
 * @author Sandip V. Chitale
 */
public class Magnifier extends JPanel {
	static ImageIcon MAGNIFIER_ICON = new ImageIcon(Magnifier.class.getResource("Magnifier.gif"));
	
	private JLabel magnifiedLabel;

	private JSlider zoomer;

	private Robot robot;

	private static final int ZOOMED_DIAMETER = 256;
	private int DEFAULT_DIAMETER = 16;
	private int diameter;
	private int radius;

	private static final Image MAGNIFIER_IMAGE   = MAGNIFIER_ICON.getImage(); 
	private static final Cursor MAGNIFIER_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(MAGNIFIER_IMAGE, new Point(15, 15), "Magnifier");

	
	/**
	 * Creates a new <code>Magnifier</code> instance.
	 *
	 */
	public Magnifier() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.err.println(e);
			System.exit(1);
		}

		setLayout(new BorderLayout());
		
		magnifiedLabel = new JLabel();
		magnifiedLabel.setOpaque(true);
		magnifiedLabel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.black, 1),
				BorderFactory.createEmptyBorder(1,1,1,1)));
		add(magnifiedLabel, BorderLayout.WEST);
		
		magnifiedLabel.setToolTipText(ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("TOOLTIP_Magnifier"));
		
		magnifiedLabel.addMouseListener(
			new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					SwingUtilities.getWindowAncestor(Magnifier.this).setCursor(MAGNIFIER_CURSOR);
				}
				public void mouseReleased(MouseEvent me) {
					Point p = me.getPoint();
					SwingUtilities.convertPointToScreen(p, me.getComponent());
					magnifierAtPoint(p, false);
					SwingUtilities.getWindowAncestor(Magnifier.this).setCursor(Cursor.getDefaultCursor());				
				}
			});
		
		magnifiedLabel.addMouseMotionListener(
			new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent me) {
					Point p = me.getPoint();
					SwingUtilities.convertPointToScreen(p, me.getComponent());
					magnifierAtPoint(p, true);					
					SwingUtilities.getWindowAncestor(Magnifier.this).setCursor(MAGNIFIER_CURSOR);
				}
			});

		zoomer = new JSlider(JSlider.VERTICAL, 1, DEFAULT_DIAMETER, DEFAULT_DIAMETER);
		zoomer.setMajorTickSpacing(1);
		zoomer.setSnapToTicks(true);
		zoomer.setSnapToTicks(true);
		zoomer.setPaintTicks(true);
		zoomer.setPaintLabels(true);
		add(zoomer, BorderLayout.EAST);
		zoomer.addChangeListener(
			new ChangeListener() {
				public void stateChanged(ChangeEvent ce) {
					if (zoomer.getValueIsAdjusting()) {
						return;
					}
					computeMagnification();					
				}
			});

		zoomer.setValue(ZOOMED_DIAMETER/DEFAULT_DIAMETER);
                
                zoomer.setToolTipText(ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("TOOLTIP_Scale")); // NOI18N

		computeMagnification();

		showMagnified(MAGNIFIER_ICON.getImage(), false);
	}
	
	public void magnifierAtPoint(Point p, boolean temporary) {
		Rectangle rect = new Rectangle(p.x-radius, p.y-radius, diameter, diameter);
		showMagnified(robot.createScreenCapture(rect), temporary);
	}

	private void computeMagnification() {
		diameter = ZOOMED_DIAMETER/zoomer.getValue();
		radius = diameter/2;		
	}

	void showMagnified(Image image, boolean temporary) {
		if (image == null) {
			return;
		}

		image = image.getScaledInstance(ZOOMED_DIAMETER, ZOOMED_DIAMETER, (temporary ? Image.SCALE_FAST : Image.SCALE_SMOOTH));
		
		ImageIcon icon = new ImageIcon(image);
		magnifiedLabel.setIcon(icon);
	}

	/**
	 * <code>Magnifier</code> test.
	 *
	 * @param args a <code>String[]</code> value
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println(e);
		}

		JFrame frame = new JFrame(java.util.ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("Desktop_Color_Sampler"));
		frame.setContentPane(new Magnifier());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

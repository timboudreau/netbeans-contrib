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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.highlightboxingunboxingvarargs.impl;

import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.NbPreferences;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class HighlightBoxingUnboxingVarargs {

    private static Preferences preferences;

    static final String HIGHLIGHT_BACKGROUND_COLORS = "HIGHLIGHT_BACKGROUND_COLORS";

    private static boolean highlightBoxing;
    private static boolean highlightUnboxing;
    private static boolean highlightVarargs;

    private static Color DEFAULT_boxingHighlightBackground   = new Color(254, 244, 173);
    private static Color DEFAULT_unboxingHighlightBackground = new Color(255, 184, 178);
    private static Color DEFAULT_varargsHighlightBackground  = new Color(201, 229, 251);

    /**
     * Get the value of boxingHighlightBackground
     *
     * @return the value of boxingHighlightBackground
     */
    public static Color getBoxingHighlightBackground() {
        int boxingHighlightBackgroundRGB = getPreferences().getInt("BOXING_HIGHLIGHT_BACKGROUND_COLOR", DEFAULT_boxingHighlightBackground.getRGB());
        return new Color(boxingHighlightBackgroundRGB);
    }

    /**
     * Set the value of boxingHighlightBackground
     *
     * @param new value of boxingHighlightBackground
     */
    public static void setBoxingHighlightBackground(Color newboxingHighlightBackground) {
        getPreferences().putInt("BOXING_HIGHLIGHT_BACKGROUND_COLOR", newboxingHighlightBackground.getRGB());
    }

    /**
     * Get the value of unboxingHighlightBackground
     *
     * @return the value of unboxingHighlightBackground
     */
    public static Color getUnboxingHighlightBackground() {
        int unboxingHighlightBackgroundRGB = getPreferences().getInt("UNBOXING_HIGHLIGHT_BACKGROUND_COLOR", DEFAULT_unboxingHighlightBackground.getRGB());
        return new Color(unboxingHighlightBackgroundRGB);
    }

    /**
     * Set the value of unboxingHighlightBackground
     *
     * @param new value of unboxingHighlightBackground
     */
    public static void setUnboxingHighlightBackground(Color newunboxingHighlightBackground) {
        getPreferences().putInt("UNBOXING_HIGHLIGHT_BACKGROUND_COLOR", newunboxingHighlightBackground.getRGB());
    }

    /**
     * Get the value of varargsHighlightBackground
     *
     * @return the value of varargsHighlightBackground
     */
    public static Color getVarargsHighlightBackground() {
        int varargsHighlightBackgroundRGB = getPreferences().getInt("VARARGS_HIGHLIGHT_BACKGROUND_COLOR", DEFAULT_varargsHighlightBackground.getRGB());
        return new Color(varargsHighlightBackgroundRGB);
    }

    /**
     * Set the value of varargsHighlightBackground
     *
     * @param new value of varargsHighlightBackground
     */
    public static void setVarargsHighlightBackground(Color newvarargsHighlightBackground) {
        getPreferences().putInt("VARARGS_HIGHLIGHT_BACKGROUND_COLOR", newvarargsHighlightBackground.getRGB());
    }

    /**
     * Set the value of HighlightBackground colors
     *
     */
    public static void setHighlightBackgrounds(
            Color newboxingHighlightBackground,
            Color newunboxingHighlightBackground,
            Color newvarargsHighlightBackground) {
        setBoxingHighlightBackground(newboxingHighlightBackground);
        setUnboxingHighlightBackground(newunboxingHighlightBackground);
        setVarargsHighlightBackground(newvarargsHighlightBackground);
        propertyChangeSupport.firePropertyChange(HIGHLIGHT_BACKGROUND_COLORS, null, null);
    }

    private static ChangeSupport cs = new ChangeSupport(HighlightBoxingUnboxingVarargs.class);

    /**
     * Get the value of highlightBoxing
     *
     * @return the value of highlightBoxing
     */
    public static boolean isHighlightBoxing() {
        return highlightBoxing;
    }

    /**
     * Set the value of highlightBoxing
     *
     * @param new value of highlightBoxing
     */
    public static void setHighlightBoxing(boolean newhighlightBoxing) {
        highlightBoxing = newhighlightBoxing;
        cs.fireChange();
    }

    /**
     * Get the value of highlightUnboxing
     *
     * @return the value of highlightUnboxing
     */
    public static boolean isHighlightUnboxing() {
        return highlightUnboxing;
    }

    /**
     * Set the value of highlightUnboxing
     *
     * @param new value of highlightUnboxing
     */
    public static void setHighlightUnboxing(boolean newhighlightUnboxing) {
        highlightUnboxing = newhighlightUnboxing;
        cs.fireChange();
    }

    /**
     * Get the value of highlightVarargs
     *
     * @return the value of highlightVarargs
     */
    public static boolean isHighlightVarargs() {
        return highlightVarargs;
    }

    /**
     * Set the value of highlightVarargs
     *
     * @param new value of highlightVarargs
     */
    public static void setHighlightVarargs(boolean newhighlightVarargs) {
        highlightVarargs = newhighlightVarargs;
        cs.fireChange();
    }

    public static void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    public static void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }

    private static PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(HighlightBoxingUnboxingVarargs.class);

    public static void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.addPropertyChangeListener( listener );
    }

    public static void removePropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }

    public static Preferences getPreferences() {
        if (preferences == null) {
            preferences = NbPreferences.forModule(HighlightBoxingUnboxingVarargs.class);
        }
        return preferences;
    }

    private static Map<Integer, Color> colorCache;

    private static Color getColorForRGB(int rgb) {
        if (colorCache == null) {
            colorCache = new HashMap<Integer, Color>();
        }
        Color color = colorCache.get(rgb);
        if (color == null) {
            color = new Color(rgb);
            colorCache.put(rgb, color);
        }
        return color;
    }

}

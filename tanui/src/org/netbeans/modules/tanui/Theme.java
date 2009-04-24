/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.tanui;

import java.awt.Font;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalTheme;

/**
 *
 * @author Tim Boudreau
 */
public class Theme extends MetalTheme {

    @Override
    public String getName() {
        return "Stuff";
    }

//            Color bg = new Color(236, 233, 216);
//        Color darker = new Color(220, 218, 194);

    @Override
    protected ColorUIResource getSecondary1() {
        return new ColorUIResource (236, 233, 216);
    }

    @Override
    protected ColorUIResource getSecondary2() {
        return new ColorUIResource(220, 218, 194);
    }

    @Override
    protected ColorUIResource getSecondary3() {
        return new ColorUIResource(203, 197, 180);
    }

    @Override
    protected ColorUIResource getPrimary1() {
//        return new ColorUIResource(200,200,232);
        return getSecondary1();
    }

    @Override
    protected ColorUIResource getPrimary2() {
//        return new ColorUIResource(200,200,225);
        return getSecondary2();
    }

    @Override
    protected ColorUIResource getPrimary3() {
//        return new ColorUIResource(190,190,240);
        return getSecondary3();
    }

    @Override
    public FontUIResource getControlTextFont() {
        return new FontUIResource ("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getSystemTextFont() {
        return new FontUIResource ("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getUserTextFont() {
        return new FontUIResource ("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getMenuTextFont() {
        return new FontUIResource ("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getWindowTitleFont() {
        return new FontUIResource ("Dialog", Font.PLAIN, 12);
    }

    @Override
    public FontUIResource getSubTextFont() {
        return new FontUIResource ("Dialog", Font.PLAIN, 12);
    }
}

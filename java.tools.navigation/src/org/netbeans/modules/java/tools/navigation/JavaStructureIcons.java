/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.tools.navigation;

import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.UiUtils;

import org.openide.ErrorManager;

import org.openide.filesystems.FileObject;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class JavaStructureIcons {
    public static final Icon FQN_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/fqn.gif")); // NOI18N
    public static final Icon CLASS_ICON = UiUtils.getElementIcon(ElementKind.CLASS,
            EnumSet.of(Modifier.PUBLIC));
    public static final Icon INNER_CLASS_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/innerclass.gif")); // NOI18N
    public static final Icon INTERFACE_ICON = UiUtils.getElementIcon(ElementKind.INTERFACE,
            EnumSet.of(Modifier.PUBLIC));
    public static final Icon INNER_INTERFACE_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/innerinterface.gif")); // NOI18N
    public static final Icon CONSTRUCTOR_ICON = UiUtils.getElementIcon(ElementKind.CONSTRUCTOR,
            EnumSet.of(Modifier.PUBLIC));
    public static final Icon METHOD_ICON = UiUtils.getElementIcon(ElementKind.METHOD,
            EnumSet.of(Modifier.PUBLIC));
    public static final Icon FIELD_ICON = UiUtils.getElementIcon(ElementKind.FIELD,
            EnumSet.of(Modifier.PUBLIC));
    public static final Icon ENUM_ICON = UiUtils.getElementIcon(ElementKind.ENUM,
            EnumSet.of(Modifier.PUBLIC));
    public static final Icon ENUM_CONSTANTS_ICON = UiUtils.getElementIcon(ElementKind.ENUM_CONSTANT,
            EnumSet.of(Modifier.PUBLIC));
    public static final Icon PACKAGE_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/package.gif")); // NOI18N
    public static final Icon PRIVATE_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/private.gif")); // NOI18N
    public static final Icon PROTECTED_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/protected.gif")); // NOI18N
    public static final Icon PUBLIC_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/public.gif")); // NOI18N
    public static final Icon STATIC_ICON = new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/java/tools/navigation/resources/static.gif")); // NOI18N
}

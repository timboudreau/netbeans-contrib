/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.lib.callgraph;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Settings for a call graph, implemented by Arguments
 *
 * @author Tim Boudreau
 */
interface CallgraphControl extends Iterable<File> {

    static final String CMD_NOSELF = "noself";
    static final String CMD_SIMPLE = "simple";
    static final String CMD_ANT = "ant";
    static final String CMD_MAVEN = "maven";
    static final String CMD_GRADLE = "gradle";
    static final String CMD_EXTENDED_PROPERTIES = "extensions";
    static final String CMD_AGGRESSIVE_MEMORY = "aggressive";
    static final String CMD_IGNORE = "ignore";
    static final String CMD_PACKAGEGRAPH = "packagegraph";
    static final String CMD_METHODGRAPH = "methodgraph";
    static final String CMD_CLASSGRAPH = "classgraph";
    static final String CMD_QUIET = "quiet";
    static final String CMD_EXCLUDE = "exclude";
    static final String CMD_VERBOSE = "verbose";
    static final String CMD_OMIT_ABSTRACT = "omit_abstract";
    static final String CMD_DISABLE_EIGHT_BIT_STRINGS = "use_java_strings";
    static final String CMD_REVERSE = "reverse";

    boolean isDisableEightBitStrings();

    File classGraphFile();

    Set<String> excludePrefixes();

    Set<File> folders();

    boolean isMaven();

    boolean isGradle();

    boolean isAnt();

    boolean isExtendedProperties();

    boolean isAggressive();

    boolean isQuiet();

    boolean isSelfReferences();

    boolean isShortNames();

    File methodGraphFile();

    File packageGraphFile();

    boolean isExcluded(String qname);

    boolean isVerbose();

    boolean isOmitAbstract();

    boolean isReverse();
}

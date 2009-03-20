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
package org.netbeans.modules.licensechanger.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.licensechanger.*;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tim Boudreau
 */
public abstract class FileHandler {

    public abstract boolean match(FileObject file);

    public abstract boolean shouldSkipFile(FileObject file);

    public abstract String getDisplayName();

    protected abstract Offsets getReplaceOffsets(CharSequence seq);

    protected abstract String escape(String licenseText);

    public String transform(String origText, String licenseText) {
        Offsets offsets = getReplaceOffsets(origText);
        System.err.println("Will delete from " + offsets.getStart() + " to " + offsets.getEnd());
        StringBuilder after = new StringBuilder(origText);
        String escaped = escape(licenseText);
        after.delete(offsets.getStart(), offsets.getEnd());
        after.insert(offsets.getStart(), escaped);
        return after.toString();
    }

    static Pattern p = Pattern.compile("(.*?)\\n|\\z", Pattern.UNIX_LINES);
    public static String[] splitIntoLines (CharSequence content) {
        List <String> result = new ArrayList<String>(40);
        Matcher m = p.matcher(content);
        while (m.find()) {
            if (m.groupCount() == 1) {
                if (m.group(1) != null) {
                    result.add (m.group(1));
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }
}

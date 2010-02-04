/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009-2010 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009-2010 Sun
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
package org.netbeans.modules.editor.fold.spi.support;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.fold.FoldType;

/**
 *
 * @author lahvac
 */
public final class FoldInfo {

    private final Document doc;
    private final @NonNull Position start;
    private final @NonNull Position end;
    private final FoldType type;
    private final String description;
    private final boolean collapseByDefault;

    private FoldInfo(Document doc, @NonNull Position start, @NonNull Position end, FoldType type, String description, boolean collapseByDefault) {
        this.doc = doc;
        this.start = start;
        this.end = end;
        this.type = type;
        this.description = description;
        this.collapseByDefault = collapseByDefault;
    }

    public static FoldInfo create(Document doc, int start, int end, FoldType template, String description, boolean collapseByDefault) throws BadLocationException {
        Position startPos = doc.createPosition(start);
        Position endPos   = doc.createPosition(end);
        
        return new FoldInfo(doc, startPos, endPos, template, description, collapseByDefault);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FoldInfo other = (FoldInfo) obj;
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.collapseByDefault != other.collapseByDefault) {
            return false;
        }
        final boolean[] equals = new boolean[] {true};
        doc.render(new Runnable() {
            public void run() {
                if (FoldInfo.this.start.getOffset() != other.start.getOffset()) {
                    equals[0] = false;
                    return ;
                }
                if (FoldInfo.this.end.getOffset() != other.end.getOffset()) {
                    equals[0] = false;
                    return ;
                }
            }
        });
        return equals[0];
    }

    //XXX: there should be no public getters:
    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public boolean isCollapseByDefault() {
        return collapseByDefault;
    }

    public String getDescription() {
        return description;
    }

    public FoldType getType() {
        return type;
    }

}

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.fuse.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.php.fuse.lexer.FuseTokenId;
import org.netbeans.modules.php.fuse.lexer.FuseTopTokenId;

/**
 * Provides code completion for T_HTML tokens
 *
 */
public class FuseEmbeddingProvider extends EmbeddingProvider {

    public static final String GENERATED_CODE = "@@@"; //NOI18N

    @Override
    public List<Embedding> getEmbeddings(Snapshot snapshot) {
        TokenHierarchy<CharSequence> th = TokenHierarchy.create(snapshot.getText(), FuseTopTokenId.language());
        TokenSequence<FuseTopTokenId> sequence = th.tokenSequence(FuseTopTokenId.language());

        if(sequence == null) {
            Logger.getLogger("FuseEmbeddingProvider").warning(
                    "TokenHierarchy.tokenSequence(FuseTopTokenId.language()) == null " +
                    "for static immutable Fuse TokenHierarchy!\nFile = '"+
                    snapshot.getSource().getFileObject().getPath() +
                    "' ;snapshot mimepath='" + snapshot.getMimePath() + "'");

            return Collections.emptyList();
        }

        sequence.moveStart();
        List<Embedding> embeddings = new ArrayList<Embedding>();

        int from = -1;
        int len = 0;
        int state = -1;
        int addToOffset = 0;
        boolean changed = false;
        while (sequence.moveNext()) {
            Token t = sequence.token();
            if (t.id() == FuseTopTokenId.T_HTML) {
                if(from < 0) {
                    from = sequence.offset();
                }
                len += t.length();
                if (state != 1) {
                    changed = true;
                    state = 1;
                }
            } else if (t.id() == FuseTopTokenId.T_FUSE) {
                TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(t.text(), FuseTokenId.language());
                TokenSequence<FuseTokenId> sequence2 = th2.tokenSequence(FuseTokenId.language());
                while (sequence2.moveNext()) {
                    t = sequence2.token();
    //                FuseTokenId t2 = FuseTokenId.valueOf(t.text().toString());
                    if (t.id() == FuseTokenId.IDENTIFIER) {
                        if(from < 0) {
                            from = sequence.offset();
                        }
                        len += t.length();
                        if (state != 2) {
                            changed = true;
                            state = 2;
                        }
                    }
                }
            } else if (t.id() == FuseTopTokenId.T_FUSE_OPEN_DELIMITER) {
                embeddings.add(snapshot.create("<?", "text/x-php5"));
            } else if (t.id() == FuseTopTokenId.T_FUSE_CLOSE_DELIMITER) {
                embeddings.add(snapshot.create("?>", "text/x-php5"));
            }
            if (changed) {
                if(from >= 0) {
                    if (state == 1) {
                        embeddings.add(snapshot.create(from, len, "text/x-php5")); //NOI18N
                    }
                    else {
                        embeddings.add(snapshot.create(from, len, "text/x-php5")); //NOI18N
                    }
                }

                from = -1;
                len = 0;
            }
        }

        if(from >= 0) {
            embeddings.add(snapshot.create(from, len, "text/x-php5")); //NOI18N
        }

        if (embeddings.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(Embedding.create(embeddings));
        }
    }

    @Override
    public int getPriority() {
        return 110;
    }

    @Override
    public void cancel() {
        //do nothing
    }

    public static final class Factory extends TaskFactory {

        @Override
        public Collection<SchedulerTask> create(final Snapshot snapshot) {
            return Collections.<SchedulerTask>singletonList(new FuseEmbeddingProvider());
        }
    }
}

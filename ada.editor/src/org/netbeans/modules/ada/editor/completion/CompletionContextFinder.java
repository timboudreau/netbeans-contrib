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

package org.netbeans.modules.ada.editor.completion;


/**
 * Based on org.netbeans.modules.php.editor.CompletionContextFinder
 *
 * @author Andrea Lucarelli
 */
/*
class CompletionContextFinder {
    private static final List<AdaTokenId[]> PACKAGE_MEMBER_TOKENCHAINS = Arrays.asList(
        new AdaTokenId[]{AdaTokenId.DOT},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.STRING_LITERAL},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.IDENTIFIER},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.LPAREN},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.RPAREN},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.WHITESPACE},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.WHITESPACE, AdaTokenId.STRING_LITERAL},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.WHITESPACE, AdaTokenId.IDENTIFIER},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.WHITESPACE, AdaTokenId.LPAREN},
        new AdaTokenId[]{AdaTokenId.DOT, AdaTokenId.WHITESPACE, AdaTokenId.RPAREN}
        );

    private static final List<AdaTokenId[]> STATIC_CLASS_MEMBER_TOKENCHAINS = Arrays.asList(
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM},
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM, AdaTokenId.PHP_STRING},
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM, AdaTokenId.PHP_VARIABLE},
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM, AdaTokenId.PHP_TOKEN},
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM, AdaTokenId.WHITESPACE},
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM, AdaTokenId.WHITESPACE, AdaTokenId.PHP_STRING},
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM, AdaTokenId.WHITESPACE, AdaTokenId.PHP_VARIABLE},
        new AdaTokenId[]{AdaTokenId.PHP_PAAMAYIM_NEKUDOTAYIM, AdaTokenId.WHITESPACE, AdaTokenId.PHP_TOKEN}
        );

    private static final AdaTokenId[] COMMENT_TOKENS = new AdaTokenId[]{
        AdaTokenId.PHP_COMMENT_START, AdaTokenId.PHP_COMMENT, AdaTokenId.PHP_LINE_COMMENT, AdaTokenId.PHP_COMMENT_END};

    private static final List<AdaTokenId[]> PHPDOC_TOKENCHAINS = Arrays.asList(
            new AdaTokenId[]{AdaTokenId.PHPDOC_COMMENT_START},
            new AdaTokenId[]{AdaTokenId.PHPDOC_COMMENT}
            );

    private static final List<AdaTokenId[]> FUNCTION_TOKENCHAINS = Arrays.asList(
            new AdaTokenId[]{AdaTokenId.PHP_FUNCTION},
            new AdaTokenId[]{AdaTokenId.PHP_FUNCTION,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_FUNCTION,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING}
            );

    private static final List<AdaTokenId[]> CLASS_CONTEXT_KEYWORDS_TOKENCHAINS = Arrays.asList(
            new AdaTokenId[]{AdaTokenId.PHP_PRIVATE},
            new AdaTokenId[]{AdaTokenId.PHP_PRIVATE,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_PRIVATE,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_PROTECTED},
            new AdaTokenId[]{AdaTokenId.PHP_PROTECTED,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_PROTECTED,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_PUBLIC},
            new AdaTokenId[]{AdaTokenId.PHP_PUBLIC,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_PUBLIC,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_STATIC},
            new AdaTokenId[]{AdaTokenId.PHP_STATIC,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_STATIC,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_ABSTRACT},
            new AdaTokenId[]{AdaTokenId.PHP_ABSTRACT,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_ABSTRACT,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_FINAL},
            new AdaTokenId[]{AdaTokenId.PHP_FINAL,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_FINAL,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_CURLY_OPEN},
            new AdaTokenId[]{AdaTokenId.PHP_LINE_COMMENT},
            new AdaTokenId[]{AdaTokenId.PHP_LINE_COMMENT,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_LINE_COMMENT, AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_COMMENT_END},
            new AdaTokenId[]{AdaTokenId.PHP_COMMENT_END, AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_COMMENT_END,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_COMMENT_END,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHPDOC_COMMENT_END},
            new AdaTokenId[]{AdaTokenId.PHPDOC_COMMENT_END, AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHPDOC_COMMENT_END,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHPDOC_COMMENT_END,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_CURLY_CLOSE},
            new AdaTokenId[]{AdaTokenId.PHP_CURLY_CLOSE,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_CURLY_CLOSE,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_CURLY_OPEN},
            new AdaTokenId[]{AdaTokenId.PHP_CURLY_OPEN,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_CURLY_OPEN,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING},
            new AdaTokenId[]{AdaTokenId.PHP_SEMICOLON},
            new AdaTokenId[]{AdaTokenId.PHP_SEMICOLON,AdaTokenId.WHITESPACE},
            new AdaTokenId[]{AdaTokenId.PHP_SEMICOLON,AdaTokenId.WHITESPACE,AdaTokenId.PHP_STRING}
            );


       private static final List<AdaTokenId[]> SERVER_ARRAY_TOKENCHAINS = Collections.singletonList(
            new AdaTokenId[]{AdaTokenId.PHP_VARIABLE, AdaTokenId.PHP_TOKEN});

       private static final List<String> SERVER_ARRAY_TOKENTEXTS =
               Arrays.asList(new String[] {"$_SERVER","["});//NOI18N


    static enum CompletionContext {EXPRESSION, HTML, CLASS_NAME, INTERFACE_NAME, TYPE_NAME, STRING,
        CLASS_MEMBER, STATIC_CLASS_MEMBER, PHPDOC, INHERITANCE, EXTENDS, IMPLEMENTS, METHOD_NAME,
        CLASS_CONTEXT_KEYWORDS, SERVER_ENTRY_CONSTANTS, NONE};

    static enum KeywordCompletionType {SIMPLE, CURSOR_INSIDE_BRACKETS, ENDS_WITH_CURLY_BRACKETS,
    ENDS_WITH_SPACE, ENDS_WITH_SEMICOLON, ENDS_WITH_COLON};
    
        @NonNull
    static CompletionContext findCompletionContext(CompilationInfo info, int caretOffset){
       Document document = info.getDocument();
        if (document == null) {
            return CompletionContext.NONE;
        }

        TokenSequence<AdaTokenId> tokenSequence = LexUtilities.getPHPTokenSequence(document, caretOffset);
        if (tokenSequence == null) {
            return CompletionContext.NONE;
        }
        TokenHierarchy th = TokenHierarchy.get(document);
        tokenSequence.move(caretOffset);
        if (!tokenSequence.moveNext() && !tokenSequence.movePrevious()){
            return CompletionContext.NONE;
        }
        Token<AdaTokenId> token = tokenSequence.token();
        AdaTokenId tokenId =token.id();
        int tokenIdOffset = tokenSequence.token().offset(th);
        
        CompletionContext clsIfaceDeclContext = getClsIfaceDeclContext(token,
                (caretOffset-tokenIdOffset), tokenSequence);
        if (clsIfaceDeclContext != null) {
            return clsIfaceDeclContext;
        }

        if (acceptTokenChains(tokenSequence, CLASS_NAME_TOKENCHAINS)){
            return CompletionContext.CLASS_NAME;
        } else if (acceptTokenChains(tokenSequence, PACKAGE_MEMBER_TOKENCHAINS)){
            return CompletionContext.CLASS_MEMBER;
        } else if (acceptTokenChains(tokenSequence, STATIC_CLASS_MEMBER_TOKENCHAINS)){
            return CompletionContext.STATIC_CLASS_MEMBER;
        } else if (isOneOfTokens(tokenSequence, COMMENT_TOKENS)){
            return CompletionContext.NONE;
        } else if (acceptTokenChains(tokenSequence, PHPDOC_TOKENCHAINS)){
            return CompletionContext.PHPDOC;
        } else if (acceptTokenChains(tokenSequence, TYPE_TOKENCHAINS)){
            return CompletionContext.TYPE_NAME;
        } else if (isInsideClassIfaceDeclarationBlock(info, caretOffset, tokenSequence)) {
            if (acceptTokenChains(tokenSequence, CLASS_CONTEXT_KEYWORDS_TOKENCHAINS)) {
                return CompletionContext.CLASS_CONTEXT_KEYWORDS;
            } else if (acceptTokenChains(tokenSequence, FUNCTION_TOKENCHAINS)) {
                return CompletionContext.METHOD_NAME;
            }
            return CompletionContext.NONE;
        }
        
        switch (tokenId){
            case T_INLINE_HTML:
                return CompletionContext.HTML;
            case PHP_CONSTANT_ENCAPSED_STRING:
                char encChar = tokenSequence.token().text().charAt(0);
                if (encChar == '"') {//NOI18N
                    if (acceptTokenChains(tokenSequence, SERVER_ARRAY_TOKENCHAINS)
                            && acceptTokenChainTexts(tokenSequence, SERVER_ARRAY_TOKENTEXTS)) {
                        return CompletionContext.SERVER_ENTRY_CONSTANTS;
                    }
                    return CompletionContext.STRING;
                } else if (encChar == '\'') {//NOI18N
                    if (acceptTokenChains(tokenSequence, SERVER_ARRAY_TOKENCHAINS)
                            && acceptTokenChainTexts(tokenSequence, SERVER_ARRAY_TOKENTEXTS)) {
                        return CompletionContext.SERVER_ENTRY_CONSTANTS;
                    }
                }
                return CompletionContext.NONE;
            default:
        }
        
        return CompletionContext.EXPRESSION;
    }

    private static boolean isOneOfTokens(TokenSequence tokenSequence, AdaTokenId[] tokenIds){
        TokenId searchedId = tokenSequence.token().id();

        for (TokenId tokenId : tokenIds){
            if (tokenId.equals(searchedId)){
                return true;
            }
        }

        return false;
    }

    private static boolean acceptTokenChainTexts(TokenSequence tokenSequence, List<String> tokenTexts) {
        Token[] preceedingTokens = getPreceedingTokens(tokenSequence, tokenTexts.size());
        if (preceedingTokens.length != tokenTexts.size()) {
            return false;
        }
        for (int idx = 0; idx < preceedingTokens.length; idx++) {
            String expectedText = tokenTexts.get(idx);
            if (!expectedText.contentEquals(preceedingTokens[idx].text())) {
                return false;
            }
        }
        return true;
    }

    private static boolean acceptTokenChains(TokenSequence tokenSequence, List<AdaTokenId[]> tokenIdChains) {
        int maxLen = 0;

        for (AdaTokenId tokenIds[] : tokenIdChains){
            if (maxLen < tokenIds.length){
                maxLen = tokenIds.length;
            }
        }

        Token preceedingTokens[] = getPreceedingTokens(tokenSequence, maxLen);

        chain_search:
        for (AdaTokenId tokenIds[] : tokenIdChains){

            int startWithinPrefix = preceedingTokens.length - tokenIds.length;

            if (startWithinPrefix >= 0){
                for (int i = 0; i < tokenIds.length; i ++){
                    if (tokenIds[i] != preceedingTokens[i + startWithinPrefix].id()){
                        continue chain_search;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private static Token[] getPreceedingTokens(TokenSequence tokenSequence, int maxNumberOfTokens){
        int orgOffset = tokenSequence.offset();
        LinkedList<Token> tokens = new LinkedList<Token>();

        for (int i = 0; i < maxNumberOfTokens; i++) {
            if (!tokenSequence.movePrevious()){
                break;
            }

            tokens.addFirst(tokenSequence.token());
        }

        tokenSequence.move(orgOffset);
        tokenSequence.moveNext();
        return tokens.toArray(new Token[tokens.size()]);
    }

    @CheckForNull
    private static CompletionContext getClsIfaceDeclContext(Token<AdaTokenId> token, int tokenOffset, TokenSequence<AdaTokenId> tokenSequence) {
        boolean isClass = false;
        boolean isIface = false;
        boolean isExtends = false;
        boolean isImplements = false;
        boolean isString = false;
        Token<AdaTokenId> stringToken = null;
        List<? extends Token<AdaTokenId>> preceedingLineTokens = getPreceedingLineTokens(token, tokenOffset, tokenSequence);
        for (Token<AdaTokenId> cToken : preceedingLineTokens) {
            TokenId id = cToken.id();
            boolean nokeywords = !isIface && !isClass && !isExtends && !isImplements;
            if (id.equals(AdaTokenId.PHP_CLASS)) {
                isClass = true;
                break;
            } else if (id.equals(AdaTokenId.PHP_INTERFACE)) {
                isIface = true;
                break;
            } else if (id.equals(AdaTokenId.PHP_EXTENDS)) {
                isExtends = true;
            } else if (id.equals(AdaTokenId.PHP_IMPLEMENTS)) {
                isImplements = true;
            } else if (nokeywords && id.equals(AdaTokenId.PHP_STRING)) {
                isString = true;
                stringToken = cToken;
            } else {
                if (nokeywords && id.equals(AdaTokenId.PHP_CURLY_OPEN)) {
                    return null;
                }
            }
        }
        if (isClass || isIface) {
            if (isImplements) {
                return CompletionContext.INTERFACE_NAME;
            } else if (isExtends) {
                if (isString && isClass && stringToken != null && tokenOffset == 0
                        && preceedingLineTokens.size() > 0 && preceedingLineTokens.get(0).text().equals(stringToken.text())) {
                    return CompletionContext.CLASS_NAME;
                } else if (isString && isClass) {
                    return CompletionContext.IMPLEMENTS;
                } else if (!isString && isClass) {
                    return CompletionContext.CLASS_NAME;
                } else if (isIface) {
                    return CompletionContext.INTERFACE_NAME;
                }
                return !isString ? isClass ? CompletionContext.CLASS_NAME : CompletionContext.INTERFACE_NAME : isClass ? CompletionContext.IMPLEMENTS : CompletionContext.INTERFACE_NAME;
            } else if (isIface) {
                return !isString ? CompletionContext.NONE : CompletionContext.EXTENDS;
            } else if (isClass) {
                return !isString ? CompletionContext.NONE : CompletionContext.INHERITANCE;
            }
        }
        return null;
    }

    static boolean lineContainsAny(Token<AdaTokenId> token,int tokenOffset, TokenSequence<AdaTokenId> tokenSequence, List<AdaTokenId> ids) {
        List<? extends Token<AdaTokenId>> preceedingLineTokens = getPreceedingLineTokens(token, tokenOffset, tokenSequence);
        for (Token t : preceedingLineTokens) {
            if (ids.contains(t.id())) {
                return true;
            }
        }
        return false;
    }
 
    **
     * @return all preceding tokens for current line
     *
    private static List<? extends Token<AdaTokenId>> getPreceedingLineTokens(Token<AdaTokenId> token, int tokenOffset, TokenSequence<AdaTokenId> tokenSequence) {
        int orgOffset = tokenSequence.offset();
        LinkedList<Token<AdaTokenId>> tokens = new LinkedList<Token<AdaTokenId>>();
        if (token.id() != AdaTokenId.WHITESPACE ||
                token.text().subSequence(0, 
                Math.min(token.text().length(), tokenOffset)).toString().indexOf("\n") == -1) {//NOI18N
            while (true) {
                if (!tokenSequence.movePrevious()) {
                    break;
                }
                Token<AdaTokenId> cToken = tokenSequence.token();
                if (cToken.id() == AdaTokenId.WHITESPACE &&
                        cToken.text().toString().indexOf("\n") != -1) {//NOI18N
                    break;
                }                
                tokens.addLast(cToken);
            }
        }

        tokenSequence.move(orgOffset);
        tokenSequence.moveNext();
        
        return tokens;
    }

    private synchronized static boolean isInsideClassIfaceDeclarationBlock(CompilationInfo info,
            int caretOffset, TokenSequence tokenSequence){
        List<ASTNode> nodePath = NavUtils.underCaret(info, lexerToASTOffset(info, caretOffset));
        boolean methDecl = false;
        boolean funcDecl = false;
        boolean clsDecl = false;
        boolean isClassInsideFunc = false;
        boolean isFuncInsideClass = false;
        for (ASTNode aSTNode : nodePath) {
            if (aSTNode instanceof FunctionDeclaration) {
                funcDecl = true;
                if (clsDecl) isFuncInsideClass = true;
            } else if (aSTNode instanceof MethodDeclaration) {
                methDecl = true;
            } else if (aSTNode instanceof ClassDeclaration) {
                clsDecl = true;
                if (funcDecl) isClassInsideFunc = true;
            } else if (aSTNode instanceof InterfaceDeclaration) {
                clsDecl = true;
            }
        }
        if (funcDecl && !methDecl && !clsDecl) {
            final StringBuilder sb = new StringBuilder();
            new DefaultVisitor(){
                @Override
                public void visit(ASTError astError) {
                    super.visit(astError);
                    sb.append(astError.toString());
                }
            }.scan(Utils.getRoot(info));
            if (sb.length() == 0) {
                return false;
            }
        }
        if (isClassInsideFunc && !isFuncInsideClass) {
            return true;
        }
        int orgOffset = tokenSequence.offset();
        try {
            int curly_open = 0;
            int curly_close = 0;
            while (tokenSequence.movePrevious()) {
                Token token = tokenSequence.token();
                TokenId id = token.id();
                if (id.equals(AdaTokenId.PHP_CURLY_OPEN)) {
                    curly_open++;
                } else if (id.equals(AdaTokenId.PHP_CURLY_CLOSE)) {
                    curly_close++;
                } else if ((id.equals(AdaTokenId.PHP_FUNCTION) ||
                        id.equals(AdaTokenId.PHP_WHILE) ||
                        id.equals(AdaTokenId.PHP_IF) ||
                        id.equals(AdaTokenId.PHP_FOR) ||
                        id.equals(AdaTokenId.PHP_FOREACH) ||
                        id.equals(AdaTokenId.PHP_TRY) ||
                        id.equals(AdaTokenId.PHP_CATCH))
                        && (curly_open > curly_close)) {
                    return false;
                } else if (id.equals(AdaTokenId.PHP_CLASS) || id.equals(AdaTokenId.PHP_INTERFACE)) {
                    boolean isClassScope = curly_open > 0 && (curly_open > curly_close);
                    return isClassScope;
                }
            }
        } finally {
            tokenSequence.move(orgOffset);
            tokenSequence.moveNext();
        }
        return false;
    }

    static int lexerToASTOffset (PHPParseResult result, int lexerOffset) {
        if (result.getTranslatedSource() != null) {
            return result.getTranslatedSource().getAstOffset(lexerOffset);
        }
        return lexerOffset;
    }

    static int lexerToASTOffset(CompilationInfo info, int lexerOffset) {
        PHPParseResult result = (PHPParseResult) info.getEmbeddedResult(PHPLanguage.PHP_MIME_TYPE, lexerOffset);
        return lexerToASTOffset(result, lexerOffset);
    }
}
*/
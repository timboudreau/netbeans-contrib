// $ANTLR 3.3 Nov 30, 2010 12:50:56 /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g 2013-01-14 14:05:33

package org.netbeans.modules.antlr.editor.gen;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.debug.*;
import java.io.IOException;
/** ANTLR v3 tree grammar to walk trees created by ANTLRv3.g */
public class ANTLRv3Tree extends DebugTreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DOC_COMMENT", "PARSER", "LEXER", "RULE", "BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "RANGE", "CHAR_RANGE", "EPSILON", "ALT", "EOR", "EOB", "EOA", "ID", "ARG", "ARGLIST", "RET", "LEXER_GRAMMAR", "PARSER_GRAMMAR", "TREE_GRAMMAR", "COMBINED_GRAMMAR", "INITACTION", "LABEL", "TEMPLATE", "SCOPE", "SEMPRED", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "FRAGMENT", "TREE_BEGIN", "ROOT", "BANG", "REWRITE", "TOKENS", "TOKEN_REF", "STRING_LITERAL", "CHAR_LITERAL", "ACTION", "OPTIONS", "INT", "ARG_ACTION", "RULE_REF", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "SRC", "SL_COMMENT", "ML_COMMENT", "LITERAL_CHAR", "ESC", "XDIGIT", "NESTED_ARG_ACTION", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "NESTED_ACTION", "ACTION_ESC", "WS_LOOP", "WS", "'lexer'", "'parser'", "'tree'", "'grammar'", "';'", "'}'", "'='", "'@'", "'::'", "'*'", "'protected'", "'public'", "'private'", "'returns'", "':'", "'throws'", "','", "'('", "'|'", "')'", "'catch'", "'finally'", "'+='", "'=>'", "'~'", "'?'", "'+'", "'.'", "'$'"
    };
    public static final int EOF=-1;
    public static final int T__65=65;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int T__73=73;
    public static final int T__74=74;
    public static final int T__75=75;
    public static final int T__76=76;
    public static final int T__77=77;
    public static final int T__78=78;
    public static final int T__79=79;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int T__83=83;
    public static final int T__84=84;
    public static final int T__85=85;
    public static final int T__86=86;
    public static final int T__87=87;
    public static final int T__88=88;
    public static final int T__89=89;
    public static final int T__90=90;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__93=93;
    public static final int DOC_COMMENT=4;
    public static final int PARSER=5;
    public static final int LEXER=6;
    public static final int RULE=7;
    public static final int BLOCK=8;
    public static final int OPTIONAL=9;
    public static final int CLOSURE=10;
    public static final int POSITIVE_CLOSURE=11;
    public static final int SYNPRED=12;
    public static final int RANGE=13;
    public static final int CHAR_RANGE=14;
    public static final int EPSILON=15;
    public static final int ALT=16;
    public static final int EOR=17;
    public static final int EOB=18;
    public static final int EOA=19;
    public static final int ID=20;
    public static final int ARG=21;
    public static final int ARGLIST=22;
    public static final int RET=23;
    public static final int LEXER_GRAMMAR=24;
    public static final int PARSER_GRAMMAR=25;
    public static final int TREE_GRAMMAR=26;
    public static final int COMBINED_GRAMMAR=27;
    public static final int INITACTION=28;
    public static final int LABEL=29;
    public static final int TEMPLATE=30;
    public static final int SCOPE=31;
    public static final int SEMPRED=32;
    public static final int GATED_SEMPRED=33;
    public static final int SYN_SEMPRED=34;
    public static final int BACKTRACK_SEMPRED=35;
    public static final int FRAGMENT=36;
    public static final int TREE_BEGIN=37;
    public static final int ROOT=38;
    public static final int BANG=39;
    public static final int REWRITE=40;
    public static final int TOKENS=41;
    public static final int TOKEN_REF=42;
    public static final int STRING_LITERAL=43;
    public static final int CHAR_LITERAL=44;
    public static final int ACTION=45;
    public static final int OPTIONS=46;
    public static final int INT=47;
    public static final int ARG_ACTION=48;
    public static final int RULE_REF=49;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=50;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=51;
    public static final int SRC=52;
    public static final int SL_COMMENT=53;
    public static final int ML_COMMENT=54;
    public static final int LITERAL_CHAR=55;
    public static final int ESC=56;
    public static final int XDIGIT=57;
    public static final int NESTED_ARG_ACTION=58;
    public static final int ACTION_STRING_LITERAL=59;
    public static final int ACTION_CHAR_LITERAL=60;
    public static final int NESTED_ACTION=61;
    public static final int ACTION_ESC=62;
    public static final int WS_LOOP=63;
    public static final int WS=64;

    // delegates
    // delegators

    public static final String[] ruleNames = new String[] {
        "invalidRule", "rewrite_tree_ebnf", "modifier", "rewrite_indirect_template_head", 
        "grammarType", "rewrite_tree_element", "terminal", "ruleAction", 
        "elementNoOptionSpec", "rewrite_template_args", "tokensSpec", "option", 
        "atom", "altList", "action", "rewrite_tree_block", "optionValue", 
        "rewrite_tree_alternative", "attrScope", "tokenSpec", "rewrite_template", 
        "rewrite_tree", "rewrite", "exceptionGroup", "block", "notTerminal", 
        "exceptionHandler", "notSet", "rewrite_tree_atom", "rewrite_alternative", 
        "ebnfSuffix", "range", "ebnf", "throwsSpec", "optionsSpec", "grammarDef", 
        "alternative", "rewrite_template_ref", "treeSpec", "finallyClause", 
        "element", "ruleScopeSpec", "rewrite_template_arg", "rule"
    };
    public static final boolean[] decisionCanBacktrack = new boolean[] {
        false, // invalid decision
        false, false, false, false, false, false, false, false, false, false, 
            false, false, false, false, false, false, false, false, false, 
            false, false, false, false, false, false, false, false, false, 
            false, false, false, false, false, false, false, false, false, 
            false, false, false, false, false, false, false, false
    };

     
        public int ruleLevel = 0;
        public int getRuleLevel() { return ruleLevel; }
        public void incRuleLevel() { ruleLevel++; }
        public void decRuleLevel() { ruleLevel--; }
        public ANTLRv3Tree(TreeNodeStream input) {
            this(input, DebugEventSocketProxy.DEFAULT_DEBUGGER_PORT, new RecognizerSharedState());
        }
        public ANTLRv3Tree(TreeNodeStream input, int port, RecognizerSharedState state) {
            super(input, state);
            DebugEventSocketProxy proxy =
                new DebugEventSocketProxy(this, port, input.getTreeAdaptor());
            setDebugListener(proxy);
            try {
                proxy.handshake();
            }
            catch (IOException ioe) {
                reportError(ioe);
            }
        }
    public ANTLRv3Tree(TreeNodeStream input, DebugEventListener dbg) {
        super(input, dbg, new RecognizerSharedState());

    }
    protected boolean evalPredicate(boolean result, String predicate) {
        dbg.semanticPredicate(result, predicate);
        return result;
    }


    public String[] getTokenNames() { return ANTLRv3Tree.tokenNames; }
    public String getGrammarFileName() { return "/Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g"; }



    // $ANTLR start "grammarDef"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:41:1: grammarDef : ^( grammarType ID ( DOC_COMMENT )? ( optionsSpec )? ( tokensSpec )? ( attrScope )* ( action )* ( rule )+ ) ;
    public final void grammarDef() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "grammarDef");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(41, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:5: ( ^( grammarType ID ( DOC_COMMENT )? ( optionsSpec )? ( tokensSpec )? ( attrScope )* ( action )* ( rule )+ ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:9: ^( grammarType ID ( DOC_COMMENT )? ( optionsSpec )? ( tokensSpec )? ( attrScope )* ( action )* ( rule )+ )
            {
            dbg.location(42,9);
            dbg.location(42,12);
            pushFollow(FOLLOW_grammarType_in_grammarDef52);
            grammarType();

            state._fsp--;


            match(input, Token.DOWN, null); 
            dbg.location(42,24);
            match(input,ID,FOLLOW_ID_in_grammarDef54); 
            dbg.location(42,27);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:27: ( DOC_COMMENT )?
            int alt1=2;
            try { dbg.enterSubRule(1);
            try { dbg.enterDecision(1, decisionCanBacktrack[1]);

            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            } finally {dbg.exitDecision(1);}

            switch (alt1) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:27: DOC_COMMENT
                    {
                    dbg.location(42,27);
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarDef56); 

                    }
                    break;

            }
            } finally {dbg.exitSubRule(1);}

            dbg.location(42,40);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:40: ( optionsSpec )?
            int alt2=2;
            try { dbg.enterSubRule(2);
            try { dbg.enterDecision(2, decisionCanBacktrack[2]);

            int LA2_0 = input.LA(1);

            if ( (LA2_0==OPTIONS) ) {
                alt2=1;
            }
            } finally {dbg.exitDecision(2);}

            switch (alt2) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:40: optionsSpec
                    {
                    dbg.location(42,40);
                    pushFollow(FOLLOW_optionsSpec_in_grammarDef59);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(2);}

            dbg.location(42,53);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:53: ( tokensSpec )?
            int alt3=2;
            try { dbg.enterSubRule(3);
            try { dbg.enterDecision(3, decisionCanBacktrack[3]);

            int LA3_0 = input.LA(1);

            if ( (LA3_0==TOKENS) ) {
                alt3=1;
            }
            } finally {dbg.exitDecision(3);}

            switch (alt3) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:53: tokensSpec
                    {
                    dbg.location(42,53);
                    pushFollow(FOLLOW_tokensSpec_in_grammarDef62);
                    tokensSpec();

                    state._fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(3);}

            dbg.location(42,65);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:65: ( attrScope )*
            try { dbg.enterSubRule(4);

            loop4:
            do {
                int alt4=2;
                try { dbg.enterDecision(4, decisionCanBacktrack[4]);

                int LA4_0 = input.LA(1);

                if ( (LA4_0==SCOPE) ) {
                    alt4=1;
                }


                } finally {dbg.exitDecision(4);}

                switch (alt4) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:65: attrScope
            	    {
            	    dbg.location(42,65);
            	    pushFollow(FOLLOW_attrScope_in_grammarDef65);
            	    attrScope();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);
            } finally {dbg.exitSubRule(4);}

            dbg.location(42,76);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:76: ( action )*
            try { dbg.enterSubRule(5);

            loop5:
            do {
                int alt5=2;
                try { dbg.enterDecision(5, decisionCanBacktrack[5]);

                int LA5_0 = input.LA(1);

                if ( (LA5_0==72) ) {
                    alt5=1;
                }


                } finally {dbg.exitDecision(5);}

                switch (alt5) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:76: action
            	    {
            	    dbg.location(42,76);
            	    pushFollow(FOLLOW_action_in_grammarDef68);
            	    action();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);
            } finally {dbg.exitSubRule(5);}

            dbg.location(42,84);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:84: ( rule )+
            int cnt6=0;
            try { dbg.enterSubRule(6);

            loop6:
            do {
                int alt6=2;
                try { dbg.enterDecision(6, decisionCanBacktrack[6]);

                int LA6_0 = input.LA(1);

                if ( (LA6_0==RULE) ) {
                    alt6=1;
                }


                } finally {dbg.exitDecision(6);}

                switch (alt6) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:42:84: rule
            	    {
            	    dbg.location(42,84);
            	    pushFollow(FOLLOW_rule_in_grammarDef71);
            	    rule();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt6++;
            } while (true);
            } finally {dbg.exitSubRule(6);}


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(43, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "grammarDef");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "grammarDef"


    // $ANTLR start "grammarType"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:45:1: grammarType : ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR );
    public final void grammarType() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "grammarType");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(45, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:46:2: ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:
            {
            dbg.location(46,2);
            if ( (input.LA(1)>=LEXER_GRAMMAR && input.LA(1)<=COMBINED_GRAMMAR) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(50, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "grammarType");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "grammarType"


    // $ANTLR start "tokensSpec"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:52:1: tokensSpec : ^( TOKENS ( tokenSpec )+ ) ;
    public final void tokensSpec() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "tokensSpec");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(52, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:53:2: ( ^( TOKENS ( tokenSpec )+ ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:53:4: ^( TOKENS ( tokenSpec )+ )
            {
            dbg.location(53,4);
            dbg.location(53,6);
            match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec127); 

            match(input, Token.DOWN, null); 
            dbg.location(53,13);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:53:13: ( tokenSpec )+
            int cnt7=0;
            try { dbg.enterSubRule(7);

            loop7:
            do {
                int alt7=2;
                try { dbg.enterDecision(7, decisionCanBacktrack[7]);

                int LA7_0 = input.LA(1);

                if ( (LA7_0==TOKEN_REF||LA7_0==71) ) {
                    alt7=1;
                }


                } finally {dbg.exitDecision(7);}

                switch (alt7) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:53:13: tokenSpec
            	    {
            	    dbg.location(53,13);
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec129);
            	    tokenSpec();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt7++;
            } while (true);
            } finally {dbg.exitSubRule(7);}


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(54, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "tokensSpec");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "tokensSpec"


    // $ANTLR start "tokenSpec"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:56:1: tokenSpec : ( ^( '=' TOKEN_REF STRING_LITERAL ) | ^( '=' TOKEN_REF CHAR_LITERAL ) | TOKEN_REF );
    public final void tokenSpec() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "tokenSpec");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(56, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:57:2: ( ^( '=' TOKEN_REF STRING_LITERAL ) | ^( '=' TOKEN_REF CHAR_LITERAL ) | TOKEN_REF )
            int alt8=3;
            try { dbg.enterDecision(8, decisionCanBacktrack[8]);

            int LA8_0 = input.LA(1);

            if ( (LA8_0==71) ) {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==DOWN) ) {
                    int LA8_3 = input.LA(3);

                    if ( (LA8_3==TOKEN_REF) ) {
                        int LA8_4 = input.LA(4);

                        if ( (LA8_4==STRING_LITERAL) ) {
                            alt8=1;
                        }
                        else if ( (LA8_4==CHAR_LITERAL) ) {
                            alt8=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 8, 4, input);

                            dbg.recognitionException(nvae);
                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 3, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else if ( (LA8_0==TOKEN_REF) ) {
                alt8=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(8);}

            switch (alt8) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:57:4: ^( '=' TOKEN_REF STRING_LITERAL )
                    {
                    dbg.location(57,4);
                    dbg.location(57,6);
                    match(input,71,FOLLOW_71_in_tokenSpec143); 

                    match(input, Token.DOWN, null); 
                    dbg.location(57,10);
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenSpec145); 
                    dbg.location(57,20);
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec147); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:58:4: ^( '=' TOKEN_REF CHAR_LITERAL )
                    {
                    dbg.location(58,4);
                    dbg.location(58,6);
                    match(input,71,FOLLOW_71_in_tokenSpec154); 

                    match(input, Token.DOWN, null); 
                    dbg.location(58,10);
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenSpec156); 
                    dbg.location(58,20);
                    match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_tokenSpec158); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:59:4: TOKEN_REF
                    {
                    dbg.location(59,4);
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenSpec164); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(60, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "tokenSpec");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "tokenSpec"


    // $ANTLR start "attrScope"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:62:1: attrScope : ^( 'scope' ID ACTION ) ;
    public final void attrScope() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "attrScope");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(62, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:63:2: ( ^( 'scope' ID ACTION ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:63:4: ^( 'scope' ID ACTION )
            {
            dbg.location(63,4);
            dbg.location(63,6);
            match(input,SCOPE,FOLLOW_SCOPE_in_attrScope176); 

            match(input, Token.DOWN, null); 
            dbg.location(63,14);
            match(input,ID,FOLLOW_ID_in_attrScope178); 
            dbg.location(63,17);
            match(input,ACTION,FOLLOW_ACTION_in_attrScope180); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(64, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "attrScope");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "attrScope"


    // $ANTLR start "action"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:66:1: action : ( ^( '@' ID ID ACTION ) | ^( '@' ID ACTION ) );
    public final void action() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "action");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(66, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:67:2: ( ^( '@' ID ID ACTION ) | ^( '@' ID ACTION ) )
            int alt9=2;
            try { dbg.enterDecision(9, decisionCanBacktrack[9]);

            int LA9_0 = input.LA(1);

            if ( (LA9_0==72) ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==DOWN) ) {
                    int LA9_2 = input.LA(3);

                    if ( (LA9_2==ID) ) {
                        int LA9_3 = input.LA(4);

                        if ( (LA9_3==ID) ) {
                            alt9=1;
                        }
                        else if ( (LA9_3==ACTION) ) {
                            alt9=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 3, input);

                            dbg.recognitionException(nvae);
                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 2, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(9);}

            switch (alt9) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:67:4: ^( '@' ID ID ACTION )
                    {
                    dbg.location(67,4);
                    dbg.location(67,6);
                    match(input,72,FOLLOW_72_in_action193); 

                    match(input, Token.DOWN, null); 
                    dbg.location(67,10);
                    match(input,ID,FOLLOW_ID_in_action195); 
                    dbg.location(67,13);
                    match(input,ID,FOLLOW_ID_in_action197); 
                    dbg.location(67,16);
                    match(input,ACTION,FOLLOW_ACTION_in_action199); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:68:4: ^( '@' ID ACTION )
                    {
                    dbg.location(68,4);
                    dbg.location(68,6);
                    match(input,72,FOLLOW_72_in_action206); 

                    match(input, Token.DOWN, null); 
                    dbg.location(68,10);
                    match(input,ID,FOLLOW_ID_in_action208); 
                    dbg.location(68,13);
                    match(input,ACTION,FOLLOW_ACTION_in_action210); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(69, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "action");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "action"


    // $ANTLR start "optionsSpec"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:71:1: optionsSpec : ^( OPTIONS ( option )+ ) ;
    public final void optionsSpec() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "optionsSpec");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(71, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:72:2: ( ^( OPTIONS ( option )+ ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:72:4: ^( OPTIONS ( option )+ )
            {
            dbg.location(72,4);
            dbg.location(72,6);
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec223); 

            match(input, Token.DOWN, null); 
            dbg.location(72,14);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:72:14: ( option )+
            int cnt10=0;
            try { dbg.enterSubRule(10);

            loop10:
            do {
                int alt10=2;
                try { dbg.enterDecision(10, decisionCanBacktrack[10]);

                int LA10_0 = input.LA(1);

                if ( (LA10_0==71) ) {
                    alt10=1;
                }


                } finally {dbg.exitDecision(10);}

                switch (alt10) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:72:14: option
            	    {
            	    dbg.location(72,14);
            	    pushFollow(FOLLOW_option_in_optionsSpec225);
            	    option();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt10++;
            } while (true);
            } finally {dbg.exitSubRule(10);}


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(73, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "optionsSpec");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "optionsSpec"


    // $ANTLR start "option"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:75:1: option : ^( '=' ID optionValue ) ;
    public final void option() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "option");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(75, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:76:5: ( ^( '=' ID optionValue ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:76:9: ^( '=' ID optionValue )
            {
            dbg.location(76,9);
            dbg.location(76,11);
            match(input,71,FOLLOW_71_in_option244); 

            match(input, Token.DOWN, null); 
            dbg.location(76,15);
            match(input,ID,FOLLOW_ID_in_option246); 
            dbg.location(76,18);
            pushFollow(FOLLOW_optionValue_in_option248);
            optionValue();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(77, 3);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "option");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "option"


    // $ANTLR start "optionValue"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:79:1: optionValue : ( ID | STRING_LITERAL | CHAR_LITERAL | INT );
    public final void optionValue() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "optionValue");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(79, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:80:5: ( ID | STRING_LITERAL | CHAR_LITERAL | INT )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:
            {
            dbg.location(80,5);
            if ( input.LA(1)==ID||(input.LA(1)>=STRING_LITERAL && input.LA(1)<=CHAR_LITERAL)||input.LA(1)==INT ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(84, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "optionValue");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "optionValue"


    // $ANTLR start "rule"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:86:1: rule : ^( RULE ID ( modifier )? ( ^( ARG ARG_ACTION ) )? ( ^( RET ARG_ACTION ) )? ( optionsSpec )? ( ruleScopeSpec )? ( ruleAction )* altList ( exceptionGroup )? EOR ) ;
    public final void rule() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rule");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(86, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:2: ( ^( RULE ID ( modifier )? ( ^( ARG ARG_ACTION ) )? ( ^( RET ARG_ACTION ) )? ( optionsSpec )? ( ruleScopeSpec )? ( ruleAction )* altList ( exceptionGroup )? EOR ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:4: ^( RULE ID ( modifier )? ( ^( ARG ARG_ACTION ) )? ( ^( RET ARG_ACTION ) )? ( optionsSpec )? ( ruleScopeSpec )? ( ruleAction )* altList ( exceptionGroup )? EOR )
            {
            dbg.location(87,4);
            dbg.location(87,7);
            match(input,RULE,FOLLOW_RULE_in_rule314); 

            match(input, Token.DOWN, null); 
            dbg.location(87,12);
            match(input,ID,FOLLOW_ID_in_rule316); 
            dbg.location(87,15);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:15: ( modifier )?
            int alt11=2;
            try { dbg.enterSubRule(11);
            try { dbg.enterDecision(11, decisionCanBacktrack[11]);

            int LA11_0 = input.LA(1);

            if ( (LA11_0==FRAGMENT||(LA11_0>=75 && LA11_0<=77)) ) {
                alt11=1;
            }
            } finally {dbg.exitDecision(11);}

            switch (alt11) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:15: modifier
                    {
                    dbg.location(87,15);
                    pushFollow(FOLLOW_modifier_in_rule318);
                    modifier();

                    state._fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(11);}

            dbg.location(87,25);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:25: ( ^( ARG ARG_ACTION ) )?
            int alt12=2;
            try { dbg.enterSubRule(12);
            try { dbg.enterDecision(12, decisionCanBacktrack[12]);

            int LA12_0 = input.LA(1);

            if ( (LA12_0==ARG) ) {
                alt12=1;
            }
            } finally {dbg.exitDecision(12);}

            switch (alt12) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:26: ^( ARG ARG_ACTION )
                    {
                    dbg.location(87,26);
                    dbg.location(87,28);
                    match(input,ARG,FOLLOW_ARG_in_rule323); 

                    match(input, Token.DOWN, null); 
                    dbg.location(87,32);
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule325); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
            } finally {dbg.exitSubRule(12);}

            dbg.location(87,46);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:46: ( ^( RET ARG_ACTION ) )?
            int alt13=2;
            try { dbg.enterSubRule(13);
            try { dbg.enterDecision(13, decisionCanBacktrack[13]);

            int LA13_0 = input.LA(1);

            if ( (LA13_0==RET) ) {
                alt13=1;
            }
            } finally {dbg.exitDecision(13);}

            switch (alt13) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:87:47: ^( RET ARG_ACTION )
                    {
                    dbg.location(87,47);
                    dbg.location(87,49);
                    match(input,RET,FOLLOW_RET_in_rule332); 

                    match(input, Token.DOWN, null); 
                    dbg.location(87,53);
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule334); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
            } finally {dbg.exitSubRule(13);}

            dbg.location(88,9);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:88:9: ( optionsSpec )?
            int alt14=2;
            try { dbg.enterSubRule(14);
            try { dbg.enterDecision(14, decisionCanBacktrack[14]);

            int LA14_0 = input.LA(1);

            if ( (LA14_0==OPTIONS) ) {
                alt14=1;
            }
            } finally {dbg.exitDecision(14);}

            switch (alt14) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:88:9: optionsSpec
                    {
                    dbg.location(88,9);
                    pushFollow(FOLLOW_optionsSpec_in_rule347);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(14);}

            dbg.location(88,22);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:88:22: ( ruleScopeSpec )?
            int alt15=2;
            try { dbg.enterSubRule(15);
            try { dbg.enterDecision(15, decisionCanBacktrack[15]);

            int LA15_0 = input.LA(1);

            if ( (LA15_0==SCOPE) ) {
                alt15=1;
            }
            } finally {dbg.exitDecision(15);}

            switch (alt15) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:88:22: ruleScopeSpec
                    {
                    dbg.location(88,22);
                    pushFollow(FOLLOW_ruleScopeSpec_in_rule350);
                    ruleScopeSpec();

                    state._fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(15);}

            dbg.location(88,37);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:88:37: ( ruleAction )*
            try { dbg.enterSubRule(16);

            loop16:
            do {
                int alt16=2;
                try { dbg.enterDecision(16, decisionCanBacktrack[16]);

                int LA16_0 = input.LA(1);

                if ( (LA16_0==72) ) {
                    alt16=1;
                }


                } finally {dbg.exitDecision(16);}

                switch (alt16) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:88:37: ruleAction
            	    {
            	    dbg.location(88,37);
            	    pushFollow(FOLLOW_ruleAction_in_rule353);
            	    ruleAction();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);
            } finally {dbg.exitSubRule(16);}

            dbg.location(89,9);
            pushFollow(FOLLOW_altList_in_rule364);
            altList();

            state._fsp--;

            dbg.location(90,9);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:90:9: ( exceptionGroup )?
            int alt17=2;
            try { dbg.enterSubRule(17);
            try { dbg.enterDecision(17, decisionCanBacktrack[17]);

            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=85 && LA17_0<=86)) ) {
                alt17=1;
            }
            } finally {dbg.exitDecision(17);}

            switch (alt17) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:90:9: exceptionGroup
                    {
                    dbg.location(90,9);
                    pushFollow(FOLLOW_exceptionGroup_in_rule374);
                    exceptionGroup();

                    state._fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(17);}

            dbg.location(90,25);
            match(input,EOR,FOLLOW_EOR_in_rule377); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(92, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rule");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rule"


    // $ANTLR start "modifier"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:94:1: modifier : ( 'protected' | 'public' | 'private' | 'fragment' );
    public final void modifier() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "modifier");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(94, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:95:2: ( 'protected' | 'public' | 'private' | 'fragment' )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:
            {
            dbg.location(95,2);
            if ( input.LA(1)==FRAGMENT||(input.LA(1)>=75 && input.LA(1)<=77) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(96, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "modifier");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "modifier"


    // $ANTLR start "ruleAction"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:98:1: ruleAction : ^( '@' ID ACTION ) ;
    public final void ruleAction() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "ruleAction");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(98, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:100:2: ( ^( '@' ID ACTION ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:100:4: ^( '@' ID ACTION )
            {
            dbg.location(100,4);
            dbg.location(100,6);
            match(input,72,FOLLOW_72_in_ruleAction416); 

            match(input, Token.DOWN, null); 
            dbg.location(100,10);
            match(input,ID,FOLLOW_ID_in_ruleAction418); 
            dbg.location(100,13);
            match(input,ACTION,FOLLOW_ACTION_in_ruleAction420); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(101, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "ruleAction");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "ruleAction"


    // $ANTLR start "throwsSpec"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:103:1: throwsSpec : ^( 'throws' ( ID )+ ) ;
    public final void throwsSpec() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "throwsSpec");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(103, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:104:2: ( ^( 'throws' ( ID )+ ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:104:4: ^( 'throws' ( ID )+ )
            {
            dbg.location(104,4);
            dbg.location(104,6);
            match(input,80,FOLLOW_80_in_throwsSpec433); 

            match(input, Token.DOWN, null); 
            dbg.location(104,15);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:104:15: ( ID )+
            int cnt18=0;
            try { dbg.enterSubRule(18);

            loop18:
            do {
                int alt18=2;
                try { dbg.enterDecision(18, decisionCanBacktrack[18]);

                int LA18_0 = input.LA(1);

                if ( (LA18_0==ID) ) {
                    alt18=1;
                }


                } finally {dbg.exitDecision(18);}

                switch (alt18) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:104:15: ID
            	    {
            	    dbg.location(104,15);
            	    match(input,ID,FOLLOW_ID_in_throwsSpec435); 

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt18++;
            } while (true);
            } finally {dbg.exitSubRule(18);}


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(105, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "throwsSpec");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "throwsSpec"


    // $ANTLR start "ruleScopeSpec"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:107:1: ruleScopeSpec : ( ^( 'scope' ACTION ) | ^( 'scope' ACTION ( ID )+ ) | ^( 'scope' ( ID )+ ) );
    public final void ruleScopeSpec() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "ruleScopeSpec");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(107, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:108:2: ( ^( 'scope' ACTION ) | ^( 'scope' ACTION ( ID )+ ) | ^( 'scope' ( ID )+ ) )
            int alt21=3;
            try { dbg.enterDecision(21, decisionCanBacktrack[21]);

            int LA21_0 = input.LA(1);

            if ( (LA21_0==SCOPE) ) {
                int LA21_1 = input.LA(2);

                if ( (LA21_1==DOWN) ) {
                    int LA21_2 = input.LA(3);

                    if ( (LA21_2==ACTION) ) {
                        int LA21_3 = input.LA(4);

                        if ( (LA21_3==UP) ) {
                            alt21=1;
                        }
                        else if ( (LA21_3==ID) ) {
                            alt21=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 21, 3, input);

                            dbg.recognitionException(nvae);
                            throw nvae;
                        }
                    }
                    else if ( (LA21_2==ID) ) {
                        alt21=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 21, 2, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(21);}

            switch (alt21) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:108:4: ^( 'scope' ACTION )
                    {
                    dbg.location(108,4);
                    dbg.location(108,6);
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec449); 

                    match(input, Token.DOWN, null); 
                    dbg.location(108,14);
                    match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec451); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:109:4: ^( 'scope' ACTION ( ID )+ )
                    {
                    dbg.location(109,4);
                    dbg.location(109,6);
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec458); 

                    match(input, Token.DOWN, null); 
                    dbg.location(109,14);
                    match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec460); 
                    dbg.location(109,21);
                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:109:21: ( ID )+
                    int cnt19=0;
                    try { dbg.enterSubRule(19);

                    loop19:
                    do {
                        int alt19=2;
                        try { dbg.enterDecision(19, decisionCanBacktrack[19]);

                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==ID) ) {
                            alt19=1;
                        }


                        } finally {dbg.exitDecision(19);}

                        switch (alt19) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:109:21: ID
                    	    {
                    	    dbg.location(109,21);
                    	    match(input,ID,FOLLOW_ID_in_ruleScopeSpec462); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt19 >= 1 ) break loop19;
                                EarlyExitException eee =
                                    new EarlyExitException(19, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt19++;
                    } while (true);
                    } finally {dbg.exitSubRule(19);}


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:110:4: ^( 'scope' ( ID )+ )
                    {
                    dbg.location(110,4);
                    dbg.location(110,6);
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec470); 

                    match(input, Token.DOWN, null); 
                    dbg.location(110,14);
                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:110:14: ( ID )+
                    int cnt20=0;
                    try { dbg.enterSubRule(20);

                    loop20:
                    do {
                        int alt20=2;
                        try { dbg.enterDecision(20, decisionCanBacktrack[20]);

                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==ID) ) {
                            alt20=1;
                        }


                        } finally {dbg.exitDecision(20);}

                        switch (alt20) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:110:14: ID
                    	    {
                    	    dbg.location(110,14);
                    	    match(input,ID,FOLLOW_ID_in_ruleScopeSpec472); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt20 >= 1 ) break loop20;
                                EarlyExitException eee =
                                    new EarlyExitException(20, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt20++;
                    } while (true);
                    } finally {dbg.exitSubRule(20);}


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(111, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "ruleScopeSpec");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "ruleScopeSpec"


    // $ANTLR start "block"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:113:1: block : ^( BLOCK ( optionsSpec )? ( alternative rewrite )+ EOB ) ;
    public final void block() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "block");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(113, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:114:5: ( ^( BLOCK ( optionsSpec )? ( alternative rewrite )+ EOB ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:114:9: ^( BLOCK ( optionsSpec )? ( alternative rewrite )+ EOB )
            {
            dbg.location(114,9);
            dbg.location(114,12);
            match(input,BLOCK,FOLLOW_BLOCK_in_block492); 

            match(input, Token.DOWN, null); 
            dbg.location(114,18);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:114:18: ( optionsSpec )?
            int alt22=2;
            try { dbg.enterSubRule(22);
            try { dbg.enterDecision(22, decisionCanBacktrack[22]);

            int LA22_0 = input.LA(1);

            if ( (LA22_0==OPTIONS) ) {
                alt22=1;
            }
            } finally {dbg.exitDecision(22);}

            switch (alt22) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:114:18: optionsSpec
                    {
                    dbg.location(114,18);
                    pushFollow(FOLLOW_optionsSpec_in_block494);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(22);}

            dbg.location(114,31);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:114:31: ( alternative rewrite )+
            int cnt23=0;
            try { dbg.enterSubRule(23);

            loop23:
            do {
                int alt23=2;
                try { dbg.enterDecision(23, decisionCanBacktrack[23]);

                int LA23_0 = input.LA(1);

                if ( (LA23_0==ALT) ) {
                    alt23=1;
                }


                } finally {dbg.exitDecision(23);}

                switch (alt23) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:114:32: alternative rewrite
            	    {
            	    dbg.location(114,32);
            	    pushFollow(FOLLOW_alternative_in_block498);
            	    alternative();

            	    state._fsp--;

            	    dbg.location(114,44);
            	    pushFollow(FOLLOW_rewrite_in_block500);
            	    rewrite();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt23++;
            } while (true);
            } finally {dbg.exitSubRule(23);}

            dbg.location(114,54);
            match(input,EOB,FOLLOW_EOB_in_block504); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(115, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "block");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "block"


    // $ANTLR start "altList"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:117:1: altList : ^( BLOCK ( alternative rewrite )+ EOB ) ;
    public final void altList() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "altList");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(117, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:118:5: ( ^( BLOCK ( alternative rewrite )+ EOB ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:118:9: ^( BLOCK ( alternative rewrite )+ EOB )
            {
            dbg.location(118,9);
            dbg.location(118,12);
            match(input,BLOCK,FOLLOW_BLOCK_in_altList527); 

            match(input, Token.DOWN, null); 
            dbg.location(118,18);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:118:18: ( alternative rewrite )+
            int cnt24=0;
            try { dbg.enterSubRule(24);

            loop24:
            do {
                int alt24=2;
                try { dbg.enterDecision(24, decisionCanBacktrack[24]);

                int LA24_0 = input.LA(1);

                if ( (LA24_0==ALT) ) {
                    alt24=1;
                }


                } finally {dbg.exitDecision(24);}

                switch (alt24) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:118:19: alternative rewrite
            	    {
            	    dbg.location(118,19);
            	    pushFollow(FOLLOW_alternative_in_altList530);
            	    alternative();

            	    state._fsp--;

            	    dbg.location(118,31);
            	    pushFollow(FOLLOW_rewrite_in_altList532);
            	    rewrite();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt24++;
            } while (true);
            } finally {dbg.exitSubRule(24);}

            dbg.location(118,41);
            match(input,EOB,FOLLOW_EOB_in_altList536); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(119, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "altList");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "altList"


    // $ANTLR start "alternative"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:121:1: alternative : ( ^( ALT ( element )+ EOA ) | ^( ALT EPSILON EOA ) );
    public final void alternative() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "alternative");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(121, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:122:5: ( ^( ALT ( element )+ EOA ) | ^( ALT EPSILON EOA ) )
            int alt26=2;
            try { dbg.enterDecision(26, decisionCanBacktrack[26]);

            int LA26_0 = input.LA(1);

            if ( (LA26_0==ALT) ) {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==DOWN) ) {
                    int LA26_2 = input.LA(3);

                    if ( (LA26_2==EPSILON) ) {
                        alt26=2;
                    }
                    else if ( ((LA26_2>=BLOCK && LA26_2<=SYNPRED)||LA26_2==CHAR_RANGE||(LA26_2>=SEMPRED && LA26_2<=SYN_SEMPRED)||(LA26_2>=TREE_BEGIN && LA26_2<=BANG)||(LA26_2>=TOKEN_REF && LA26_2<=ACTION)||LA26_2==RULE_REF||LA26_2==71||LA26_2==87||LA26_2==89||LA26_2==92) ) {
                        alt26=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 2, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(26);}

            switch (alt26) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:122:9: ^( ALT ( element )+ EOA )
                    {
                    dbg.location(122,9);
                    dbg.location(122,11);
                    match(input,ALT,FOLLOW_ALT_in_alternative558); 

                    match(input, Token.DOWN, null); 
                    dbg.location(122,15);
                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:122:15: ( element )+
                    int cnt25=0;
                    try { dbg.enterSubRule(25);

                    loop25:
                    do {
                        int alt25=2;
                        try { dbg.enterDecision(25, decisionCanBacktrack[25]);

                        int LA25_0 = input.LA(1);

                        if ( ((LA25_0>=BLOCK && LA25_0<=SYNPRED)||LA25_0==CHAR_RANGE||(LA25_0>=SEMPRED && LA25_0<=SYN_SEMPRED)||(LA25_0>=TREE_BEGIN && LA25_0<=BANG)||(LA25_0>=TOKEN_REF && LA25_0<=ACTION)||LA25_0==RULE_REF||LA25_0==71||LA25_0==87||LA25_0==89||LA25_0==92) ) {
                            alt25=1;
                        }


                        } finally {dbg.exitDecision(25);}

                        switch (alt25) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:122:15: element
                    	    {
                    	    dbg.location(122,15);
                    	    pushFollow(FOLLOW_element_in_alternative560);
                    	    element();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt25 >= 1 ) break loop25;
                                EarlyExitException eee =
                                    new EarlyExitException(25, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt25++;
                    } while (true);
                    } finally {dbg.exitSubRule(25);}

                    dbg.location(122,24);
                    match(input,EOA,FOLLOW_EOA_in_alternative563); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:123:9: ^( ALT EPSILON EOA )
                    {
                    dbg.location(123,9);
                    dbg.location(123,11);
                    match(input,ALT,FOLLOW_ALT_in_alternative575); 

                    match(input, Token.DOWN, null); 
                    dbg.location(123,15);
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative577); 
                    dbg.location(123,23);
                    match(input,EOA,FOLLOW_EOA_in_alternative579); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(124, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "alternative");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "alternative"


    // $ANTLR start "exceptionGroup"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:126:1: exceptionGroup : ( ( exceptionHandler )+ ( finallyClause )? | finallyClause );
    public final void exceptionGroup() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "exceptionGroup");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(126, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:127:2: ( ( exceptionHandler )+ ( finallyClause )? | finallyClause )
            int alt29=2;
            try { dbg.enterDecision(29, decisionCanBacktrack[29]);

            int LA29_0 = input.LA(1);

            if ( (LA29_0==85) ) {
                alt29=1;
            }
            else if ( (LA29_0==86) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(29);}

            switch (alt29) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:127:4: ( exceptionHandler )+ ( finallyClause )?
                    {
                    dbg.location(127,4);
                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:127:4: ( exceptionHandler )+
                    int cnt27=0;
                    try { dbg.enterSubRule(27);

                    loop27:
                    do {
                        int alt27=2;
                        try { dbg.enterDecision(27, decisionCanBacktrack[27]);

                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==85) ) {
                            alt27=1;
                        }


                        } finally {dbg.exitDecision(27);}

                        switch (alt27) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:127:4: exceptionHandler
                    	    {
                    	    dbg.location(127,4);
                    	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup594);
                    	    exceptionHandler();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt27 >= 1 ) break loop27;
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt27++;
                    } while (true);
                    } finally {dbg.exitSubRule(27);}

                    dbg.location(127,22);
                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:127:22: ( finallyClause )?
                    int alt28=2;
                    try { dbg.enterSubRule(28);
                    try { dbg.enterDecision(28, decisionCanBacktrack[28]);

                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==86) ) {
                        alt28=1;
                    }
                    } finally {dbg.exitDecision(28);}

                    switch (alt28) {
                        case 1 :
                            dbg.enterAlt(1);

                            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:127:22: finallyClause
                            {
                            dbg.location(127,22);
                            pushFollow(FOLLOW_finallyClause_in_exceptionGroup597);
                            finallyClause();

                            state._fsp--;


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(28);}


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:128:4: finallyClause
                    {
                    dbg.location(128,4);
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup603);
                    finallyClause();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(129, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "exceptionGroup");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "exceptionGroup"


    // $ANTLR start "exceptionHandler"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:131:1: exceptionHandler : ^( 'catch' ARG_ACTION ACTION ) ;
    public final void exceptionHandler() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "exceptionHandler");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(131, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:132:5: ( ^( 'catch' ARG_ACTION ACTION ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:132:10: ^( 'catch' ARG_ACTION ACTION )
            {
            dbg.location(132,10);
            dbg.location(132,12);
            match(input,85,FOLLOW_85_in_exceptionHandler624); 

            match(input, Token.DOWN, null); 
            dbg.location(132,20);
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler626); 
            dbg.location(132,31);
            match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler628); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(133, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "exceptionHandler");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "exceptionHandler"


    // $ANTLR start "finallyClause"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:135:1: finallyClause : ^( 'finally' ACTION ) ;
    public final void finallyClause() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "finallyClause");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(135, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:136:5: ( ^( 'finally' ACTION ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:136:10: ^( 'finally' ACTION )
            {
            dbg.location(136,10);
            dbg.location(136,12);
            match(input,86,FOLLOW_86_in_finallyClause650); 

            match(input, Token.DOWN, null); 
            dbg.location(136,22);
            match(input,ACTION,FOLLOW_ACTION_in_finallyClause652); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(137, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "finallyClause");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "finallyClause"


    // $ANTLR start "element"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:139:1: element : elementNoOptionSpec ;
    public final void element() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "element");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(139, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:140:2: ( elementNoOptionSpec )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:140:4: elementNoOptionSpec
            {
            dbg.location(140,4);
            pushFollow(FOLLOW_elementNoOptionSpec_in_element667);
            elementNoOptionSpec();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(141, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "element");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "element"


    // $ANTLR start "elementNoOptionSpec"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:143:1: elementNoOptionSpec : ( ^( ( '=' | '+=' ) ID block ) | ^( ( '=' | '+=' ) ID atom ) | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final void elementNoOptionSpec() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "elementNoOptionSpec");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(143, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:144:2: ( ^( ( '=' | '+=' ) ID block ) | ^( ( '=' | '+=' ) ID atom ) | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt30=8;
            try { dbg.enterDecision(30, decisionCanBacktrack[30]);

            try {
                isCyclicDecision = true;
                alt30 = dfa30.predict(input);
            }
            catch (NoViableAltException nvae) {
                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(30);}

            switch (alt30) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:144:4: ^( ( '=' | '+=' ) ID block )
                    {
                    dbg.location(144,4);
                    dbg.location(144,6);
                    if ( input.LA(1)==71||input.LA(1)==87 ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        dbg.recognitionException(mse);
                        throw mse;
                    }


                    match(input, Token.DOWN, null); 
                    dbg.location(144,17);
                    match(input,ID,FOLLOW_ID_in_elementNoOptionSpec685); 
                    dbg.location(144,20);
                    pushFollow(FOLLOW_block_in_elementNoOptionSpec687);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:145:4: ^( ( '=' | '+=' ) ID atom )
                    {
                    dbg.location(145,4);
                    dbg.location(145,6);
                    if ( input.LA(1)==71||input.LA(1)==87 ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        dbg.recognitionException(mse);
                        throw mse;
                    }


                    match(input, Token.DOWN, null); 
                    dbg.location(145,17);
                    match(input,ID,FOLLOW_ID_in_elementNoOptionSpec700); 
                    dbg.location(145,20);
                    pushFollow(FOLLOW_atom_in_elementNoOptionSpec702);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:146:4: atom
                    {
                    dbg.location(146,4);
                    pushFollow(FOLLOW_atom_in_elementNoOptionSpec708);
                    atom();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:147:4: ebnf
                    {
                    dbg.location(147,4);
                    pushFollow(FOLLOW_ebnf_in_elementNoOptionSpec713);
                    ebnf();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:148:6: ACTION
                    {
                    dbg.location(148,6);
                    match(input,ACTION,FOLLOW_ACTION_in_elementNoOptionSpec720); 

                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:149:6: SEMPRED
                    {
                    dbg.location(149,6);
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_elementNoOptionSpec727); 

                    }
                    break;
                case 7 :
                    dbg.enterAlt(7);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:150:4: GATED_SEMPRED
                    {
                    dbg.location(150,4);
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_elementNoOptionSpec732); 

                    }
                    break;
                case 8 :
                    dbg.enterAlt(8);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:151:6: treeSpec
                    {
                    dbg.location(151,6);
                    pushFollow(FOLLOW_treeSpec_in_elementNoOptionSpec739);
                    treeSpec();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(152, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "elementNoOptionSpec");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "elementNoOptionSpec"


    // $ANTLR start "atom"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:154:1: atom : ( ^( ( '^' | '!' ) atom ) | range | notSet | ^( RULE_REF ARG_ACTION ) | RULE_REF | terminal );
    public final void atom() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "atom");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(154, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:154:5: ( ^( ( '^' | '!' ) atom ) | range | notSet | ^( RULE_REF ARG_ACTION ) | RULE_REF | terminal )
            int alt31=6;
            try { dbg.enterDecision(31, decisionCanBacktrack[31]);

            switch ( input.LA(1) ) {
            case ROOT:
            case BANG:
                {
                alt31=1;
                }
                break;
            case CHAR_RANGE:
                {
                alt31=2;
                }
                break;
            case 89:
                {
                alt31=3;
                }
                break;
            case RULE_REF:
                {
                int LA31_4 = input.LA(2);

                if ( (LA31_4==DOWN) ) {
                    alt31=4;
                }
                else if ( (LA31_4==UP||(LA31_4>=BLOCK && LA31_4<=SYNPRED)||LA31_4==CHAR_RANGE||LA31_4==EOA||(LA31_4>=SEMPRED && LA31_4<=SYN_SEMPRED)||(LA31_4>=TREE_BEGIN && LA31_4<=BANG)||(LA31_4>=TOKEN_REF && LA31_4<=ACTION)||LA31_4==RULE_REF||LA31_4==71||LA31_4==87||LA31_4==89||LA31_4==92) ) {
                    alt31=5;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 4, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
                }
                break;
            case TOKEN_REF:
            case STRING_LITERAL:
            case CHAR_LITERAL:
            case 92:
                {
                alt31=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(31);}

            switch (alt31) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:154:9: ^( ( '^' | '!' ) atom )
                    {
                    dbg.location(154,9);
                    dbg.location(154,11);
                    if ( (input.LA(1)>=ROOT && input.LA(1)<=BANG) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        dbg.recognitionException(mse);
                        throw mse;
                    }


                    match(input, Token.DOWN, null); 
                    dbg.location(154,21);
                    pushFollow(FOLLOW_atom_in_atom757);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:155:4: range
                    {
                    dbg.location(155,4);
                    pushFollow(FOLLOW_range_in_atom763);
                    range();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:156:4: notSet
                    {
                    dbg.location(156,4);
                    pushFollow(FOLLOW_notSet_in_atom768);
                    notSet();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:157:7: ^( RULE_REF ARG_ACTION )
                    {
                    dbg.location(157,7);
                    dbg.location(157,9);
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_atom777); 

                    match(input, Token.DOWN, null); 
                    dbg.location(157,18);
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_atom779); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:158:7: RULE_REF
                    {
                    dbg.location(158,7);
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_atom788); 

                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:159:9: terminal
                    {
                    dbg.location(159,9);
                    pushFollow(FOLLOW_terminal_in_atom798);
                    terminal();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(160, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "atom");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "atom"


    // $ANTLR start "notSet"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:162:1: notSet : ( ^( '~' notTerminal ) | ^( '~' block ) );
    public final void notSet() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "notSet");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(162, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:163:2: ( ^( '~' notTerminal ) | ^( '~' block ) )
            int alt32=2;
            try { dbg.enterDecision(32, decisionCanBacktrack[32]);

            int LA32_0 = input.LA(1);

            if ( (LA32_0==89) ) {
                int LA32_1 = input.LA(2);

                if ( (LA32_1==DOWN) ) {
                    int LA32_2 = input.LA(3);

                    if ( ((LA32_2>=TOKEN_REF && LA32_2<=CHAR_LITERAL)) ) {
                        alt32=1;
                    }
                    else if ( (LA32_2==BLOCK) ) {
                        alt32=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 2, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(32);}

            switch (alt32) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:163:4: ^( '~' notTerminal )
                    {
                    dbg.location(163,4);
                    dbg.location(163,6);
                    match(input,89,FOLLOW_89_in_notSet813); 

                    match(input, Token.DOWN, null); 
                    dbg.location(163,10);
                    pushFollow(FOLLOW_notTerminal_in_notSet815);
                    notTerminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:164:4: ^( '~' block )
                    {
                    dbg.location(164,4);
                    dbg.location(164,6);
                    match(input,89,FOLLOW_89_in_notSet822); 

                    match(input, Token.DOWN, null); 
                    dbg.location(164,10);
                    pushFollow(FOLLOW_block_in_notSet824);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(165, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "notSet");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "notSet"


    // $ANTLR start "treeSpec"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:167:1: treeSpec : ^( TREE_BEGIN ( element )+ ) ;
    public final void treeSpec() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "treeSpec");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(167, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:168:2: ( ^( TREE_BEGIN ( element )+ ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:168:4: ^( TREE_BEGIN ( element )+ )
            {
            dbg.location(168,4);
            dbg.location(168,6);
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec837); 

            match(input, Token.DOWN, null); 
            dbg.location(168,17);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:168:17: ( element )+
            int cnt33=0;
            try { dbg.enterSubRule(33);

            loop33:
            do {
                int alt33=2;
                try { dbg.enterDecision(33, decisionCanBacktrack[33]);

                int LA33_0 = input.LA(1);

                if ( ((LA33_0>=BLOCK && LA33_0<=SYNPRED)||LA33_0==CHAR_RANGE||(LA33_0>=SEMPRED && LA33_0<=SYN_SEMPRED)||(LA33_0>=TREE_BEGIN && LA33_0<=BANG)||(LA33_0>=TOKEN_REF && LA33_0<=ACTION)||LA33_0==RULE_REF||LA33_0==71||LA33_0==87||LA33_0==89||LA33_0==92) ) {
                    alt33=1;
                }


                } finally {dbg.exitDecision(33);}

                switch (alt33) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:168:17: element
            	    {
            	    dbg.location(168,17);
            	    pushFollow(FOLLOW_element_in_treeSpec839);
            	    element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt33 >= 1 ) break loop33;
                        EarlyExitException eee =
                            new EarlyExitException(33, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt33++;
            } while (true);
            } finally {dbg.exitSubRule(33);}


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(169, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "treeSpec");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "treeSpec"


    // $ANTLR start "ebnf"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:171:1: ebnf : ( ^( SYNPRED block ) | SYN_SEMPRED | ^( ebnfSuffix block ) | block );
    public final void ebnf() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "ebnf");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(171, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:173:2: ( ^( SYNPRED block ) | SYN_SEMPRED | ^( ebnfSuffix block ) | block )
            int alt34=4;
            try { dbg.enterDecision(34, decisionCanBacktrack[34]);

            switch ( input.LA(1) ) {
            case SYNPRED:
                {
                alt34=1;
                }
                break;
            case SYN_SEMPRED:
                {
                alt34=2;
                }
                break;
            case OPTIONAL:
            case CLOSURE:
            case POSITIVE_CLOSURE:
                {
                alt34=3;
                }
                break;
            case BLOCK:
                {
                alt34=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(34);}

            switch (alt34) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:173:4: ^( SYNPRED block )
                    {
                    dbg.location(173,4);
                    dbg.location(173,6);
                    match(input,SYNPRED,FOLLOW_SYNPRED_in_ebnf855); 

                    match(input, Token.DOWN, null); 
                    dbg.location(173,14);
                    pushFollow(FOLLOW_block_in_ebnf857);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:174:4: SYN_SEMPRED
                    {
                    dbg.location(174,4);
                    match(input,SYN_SEMPRED,FOLLOW_SYN_SEMPRED_in_ebnf863); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:175:4: ^( ebnfSuffix block )
                    {
                    dbg.location(175,4);
                    dbg.location(175,6);
                    pushFollow(FOLLOW_ebnfSuffix_in_ebnf869);
                    ebnfSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    dbg.location(175,17);
                    pushFollow(FOLLOW_block_in_ebnf871);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:176:4: block
                    {
                    dbg.location(176,4);
                    pushFollow(FOLLOW_block_in_ebnf877);
                    block();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(177, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "ebnf");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "ebnf"


    // $ANTLR start "range"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:179:1: range : ^( CHAR_RANGE CHAR_LITERAL CHAR_LITERAL ) ;
    public final void range() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "range");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(179, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:180:2: ( ^( CHAR_RANGE CHAR_LITERAL CHAR_LITERAL ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:180:4: ^( CHAR_RANGE CHAR_LITERAL CHAR_LITERAL )
            {
            dbg.location(180,4);
            dbg.location(180,6);
            match(input,CHAR_RANGE,FOLLOW_CHAR_RANGE_in_range889); 

            match(input, Token.DOWN, null); 
            dbg.location(180,17);
            match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_range891); 
            dbg.location(180,30);
            match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_range893); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(181, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "range");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "range"


    // $ANTLR start "terminal"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:183:1: terminal : ( CHAR_LITERAL | TOKEN_REF | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION ) | '.' );
    public final void terminal() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "terminal");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(183, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:184:5: ( CHAR_LITERAL | TOKEN_REF | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION ) | '.' )
            int alt35=5;
            try { dbg.enterDecision(35, decisionCanBacktrack[35]);

            switch ( input.LA(1) ) {
            case CHAR_LITERAL:
                {
                alt35=1;
                }
                break;
            case TOKEN_REF:
                {
                int LA35_2 = input.LA(2);

                if ( (LA35_2==DOWN) ) {
                    alt35=4;
                }
                else if ( (LA35_2==UP||(LA35_2>=BLOCK && LA35_2<=SYNPRED)||LA35_2==CHAR_RANGE||LA35_2==EOA||(LA35_2>=SEMPRED && LA35_2<=SYN_SEMPRED)||(LA35_2>=TREE_BEGIN && LA35_2<=BANG)||(LA35_2>=TOKEN_REF && LA35_2<=ACTION)||LA35_2==RULE_REF||LA35_2==71||LA35_2==87||LA35_2==89||LA35_2==92) ) {
                    alt35=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 35, 2, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
                }
                break;
            case STRING_LITERAL:
                {
                alt35=3;
                }
                break;
            case 92:
                {
                alt35=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(35);}

            switch (alt35) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:184:9: CHAR_LITERAL
                    {
                    dbg.location(184,9);
                    match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_terminal910); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:185:7: TOKEN_REF
                    {
                    dbg.location(185,7);
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal918); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:186:7: STRING_LITERAL
                    {
                    dbg.location(186,7);
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal926); 

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:187:7: ^( TOKEN_REF ARG_ACTION )
                    {
                    dbg.location(187,7);
                    dbg.location(187,9);
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal935); 

                    match(input, Token.DOWN, null); 
                    dbg.location(187,19);
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal937); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:188:7: '.'
                    {
                    dbg.location(188,7);
                    match(input,92,FOLLOW_92_in_terminal946); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(189, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "terminal");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "terminal"


    // $ANTLR start "notTerminal"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:191:1: notTerminal : ( CHAR_LITERAL | TOKEN_REF | STRING_LITERAL );
    public final void notTerminal() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "notTerminal");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(191, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:192:2: ( CHAR_LITERAL | TOKEN_REF | STRING_LITERAL )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:
            {
            dbg.location(192,2);
            if ( (input.LA(1)>=TOKEN_REF && input.LA(1)<=CHAR_LITERAL) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(195, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "notTerminal");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "notTerminal"


    // $ANTLR start "ebnfSuffix"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:197:1: ebnfSuffix : ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE );
    public final void ebnfSuffix() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "ebnfSuffix");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(197, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:198:2: ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:
            {
            dbg.location(198,2);
            if ( (input.LA(1)>=OPTIONAL && input.LA(1)<=POSITIVE_CLOSURE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(201, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "ebnfSuffix");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "ebnfSuffix"


    // $ANTLR start "rewrite"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:205:1: rewrite : ( ( ^( '->' SEMPRED rewrite_alternative ) )* ^( '->' rewrite_alternative ) | );
    public final void rewrite() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(205, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:206:2: ( ( ^( '->' SEMPRED rewrite_alternative ) )* ^( '->' rewrite_alternative ) | )
            int alt37=2;
            try { dbg.enterDecision(37, decisionCanBacktrack[37]);

            int LA37_0 = input.LA(1);

            if ( (LA37_0==REWRITE) ) {
                alt37=1;
            }
            else if ( (LA37_0==ALT||LA37_0==EOB) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(37);}

            switch (alt37) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:206:4: ( ^( '->' SEMPRED rewrite_alternative ) )* ^( '->' rewrite_alternative )
                    {
                    dbg.location(206,4);
                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:206:4: ( ^( '->' SEMPRED rewrite_alternative ) )*
                    try { dbg.enterSubRule(36);

                    loop36:
                    do {
                        int alt36=2;
                        try { dbg.enterDecision(36, decisionCanBacktrack[36]);

                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==REWRITE) ) {
                            int LA36_1 = input.LA(2);

                            if ( (LA36_1==DOWN) ) {
                                int LA36_2 = input.LA(3);

                                if ( (LA36_2==SEMPRED) ) {
                                    alt36=1;
                                }


                            }


                        }


                        } finally {dbg.exitDecision(36);}

                        switch (alt36) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:206:5: ^( '->' SEMPRED rewrite_alternative )
                    	    {
                    	    dbg.location(206,5);
                    	    dbg.location(206,7);
                    	    match(input,REWRITE,FOLLOW_REWRITE_in_rewrite1012); 

                    	    match(input, Token.DOWN, null); 
                    	    dbg.location(206,12);
                    	    match(input,SEMPRED,FOLLOW_SEMPRED_in_rewrite1014); 
                    	    dbg.location(206,20);
                    	    pushFollow(FOLLOW_rewrite_alternative_in_rewrite1016);
                    	    rewrite_alternative();

                    	    state._fsp--;


                    	    match(input, Token.UP, null); 

                    	    }
                    	    break;

                    	default :
                    	    break loop36;
                        }
                    } while (true);
                    } finally {dbg.exitSubRule(36);}

                    dbg.location(206,43);
                    dbg.location(206,45);
                    match(input,REWRITE,FOLLOW_REWRITE_in_rewrite1022); 

                    match(input, Token.DOWN, null); 
                    dbg.location(206,50);
                    pushFollow(FOLLOW_rewrite_alternative_in_rewrite1024);
                    rewrite_alternative();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:208:2: 
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(208, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite"


    // $ANTLR start "rewrite_alternative"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:210:1: rewrite_alternative : ( rewrite_template | rewrite_tree_alternative | ^( ALT EPSILON EOA ) );
    public final void rewrite_alternative() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_alternative");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(210, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:211:2: ( rewrite_template | rewrite_tree_alternative | ^( ALT EPSILON EOA ) )
            int alt38=3;
            try { dbg.enterDecision(38, decisionCanBacktrack[38]);

            int LA38_0 = input.LA(1);

            if ( (LA38_0==TEMPLATE||LA38_0==ACTION) ) {
                alt38=1;
            }
            else if ( (LA38_0==ALT) ) {
                int LA38_2 = input.LA(2);

                if ( (LA38_2==DOWN) ) {
                    int LA38_3 = input.LA(3);

                    if ( (LA38_3==EPSILON) ) {
                        alt38=3;
                    }
                    else if ( ((LA38_3>=BLOCK && LA38_3<=POSITIVE_CLOSURE)||LA38_3==LABEL||LA38_3==TREE_BEGIN||(LA38_3>=TOKEN_REF && LA38_3<=ACTION)||LA38_3==RULE_REF) ) {
                        alt38=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 38, 3, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 38, 2, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(38);}

            switch (alt38) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:211:4: rewrite_template
                    {
                    dbg.location(211,4);
                    pushFollow(FOLLOW_rewrite_template_in_rewrite_alternative1039);
                    rewrite_template();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:212:4: rewrite_tree_alternative
                    {
                    dbg.location(212,4);
                    pushFollow(FOLLOW_rewrite_tree_alternative_in_rewrite_alternative1044);
                    rewrite_tree_alternative();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:213:9: ^( ALT EPSILON EOA )
                    {
                    dbg.location(213,9);
                    dbg.location(213,11);
                    match(input,ALT,FOLLOW_ALT_in_rewrite_alternative1055); 

                    match(input, Token.DOWN, null); 
                    dbg.location(213,15);
                    match(input,EPSILON,FOLLOW_EPSILON_in_rewrite_alternative1057); 
                    dbg.location(213,23);
                    match(input,EOA,FOLLOW_EOA_in_rewrite_alternative1059); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(214, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_alternative");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_alternative"


    // $ANTLR start "rewrite_tree_block"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:216:1: rewrite_tree_block : ^( BLOCK rewrite_tree_alternative EOB ) ;
    public final void rewrite_tree_block() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_tree_block");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(216, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:217:5: ( ^( BLOCK rewrite_tree_alternative EOB ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:217:9: ^( BLOCK rewrite_tree_alternative EOB )
            {
            dbg.location(217,9);
            dbg.location(217,11);
            match(input,BLOCK,FOLLOW_BLOCK_in_rewrite_tree_block1078); 

            match(input, Token.DOWN, null); 
            dbg.location(217,17);
            pushFollow(FOLLOW_rewrite_tree_alternative_in_rewrite_tree_block1080);
            rewrite_tree_alternative();

            state._fsp--;

            dbg.location(217,42);
            match(input,EOB,FOLLOW_EOB_in_rewrite_tree_block1082); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(218, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_tree_block");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_tree_block"


    // $ANTLR start "rewrite_tree_alternative"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:220:1: rewrite_tree_alternative : ^( ALT ( rewrite_tree_element )+ EOA ) ;
    public final void rewrite_tree_alternative() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_tree_alternative");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(220, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:221:5: ( ^( ALT ( rewrite_tree_element )+ EOA ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:221:7: ^( ALT ( rewrite_tree_element )+ EOA )
            {
            dbg.location(221,7);
            dbg.location(221,9);
            match(input,ALT,FOLLOW_ALT_in_rewrite_tree_alternative1101); 

            match(input, Token.DOWN, null); 
            dbg.location(221,13);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:221:13: ( rewrite_tree_element )+
            int cnt39=0;
            try { dbg.enterSubRule(39);

            loop39:
            do {
                int alt39=2;
                try { dbg.enterDecision(39, decisionCanBacktrack[39]);

                int LA39_0 = input.LA(1);

                if ( ((LA39_0>=BLOCK && LA39_0<=POSITIVE_CLOSURE)||LA39_0==LABEL||LA39_0==TREE_BEGIN||(LA39_0>=TOKEN_REF && LA39_0<=ACTION)||LA39_0==RULE_REF) ) {
                    alt39=1;
                }


                } finally {dbg.exitDecision(39);}

                switch (alt39) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:221:13: rewrite_tree_element
            	    {
            	    dbg.location(221,13);
            	    pushFollow(FOLLOW_rewrite_tree_element_in_rewrite_tree_alternative1103);
            	    rewrite_tree_element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt39 >= 1 ) break loop39;
                        EarlyExitException eee =
                            new EarlyExitException(39, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt39++;
            } while (true);
            } finally {dbg.exitSubRule(39);}

            dbg.location(221,35);
            match(input,EOA,FOLLOW_EOA_in_rewrite_tree_alternative1106); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(222, 5);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_tree_alternative");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_tree_alternative"


    // $ANTLR start "rewrite_tree_element"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:224:1: rewrite_tree_element : ( rewrite_tree_atom | rewrite_tree | rewrite_tree_block | rewrite_tree_ebnf );
    public final void rewrite_tree_element() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_tree_element");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(224, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:225:2: ( rewrite_tree_atom | rewrite_tree | rewrite_tree_block | rewrite_tree_ebnf )
            int alt40=4;
            try { dbg.enterDecision(40, decisionCanBacktrack[40]);

            switch ( input.LA(1) ) {
            case LABEL:
            case TOKEN_REF:
            case STRING_LITERAL:
            case CHAR_LITERAL:
            case ACTION:
            case RULE_REF:
                {
                alt40=1;
                }
                break;
            case TREE_BEGIN:
                {
                alt40=2;
                }
                break;
            case BLOCK:
                {
                alt40=3;
                }
                break;
            case OPTIONAL:
            case CLOSURE:
            case POSITIVE_CLOSURE:
                {
                alt40=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(40);}

            switch (alt40) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:225:4: rewrite_tree_atom
                    {
                    dbg.location(225,4);
                    pushFollow(FOLLOW_rewrite_tree_atom_in_rewrite_tree_element1121);
                    rewrite_tree_atom();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:226:4: rewrite_tree
                    {
                    dbg.location(226,4);
                    pushFollow(FOLLOW_rewrite_tree_in_rewrite_tree_element1126);
                    rewrite_tree();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:227:6: rewrite_tree_block
                    {
                    dbg.location(227,6);
                    pushFollow(FOLLOW_rewrite_tree_block_in_rewrite_tree_element1133);
                    rewrite_tree_block();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:228:6: rewrite_tree_ebnf
                    {
                    dbg.location(228,6);
                    pushFollow(FOLLOW_rewrite_tree_ebnf_in_rewrite_tree_element1140);
                    rewrite_tree_ebnf();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(229, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_tree_element");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_tree_element"


    // $ANTLR start "rewrite_tree_atom"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:231:1: rewrite_tree_atom : ( CHAR_LITERAL | TOKEN_REF | ^( TOKEN_REF ARG_ACTION ) | RULE_REF | STRING_LITERAL | LABEL | ACTION );
    public final void rewrite_tree_atom() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_tree_atom");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(231, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:232:5: ( CHAR_LITERAL | TOKEN_REF | ^( TOKEN_REF ARG_ACTION ) | RULE_REF | STRING_LITERAL | LABEL | ACTION )
            int alt41=7;
            try { dbg.enterDecision(41, decisionCanBacktrack[41]);

            switch ( input.LA(1) ) {
            case CHAR_LITERAL:
                {
                alt41=1;
                }
                break;
            case TOKEN_REF:
                {
                int LA41_2 = input.LA(2);

                if ( (LA41_2==DOWN) ) {
                    alt41=3;
                }
                else if ( (LA41_2==UP||(LA41_2>=BLOCK && LA41_2<=POSITIVE_CLOSURE)||LA41_2==EOA||LA41_2==LABEL||LA41_2==TREE_BEGIN||(LA41_2>=TOKEN_REF && LA41_2<=ACTION)||LA41_2==RULE_REF) ) {
                    alt41=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 2, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
                }
                break;
            case RULE_REF:
                {
                alt41=4;
                }
                break;
            case STRING_LITERAL:
                {
                alt41=5;
                }
                break;
            case LABEL:
                {
                alt41=6;
                }
                break;
            case ACTION:
                {
                alt41=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(41);}

            switch (alt41) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:232:9: CHAR_LITERAL
                    {
                    dbg.location(232,9);
                    match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_rewrite_tree_atom1156); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:233:6: TOKEN_REF
                    {
                    dbg.location(233,6);
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewrite_tree_atom1163); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:234:6: ^( TOKEN_REF ARG_ACTION )
                    {
                    dbg.location(234,6);
                    dbg.location(234,8);
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewrite_tree_atom1171); 

                    match(input, Token.DOWN, null); 
                    dbg.location(234,18);
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewrite_tree_atom1173); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:235:9: RULE_REF
                    {
                    dbg.location(235,9);
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_rewrite_tree_atom1185); 

                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:236:6: STRING_LITERAL
                    {
                    dbg.location(236,6);
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewrite_tree_atom1192); 

                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:237:6: LABEL
                    {
                    dbg.location(237,6);
                    match(input,LABEL,FOLLOW_LABEL_in_rewrite_tree_atom1199); 

                    }
                    break;
                case 7 :
                    dbg.enterAlt(7);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:238:4: ACTION
                    {
                    dbg.location(238,4);
                    match(input,ACTION,FOLLOW_ACTION_in_rewrite_tree_atom1204); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(239, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_tree_atom");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_tree_atom"


    // $ANTLR start "rewrite_tree_ebnf"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:241:1: rewrite_tree_ebnf : ^( ebnfSuffix rewrite_tree_block ) ;
    public final void rewrite_tree_ebnf() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_tree_ebnf");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(241, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:242:2: ( ^( ebnfSuffix rewrite_tree_block ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:242:4: ^( ebnfSuffix rewrite_tree_block )
            {
            dbg.location(242,4);
            dbg.location(242,6);
            pushFollow(FOLLOW_ebnfSuffix_in_rewrite_tree_ebnf1216);
            ebnfSuffix();

            state._fsp--;


            match(input, Token.DOWN, null); 
            dbg.location(242,17);
            pushFollow(FOLLOW_rewrite_tree_block_in_rewrite_tree_ebnf1218);
            rewrite_tree_block();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(243, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_tree_ebnf");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_tree_ebnf"


    // $ANTLR start "rewrite_tree"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:245:1: rewrite_tree : ^( TREE_BEGIN rewrite_tree_atom ( rewrite_tree_element )* ) ;
    public final void rewrite_tree() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_tree");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(245, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:246:2: ( ^( TREE_BEGIN rewrite_tree_atom ( rewrite_tree_element )* ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:246:4: ^( TREE_BEGIN rewrite_tree_atom ( rewrite_tree_element )* )
            {
            dbg.location(246,4);
            dbg.location(246,6);
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewrite_tree1232); 

            match(input, Token.DOWN, null); 
            dbg.location(246,17);
            pushFollow(FOLLOW_rewrite_tree_atom_in_rewrite_tree1234);
            rewrite_tree_atom();

            state._fsp--;

            dbg.location(246,35);
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:246:35: ( rewrite_tree_element )*
            try { dbg.enterSubRule(42);

            loop42:
            do {
                int alt42=2;
                try { dbg.enterDecision(42, decisionCanBacktrack[42]);

                int LA42_0 = input.LA(1);

                if ( ((LA42_0>=BLOCK && LA42_0<=POSITIVE_CLOSURE)||LA42_0==LABEL||LA42_0==TREE_BEGIN||(LA42_0>=TOKEN_REF && LA42_0<=ACTION)||LA42_0==RULE_REF) ) {
                    alt42=1;
                }


                } finally {dbg.exitDecision(42);}

                switch (alt42) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:246:35: rewrite_tree_element
            	    {
            	    dbg.location(246,35);
            	    pushFollow(FOLLOW_rewrite_tree_element_in_rewrite_tree1236);
            	    rewrite_tree_element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);
            } finally {dbg.exitSubRule(42);}


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(247, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_tree");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_tree"


    // $ANTLR start "rewrite_template"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:249:1: rewrite_template : ( ^( TEMPLATE ID rewrite_template_args ( DOUBLE_QUOTE_STRING_LITERAL | DOUBLE_ANGLE_STRING_LITERAL ) ) | rewrite_template_ref | rewrite_indirect_template_head | ACTION );
    public final void rewrite_template() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_template");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(249, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:250:2: ( ^( TEMPLATE ID rewrite_template_args ( DOUBLE_QUOTE_STRING_LITERAL | DOUBLE_ANGLE_STRING_LITERAL ) ) | rewrite_template_ref | rewrite_indirect_template_head | ACTION )
            int alt43=4;
            try { dbg.enterDecision(43, decisionCanBacktrack[43]);

            try {
                isCyclicDecision = true;
                alt43 = dfa43.predict(input);
            }
            catch (NoViableAltException nvae) {
                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(43);}

            switch (alt43) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:250:6: ^( TEMPLATE ID rewrite_template_args ( DOUBLE_QUOTE_STRING_LITERAL | DOUBLE_ANGLE_STRING_LITERAL ) )
                    {
                    dbg.location(250,6);
                    dbg.location(250,9);
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewrite_template1254); 

                    match(input, Token.DOWN, null); 
                    dbg.location(250,18);
                    match(input,ID,FOLLOW_ID_in_rewrite_template1256); 
                    dbg.location(250,21);
                    pushFollow(FOLLOW_rewrite_template_args_in_rewrite_template1258);
                    rewrite_template_args();

                    state._fsp--;

                    dbg.location(251,6);
                    if ( (input.LA(1)>=DOUBLE_QUOTE_STRING_LITERAL && input.LA(1)<=DOUBLE_ANGLE_STRING_LITERAL) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        dbg.recognitionException(mse);
                        throw mse;
                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:253:4: rewrite_template_ref
                    {
                    dbg.location(253,4);
                    pushFollow(FOLLOW_rewrite_template_ref_in_rewrite_template1281);
                    rewrite_template_ref();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:254:4: rewrite_indirect_template_head
                    {
                    dbg.location(254,4);
                    pushFollow(FOLLOW_rewrite_indirect_template_head_in_rewrite_template1286);
                    rewrite_indirect_template_head();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:255:4: ACTION
                    {
                    dbg.location(255,4);
                    match(input,ACTION,FOLLOW_ACTION_in_rewrite_template1291); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(256, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_template");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_template"


    // $ANTLR start "rewrite_template_ref"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:258:1: rewrite_template_ref : ^( TEMPLATE ID rewrite_template_args ) ;
    public final void rewrite_template_ref() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_template_ref");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(258, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:260:2: ( ^( TEMPLATE ID rewrite_template_args ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:260:4: ^( TEMPLATE ID rewrite_template_args )
            {
            dbg.location(260,4);
            dbg.location(260,6);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewrite_template_ref1305); 

            match(input, Token.DOWN, null); 
            dbg.location(260,15);
            match(input,ID,FOLLOW_ID_in_rewrite_template_ref1307); 
            dbg.location(260,18);
            pushFollow(FOLLOW_rewrite_template_args_in_rewrite_template_ref1309);
            rewrite_template_args();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(261, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_template_ref");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_template_ref"


    // $ANTLR start "rewrite_indirect_template_head"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:263:1: rewrite_indirect_template_head : ^( TEMPLATE ACTION rewrite_template_args ) ;
    public final void rewrite_indirect_template_head() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_indirect_template_head");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(263, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:265:2: ( ^( TEMPLATE ACTION rewrite_template_args ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:265:4: ^( TEMPLATE ACTION rewrite_template_args )
            {
            dbg.location(265,4);
            dbg.location(265,6);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewrite_indirect_template_head1324); 

            match(input, Token.DOWN, null); 
            dbg.location(265,15);
            match(input,ACTION,FOLLOW_ACTION_in_rewrite_indirect_template_head1326); 
            dbg.location(265,22);
            pushFollow(FOLLOW_rewrite_template_args_in_rewrite_indirect_template_head1328);
            rewrite_template_args();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(266, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_indirect_template_head");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_indirect_template_head"


    // $ANTLR start "rewrite_template_args"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:268:1: rewrite_template_args : ( ^( ARGLIST ( rewrite_template_arg )+ ) | ARGLIST );
    public final void rewrite_template_args() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_template_args");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(268, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:269:2: ( ^( ARGLIST ( rewrite_template_arg )+ ) | ARGLIST )
            int alt45=2;
            try { dbg.enterDecision(45, decisionCanBacktrack[45]);

            int LA45_0 = input.LA(1);

            if ( (LA45_0==ARGLIST) ) {
                int LA45_1 = input.LA(2);

                if ( (LA45_1==DOWN) ) {
                    alt45=1;
                }
                else if ( (LA45_1==UP||(LA45_1>=DOUBLE_QUOTE_STRING_LITERAL && LA45_1<=DOUBLE_ANGLE_STRING_LITERAL)) ) {
                    alt45=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 45, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(45);}

            switch (alt45) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:269:4: ^( ARGLIST ( rewrite_template_arg )+ )
                    {
                    dbg.location(269,4);
                    dbg.location(269,6);
                    match(input,ARGLIST,FOLLOW_ARGLIST_in_rewrite_template_args1341); 

                    match(input, Token.DOWN, null); 
                    dbg.location(269,14);
                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:269:14: ( rewrite_template_arg )+
                    int cnt44=0;
                    try { dbg.enterSubRule(44);

                    loop44:
                    do {
                        int alt44=2;
                        try { dbg.enterDecision(44, decisionCanBacktrack[44]);

                        int LA44_0 = input.LA(1);

                        if ( (LA44_0==ARG) ) {
                            alt44=1;
                        }


                        } finally {dbg.exitDecision(44);}

                        switch (alt44) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:269:14: rewrite_template_arg
                    	    {
                    	    dbg.location(269,14);
                    	    pushFollow(FOLLOW_rewrite_template_arg_in_rewrite_template_args1343);
                    	    rewrite_template_arg();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt44 >= 1 ) break loop44;
                                EarlyExitException eee =
                                    new EarlyExitException(44, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt44++;
                    } while (true);
                    } finally {dbg.exitSubRule(44);}


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:270:4: ARGLIST
                    {
                    dbg.location(270,4);
                    match(input,ARGLIST,FOLLOW_ARGLIST_in_rewrite_template_args1350); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(271, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_template_args");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_template_args"


    // $ANTLR start "rewrite_template_arg"
    // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:273:1: rewrite_template_arg : ^( ARG ID ACTION ) ;
    public final void rewrite_template_arg() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "rewrite_template_arg");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(273, 1);

        try {
            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:274:2: ( ^( ARG ID ACTION ) )
            dbg.enterAlt(1);

            // /Volumes/Mercurial/web-main/contrib/o.n.antlr.editor/src/org/netbeans/modules/antlr/editor/ANTLRv3Tree.g:274:6: ^( ARG ID ACTION )
            {
            dbg.location(274,6);
            dbg.location(274,8);
            match(input,ARG,FOLLOW_ARG_in_rewrite_template_arg1364); 

            match(input, Token.DOWN, null); 
            dbg.location(274,12);
            match(input,ID,FOLLOW_ID_in_rewrite_template_arg1366); 
            dbg.location(274,15);
            match(input,ACTION,FOLLOW_ACTION_in_rewrite_template_arg1368); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(275, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "rewrite_template_arg");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "rewrite_template_arg"

    // Delegated rules


    protected DFA30 dfa30 = new DFA30(this);
    protected DFA43 dfa43 = new DFA43(this);
    static final String DFA30_eotS =
        "\14\uffff";
    static final String DFA30_eofS =
        "\14\uffff";
    static final String DFA30_minS =
        "\1\10\1\2\6\uffff\1\24\1\10\2\uffff";
    static final String DFA30_maxS =
        "\1\134\1\2\6\uffff\1\24\1\134\2\uffff";
    static final String DFA30_acceptS =
        "\2\uffff\1\3\1\4\1\5\1\6\1\7\1\10\2\uffff\1\1\1\2";
    static final String DFA30_specialS =
        "\14\uffff}>";
    static final String[] DFA30_transitionS = {
            "\5\3\1\uffff\1\2\21\uffff\1\5\1\6\1\3\2\uffff\1\7\2\2\2\uffff"+
            "\3\2\1\4\3\uffff\1\2\25\uffff\1\1\17\uffff\1\1\1\uffff\1\2\2"+
            "\uffff\1\2",
            "\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\11",
            "\1\12\5\uffff\1\13\27\uffff\2\13\2\uffff\3\13\4\uffff\1\13"+
            "\47\uffff\1\13\2\uffff\1\13",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "143:1: elementNoOptionSpec : ( ^( ( '=' | '+=' ) ID block ) | ^( ( '=' | '+=' ) ID atom ) | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
    }
    static final String DFA43_eotS =
        "\20\uffff";
    static final String DFA43_eofS =
        "\20\uffff";
    static final String DFA43_minS =
        "\1\36\1\2\1\uffff\1\24\1\26\1\uffff\1\2\1\25\2\uffff\1\2\1\24\1"+
        "\55\3\3";
    static final String DFA43_maxS =
        "\1\55\1\2\1\uffff\1\55\1\26\1\uffff\1\63\1\25\2\uffff\1\2\1\24\1"+
        "\55\1\3\1\25\1\63";
    static final String DFA43_acceptS =
        "\2\uffff\1\4\2\uffff\1\3\2\uffff\1\1\1\2\6\uffff";
    static final String DFA43_specialS =
        "\20\uffff}>";
    static final String[] DFA43_transitionS = {
            "\1\1\16\uffff\1\2",
            "\1\3",
            "",
            "\1\4\30\uffff\1\5",
            "\1\6",
            "",
            "\1\7\1\11\56\uffff\2\10",
            "\1\12",
            "",
            "",
            "\1\13",
            "\1\14",
            "\1\15",
            "\1\16",
            "\1\17\21\uffff\1\12",
            "\1\11\56\uffff\2\10"
    };

    static final short[] DFA43_eot = DFA.unpackEncodedString(DFA43_eotS);
    static final short[] DFA43_eof = DFA.unpackEncodedString(DFA43_eofS);
    static final char[] DFA43_min = DFA.unpackEncodedStringToUnsignedChars(DFA43_minS);
    static final char[] DFA43_max = DFA.unpackEncodedStringToUnsignedChars(DFA43_maxS);
    static final short[] DFA43_accept = DFA.unpackEncodedString(DFA43_acceptS);
    static final short[] DFA43_special = DFA.unpackEncodedString(DFA43_specialS);
    static final short[][] DFA43_transition;

    static {
        int numStates = DFA43_transitionS.length;
        DFA43_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA43_transition[i] = DFA.unpackEncodedString(DFA43_transitionS[i]);
        }
    }

    class DFA43 extends DFA {

        public DFA43(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 43;
            this.eot = DFA43_eot;
            this.eof = DFA43_eof;
            this.min = DFA43_min;
            this.max = DFA43_max;
            this.accept = DFA43_accept;
            this.special = DFA43_special;
            this.transition = DFA43_transition;
        }
        public String getDescription() {
            return "249:1: rewrite_template : ( ^( TEMPLATE ID rewrite_template_args ( DOUBLE_QUOTE_STRING_LITERAL | DOUBLE_ANGLE_STRING_LITERAL ) ) | rewrite_template_ref | rewrite_indirect_template_head | ACTION );";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
    }
 

    public static final BitSet FOLLOW_grammarType_in_grammarDef52 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarDef54 = new BitSet(new long[]{0x0000420080000090L,0x0000000000000100L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarDef56 = new BitSet(new long[]{0x0000420080000090L,0x0000000000000100L});
    public static final BitSet FOLLOW_optionsSpec_in_grammarDef59 = new BitSet(new long[]{0x0000420080000090L,0x0000000000000100L});
    public static final BitSet FOLLOW_tokensSpec_in_grammarDef62 = new BitSet(new long[]{0x0000420080000090L,0x0000000000000100L});
    public static final BitSet FOLLOW_attrScope_in_grammarDef65 = new BitSet(new long[]{0x0000420080000090L,0x0000000000000100L});
    public static final BitSet FOLLOW_action_in_grammarDef68 = new BitSet(new long[]{0x0000420080000090L,0x0000000000000100L});
    public static final BitSet FOLLOW_rule_in_grammarDef71 = new BitSet(new long[]{0x0000420080000098L,0x0000000000000100L});
    public static final BitSet FOLLOW_set_in_grammarType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec127 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec129 = new BitSet(new long[]{0x0000040000000008L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_tokenSpec143 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenSpec145 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec147 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_71_in_tokenSpec154 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenSpec156 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_tokenSpec158 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenSpec164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope176 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_attrScope178 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope180 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_72_in_action193 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action195 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_ID_in_action197 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_action199 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_72_in_action206 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action208 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_action210 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec223 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec225 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_option244 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option246 = new BitSet(new long[]{0x0000980000100000L});
    public static final BitSet FOLLOW_optionValue_in_option248 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule314 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule316 = new BitSet(new long[]{0x0000401080A00100L,0x0000000000003900L});
    public static final BitSet FOLLOW_modifier_in_rule318 = new BitSet(new long[]{0x0000401080A00100L,0x0000000000003900L});
    public static final BitSet FOLLOW_ARG_in_rule323 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule325 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RET_in_rule332 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule334 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_optionsSpec_in_rule347 = new BitSet(new long[]{0x0000401080A00100L,0x0000000000003900L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rule350 = new BitSet(new long[]{0x0000401080A00100L,0x0000000000003900L});
    public static final BitSet FOLLOW_ruleAction_in_rule353 = new BitSet(new long[]{0x0000401080A00100L,0x0000000000003900L});
    public static final BitSet FOLLOW_altList_in_rule364 = new BitSet(new long[]{0x0000000000020000L,0x0000000000600000L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule374 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_EOR_in_rule377 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_modifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleAction416 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleAction418 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction420 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_80_in_throwsSpec433 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_throwsSpec435 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec449 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec451 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec458 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec460 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec462 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec470 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec472 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_BLOCK_in_block492 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_optionsSpec_in_block494 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_alternative_in_block498 = new BitSet(new long[]{0x0000010000050000L});
    public static final BitSet FOLLOW_rewrite_in_block500 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_EOB_in_block504 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_altList527 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_altList530 = new BitSet(new long[]{0x0000010000050000L});
    public static final BitSet FOLLOW_rewrite_in_altList532 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_EOB_in_altList536 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative558 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative560 = new BitSet(new long[]{0x00023CE700085F00L,0x0000000012800080L});
    public static final BitSet FOLLOW_EOA_in_alternative563 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative575 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative577 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_EOA_in_alternative579 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup594 = new BitSet(new long[]{0x0000000000000002L,0x0000000000600000L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_exceptionHandler624 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler626 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler628 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_86_in_finallyClause650 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause652 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_elementNoOptionSpec_in_element667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_elementNoOptionSpec679 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementNoOptionSpec685 = new BitSet(new long[]{0x0000000400001F00L});
    public static final BitSet FOLLOW_block_in_elementNoOptionSpec687 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_elementNoOptionSpec694 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementNoOptionSpec700 = new BitSet(new long[]{0x00021CC000004000L,0x0000000012000000L});
    public static final BitSet FOLLOW_atom_in_elementNoOptionSpec702 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_atom_in_elementNoOptionSpec708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_elementNoOptionSpec713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_elementNoOptionSpec720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_elementNoOptionSpec727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_elementNoOptionSpec732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_elementNoOptionSpec739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_atom751 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_atom_in_atom757 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_range_in_atom763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notSet_in_atom768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_atom777 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_atom779 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_atom788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_89_in_notSet813 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notTerminal_in_notSet815 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_89_in_notSet822 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet824 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec837 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec839 = new BitSet(new long[]{0x00023CE700085F08L,0x0000000012800080L});
    public static final BitSet FOLLOW_SYNPRED_in_ebnf855 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf857 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SYN_SEMPRED_in_ebnf863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_ebnf869 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf871 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_RANGE_in_range889 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_range891 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_range893 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_terminal910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal935 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal937 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_92_in_terminal946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_notTerminal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ebnfSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REWRITE_in_rewrite1012 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SEMPRED_in_rewrite1014 = new BitSet(new long[]{0x0000200040010000L});
    public static final BitSet FOLLOW_rewrite_alternative_in_rewrite1016 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_REWRITE_in_rewrite1022 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewrite_alternative_in_rewrite1024 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewrite_template_in_rewrite_alternative1039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_tree_alternative_in_rewrite_alternative1044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_rewrite_alternative1055 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_rewrite_alternative1057 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_EOA_in_rewrite_alternative1059 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_rewrite_tree_block1078 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewrite_tree_alternative_in_rewrite_tree_block1080 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_EOB_in_rewrite_tree_block1082 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_rewrite_tree_alternative1101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewrite_tree_element_in_rewrite_tree_alternative1103 = new BitSet(new long[]{0x00023C2020080F00L});
    public static final BitSet FOLLOW_EOA_in_rewrite_tree_alternative1106 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewrite_tree_atom_in_rewrite_tree_element1121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_tree_in_rewrite_tree_element1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_tree_block_in_rewrite_tree_element1133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_tree_ebnf_in_rewrite_tree_element1140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_rewrite_tree_atom1156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewrite_tree_atom1163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewrite_tree_atom1171 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewrite_tree_atom1173 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_rewrite_tree_atom1185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewrite_tree_atom1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LABEL_in_rewrite_tree_atom1199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewrite_tree_atom1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewrite_tree_ebnf1216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewrite_tree_block_in_rewrite_tree_ebnf1218 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewrite_tree1232 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewrite_tree_atom_in_rewrite_tree1234 = new BitSet(new long[]{0x00023C2020080F08L});
    public static final BitSet FOLLOW_rewrite_tree_element_in_rewrite_tree1236 = new BitSet(new long[]{0x00023C2020080F08L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewrite_template1254 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewrite_template1256 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rewrite_template_args_in_rewrite_template1258 = new BitSet(new long[]{0x000C000000000000L});
    public static final BitSet FOLLOW_set_in_rewrite_template1265 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewrite_template_ref_in_rewrite_template1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_indirect_template_head_in_rewrite_template1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewrite_template1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewrite_template_ref1305 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewrite_template_ref1307 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rewrite_template_args_in_rewrite_template_ref1309 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewrite_indirect_template_head1324 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_rewrite_indirect_template_head1326 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_rewrite_template_args_in_rewrite_indirect_template_head1328 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARGLIST_in_rewrite_template_args1341 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewrite_template_arg_in_rewrite_template_args1343 = new BitSet(new long[]{0x0000000000200008L});
    public static final BitSet FOLLOW_ARGLIST_in_rewrite_template_args1350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_rewrite_template_arg1364 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewrite_template_arg1366 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_rewrite_template_arg1368 = new BitSet(new long[]{0x0000000000000008L});

}
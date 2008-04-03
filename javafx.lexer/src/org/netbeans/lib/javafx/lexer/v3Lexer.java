// $ANTLR 3.1b1 E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g 2008-04-02 14:26:40

package org.netbeans.lib.javafx.lexer;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import org.antlr.runtime.*;

public class v3Lexer extends Lexer {
    public static final int COMMA=80;
    public static final int LAZY=66;
    public static final int EXPR_LIST=118;
    public static final int SEQ_INDEX=123;
    public static final int AS=51;
    public static final int HexDigit=157;
    public static final int SEQ_SLICE_EXCLUSIVE=125;
    public static final int INTO=63;
    public static final int TranslationKeyBody=150;
    public static final int FALSE=14;
    public static final int ABSTRACT=5;
    public static final int THEN=72;
    public static final int STEP=71;
    public static final int PLUSPLUS=45;
    public static final int IMPORT=18;
    public static final int PACKAGE=26;
    public static final int PIPE=47;
    public static final int SIZEOF=34;
    public static final int ON=67;
    public static final int CONTINUE=12;
    public static final int DOT=81;
    public static final int SingleQuoteBody=141;
    public static final int PRIVATE=28;
    public static final int Letter=161;
    public static final int AND=50;
    public static final int EXPRESSION=109;
    public static final int TYPED_ARG_LIST=136;
    public static final int FUNCTION=16;
    public static final int STRING_LITERAL=142;
    public static final int RBRACKET=78;
    public static final int MODULE=103;
    public static final int RPAREN=77;
    public static final int SEMI_INSERT_START=4;
    public static final int ASSERT=6;
    public static final int RBRACE_LBRACE_STRING_LITERAL=147;
    public static final int PLUS=89;
    public static final int OBJECT_LIT=126;
    public static final int FINALLY=57;
    public static final int ON_REPLACE=114;
    public static final int EXTENDS=56;
    public static final int TIME_LITERAL=155;
    public static final int SUPER=33;
    public static final int DECIMAL_LITERAL=152;
    public static final int SLICE_CLAUSE=112;
    public static final int WS=164;
    public static final int SUCHTHAT_BLOCK=138;
    public static final int NEW=22;
    public static final int SUBSUB=46;
    public static final int EQ=83;
    public static final int NAMED_TWEEN=139;
    public static final int FUNC_EXPR=107;
    public static final int EXCLUSIVE=55;
    public static final int LT=85;
    public static final int BOUND=9;
    public static final int LINE_COMMENT=166;
    public static final int RangeDots=159;
    public static final int NEGATIVE=120;
    public static final int EQEQ=82;
    public static final int QUOTE_LBRACE_STRING_LITERAL=144;
    public static final int FLOATING_POINT_LITERAL=160;
    public static final int TYPE_ANY=133;
    public static final int STATIC=35;
    public static final int CATCH=53;
    public static final int SEMI=79;
    public static final int ELSE=54;
    public static final int INDEXOF=61;
    public static final int FORMAT_STRING_LITERAL=149;
    public static final int LTEQ=87;
    public static final int BREAK=10;
    public static final int FIRST=58;
    public static final int NULL=24;
    public static final int QUES=100;
    public static final int COLON=99;
    public static final int DOTDOT=76;
    public static final int IDENTIFIER=163;
    public static final int NextIsPercent=143;
    public static final int TYPE_UNKNOWN=134;
    public static final int INSERT=20;
    public static final int TRUE=39;
    public static final int DOC_COMMENT=137;
    public static final int POUND=42;
    public static final int THROW=37;
    public static final int POSTINIT=27;
    public static final int WHERE=75;
    public static final int POSTINCR=121;
    public static final int OBJECT_LIT_PART=127;
    public static final int PUBLIC=30;
    public static final int LTGT=86;
    public static final int STATEMENT=108;
    public static final int TYPEOF=73;
    public static final int PERCENT=93;
    public static final int LAST=65;
    public static final int ON_INSERT_ELEMENT=116;
    public static final int SEQ_EMPTY=128;
    public static final int READONLY=31;
    public static final int LBRACKET=44;
    public static final int INIT=19;
    public static final int OCTAL_LITERAL=156;
    public static final int SEQ_SLICE=124;
    public static final int FUNC_APPLY=119;
    public static final int HEX_LITERAL=158;
    public static final int OR=68;
    public static final int AFTER=49;
    public static final int LBRACE=145;
    public static final int BLOCK=110;
    public static final int RBRACE=148;
    public static final int PROTECTED=29;
    public static final int EMPTY_FORMAT_STRING=130;
    public static final int INVERSE=64;
    public static final int SUBEQ=95;
    public static final int TYPE_NAMED=131;
    public static final int INSTANCEOF=62;
    public static final int POSTDECR=122;
    public static final int TRANSLATION_KEY=151;
    public static final int PARAM=106;
    public static final int ON_REPLACE_SLICE=113;
    public static final int LPAREN=43;
    public static final int SLASHEQ=97;
    public static final int DoubleQuoteBody=140;
    public static final int FROM=59;
    public static final int DELETE=13;
    public static final int PERCENTEQ=98;
    public static final int Exponent=154;
    public static final int SLASH=92;
    public static final int WHILE=41;
    public static final int STAREQ=96;
    public static final int ON_DELETE_ELEMENT=117;
    public static final int CLASS_MEMBERS=105;
    public static final int PLUSEQ=94;
    public static final int REPLACE=69;
    public static final int GT=84;
    public static final int COMMENT=165;
    public static final int ON_REPLACE_ELEMENT=115;
    public static final int OVERRIDE=25;
    public static final int GTEQ=88;
    public static final int THIS=36;
    public static final int SEQ_EXPLICIT=129;
    public static final int WITH=74;
    public static final int REVERSE=70;
    public static final int IN=60;
    public static final int VAR=40;
    public static final int JavaIDDigit=162;
    public static final int CLASS=11;
    public static final int TWEEN=101;
    public static final int RETURN=32;
    public static final int IF=17;
    public static final int LET=21;
    public static final int SEMI_INSERT_END=48;
    public static final int SUCHTHAT=102;
    public static final int EOF=-1;
    public static final int TYPE_FUNCTION=132;
    public static final int FOR=15;
    public static final int LAST_TOKEN=167;
    public static final int BEFORE=52;
    public static final int STAR=91;
    public static final int MISSING_NAME=111;
    public static final int ATTRIBUTE=7;
    public static final int SUB=90;
    public static final int BIND=8;
    public static final int MODIFIER=104;
    public static final int NOT=23;
    public static final int TRY=38;
    public static final int TYPE_ARG=135;
    public static final int Digits=153;
    public static final int RBRACE_QUOTE_STRING_LITERAL=146;
    
        /** The log to be used for error diagnostics.
         */
        private Log log;
        
        static final byte NO_INSERT_SEMI = 0; // default
        static final byte INSERT_SEMI = 1; 
        static final byte IGNORE_FOR_SEMI = 2; 
        static final byte[] semiKind = new byte[LAST_TOKEN];
        { 
          for (int i = SEMI_INSERT_START; i < SEMI_INSERT_END; ++i) {
              semiKind[i] = INSERT_SEMI;
          }
          semiKind[RBRACE] = INSERT_SEMI;
          semiKind[STRING_LITERAL] = INSERT_SEMI;
          semiKind[QUOTE_LBRACE_STRING_LITERAL] = INSERT_SEMI;
          semiKind[DECIMAL_LITERAL] = INSERT_SEMI;
          semiKind[OCTAL_LITERAL] = INSERT_SEMI;
          semiKind[HEX_LITERAL] = INSERT_SEMI;
          semiKind[TIME_LITERAL] = INSERT_SEMI;	
          semiKind[FLOATING_POINT_LITERAL] = INSERT_SEMI;
          semiKind[IDENTIFIER] = INSERT_SEMI;
          
          semiKind[WS] = IGNORE_FOR_SEMI;
          semiKind[COMMENT] = IGNORE_FOR_SEMI;
          semiKind[LINE_COMMENT] = IGNORE_FOR_SEMI;
        }
          
        int previousTokenType = -1;
    
        
        public v3Lexer(Context context, CharStream input) {
        	this(input);
            this.log = Log.instance(context);
        }
           
        // quote context --
        static final int CUR_QUOTE_CTX	= 0;	// 0 = use current quote context
        static final int SNG_QUOTE_CTX	= 1;	// 1 = single quote quote context
        static final int DBL_QUOTE_CTX	= 2;	// 2 = double quote quote context
     

    // delegates
    // delegators

    public v3Lexer() {;} 
    public v3Lexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public v3Lexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g"; }

    // $ANTLR start ABSTRACT
    public final void mABSTRACT() throws RecognitionException {
        try {
            int _type = ABSTRACT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:51:10: ( 'abstract' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:51:12: 'abstract'
            {
            match("abstract"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ABSTRACT

    // $ANTLR start ASSERT
    public final void mASSERT() throws RecognitionException {
        try {
            int _type = ASSERT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:52:8: ( 'assert' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:52:10: 'assert'
            {
            match("assert"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ASSERT

    // $ANTLR start ATTRIBUTE
    public final void mATTRIBUTE() throws RecognitionException {
        try {
            int _type = ATTRIBUTE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:53:11: ( 'attribute' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:53:13: 'attribute'
            {
            match("attribute"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ATTRIBUTE

    // $ANTLR start BIND
    public final void mBIND() throws RecognitionException {
        try {
            int _type = BIND;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:54:6: ( 'bind' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:54:8: 'bind'
            {
            match("bind"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BIND

    // $ANTLR start BOUND
    public final void mBOUND() throws RecognitionException {
        try {
            int _type = BOUND;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:55:7: ( 'bound' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:55:9: 'bound'
            {
            match("bound"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BOUND

    // $ANTLR start BREAK
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:56:7: ( 'break' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:56:9: 'break'
            {
            match("break"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BREAK

    // $ANTLR start CLASS
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:57:7: ( 'class' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:57:9: 'class'
            {
            match("class"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CLASS

    // $ANTLR start CONTINUE
    public final void mCONTINUE() throws RecognitionException {
        try {
            int _type = CONTINUE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:58:10: ( 'continue' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:58:12: 'continue'
            {
            match("continue"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CONTINUE

    // $ANTLR start DELETE
    public final void mDELETE() throws RecognitionException {
        try {
            int _type = DELETE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:59:8: ( 'delete' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:59:10: 'delete'
            {
            match("delete"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DELETE

    // $ANTLR start FALSE
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:60:7: ( 'false' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:60:9: 'false'
            {
            match("false"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FALSE

    // $ANTLR start FOR
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:61:5: ( 'for' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:61:7: 'for'
            {
            match("for"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FOR

    // $ANTLR start FUNCTION
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:62:10: ( 'function' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:62:12: 'function'
            {
            match("function"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FUNCTION

    // $ANTLR start IF
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:63:4: ( 'if' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:63:6: 'if'
            {
            match("if"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IF

    // $ANTLR start IMPORT
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:64:8: ( 'import' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:64:10: 'import'
            {
            match("import"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IMPORT

    // $ANTLR start INIT
    public final void mINIT() throws RecognitionException {
        try {
            int _type = INIT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:65:6: ( 'init' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:65:8: 'init'
            {
            match("init"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INIT

    // $ANTLR start INSERT
    public final void mINSERT() throws RecognitionException {
        try {
            int _type = INSERT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:66:8: ( 'insert' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:66:10: 'insert'
            {
            match("insert"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INSERT

    // $ANTLR start LET
    public final void mLET() throws RecognitionException {
        try {
            int _type = LET;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:67:5: ( 'let' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:67:7: 'let'
            {
            match("let"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LET

    // $ANTLR start NEW
    public final void mNEW() throws RecognitionException {
        try {
            int _type = NEW;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:68:5: ( 'new' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:68:7: 'new'
            {
            match("new"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NEW

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:69:5: ( 'not' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:69:7: 'not'
            {
            match("not"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start NULL
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:70:6: ( 'null' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:70:8: 'null'
            {
            match("null"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULL

    // $ANTLR start OVERRIDE
    public final void mOVERRIDE() throws RecognitionException {
        try {
            int _type = OVERRIDE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:71:10: ( 'override' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:71:12: 'override'
            {
            match("override"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OVERRIDE

    // $ANTLR start PACKAGE
    public final void mPACKAGE() throws RecognitionException {
        try {
            int _type = PACKAGE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:72:9: ( 'package' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:72:11: 'package'
            {
            match("package"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PACKAGE

    // $ANTLR start POSTINIT
    public final void mPOSTINIT() throws RecognitionException {
        try {
            int _type = POSTINIT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:73:10: ( 'postinit' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:73:12: 'postinit'
            {
            match("postinit"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end POSTINIT

    // $ANTLR start PRIVATE
    public final void mPRIVATE() throws RecognitionException {
        try {
            int _type = PRIVATE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:74:9: ( 'private' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:74:11: 'private'
            {
            match("private"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PRIVATE

    // $ANTLR start PROTECTED
    public final void mPROTECTED() throws RecognitionException {
        try {
            int _type = PROTECTED;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:75:11: ( 'protected' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:75:13: 'protected'
            {
            match("protected"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PROTECTED

    // $ANTLR start PUBLIC
    public final void mPUBLIC() throws RecognitionException {
        try {
            int _type = PUBLIC;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:76:8: ( 'public' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:76:10: 'public'
            {
            match("public"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PUBLIC

    // $ANTLR start READONLY
    public final void mREADONLY() throws RecognitionException {
        try {
            int _type = READONLY;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:77:10: ( 'readonly' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:77:12: 'readonly'
            {
            match("readonly"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end READONLY

    // $ANTLR start RETURN
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:78:8: ( 'return' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:78:10: 'return'
            {
            match("return"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RETURN

    // $ANTLR start SUPER
    public final void mSUPER() throws RecognitionException {
        try {
            int _type = SUPER;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:79:7: ( 'super' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:79:9: 'super'
            {
            match("super"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUPER

    // $ANTLR start SIZEOF
    public final void mSIZEOF() throws RecognitionException {
        try {
            int _type = SIZEOF;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:80:8: ( 'sizeof' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:80:10: 'sizeof'
            {
            match("sizeof"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SIZEOF

    // $ANTLR start STATIC
    public final void mSTATIC() throws RecognitionException {
        try {
            int _type = STATIC;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:81:8: ( 'static' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:81:10: 'static'
            {
            match("static"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STATIC

    // $ANTLR start THIS
    public final void mTHIS() throws RecognitionException {
        try {
            int _type = THIS;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:82:6: ( 'this' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:82:8: 'this'
            {
            match("this"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THIS

    // $ANTLR start THROW
    public final void mTHROW() throws RecognitionException {
        try {
            int _type = THROW;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:83:7: ( 'throw' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:83:9: 'throw'
            {
            match("throw"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THROW

    // $ANTLR start TRY
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:84:5: ( 'try' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:84:7: 'try'
            {
            match("try"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRY

    // $ANTLR start TRUE
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:85:6: ( 'true' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:85:8: 'true'
            {
            match("true"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRUE

    // $ANTLR start VAR
    public final void mVAR() throws RecognitionException {
        try {
            int _type = VAR;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:86:5: ( 'var' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:86:7: 'var'
            {
            match("var"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end VAR

    // $ANTLR start WHILE
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:87:7: ( 'while' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:87:9: 'while'
            {
            match("while"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHILE

    // $ANTLR start POUND
    public final void mPOUND() throws RecognitionException {
        try {
            int _type = POUND;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:88:7: ( '#' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:88:9: '#'
            {
            match('#'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end POUND

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:89:8: ( '(' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:89:10: '('
            {
            match('('); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LPAREN

    // $ANTLR start LBRACKET
    public final void mLBRACKET() throws RecognitionException {
        try {
            int _type = LBRACKET;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:90:10: ( '[' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:90:12: '['
            {
            match('['); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LBRACKET

    // $ANTLR start PLUSPLUS
    public final void mPLUSPLUS() throws RecognitionException {
        try {
            int _type = PLUSPLUS;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:91:10: ( '++' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:91:12: '++'
            {
            match("++"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUSPLUS

    // $ANTLR start SUBSUB
    public final void mSUBSUB() throws RecognitionException {
        try {
            int _type = SUBSUB;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:92:8: ( '--' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:92:10: '--'
            {
            match("--"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUBSUB

    // $ANTLR start PIPE
    public final void mPIPE() throws RecognitionException {
        try {
            int _type = PIPE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:93:6: ( '|' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:93:8: '|'
            {
            match('|'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PIPE

    // $ANTLR start AFTER
    public final void mAFTER() throws RecognitionException {
        try {
            int _type = AFTER;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:94:7: ( 'after' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:94:9: 'after'
            {
            match("after"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AFTER

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:95:5: ( 'and' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:95:7: 'and'
            {
            match("and"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start AS
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:96:4: ( 'as' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:96:6: 'as'
            {
            match("as"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AS

    // $ANTLR start BEFORE
    public final void mBEFORE() throws RecognitionException {
        try {
            int _type = BEFORE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:97:8: ( 'before' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:97:10: 'before'
            {
            match("before"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BEFORE

    // $ANTLR start CATCH
    public final void mCATCH() throws RecognitionException {
        try {
            int _type = CATCH;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:98:7: ( 'catch' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:98:9: 'catch'
            {
            match("catch"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CATCH

    // $ANTLR start ELSE
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:99:6: ( 'else' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:99:8: 'else'
            {
            match("else"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELSE

    // $ANTLR start EXCLUSIVE
    public final void mEXCLUSIVE() throws RecognitionException {
        try {
            int _type = EXCLUSIVE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:100:11: ( 'exclusive' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:100:13: 'exclusive'
            {
            match("exclusive"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXCLUSIVE

    // $ANTLR start EXTENDS
    public final void mEXTENDS() throws RecognitionException {
        try {
            int _type = EXTENDS;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:101:9: ( 'extends' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:101:11: 'extends'
            {
            match("extends"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXTENDS

    // $ANTLR start FINALLY
    public final void mFINALLY() throws RecognitionException {
        try {
            int _type = FINALLY;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:102:9: ( 'finally' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:102:11: 'finally'
            {
            match("finally"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FINALLY

    // $ANTLR start FIRST
    public final void mFIRST() throws RecognitionException {
        try {
            int _type = FIRST;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:103:7: ( 'first' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:103:9: 'first'
            {
            match("first"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FIRST

    // $ANTLR start FROM
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:104:6: ( 'from' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:104:8: 'from'
            {
            match("from"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FROM

    // $ANTLR start IN
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:105:4: ( 'in' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:105:6: 'in'
            {
            match("in"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IN

    // $ANTLR start INDEXOF
    public final void mINDEXOF() throws RecognitionException {
        try {
            int _type = INDEXOF;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:106:9: ( 'indexof' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:106:11: 'indexof'
            {
            match("indexof"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INDEXOF

    // $ANTLR start INSTANCEOF
    public final void mINSTANCEOF() throws RecognitionException {
        try {
            int _type = INSTANCEOF;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:107:12: ( 'instanceof' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:107:14: 'instanceof'
            {
            match("instanceof"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INSTANCEOF

    // $ANTLR start INTO
    public final void mINTO() throws RecognitionException {
        try {
            int _type = INTO;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:108:6: ( 'into' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:108:8: 'into'
            {
            match("into"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INTO

    // $ANTLR start INVERSE
    public final void mINVERSE() throws RecognitionException {
        try {
            int _type = INVERSE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:109:9: ( 'inverse' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:109:11: 'inverse'
            {
            match("inverse"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INVERSE

    // $ANTLR start LAST
    public final void mLAST() throws RecognitionException {
        try {
            int _type = LAST;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:110:6: ( 'last' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:110:8: 'last'
            {
            match("last"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LAST

    // $ANTLR start LAZY
    public final void mLAZY() throws RecognitionException {
        try {
            int _type = LAZY;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:111:6: ( 'lazy' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:111:8: 'lazy'
            {
            match("lazy"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LAZY

    // $ANTLR start ON
    public final void mON() throws RecognitionException {
        try {
            int _type = ON;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:112:4: ( 'on' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:112:6: 'on'
            {
            match("on"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ON

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:113:4: ( 'or' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:113:6: 'or'
            {
            match("or"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start REPLACE
    public final void mREPLACE() throws RecognitionException {
        try {
            int _type = REPLACE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:114:9: ( 'replace' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:114:11: 'replace'
            {
            match("replace"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end REPLACE

    // $ANTLR start REVERSE
    public final void mREVERSE() throws RecognitionException {
        try {
            int _type = REVERSE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:115:9: ( 'reverse' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:115:11: 'reverse'
            {
            match("reverse"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end REVERSE

    // $ANTLR start STEP
    public final void mSTEP() throws RecognitionException {
        try {
            int _type = STEP;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:116:6: ( 'step' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:116:8: 'step'
            {
            match("step"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STEP

    // $ANTLR start THEN
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:117:6: ( 'then' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:117:8: 'then'
            {
            match("then"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THEN

    // $ANTLR start TYPEOF
    public final void mTYPEOF() throws RecognitionException {
        try {
            int _type = TYPEOF;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:118:8: ( 'typeof' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:118:10: 'typeof'
            {
            match("typeof"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TYPEOF

    // $ANTLR start WITH
    public final void mWITH() throws RecognitionException {
        try {
            int _type = WITH;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:119:6: ( 'with' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:119:8: 'with'
            {
            match("with"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WITH

    // $ANTLR start WHERE
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:120:7: ( 'where' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:120:9: 'where'
            {
            match("where"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHERE

    // $ANTLR start DOTDOT
    public final void mDOTDOT() throws RecognitionException {
        try {
            int _type = DOTDOT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:121:8: ( '..' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:121:10: '..'
            {
            match(".."); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOTDOT

    // $ANTLR start RPAREN
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:122:8: ( ')' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:122:10: ')'
            {
            match(')'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RPAREN

    // $ANTLR start RBRACKET
    public final void mRBRACKET() throws RecognitionException {
        try {
            int _type = RBRACKET;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:123:10: ( ']' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:123:12: ']'
            {
            match(']'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RBRACKET

    // $ANTLR start SEMI
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:124:6: ( ';' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:124:8: ';'
            {
            match(';'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SEMI

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:125:7: ( ',' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:125:9: ','
            {
            match(','); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:126:5: ( '.' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:126:7: '.'
            {
            match('.'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start EQEQ
    public final void mEQEQ() throws RecognitionException {
        try {
            int _type = EQEQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:127:6: ( '==' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:127:8: '=='
            {
            match("=="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQEQ

    // $ANTLR start EQ
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:128:4: ( '=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:128:6: '='
            {
            match('='); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQ

    // $ANTLR start GT
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:129:4: ( '>' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:129:6: '>'
            {
            match('>'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GT

    // $ANTLR start LT
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:130:4: ( '<' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:130:6: '<'
            {
            match('<'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LT

    // $ANTLR start LTGT
    public final void mLTGT() throws RecognitionException {
        try {
            int _type = LTGT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:131:6: ( '<>' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:131:8: '<>'
            {
            match("<>"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LTGT

    // $ANTLR start LTEQ
    public final void mLTEQ() throws RecognitionException {
        try {
            int _type = LTEQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:132:6: ( '<=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:132:8: '<='
            {
            match("<="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LTEQ

    // $ANTLR start GTEQ
    public final void mGTEQ() throws RecognitionException {
        try {
            int _type = GTEQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:133:6: ( '>=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:133:8: '>='
            {
            match(">="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GTEQ

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:134:6: ( '+' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:134:8: '+'
            {
            match('+'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start SUB
    public final void mSUB() throws RecognitionException {
        try {
            int _type = SUB;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:135:5: ( '-' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:135:7: '-'
            {
            match('-'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUB

    // $ANTLR start STAR
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:136:6: ( '*' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:136:8: '*'
            {
            match('*'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STAR

    // $ANTLR start SLASH
    public final void mSLASH() throws RecognitionException {
        try {
            int _type = SLASH;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:137:7: ( '/' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:137:9: '/'
            {
            match('/'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SLASH

    // $ANTLR start PERCENT
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:138:9: ( '%' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:138:11: '%'
            {
            match('%'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PERCENT

    // $ANTLR start PLUSEQ
    public final void mPLUSEQ() throws RecognitionException {
        try {
            int _type = PLUSEQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:139:8: ( '+=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:139:10: '+='
            {
            match("+="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUSEQ

    // $ANTLR start SUBEQ
    public final void mSUBEQ() throws RecognitionException {
        try {
            int _type = SUBEQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:140:7: ( '-=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:140:9: '-='
            {
            match("-="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUBEQ

    // $ANTLR start STAREQ
    public final void mSTAREQ() throws RecognitionException {
        try {
            int _type = STAREQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:141:8: ( '*=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:141:10: '*='
            {
            match("*="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STAREQ

    // $ANTLR start SLASHEQ
    public final void mSLASHEQ() throws RecognitionException {
        try {
            int _type = SLASHEQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:142:9: ( '/=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:142:11: '/='
            {
            match("/="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SLASHEQ

    // $ANTLR start PERCENTEQ
    public final void mPERCENTEQ() throws RecognitionException {
        try {
            int _type = PERCENTEQ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:143:11: ( '%=' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:143:13: '%='
            {
            match("%="); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PERCENTEQ

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:144:7: ( ':' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:144:9: ':'
            {
            match(':'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start QUES
    public final void mQUES() throws RecognitionException {
        try {
            int _type = QUES;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:145:6: ( '?' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:145:8: '?'
            {
            match('?'); if (state.failed) return ;


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end QUES

    // $ANTLR start TWEEN
    public final void mTWEEN() throws RecognitionException {
        try {
            int _type = TWEEN;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:146:7: ( 'tween' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:146:9: 'tween'
            {
            match("tween"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TWEEN

    // $ANTLR start SUCHTHAT
    public final void mSUCHTHAT() throws RecognitionException {
        try {
            int _type = SUCHTHAT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:147:10: ( '=>' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:147:12: '=>'
            {
            match("=>"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUCHTHAT

    // $ANTLR start STRING_LITERAL
    public final void mSTRING_LITERAL() throws RecognitionException {
        try {
            int _type = STRING_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:259:19: ( '\"' DoubleQuoteBody '\"' | '\\'' SingleQuoteBody '\\'' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='\"') ) {
                alt1=1;
            }
            else if ( (LA1_0=='\'') ) {
                alt1=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:259:21: '\"' DoubleQuoteBody '\"'
                    {
                    match('\"'); if (state.failed) return ;
                    mDoubleQuoteBody(); if (state.failed) return ;
                    match('\"'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       processString(); 
                    }


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:260:7: '\\'' SingleQuoteBody '\\''
                    {
                    match('\''); if (state.failed) return ;
                    mSingleQuoteBody(); if (state.failed) return ;
                    match('\''); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       processString(); 
                    }


                    }
                    break;

            }
            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING_LITERAL

    // $ANTLR start QUOTE_LBRACE_STRING_LITERAL
    public final void mQUOTE_LBRACE_STRING_LITERAL() throws RecognitionException {
        try {
            int _type = QUOTE_LBRACE_STRING_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:263:30: ( '\"' DoubleQuoteBody '{' NextIsPercent[DBL_QUOTE_CTX] | '\\'' SingleQuoteBody '{' NextIsPercent[SNG_QUOTE_CTX] )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\"') ) {
                alt2=1;
            }
            else if ( (LA2_0=='\'') ) {
                alt2=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:263:32: '\"' DoubleQuoteBody '{' NextIsPercent[DBL_QUOTE_CTX]
                    {
                    match('\"'); if (state.failed) return ;
                    mDoubleQuoteBody(); if (state.failed) return ;
                    match('{'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       processString(); 
                    }
                    mNextIsPercent(DBL_QUOTE_CTX); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:265:7: '\\'' SingleQuoteBody '{' NextIsPercent[SNG_QUOTE_CTX]
                    {
                    match('\''); if (state.failed) return ;
                    mSingleQuoteBody(); if (state.failed) return ;
                    match('{'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       processString(); 
                    }
                    mNextIsPercent(SNG_QUOTE_CTX); if (state.failed) return ;


                    }
                    break;

            }
            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end QUOTE_LBRACE_STRING_LITERAL

    // $ANTLR start LBRACE
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:268:11: ( '{' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:268:13: '{'
            {
            match('{'); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               enterBrace(0, false); 
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LBRACE

    // $ANTLR start RBRACE_QUOTE_STRING_LITERAL
    public final void mRBRACE_QUOTE_STRING_LITERAL() throws RecognitionException {
        try {
            int _type = RBRACE_QUOTE_STRING_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:270:30: ({...}? => '}' DoubleQuoteBody '\"' | {...}? => '}' SingleQuoteBody '\\'' )
            int alt3=2;
            alt3 = dfa3.predict(input);
            switch (alt3) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:270:35: {...}? => '}' DoubleQuoteBody '\"'
                    {
                    if ( !( rightBraceLikeQuote(DBL_QUOTE_CTX) ) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "RBRACE_QUOTE_STRING_LITERAL", " rightBraceLikeQuote(DBL_QUOTE_CTX) ");
                    }
                    match('}'); if (state.failed) return ;
                    mDoubleQuoteBody(); if (state.failed) return ;
                    match('\"'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       leaveBrace(); 
                      				         			  leaveQuote(); 
                      				         			  processString(); 
                    }


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:274:10: {...}? => '}' SingleQuoteBody '\\''
                    {
                    if ( !( rightBraceLikeQuote(SNG_QUOTE_CTX) ) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "RBRACE_QUOTE_STRING_LITERAL", " rightBraceLikeQuote(SNG_QUOTE_CTX) ");
                    }
                    match('}'); if (state.failed) return ;
                    mSingleQuoteBody(); if (state.failed) return ;
                    match('\''); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       leaveBrace(); 
                      				         			  leaveQuote(); 
                      				         			  processString(); 
                    }


                    }
                    break;

            }
            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RBRACE_QUOTE_STRING_LITERAL

    // $ANTLR start RBRACE_LBRACE_STRING_LITERAL
    public final void mRBRACE_LBRACE_STRING_LITERAL() throws RecognitionException {
        try {
            int _type = RBRACE_LBRACE_STRING_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:279:31: ({...}? => '}' DoubleQuoteBody '{' NextIsPercent[CUR_QUOTE_CTX] | {...}? => '}' SingleQuoteBody '{' NextIsPercent[CUR_QUOTE_CTX] )
            int alt4=2;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:279:36: {...}? => '}' DoubleQuoteBody '{' NextIsPercent[CUR_QUOTE_CTX]
                    {
                    if ( !( rightBraceLikeQuote(DBL_QUOTE_CTX) ) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "RBRACE_LBRACE_STRING_LITERAL", " rightBraceLikeQuote(DBL_QUOTE_CTX) ");
                    }
                    match('}'); if (state.failed) return ;
                    mDoubleQuoteBody(); if (state.failed) return ;
                    match('{'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       leaveBrace(); 
                      				         			  processString(); 
                    }
                    mNextIsPercent(CUR_QUOTE_CTX); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:283:10: {...}? => '}' SingleQuoteBody '{' NextIsPercent[CUR_QUOTE_CTX]
                    {
                    if ( !( rightBraceLikeQuote(SNG_QUOTE_CTX) ) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "RBRACE_LBRACE_STRING_LITERAL", " rightBraceLikeQuote(SNG_QUOTE_CTX) ");
                    }
                    match('}'); if (state.failed) return ;
                    mSingleQuoteBody(); if (state.failed) return ;
                    match('{'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       leaveBrace(); 
                      				         			  processString(); 
                    }
                    mNextIsPercent(CUR_QUOTE_CTX); if (state.failed) return ;


                    }
                    break;

            }
            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RBRACE_LBRACE_STRING_LITERAL

    // $ANTLR start RBRACE
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:288:11: ({...}? => '}' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:288:16: {...}? => '}'
            {
            if ( !( !rightBraceLikeQuote(CUR_QUOTE_CTX) ) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "RBRACE", " !rightBraceLikeQuote(CUR_QUOTE_CTX) ");
            }
            match('}'); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               leaveBrace(); 
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RBRACE

    // $ANTLR start DoubleQuoteBody
    public final void mDoubleQuoteBody() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:292:18: ( (~ ( '{' | '\"' | '\\\\' ) | '\\\\' . )* )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:292:21: (~ ( '{' | '\"' | '\\\\' ) | '\\\\' . )*
            {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:292:21: (~ ( '{' | '\"' | '\\\\' ) | '\\\\' . )*
            loop5:
            do {
                int alt5=3;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='\u0000' && LA5_0<='!')||(LA5_0>='#' && LA5_0<='[')||(LA5_0>=']' && LA5_0<='z')||(LA5_0>='|' && LA5_0<='\uFFFE')) ) {
                    alt5=1;
                }
                else if ( (LA5_0=='\\') ) {
                    alt5=2;
                }


                switch (alt5) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:292:22: ~ ( '{' | '\"' | '\\\\' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='z')||(input.LA(1)>='|' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}



            	    }
            	    break;
            	case 2 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:292:39: '\\\\' .
            	    {
            	    match('\\'); if (state.failed) return ;
            	    matchAny(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);



            }

        }
        finally {
        }
    }
    // $ANTLR end DoubleQuoteBody

    // $ANTLR start SingleQuoteBody
    public final void mSingleQuoteBody() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:295:18: ( (~ ( '{' | '\\'' | '\\\\' ) | '\\\\' . )* )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:295:21: (~ ( '{' | '\\'' | '\\\\' ) | '\\\\' . )*
            {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:295:21: (~ ( '{' | '\\'' | '\\\\' ) | '\\\\' . )*
            loop6:
            do {
                int alt6=3;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>='\u0000' && LA6_0<='&')||(LA6_0>='(' && LA6_0<='[')||(LA6_0>=']' && LA6_0<='z')||(LA6_0>='|' && LA6_0<='\uFFFE')) ) {
                    alt6=1;
                }
                else if ( (LA6_0=='\\') ) {
                    alt6=2;
                }


                switch (alt6) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:295:22: ~ ( '{' | '\\'' | '\\\\' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='z')||(input.LA(1)>='|' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}



            	    }
            	    break;
            	case 2 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:295:40: '\\\\' .
            	    {
            	    match('\\'); if (state.failed) return ;
            	    matchAny(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);



            }

        }
        finally {
        }
    }
    // $ANTLR end SingleQuoteBody

    // $ANTLR start NextIsPercent
    public final void mNextIsPercent(int quoteContext) throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:299:6: ( ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )* '%' )=> | )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (synpred1_v3()) ) {
                alt7=1;
            }
            else if ( (true) ) {
                alt7=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:299:8: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )* '%' )=>
                    {
                    if ( state.backtracking==0 ) {
                       enterBrace(quoteContext, true); 
                    }


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:301:10: 
                    {
                    if ( state.backtracking==0 ) {
                       enterBrace(quoteContext, false); 
                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end NextIsPercent

    // $ANTLR start FORMAT_STRING_LITERAL
    public final void mFORMAT_STRING_LITERAL() throws RecognitionException {
        try {
            int _type = FORMAT_STRING_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:303:24: ({...}? => '%' (~ ' ' )* )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:303:30: {...}? => '%' (~ ' ' )*
            {
            if ( !( percentIsFormat() ) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "FORMAT_STRING_LITERAL", " percentIsFormat() ");
            }
            match('%'); if (state.failed) return ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:304:11: (~ ' ' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='\u0000' && LA8_0<='\u001F')||(LA8_0>='!' && LA8_0<='\uFFFE')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:304:12: ~ ' '
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\u001F')||(input.LA(1)>='!' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}



            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               resetPercentIsFormat(); 
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FORMAT_STRING_LITERAL

    // $ANTLR start TRANSLATION_KEY
    public final void mTRANSLATION_KEY() throws RecognitionException {
        try {
            int _type = TRANSLATION_KEY;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:306:33: ( '##' ( '[' TranslationKeyBody ']' )? )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:306:35: '##' ( '[' TranslationKeyBody ']' )?
            {
            match("##"); if (state.failed) return ;

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:307:35: ( '[' TranslationKeyBody ']' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='[') ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:308:37: '[' TranslationKeyBody ']'
                    {
                    match('['); if (state.failed) return ;
                    mTranslationKeyBody(); if (state.failed) return ;
                    match(']'); if (state.failed) return ;


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               processTranslationKey(); 
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRANSLATION_KEY

    // $ANTLR start TranslationKeyBody
    public final void mTranslationKeyBody() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:313:33: ( (~ ( '[' | ']' | '\\\\' ) | '\\\\' . )+ )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:313:35: (~ ( '[' | ']' | '\\\\' ) | '\\\\' . )+
            {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:313:35: (~ ( '[' | ']' | '\\\\' ) | '\\\\' . )+
            int cnt10=0;
            loop10:
            do {
                int alt10=3;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>='\u0000' && LA10_0<='Z')||(LA10_0>='^' && LA10_0<='\uFFFE')) ) {
                    alt10=1;
                }
                else if ( (LA10_0=='\\') ) {
                    alt10=2;
                }


                switch (alt10) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:313:36: ~ ( '[' | ']' | '\\\\' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='Z')||(input.LA(1)>='^' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}



            	    }
            	    break;
            	case 2 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:313:56: '\\\\' .
            	    {
            	    match('\\'); if (state.failed) return ;
            	    matchAny(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);



            }

        }
        finally {
        }
    }
    // $ANTLR end TranslationKeyBody

    // $ANTLR start TIME_LITERAL
    public final void mTIME_LITERAL() throws RecognitionException {
        try {
            int _type = TIME_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:14: ( ( DECIMAL_LITERAL | Digits '.' ( Digits )? ( Exponent )? ) ( 'ms' | 'm' | 's' | 'h' ) )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:16: ( DECIMAL_LITERAL | Digits '.' ( Digits )? ( Exponent )? ) ( 'ms' | 'm' | 's' | 'h' )
            {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:16: ( DECIMAL_LITERAL | Digits '.' ( Digits )? ( Exponent )? )
            int alt13=2;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:17: DECIMAL_LITERAL
                    {
                    mDECIMAL_LITERAL(); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:35: Digits '.' ( Digits )? ( Exponent )?
                    {
                    mDigits(); if (state.failed) return ;
                    match('.'); if (state.failed) return ;
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:46: ( Digits )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:47: Digits
                            {
                            mDigits(); if (state.failed) return ;


                            }
                            break;

                    }

                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:56: ( Exponent )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0=='E'||LA12_0=='e') ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:57: Exponent
                            {
                            mExponent(); if (state.failed) return ;


                            }
                            break;

                    }



                    }
                    break;

            }

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:70: ( 'ms' | 'm' | 's' | 'h' )
            int alt14=4;
            switch ( input.LA(1) ) {
            case 'm':
                {
                int LA14_1 = input.LA(2);

                if ( (LA14_1=='s') ) {
                    alt14=1;
                }
                else {
                    alt14=2;}
                }
                break;
            case 's':
                {
                alt14=3;
                }
                break;
            case 'h':
                {
                alt14=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:72: 'ms'
                    {
                    match("ms"); if (state.failed) return ;



                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:79: 'm'
                    {
                    match('m'); if (state.failed) return ;


                    }
                    break;
                case 3 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:85: 's'
                    {
                    match('s'); if (state.failed) return ;


                    }
                    break;
                case 4 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:316:91: 'h'
                    {
                    match('h'); if (state.failed) return ;


                    }
                    break;

            }



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TIME_LITERAL

    // $ANTLR start DECIMAL_LITERAL
    public final void mDECIMAL_LITERAL() throws RecognitionException {
        try {
            int _type = DECIMAL_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:318:17: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:318:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:318:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='0') ) {
                alt16=1;
            }
            else if ( ((LA16_0>='1' && LA16_0<='9')) ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:318:20: '0'
                    {
                    match('0'); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:318:26: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); if (state.failed) return ;
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:318:35: ( '0' .. '9' )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:318:35: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;


                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);



                    }
                    break;

            }



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DECIMAL_LITERAL

    // $ANTLR start OCTAL_LITERAL
    public final void mOCTAL_LITERAL() throws RecognitionException {
        try {
            int _type = OCTAL_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:320:15: ( '0' ( '0' .. '7' )+ )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:320:17: '0' ( '0' .. '7' )+
            {
            match('0'); if (state.failed) return ;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:320:21: ( '0' .. '7' )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>='0' && LA17_0<='7')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:320:22: '0' .. '7'
            	    {
            	    matchRange('0','7'); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OCTAL_LITERAL

    // $ANTLR start HEX_LITERAL
    public final void mHEX_LITERAL() throws RecognitionException {
        try {
            int _type = HEX_LITERAL;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:322:13: ( '0' ( 'x' | 'X' ) ( HexDigit )+ )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:322:15: '0' ( 'x' | 'X' ) ( HexDigit )+
            {
            match('0'); if (state.failed) return ;
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:322:29: ( HexDigit )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>='0' && LA18_0<='9')||(LA18_0>='A' && LA18_0<='F')||(LA18_0>='a' && LA18_0<='f')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:322:29: HexDigit
            	    {
            	    mHexDigit(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);

            if ( state.backtracking==0 ) {
               setText(getText().substring(2, getText().length())); 
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HEX_LITERAL

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:325:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:325:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}



            }

        }
        finally {
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start FLOATING_POINT_LITERAL
    public final void mFLOATING_POINT_LITERAL() throws RecognitionException {
        try {
            int _type = FLOATING_POINT_LITERAL;
            Token d=null;
            Token RangeDots1=null;
            Token RangeDots2=null;

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:328:5: (d= DECIMAL_LITERAL RangeDots | d= OCTAL_LITERAL RangeDots | Digits '.' ( Digits )? ( Exponent )? | '.' Digits ( Exponent )? | Digits Exponent )
            int alt22=5;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:328:11: d= DECIMAL_LITERAL RangeDots
                    {
                    int dStart1650 = getCharIndex();
                    mDECIMAL_LITERAL(); if (state.failed) return ;
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart1650, getCharIndex()-1);
                    int RangeDots1Start1652 = getCharIndex();
                    mRangeDots(); if (state.failed) return ;
                    RangeDots1 = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, RangeDots1Start1652, getCharIndex()-1);
                    if ( state.backtracking==0 ) {
                      
                          	  		d.setType(DECIMAL_LITERAL);
                          	  		emit(d);
                                		RangeDots1.setType(DOTDOT);
                          	  		emit(RangeDots1);
                          	  	
                    }


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:335:11: d= OCTAL_LITERAL RangeDots
                    {
                    int dStart1676 = getCharIndex();
                    mOCTAL_LITERAL(); if (state.failed) return ;
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart1676, getCharIndex()-1);
                    int RangeDots2Start1678 = getCharIndex();
                    mRangeDots(); if (state.failed) return ;
                    RangeDots2 = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, RangeDots2Start1678, getCharIndex()-1);
                    if ( state.backtracking==0 ) {
                      
                          	  		d.setType(OCTAL_LITERAL);
                          	  		emit(d);
                                		RangeDots2.setType(DOTDOT);
                          	  		emit(RangeDots2);
                          	  	
                    }


                    }
                    break;
                case 3 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:342:9: Digits '.' ( Digits )? ( Exponent )?
                    {
                    mDigits(); if (state.failed) return ;
                    match('.'); if (state.failed) return ;
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:342:20: ( Digits )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( ((LA19_0>='0' && LA19_0<='9')) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:342:21: Digits
                            {
                            mDigits(); if (state.failed) return ;


                            }
                            break;

                    }

                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:342:30: ( Exponent )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0=='E'||LA20_0=='e') ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:342:31: Exponent
                            {
                            mExponent(); if (state.failed) return ;


                            }
                            break;

                    }



                    }
                    break;
                case 4 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:343:7: '.' Digits ( Exponent )?
                    {
                    match('.'); if (state.failed) return ;
                    mDigits(); if (state.failed) return ;
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:343:18: ( Exponent )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0=='E'||LA21_0=='e') ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:343:19: Exponent
                            {
                            mExponent(); if (state.failed) return ;


                            }
                            break;

                    }



                    }
                    break;
                case 5 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:344:11: Digits Exponent
                    {
                    mDigits(); if (state.failed) return ;
                    mExponent(); if (state.failed) return ;


                    }
                    break;

            }
            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FLOATING_POINT_LITERAL

    // $ANTLR start RangeDots
    public final void mRangeDots() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:349:2: ( DOTDOT )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:349:4: DOTDOT
            {
            mDOTDOT(); if (state.failed) return ;


            }

        }
        finally {
        }
    }
    // $ANTLR end RangeDots

    // $ANTLR start Digits
    public final void mDigits() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:352:8: ( ( '0' .. '9' )+ )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:352:10: ( '0' .. '9' )+
            {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:352:10: ( '0' .. '9' )+
            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>='0' && LA23_0<='9')) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:352:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);



            }

        }
        finally {
        }
    }
    // $ANTLR end Digits

    // $ANTLR start Exponent
    public final void mExponent() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:355:10: ( ( 'e' | 'E' ) ( '+' | '-' )? Digits )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:355:13: ( 'e' | 'E' ) ( '+' | '-' )? Digits
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:355:23: ( '+' | '-' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='+'||LA24_0=='-') ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            mDigits(); if (state.failed) return ;


            }

        }
        finally {
        }
    }
    // $ANTLR end Exponent

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:359:2: ( Letter ( Letter | JavaIDDigit )* | '<<' (~ '>' | '>' ~ '>' )* ( '>' )* '>>' )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0=='$'||(LA28_0>='A' && LA28_0<='Z')||LA28_0=='_'||(LA28_0>='a' && LA28_0<='z')||(LA28_0>='\u00C0' && LA28_0<='\u00D6')||(LA28_0>='\u00D8' && LA28_0<='\u00F6')||(LA28_0>='\u00F8' && LA28_0<='\u1FFF')||(LA28_0>='\u3040' && LA28_0<='\u318F')||(LA28_0>='\u3300' && LA28_0<='\u337F')||(LA28_0>='\u3400' && LA28_0<='\u3D2D')||(LA28_0>='\u4E00' && LA28_0<='\u9FFF')||(LA28_0>='\uF900' && LA28_0<='\uFAFF')) ) {
                alt28=1;
            }
            else if ( (LA28_0=='<') ) {
                alt28=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:359:4: Letter ( Letter | JavaIDDigit )*
                    {
                    mLetter(); if (state.failed) return ;
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:359:11: ( Letter | JavaIDDigit )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0=='$'||(LA25_0>='0' && LA25_0<='9')||(LA25_0>='A' && LA25_0<='Z')||LA25_0=='_'||(LA25_0>='a' && LA25_0<='z')||(LA25_0>='\u00C0' && LA25_0<='\u00D6')||(LA25_0>='\u00D8' && LA25_0<='\u00F6')||(LA25_0>='\u00F8' && LA25_0<='\u1FFF')||(LA25_0>='\u3040' && LA25_0<='\u318F')||(LA25_0>='\u3300' && LA25_0<='\u337F')||(LA25_0>='\u3400' && LA25_0<='\u3D2D')||(LA25_0>='\u4E00' && LA25_0<='\u9FFF')||(LA25_0>='\uF900' && LA25_0<='\uFAFF')) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:
                    	    {
                    	    if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);



                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:360:4: '<<' (~ '>' | '>' ~ '>' )* ( '>' )* '>>'
                    {
                    match("<<"); if (state.failed) return ;

                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:360:9: (~ '>' | '>' ~ '>' )*
                    loop26:
                    do {
                        int alt26=3;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0=='>') ) {
                            int LA26_1 = input.LA(2);

                            if ( ((LA26_1>='\u0000' && LA26_1<='=')||(LA26_1>='?' && LA26_1<='\uFFFE')) ) {
                                alt26=2;
                            }


                        }
                        else if ( ((LA26_0>='\u0000' && LA26_0<='=')||(LA26_0>='?' && LA26_0<='\uFFFE')) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:360:10: ~ '>'
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='=')||(input.LA(1)>='?' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}



                    	    }
                    	    break;
                    	case 2 :
                    	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:360:16: '>' ~ '>'
                    	    {
                    	    match('>'); if (state.failed) return ;
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='=')||(input.LA(1)>='?' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}



                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:360:27: ( '>' )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0=='>') ) {
                            int LA27_1 = input.LA(2);

                            if ( (LA27_1=='>') ) {
                                int LA27_2 = input.LA(3);

                                if ( (LA27_2=='>') ) {
                                    alt27=1;
                                }


                            }


                        }


                        switch (alt27) {
                    	case 1 :
                    	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:360:27: '>'
                    	    {
                    	    match('>'); if (state.failed) return ;


                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);

                    match(">>"); if (state.failed) return ;

                    if ( state.backtracking==0 ) {
                       setText(getText().substring(2, getText().length()-2)); 
                    }


                    }
                    break;

            }
            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IDENTIFIER

    // $ANTLR start Letter
    public final void mLetter() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:365:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' | '\\u0100' .. '\\u1fff' | '\\u3040' .. '\\u318f' | '\\u3300' .. '\\u337f' | '\\u3400' .. '\\u3d2d' | '\\u4e00' .. '\\u9fff' | '\\uf900' .. '\\ufaff' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end Letter

    // $ANTLR start JavaIDDigit
    public final void mJavaIDDigit() throws RecognitionException {
        try {
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:382:5: ( '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06f0' .. '\\u06f9' | '\\u0966' .. '\\u096f' | '\\u09e6' .. '\\u09ef' | '\\u0a66' .. '\\u0a6f' | '\\u0ae6' .. '\\u0aef' | '\\u0b66' .. '\\u0b6f' | '\\u0be7' .. '\\u0bef' | '\\u0c66' .. '\\u0c6f' | '\\u0ce6' .. '\\u0cef' | '\\u0d66' .. '\\u0d6f' | '\\u0e50' .. '\\u0e59' | '\\u0ed0' .. '\\u0ed9' | '\\u1040' .. '\\u1049' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='\u0660' && input.LA(1)<='\u0669')||(input.LA(1)>='\u06F0' && input.LA(1)<='\u06F9')||(input.LA(1)>='\u0966' && input.LA(1)<='\u096F')||(input.LA(1)>='\u09E6' && input.LA(1)<='\u09EF')||(input.LA(1)>='\u0A66' && input.LA(1)<='\u0A6F')||(input.LA(1)>='\u0AE6' && input.LA(1)<='\u0AEF')||(input.LA(1)>='\u0B66' && input.LA(1)<='\u0B6F')||(input.LA(1)>='\u0BE7' && input.LA(1)<='\u0BEF')||(input.LA(1)>='\u0C66' && input.LA(1)<='\u0C6F')||(input.LA(1)>='\u0CE6' && input.LA(1)<='\u0CEF')||(input.LA(1)>='\u0D66' && input.LA(1)<='\u0D6F')||(input.LA(1)>='\u0E50' && input.LA(1)<='\u0E59')||(input.LA(1)>='\u0ED0' && input.LA(1)<='\u0ED9')||(input.LA(1)>='\u1040' && input.LA(1)<='\u1049') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end JavaIDDigit

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:399:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:399:8: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( state.backtracking==0 ) {
              state.channel=HIDDEN;
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start COMMENT
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:403:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:403:9: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (state.failed) return ;

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:403:14: ( options {greedy=false; } : . )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0=='*') ) {
                    int LA29_1 = input.LA(2);

                    if ( (LA29_1=='/') ) {
                        alt29=2;
                    }
                    else if ( ((LA29_1>='\u0000' && LA29_1<='.')||(LA29_1>='0' && LA29_1<='\uFFFE')) ) {
                        alt29=1;
                    }


                }
                else if ( ((LA29_0>='\u0000' && LA29_0<=')')||(LA29_0>='+' && LA29_0<='\uFFFE')) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:403:42: .
            	    {
            	    matchAny(); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

            match("*/"); if (state.failed) return ;

            if ( state.backtracking==0 ) {
              state.channel=HIDDEN;
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMENT

    // $ANTLR start LINE_COMMENT
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' | EOF ) )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:7: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' | EOF )
            {
            match("//"); if (state.failed) return ;

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:12: (~ ( '\\n' | '\\r' ) )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( ((LA30_0>='\u0000' && LA30_0<='\t')||(LA30_0>='\u000B' && LA30_0<='\f')||(LA30_0>='\u000E' && LA30_0<='\uFFFE')) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:12: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}



            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:26: ( '\\r' )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0=='\r') ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:26: '\\r'
                    {
                    match('\r'); if (state.failed) return ;


                    }
                    break;

            }

            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:32: ( '\\n' | EOF )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0=='\n') ) {
                alt32=1;
            }
            else {
                alt32=2;}
            switch (alt32) {
                case 1 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:33: '\\n'
                    {
                    match('\n'); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:407:40: EOF
                    {
                    match(EOF); if (state.failed) return ;


                    }
                    break;

            }

            if ( state.backtracking==0 ) {
              state.channel=HIDDEN;
            }


            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE_COMMENT

    // $ANTLR start LAST_TOKEN
    public final void mLAST_TOKEN() throws RecognitionException {
        try {
            int _type = LAST_TOKEN;
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:411:5: ( '~~~~~~~~' {...}? '~~~~~~~~' )
            // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:411:7: '~~~~~~~~' {...}? '~~~~~~~~'
            {
            match("~~~~~~~~"); if (state.failed) return ;

            if ( !(false) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "LAST_TOKEN", "false");
            }
            match("~~~~~~~~"); if (state.failed) return ;



            }

            state.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LAST_TOKEN

    public void mTokens() throws RecognitionException {
        // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:8: ( ABSTRACT | ASSERT | ATTRIBUTE | BIND | BOUND | BREAK | CLASS | CONTINUE | DELETE | FALSE | FOR | FUNCTION | IF | IMPORT | INIT | INSERT | LET | NEW | NOT | NULL | OVERRIDE | PACKAGE | POSTINIT | PRIVATE | PROTECTED | PUBLIC | READONLY | RETURN | SUPER | SIZEOF | STATIC | THIS | THROW | TRY | TRUE | VAR | WHILE | POUND | LPAREN | LBRACKET | PLUSPLUS | SUBSUB | PIPE | AFTER | AND | AS | BEFORE | CATCH | ELSE | EXCLUSIVE | EXTENDS | FINALLY | FIRST | FROM | IN | INDEXOF | INSTANCEOF | INTO | INVERSE | LAST | LAZY | ON | OR | REPLACE | REVERSE | STEP | THEN | TYPEOF | WITH | WHERE | DOTDOT | RPAREN | RBRACKET | SEMI | COMMA | DOT | EQEQ | EQ | GT | LT | LTGT | LTEQ | GTEQ | PLUS | SUB | STAR | SLASH | PERCENT | PLUSEQ | SUBEQ | STAREQ | SLASHEQ | PERCENTEQ | COLON | QUES | TWEEN | SUCHTHAT | STRING_LITERAL | QUOTE_LBRACE_STRING_LITERAL | LBRACE | RBRACE_QUOTE_STRING_LITERAL | RBRACE_LBRACE_STRING_LITERAL | RBRACE | FORMAT_STRING_LITERAL | TRANSLATION_KEY | TIME_LITERAL | DECIMAL_LITERAL | OCTAL_LITERAL | HEX_LITERAL | FLOATING_POINT_LITERAL | IDENTIFIER | WS | COMMENT | LINE_COMMENT | LAST_TOKEN )
        int alt33=115;
        alt33 = dfa33.predict(input);
        switch (alt33) {
            case 1 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:10: ABSTRACT
                {
                mABSTRACT(); if (state.failed) return ;


                }
                break;
            case 2 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:19: ASSERT
                {
                mASSERT(); if (state.failed) return ;


                }
                break;
            case 3 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:26: ATTRIBUTE
                {
                mATTRIBUTE(); if (state.failed) return ;


                }
                break;
            case 4 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:36: BIND
                {
                mBIND(); if (state.failed) return ;


                }
                break;
            case 5 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:41: BOUND
                {
                mBOUND(); if (state.failed) return ;


                }
                break;
            case 6 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:47: BREAK
                {
                mBREAK(); if (state.failed) return ;


                }
                break;
            case 7 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:53: CLASS
                {
                mCLASS(); if (state.failed) return ;


                }
                break;
            case 8 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:59: CONTINUE
                {
                mCONTINUE(); if (state.failed) return ;


                }
                break;
            case 9 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:68: DELETE
                {
                mDELETE(); if (state.failed) return ;


                }
                break;
            case 10 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:75: FALSE
                {
                mFALSE(); if (state.failed) return ;


                }
                break;
            case 11 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:81: FOR
                {
                mFOR(); if (state.failed) return ;


                }
                break;
            case 12 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:85: FUNCTION
                {
                mFUNCTION(); if (state.failed) return ;


                }
                break;
            case 13 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:94: IF
                {
                mIF(); if (state.failed) return ;


                }
                break;
            case 14 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:97: IMPORT
                {
                mIMPORT(); if (state.failed) return ;


                }
                break;
            case 15 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:104: INIT
                {
                mINIT(); if (state.failed) return ;


                }
                break;
            case 16 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:109: INSERT
                {
                mINSERT(); if (state.failed) return ;


                }
                break;
            case 17 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:116: LET
                {
                mLET(); if (state.failed) return ;


                }
                break;
            case 18 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:120: NEW
                {
                mNEW(); if (state.failed) return ;


                }
                break;
            case 19 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:124: NOT
                {
                mNOT(); if (state.failed) return ;


                }
                break;
            case 20 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:128: NULL
                {
                mNULL(); if (state.failed) return ;


                }
                break;
            case 21 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:133: OVERRIDE
                {
                mOVERRIDE(); if (state.failed) return ;


                }
                break;
            case 22 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:142: PACKAGE
                {
                mPACKAGE(); if (state.failed) return ;


                }
                break;
            case 23 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:150: POSTINIT
                {
                mPOSTINIT(); if (state.failed) return ;


                }
                break;
            case 24 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:159: PRIVATE
                {
                mPRIVATE(); if (state.failed) return ;


                }
                break;
            case 25 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:167: PROTECTED
                {
                mPROTECTED(); if (state.failed) return ;


                }
                break;
            case 26 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:177: PUBLIC
                {
                mPUBLIC(); if (state.failed) return ;


                }
                break;
            case 27 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:184: READONLY
                {
                mREADONLY(); if (state.failed) return ;


                }
                break;
            case 28 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:193: RETURN
                {
                mRETURN(); if (state.failed) return ;


                }
                break;
            case 29 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:200: SUPER
                {
                mSUPER(); if (state.failed) return ;


                }
                break;
            case 30 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:206: SIZEOF
                {
                mSIZEOF(); if (state.failed) return ;


                }
                break;
            case 31 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:213: STATIC
                {
                mSTATIC(); if (state.failed) return ;


                }
                break;
            case 32 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:220: THIS
                {
                mTHIS(); if (state.failed) return ;


                }
                break;
            case 33 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:225: THROW
                {
                mTHROW(); if (state.failed) return ;


                }
                break;
            case 34 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:231: TRY
                {
                mTRY(); if (state.failed) return ;


                }
                break;
            case 35 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:235: TRUE
                {
                mTRUE(); if (state.failed) return ;


                }
                break;
            case 36 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:240: VAR
                {
                mVAR(); if (state.failed) return ;


                }
                break;
            case 37 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:244: WHILE
                {
                mWHILE(); if (state.failed) return ;


                }
                break;
            case 38 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:250: POUND
                {
                mPOUND(); if (state.failed) return ;


                }
                break;
            case 39 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:256: LPAREN
                {
                mLPAREN(); if (state.failed) return ;


                }
                break;
            case 40 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:263: LBRACKET
                {
                mLBRACKET(); if (state.failed) return ;


                }
                break;
            case 41 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:272: PLUSPLUS
                {
                mPLUSPLUS(); if (state.failed) return ;


                }
                break;
            case 42 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:281: SUBSUB
                {
                mSUBSUB(); if (state.failed) return ;


                }
                break;
            case 43 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:288: PIPE
                {
                mPIPE(); if (state.failed) return ;


                }
                break;
            case 44 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:293: AFTER
                {
                mAFTER(); if (state.failed) return ;


                }
                break;
            case 45 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:299: AND
                {
                mAND(); if (state.failed) return ;


                }
                break;
            case 46 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:303: AS
                {
                mAS(); if (state.failed) return ;


                }
                break;
            case 47 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:306: BEFORE
                {
                mBEFORE(); if (state.failed) return ;


                }
                break;
            case 48 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:313: CATCH
                {
                mCATCH(); if (state.failed) return ;


                }
                break;
            case 49 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:319: ELSE
                {
                mELSE(); if (state.failed) return ;


                }
                break;
            case 50 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:324: EXCLUSIVE
                {
                mEXCLUSIVE(); if (state.failed) return ;


                }
                break;
            case 51 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:334: EXTENDS
                {
                mEXTENDS(); if (state.failed) return ;


                }
                break;
            case 52 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:342: FINALLY
                {
                mFINALLY(); if (state.failed) return ;


                }
                break;
            case 53 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:350: FIRST
                {
                mFIRST(); if (state.failed) return ;


                }
                break;
            case 54 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:356: FROM
                {
                mFROM(); if (state.failed) return ;


                }
                break;
            case 55 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:361: IN
                {
                mIN(); if (state.failed) return ;


                }
                break;
            case 56 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:364: INDEXOF
                {
                mINDEXOF(); if (state.failed) return ;


                }
                break;
            case 57 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:372: INSTANCEOF
                {
                mINSTANCEOF(); if (state.failed) return ;


                }
                break;
            case 58 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:383: INTO
                {
                mINTO(); if (state.failed) return ;


                }
                break;
            case 59 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:388: INVERSE
                {
                mINVERSE(); if (state.failed) return ;


                }
                break;
            case 60 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:396: LAST
                {
                mLAST(); if (state.failed) return ;


                }
                break;
            case 61 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:401: LAZY
                {
                mLAZY(); if (state.failed) return ;


                }
                break;
            case 62 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:406: ON
                {
                mON(); if (state.failed) return ;


                }
                break;
            case 63 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:409: OR
                {
                mOR(); if (state.failed) return ;


                }
                break;
            case 64 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:412: REPLACE
                {
                mREPLACE(); if (state.failed) return ;


                }
                break;
            case 65 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:420: REVERSE
                {
                mREVERSE(); if (state.failed) return ;


                }
                break;
            case 66 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:428: STEP
                {
                mSTEP(); if (state.failed) return ;


                }
                break;
            case 67 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:433: THEN
                {
                mTHEN(); if (state.failed) return ;


                }
                break;
            case 68 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:438: TYPEOF
                {
                mTYPEOF(); if (state.failed) return ;


                }
                break;
            case 69 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:445: WITH
                {
                mWITH(); if (state.failed) return ;


                }
                break;
            case 70 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:450: WHERE
                {
                mWHERE(); if (state.failed) return ;


                }
                break;
            case 71 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:456: DOTDOT
                {
                mDOTDOT(); if (state.failed) return ;


                }
                break;
            case 72 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:463: RPAREN
                {
                mRPAREN(); if (state.failed) return ;


                }
                break;
            case 73 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:470: RBRACKET
                {
                mRBRACKET(); if (state.failed) return ;


                }
                break;
            case 74 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:479: SEMI
                {
                mSEMI(); if (state.failed) return ;


                }
                break;
            case 75 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:484: COMMA
                {
                mCOMMA(); if (state.failed) return ;


                }
                break;
            case 76 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:490: DOT
                {
                mDOT(); if (state.failed) return ;


                }
                break;
            case 77 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:494: EQEQ
                {
                mEQEQ(); if (state.failed) return ;


                }
                break;
            case 78 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:499: EQ
                {
                mEQ(); if (state.failed) return ;


                }
                break;
            case 79 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:502: GT
                {
                mGT(); if (state.failed) return ;


                }
                break;
            case 80 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:505: LT
                {
                mLT(); if (state.failed) return ;


                }
                break;
            case 81 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:508: LTGT
                {
                mLTGT(); if (state.failed) return ;


                }
                break;
            case 82 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:513: LTEQ
                {
                mLTEQ(); if (state.failed) return ;


                }
                break;
            case 83 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:518: GTEQ
                {
                mGTEQ(); if (state.failed) return ;


                }
                break;
            case 84 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:523: PLUS
                {
                mPLUS(); if (state.failed) return ;


                }
                break;
            case 85 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:528: SUB
                {
                mSUB(); if (state.failed) return ;


                }
                break;
            case 86 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:532: STAR
                {
                mSTAR(); if (state.failed) return ;


                }
                break;
            case 87 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:537: SLASH
                {
                mSLASH(); if (state.failed) return ;


                }
                break;
            case 88 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:543: PERCENT
                {
                mPERCENT(); if (state.failed) return ;


                }
                break;
            case 89 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:551: PLUSEQ
                {
                mPLUSEQ(); if (state.failed) return ;


                }
                break;
            case 90 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:558: SUBEQ
                {
                mSUBEQ(); if (state.failed) return ;


                }
                break;
            case 91 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:564: STAREQ
                {
                mSTAREQ(); if (state.failed) return ;


                }
                break;
            case 92 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:571: SLASHEQ
                {
                mSLASHEQ(); if (state.failed) return ;


                }
                break;
            case 93 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:579: PERCENTEQ
                {
                mPERCENTEQ(); if (state.failed) return ;


                }
                break;
            case 94 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:589: COLON
                {
                mCOLON(); if (state.failed) return ;


                }
                break;
            case 95 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:595: QUES
                {
                mQUES(); if (state.failed) return ;


                }
                break;
            case 96 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:600: TWEEN
                {
                mTWEEN(); if (state.failed) return ;


                }
                break;
            case 97 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:606: SUCHTHAT
                {
                mSUCHTHAT(); if (state.failed) return ;


                }
                break;
            case 98 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:615: STRING_LITERAL
                {
                mSTRING_LITERAL(); if (state.failed) return ;


                }
                break;
            case 99 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:630: QUOTE_LBRACE_STRING_LITERAL
                {
                mQUOTE_LBRACE_STRING_LITERAL(); if (state.failed) return ;


                }
                break;
            case 100 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:658: LBRACE
                {
                mLBRACE(); if (state.failed) return ;


                }
                break;
            case 101 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:665: RBRACE_QUOTE_STRING_LITERAL
                {
                mRBRACE_QUOTE_STRING_LITERAL(); if (state.failed) return ;


                }
                break;
            case 102 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:693: RBRACE_LBRACE_STRING_LITERAL
                {
                mRBRACE_LBRACE_STRING_LITERAL(); if (state.failed) return ;


                }
                break;
            case 103 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:722: RBRACE
                {
                mRBRACE(); if (state.failed) return ;


                }
                break;
            case 104 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:729: FORMAT_STRING_LITERAL
                {
                mFORMAT_STRING_LITERAL(); if (state.failed) return ;


                }
                break;
            case 105 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:751: TRANSLATION_KEY
                {
                mTRANSLATION_KEY(); if (state.failed) return ;


                }
                break;
            case 106 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:767: TIME_LITERAL
                {
                mTIME_LITERAL(); if (state.failed) return ;


                }
                break;
            case 107 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:780: DECIMAL_LITERAL
                {
                mDECIMAL_LITERAL(); if (state.failed) return ;


                }
                break;
            case 108 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:796: OCTAL_LITERAL
                {
                mOCTAL_LITERAL(); if (state.failed) return ;


                }
                break;
            case 109 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:810: HEX_LITERAL
                {
                mHEX_LITERAL(); if (state.failed) return ;


                }
                break;
            case 110 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:822: FLOATING_POINT_LITERAL
                {
                mFLOATING_POINT_LITERAL(); if (state.failed) return ;


                }
                break;
            case 111 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:845: IDENTIFIER
                {
                mIDENTIFIER(); if (state.failed) return ;


                }
                break;
            case 112 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:856: WS
                {
                mWS(); if (state.failed) return ;


                }
                break;
            case 113 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:859: COMMENT
                {
                mCOMMENT(); if (state.failed) return ;


                }
                break;
            case 114 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:867: LINE_COMMENT
                {
                mLINE_COMMENT(); if (state.failed) return ;


                }
                break;
            case 115 :
                // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:1:880: LAST_TOKEN
                {
                mLAST_TOKEN(); if (state.failed) return ;


                }
                break;

        }

    }

    // $ANTLR start synpred1_v3
    public final void synpred1_v3_fragment() throws RecognitionException {   
        // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:299:8: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )* '%' )
        // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:299:9: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )* '%'
        {
        // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:299:9: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )*
        loop34:
        do {
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( ((LA34_0>='\t' && LA34_0<='\n')||(LA34_0>='\f' && LA34_0<='\r')||LA34_0==' ') ) {
                alt34=1;
            }


            switch (alt34) {
        	case 1 :
        	    // E:\\SunWork\\nbjfxp\\localrep\\main\\contrib\\javafx.lexer/src/org/netbeans/lib/javafx/lexer/v3.g:
        	    {
        	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
        	        input.consume();
        	    state.failed=false;
        	    }
        	    else {
        	        if (state.backtracking>0) {state.failed=true; return ;}
        	        MismatchedSetException mse = new MismatchedSetException(null,input);
        	        recover(mse);
        	        throw mse;}


        	    }
        	    break;

        	default :
        	    break loop34;
            }
        } while (true);

        match('%'); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred1_v3

    public final boolean synpred1_v3() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_v3_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA3 dfa3 = new DFA3(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA13 dfa13 = new DFA13(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA33 dfa33 = new DFA33(this);
    static final String DFA3_eotS =
        "\4\uffff\1\7\1\10\3\uffff";
    static final String DFA3_eofS =
        "\11\uffff";
    static final String DFA3_minS =
        "\1\175\6\0\2\uffff";
    static final String DFA3_maxS =
        "\1\175\6\ufffe\2\uffff";
    static final String DFA3_acceptS =
        "\7\uffff\1\2\1\1";
    static final String DFA3_specialS =
        "\1\3\1\2\1\5\1\1\1\0\1\4\1\6\2\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\1",
            "\42\2\1\5\4\2\1\4\64\2\1\3\36\2\1\uffff\uff83\2",
            "\42\2\1\5\4\2\1\4\64\2\1\3\36\2\1\uffff\uff83\2",
            "\uffff\6",
            "\173\10\1\uffff\uff83\10",
            "\173\7\1\uffff\uff83\7",
            "\42\2\1\5\4\2\1\4\64\2\1\3\36\2\1\uffff\uff83\2",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "270:1: RBRACE_QUOTE_STRING_LITERAL : ({...}? => '}' DoubleQuoteBody '\"' | {...}? => '}' SingleQuoteBody '\\'' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA3_4 = input.LA(1);

                         
                        int index3_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA3_4>='\u0000' && LA3_4<='z')||(LA3_4>='|' && LA3_4<='\uFFFE')) && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 8;}

                        else s = 7;

                         
                        input.seek(index3_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA3_3 = input.LA(1);

                         
                        int index3_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA3_3>='\u0000' && LA3_3<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 6;}

                         
                        input.seek(index3_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA3_1 = input.LA(1);

                         
                        int index3_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA3_1>='\u0000' && LA3_1<='!')||(LA3_1>='#' && LA3_1<='&')||(LA3_1>='(' && LA3_1<='[')||(LA3_1>=']' && LA3_1<='z')||(LA3_1>='|' && LA3_1<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 2;}

                        else if ( (LA3_1=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 3;}

                        else if ( (LA3_1=='\'') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 4;}

                        else if ( (LA3_1=='\"') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 5;}

                         
                        input.seek(index3_1);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA3_0 = input.LA(1);

                         
                        int index3_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA3_0=='}') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 1;}

                         
                        input.seek(index3_0);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA3_5 = input.LA(1);

                         
                        int index3_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA3_5>='\u0000' && LA3_5<='z')||(LA3_5>='|' && LA3_5<='\uFFFE')) && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 7;}

                        else s = 8;

                         
                        input.seek(index3_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA3_2 = input.LA(1);

                         
                        int index3_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA3_2=='\"') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 5;}

                        else if ( (LA3_2=='\'') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 4;}

                        else if ( (LA3_2=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 3;}

                        else if ( ((LA3_2>='\u0000' && LA3_2<='!')||(LA3_2>='#' && LA3_2<='&')||(LA3_2>='(' && LA3_2<='[')||(LA3_2>=']' && LA3_2<='z')||(LA3_2>='|' && LA3_2<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 2;}

                         
                        input.seek(index3_2);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA3_6 = input.LA(1);

                         
                        int index3_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA3_6=='\'') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 4;}

                        else if ( (LA3_6=='\"') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 5;}

                        else if ( (LA3_6=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 3;}

                        else if ( ((LA3_6>='\u0000' && LA3_6<='!')||(LA3_6>='#' && LA3_6<='&')||(LA3_6>='(' && LA3_6<='[')||(LA3_6>=']' && LA3_6<='z')||(LA3_6>='|' && LA3_6<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 2;}

                         
                        input.seek(index3_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 3, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA4_eotS =
        "\10\uffff";
    static final String DFA4_eofS =
        "\10\uffff";
    static final String DFA4_minS =
        "\1\175\4\0\2\uffff\1\0";
    static final String DFA4_maxS =
        "\1\175\3\ufffe\1\0\2\uffff\1\ufffe";
    static final String DFA4_acceptS =
        "\5\uffff\1\2\1\1\1\uffff";
    static final String DFA4_specialS =
        "\1\2\1\3\1\5\1\0\1\4\2\uffff\1\1}>";
    static final String[] DFA4_transitionS = {
            "\1\1",
            "\42\2\1\5\4\2\1\6\64\2\1\3\36\2\1\4\uff83\2",
            "\42\2\1\5\4\2\1\6\64\2\1\3\36\2\1\4\uff83\2",
            "\uffff\7",
            "\1\uffff",
            "",
            "",
            "\42\2\1\5\4\2\1\6\64\2\1\3\36\2\1\4\uff83\2"
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "279:1: RBRACE_LBRACE_STRING_LITERAL : ({...}? => '}' DoubleQuoteBody '{' NextIsPercent[CUR_QUOTE_CTX] | {...}? => '}' SingleQuoteBody '{' NextIsPercent[CUR_QUOTE_CTX] );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA4_3 = input.LA(1);

                         
                        int index4_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA4_3>='\u0000' && LA4_3<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 7;}

                         
                        input.seek(index4_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA4_7 = input.LA(1);

                         
                        int index4_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA4_7=='{') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 4;}

                        else if ( ((LA4_7>='\u0000' && LA4_7<='!')||(LA4_7>='#' && LA4_7<='&')||(LA4_7>='(' && LA4_7<='[')||(LA4_7>=']' && LA4_7<='z')||(LA4_7>='|' && LA4_7<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 2;}

                        else if ( (LA4_7=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 3;}

                        else if ( (LA4_7=='\'') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 6;}

                        else if ( (LA4_7=='\"') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 5;}

                         
                        input.seek(index4_7);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA4_0 = input.LA(1);

                         
                        int index4_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA4_0=='}') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 1;}

                         
                        input.seek(index4_0);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA4_1 = input.LA(1);

                         
                        int index4_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA4_1>='\u0000' && LA4_1<='!')||(LA4_1>='#' && LA4_1<='&')||(LA4_1>='(' && LA4_1<='[')||(LA4_1>=']' && LA4_1<='z')||(LA4_1>='|' && LA4_1<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 2;}

                        else if ( (LA4_1=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 3;}

                        else if ( (LA4_1=='{') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 4;}

                        else if ( (LA4_1=='\"') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 5;}

                        else if ( (LA4_1=='\'') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 6;}

                         
                        input.seek(index4_1);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA4_4 = input.LA(1);

                         
                        int index4_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ( rightBraceLikeQuote(DBL_QUOTE_CTX) ) ) {s = 6;}

                        else if ( ( rightBraceLikeQuote(SNG_QUOTE_CTX) ) ) {s = 5;}

                         
                        input.seek(index4_4);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA4_2 = input.LA(1);

                         
                        int index4_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA4_2=='{') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 4;}

                        else if ( ((LA4_2>='\u0000' && LA4_2<='!')||(LA4_2>='#' && LA4_2<='&')||(LA4_2>='(' && LA4_2<='[')||(LA4_2>=']' && LA4_2<='z')||(LA4_2>='|' && LA4_2<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 2;}

                        else if ( (LA4_2=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 3;}

                        else if ( (LA4_2=='\"') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 5;}

                        else if ( (LA4_2=='\'') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 6;}

                         
                        input.seek(index4_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA13_eotS =
        "\6\uffff";
    static final String DFA13_eofS =
        "\6\uffff";
    static final String DFA13_minS =
        "\1\60\2\56\2\uffff\1\56";
    static final String DFA13_maxS =
        "\1\71\2\163\2\uffff\1\163";
    static final String DFA13_acceptS =
        "\3\uffff\1\1\1\2\1\uffff";
    static final String DFA13_specialS =
        "\6\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\1\11\2",
            "\1\4\1\uffff\12\4\56\uffff\1\3\4\uffff\1\3\5\uffff\1\3",
            "\1\4\1\uffff\12\5\56\uffff\1\3\4\uffff\1\3\5\uffff\1\3",
            "",
            "",
            "\1\4\1\uffff\12\5\56\uffff\1\3\4\uffff\1\3\5\uffff\1\3"
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "316:16: ( DECIMAL_LITERAL | Digits '.' ( Digits )? ( Exponent )? )";
        }
    }
    static final String DFA22_eotS =
        "\4\uffff\1\12\6\uffff\1\12\1\uffff";
    static final String DFA22_eofS =
        "\15\uffff";
    static final String DFA22_minS =
        "\3\56\1\uffff\2\56\1\uffff\2\56\2\uffff\1\56\1\uffff";
    static final String DFA22_maxS =
        "\1\71\2\145\1\uffff\1\56\1\145\1\uffff\2\145\2\uffff\1\56\1\uffff";
    static final String DFA22_acceptS =
        "\3\uffff\1\4\2\uffff\1\5\2\uffff\1\1\1\3\1\uffff\1\2";
    static final String DFA22_specialS =
        "\15\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\3\1\uffff\1\1\11\2",
            "\1\4\1\uffff\10\5\2\7\13\uffff\1\6\37\uffff\1\6",
            "\1\4\1\uffff\12\10\13\uffff\1\6\37\uffff\1\6",
            "",
            "\1\11",
            "\1\13\1\uffff\10\5\2\7\13\uffff\1\6\37\uffff\1\6",
            "",
            "\1\12\1\uffff\12\7\13\uffff\1\6\37\uffff\1\6",
            "\1\4\1\uffff\12\10\13\uffff\1\6\37\uffff\1\6",
            "",
            "",
            "\1\14",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "327:1: FLOATING_POINT_LITERAL : (d= DECIMAL_LITERAL RangeDots | d= OCTAL_LITERAL RangeDots | Digits '.' ( Digits )? ( Exponent )? | '.' Digits ( Exponent )? | Digits Exponent );";
        }
    }
    static final String DFA33_eotS =
        "\1\uffff\17\52\1\132\2\uffff\1\135\1\140\1\uffff\1\52\1\144\4\uffff"+
        "\1\150\1\152\1\155\1\157\1\163\1\165\5\uffff\1\175\2\u0084\3\uffff"+
        "\1\52\1\u008c\20\52\1\u009e\1\52\1\u00a5\6\52\1\u00ad\1\u00ae\17"+
        "\52\10\uffff\2\52\21\uffff\1\u00ca\13\uffff\1\u00cf\1\uffff\1\u00d3"+
        "\2\uffff\1\145\1\u00da\2\uffff\1\u0084\2\52\1\uffff\2\52\1\u00e1"+
        "\11\52\1\u00eb\4\52\1\uffff\6\52\1\uffff\1\u00f7\2\52\1\u00fa\1"+
        "\u00fb\2\52\2\uffff\20\52\1\u010e\3\52\1\u0112\6\52\16\uffff\1\145"+
        "\2\uffff\2\145\4\52\1\uffff\1\u0122\10\52\1\uffff\3\52\1\u012e\1"+
        "\52\1\u0130\3\52\1\u0134\1\52\1\uffff\1\u0136\1\u0137\2\uffff\1"+
        "\u0138\15\52\1\u0146\1\u0147\1\52\1\u0149\1\uffff\1\u014a\2\52\1"+
        "\uffff\2\52\1\u014f\1\u0150\2\52\4\uffff\1\145\3\52\1\u0156\1\uffff"+
        "\1\u0157\1\u0158\1\52\1\u015a\1\52\1\u015c\1\52\1\u015e\2\52\1\u0161"+
        "\1\uffff\1\52\1\uffff\3\52\1\uffff\1\52\3\uffff\12\52\1\u0171\2"+
        "\52\2\uffff\1\u0174\2\uffff\1\52\1\u0176\1\u0177\1\u0178\2\uffff"+
        "\3\52\1\u017c\1\52\3\uffff\1\u017e\1\uffff\1\52\1\uffff\1\u0180"+
        "\1\uffff\2\52\1\uffff\1\u0183\1\u0184\10\52\1\u018d\1\52\1\u018f"+
        "\2\52\1\uffff\1\u0192\1\u0193\1\uffff\1\u0194\3\uffff\3\52\1\uffff"+
        "\1\52\1\uffff\1\52\1\uffff\1\52\1\u019b\2\uffff\1\52\1\u019d\1\u019e"+
        "\1\52\1\u01a0\1\52\1\u01a2\1\52\1\uffff\1\52\1\uffff\1\u01a5\1\u01a6"+
        "\3\uffff\1\52\1\u01a8\1\u01a9\1\52\1\u01ab\1\u01ac\1\uffff\1\52"+
        "\2\uffff\1\u01ae\1\uffff\1\u01af\1\uffff\1\52\1\u01b1\2\uffff\1"+
        "\52\2\uffff\1\u01b3\2\uffff\1\52\2\uffff\1\u01b5\1\uffff\1\u01b6"+
        "\1\uffff\1\u01b7\3\uffff";
    static final String DFA33_eofS =
        "\u01b8\uffff";
    static final String DFA33_minS =
        "\1\11\1\142\1\145\1\141\1\145\1\141\1\146\1\141\1\145\1\156\1\141"+
        "\1\145\1\151\1\150\1\141\1\150\1\43\2\uffff\1\53\1\55\1\uffff\1"+
        "\154\1\56\4\uffff\2\75\1\74\1\75\1\52\1\0\2\uffff\2\0\1\uffff\1"+
        "\0\2\56\3\uffff\1\163\1\44\2\164\1\144\1\156\1\165\1\145\1\146\1"+
        "\141\1\156\1\164\2\154\1\162\2\156\1\157\1\44\1\160\1\44\1\164\1"+
        "\163\1\167\1\164\1\154\1\145\2\44\1\143\1\163\1\151\1\142\1\141"+
        "\1\160\1\172\1\141\1\145\1\165\1\160\1\145\1\162\1\145\1\164\10"+
        "\uffff\1\163\1\143\21\uffff\2\0\1\uffff\2\0\2\uffff\2\0\1\uffff"+
        "\3\0\1\uffff\1\0\2\uffff\1\60\2\56\1\uffff\1\56\1\164\1\145\1\uffff"+
        "\1\162\1\145\1\44\1\144\1\156\1\141\1\157\1\163\1\164\1\143\1\145"+
        "\1\163\1\44\1\143\1\141\1\163\1\155\1\uffff\1\157\1\164\2\145\1"+
        "\157\1\145\1\uffff\1\44\1\164\1\171\2\44\1\154\1\162\2\uffff\1\153"+
        "\1\164\1\166\1\164\1\154\1\144\1\165\1\154\3\145\1\164\1\160\1\163"+
        "\1\157\1\156\1\44\3\145\1\44\1\154\1\162\1\150\1\145\1\154\1\145"+
        "\1\0\1\uffff\3\0\2\uffff\2\0\2\uffff\2\0\1\uffff\1\60\1\53\1\uffff"+
        "\2\60\2\162\1\151\1\162\1\uffff\1\44\1\144\1\153\1\162\1\163\1\151"+
        "\1\150\1\164\1\145\1\uffff\1\164\1\154\1\164\1\44\1\162\1\44\1\162"+
        "\1\141\1\170\1\44\1\162\1\uffff\2\44\2\uffff\1\44\1\162\1\141\1"+
        "\151\1\141\1\145\1\151\1\157\1\162\1\141\2\162\1\157\1\151\2\44"+
        "\1\167\1\44\1\uffff\1\44\1\157\1\156\1\uffff\2\145\2\44\1\165\1"+
        "\156\1\uffff\2\0\2\60\1\141\1\164\1\142\1\44\1\uffff\2\44\1\145"+
        "\1\44\1\156\1\44\1\145\1\44\1\151\1\154\1\44\1\uffff\1\164\1\uffff"+
        "\1\164\1\156\1\157\1\uffff\1\163\3\uffff\1\151\1\147\1\156\1\164"+
        "\2\143\2\156\1\143\1\163\1\44\1\146\1\143\2\uffff\1\44\2\uffff\1"+
        "\146\3\44\2\uffff\1\163\1\144\1\143\1\44\1\165\3\uffff\1\44\1\uffff"+
        "\1\165\1\uffff\1\44\1\uffff\1\157\1\171\1\uffff\2\44\1\143\1\146"+
        "\1\145\1\144\1\145\1\151\1\145\1\164\1\44\1\154\1\44\2\145\1\uffff"+
        "\2\44\1\uffff\1\44\3\uffff\1\151\1\163\1\164\1\uffff\1\164\1\uffff"+
        "\1\145\1\uffff\1\156\1\44\2\uffff\1\145\2\44\1\145\1\44\1\164\1"+
        "\44\1\145\1\uffff\1\171\1\uffff\2\44\3\uffff\1\166\2\44\1\145\2"+
        "\44\1\uffff\1\157\2\uffff\1\44\1\uffff\1\44\1\uffff\1\144\1\44\2"+
        "\uffff\1\145\2\uffff\1\44\2\uffff\1\146\2\uffff\1\44\1\uffff\1\44"+
        "\1\uffff\1\44\3\uffff";
    static final String DFA33_maxS =
        "\1\ufaff\1\164\1\162\1\157\1\145\1\165\1\156\1\145\1\165\1\166\1"+
        "\165\1\145\1\165\1\171\1\141\1\151\1\43\2\uffff\2\75\1\uffff\1\170"+
        "\1\71\4\uffff\1\76\1\75\1\76\2\75\1\ufffe\2\uffff\2\ufffe\1\uffff"+
        "\1\ufffe\1\170\1\163\3\uffff\1\163\1\ufaff\2\164\1\144\1\156\1\165"+
        "\1\145\1\146\1\141\1\156\1\164\2\154\1\162\1\156\1\162\1\157\1\ufaff"+
        "\1\160\1\ufaff\1\164\1\172\1\167\1\164\1\154\1\145\2\ufaff\1\143"+
        "\1\163\1\157\1\142\1\166\1\160\1\172\1\145\1\162\1\171\1\160\1\145"+
        "\1\162\1\151\1\164\10\uffff\1\163\1\164\21\uffff\1\ufffe\1\0\1\uffff"+
        "\2\ufffe\2\uffff\2\ufffe\1\uffff\3\ufffe\1\uffff\1\ufffe\2\uffff"+
        "\1\163\2\145\1\uffff\1\163\1\164\1\145\1\uffff\1\162\1\145\1\ufaff"+
        "\1\144\1\156\1\141\1\157\1\163\1\164\1\143\1\145\1\163\1\ufaff\1"+
        "\143\1\141\1\163\1\155\1\uffff\1\157\2\164\1\145\1\157\1\145\1\uffff"+
        "\1\ufaff\1\164\1\171\2\ufaff\1\154\1\162\2\uffff\1\153\1\164\1\166"+
        "\1\164\1\154\1\144\1\165\1\154\3\145\1\164\1\160\1\163\1\157\1\156"+
        "\1\ufaff\3\145\1\ufaff\1\154\1\162\1\150\1\145\1\154\1\145\1\0\1"+
        "\uffff\3\ufffe\2\uffff\2\ufffe\2\uffff\2\ufffe\1\uffff\1\163\1\71"+
        "\1\uffff\2\163\2\162\1\151\1\162\1\uffff\1\ufaff\1\144\1\153\1\162"+
        "\1\163\1\151\1\150\1\164\1\145\1\uffff\1\164\1\154\1\164\1\ufaff"+
        "\1\162\1\ufaff\1\162\1\141\1\170\1\ufaff\1\162\1\uffff\2\ufaff\2"+
        "\uffff\1\ufaff\1\162\1\141\1\151\1\141\1\145\1\151\1\157\1\162\1"+
        "\141\2\162\1\157\1\151\2\ufaff\1\167\1\ufaff\1\uffff\1\ufaff\1\157"+
        "\1\156\1\uffff\2\145\2\ufaff\1\165\1\156\1\uffff\2\ufffe\1\71\1"+
        "\163\1\141\1\164\1\142\1\ufaff\1\uffff\2\ufaff\1\145\1\ufaff\1\156"+
        "\1\ufaff\1\145\1\ufaff\1\151\1\154\1\ufaff\1\uffff\1\164\1\uffff"+
        "\1\164\1\156\1\157\1\uffff\1\163\3\uffff\1\151\1\147\1\156\1\164"+
        "\2\143\2\156\1\143\1\163\1\ufaff\1\146\1\143\2\uffff\1\ufaff\2\uffff"+
        "\1\146\3\ufaff\2\uffff\1\163\1\144\1\143\1\ufaff\1\165\3\uffff\1"+
        "\ufaff\1\uffff\1\165\1\uffff\1\ufaff\1\uffff\1\157\1\171\1\uffff"+
        "\2\ufaff\1\143\1\146\1\145\1\144\1\145\1\151\1\145\1\164\1\ufaff"+
        "\1\154\1\ufaff\2\145\1\uffff\2\ufaff\1\uffff\1\ufaff\3\uffff\1\151"+
        "\1\163\1\164\1\uffff\1\164\1\uffff\1\145\1\uffff\1\156\1\ufaff\2"+
        "\uffff\1\145\2\ufaff\1\145\1\ufaff\1\164\1\ufaff\1\145\1\uffff\1"+
        "\171\1\uffff\2\ufaff\3\uffff\1\166\2\ufaff\1\145\2\ufaff\1\uffff"+
        "\1\157\2\uffff\1\ufaff\1\uffff\1\ufaff\1\uffff\1\144\1\ufaff\2\uffff"+
        "\1\145\2\uffff\1\ufaff\2\uffff\1\146\2\uffff\1\ufaff\1\uffff\1\ufaff"+
        "\1\uffff\1\ufaff\3\uffff";
    static final String DFA33_acceptS =
        "\21\uffff\1\47\1\50\2\uffff\1\53\2\uffff\1\110\1\111\1\112\1\113"+
        "\6\uffff\1\136\1\137\2\uffff\1\144\3\uffff\1\157\1\160\1\163\54"+
        "\uffff\1\151\1\46\1\51\1\131\1\124\1\52\1\132\1\125\2\uffff\1\107"+
        "\1\114\1\156\1\115\1\141\1\116\1\123\1\117\1\121\1\122\1\120\1\133"+
        "\1\126\1\134\1\161\1\162\1\127\2\uffff\1\150\2\uffff\1\142\1\143"+
        "\2\uffff\1\147\3\uffff\1\146\1\uffff\1\155\1\153\3\uffff\1\152\3"+
        "\uffff\1\56\21\uffff\1\15\6\uffff\1\67\7\uffff\1\76\1\77\34\uffff"+
        "\1\130\3\uffff\1\145\1\146\2\uffff\1\145\1\146\2\uffff\1\145\2\uffff"+
        "\1\154\6\uffff\1\55\11\uffff\1\13\13\uffff\1\21\2\uffff\1\22\1\23"+
        "\22\uffff\1\42\3\uffff\1\44\6\uffff\1\135\10\uffff\1\4\13\uffff"+
        "\1\66\1\uffff\1\17\3\uffff\1\72\1\uffff\1\74\1\75\1\24\15\uffff"+
        "\1\102\1\40\1\uffff\1\103\1\43\4\uffff\1\105\1\61\5\uffff\1\54\1"+
        "\5\1\6\1\uffff\1\7\1\uffff\1\60\1\uffff\1\12\2\uffff\1\65\17\uffff"+
        "\1\35\2\uffff\1\41\1\uffff\1\140\1\45\1\106\3\uffff\1\2\1\uffff"+
        "\1\57\1\uffff\1\11\2\uffff\1\16\1\20\10\uffff\1\32\1\uffff\1\34"+
        "\2\uffff\1\36\1\37\1\104\6\uffff\1\64\1\uffff\1\70\1\73\1\uffff"+
        "\1\26\1\uffff\1\30\2\uffff\1\100\1\101\1\uffff\1\63\1\1\1\uffff"+
        "\1\10\1\14\1\uffff\1\25\1\27\1\uffff\1\33\1\uffff\1\3\1\uffff\1"+
        "\31\1\62\1\71";
    static final String DFA33_specialS =
        "\1\20\40\uffff\1\0\5\uffff\1\5\114\uffff\1\4\1\1\10\uffff\1\11\1"+
        "\14\1\13\1\uffff\1\12\107\uffff\1\15\3\uffff\1\17\2\uffff\1\10\1"+
        "\7\2\uffff\1\3\1\2\103\uffff\1\16\1\6\u009c\uffff}>";
    static final String[] DFA33_transitionS = {
            "\2\53\1\uffff\2\53\22\uffff\1\53\1\uffff\1\44\1\20\1\52\1\41"+
            "\1\uffff\1\45\1\21\1\30\1\37\1\23\1\33\1\24\1\27\1\40\1\50\11"+
            "\51\1\42\1\32\1\36\1\34\1\35\1\43\1\uffff\32\52\1\22\1\uffff"+
            "\1\31\1\uffff\1\52\1\uffff\1\1\1\2\1\3\1\4\1\26\1\5\2\52\1\6"+
            "\2\52\1\7\1\52\1\10\1\11\1\12\1\52\1\13\1\14\1\15\1\52\1\16"+
            "\1\17\3\52\1\46\1\25\1\47\1\54\101\uffff\27\52\1\uffff\37\52"+
            "\1\uffff\u1f08\52\u1040\uffff\u0150\52\u0170\uffff\u0080\52"+
            "\u0080\uffff\u092e\52\u10d2\uffff\u5200\52\u5900\uffff\u0200"+
            "\52",
            "\1\55\3\uffff\1\60\7\uffff\1\61\4\uffff\1\56\1\57",
            "\1\65\3\uffff\1\62\5\uffff\1\63\2\uffff\1\64",
            "\1\70\12\uffff\1\66\2\uffff\1\67",
            "\1\71",
            "\1\72\7\uffff\1\75\5\uffff\1\73\2\uffff\1\76\2\uffff\1\74",
            "\1\77\6\uffff\1\100\1\101",
            "\1\103\3\uffff\1\102",
            "\1\104\11\uffff\1\105\5\uffff\1\106",
            "\1\110\3\uffff\1\111\3\uffff\1\107",
            "\1\112\15\uffff\1\113\2\uffff\1\114\2\uffff\1\115",
            "\1\116",
            "\1\120\12\uffff\1\121\1\117",
            "\1\122\11\uffff\1\123\4\uffff\1\125\1\uffff\1\124",
            "\1\126",
            "\1\127\1\130",
            "\1\131",
            "",
            "",
            "\1\133\21\uffff\1\134",
            "\1\136\17\uffff\1\137",
            "",
            "\1\141\13\uffff\1\142",
            "\1\143\1\uffff\12\145",
            "",
            "",
            "",
            "",
            "\1\146\1\147",
            "\1\151",
            "\1\52\1\154\1\153",
            "\1\156",
            "\1\161\4\uffff\1\162\15\uffff\1\160",
            "\40\166\1\uffff\34\166\1\164\uffc1\166",
            "",
            "",
            "\42\167\1\171\71\167\1\170\36\167\1\172\uff83\167",
            "\47\173\1\171\64\173\1\174\36\173\1\172\uff83\173",
            "",
            "\42\176\1\u0082\4\176\1\u0080\64\176\1\177\36\176\1\u0081\uff83"+
            "\176",
            "\1\u0085\1\uffff\10\u0086\2\u0087\13\uffff\1\145\22\uffff\1"+
            "\u0083\14\uffff\1\145\2\uffff\1\u0088\4\uffff\1\u0088\5\uffff"+
            "\1\u0088\4\uffff\1\u0083",
            "\1\u0085\1\uffff\12\u0089\13\uffff\1\145\37\uffff\1\145\2\uffff"+
            "\1\u0088\4\uffff\1\u0088\5\uffff\1\u0088",
            "",
            "",
            "",
            "\1\u008a",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\u008b\7\52\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52"+
            "\u1040\uffff\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e"+
            "\52\u10d2\uffff\u5200\52\u5900\uffff\u0200\52",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b\3\uffff\1\u009c",
            "\1\u009d",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u009f",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\3\52"+
            "\1\u00a2\4\52\1\u00a0\11\52\1\u00a1\1\u00a3\1\52\1\u00a4\4\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u00a6",
            "\1\u00a7\6\uffff\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1\5\uffff\1\u00b2",
            "\1\u00b3",
            "\1\u00b4\16\uffff\1\u00b6\3\uffff\1\u00b5\1\uffff\1\u00b7",
            "\1\u00b8",
            "\1\u00b9",
            "\1\u00ba\3\uffff\1\u00bb",
            "\1\u00be\3\uffff\1\u00bc\10\uffff\1\u00bd",
            "\1\u00c0\3\uffff\1\u00bf",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00c5\3\uffff\1\u00c4",
            "\1\u00c6",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00c7",
            "\1\u00c8\20\uffff\1\u00c9",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\40\166\1\uffff\uffde\166",
            "\1\uffff",
            "",
            "\42\167\1\171\71\167\1\170\36\167\1\172\uff83\167",
            "\uffff\u00cc",
            "",
            "",
            "\47\173\1\171\64\173\1\174\36\173\1\172\uff83\173",
            "\uffff\u00cd",
            "",
            "\42\176\1\u0082\4\176\1\u0080\64\176\1\177\36\176\1\u0081\uff83"+
            "\176",
            "\uffff\u00ce",
            "\42\u00d1\1\u00d3\71\u00d1\1\u00d2\36\u00d1\1\u00d0\uff83\u00d1",
            "",
            "\47\u00d5\1\u00d7\64\u00d5\1\u00d6\36\u00d5\1\u00d4\uff83\u00d5",
            "",
            "",
            "\12\u00d8\13\uffff\1\u00d9\37\uffff\1\u00d9\2\uffff\1\u0088"+
            "\4\uffff\1\u0088\5\uffff\1\u0088",
            "\1\u00db\1\uffff\10\u0086\2\u0087\13\uffff\1\145\37\uffff\1"+
            "\145",
            "\1\u00dc\1\uffff\12\u0087\13\uffff\1\145\37\uffff\1\145",
            "",
            "\1\u0085\1\uffff\12\u0089\13\uffff\1\145\37\uffff\1\145\2\uffff"+
            "\1\u0088\4\uffff\1\u0088\5\uffff\1\u0088",
            "\1\u00dd",
            "\1\u00de",
            "",
            "\1\u00df",
            "\1\u00e0",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u00e2",
            "\1\u00e3",
            "\1\u00e4",
            "\1\u00e5",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "\1\u00e9",
            "\1\u00ea",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u00ec",
            "\1\u00ed",
            "\1\u00ee",
            "\1\u00ef",
            "",
            "\1\u00f0",
            "\1\u00f1",
            "\1\u00f2\16\uffff\1\u00f3",
            "\1\u00f4",
            "\1\u00f5",
            "\1\u00f6",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u00f8",
            "\1\u00f9",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u00fc",
            "\1\u00fd",
            "",
            "",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\1\u010b",
            "\1\u010c",
            "\1\u010d",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0113",
            "\1\u0114",
            "\1\u0115",
            "\1\u0116",
            "\1\u0117",
            "\1\u0118",
            "\1\uffff",
            "",
            "\42\167\1\171\71\167\1\170\36\167\1\172\uff83\167",
            "\47\173\1\171\64\173\1\174\36\173\1\172\uff83\173",
            "\42\176\1\u0082\4\176\1\u0080\64\176\1\177\36\176\1\u0081\uff83"+
            "\176",
            "",
            "",
            "\42\u00d1\1\u00d3\71\u00d1\1\u00d2\36\u00d1\1\u00d0\uff83\u00d1",
            "\uffff\u011a",
            "",
            "",
            "\47\u00d5\1\u00d7\64\u00d5\1\u00d6\36\u00d5\1\u00d4\uff83\u00d5",
            "\uffff\u011b",
            "",
            "\12\u00d8\13\uffff\1\u00d9\37\uffff\1\u00d9\2\uffff\1\u0088"+
            "\4\uffff\1\u0088\5\uffff\1\u0088",
            "\1\u011c\1\uffff\1\u011c\2\uffff\12\u011d",
            "",
            "\12\u00d8\13\uffff\1\u00d9\37\uffff\1\u00d9\2\uffff\1\u0088"+
            "\4\uffff\1\u0088\5\uffff\1\u0088",
            "\12\u00d8\13\uffff\1\u00d9\37\uffff\1\u00d9\2\uffff\1\u0088"+
            "\4\uffff\1\u0088\5\uffff\1\u0088",
            "\1\u011e",
            "\1\u011f",
            "\1\u0120",
            "\1\u0121",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0123",
            "\1\u0124",
            "\1\u0125",
            "\1\u0126",
            "\1\u0127",
            "\1\u0128",
            "\1\u0129",
            "\1\u012a",
            "",
            "\1\u012b",
            "\1\u012c",
            "\1\u012d",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u012f",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0131",
            "\1\u0132",
            "\1\u0133",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0135",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0139",
            "\1\u013a",
            "\1\u013b",
            "\1\u013c",
            "\1\u013d",
            "\1\u013e",
            "\1\u013f",
            "\1\u0140",
            "\1\u0141",
            "\1\u0142",
            "\1\u0143",
            "\1\u0144",
            "\1\u0145",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0148",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u014b",
            "\1\u014c",
            "",
            "\1\u014d",
            "\1\u014e",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0151",
            "\1\u0152",
            "",
            "\42\u00d1\1\u00d3\71\u00d1\1\u00d2\36\u00d1\1\u00d0\uff83\u00d1",
            "\47\u00d5\1\u00d7\64\u00d5\1\u00d6\36\u00d5\1\u00d4\uff83\u00d5",
            "\12\u011d",
            "\12\u011d\56\uffff\1\u0088\4\uffff\1\u0088\5\uffff\1\u0088",
            "\1\u0153",
            "\1\u0154",
            "\1\u0155",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0159",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u015b",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u015d",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u015f",
            "\1\u0160",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\u0162",
            "",
            "\1\u0163",
            "\1\u0164",
            "\1\u0165",
            "",
            "\1\u0166",
            "",
            "",
            "",
            "\1\u0167",
            "\1\u0168",
            "\1\u0169",
            "\1\u016a",
            "\1\u016b",
            "\1\u016c",
            "\1\u016d",
            "\1\u016e",
            "\1\u016f",
            "\1\u0170",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0172",
            "\1\u0173",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "\1\u0175",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "\1\u0179",
            "\1\u017a",
            "\1\u017b",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u017d",
            "",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\u017f",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\u0181",
            "\1\u0182",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0185",
            "\1\u0186",
            "\1\u0187",
            "\1\u0188",
            "\1\u0189",
            "\1\u018a",
            "\1\u018b",
            "\1\u018c",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u018e",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u0190",
            "\1\u0191",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "",
            "\1\u0195",
            "\1\u0196",
            "\1\u0197",
            "",
            "\1\u0198",
            "",
            "\1\u0199",
            "",
            "\1\u019a",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "\1\u019c",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u019f",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u01a1",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u01a3",
            "",
            "\1\u01a4",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "",
            "\1\u01a7",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\u01aa",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\u01ad",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\u01b0",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "\1\u01b2",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            "\1\u01b4",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52"+
            "\105\uffff\27\52\1\uffff\37\52\1\uffff\u1f08\52\u1040\uffff"+
            "\u0150\52\u0170\uffff\u0080\52\u0080\uffff\u092e\52\u10d2\uffff"+
            "\u5200\52\u5900\uffff\u0200\52",
            "",
            "",
            ""
    };

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ABSTRACT | ASSERT | ATTRIBUTE | BIND | BOUND | BREAK | CLASS | CONTINUE | DELETE | FALSE | FOR | FUNCTION | IF | IMPORT | INIT | INSERT | LET | NEW | NOT | NULL | OVERRIDE | PACKAGE | POSTINIT | PRIVATE | PROTECTED | PUBLIC | READONLY | RETURN | SUPER | SIZEOF | STATIC | THIS | THROW | TRY | TRUE | VAR | WHILE | POUND | LPAREN | LBRACKET | PLUSPLUS | SUBSUB | PIPE | AFTER | AND | AS | BEFORE | CATCH | ELSE | EXCLUSIVE | EXTENDS | FINALLY | FIRST | FROM | IN | INDEXOF | INSTANCEOF | INTO | INVERSE | LAST | LAZY | ON | OR | REPLACE | REVERSE | STEP | THEN | TYPEOF | WITH | WHERE | DOTDOT | RPAREN | RBRACKET | SEMI | COMMA | DOT | EQEQ | EQ | GT | LT | LTGT | LTEQ | GTEQ | PLUS | SUB | STAR | SLASH | PERCENT | PLUSEQ | SUBEQ | STAREQ | SLASHEQ | PERCENTEQ | COLON | QUES | TWEEN | SUCHTHAT | STRING_LITERAL | QUOTE_LBRACE_STRING_LITERAL | LBRACE | RBRACE_QUOTE_STRING_LITERAL | RBRACE_LBRACE_STRING_LITERAL | RBRACE | FORMAT_STRING_LITERAL | TRANSLATION_KEY | TIME_LITERAL | DECIMAL_LITERAL | OCTAL_LITERAL | HEX_LITERAL | FLOATING_POINT_LITERAL | IDENTIFIER | WS | COMMENT | LINE_COMMENT | LAST_TOKEN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA33_33 = input.LA(1);

                         
                        int index33_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_33=='=') ) {s = 116;}

                        else if ( ((LA33_33>='\u0000' && LA33_33<='\u001F')||(LA33_33>='!' && LA33_33<='<')||(LA33_33>='>' && LA33_33<='\uFFFE')) && ( percentIsFormat() )) {s = 118;}

                        else s = 117;

                         
                        input.seek(index33_33);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA33_117 = input.LA(1);

                         
                        int index33_117 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!( percentIsFormat() )) ) {s = 203;}

                        else if ( ( percentIsFormat() ) ) {s = 118;}

                         
                        input.seek(index33_117);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA33_214 = input.LA(1);

                         
                        int index33_214 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA33_214>='\u0000' && LA33_214<='\uFFFE')) && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 283;}

                         
                        input.seek(index33_214);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA33_213 = input.LA(1);

                         
                        int index33_213 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_213=='{') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 212;}

                        else if ( ((LA33_213>='\u0000' && LA33_213<='&')||(LA33_213>='(' && LA33_213<='[')||(LA33_213>=']' && LA33_213<='z')||(LA33_213>='|' && LA33_213<='\uFFFE')) && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 213;}

                        else if ( (LA33_213=='\\') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 214;}

                        else if ( (LA33_213=='\'') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 215;}

                         
                        input.seek(index33_213);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA33_116 = input.LA(1);

                         
                        int index33_116 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA33_116>='\u0000' && LA33_116<='\u001F')||(LA33_116>='!' && LA33_116<='\uFFFE')) && ( percentIsFormat() )) {s = 118;}

                        else s = 202;

                         
                        input.seek(index33_116);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA33_39 = input.LA(1);

                         
                        int index33_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA33_39>='\u0000' && LA33_39<='!')||(LA33_39>='#' && LA33_39<='&')||(LA33_39>='(' && LA33_39<='[')||(LA33_39>=']' && LA33_39<='z')||(LA33_39>='|' && LA33_39<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 126;}

                        else if ( (LA33_39=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 127;}

                        else if ( (LA33_39=='\'') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 128;}

                        else if ( (LA33_39=='{') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 129;}

                        else if ( (LA33_39=='\"') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 130;}

                        else s = 125;

                         
                        input.seek(index33_39);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA33_283 = input.LA(1);

                         
                        int index33_283 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_283=='\'') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 215;}

                        else if ( ((LA33_283>='\u0000' && LA33_283<='&')||(LA33_283>='(' && LA33_283<='[')||(LA33_283>=']' && LA33_283<='z')||(LA33_283>='|' && LA33_283<='\uFFFE')) && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 213;}

                        else if ( (LA33_283=='\\') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 214;}

                        else if ( (LA33_283=='{') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 212;}

                         
                        input.seek(index33_283);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA33_210 = input.LA(1);

                         
                        int index33_210 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA33_210>='\u0000' && LA33_210<='\uFFFE')) && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 282;}

                         
                        input.seek(index33_210);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA33_209 = input.LA(1);

                         
                        int index33_209 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_209=='{') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 208;}

                        else if ( ((LA33_209>='\u0000' && LA33_209<='!')||(LA33_209>='#' && LA33_209<='[')||(LA33_209>=']' && LA33_209<='z')||(LA33_209>='|' && LA33_209<='\uFFFE')) && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 209;}

                        else if ( (LA33_209=='\\') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 210;}

                        else if ( (LA33_209=='\"') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 211;}

                         
                        input.seek(index33_209);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA33_126 = input.LA(1);

                         
                        int index33_126 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_126=='{') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 129;}

                        else if ( ((LA33_126>='\u0000' && LA33_126<='!')||(LA33_126>='#' && LA33_126<='&')||(LA33_126>='(' && LA33_126<='[')||(LA33_126>=']' && LA33_126<='z')||(LA33_126>='|' && LA33_126<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 126;}

                        else if ( (LA33_126=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 127;}

                        else if ( (LA33_126=='\"') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 130;}

                        else if ( (LA33_126=='\'') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 128;}

                         
                        input.seek(index33_126);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA33_130 = input.LA(1);

                         
                        int index33_130 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_130=='{') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 212;}

                        else if ( ((LA33_130>='\u0000' && LA33_130<='&')||(LA33_130>='(' && LA33_130<='[')||(LA33_130>=']' && LA33_130<='z')||(LA33_130>='|' && LA33_130<='\uFFFE')) && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 213;}

                        else if ( (LA33_130=='\\') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 214;}

                        else if ( (LA33_130=='\'') && ( rightBraceLikeQuote(SNG_QUOTE_CTX) )) {s = 215;}

                        else s = 211;

                         
                        input.seek(index33_130);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA33_128 = input.LA(1);

                         
                        int index33_128 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_128=='{') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 208;}

                        else if ( ((LA33_128>='\u0000' && LA33_128<='!')||(LA33_128>='#' && LA33_128<='[')||(LA33_128>=']' && LA33_128<='z')||(LA33_128>='|' && LA33_128<='\uFFFE')) && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 209;}

                        else if ( (LA33_128=='\\') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 210;}

                        else if ( (LA33_128=='\"') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 211;}

                        else s = 207;

                         
                        input.seek(index33_128);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA33_127 = input.LA(1);

                         
                        int index33_127 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA33_127>='\u0000' && LA33_127<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 206;}

                         
                        input.seek(index33_127);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA33_202 = input.LA(1);

                         
                        int index33_202 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!( percentIsFormat() )) ) {s = 281;}

                        else if ( ( percentIsFormat() ) ) {s = 118;}

                         
                        input.seek(index33_202);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA33_282 = input.LA(1);

                         
                        int index33_282 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_282=='\"') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 211;}

                        else if ( ((LA33_282>='\u0000' && LA33_282<='!')||(LA33_282>='#' && LA33_282<='[')||(LA33_282>=']' && LA33_282<='z')||(LA33_282>='|' && LA33_282<='\uFFFE')) && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 209;}

                        else if ( (LA33_282=='\\') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 210;}

                        else if ( (LA33_282=='{') && ( rightBraceLikeQuote(DBL_QUOTE_CTX) )) {s = 208;}

                         
                        input.seek(index33_282);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA33_206 = input.LA(1);

                         
                        int index33_206 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_206=='\"') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 130;}

                        else if ( (LA33_206=='\'') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 128;}

                        else if ( (LA33_206=='\\') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 127;}

                        else if ( ((LA33_206>='\u0000' && LA33_206<='!')||(LA33_206>='#' && LA33_206<='&')||(LA33_206>='(' && LA33_206<='[')||(LA33_206>=']' && LA33_206<='z')||(LA33_206>='|' && LA33_206<='\uFFFE')) && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 126;}

                        else if ( (LA33_206=='{') && (( rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 129;}

                         
                        input.seek(index33_206);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA33_0 = input.LA(1);

                         
                        int index33_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_0=='a') ) {s = 1;}

                        else if ( (LA33_0=='b') ) {s = 2;}

                        else if ( (LA33_0=='c') ) {s = 3;}

                        else if ( (LA33_0=='d') ) {s = 4;}

                        else if ( (LA33_0=='f') ) {s = 5;}

                        else if ( (LA33_0=='i') ) {s = 6;}

                        else if ( (LA33_0=='l') ) {s = 7;}

                        else if ( (LA33_0=='n') ) {s = 8;}

                        else if ( (LA33_0=='o') ) {s = 9;}

                        else if ( (LA33_0=='p') ) {s = 10;}

                        else if ( (LA33_0=='r') ) {s = 11;}

                        else if ( (LA33_0=='s') ) {s = 12;}

                        else if ( (LA33_0=='t') ) {s = 13;}

                        else if ( (LA33_0=='v') ) {s = 14;}

                        else if ( (LA33_0=='w') ) {s = 15;}

                        else if ( (LA33_0=='#') ) {s = 16;}

                        else if ( (LA33_0=='(') ) {s = 17;}

                        else if ( (LA33_0=='[') ) {s = 18;}

                        else if ( (LA33_0=='+') ) {s = 19;}

                        else if ( (LA33_0=='-') ) {s = 20;}

                        else if ( (LA33_0=='|') ) {s = 21;}

                        else if ( (LA33_0=='e') ) {s = 22;}

                        else if ( (LA33_0=='.') ) {s = 23;}

                        else if ( (LA33_0==')') ) {s = 24;}

                        else if ( (LA33_0==']') ) {s = 25;}

                        else if ( (LA33_0==';') ) {s = 26;}

                        else if ( (LA33_0==',') ) {s = 27;}

                        else if ( (LA33_0=='=') ) {s = 28;}

                        else if ( (LA33_0=='>') ) {s = 29;}

                        else if ( (LA33_0=='<') ) {s = 30;}

                        else if ( (LA33_0=='*') ) {s = 31;}

                        else if ( (LA33_0=='/') ) {s = 32;}

                        else if ( (LA33_0=='%') ) {s = 33;}

                        else if ( (LA33_0==':') ) {s = 34;}

                        else if ( (LA33_0=='?') ) {s = 35;}

                        else if ( (LA33_0=='\"') ) {s = 36;}

                        else if ( (LA33_0=='\'') ) {s = 37;}

                        else if ( (LA33_0=='{') ) {s = 38;}

                        else if ( (LA33_0=='}') && (( !rightBraceLikeQuote(CUR_QUOTE_CTX) || rightBraceLikeQuote(DBL_QUOTE_CTX) || rightBraceLikeQuote(SNG_QUOTE_CTX) ))) {s = 39;}

                        else if ( (LA33_0=='0') ) {s = 40;}

                        else if ( ((LA33_0>='1' && LA33_0<='9')) ) {s = 41;}

                        else if ( (LA33_0=='$'||(LA33_0>='A' && LA33_0<='Z')||LA33_0=='_'||(LA33_0>='g' && LA33_0<='h')||(LA33_0>='j' && LA33_0<='k')||LA33_0=='m'||LA33_0=='q'||LA33_0=='u'||(LA33_0>='x' && LA33_0<='z')||(LA33_0>='\u00C0' && LA33_0<='\u00D6')||(LA33_0>='\u00D8' && LA33_0<='\u00F6')||(LA33_0>='\u00F8' && LA33_0<='\u1FFF')||(LA33_0>='\u3040' && LA33_0<='\u318F')||(LA33_0>='\u3300' && LA33_0<='\u337F')||(LA33_0>='\u3400' && LA33_0<='\u3D2D')||(LA33_0>='\u4E00' && LA33_0<='\u9FFF')||(LA33_0>='\uF900' && LA33_0<='\uFAFF')) ) {s = 42;}

                        else if ( ((LA33_0>='\t' && LA33_0<='\n')||(LA33_0>='\f' && LA33_0<='\r')||LA33_0==' ') ) {s = 43;}

                        else if ( (LA33_0=='~') ) {s = 44;}

                         
                        input.seek(index33_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 33, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}
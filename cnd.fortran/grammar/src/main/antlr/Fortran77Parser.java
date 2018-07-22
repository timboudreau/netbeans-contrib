// $ANTLR 2.7.7 (20060906): "f77-antlr2.g" -> "Fortran77Parser.java"$

package org.netbeans.modules.fortran.generated;
import org.netbeans.modules.fortran.ast.TokenAST;
import antlr.CommonToken;
import java.util.HashSet;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class Fortran77Parser extends antlr.LLkParser       implements Fortran77TokenTypes
 {

        private String progName = new String("");
        HashSet<String> variables = new HashSet<String>();
        public String getProgName(){
              return progName;
        }
        public HashSet<String> getVariables(){
               return variables;
        }
	private AST createNewNode(int type, String text, int line, int column)
	{
		Token t = new CommonToken(type, text);
		t.setLine(line);
		t.setColumn(column);
		return new TokenAST(t);
	}

protected Fortran77Parser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Fortran77Parser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected Fortran77Parser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Fortran77Parser(TokenStream lexer) {
  this(lexer,2);
}

public Fortran77Parser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST program_AST = null;
		
		{
		int _cnt7=0;
		_loop7:
		do {
			boolean synPredMatched4 = false;
			if (((_tokenSet_0.member(LA(1))) && (_tokenSet_1.member(LA(2))))) {
				int _m4 = mark();
				synPredMatched4 = true;
				inputState.guessing++;
				try {
					{
					matchNot(COMMENT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched4 = false;
				}
				rewind(_m4);
inputState.guessing--;
			}
			if ( synPredMatched4 ) {
				executableUnit();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==COMMENT) && (_tokenSet_2.member(LA(2)))) {
				{
				int _cnt6=0;
				_loop6:
				do {
					if ((LA(1)==COMMENT) && (_tokenSet_2.member(LA(2)))) {
						AST tmp1_AST = null;
						tmp1_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp1_AST);
						match(COMMENT);
					}
					else {
						if ( _cnt6>=1 ) { break _loop6; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt6++;
				} while (true);
				}
			}
			else {
				if ( _cnt7>=1 ) { break _loop7; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt7++;
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			program_AST = (AST)currentAST.root;
			program_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CODEROOT,"[program]")).add(program_AST)) ;
			currentAST.root = program_AST;
			currentAST.child = program_AST!=null &&program_AST.getFirstChild()!=null ?
				program_AST.getFirstChild() : program_AST;
			currentAST.advanceChildToEnd();
		}
		program_AST = (AST)currentAST.root;
		returnAST = program_AST;
	}
	
	public final void executableUnit() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST executableUnit_AST = null;
		
		boolean synPredMatched10 = false;
		if (((_tokenSet_3.member(LA(1))) && (LA(2)==NAME||LA(2)==FUNCTION||LA(2)==STAR||LA(2)==COMPLEX||LA(2)==PRECISION))) {
			int _m10 = mark();
			synPredMatched10 = true;
			inputState.guessing++;
			try {
				{
				functionStatement();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched10 = false;
			}
			rewind(_m10);
inputState.guessing--;
		}
		if ( synPredMatched10 ) {
			functionSubprogram();
			astFactory.addASTChild(currentAST, returnAST);
			executableUnit_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
			mainProgram();
			astFactory.addASTChild(currentAST, returnAST);
			executableUnit_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==SUBROUTINE)) {
			subroutineSubprogram();
			astFactory.addASTChild(currentAST, returnAST);
			executableUnit_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==BLOCK)) {
			blockdataSubprogram();
			astFactory.addASTChild(currentAST, returnAST);
			executableUnit_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = executableUnit_AST;
	}
	
	public final void functionStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionStatement_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		{
		if ((_tokenSet_6.member(LA(1)))) {
			type();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==FUNCTION)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		AST tmp2_AST = null;
		tmp2_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp2_AST);
		match(FUNCTION);
		s = LT(1);
		s_AST = astFactory.create(s);
		astFactory.addASTChild(currentAST, s_AST);
		match(NAME);
		match(LPAREN);
		{
		if ((LA(1)==NAME||LA(1)==LITERAL_real)) {
			namelist();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==RPAREN)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(RPAREN);
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		functionStatement_AST = (AST)currentAST.root;
		returnAST = functionStatement_AST;
	}
	
	public final void functionSubprogram() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionSubprogram_AST = null;
		AST s_AST = null;
		AST b_AST = null;
		
		functionStatement();
		s_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		subprogramBody();
		b_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			functionSubprogram_AST = (AST)currentAST.root;
			
					s_AST.addChild(b_AST);
					functionSubprogram_AST = s_AST;
				
			currentAST.root = functionSubprogram_AST;
			currentAST.child = functionSubprogram_AST!=null &&functionSubprogram_AST.getFirstChild()!=null ?
				functionSubprogram_AST.getFirstChild() : functionSubprogram_AST;
			currentAST.advanceChildToEnd();
		}
		functionSubprogram_AST = (AST)currentAST.root;
		returnAST = functionSubprogram_AST;
	}
	
	public final void mainProgram() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mainProgram_AST = null;
		AST s_AST = null;
		AST b_AST = null;
		
		{
		if ((LA(1)==PROGRAM)) {
			programStatement();
			s_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((_tokenSet_7.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		subprogramBody();
		b_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			mainProgram_AST = (AST)currentAST.root;
			
					if (s_AST != null) {
						s_AST.addChild(b_AST);
						mainProgram_AST = s_AST;
					} else {
						mainProgram_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LITERAL_program,"program")).add(astFactory.create(NAME,"main")).add(b_AST));
					}
				
			currentAST.root = mainProgram_AST;
			currentAST.child = mainProgram_AST!=null &&mainProgram_AST.getFirstChild()!=null ?
				mainProgram_AST.getFirstChild() : mainProgram_AST;
			currentAST.advanceChildToEnd();
		}
		mainProgram_AST = (AST)currentAST.root;
		returnAST = mainProgram_AST;
	}
	
	public final void subroutineSubprogram() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subroutineSubprogram_AST = null;
		AST s_AST = null;
		AST b_AST = null;
		
		subroutineStatement();
		s_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		subprogramBody();
		b_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			subroutineSubprogram_AST = (AST)currentAST.root;
			
					s_AST.addChild(b_AST);
					subroutineSubprogram_AST = s_AST;
				
			currentAST.root = subroutineSubprogram_AST;
			currentAST.child = subroutineSubprogram_AST!=null &&subroutineSubprogram_AST.getFirstChild()!=null ?
				subroutineSubprogram_AST.getFirstChild() : subroutineSubprogram_AST;
			currentAST.advanceChildToEnd();
		}
		subroutineSubprogram_AST = (AST)currentAST.root;
		returnAST = subroutineSubprogram_AST;
	}
	
	public final void blockdataSubprogram() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST blockdataSubprogram_AST = null;
		AST s_AST = null;
		AST b_AST = null;
		
		blockdataStatement();
		s_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		subprogramBody();
		b_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			blockdataSubprogram_AST = (AST)currentAST.root;
			
					s_AST.addChild(b_AST);
					blockdataSubprogram_AST = s_AST;
				
			currentAST.root = blockdataSubprogram_AST;
			currentAST.child = blockdataSubprogram_AST!=null &&blockdataSubprogram_AST.getFirstChild()!=null ?
				blockdataSubprogram_AST.getFirstChild() : blockdataSubprogram_AST;
			currentAST.advanceChildToEnd();
		}
		blockdataSubprogram_AST = (AST)currentAST.root;
		returnAST = blockdataSubprogram_AST;
	}
	
	public final void programStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST programStatement_AST = null;
		Token  var1 = null;
		AST var1_AST = null;
		
		AST tmp5_AST = null;
		tmp5_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp5_AST);
		match(PROGRAM);
		var1 = LT(1);
		var1_AST = astFactory.create(var1);
		astFactory.addASTChild(currentAST, var1_AST);
		match(NAME);
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		if ( inputState.guessing==0 ) {
			
			progName = var1.getText();
			
		}
		programStatement_AST = (AST)currentAST.root;
		returnAST = programStatement_AST;
	}
	
	public final void subprogramBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subprogramBody_AST = null;
		
		{
		_loop38:
		do {
			if ((_tokenSet_8.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
				wholeStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop38;
			}
			
		} while (true);
		}
		endStatement();
		astFactory.addASTChild(currentAST, returnAST);
		if ( inputState.guessing==0 ) {
			subprogramBody_AST = (AST)currentAST.root;
			subprogramBody_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SUBPROGRAMBLOCK,"[subprogramBody]")).add(subprogramBody_AST));
			currentAST.root = subprogramBody_AST;
			currentAST.child = subprogramBody_AST!=null &&subprogramBody_AST.getFirstChild()!=null ?
				subprogramBody_AST.getFirstChild() : subprogramBody_AST;
			currentAST.advanceChildToEnd();
		}
		subprogramBody_AST = (AST)currentAST.root;
		returnAST = subprogramBody_AST;
	}
	
	public final void subroutineStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subroutineStatement_AST = null;
		
		AST tmp6_AST = null;
		tmp6_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp6_AST);
		match(SUBROUTINE);
		AST tmp7_AST = null;
		tmp7_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp7_AST);
		match(NAME);
		{
		if ((LA(1)==LPAREN)) {
			match(LPAREN);
			{
			if ((LA(1)==NAME||LA(1)==LITERAL_real)) {
				namelist();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==RPAREN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(RPAREN);
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		subroutineStatement_AST = (AST)currentAST.root;
		returnAST = subroutineStatement_AST;
	}
	
	public final void blockdataStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST blockdataStatement_AST = null;
		
		AST tmp10_AST = null;
		tmp10_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp10_AST);
		match(BLOCK);
		AST tmp11_AST = null;
		tmp11_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp11_AST);
		match(NAME);
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		blockdataStatement_AST = (AST)currentAST.root;
		returnAST = blockdataStatement_AST;
	}
	
	public final void otherSpecificationStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST otherSpecificationStatement_AST = null;
		
		switch ( LA(1)) {
		case DIMENSION:
		{
			dimensionStatement();
			astFactory.addASTChild(currentAST, returnAST);
			otherSpecificationStatement_AST = (AST)currentAST.root;
			break;
		}
		case EQUIVALENCE:
		{
			equivalenceStatement();
			astFactory.addASTChild(currentAST, returnAST);
			otherSpecificationStatement_AST = (AST)currentAST.root;
			break;
		}
		case INTRINSIC:
		{
			intrinsicStatement();
			astFactory.addASTChild(currentAST, returnAST);
			otherSpecificationStatement_AST = (AST)currentAST.root;
			break;
		}
		case SAVE:
		{
			saveStatement();
			astFactory.addASTChild(currentAST, returnAST);
			otherSpecificationStatement_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = otherSpecificationStatement_AST;
	}
	
	public final void dimensionStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dimensionStatement_AST = null;
		
		AST tmp12_AST = null;
		tmp12_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp12_AST);
		match(DIMENSION);
		arrayDeclarators();
		astFactory.addASTChild(currentAST, returnAST);
		dimensionStatement_AST = (AST)currentAST.root;
		returnAST = dimensionStatement_AST;
	}
	
	public final void equivalenceStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equivalenceStatement_AST = null;
		
		AST tmp13_AST = null;
		tmp13_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp13_AST);
		match(EQUIVALENCE);
		equivEntityGroup();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop57:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				equivEntityGroup();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop57;
			}
			
		} while (true);
		}
		equivalenceStatement_AST = (AST)currentAST.root;
		returnAST = equivalenceStatement_AST;
	}
	
	public final void intrinsicStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST intrinsicStatement_AST = null;
		
		AST tmp15_AST = null;
		tmp15_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp15_AST);
		match(INTRINSIC);
		namelist();
		astFactory.addASTChild(currentAST, returnAST);
		intrinsicStatement_AST = (AST)currentAST.root;
		returnAST = intrinsicStatement_AST;
	}
	
	public final void saveStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST saveStatement_AST = null;
		
		AST tmp16_AST = null;
		tmp16_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp16_AST);
		match(SAVE);
		{
		if ((LA(1)==NAME||LA(1)==DIV)) {
			saveEntity();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop122:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					saveEntity();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop122;
				}
				
			} while (true);
			}
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		saveStatement_AST = (AST)currentAST.root;
		returnAST = saveStatement_AST;
	}
	
	public final void executableStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST executableStatement_AST = null;
		
		{
		switch ( LA(1)) {
		case NAME:
		case ASSIGN:
		case LITERAL_real:
		{
			assignmentStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case GOTO:
		case GO:
		{
			gotoStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case IF:
		{
			ifStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case DO:
		{
			doStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case CONTINUE:
		{
			continueStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case STOP:
		{
			stopStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case PAUSE:
		{
			pauseStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case READ:
		{
			readStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case WRITE:
		{
			writeStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case PRINT:
		{
			printStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case REWIND:
		{
			rewindStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case BACKSPACE:
		{
			backspaceStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case OPEN:
		{
			openStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case CLOSE:
		{
			closeStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case ENDFILE:
		{
			endfileStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case INQUIRE:
		{
			inquireStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case CALL:
		{
			callStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case RETURN:
		{
			returnStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		executableStatement_AST = (AST)currentAST.root;
		returnAST = executableStatement_AST;
	}
	
	public final void assignmentStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentStatement_AST = null;
		
		if ((LA(1)==NAME||LA(1)==LITERAL_real)) {
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp18_AST = null;
			tmp18_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp18_AST);
			match(ASSIGN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			assignmentStatement_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==ASSIGN)) {
			AST tmp19_AST = null;
			tmp19_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp19_AST);
			match(ASSIGN);
			AST tmp20_AST = null;
			tmp20_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp20_AST);
			match(ICON);
			to();
			variableName();
			astFactory.addASTChild(currentAST, returnAST);
			assignmentStatement_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = assignmentStatement_AST;
	}
	
	public final void gotoStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST gotoStatement_AST = null;
		
		{
		if ((LA(1)==GOTO)) {
			AST tmp21_AST = null;
			tmp21_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp21_AST);
			match(GOTO);
		}
		else if ((LA(1)==GO)) {
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp22_AST);
			match(GO);
			to();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		switch ( LA(1)) {
		case ICON:
		{
			unconditionalGoto();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case LPAREN:
		{
			computedGoto();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case NAME:
		{
			assignedGoto();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		gotoStatement_AST = (AST)currentAST.root;
		returnAST = gotoStatement_AST;
	}
	
	public final void ifStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ifStatement_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		i = LT(1);
		i_AST = astFactory.create(i);
		astFactory.makeASTRoot(currentAST, i_AST);
		match(IF);
		match(LPAREN);
		logicalExpression();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		{
		switch ( LA(1)) {
		case THEN:
		{
			blockIfStatement();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case NAME:
		case ASSIGN:
		case GOTO:
		case GO:
		case IF:
		case DO:
		case CONTINUE:
		case STOP:
		case PAUSE:
		case WRITE:
		case READ:
		case PRINT:
		case OPEN:
		case CLOSE:
		case INQUIRE:
		case BACKSPACE:
		case ENDFILE:
		case REWIND:
		case CALL:
		case RETURN:
		case LITERAL_real:
		{
			logicalIfStatement(i);
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case ICON:
		{
			arithmeticIfStatement();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				i_AST.setType(AIF);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		ifStatement_AST = (AST)currentAST.root;
		returnAST = ifStatement_AST;
	}
	
	public final void doStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST doStatement_AST = null;
		Token  d = null;
		AST d_AST = null;
		
		d = LT(1);
		d_AST = astFactory.create(d);
		astFactory.makeASTRoot(currentAST, d_AST);
		match(DO);
		{
		if ((LA(1)==ICON)) {
			doWithLabel();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==NAME)) {
			doWithEndDo(d);
			astFactory.addASTChild(currentAST, returnAST);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		doStatement_AST = (AST)currentAST.root;
		returnAST = doStatement_AST;
	}
	
	public final void continueStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST continueStatement_AST = null;
		
		AST tmp25_AST = null;
		tmp25_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp25_AST);
		match(CONTINUE);
		continueStatement_AST = (AST)currentAST.root;
		returnAST = continueStatement_AST;
	}
	
	public final void stopStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST stopStatement_AST = null;
		
		AST tmp26_AST = null;
		tmp26_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp26_AST);
		match(STOP);
		{
		switch ( LA(1)) {
		case ICON:
		{
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp27_AST);
			match(ICON);
			break;
		}
		case HOLLERITH:
		{
			AST tmp28_AST = null;
			tmp28_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp28_AST);
			match(HOLLERITH);
			break;
		}
		case EOS:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		stopStatement_AST = (AST)currentAST.root;
		returnAST = stopStatement_AST;
	}
	
	public final void pauseStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pauseStatement_AST = null;
		
		AST tmp29_AST = null;
		tmp29_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp29_AST);
		match(PAUSE);
		{
		if ((LA(1)==ICON)) {
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp30_AST);
			match(ICON);
		}
		else if ((LA(1)==HOLLERITH)) {
			AST tmp31_AST = null;
			tmp31_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp31_AST);
			match(HOLLERITH);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		pauseStatement_AST = (AST)currentAST.root;
		returnAST = pauseStatement_AST;
	}
	
	public final void readStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST readStatement_AST = null;
		
		AST tmp32_AST = null;
		tmp32_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp32_AST);
		match(READ);
		{
		boolean synPredMatched204 = false;
		if (((_tokenSet_9.member(LA(1))) && (_tokenSet_10.member(LA(2))))) {
			int _m204 = mark();
			synPredMatched204 = true;
			inputState.guessing++;
			try {
				{
				formatIdentifier();
				{
				if ((LA(1)==COMMA)) {
					match(COMMA);
					ioList();
				}
				else if ((LA(1)==EOS)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				match(EOS);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched204 = false;
			}
			rewind(_m204);
inputState.guessing--;
		}
		if ( synPredMatched204 ) {
			{
			formatIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			{
			if ((LA(1)==COMMA)) {
				AST tmp33_AST = null;
				tmp33_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp33_AST);
				match(COMMA);
				ioList();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==EOS)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			}
		}
		else if ((LA(1)==LPAREN) && (_tokenSet_11.member(LA(2)))) {
			AST tmp34_AST = null;
			tmp34_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp34_AST);
			match(LPAREN);
			controlInfoList();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp35_AST = null;
			tmp35_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp35_AST);
			match(RPAREN);
			{
			if ((_tokenSet_12.member(LA(1)))) {
				ioList();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==EOS)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		readStatement_AST = (AST)currentAST.root;
		returnAST = readStatement_AST;
	}
	
	public final void writeStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST writeStatement_AST = null;
		
		AST tmp36_AST = null;
		tmp36_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp36_AST);
		match(WRITE);
		AST tmp37_AST = null;
		tmp37_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp37_AST);
		match(LPAREN);
		controlInfoList();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp38_AST = null;
		tmp38_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp38_AST);
		match(RPAREN);
		{
		if ((_tokenSet_12.member(LA(1)))) {
			ioList();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		writeStatement_AST = (AST)currentAST.root;
		returnAST = writeStatement_AST;
	}
	
	public final void printStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST printStatement_AST = null;
		
		AST tmp39_AST = null;
		tmp39_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp39_AST);
		match(PRINT);
		formatIdentifier();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==COMMA)) {
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp40_AST);
			match(COMMA);
			ioList();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		printStatement_AST = (AST)currentAST.root;
		returnAST = printStatement_AST;
	}
	
	public final void rewindStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rewindStatement_AST = null;
		
		AST tmp41_AST = null;
		tmp41_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp41_AST);
		match(REWIND);
		berFinish();
		astFactory.addASTChild(currentAST, returnAST);
		rewindStatement_AST = (AST)currentAST.root;
		returnAST = rewindStatement_AST;
	}
	
	public final void backspaceStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST backspaceStatement_AST = null;
		
		AST tmp42_AST = null;
		tmp42_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp42_AST);
		match(BACKSPACE);
		berFinish();
		astFactory.addASTChild(currentAST, returnAST);
		backspaceStatement_AST = (AST)currentAST.root;
		returnAST = backspaceStatement_AST;
	}
	
	public final void openStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST openStatement_AST = null;
		
		AST tmp43_AST = null;
		tmp43_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp43_AST);
		match(OPEN);
		match(LPAREN);
		openControl();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop228:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				openControl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop228;
			}
			
		} while (true);
		}
		match(RPAREN);
		openStatement_AST = (AST)currentAST.root;
		returnAST = openStatement_AST;
	}
	
	public final void closeStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST closeStatement_AST = null;
		
		AST tmp47_AST = null;
		tmp47_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp47_AST);
		match(CLOSE);
		match(LPAREN);
		closeControl();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop256:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				closeControl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop256;
			}
			
		} while (true);
		}
		match(RPAREN);
		closeStatement_AST = (AST)currentAST.root;
		returnAST = closeStatement_AST;
	}
	
	public final void endfileStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST endfileStatement_AST = null;
		
		AST tmp51_AST = null;
		tmp51_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp51_AST);
		match(ENDFILE);
		berFinish();
		astFactory.addASTChild(currentAST, returnAST);
		endfileStatement_AST = (AST)currentAST.root;
		returnAST = endfileStatement_AST;
	}
	
	public final void inquireStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inquireStatement_AST = null;
		
		AST tmp52_AST = null;
		tmp52_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp52_AST);
		match(INQUIRE);
		match(LPAREN);
		inquireControl();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop260:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				inquireControl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop260;
			}
			
		} while (true);
		}
		match(RPAREN);
		inquireStatement_AST = (AST)currentAST.root;
		returnAST = inquireStatement_AST;
	}
	
	public final void callStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST callStatement_AST = null;
		
		AST tmp56_AST = null;
		tmp56_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp56_AST);
		match(CALL);
		subroutineCall();
		astFactory.addASTChild(currentAST, returnAST);
		callStatement_AST = (AST)currentAST.root;
		returnAST = callStatement_AST;
	}
	
	public final void returnStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST returnStatement_AST = null;
		
		AST tmp57_AST = null;
		tmp57_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp57_AST);
		match(RETURN);
		{
		if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==ICON||LA(1)==MINUS||LA(1)==PLUS)) {
			integerExpr();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		returnStatement_AST = (AST)currentAST.root;
		returnAST = returnStatement_AST;
	}
	
	public final void seos() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST seos_AST = null;
		
		match(EOS);
		seos_AST = (AST)currentAST.root;
		returnAST = seos_AST;
	}
	
	public final void entryStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST entryStatement_AST = null;
		
		AST tmp59_AST = null;
		tmp59_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp59_AST);
		match(ENTRY);
		AST tmp60_AST = null;
		tmp60_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp60_AST);
		match(NAME);
		{
		if ((LA(1)==LPAREN)) {
			match(LPAREN);
			namelist();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		entryStatement_AST = (AST)currentAST.root;
		returnAST = entryStatement_AST;
	}
	
	public final void namelist() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namelist_AST = null;
		
		identifier();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop32:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				identifier();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop32;
			}
			
		} while (true);
		}
		namelist_AST = (AST)currentAST.root;
		returnAST = namelist_AST;
	}
	
	public final void type() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		
		if ((LA(1)==REAL||LA(1)==COMPLEX||LA(1)==DOUBLE||LA(1)==INTEGER||LA(1)==LOGICAL)) {
			typename();
			astFactory.addASTChild(currentAST, returnAST);
			type_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==CHARACTER)) {
			characterWithLen();
			astFactory.addASTChild(currentAST, returnAST);
			type_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = type_AST;
	}
	
	public final void identifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identifier_AST = null;
		Token  s = null;
		AST s_AST = null;
		Token id = LT(1);
		
		if ((LA(1)==NAME)) {
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(NAME);
			if ( inputState.guessing==0 ) {
				variables.add(s.getText());
			}
			identifier_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==LITERAL_real)) {
			{
			AST tmp64_AST = null;
			tmp64_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp64_AST);
			match(LITERAL_real);
			}
			if ( inputState.guessing==0 ) {
				id.setType(NAME);
			}
			identifier_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = identifier_AST;
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		switch ( LA(1)) {
		case FORMAT:
		{
			formatStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case ENTRY:
		{
			entryStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case IMPLICIT:
		{
			implicitStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case PARAMETER:
		{
			parameterStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case REAL:
		case COMPLEX:
		case DOUBLE:
		case INTEGER:
		case LOGICAL:
		case CHARACTER:
		{
			typeStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case COMMON:
		{
			commonStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case POINTER:
		{
			pointerStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case EXTERNAL:
		{
			externalStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case DIMENSION:
		case EQUIVALENCE:
		case INTRINSIC:
		case SAVE:
		{
			otherSpecificationStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case DATA:
		{
			dataStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LET:
		{
			statementFunctionStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case NAME:
		case ASSIGN:
		case GOTO:
		case GO:
		case IF:
		case DO:
		case CONTINUE:
		case STOP:
		case PAUSE:
		case WRITE:
		case READ:
		case PRINT:
		case OPEN:
		case CLOSE:
		case INQUIRE:
		case BACKSPACE:
		case ENDFILE:
		case REWIND:
		case CALL:
		case RETURN:
		case LITERAL_real:
		{
			executableStatement();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = statement_AST;
	}
	
	public final void formatStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formatStatement_AST = null;
		
		AST tmp65_AST = null;
		tmp65_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp65_AST);
		match(FORMAT);
		match(LPAREN);
		fmtSpec();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		formatStatement_AST = (AST)currentAST.root;
		returnAST = formatStatement_AST;
	}
	
	public final void implicitStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implicitStatement_AST = null;
		
		AST tmp68_AST = null;
		tmp68_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp68_AST);
		match(IMPLICIT);
		{
		if ((LA(1)==NONE)) {
			implicitNone();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((_tokenSet_6.member(LA(1)))) {
			implicitSpecs();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		implicitStatement_AST = (AST)currentAST.root;
		returnAST = implicitStatement_AST;
	}
	
	public final void parameterStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterStatement_AST = null;
		
		AST tmp69_AST = null;
		tmp69_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp69_AST);
		match(PARAMETER);
		match(LPAREN);
		paramlist();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		parameterStatement_AST = (AST)currentAST.root;
		returnAST = parameterStatement_AST;
	}
	
	public final void typeStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeStatement_AST = null;
		AST ty_AST = null;
		AST ls_AST = null;
		AST c_AST = null;
		AST t_AST = null;
		
		if ((LA(1)==REAL||LA(1)==COMPLEX||LA(1)==DOUBLE||LA(1)==INTEGER||LA(1)==LOGICAL)) {
			typename();
			ty_AST = (AST)returnAST;
			typeStatementNameList();
			ls_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				typeStatement_AST = (AST)currentAST.root;
				ty_AST.addChild(ls_AST); typeStatement_AST = ty_AST;
				currentAST.root = typeStatement_AST;
				currentAST.child = typeStatement_AST!=null &&typeStatement_AST.getFirstChild()!=null ?
					typeStatement_AST.getFirstChild() : typeStatement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		else if ((LA(1)==CHARACTER)) {
			characterWithLen();
			c_AST = (AST)returnAST;
			typeStatementNameCharList();
			t_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				typeStatement_AST = (AST)currentAST.root;
				c_AST.addChild(t_AST); typeStatement_AST = c_AST;
				currentAST.root = typeStatement_AST;
				currentAST.child = typeStatement_AST!=null &&typeStatement_AST.getFirstChild()!=null ?
					typeStatement_AST.getFirstChild() : typeStatement_AST;
				currentAST.advanceChildToEnd();
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = typeStatement_AST;
	}
	
	public final void commonStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commonStatement_AST = null;
		
		AST tmp72_AST = null;
		tmp72_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp72_AST);
		match(COMMON);
		{
		if ((LA(1)==DIV)) {
			commonBlock();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop65:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					commonBlock();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop65;
				}
				
			} while (true);
			}
		}
		else if ((LA(1)==NAME||LA(1)==REAL)) {
			commonItems();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		commonStatement_AST = (AST)currentAST.root;
		returnAST = commonStatement_AST;
	}
	
	public final void pointerStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pointerStatement_AST = null;
		
		AST tmp74_AST = null;
		tmp74_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp74_AST);
		match(POINTER);
		pointerDecl();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop91:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				pointerDecl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop91;
			}
			
		} while (true);
		}
		pointerStatement_AST = (AST)currentAST.root;
		returnAST = pointerStatement_AST;
	}
	
	public final void externalStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST externalStatement_AST = null;
		
		AST tmp76_AST = null;
		tmp76_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp76_AST);
		match(EXTERNAL);
		namelist();
		astFactory.addASTChild(currentAST, returnAST);
		externalStatement_AST = (AST)currentAST.root;
		returnAST = externalStatement_AST;
	}
	
	public final void dataStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataStatement_AST = null;
		
		AST tmp77_AST = null;
		tmp77_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp77_AST);
		match(DATA);
		dataStatementEntity();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop128:
		do {
			if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==COMMA||LA(1)==LITERAL_real)) {
				{
				if ((LA(1)==COMMA)) {
					match(COMMA);
				}
				else if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==LITERAL_real)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				dataStatementEntity();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop128;
			}
			
		} while (true);
		}
		dataStatement_AST = (AST)currentAST.root;
		returnAST = dataStatement_AST;
	}
	
	public final void statementFunctionStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statementFunctionStatement_AST = null;
		
		AST tmp79_AST = null;
		tmp79_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp79_AST);
		match(LET);
		sfArgs();
		astFactory.addASTChild(currentAST, returnAST);
		match(ASSIGN);
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		statementFunctionStatement_AST = (AST)currentAST.root;
		returnAST = statementFunctionStatement_AST;
	}
	
	public final void wholeStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST wholeStatement_AST = null;
		Token  l = null;
		AST l_AST = null;
		AST s_AST = null;
		
		if ((LA(1)==COMMENT)) {
			AST tmp81_AST = null;
			tmp81_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp81_AST);
			match(COMMENT);
			wholeStatement_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_13.member(LA(1)))) {
			{
			if ((LA(1)==LABEL)) {
				l = LT(1);
				l_AST = astFactory.create(l);
				match(LABEL);
			}
			else if ((_tokenSet_14.member(LA(1)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			if (!(LA(1) != LITERAL_end))
			  throw new SemanticException("LA(1) != LITERAL_end");
			statement();
			s_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			seos();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				
					 	if (l_AST != null)
					 	{
					 		AST tmpFirstChild = s_AST.getFirstChild();
					 		l_AST.setNextSibling(tmpFirstChild);
					 		s_AST.setFirstChild(l_AST);
					 	}
					
			}
			wholeStatement_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = wholeStatement_AST;
	}
	
	public final void endStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST endStatement_AST = null;
		
		{
		if ((LA(1)==LABEL)) {
			AST tmp82_AST = null;
			tmp82_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp82_AST);
			match(LABEL);
		}
		else if ((LA(1)==END)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		AST tmp83_AST = null;
		tmp83_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp83_AST);
		match(END);
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		endStatement_AST = (AST)currentAST.root;
		returnAST = endStatement_AST;
	}
	
	public final void arrayDeclarators() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayDeclarators_AST = null;
		
		arrayDeclarator();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop48:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				arrayDeclarator();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop48;
			}
			
		} while (true);
		}
		arrayDeclarators_AST = (AST)currentAST.root;
		returnAST = arrayDeclarators_AST;
	}
	
	public final void arrayDeclarator() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayDeclarator_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		{
		if ((LA(1)==NAME)) {
			AST tmp85_AST = null;
			tmp85_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp85_AST);
			match(NAME);
		}
		else if ((LA(1)==REAL)) {
			n = LT(1);
			n_AST = astFactory.create(n);
			astFactory.makeASTRoot(currentAST, n_AST);
			match(REAL);
			if ( inputState.guessing==0 ) {
				n.setType(NAME);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(LPAREN);
		arrayDeclaratorExtents();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		arrayDeclarator_AST = (AST)currentAST.root;
		returnAST = arrayDeclarator_AST;
	}
	
	public final void arrayDeclaratorExtents() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayDeclaratorExtents_AST = null;
		
		arrayDeclaratorExtent();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop51:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				arrayDeclaratorExtent();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop51;
			}
			
		} while (true);
		}
		arrayDeclaratorExtents_AST = (AST)currentAST.root;
		returnAST = arrayDeclaratorExtents_AST;
	}
	
	public final void arrayDeclaratorExtent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayDeclaratorExtent_AST = null;
		
		if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==ICON||LA(1)==MINUS||LA(1)==PLUS)) {
			iexprCode();
			astFactory.addASTChild(currentAST, returnAST);
			{
			if ((LA(1)==COLON)) {
				AST tmp89_AST = null;
				tmp89_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp89_AST);
				match(COLON);
				{
				if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==ICON||LA(1)==MINUS||LA(1)==PLUS)) {
					iexprCode();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==STAR)) {
					AST tmp90_AST = null;
					tmp90_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp90_AST);
					match(STAR);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
			}
			else if ((LA(1)==RPAREN||LA(1)==COMMA)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			arrayDeclaratorExtent_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==STAR)) {
			AST tmp91_AST = null;
			tmp91_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp91_AST);
			match(STAR);
			arrayDeclaratorExtent_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = arrayDeclaratorExtent_AST;
	}
	
	public final void iexprCode() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iexprCode_AST = null;
		
		iexpr1();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop346:
		do {
			if ((LA(1)==MINUS||LA(1)==PLUS)) {
				{
				if ((LA(1)==PLUS)) {
					AST tmp92_AST = null;
					tmp92_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp92_AST);
					match(PLUS);
				}
				else if ((LA(1)==MINUS)) {
					AST tmp93_AST = null;
					tmp93_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp93_AST);
					match(MINUS);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				iexpr1();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop346;
			}
			
		} while (true);
		}
		iexprCode_AST = (AST)currentAST.root;
		returnAST = iexprCode_AST;
	}
	
	public final void equivEntityGroup() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equivEntityGroup_AST = null;
		
		AST tmp94_AST = null;
		tmp94_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp94_AST);
		match(LPAREN);
		equivEntity();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop60:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				equivEntity();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop60;
			}
			
		} while (true);
		}
		match(RPAREN);
		equivEntityGroup_AST = (AST)currentAST.root;
		returnAST = equivEntityGroup_AST;
	}
	
	public final void equivEntity() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equivEntity_AST = null;
		
		varRef();
		astFactory.addASTChild(currentAST, returnAST);
		equivEntity_AST = (AST)currentAST.root;
		returnAST = equivEntity_AST;
	}
	
	public final void varRef() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varRef_AST = null;
		Token  s = null;
		AST s_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		{
		if ((LA(1)==NAME)) {
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.makeASTRoot(currentAST, s_AST);
			match(NAME);
			if ( inputState.guessing==0 ) {
				variables.add(s.getText());
			}
		}
		else if ((LA(1)==LITERAL_real)) {
			n = LT(1);
			n_AST = astFactory.create(n);
			astFactory.makeASTRoot(currentAST, n_AST);
			match(LITERAL_real);
			if ( inputState.guessing==0 ) {
				n.setType(NAME);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		if ((LA(1)==LPAREN)) {
			subscripts();
			astFactory.addASTChild(currentAST, returnAST);
			{
			if ((LA(1)==LPAREN)) {
				substringApp();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_15.member(LA(1)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else if ((_tokenSet_15.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		varRef_AST = (AST)currentAST.root;
		returnAST = varRef_AST;
	}
	
	public final void commonBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commonBlock_AST = null;
		
		commonName();
		astFactory.addASTChild(currentAST, returnAST);
		commonItems();
		astFactory.addASTChild(currentAST, returnAST);
		commonBlock_AST = (AST)currentAST.root;
		returnAST = commonBlock_AST;
	}
	
	public final void commonItems() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commonItems_AST = null;
		
		commonItem();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop71:
		do {
			if ((LA(1)==COMMA) && (LA(2)==NAME||LA(2)==REAL)) {
				match(COMMA);
				commonItem();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop71;
			}
			
		} while (true);
		}
		commonItems_AST = (AST)currentAST.root;
		returnAST = commonItems_AST;
	}
	
	public final void commonName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commonName_AST = null;
		
		AST tmp98_AST = null;
		tmp98_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp98_AST);
		match(DIV);
		{
		if ((LA(1)==NAME)) {
			AST tmp99_AST = null;
			tmp99_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp99_AST);
			match(NAME);
			AST tmp100_AST = null;
			tmp100_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp100_AST);
			match(DIV);
		}
		else if ((LA(1)==DIV)) {
			AST tmp101_AST = null;
			tmp101_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp101_AST);
			match(DIV);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		commonName_AST = (AST)currentAST.root;
		returnAST = commonName_AST;
	}
	
	public final void commonItem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commonItem_AST = null;
		
		if ((LA(1)==NAME) && (LA(2)==EOS||LA(2)==COMMA)) {
			AST tmp102_AST = null;
			tmp102_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp102_AST);
			match(NAME);
			commonItem_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==NAME||LA(1)==REAL) && (LA(2)==LPAREN)) {
			arrayDeclarator();
			astFactory.addASTChild(currentAST, returnAST);
			commonItem_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = commonItem_AST;
	}
	
	public final void typename() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typename_AST = null;
		Token  c = null;
		AST c_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		{
		switch ( LA(1)) {
		case REAL:
		{
			AST tmp103_AST = null;
			tmp103_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp103_AST);
			match(REAL);
			break;
		}
		case COMPLEX:
		{
			c = LT(1);
			c_AST = astFactory.create(c);
			astFactory.addASTChild(currentAST, c_AST);
			match(COMPLEX);
			{
			if ((LA(1)==STAR)) {
				match(STAR);
				i = LT(1);
				i_AST = astFactory.create(i);
				match(ICON);
				if (!(Integer.parseInt(i.getText()) == 16))
				  throw new SemanticException("Integer.parseInt(i.getText()) == 16");
				if ( inputState.guessing==0 ) {
					c_AST.setType(LITERAL_double); c_AST.setText("double");
				}
			}
			else if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==FUNCTION||LA(1)==REAL||LA(1)==LITERAL_real)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			break;
		}
		case INTEGER:
		{
			AST tmp105_AST = null;
			tmp105_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp105_AST);
			match(INTEGER);
			break;
		}
		case LOGICAL:
		{
			AST tmp106_AST = null;
			tmp106_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp106_AST);
			match(LOGICAL);
			break;
		}
		default:
			if ((LA(1)==DOUBLE) && (LA(2)==COMPLEX)) {
				AST tmp107_AST = null;
				tmp107_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp107_AST);
				match(DOUBLE);
				match(COMPLEX);
			}
			else if ((LA(1)==DOUBLE) && (LA(2)==PRECISION)) {
				match(DOUBLE);
				AST tmp110_AST = null;
				tmp110_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp110_AST);
				match(PRECISION);
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		typename_AST = (AST)currentAST.root;
		returnAST = typename_AST;
	}
	
	public final void typeStatementNameList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeStatementNameList_AST = null;
		
		typeStatementName();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop76:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				typeStatementName();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop76;
			}
			
		} while (true);
		}
		typeStatementNameList_AST = (AST)currentAST.root;
		returnAST = typeStatementNameList_AST;
	}
	
	public final void characterWithLen() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST characterWithLen_AST = null;
		
		AST tmp112_AST = null;
		tmp112_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp112_AST);
		match(CHARACTER);
		{
		if ((LA(1)==STAR)) {
			cwlLen();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==FUNCTION||LA(1)==REAL||LA(1)==LITERAL_real)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		characterWithLen_AST = (AST)currentAST.root;
		returnAST = characterWithLen_AST;
	}
	
	public final void typeStatementNameCharList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeStatementNameCharList_AST = null;
		
		typeStatementNameChar();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop80:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				typeStatementNameChar();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop80;
			}
			
		} while (true);
		}
		typeStatementNameCharList_AST = (AST)currentAST.root;
		returnAST = typeStatementNameCharList_AST;
	}
	
	public final void typeStatementName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeStatementName_AST = null;
		
		if ((LA(1)==NAME||LA(1)==LITERAL_real) && (LA(2)==EOS||LA(2)==LPAREN||LA(2)==COMMA||LA(2)==STAR)) {
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			typeStatementName_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==NAME||LA(1)==REAL) && (LA(2)==LPAREN)) {
			arrayDeclarator();
			astFactory.addASTChild(currentAST, returnAST);
			typeStatementName_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = typeStatementName_AST;
	}
	
	public final void typeStatementNameChar() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeStatementNameChar_AST = null;
		AST n_AST = null;
		AST len_AST = null;
		
		typeStatementName();
		n_AST = (AST)returnAST;
		{
		if ((LA(1)==STAR)) {
			typeStatementLenSpec();
			len_AST = (AST)returnAST;
		}
		else if ((LA(1)==EOS||LA(1)==COMMA)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			typeStatementNameChar_AST = (AST)currentAST.root;
			n_AST.addChild(len_AST); typeStatementNameChar_AST = n_AST;
			currentAST.root = typeStatementNameChar_AST;
			currentAST.child = typeStatementNameChar_AST!=null &&typeStatementNameChar_AST.getFirstChild()!=null ?
				typeStatementNameChar_AST.getFirstChild() : typeStatementNameChar_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = typeStatementNameChar_AST;
	}
	
	public final void typeStatementLenSpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeStatementLenSpec_AST = null;
		
		AST tmp114_AST = null;
		tmp114_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp114_AST);
		match(STAR);
		lenSpecification();
		astFactory.addASTChild(currentAST, returnAST);
		typeStatementLenSpec_AST = (AST)currentAST.root;
		returnAST = typeStatementLenSpec_AST;
	}
	
	public final void lenSpecification() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lenSpecification_AST = null;
		
		boolean synPredMatched108 = false;
		if (((LA(1)==LPAREN) && (LA(2)==STAR))) {
			int _m108 = mark();
			synPredMatched108 = true;
			inputState.guessing++;
			try {
				{
				match(LPAREN);
				match(STAR);
				match(RPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched108 = false;
			}
			rewind(_m108);
inputState.guessing--;
		}
		if ( synPredMatched108 ) {
			match(LPAREN);
			AST tmp116_AST = null;
			tmp116_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp116_AST);
			match(STAR);
			match(RPAREN);
			lenSpecification_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==ICON)) {
			AST tmp118_AST = null;
			tmp118_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp118_AST);
			match(ICON);
			lenSpecification_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==LPAREN) && (_tokenSet_12.member(LA(2)))) {
			AST tmp119_AST = null;
			tmp119_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp119_AST);
			match(LPAREN);
			intConstantExpr();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp120_AST = null;
			tmp120_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp120_AST);
			match(RPAREN);
			lenSpecification_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = lenSpecification_AST;
	}
	
	public final void typenameLen() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typenameLen_AST = null;
		
		AST tmp121_AST = null;
		tmp121_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp121_AST);
		match(STAR);
		AST tmp122_AST = null;
		tmp122_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp122_AST);
		match(ICON);
		typenameLen_AST = (AST)currentAST.root;
		returnAST = typenameLen_AST;
	}
	
	public final void pointerDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pointerDecl_AST = null;
		
		match(LPAREN);
		AST tmp124_AST = null;
		tmp124_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp124_AST);
		match(NAME);
		match(COMMA);
		AST tmp126_AST = null;
		tmp126_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp126_AST);
		match(NAME);
		match(RPAREN);
		pointerDecl_AST = (AST)currentAST.root;
		returnAST = pointerDecl_AST;
	}
	
	public final void implicitNone() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implicitNone_AST = null;
		
		AST tmp128_AST = null;
		tmp128_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp128_AST);
		match(NONE);
		implicitNone_AST = (AST)currentAST.root;
		returnAST = implicitNone_AST;
	}
	
	public final void implicitSpecs() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implicitSpecs_AST = null;
		
		implicitSpec();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop98:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				implicitSpec();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop98;
			}
			
		} while (true);
		}
		implicitSpecs_AST = (AST)currentAST.root;
		returnAST = implicitSpecs_AST;
	}
	
	public final void implicitSpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implicitSpec_AST = null;
		AST ty_AST = null;
		AST im_AST = null;
		
		type();
		ty_AST = (AST)returnAST;
		match(LPAREN);
		implicitLetters();
		im_AST = (AST)returnAST;
		match(RPAREN);
		if ( inputState.guessing==0 ) {
			implicitSpec_AST = (AST)currentAST.root;
			ty_AST.addChild(im_AST); implicitSpec_AST = ty_AST;
			currentAST.root = implicitSpec_AST;
			currentAST.child = implicitSpec_AST!=null &&implicitSpec_AST.getFirstChild()!=null ?
				implicitSpec_AST.getFirstChild() : implicitSpec_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = implicitSpec_AST;
	}
	
	public final void implicitLetters() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implicitLetters_AST = null;
		
		implicitRange();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop105:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				implicitRange();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop105;
			}
			
		} while (true);
		}
		implicitLetters_AST = (AST)currentAST.root;
		returnAST = implicitLetters_AST;
	}
	
	public final void implicitLetter() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implicitLetter_AST = null;
		
		AST tmp133_AST = null;
		tmp133_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp133_AST);
		match(NAME);
		implicitLetter_AST = (AST)currentAST.root;
		returnAST = implicitLetter_AST;
	}
	
	public final void implicitRange() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implicitRange_AST = null;
		
		implicitLetter();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==MINUS)) {
			AST tmp134_AST = null;
			tmp134_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp134_AST);
			match(MINUS);
			implicitLetter();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==RPAREN||LA(1)==COMMA)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		implicitRange_AST = (AST)currentAST.root;
		returnAST = implicitRange_AST;
	}
	
	public final void intConstantExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST intConstantExpr_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		intConstantExpr_AST = (AST)currentAST.root;
		returnAST = intConstantExpr_AST;
	}
	
	public final void cwlLen() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cwlLen_AST = null;
		
		AST tmp135_AST = null;
		tmp135_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp135_AST);
		match(STAR);
		lenSpecification();
		astFactory.addASTChild(currentAST, returnAST);
		cwlLen_AST = (AST)currentAST.root;
		returnAST = cwlLen_AST;
	}
	
	public final void paramlist() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST paramlist_AST = null;
		
		paramassign();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop115:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				paramassign();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop115;
			}
			
		} while (true);
		}
		paramlist_AST = (AST)currentAST.root;
		returnAST = paramlist_AST;
	}
	
	public final void paramassign() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST paramassign_AST = null;
		
		AST tmp137_AST = null;
		tmp137_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp137_AST);
		match(NAME);
		AST tmp138_AST = null;
		tmp138_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp138_AST);
		match(ASSIGN);
		constantExpr();
		astFactory.addASTChild(currentAST, returnAST);
		paramassign_AST = (AST)currentAST.root;
		returnAST = paramassign_AST;
	}
	
	public final void constantExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constantExpr_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		constantExpr_AST = (AST)currentAST.root;
		returnAST = constantExpr_AST;
	}
	
	public final void saveEntity() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST saveEntity_AST = null;
		
		{
		if ((LA(1)==NAME)) {
			AST tmp139_AST = null;
			tmp139_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp139_AST);
			match(NAME);
		}
		else if ((LA(1)==DIV)) {
			AST tmp140_AST = null;
			tmp140_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp140_AST);
			match(DIV);
			AST tmp141_AST = null;
			tmp141_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp141_AST);
			match(NAME);
			match(DIV);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		saveEntity_AST = (AST)currentAST.root;
		returnAST = saveEntity_AST;
	}
	
	public final void dataStatementEntity() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataStatementEntity_AST = null;
		
		dse1();
		astFactory.addASTChild(currentAST, returnAST);
		dse2();
		astFactory.addASTChild(currentAST, returnAST);
		dataStatementEntity_AST = (AST)currentAST.root;
		returnAST = dataStatementEntity_AST;
	}
	
	public final void dataStatementItem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataStatementItem_AST = null;
		
		if ((LA(1)==NAME||LA(1)==LITERAL_real)) {
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			dataStatementItem_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==LPAREN)) {
			dataImpliedDo();
			astFactory.addASTChild(currentAST, returnAST);
			dataStatementItem_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = dataStatementItem_AST;
	}
	
	public final void dataImpliedDo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataImpliedDo_AST = null;
		
		AST tmp143_AST = null;
		tmp143_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp143_AST);
		match(LPAREN);
		dataImpliedDoList();
		astFactory.addASTChild(currentAST, returnAST);
		match(COMMA);
		dataImpliedDoRange();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		dataImpliedDo_AST = (AST)currentAST.root;
		returnAST = dataImpliedDo_AST;
	}
	
	public final void dataStatementMultiple() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataStatementMultiple_AST = null;
		
		{
		if ((LA(1)==NAME||LA(1)==ICON) && (LA(2)==STAR)) {
			{
			if ((LA(1)==ICON)) {
				AST tmp146_AST = null;
				tmp146_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp146_AST);
				match(ICON);
			}
			else if ((LA(1)==NAME)) {
				AST tmp147_AST = null;
				tmp147_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp147_AST);
				match(NAME);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			AST tmp148_AST = null;
			tmp148_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp148_AST);
			match(STAR);
		}
		else if ((_tokenSet_16.member(LA(1))) && (_tokenSet_17.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		if ((_tokenSet_18.member(LA(1)))) {
			constant();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==NAME)) {
			AST tmp149_AST = null;
			tmp149_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp149_AST);
			match(NAME);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		dataStatementMultiple_AST = (AST)currentAST.root;
		returnAST = dataStatementMultiple_AST;
	}
	
	public final void constant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;
		
		switch ( LA(1)) {
		case RCON:
		case LPAREN:
		case ICON:
		case MINUS:
		case PLUS:
		{
			{
			if ((LA(1)==MINUS||LA(1)==PLUS)) {
				{
				if ((LA(1)==PLUS)) {
					AST tmp150_AST = null;
					tmp150_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp150_AST);
					match(PLUS);
				}
				else if ((LA(1)==MINUS)) {
					AST tmp151_AST = null;
					tmp151_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp151_AST);
					match(MINUS);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
			}
			else if ((LA(1)==RCON||LA(1)==LPAREN||LA(1)==ICON)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			unsignedArithmeticConstant();
			astFactory.addASTChild(currentAST, returnAST);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case SCON:
		{
			AST tmp152_AST = null;
			tmp152_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp152_AST);
			match(SCON);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case HOLLERITH:
		{
			AST tmp153_AST = null;
			tmp153_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp153_AST);
			match(HOLLERITH);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case TRUE:
		case FALSE:
		{
			logicalConstant();
			astFactory.addASTChild(currentAST, returnAST);
			constant_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = constant_AST;
	}
	
	public final void dse1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dse1_AST = null;
		
		dataStatementItem();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop137:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				dataStatementItem();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop137;
			}
			
		} while (true);
		}
		AST tmp155_AST = null;
		tmp155_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp155_AST);
		match(DIV);
		dse1_AST = (AST)currentAST.root;
		returnAST = dse1_AST;
	}
	
	public final void dse2() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dse2_AST = null;
		
		dataStatementMultiple();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop140:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				dataStatementMultiple();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop140;
			}
			
		} while (true);
		}
		AST tmp157_AST = null;
		tmp157_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp157_AST);
		match(DIV);
		dse2_AST = (AST)currentAST.root;
		returnAST = dse2_AST;
	}
	
	public final void dataImpliedDoList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataImpliedDoList_AST = null;
		
		if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==LITERAL_real)) {
			dataImpliedDoListWhat();
			astFactory.addASTChild(currentAST, returnAST);
			dataImpliedDoList_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==COMMA)) {
			match(COMMA);
			dataImpliedDoList();
			astFactory.addASTChild(currentAST, returnAST);
			dataImpliedDoList_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = dataImpliedDoList_AST;
	}
	
	public final void dataImpliedDoRange() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataImpliedDoRange_AST = null;
		
		AST tmp159_AST = null;
		tmp159_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp159_AST);
		match(NAME);
		AST tmp160_AST = null;
		tmp160_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp160_AST);
		match(ASSIGN);
		intConstantExpr();
		astFactory.addASTChild(currentAST, returnAST);
		match(COMMA);
		intConstantExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==COMMA)) {
			match(COMMA);
			intConstantExpr();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==RPAREN)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		dataImpliedDoRange_AST = (AST)currentAST.root;
		returnAST = dataImpliedDoRange_AST;
	}
	
	public final void dataImpliedDoListWhat() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataImpliedDoListWhat_AST = null;
		
		{
		if ((LA(1)==NAME||LA(1)==LITERAL_real)) {
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==LPAREN)) {
			dataImpliedDo();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		dataImpliedDoListWhat_AST = (AST)currentAST.root;
		returnAST = dataImpliedDoListWhat_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		ncExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==COLON)) {
			AST tmp163_AST = null;
			tmp163_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp163_AST);
			match(COLON);
			ncExpr();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((_tokenSet_19.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		expression_AST = (AST)currentAST.root;
		returnAST = expression_AST;
	}
	
	public final void to() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST to_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		n = LT(1);
		n_AST = astFactory.create(n);
		astFactory.addASTChild(currentAST, n_AST);
		match(NAME);
		if (!(n.getText().compareToIgnoreCase("to") == 0))
		  throw new SemanticException("n.getText().compareToIgnoreCase(\"to\") == 0");
		if ( inputState.guessing==0 ) {
			n.setType(TO);
		}
		to_AST = (AST)currentAST.root;
		returnAST = to_AST;
	}
	
	public final void variableName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableName_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		s = LT(1);
		s_AST = astFactory.create(s);
		astFactory.addASTChild(currentAST, s_AST);
		match(NAME);
		if ( inputState.guessing==0 ) {
			variables.add(s.getText());
		}
		variableName_AST = (AST)currentAST.root;
		returnAST = variableName_AST;
	}
	
	public final void unconditionalGoto() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unconditionalGoto_AST = null;
		
		lblRef();
		astFactory.addASTChild(currentAST, returnAST);
		unconditionalGoto_AST = (AST)currentAST.root;
		returnAST = unconditionalGoto_AST;
	}
	
	public final void computedGoto() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST computedGoto_AST = null;
		
		match(LPAREN);
		labelList();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp165_AST = null;
		tmp165_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp165_AST);
		match(RPAREN);
		{
		if ((LA(1)==COMMA)) {
			match(COMMA);
		}
		else if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==ICON||LA(1)==MINUS||LA(1)==PLUS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		integerExpr();
		astFactory.addASTChild(currentAST, returnAST);
		computedGoto_AST = (AST)currentAST.root;
		returnAST = computedGoto_AST;
	}
	
	public final void assignedGoto() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignedGoto_AST = null;
		
		AST tmp167_AST = null;
		tmp167_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp167_AST);
		match(NAME);
		{
		if ((LA(1)==LPAREN||LA(1)==COMMA)) {
			{
			if ((LA(1)==COMMA)) {
				match(COMMA);
			}
			else if ((LA(1)==LPAREN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(LPAREN);
			labelList();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		assignedGoto_AST = (AST)currentAST.root;
		returnAST = assignedGoto_AST;
	}
	
	public final void lblRef() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lblRef_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		l = LT(1);
		l_AST = astFactory.create(l);
		astFactory.addASTChild(currentAST, l_AST);
		match(ICON);
		if ( inputState.guessing==0 ) {
			l_AST.setType(LABELREF);
		}
		lblRef_AST = (AST)currentAST.root;
		returnAST = lblRef_AST;
	}
	
	public final void labelList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST labelList_AST = null;
		
		lblRef();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop157:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				lblRef();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop157;
			}
			
		} while (true);
		}
		labelList_AST = (AST)currentAST.root;
		returnAST = labelList_AST;
	}
	
	public final void integerExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST integerExpr_AST = null;
		
		iexpr();
		astFactory.addASTChild(currentAST, returnAST);
		integerExpr_AST = (AST)currentAST.root;
		returnAST = integerExpr_AST;
	}
	
	public final void logicalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpression_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		logicalExpression_AST = (AST)currentAST.root;
		returnAST = logicalExpression_AST;
	}
	
	public final void blockIfStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST blockIfStatement_AST = null;
		
		firstIfBlock();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop167:
		do {
			if ((LA(1)==ELSEIF||LA(1)==LITERAL_else)) {
				elseIfStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop167;
			}
			
		} while (true);
		}
		{
		if ((LA(1)==ELSE)) {
			elseStatement();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==ENDIF||LA(1)==LITERAL_end)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		endIfStatement();
		astFactory.addASTChild(currentAST, returnAST);
		blockIfStatement_AST = (AST)currentAST.root;
		returnAST = blockIfStatement_AST;
	}
	
	public final void logicalIfStatement(
		Token ifstmt
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalIfStatement_AST = null;
		
		executableStatement();
		astFactory.addASTChild(currentAST, returnAST);
		if ( inputState.guessing==0 ) {
			logicalIfStatement_AST = (AST)currentAST.root;
			logicalIfStatement_AST = (AST)astFactory.make( (new ASTArray(2)).add(createNewNode(THENBLOCK,"then",ifstmt.getLine(),ifstmt.getColumn())).add(logicalIfStatement_AST));
			currentAST.root = logicalIfStatement_AST;
			currentAST.child = logicalIfStatement_AST!=null &&logicalIfStatement_AST.getFirstChild()!=null ?
				logicalIfStatement_AST.getFirstChild() : logicalIfStatement_AST;
			currentAST.advanceChildToEnd();
		}
		logicalIfStatement_AST = (AST)currentAST.root;
		returnAST = logicalIfStatement_AST;
	}
	
	public final void arithmeticIfStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arithmeticIfStatement_AST = null;
		
		lblRef();
		astFactory.addASTChild(currentAST, returnAST);
		match(COMMA);
		lblRef();
		astFactory.addASTChild(currentAST, returnAST);
		match(COMMA);
		lblRef();
		astFactory.addASTChild(currentAST, returnAST);
		arithmeticIfStatement_AST = (AST)currentAST.root;
		returnAST = arithmeticIfStatement_AST;
	}
	
	public final void firstIfBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST firstIfBlock_AST = null;
		Token  then = null;
		AST then_AST = null;
		
		then = LT(1);
		then_AST = astFactory.create(then);
		match(THEN);
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		{
		int _cnt171=0;
		_loop171:
		do {
			if ((_tokenSet_8.member(LA(1)))) {
				wholeStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt171>=1 ) { break _loop171; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt171++;
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			firstIfBlock_AST = (AST)currentAST.root;
			firstIfBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(createNewNode(THENBLOCK,"then",then.getLine(),then.getColumn())).add(firstIfBlock_AST));
			currentAST.root = firstIfBlock_AST;
			currentAST.child = firstIfBlock_AST!=null &&firstIfBlock_AST.getFirstChild()!=null ?
				firstIfBlock_AST.getFirstChild() : firstIfBlock_AST;
			currentAST.advanceChildToEnd();
		}
		firstIfBlock_AST = (AST)currentAST.root;
		returnAST = firstIfBlock_AST;
	}
	
	public final void elseIfStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elseIfStatement_AST = null;
		AST le_AST = null;
		Token  t = null;
		AST t_AST = null;
		Token el = null;
		
		if ( inputState.guessing==0 ) {
			el = LT(1);
		}
		{
		if ((LA(1)==ELSEIF)) {
			match(ELSEIF);
		}
		else if ((LA(1)==LITERAL_else)) {
			match(LITERAL_else);
			match(LITERAL_if);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(LPAREN);
		logicalExpression();
		le_AST = (AST)returnAST;
		match(RPAREN);
		t = LT(1);
		t_AST = astFactory.create(t);
		match(LITERAL_then);
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop175:
		do {
			if ((_tokenSet_8.member(LA(1)))) {
				wholeStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop175;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			elseIfStatement_AST = (AST)currentAST.root;
			
					elseIfStatement_AST =
					(AST)astFactory.make( (new ASTArray(3)).add(createNewNode(ELSEIF,"elseif",el.getLine(),el.getColumn())).add(le_AST).add((AST)astFactory.make( (new ASTArray(2)).add(createNewNode(THENBLOCK,"then",t.getLine(),t.getColumn())).add(elseIfStatement_AST)))) ;
				
			currentAST.root = elseIfStatement_AST;
			currentAST.child = elseIfStatement_AST!=null &&elseIfStatement_AST.getFirstChild()!=null ?
				elseIfStatement_AST.getFirstChild() : elseIfStatement_AST;
			currentAST.advanceChildToEnd();
		}
		elseIfStatement_AST = (AST)currentAST.root;
		returnAST = elseIfStatement_AST;
	}
	
	public final void elseStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elseStatement_AST = null;
		Token  e = null;
		AST e_AST = null;
		
		e = LT(1);
		e_AST = astFactory.create(e);
		match(ELSE);
		seos();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop178:
		do {
			if ((_tokenSet_8.member(LA(1)))) {
				wholeStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop178;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			elseStatement_AST = (AST)currentAST.root;
			elseStatement_AST = (AST)astFactory.make( (new ASTArray(2)).add(createNewNode(ELSEBLOCK,"else",e.getLine(),e.getColumn())).add(elseStatement_AST)) ;
			currentAST.root = elseStatement_AST;
			currentAST.child = elseStatement_AST!=null &&elseStatement_AST.getFirstChild()!=null ?
				elseStatement_AST.getFirstChild() : elseStatement_AST;
			currentAST.advanceChildToEnd();
		}
		elseStatement_AST = (AST)currentAST.root;
		returnAST = elseStatement_AST;
	}
	
	public final void endIfStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST endIfStatement_AST = null;
		
		{
		if ((LA(1)==ENDIF)) {
			match(ENDIF);
		}
		else if ((LA(1)==LITERAL_end)) {
			match(LITERAL_end);
			match(LITERAL_if);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		endIfStatement_AST = (AST)currentAST.root;
		returnAST = endIfStatement_AST;
	}
	
	public final void doWithLabel() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST doWithLabel_AST = null;
		
		lblRef();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==COMMA)) {
			match(COMMA);
		}
		else if ((LA(1)==NAME)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		doVarArgs();
		astFactory.addASTChild(currentAST, returnAST);
		doWithLabel_AST = (AST)currentAST.root;
		returnAST = doWithLabel_AST;
	}
	
	public final void doWithEndDo(
		Token doT
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST doWithEndDo_AST = null;
		
		doVarArgs();
		astFactory.addASTChild(currentAST, returnAST);
		doBody(doT);
		astFactory.addASTChild(currentAST, returnAST);
		enddoStatement();
		astFactory.addASTChild(currentAST, returnAST);
		doWithEndDo_AST = (AST)currentAST.root;
		returnAST = doWithEndDo_AST;
	}
	
	public final void doVarArgs() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST doVarArgs_AST = null;
		
		variableName();
		astFactory.addASTChild(currentAST, returnAST);
		match(ASSIGN);
		intRealDpExpr();
		astFactory.addASTChild(currentAST, returnAST);
		match(COMMA);
		intRealDpExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==COMMA)) {
			match(COMMA);
			intRealDpExpr();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((_tokenSet_20.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		doVarArgs_AST = (AST)currentAST.root;
		returnAST = doVarArgs_AST;
	}
	
	public final void intRealDpExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST intRealDpExpr_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		intRealDpExpr_AST = (AST)currentAST.root;
		returnAST = intRealDpExpr_AST;
	}
	
	public final void doBody(
		Token doT
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST doBody_AST = null;
		
		{
		_loop189:
		do {
			if ((_tokenSet_8.member(LA(1)))) {
				wholeStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop189;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			doBody_AST = (AST)currentAST.root;
			doBody_AST = (AST)astFactory.make( (new ASTArray(2)).add(createNewNode(DOBLOCK,"[doLoopBody]",doT.getLine(),doT.getColumn())).add(doBody_AST)) ;
			currentAST.root = doBody_AST;
			currentAST.child = doBody_AST!=null &&doBody_AST.getFirstChild()!=null ?
				doBody_AST.getFirstChild() : doBody_AST;
			currentAST.advanceChildToEnd();
		}
		doBody_AST = (AST)currentAST.root;
		returnAST = doBody_AST;
	}
	
	public final void enddoStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enddoStatement_AST = null;
		
		{
		if ((LA(1)==ENDDO)) {
			match(ENDDO);
		}
		else if ((LA(1)==LITERAL_end)) {
			match(LITERAL_end);
			match(LITERAL_do);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		enddoStatement_AST = (AST)currentAST.root;
		returnAST = enddoStatement_AST;
	}
	
	public final void controlInfoList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlInfoList_AST = null;
		
		controlInfoListItem();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop212:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				controlInfoListItem();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop212;
			}
			
		} while (true);
		}
		controlInfoList_AST = (AST)currentAST.root;
		returnAST = controlInfoList_AST;
	}
	
	public final void ioList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ioList_AST = null;
		
		boolean synPredMatched218 = false;
		if (((_tokenSet_12.member(LA(1))) && (_tokenSet_21.member(LA(2))))) {
			int _m218 = mark();
			synPredMatched218 = true;
			inputState.guessing++;
			try {
				{
				ioListItem();
				match(COMMA);
				match(NAME);
				match(ASSIGN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched218 = false;
			}
			rewind(_m218);
inputState.guessing--;
		}
		if ( synPredMatched218 ) {
			ioListItem();
			astFactory.addASTChild(currentAST, returnAST);
			ioList_AST = (AST)currentAST.root;
		}
		else {
			boolean synPredMatched220 = false;
			if (((_tokenSet_12.member(LA(1))) && (_tokenSet_22.member(LA(2))))) {
				int _m220 = mark();
				synPredMatched220 = true;
				inputState.guessing++;
				try {
					{
					ioListItem();
					match(COMMA);
					ioListItem();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched220 = false;
				}
				rewind(_m220);
inputState.guessing--;
			}
			if ( synPredMatched220 ) {
				ioListItem();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp190_AST = null;
				tmp190_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp190_AST);
				match(COMMA);
				ioList();
				astFactory.addASTChild(currentAST, returnAST);
				ioList_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_12.member(LA(1))) && (_tokenSet_21.member(LA(2)))) {
				ioListItem();
				astFactory.addASTChild(currentAST, returnAST);
				ioList_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			returnAST = ioList_AST;
		}
		
	public final void formatIdentifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formatIdentifier_AST = null;
		
		switch ( LA(1)) {
		case SCON:
		{
			AST tmp191_AST = null;
			tmp191_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp191_AST);
			match(SCON);
			formatIdentifier_AST = (AST)currentAST.root;
			break;
		}
		case HOLLERITH:
		{
			AST tmp192_AST = null;
			tmp192_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp192_AST);
			match(HOLLERITH);
			formatIdentifier_AST = (AST)currentAST.root;
			break;
		}
		case NAME:
		case LPAREN:
		case ICON:
		case MINUS:
		case PLUS:
		{
			iexpr();
			astFactory.addASTChild(currentAST, returnAST);
			formatIdentifier_AST = (AST)currentAST.root;
			break;
		}
		case STAR:
		{
			AST tmp193_AST = null;
			tmp193_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp193_AST);
			match(STAR);
			formatIdentifier_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = formatIdentifier_AST;
	}
	
	public final void controlInfoListItem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlInfoListItem_AST = null;
		
		switch ( LA(1)) {
		case HOLLERITH:
		{
			AST tmp194_AST = null;
			tmp194_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp194_AST);
			match(HOLLERITH);
			controlInfoListItem_AST = (AST)currentAST.root;
			break;
		}
		case SCON:
		{
			AST tmp195_AST = null;
			tmp195_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp195_AST);
			match(SCON);
			controlInfoListItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_fmt:
		{
			controlFmt();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp196_AST = null;
			tmp196_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp196_AST);
			match(ASSIGN);
			formatIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			controlInfoListItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_unit:
		{
			controlUnit();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp197_AST = null;
			tmp197_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp197_AST);
			match(ASSIGN);
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			controlInfoListItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_end:
		{
			controlEnd();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp198_AST = null;
			tmp198_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp198_AST);
			match(ASSIGN);
			lblRef();
			astFactory.addASTChild(currentAST, returnAST);
			controlInfoListItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_err:
		{
			controlErrSpec();
			astFactory.addASTChild(currentAST, returnAST);
			controlInfoListItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_iostat:
		{
			controlIostat();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp199_AST = null;
			tmp199_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp199_AST);
			match(ASSIGN);
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			controlInfoListItem_AST = (AST)currentAST.root;
			break;
		}
		default:
			if ((_tokenSet_23.member(LA(1))) && (_tokenSet_24.member(LA(2)))) {
				unitIdentifier();
				astFactory.addASTChild(currentAST, returnAST);
				controlInfoListItem_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==NAME) && (LA(2)==ASSIGN)) {
				controlRec();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp200_AST = null;
				tmp200_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp200_AST);
				match(ASSIGN);
				integerExpr();
				astFactory.addASTChild(currentAST, returnAST);
				controlInfoListItem_AST = (AST)currentAST.root;
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = controlInfoListItem_AST;
	}
	
	public final void controlErrSpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlErrSpec_AST = null;
		
		controlErr();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp201_AST = null;
		tmp201_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp201_AST);
		match(ASSIGN);
		{
		if ((LA(1)==ICON)) {
			lblRef();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==NAME)) {
			AST tmp202_AST = null;
			tmp202_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp202_AST);
			match(NAME);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		controlErrSpec_AST = (AST)currentAST.root;
		returnAST = controlErrSpec_AST;
	}
	
	public final void controlErr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlErr_AST = null;
		
		AST tmp203_AST = null;
		tmp203_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp203_AST);
		match(LITERAL_err);
		controlErr_AST = (AST)currentAST.root;
		returnAST = controlErr_AST;
	}
	
	public final void unitIdentifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unitIdentifier_AST = null;
		
		if ((LA(1)==NAME||LA(1)==LPAREN||LA(1)==ICON||LA(1)==MINUS||LA(1)==PLUS)) {
			iexpr();
			astFactory.addASTChild(currentAST, returnAST);
			unitIdentifier_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==STAR)) {
			AST tmp204_AST = null;
			tmp204_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp204_AST);
			match(STAR);
			unitIdentifier_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = unitIdentifier_AST;
	}
	
	public final void controlFmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlFmt_AST = null;
		
		AST tmp205_AST = null;
		tmp205_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp205_AST);
		match(LITERAL_fmt);
		controlFmt_AST = (AST)currentAST.root;
		returnAST = controlFmt_AST;
	}
	
	public final void controlUnit() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlUnit_AST = null;
		
		AST tmp206_AST = null;
		tmp206_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp206_AST);
		match(LITERAL_unit);
		controlUnit_AST = (AST)currentAST.root;
		returnAST = controlUnit_AST;
	}
	
	public final void controlRec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlRec_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		n = LT(1);
		n_AST = astFactory.create(n);
		astFactory.addASTChild(currentAST, n_AST);
		match(NAME);
		if (!(n.getText().compareToIgnoreCase("rec") == 0))
		  throw new SemanticException("n.getText().compareToIgnoreCase(\"rec\") == 0");
		if ( inputState.guessing==0 ) {
			n.setType(CTRLREC);
		}
		controlRec_AST = (AST)currentAST.root;
		returnAST = controlRec_AST;
	}
	
	public final void controlEnd() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlEnd_AST = null;
		
		AST tmp207_AST = null;
		tmp207_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp207_AST);
		match(LITERAL_end);
		controlEnd_AST = (AST)currentAST.root;
		returnAST = controlEnd_AST;
	}
	
	public final void controlIostat() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlIostat_AST = null;
		
		AST tmp208_AST = null;
		tmp208_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp208_AST);
		match(LITERAL_iostat);
		controlIostat_AST = (AST)currentAST.root;
		returnAST = controlIostat_AST;
	}
	
	public final void ioListItem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ioListItem_AST = null;
		
		boolean synPredMatched223 = false;
		if (((LA(1)==LPAREN) && (_tokenSet_12.member(LA(2))))) {
			int _m223 = mark();
			synPredMatched223 = true;
			inputState.guessing++;
			try {
				{
				match(LPAREN);
				ioList();
				match(COMMA);
				match(NAME);
				match(ASSIGN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched223 = false;
			}
			rewind(_m223);
inputState.guessing--;
		}
		if ( synPredMatched223 ) {
			ioImpliedDoList();
			astFactory.addASTChild(currentAST, returnAST);
			ioListItem_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_12.member(LA(1))) && (_tokenSet_21.member(LA(2)))) {
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			ioListItem_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = ioListItem_AST;
	}
	
	public final void ioImpliedDoList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ioImpliedDoList_AST = null;
		
		AST tmp209_AST = null;
		tmp209_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp209_AST);
		match(LPAREN);
		ioList();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp210_AST = null;
		tmp210_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp210_AST);
		match(COMMA);
		AST tmp211_AST = null;
		tmp211_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp211_AST);
		match(NAME);
		match(ASSIGN);
		intRealDpExpr();
		astFactory.addASTChild(currentAST, returnAST);
		match(COMMA);
		intRealDpExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==COMMA)) {
			match(COMMA);
			intRealDpExpr();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==RPAREN)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(RPAREN);
		ioImpliedDoList_AST = (AST)currentAST.root;
		returnAST = ioImpliedDoList_AST;
	}
	
	public final void openControl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST openControl_AST = null;
		
		switch ( LA(1)) {
		case NAME:
		case LPAREN:
		case STAR:
		case ICON:
		case MINUS:
		case PLUS:
		{
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_unit:
		{
			controlUnit();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp216_AST = null;
			tmp216_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp216_AST);
			match(ASSIGN);
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_err:
		{
			controlErrSpec();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_file:
		{
			controlFile();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp217_AST = null;
			tmp217_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp217_AST);
			match(ASSIGN);
			characterExpression();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_status:
		{
			controlStatus();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp218_AST = null;
			tmp218_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp218_AST);
			match(ASSIGN);
			characterExpression();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_access:
		case LITERAL_position:
		{
			{
			if ((LA(1)==LITERAL_access)) {
				controlAccess();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==LITERAL_position)) {
				controlPosition();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			AST tmp219_AST = null;
			tmp219_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp219_AST);
			match(ASSIGN);
			characterExpression();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_form:
		{
			controlForm();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp220_AST = null;
			tmp220_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp220_AST);
			match(ASSIGN);
			characterExpression();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_recl:
		{
			controlRecl();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp221_AST = null;
			tmp221_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp221_AST);
			match(ASSIGN);
			integerExpr();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_blank:
		{
			controlBlank();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp222_AST = null;
			tmp222_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp222_AST);
			match(ASSIGN);
			characterExpression();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_iostat:
		{
			controlIostat();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp223_AST = null;
			tmp223_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp223_AST);
			match(ASSIGN);
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			openControl_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = openControl_AST;
	}
	
	public final void controlFile() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlFile_AST = null;
		
		AST tmp224_AST = null;
		tmp224_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp224_AST);
		match(LITERAL_file);
		controlFile_AST = (AST)currentAST.root;
		returnAST = controlFile_AST;
	}
	
	public final void characterExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST characterExpression_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		characterExpression_AST = (AST)currentAST.root;
		returnAST = characterExpression_AST;
	}
	
	public final void controlStatus() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlStatus_AST = null;
		
		AST tmp225_AST = null;
		tmp225_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp225_AST);
		match(LITERAL_status);
		controlStatus_AST = (AST)currentAST.root;
		returnAST = controlStatus_AST;
	}
	
	public final void controlAccess() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlAccess_AST = null;
		
		AST tmp226_AST = null;
		tmp226_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp226_AST);
		match(LITERAL_access);
		controlAccess_AST = (AST)currentAST.root;
		returnAST = controlAccess_AST;
	}
	
	public final void controlPosition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlPosition_AST = null;
		
		AST tmp227_AST = null;
		tmp227_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp227_AST);
		match(LITERAL_position);
		controlPosition_AST = (AST)currentAST.root;
		returnAST = controlPosition_AST;
	}
	
	public final void controlForm() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlForm_AST = null;
		
		AST tmp228_AST = null;
		tmp228_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp228_AST);
		match(LITERAL_form);
		controlForm_AST = (AST)currentAST.root;
		returnAST = controlForm_AST;
	}
	
	public final void controlRecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlRecl_AST = null;
		
		AST tmp229_AST = null;
		tmp229_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp229_AST);
		match(LITERAL_recl);
		controlRecl_AST = (AST)currentAST.root;
		returnAST = controlRecl_AST;
	}
	
	public final void controlBlank() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlBlank_AST = null;
		
		AST tmp230_AST = null;
		tmp230_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp230_AST);
		match(LITERAL_blank);
		controlBlank_AST = (AST)currentAST.root;
		returnAST = controlBlank_AST;
	}
	
	public final void controlExist() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlExist_AST = null;
		
		AST tmp231_AST = null;
		tmp231_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp231_AST);
		match(LITERAL_exist);
		controlExist_AST = (AST)currentAST.root;
		returnAST = controlExist_AST;
	}
	
	public final void controlOpened() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlOpened_AST = null;
		
		AST tmp232_AST = null;
		tmp232_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp232_AST);
		match(LITERAL_opened);
		controlOpened_AST = (AST)currentAST.root;
		returnAST = controlOpened_AST;
	}
	
	public final void controlNumber() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlNumber_AST = null;
		
		AST tmp233_AST = null;
		tmp233_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp233_AST);
		match(LITERAL_number);
		controlNumber_AST = (AST)currentAST.root;
		returnAST = controlNumber_AST;
	}
	
	public final void controlNamed() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlNamed_AST = null;
		
		AST tmp234_AST = null;
		tmp234_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp234_AST);
		match(LITERAL_named);
		controlNamed_AST = (AST)currentAST.root;
		returnAST = controlNamed_AST;
	}
	
	public final void controlName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlName_AST = null;
		
		AST tmp235_AST = null;
		tmp235_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp235_AST);
		match(LITERAL_name);
		controlName_AST = (AST)currentAST.root;
		returnAST = controlName_AST;
	}
	
	public final void controlSequential() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlSequential_AST = null;
		
		AST tmp236_AST = null;
		tmp236_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp236_AST);
		match(LITERAL_sequential);
		controlSequential_AST = (AST)currentAST.root;
		returnAST = controlSequential_AST;
	}
	
	public final void controlDirect() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlDirect_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		n = LT(1);
		n_AST = astFactory.create(n);
		astFactory.addASTChild(currentAST, n_AST);
		match(NAME);
		if (!(n.getText().compareToIgnoreCase("direct") == 0))
		  throw new SemanticException("n.getText().compareToIgnoreCase(\"direct\") == 0");
		if ( inputState.guessing==0 ) {
			n.setType(CTRLDIRECT);
		}
		controlDirect_AST = (AST)currentAST.root;
		returnAST = controlDirect_AST;
	}
	
	public final void controlFormatted() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlFormatted_AST = null;
		
		AST tmp237_AST = null;
		tmp237_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp237_AST);
		match(LITERAL_formatted);
		controlFormatted_AST = (AST)currentAST.root;
		returnAST = controlFormatted_AST;
	}
	
	public final void controlUnformatted() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlUnformatted_AST = null;
		
		AST tmp238_AST = null;
		tmp238_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp238_AST);
		match(LITERAL_unformatted);
		controlUnformatted_AST = (AST)currentAST.root;
		returnAST = controlUnformatted_AST;
	}
	
	public final void controlNextrec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlNextrec_AST = null;
		
		AST tmp239_AST = null;
		tmp239_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp239_AST);
		match(LITERAL_nextrec);
		controlNextrec_AST = (AST)currentAST.root;
		returnAST = controlNextrec_AST;
	}
	
	public final void closeControl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST closeControl_AST = null;
		
		switch ( LA(1)) {
		case NAME:
		case LPAREN:
		case STAR:
		case ICON:
		case MINUS:
		case PLUS:
		{
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			closeControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_unit:
		{
			controlUnit();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp240_AST = null;
			tmp240_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp240_AST);
			match(ASSIGN);
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			closeControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_err:
		{
			controlErrSpec();
			astFactory.addASTChild(currentAST, returnAST);
			closeControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_status:
		{
			controlStatus();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp241_AST = null;
			tmp241_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp241_AST);
			match(ASSIGN);
			characterExpression();
			astFactory.addASTChild(currentAST, returnAST);
			closeControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_iostat:
		{
			controlIostat();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp242_AST = null;
			tmp242_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp242_AST);
			match(ASSIGN);
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			closeControl_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = closeControl_AST;
	}
	
	public final void inquireControl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inquireControl_AST = null;
		
		switch ( LA(1)) {
		case LITERAL_unit:
		{
			controlUnit();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp243_AST = null;
			tmp243_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp243_AST);
			match(ASSIGN);
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			inquireControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_file:
		{
			controlFile();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp244_AST = null;
			tmp244_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp244_AST);
			match(ASSIGN);
			characterExpression();
			astFactory.addASTChild(currentAST, returnAST);
			inquireControl_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_err:
		{
			controlErrSpec();
			astFactory.addASTChild(currentAST, returnAST);
			inquireControl_AST = (AST)currentAST.root;
			break;
		}
		default:
			if ((_tokenSet_25.member(LA(1))) && (LA(2)==ASSIGN)) {
				{
				switch ( LA(1)) {
				case LITERAL_iostat:
				{
					controlIostat();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_exist:
				{
					controlExist();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_opened:
				{
					controlOpened();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_number:
				{
					controlNumber();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_named:
				{
					controlNamed();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_name:
				{
					controlName();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_access:
				{
					controlAccess();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_sequential:
				{
					controlSequential();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case NAME:
				{
					controlDirect();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_form:
				{
					controlForm();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_formatted:
				{
					controlFormatted();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_unformatted:
				{
					controlUnformatted();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_recl:
				{
					controlRecl();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_nextrec:
				{
					controlNextrec();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LITERAL_blank:
				{
					controlBlank();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp245_AST = null;
				tmp245_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp245_AST);
				match(ASSIGN);
				varRef();
				astFactory.addASTChild(currentAST, returnAST);
				inquireControl_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_23.member(LA(1))) && (_tokenSet_24.member(LA(2)))) {
				unitIdentifier();
				astFactory.addASTChild(currentAST, returnAST);
				inquireControl_AST = (AST)currentAST.root;
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = inquireControl_AST;
	}
	
	public final void berFinish() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST berFinish_AST = null;
		
		{
		boolean synPredMatched269 = false;
		if (((_tokenSet_23.member(LA(1))) && (_tokenSet_26.member(LA(2))))) {
			int _m269 = mark();
			synPredMatched269 = true;
			inputState.guessing++;
			try {
				{
				unitIdentifier();
				match(EOS);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched269 = false;
			}
			rewind(_m269);
inputState.guessing--;
		}
		if ( synPredMatched269 ) {
			{
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			}
		}
		else if ((LA(1)==LPAREN) && (_tokenSet_27.member(LA(2)))) {
			AST tmp246_AST = null;
			tmp246_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp246_AST);
			match(LPAREN);
			berFinishItem();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop272:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					berFinishItem();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop272;
				}
				
			} while (true);
			}
			match(RPAREN);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		berFinish_AST = (AST)currentAST.root;
		returnAST = berFinish_AST;
	}
	
	public final void berFinishItem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST berFinishItem_AST = null;
		
		switch ( LA(1)) {
		case NAME:
		case LPAREN:
		case STAR:
		case ICON:
		case MINUS:
		case PLUS:
		{
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			berFinishItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_unit:
		{
			controlUnit();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp249_AST = null;
			tmp249_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp249_AST);
			match(ASSIGN);
			unitIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			berFinishItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_err:
		{
			controlErrSpec();
			astFactory.addASTChild(currentAST, returnAST);
			berFinishItem_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_iostat:
		{
			controlIostat();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp250_AST = null;
			tmp250_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp250_AST);
			match(ASSIGN);
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			berFinishItem_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = berFinishItem_AST;
	}
	
	public final void iexpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iexpr_AST = null;
		
		iexpr1();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop342:
		do {
			if ((LA(1)==MINUS||LA(1)==PLUS)) {
				{
				if ((LA(1)==PLUS)) {
					AST tmp251_AST = null;
					tmp251_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp251_AST);
					match(PLUS);
				}
				else if ((LA(1)==MINUS)) {
					AST tmp252_AST = null;
					tmp252_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp252_AST);
					match(MINUS);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				iexpr1();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop342;
			}
			
		} while (true);
		}
		iexpr_AST = (AST)currentAST.root;
		returnAST = iexpr_AST;
	}
	
	public final void fmtSpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fmtSpec_AST = null;
		
		{
		if ((_tokenSet_28.member(LA(1)))) {
			formatedit();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==COLON||LA(1)==DIV||LA(1)==DOLLAR)) {
			formatsep();
			astFactory.addASTChild(currentAST, returnAST);
			{
			if ((_tokenSet_28.member(LA(1)))) {
				formatedit();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==RPAREN||LA(1)==COMMA||LA(1)==COLON||LA(1)==DIV||LA(1)==DOLLAR)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		_loop284:
		do {
			if ((LA(1)==COLON||LA(1)==DIV||LA(1)==DOLLAR)) {
				formatsep();
				astFactory.addASTChild(currentAST, returnAST);
				{
				if ((_tokenSet_28.member(LA(1)))) {
					formatedit();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==RPAREN||LA(1)==COMMA||LA(1)==COLON||LA(1)==DIV||LA(1)==DOLLAR)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
			}
			else if ((LA(1)==COMMA)) {
				AST tmp253_AST = null;
				tmp253_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp253_AST);
				match(COMMA);
				{
				if ((_tokenSet_28.member(LA(1)))) {
					formatedit();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==COLON||LA(1)==DIV||LA(1)==DOLLAR)) {
					formatsep();
					astFactory.addASTChild(currentAST, returnAST);
					{
					if ((_tokenSet_28.member(LA(1)))) {
						formatedit();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else if ((LA(1)==RPAREN||LA(1)==COMMA||LA(1)==COLON||LA(1)==DIV||LA(1)==DOLLAR)) {
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
			}
			else {
				break _loop284;
			}
			
		} while (true);
		}
		fmtSpec_AST = (AST)currentAST.root;
		returnAST = fmtSpec_AST;
	}
	
	public final void formatedit() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formatedit_AST = null;
		
		switch ( LA(1)) {
		case XCON:
		{
			AST tmp254_AST = null;
			tmp254_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp254_AST);
			match(XCON);
			formatedit_AST = (AST)currentAST.root;
			break;
		}
		case FCON:
		case HOLLERITH:
		case NAME:
		case LPAREN:
		case SCON:
		{
			editElement();
			astFactory.addASTChild(currentAST, returnAST);
			formatedit_AST = (AST)currentAST.root;
			break;
		}
		case ICON:
		{
			AST tmp255_AST = null;
			tmp255_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp255_AST);
			match(ICON);
			editElement();
			astFactory.addASTChild(currentAST, returnAST);
			formatedit_AST = (AST)currentAST.root;
			break;
		}
		case PCON:
		case MINUS:
		case PLUS:
		{
			{
			switch ( LA(1)) {
			case PLUS:
			{
				AST tmp256_AST = null;
				tmp256_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp256_AST);
				match(PLUS);
				break;
			}
			case MINUS:
			{
				AST tmp257_AST = null;
				tmp257_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp257_AST);
				match(MINUS);
				break;
			}
			case PCON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp258_AST = null;
			tmp258_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp258_AST);
			match(PCON);
			{
			if ((_tokenSet_29.member(LA(1)))) {
				{
				if ((LA(1)==ICON)) {
					AST tmp259_AST = null;
					tmp259_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp259_AST);
					match(ICON);
				}
				else if ((LA(1)==FCON||LA(1)==HOLLERITH||LA(1)==NAME||LA(1)==LPAREN||LA(1)==SCON)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				editElement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==RPAREN||LA(1)==COMMA||LA(1)==COLON||LA(1)==DIV||LA(1)==DOLLAR)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			formatedit_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = formatedit_AST;
	}
	
	public final void formatsep() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formatsep_AST = null;
		
		switch ( LA(1)) {
		case DIV:
		{
			AST tmp260_AST = null;
			tmp260_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp260_AST);
			match(DIV);
			formatsep_AST = (AST)currentAST.root;
			break;
		}
		case COLON:
		{
			AST tmp261_AST = null;
			tmp261_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp261_AST);
			match(COLON);
			formatsep_AST = (AST)currentAST.root;
			break;
		}
		case DOLLAR:
		{
			AST tmp262_AST = null;
			tmp262_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp262_AST);
			match(DOLLAR);
			formatsep_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = formatsep_AST;
	}
	
	public final void editElement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST editElement_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		switch ( LA(1)) {
		case FCON:
		{
			AST tmp263_AST = null;
			tmp263_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp263_AST);
			match(FCON);
			editElement_AST = (AST)currentAST.root;
			break;
		}
		case SCON:
		{
			AST tmp264_AST = null;
			tmp264_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp264_AST);
			match(SCON);
			editElement_AST = (AST)currentAST.root;
			break;
		}
		case HOLLERITH:
		{
			AST tmp265_AST = null;
			tmp265_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp265_AST);
			match(HOLLERITH);
			editElement_AST = (AST)currentAST.root;
			break;
		}
		case NAME:
		{
			n = LT(1);
			n_AST = astFactory.create(n);
			astFactory.addASTChild(currentAST, n_AST);
			match(NAME);
			if ( inputState.guessing==0 ) {
				n.setType(FCON);
			}
			editElement_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			AST tmp266_AST = null;
			tmp266_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp266_AST);
			match(LPAREN);
			fmtSpec();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp267_AST = null;
			tmp267_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp267_AST);
			match(RPAREN);
			editElement_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = editElement_AST;
	}
	
	public final void sfArgs() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sfArgs_AST = null;
		
		AST tmp268_AST = null;
		tmp268_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp268_AST);
		match(NAME);
		match(LPAREN);
		namelist();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		sfArgs_AST = (AST)currentAST.root;
		returnAST = sfArgs_AST;
	}
	
	public final void subroutineCall() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subroutineCall_AST = null;
		
		AST tmp271_AST = null;
		tmp271_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp271_AST);
		match(NAME);
		{
		if ((LA(1)==LPAREN)) {
			match(LPAREN);
			{
			if ((_tokenSet_30.member(LA(1)))) {
				callArgumentList();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==RPAREN)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(RPAREN);
		}
		else if ((LA(1)==EOS)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		subroutineCall_AST = (AST)currentAST.root;
		returnAST = subroutineCall_AST;
	}
	
	public final void callArgumentList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST callArgumentList_AST = null;
		
		callArgument();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop299:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				callArgument();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop299;
			}
			
		} while (true);
		}
		callArgumentList_AST = (AST)currentAST.root;
		returnAST = callArgumentList_AST;
	}
	
	public final void callArgument() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST callArgument_AST = null;
		
		if ((_tokenSet_12.member(LA(1)))) {
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			callArgument_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==STAR)) {
			match(STAR);
			lblRef();
			astFactory.addASTChild(currentAST, returnAST);
			callArgument_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = callArgument_AST;
	}
	
	public final void ncExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ncExpr_AST = null;
		
		lexpr0();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop307:
		do {
			if (((LA(1)==DIV))&&(LA(2) == DIV)) {
				concatOp();
				astFactory.addASTChild(currentAST, returnAST);
				lexpr0();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop307;
			}
			
		} while (true);
		}
		ncExpr_AST = (AST)currentAST.root;
		returnAST = ncExpr_AST;
	}
	
	public final void lexpr0() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lexpr0_AST = null;
		
		lexpr1();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop311:
		do {
			if ((LA(1)==NEQV||LA(1)==EQV)) {
				{
				if ((LA(1)==NEQV)) {
					AST tmp276_AST = null;
					tmp276_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp276_AST);
					match(NEQV);
				}
				else if ((LA(1)==EQV)) {
					AST tmp277_AST = null;
					tmp277_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp277_AST);
					match(EQV);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				lexpr1();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop311;
			}
			
		} while (true);
		}
		lexpr0_AST = (AST)currentAST.root;
		returnAST = lexpr0_AST;
	}
	
	public final void concatOp() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concatOp_AST = null;
		
		AST tmp278_AST = null;
		tmp278_AST = astFactory.create(LT(1));
		match(DIV);
		AST tmp279_AST = null;
		tmp279_AST = astFactory.create(LT(1));
		match(DIV);
		if ( inputState.guessing==0 ) {
			concatOp_AST = (AST)currentAST.root;
			concatOp_AST = astFactory.create(CONCATOP,"//") ;
			currentAST.root = concatOp_AST;
			currentAST.child = concatOp_AST!=null &&concatOp_AST.getFirstChild()!=null ?
				concatOp_AST.getFirstChild() : concatOp_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = concatOp_AST;
	}
	
	public final void lexpr1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lexpr1_AST = null;
		
		lexpr2();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop314:
		do {
			if ((LA(1)==LOR)) {
				AST tmp280_AST = null;
				tmp280_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp280_AST);
				match(LOR);
				lexpr2();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop314;
			}
			
		} while (true);
		}
		lexpr1_AST = (AST)currentAST.root;
		returnAST = lexpr1_AST;
	}
	
	public final void lexpr2() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lexpr2_AST = null;
		
		lexpr3();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop317:
		do {
			if ((LA(1)==LAND)) {
				AST tmp281_AST = null;
				tmp281_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp281_AST);
				match(LAND);
				lexpr3();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop317;
			}
			
		} while (true);
		}
		lexpr2_AST = (AST)currentAST.root;
		returnAST = lexpr2_AST;
	}
	
	public final void lexpr3() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lexpr3_AST = null;
		
		if ((LA(1)==LNOT)) {
			AST tmp282_AST = null;
			tmp282_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp282_AST);
			match(LNOT);
			lexpr3();
			astFactory.addASTChild(currentAST, returnAST);
			lexpr3_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_31.member(LA(1)))) {
			lexpr4();
			astFactory.addASTChild(currentAST, returnAST);
			lexpr3_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = lexpr3_AST;
	}
	
	public final void lexpr4() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lexpr4_AST = null;
		
		aexpr0();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if (((LA(1) >= LT && LA(1) <= GE))) {
			{
			switch ( LA(1)) {
			case LT:
			{
				AST tmp283_AST = null;
				tmp283_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp283_AST);
				match(LT);
				break;
			}
			case LE:
			{
				AST tmp284_AST = null;
				tmp284_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp284_AST);
				match(LE);
				break;
			}
			case EQ:
			{
				AST tmp285_AST = null;
				tmp285_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp285_AST);
				match(EQ);
				break;
			}
			case NE:
			{
				AST tmp286_AST = null;
				tmp286_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp286_AST);
				match(NE);
				break;
			}
			case GT:
			{
				AST tmp287_AST = null;
				tmp287_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp287_AST);
				match(GT);
				break;
			}
			case GE:
			{
				AST tmp288_AST = null;
				tmp288_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp288_AST);
				match(GE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			aexpr0();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((_tokenSet_32.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		lexpr4_AST = (AST)currentAST.root;
		returnAST = lexpr4_AST;
	}
	
	public final void aexpr0() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aexpr0_AST = null;
		
		aexpr1();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop325:
		do {
			if ((LA(1)==MINUS||LA(1)==PLUS)) {
				{
				if ((LA(1)==PLUS)) {
					AST tmp289_AST = null;
					tmp289_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp289_AST);
					match(PLUS);
				}
				else if ((LA(1)==MINUS)) {
					AST tmp290_AST = null;
					tmp290_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp290_AST);
					match(MINUS);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				aexpr1();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop325;
			}
			
		} while (true);
		}
		aexpr0_AST = (AST)currentAST.root;
		returnAST = aexpr0_AST;
	}
	
	public final void aexpr1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aexpr1_AST = null;
		
		aexpr2();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop329:
		do {
			if ((LA(1)==STAR||LA(1)==DIV) && (_tokenSet_31.member(LA(2)))) {
				{
				if ((LA(1)==STAR)) {
					AST tmp291_AST = null;
					tmp291_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp291_AST);
					match(STAR);
				}
				else if ((LA(1)==DIV)) {
					AST tmp292_AST = null;
					tmp292_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp292_AST);
					match(DIV);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				aexpr2();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop329;
			}
			
		} while (true);
		}
		aexpr1_AST = (AST)currentAST.root;
		returnAST = aexpr1_AST;
	}
	
	public final void aexpr2() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aexpr2_AST = null;
		
		{
		_loop332:
		do {
			if ((LA(1)==PLUS)) {
				AST tmp293_AST = null;
				tmp293_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp293_AST);
				match(PLUS);
			}
			else if ((LA(1)==MINUS)) {
				AST tmp294_AST = null;
				tmp294_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp294_AST);
				match(MINUS);
			}
			else {
				break _loop332;
			}
			
		} while (true);
		}
		aexpr3();
		astFactory.addASTChild(currentAST, returnAST);
		aexpr2_AST = (AST)currentAST.root;
		returnAST = aexpr2_AST;
	}
	
	public final void aexpr3() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aexpr3_AST = null;
		
		aexpr4();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop335:
		do {
			if ((LA(1)==POWER)) {
				AST tmp295_AST = null;
				tmp295_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp295_AST);
				match(POWER);
				aexpr4();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop335;
			}
			
		} while (true);
		}
		aexpr3_AST = (AST)currentAST.root;
		returnAST = aexpr3_AST;
	}
	
	public final void aexpr4() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aexpr4_AST = null;
		
		switch ( LA(1)) {
		case HOLLERITH:
		{
			AST tmp296_AST = null;
			tmp296_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp296_AST);
			match(HOLLERITH);
			aexpr4_AST = (AST)currentAST.root;
			break;
		}
		case SCON:
		{
			AST tmp297_AST = null;
			tmp297_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp297_AST);
			match(SCON);
			aexpr4_AST = (AST)currentAST.root;
			break;
		}
		case TRUE:
		case FALSE:
		{
			logicalConstant();
			astFactory.addASTChild(currentAST, returnAST);
			aexpr4_AST = (AST)currentAST.root;
			break;
		}
		case NAME:
		case LITERAL_real:
		{
			varRef();
			astFactory.addASTChild(currentAST, returnAST);
			aexpr4_AST = (AST)currentAST.root;
			break;
		}
		default:
			boolean synPredMatched338 = false;
			if (((LA(1)==RCON||LA(1)==LPAREN||LA(1)==ICON) && (_tokenSet_33.member(LA(2))))) {
				int _m338 = mark();
				synPredMatched338 = true;
				inputState.guessing++;
				try {
					{
					unsignedArithmeticConstant();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched338 = false;
				}
				rewind(_m338);
inputState.guessing--;
			}
			if ( synPredMatched338 ) {
				unsignedArithmeticConstant();
				astFactory.addASTChild(currentAST, returnAST);
				aexpr4_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==LPAREN) && (_tokenSet_12.member(LA(2)))) {
				match(LPAREN);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				aexpr4_AST = (AST)currentAST.root;
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = aexpr4_AST;
	}
	
	public final void unsignedArithmeticConstant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unsignedArithmeticConstant_AST = null;
		
		switch ( LA(1)) {
		case ICON:
		{
			AST tmp300_AST = null;
			tmp300_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp300_AST);
			match(ICON);
			unsignedArithmeticConstant_AST = (AST)currentAST.root;
			break;
		}
		case RCON:
		{
			AST tmp301_AST = null;
			tmp301_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp301_AST);
			match(RCON);
			unsignedArithmeticConstant_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			complexConstant();
			astFactory.addASTChild(currentAST, returnAST);
			unsignedArithmeticConstant_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = unsignedArithmeticConstant_AST;
	}
	
	public final void logicalConstant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalConstant_AST = null;
		
		{
		if ((LA(1)==TRUE)) {
			AST tmp302_AST = null;
			tmp302_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp302_AST);
			match(TRUE);
		}
		else if ((LA(1)==FALSE)) {
			AST tmp303_AST = null;
			tmp303_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp303_AST);
			match(FALSE);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		logicalConstant_AST = (AST)currentAST.root;
		returnAST = logicalConstant_AST;
	}
	
	public final void iexpr1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iexpr1_AST = null;
		
		iexpr2();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop350:
		do {
			if ((LA(1)==STAR||LA(1)==DIV)) {
				{
				if ((LA(1)==STAR)) {
					AST tmp304_AST = null;
					tmp304_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp304_AST);
					match(STAR);
				}
				else if ((LA(1)==DIV)) {
					AST tmp305_AST = null;
					tmp305_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp305_AST);
					match(DIV);
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				iexpr2();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop350;
			}
			
		} while (true);
		}
		iexpr1_AST = (AST)currentAST.root;
		returnAST = iexpr1_AST;
	}
	
	public final void iexpr2() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iexpr2_AST = null;
		
		{
		_loop353:
		do {
			if ((LA(1)==PLUS)) {
				AST tmp306_AST = null;
				tmp306_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp306_AST);
				match(PLUS);
			}
			else if ((LA(1)==MINUS)) {
				AST tmp307_AST = null;
				tmp307_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp307_AST);
				match(MINUS);
			}
			else {
				break _loop353;
			}
			
		} while (true);
		}
		iexpr3();
		astFactory.addASTChild(currentAST, returnAST);
		iexpr2_AST = (AST)currentAST.root;
		returnAST = iexpr2_AST;
	}
	
	public final void iexpr3() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iexpr3_AST = null;
		
		iexpr4();
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==POWER)) {
			AST tmp308_AST = null;
			tmp308_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp308_AST);
			match(POWER);
			iexpr3();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((_tokenSet_34.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		iexpr3_AST = (AST)currentAST.root;
		returnAST = iexpr3_AST;
	}
	
	public final void iexpr4() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iexpr4_AST = null;
		
		switch ( LA(1)) {
		case ICON:
		{
			AST tmp309_AST = null;
			tmp309_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp309_AST);
			match(ICON);
			iexpr4_AST = (AST)currentAST.root;
			break;
		}
		case NAME:
		{
			varRefCode();
			astFactory.addASTChild(currentAST, returnAST);
			iexpr4_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			AST tmp310_AST = null;
			tmp310_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp310_AST);
			match(LPAREN);
			iexprCode();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp311_AST = null;
			tmp311_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp311_AST);
			match(RPAREN);
			iexpr4_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = iexpr4_AST;
	}
	
	public final void varRefCode() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varRefCode_AST = null;
		
		AST tmp312_AST = null;
		tmp312_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp312_AST);
		match(NAME);
		{
		if ((LA(1)==LPAREN)) {
			subscripts();
			astFactory.addASTChild(currentAST, returnAST);
			{
			if ((LA(1)==LPAREN)) {
				substringApp();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_35.member(LA(1)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else if ((_tokenSet_35.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		varRefCode_AST = (AST)currentAST.root;
		returnAST = varRefCode_AST;
	}
	
	public final void arithmeticExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arithmeticExpression_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		arithmeticExpression_AST = (AST)currentAST.root;
		returnAST = arithmeticExpression_AST;
	}
	
	public final void arithmeticConstExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arithmeticConstExpr_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		arithmeticConstExpr_AST = (AST)currentAST.root;
		returnAST = arithmeticConstExpr_AST;
	}
	
	public final void logicalConstExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalConstExpr_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		logicalConstExpr_AST = (AST)currentAST.root;
		returnAST = logicalConstExpr_AST;
	}
	
	public final void arrayElementName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayElementName_AST = null;
		
		AST tmp313_AST = null;
		tmp313_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp313_AST);
		match(NAME);
		match(LPAREN);
		integerExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop369:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				integerExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop369;
			}
			
		} while (true);
		}
		match(RPAREN);
		arrayElementName_AST = (AST)currentAST.root;
		returnAST = arrayElementName_AST;
	}
	
	public final void subscripts() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subscripts_AST = null;
		
		match(LPAREN);
		{
		if ((_tokenSet_12.member(LA(1)))) {
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop373:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					expression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop373;
				}
				
			} while (true);
			}
		}
		else if ((LA(1)==RPAREN)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(RPAREN);
		subscripts_AST = (AST)currentAST.root;
		returnAST = subscripts_AST;
	}
	
	public final void substringApp() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST substringApp_AST = null;
		
		match(LPAREN);
		{
		if ((_tokenSet_12.member(LA(1)))) {
			ncExpr();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==COLON)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		AST tmp321_AST = null;
		tmp321_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp321_AST);
		match(COLON);
		{
		if ((_tokenSet_12.member(LA(1)))) {
			ncExpr();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==RPAREN)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(RPAREN);
		substringApp_AST = (AST)currentAST.root;
		returnAST = substringApp_AST;
	}
	
	public final void arrayName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayName_AST = null;
		
		AST tmp323_AST = null;
		tmp323_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp323_AST);
		match(NAME);
		arrayName_AST = (AST)currentAST.root;
		returnAST = arrayName_AST;
	}
	
	public final void subroutineName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subroutineName_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		s = LT(1);
		s_AST = astFactory.create(s);
		astFactory.addASTChild(currentAST, s_AST);
		match(NAME);
		subroutineName_AST = (AST)currentAST.root;
		returnAST = subroutineName_AST;
	}
	
	public final void functionName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionName_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		s = LT(1);
		s_AST = astFactory.create(s);
		astFactory.addASTChild(currentAST, s_AST);
		match(NAME);
		functionName_AST = (AST)currentAST.root;
		returnAST = functionName_AST;
	}
	
	public final void complexConstant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST complexConstant_AST = null;
		Token  p1 = null;
		AST p1_AST = null;
		Token  m1 = null;
		AST m1_AST = null;
		Token  i1 = null;
		AST i1_AST = null;
		Token  r1 = null;
		AST r1_AST = null;
		Token  p2 = null;
		AST p2_AST = null;
		Token  m2 = null;
		AST m2_AST = null;
		Token  i2 = null;
		AST i2_AST = null;
		Token  r2 = null;
		AST r2_AST = null;
		
		match(LPAREN);
		{
		if ((LA(1)==MINUS||LA(1)==PLUS)) {
			{
			if ((LA(1)==PLUS)) {
				p1 = LT(1);
				p1_AST = astFactory.create(p1);
				match(PLUS);
			}
			else if ((LA(1)==MINUS)) {
				m1 = LT(1);
				m1_AST = astFactory.create(m1);
				match(MINUS);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else if ((LA(1)==RCON||LA(1)==ICON)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		if ((LA(1)==ICON)) {
			i1 = LT(1);
			i1_AST = astFactory.create(i1);
			match(ICON);
		}
		else if ((LA(1)==RCON)) {
			r1 = LT(1);
			r1_AST = astFactory.create(r1);
			match(RCON);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(COMMA);
		{
		if ((LA(1)==MINUS||LA(1)==PLUS)) {
			{
			if ((LA(1)==PLUS)) {
				p2 = LT(1);
				p2_AST = astFactory.create(p2);
				match(PLUS);
			}
			else if ((LA(1)==MINUS)) {
				m2 = LT(1);
				m2_AST = astFactory.create(m2);
				match(MINUS);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
		}
		else if ((LA(1)==RCON||LA(1)==ICON)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		if ((LA(1)==ICON)) {
			i2 = LT(1);
			i2_AST = astFactory.create(i2);
			match(ICON);
		}
		else if ((LA(1)==RCON)) {
			r2 = LT(1);
			r2_AST = astFactory.create(r2);
			match(RCON);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(RPAREN);
		if (!((i1 != null && i2 != null) || (r1 != null && r2 != null)))
		  throw new SemanticException("(i1 != null && i2 != null) || (r1 != null && r2 != null)");
		if ( inputState.guessing==0 ) {
			complexConstant_AST = (AST)currentAST.root;
			
					AST re, im;
					if (i1 != null)
					{
						re = i1_AST;
						im = i2_AST;
					}
					else // if (r1 != null)
					{
						re = r1_AST;
						im = r2_AST;
					}
					
					if (p1 != null)
						re = (AST)astFactory.make( (new ASTArray(2)).add(p1_AST).add(re));
					else if (m1 != null)
						re = (AST)astFactory.make( (new ASTArray(2)).add(m1_AST).add(re));
			
					if (p2 != null)
						im = (AST)astFactory.make( (new ASTArray(2)).add(p2_AST).add(im));
					else if (m2 != null)
						im = (AST)astFactory.make( (new ASTArray(2)).add(m2_AST).add(im));
					
					complexConstant_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CCON,"[complex]")).add(re).add(im));
				
			currentAST.root = complexConstant_AST;
			currentAST.child = complexConstant_AST!=null &&complexConstant_AST.getFirstChild()!=null ?
				complexConstant_AST.getFirstChild() : complexConstant_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = complexConstant_AST;
	}
	
	public final void keyword() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST keyword_AST = null;
		
		switch ( LA(1)) {
		case LITERAL_program:
		{
			AST tmp327_AST = null;
			tmp327_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp327_AST);
			match(LITERAL_program);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_entry:
		{
			AST tmp328_AST = null;
			tmp328_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp328_AST);
			match(LITERAL_entry);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_function:
		{
			AST tmp329_AST = null;
			tmp329_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp329_AST);
			match(LITERAL_function);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_block:
		{
			AST tmp330_AST = null;
			tmp330_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp330_AST);
			match(LITERAL_block);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_subroutine:
		{
			AST tmp331_AST = null;
			tmp331_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp331_AST);
			match(LITERAL_subroutine);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_end:
		{
			AST tmp332_AST = null;
			tmp332_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp332_AST);
			match(LITERAL_end);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_dimension:
		{
			AST tmp333_AST = null;
			tmp333_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp333_AST);
			match(LITERAL_dimension);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_equivalence:
		{
			AST tmp334_AST = null;
			tmp334_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp334_AST);
			match(LITERAL_equivalence);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_common:
		{
			AST tmp335_AST = null;
			tmp335_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp335_AST);
			match(LITERAL_common);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_real:
		{
			AST tmp336_AST = null;
			tmp336_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp336_AST);
			match(LITERAL_real);
			AST tmp337_AST = null;
			tmp337_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp337_AST);
			match(LITERAL_complex);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_double:
		{
			AST tmp338_AST = null;
			tmp338_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp338_AST);
			match(LITERAL_double);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_precision:
		{
			AST tmp339_AST = null;
			tmp339_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp339_AST);
			match(LITERAL_precision);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_integer:
		{
			AST tmp340_AST = null;
			tmp340_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp340_AST);
			match(LITERAL_integer);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_logical:
		{
			AST tmp341_AST = null;
			tmp341_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp341_AST);
			match(LITERAL_logical);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_pointer:
		{
			AST tmp342_AST = null;
			tmp342_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp342_AST);
			match(LITERAL_pointer);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_implicit:
		{
			AST tmp343_AST = null;
			tmp343_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp343_AST);
			match(LITERAL_implicit);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_none:
		{
			AST tmp344_AST = null;
			tmp344_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp344_AST);
			match(LITERAL_none);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_character:
		{
			AST tmp345_AST = null;
			tmp345_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp345_AST);
			match(LITERAL_character);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_parameter:
		{
			AST tmp346_AST = null;
			tmp346_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp346_AST);
			match(LITERAL_parameter);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_external:
		{
			AST tmp347_AST = null;
			tmp347_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp347_AST);
			match(LITERAL_external);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_intrinsic:
		{
			AST tmp348_AST = null;
			tmp348_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp348_AST);
			match(LITERAL_intrinsic);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_save:
		{
			AST tmp349_AST = null;
			tmp349_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp349_AST);
			match(LITERAL_save);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_data:
		{
			AST tmp350_AST = null;
			tmp350_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp350_AST);
			match(LITERAL_data);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_assign:
		{
			AST tmp351_AST = null;
			tmp351_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp351_AST);
			match(LITERAL_assign);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_goto:
		{
			AST tmp352_AST = null;
			tmp352_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp352_AST);
			match(LITERAL_goto);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_go:
		{
			AST tmp353_AST = null;
			tmp353_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp353_AST);
			match(LITERAL_go);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_if:
		{
			AST tmp354_AST = null;
			tmp354_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp354_AST);
			match(LITERAL_if);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_then:
		{
			AST tmp355_AST = null;
			tmp355_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp355_AST);
			match(LITERAL_then);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_elseif:
		{
			AST tmp356_AST = null;
			tmp356_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp356_AST);
			match(LITERAL_elseif);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_else:
		{
			AST tmp357_AST = null;
			tmp357_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp357_AST);
			match(LITERAL_else);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_endif:
		{
			AST tmp358_AST = null;
			tmp358_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp358_AST);
			match(LITERAL_endif);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_do:
		{
			AST tmp359_AST = null;
			tmp359_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp359_AST);
			match(LITERAL_do);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_enddo:
		{
			AST tmp360_AST = null;
			tmp360_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp360_AST);
			match(LITERAL_enddo);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_continue:
		{
			AST tmp361_AST = null;
			tmp361_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp361_AST);
			match(LITERAL_continue);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_stop:
		{
			AST tmp362_AST = null;
			tmp362_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp362_AST);
			match(LITERAL_stop);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_pause:
		{
			AST tmp363_AST = null;
			tmp363_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp363_AST);
			match(LITERAL_pause);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_write:
		{
			AST tmp364_AST = null;
			tmp364_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp364_AST);
			match(LITERAL_write);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_read:
		{
			AST tmp365_AST = null;
			tmp365_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp365_AST);
			match(LITERAL_read);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_print:
		{
			AST tmp366_AST = null;
			tmp366_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp366_AST);
			match(LITERAL_print);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_open:
		{
			AST tmp367_AST = null;
			tmp367_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp367_AST);
			match(LITERAL_open);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_fmt:
		{
			AST tmp368_AST = null;
			tmp368_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp368_AST);
			match(LITERAL_fmt);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_unit:
		{
			AST tmp369_AST = null;
			tmp369_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp369_AST);
			match(LITERAL_unit);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_err:
		{
			AST tmp370_AST = null;
			tmp370_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp370_AST);
			match(LITERAL_err);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_iostat:
		{
			AST tmp371_AST = null;
			tmp371_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp371_AST);
			match(LITERAL_iostat);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_file:
		{
			AST tmp372_AST = null;
			tmp372_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp372_AST);
			match(LITERAL_file);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_status:
		{
			AST tmp373_AST = null;
			tmp373_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp373_AST);
			match(LITERAL_status);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_access:
		{
			AST tmp374_AST = null;
			tmp374_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp374_AST);
			match(LITERAL_access);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_position:
		{
			AST tmp375_AST = null;
			tmp375_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp375_AST);
			match(LITERAL_position);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_form:
		{
			AST tmp376_AST = null;
			tmp376_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp376_AST);
			match(LITERAL_form);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_recl:
		{
			AST tmp377_AST = null;
			tmp377_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp377_AST);
			match(LITERAL_recl);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_blank:
		{
			AST tmp378_AST = null;
			tmp378_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp378_AST);
			match(LITERAL_blank);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_exist:
		{
			AST tmp379_AST = null;
			tmp379_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp379_AST);
			match(LITERAL_exist);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_opened:
		{
			AST tmp380_AST = null;
			tmp380_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp380_AST);
			match(LITERAL_opened);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_number:
		{
			AST tmp381_AST = null;
			tmp381_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp381_AST);
			match(LITERAL_number);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_named:
		{
			AST tmp382_AST = null;
			tmp382_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp382_AST);
			match(LITERAL_named);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_name:
		{
			AST tmp383_AST = null;
			tmp383_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp383_AST);
			match(LITERAL_name);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_sequential:
		{
			AST tmp384_AST = null;
			tmp384_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp384_AST);
			match(LITERAL_sequential);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_formatted:
		{
			AST tmp385_AST = null;
			tmp385_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp385_AST);
			match(LITERAL_formatted);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_unformatted:
		{
			AST tmp386_AST = null;
			tmp386_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp386_AST);
			match(LITERAL_unformatted);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_nextrec:
		{
			AST tmp387_AST = null;
			tmp387_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp387_AST);
			match(LITERAL_nextrec);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_close:
		{
			AST tmp388_AST = null;
			tmp388_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp388_AST);
			match(LITERAL_close);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_inquire:
		{
			AST tmp389_AST = null;
			tmp389_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp389_AST);
			match(LITERAL_inquire);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_backspace:
		{
			AST tmp390_AST = null;
			tmp390_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp390_AST);
			match(LITERAL_backspace);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_endfile:
		{
			AST tmp391_AST = null;
			tmp391_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp391_AST);
			match(LITERAL_endfile);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_rewind:
		{
			AST tmp392_AST = null;
			tmp392_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp392_AST);
			match(LITERAL_rewind);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_format:
		{
			AST tmp393_AST = null;
			tmp393_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp393_AST);
			match(LITERAL_format);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_let:
		{
			AST tmp394_AST = null;
			tmp394_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp394_AST);
			match(LITERAL_let);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_call:
		{
			AST tmp395_AST = null;
			tmp395_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp395_AST);
			match(LITERAL_call);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_return:
		{
			AST tmp396_AST = null;
			tmp396_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp396_AST);
			match(LITERAL_return);
			keyword_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = keyword_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"LABELREF",
		"XCON",
		"PCON",
		"FCON",
		"RCON",
		"CCON",
		"HOLLERITH",
		"CONCATOP",
		"CTRLDIRECT",
		"CTRLREC",
		"TO",
		"SUBPROGRAMBLOCK",
		"DOBLOCK",
		"AIF",
		"THENBLOCK",
		"ELSEIF",
		"ELSEBLOCK",
		"CODEROOT",
		"COMMENT",
		"PROGRAM",
		"NAME",
		"EOS",
		"ENTRY",
		"LPAREN",
		"RPAREN",
		"FUNCTION",
		"BLOCK",
		"SUBROUTINE",
		"COMMA",
		"LABEL",
		"END",
		"DIMENSION",
		"REAL",
		"COLON",
		"STAR",
		"EQUIVALENCE",
		"COMMON",
		"DIV",
		"COMPLEX",
		"ICON",
		"DOUBLE",
		"PRECISION",
		"INTEGER",
		"LOGICAL",
		"POINTER",
		"IMPLICIT",
		"NONE",
		"MINUS",
		"CHARACTER",
		"PARAMETER",
		"ASSIGN",
		"EXTERNAL",
		"INTRINSIC",
		"SAVE",
		"DATA",
		"GOTO",
		"GO",
		"IF",
		"THEN",
		"\"else\"",
		"\"if\"",
		"\"then\"",
		"ELSE",
		"ENDIF",
		"\"end\"",
		"DO",
		"ENDDO",
		"\"do\"",
		"CONTINUE",
		"STOP",
		"PAUSE",
		"WRITE",
		"READ",
		"PRINT",
		"SCON",
		"OPEN",
		"\"fmt\"",
		"\"unit\"",
		"\"err\"",
		"\"iostat\"",
		"\"file\"",
		"\"status\"",
		"\"access\"",
		"\"position\"",
		"\"form\"",
		"\"recl\"",
		"\"blank\"",
		"\"exist\"",
		"\"opened\"",
		"\"number\"",
		"\"named\"",
		"\"name\"",
		"\"sequential\"",
		"\"formatted\"",
		"\"unformatted\"",
		"\"nextrec\"",
		"CLOSE",
		"INQUIRE",
		"BACKSPACE",
		"ENDFILE",
		"REWIND",
		"FORMAT",
		"DOLLAR",
		"PLUS",
		"LET",
		"CALL",
		"RETURN",
		"NEQV",
		"EQV",
		"LOR",
		"LAND",
		"LNOT",
		"LT",
		"LE",
		"EQ",
		"NE",
		"GT",
		"GE",
		"POWER",
		"\"real\"",
		"TRUE",
		"FALSE",
		"\"program\"",
		"\"entry\"",
		"\"function\"",
		"\"block\"",
		"\"subroutine\"",
		"\"dimension\"",
		"\"equivalence\"",
		"\"common\"",
		"\"complex\"",
		"\"double\"",
		"\"precision\"",
		"\"integer\"",
		"\"logical\"",
		"\"pointer\"",
		"\"implicit\"",
		"\"none\"",
		"\"character\"",
		"\"parameter\"",
		"\"external\"",
		"\"intrinsic\"",
		"\"save\"",
		"\"data\"",
		"\"assign\"",
		"\"goto\"",
		"\"go\"",
		"\"elseif\"",
		"\"endif\"",
		"\"enddo\"",
		"\"continue\"",
		"\"stop\"",
		"\"pause\"",
		"\"write\"",
		"\"read\"",
		"\"print\"",
		"\"open\"",
		"\"close\"",
		"\"inquire\"",
		"\"backspace\"",
		"\"endfile\"",
		"\"rewind\"",
		"\"format\"",
		"\"let\"",
		"\"call\"",
		"\"return\"",
		"ASSIGN1",
		"XOR",
		"EOR",
		"CONTINUATION",
		"WS",
		"ZCON",
		"WHITE",
		"ALPHA",
		"NUM",
		"ALNUM",
		"HEX",
		"SIGN",
		"NOTNL",
		"INTVAL",
		"FDESC",
		"EXPON"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 4608261722166263808L, 576588226932817696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 4611685873191224320L, 576597023025856288L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 4608261722166263810L, 576588226932817696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 4736765348806656L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 4608261718408167424L, 576588226932817696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 4611685872654353408L, 576597023025856288L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 4736764811935744L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 4608261718399778816L, 576588226932817696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 4608261701219909632L, 576588226932817696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 2260870935610368L, 8796093038592L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 2263074287386624L, 288239172244733952L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 2260870935610368L, 8796094021648L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 2260596057703680L, 4037485862030688256L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 4608261701215715328L, 576588226932817696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 4608261692625780736L, 576588226932817696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 4610516116970668034L, 1150665238027222896L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 2260596057703680L, 3458773309913579520L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 2262799359148288L, 8796093022208L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 2260596040926464L, 3458773309913579520L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 4608261705816866818L, 576588226932817776L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 4608261701253464064L, 576588226932817776L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 2263211726341376L, 4611554077032071168L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 2263211692786944L, 4611554077032071168L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 2260870935609344L, 8796093022208L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 2263074522267648L, 288239172244733952L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 16777216L, 68707418112L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 2263069992419328L, 288239172244733952L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 2260870935609344L, 8796093939712L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 2260596057703648L, 8796093038592L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 8796244018304L, 16384L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 2260870935610624L, 4037485862030688256L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 2260596057703680L, 4035234062217003008L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { 4608264042279075842L, 578699289258147696L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = { 4610524913063690498L, 1150665238027222896L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = { 2254415750758400L, 8796093022208L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = { 2254415750758400L, 288239172244733952L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	
	}

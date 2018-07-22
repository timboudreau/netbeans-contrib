// $ANTLR 2.7.7 (20060906): "f77-antlr2.g" -> "Fortran77Lexer.java"$

package org.netbeans.modules.fortran.generated;
import org.netbeans.modules.fortran.ast.TokenAST;
import antlr.CommonToken;
import java.util.HashSet;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class Fortran77Lexer extends antlr.CharScanner implements Fortran77TokenTypes, TokenStream
 {
public Fortran77Lexer(InputStream in) {
	this(new ByteBuffer(in));
}
public Fortran77Lexer(Reader in) {
	this(new CharBuffer(in));
}
public Fortran77Lexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public Fortran77Lexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = false;
	setCaseSensitive(false);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("complex", this), new Integer(134));
	literals.put(new ANTLRHashString("name", this), new Integer(95));
	literals.put(new ANTLRHashString("save", this), new Integer(146));
	literals.put(new ANTLRHashString("call", this), new Integer(168));
	literals.put(new ANTLRHashString("endif", this), new Integer(152));
	literals.put(new ANTLRHashString("end", this), new Integer(68));
	literals.put(new ANTLRHashString("format", this), new Integer(166));
	literals.put(new ANTLRHashString("form", this), new Integer(88));
	literals.put(new ANTLRHashString("exist", this), new Integer(91));
	literals.put(new ANTLRHashString("integer", this), new Integer(137));
	literals.put(new ANTLRHashString("write", this), new Integer(157));
	literals.put(new ANTLRHashString("then", this), new Integer(65));
	literals.put(new ANTLRHashString("program", this), new Integer(126));
	literals.put(new ANTLRHashString("logical", this), new Integer(138));
	literals.put(new ANTLRHashString("print", this), new Integer(159));
	literals.put(new ANTLRHashString("unformatted", this), new Integer(98));
	literals.put(new ANTLRHashString("nextrec", this), new Integer(99));
	literals.put(new ANTLRHashString("named", this), new Integer(94));
	literals.put(new ANTLRHashString("return", this), new Integer(169));
	literals.put(new ANTLRHashString("position", this), new Integer(87));
	literals.put(new ANTLRHashString("external", this), new Integer(144));
	literals.put(new ANTLRHashString("pause", this), new Integer(156));
	literals.put(new ANTLRHashString("opened", this), new Integer(92));
	literals.put(new ANTLRHashString("recl", this), new Integer(89));
	literals.put(new ANTLRHashString("blank", this), new Integer(90));
	literals.put(new ANTLRHashString("iostat", this), new Integer(83));
	literals.put(new ANTLRHashString("real", this), new Integer(123));
	literals.put(new ANTLRHashString("sequential", this), new Integer(96));
	literals.put(new ANTLRHashString("open", this), new Integer(160));
	literals.put(new ANTLRHashString("status", this), new Integer(85));
	literals.put(new ANTLRHashString("do", this), new Integer(71));
	literals.put(new ANTLRHashString("character", this), new Integer(142));
	literals.put(new ANTLRHashString("function", this), new Integer(128));
	literals.put(new ANTLRHashString("entry", this), new Integer(127));
	literals.put(new ANTLRHashString("dimension", this), new Integer(131));
	literals.put(new ANTLRHashString("elseif", this), new Integer(151));
	literals.put(new ANTLRHashString("endfile", this), new Integer(164));
	literals.put(new ANTLRHashString("parameter", this), new Integer(143));
	literals.put(new ANTLRHashString("close", this), new Integer(161));
	literals.put(new ANTLRHashString("file", this), new Integer(84));
	literals.put(new ANTLRHashString("access", this), new Integer(86));
	literals.put(new ANTLRHashString("fmt", this), new Integer(80));
	literals.put(new ANTLRHashString("assign", this), new Integer(148));
	literals.put(new ANTLRHashString("none", this), new Integer(141));
	literals.put(new ANTLRHashString("if", this), new Integer(64));
	literals.put(new ANTLRHashString("double", this), new Integer(135));
	literals.put(new ANTLRHashString("intrinsic", this), new Integer(145));
	literals.put(new ANTLRHashString("formatted", this), new Integer(97));
	literals.put(new ANTLRHashString("implicit", this), new Integer(140));
	literals.put(new ANTLRHashString("data", this), new Integer(147));
	literals.put(new ANTLRHashString("subroutine", this), new Integer(130));
	literals.put(new ANTLRHashString("rewind", this), new Integer(165));
	literals.put(new ANTLRHashString("goto", this), new Integer(149));
	literals.put(new ANTLRHashString("number", this), new Integer(93));
	literals.put(new ANTLRHashString("backspace", this), new Integer(163));
	literals.put(new ANTLRHashString("unit", this), new Integer(81));
	literals.put(new ANTLRHashString("pointer", this), new Integer(139));
	literals.put(new ANTLRHashString("inquire", this), new Integer(162));
	literals.put(new ANTLRHashString("equivalence", this), new Integer(132));
	literals.put(new ANTLRHashString("stop", this), new Integer(155));
	literals.put(new ANTLRHashString("continue", this), new Integer(154));
	literals.put(new ANTLRHashString("go", this), new Integer(150));
	literals.put(new ANTLRHashString("else", this), new Integer(63));
	literals.put(new ANTLRHashString("let", this), new Integer(167));
	literals.put(new ANTLRHashString("enddo", this), new Integer(153));
	literals.put(new ANTLRHashString("block", this), new Integer(129));
	literals.put(new ANTLRHashString("precision", this), new Integer(136));
	literals.put(new ANTLRHashString("common", this), new Integer(133));
	literals.put(new ANTLRHashString("err", this), new Integer(82));
	literals.put(new ANTLRHashString("read", this), new Integer(158));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '$':
				{
					mDOLLAR(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mLPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mRPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case ':':
				{
					mCOLON(true);
					theRetToken=_returnToken;
					break;
				}
				case '=':
				{
					mASSIGN(true);
					theRetToken=_returnToken;
					break;
				}
				case '-':
				{
					mMINUS(true);
					theRetToken=_returnToken;
					break;
				}
				case '+':
				{
					mPLUS(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mDIV(true);
					theRetToken=_returnToken;
					break;
				}
				case '\n':  case '\r':
				{
					mEOS(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case ' ':
				{
					mWS(true);
					theRetToken=_returnToken;
					break;
				}
				case '\'':
				{
					mSCON(true);
					theRetToken=_returnToken;
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mICON(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='p') && (LA(2)=='r') && (LA(3)=='o') && (LA(4)=='g')) {
						mPROGRAM(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='n') && (LA(3)=='t') && (LA(4)=='r')) {
						mENTRY(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='f') && (LA(2)=='u') && (LA(3)=='n') && (LA(4)=='c')) {
						mFUNCTION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='b') && (LA(2)=='l') && (LA(3)=='o') && (LA(4)=='c')) {
						mBLOCK(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='s') && (LA(2)=='u') && (LA(3)=='b') && (LA(4)=='r')) {
						mSUBROUTINE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='d') && (LA(2)=='i') && (LA(3)=='m') && (LA(4)=='e')) {
						mDIMENSION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='r') && (LA(2)=='e') && (LA(3)=='a') && (LA(4)=='l')) {
						mREAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='q') && (LA(3)=='u') && (LA(4)=='i')) {
						mEQUIVALENCE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='o') && (LA(3)=='m') && (LA(4)=='m')) {
						mCOMMON(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='o') && (LA(3)=='m') && (LA(4)=='p')) {
						mCOMPLEX(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='d') && (LA(2)=='o') && (LA(3)=='u') && (LA(4)=='b')) {
						mDOUBLE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='r') && (LA(3)=='e') && (LA(4)=='c')) {
						mPRECISION(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='i') && (LA(2)=='n') && (LA(3)=='t') && (LA(4)=='e')) {
						mINTEGER(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='l') && (LA(2)=='o') && (LA(3)=='g') && (LA(4)=='i')) {
						mLOGICAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='o') && (LA(3)=='i') && (LA(4)=='n')) {
						mPOINTER(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='i') && (LA(2)=='m') && (LA(3)=='p') && (LA(4)=='l')) {
						mIMPLICIT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='n') && (LA(2)=='o') && (LA(3)=='n') && (LA(4)=='e')) {
						mNONE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='h') && (LA(3)=='a') && (LA(4)=='r')) {
						mCHARACTER(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='a') && (LA(3)=='r') && (LA(4)=='a')) {
						mPARAMETER(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='n') && (LA(3)=='d') && (LA(4)=='d')) {
						mENDDO(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='o') && (LA(3)=='n') && (LA(4)=='t')) {
						mCONTINUE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='s') && (LA(2)=='t') && (LA(3)=='o') && (LA(4)=='p')) {
						mSTOP(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='a') && (LA(3)=='u') && (LA(4)=='s')) {
						mPAUSE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='w') && (LA(2)=='r') && (LA(3)=='i') && (LA(4)=='t')) {
						mWRITE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='r') && (LA(2)=='e') && (LA(3)=='a') && (LA(4)=='d')) {
						mREAD(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='x') && (LA(3)=='t') && (LA(4)=='e')) {
						mEXTERNAL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='i') && (LA(2)=='n') && (LA(3)=='t') && (LA(4)=='r')) {
						mINTRINSIC(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='s') && (LA(2)=='a') && (LA(3)=='v') && (LA(4)=='e')) {
						mSAVE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='d') && (LA(2)=='a') && (LA(3)=='t') && (LA(4)=='a')) {
						mDATA(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='a') && (LA(2)=='s') && (LA(3)=='s') && (LA(4)=='i')) {
						mASSIGN1(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='g') && (LA(2)=='o') && (LA(3)=='t') && (LA(4)=='o')) {
						mGOTO(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='t') && (LA(2)=='h') && (LA(3)=='e') && (LA(4)=='n')) {
						mTHEN(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='l') && (LA(3)=='s') && (LA(4)=='e')) {
						mELSEIF(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='l') && (LA(3)=='s') && (LA(4)=='e')) {
						mELSE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='n') && (LA(3)=='d') && (LA(4)=='i')) {
						mENDIF(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='p') && (LA(2)=='r') && (LA(3)=='i') && (LA(4)=='n')) {
						mPRINT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='o') && (LA(2)=='p') && (LA(3)=='e') && (LA(4)=='n')) {
						mOPEN(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='l') && (LA(3)=='o') && (LA(4)=='s')) {
						mCLOSE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='i') && (LA(2)=='n') && (LA(3)=='q') && (LA(4)=='u')) {
						mINQUIRE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='b') && (LA(2)=='a') && (LA(3)=='c') && (LA(4)=='k')) {
						mBACKSPACE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='n') && (LA(3)=='d') && (LA(4)=='f')) {
						mENDFILE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='r') && (LA(2)=='e') && (LA(3)=='w') && (LA(4)=='i')) {
						mREWIND(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='f') && (LA(2)=='o') && (LA(3)=='r') && (LA(4)=='m')) {
						mFORMAT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='c') && (LA(2)=='a') && (LA(3)=='l') && (LA(4)=='l')) {
						mCALL(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='r') && (LA(2)=='e') && (LA(3)=='t') && (LA(4)=='u')) {
						mRETURN(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='e') && (LA(3)=='q') && (LA(4)=='v')) {
						mEQV(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='n') && (LA(3)=='e') && (LA(4)=='q')) {
						mNEQV(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='n') && (LA(3)=='e') && (LA(4)=='.')) {
						mNE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='e') && (LA(3)=='q') && (LA(4)=='.')) {
						mEQ(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='e') && (LA(2)=='n') && (LA(3)=='d') && (true)) {
						mEND(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='l') && (LA(2)=='e') && (LA(3)=='t') && (true)) {
						mLET(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='n') && (LA(3)=='o')) {
						mLNOT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='e') && (LA(3)=='o')) {
						mEOR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='l') && (LA(3)=='t')) {
						mLT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='l') && (LA(3)=='e')) {
						mLE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='g') && (LA(3)=='t')) {
						mGT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='g') && (LA(3)=='e')) {
						mGE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='g') && (LA(2)=='o') && (true) && (true)) {
						mGO(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='i') && (LA(2)=='f') && (true) && (true)) {
						mIF(true);
						theRetToken=_returnToken;
					}
					else if (((LA(1)=='*') && (LA(2)=='*') && (true) && (true))&&(getColumn() != 1)) {
						mPOWER(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='a')) {
						mLAND(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='o')) {
						mLOR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='x')) {
						mXOR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='t')) {
						mTRUE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (LA(2)=='f')) {
						mFALSE(true);
						theRetToken=_returnToken;
					}
					else if (((LA(1)=='*'||LA(1)=='c') && ((LA(2) >= '\u0000' && LA(2) <= '\u007f')) && (true) && (true))&&(getColumn() == 1)) {
						mCOMMENT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='z') && (LA(2)=='\'')) {
						mZCON(true);
						theRetToken=_returnToken;
					}
					else if (((LA(1)=='*') && (true))&&(getColumn() != 1)) {
						mSTAR(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='.') && (true) && (true)) {
						mRCON(true);
						theRetToken=_returnToken;
					}
					else if (((LA(1) >= 'a' && LA(1) <= 'z')) && (true) && (true) && (true)) {
						mNAME(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mPROGRAM(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PROGRAM;
		int _saveIndex;
		
		match("program");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mENTRY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ENTRY;
		int _saveIndex;
		
		match("entry");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mFUNCTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FUNCTION;
		int _saveIndex;
		
		match("function");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBLOCK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BLOCK;
		int _saveIndex;
		
		match("block");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSUBROUTINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SUBROUTINE;
		int _saveIndex;
		
		match("subroutine");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = END;
		int _saveIndex;
		
		match("end");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDIMENSION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIMENSION;
		int _saveIndex;
		
		match("dimension");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mREAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = REAL;
		int _saveIndex;
		
		match("real");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQUIVALENCE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQUIVALENCE;
		int _saveIndex;
		
		match("equivalence");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMON;
		int _saveIndex;
		
		match("common");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMPLEX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMPLEX;
		int _saveIndex;
		
		match("complex");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOUBLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOUBLE;
		int _saveIndex;
		
		match("double");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPRECISION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PRECISION;
		int _saveIndex;
		
		match("precision");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mINTEGER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INTEGER;
		int _saveIndex;
		
		match("integer");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLOGICAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LOGICAL;
		int _saveIndex;
		
		match("logical");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOINTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POINTER;
		int _saveIndex;
		
		match("pointer");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIMPLICIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IMPLICIT;
		int _saveIndex;
		
		match("implicit");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNONE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NONE;
		int _saveIndex;
		
		match("none");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCHARACTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CHARACTER;
		int _saveIndex;
		
		match("character");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPARAMETER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PARAMETER;
		int _saveIndex;
		
		match("parameter");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mENDDO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ENDDO;
		int _saveIndex;
		
		match("enddo");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCONTINUE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CONTINUE;
		int _saveIndex;
		
		match("continue");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTOP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STOP;
		int _saveIndex;
		
		match("stop");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPAUSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PAUSE;
		int _saveIndex;
		
		match("pause");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWRITE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WRITE;
		int _saveIndex;
		
		match("write");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mREAD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = READ;
		int _saveIndex;
		
		match("read");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEXTERNAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EXTERNAL;
		int _saveIndex;
		
		match("external");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mINTRINSIC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INTRINSIC;
		int _saveIndex;
		
		match("intrinsic");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSAVE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SAVE;
		int _saveIndex;
		
		match("save");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDATA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DATA;
		int _saveIndex;
		
		match("data");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mASSIGN1(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ASSIGN1;
		int _saveIndex;
		
		match("assign");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGOTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GOTO;
		int _saveIndex;
		
		match("goto");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GO;
		int _saveIndex;
		
		match("go");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IF;
		int _saveIndex;
		
		match("if");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTHEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = THEN;
		int _saveIndex;
		
		match("then");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mELSEIF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ELSEIF;
		int _saveIndex;
		
		match("elseif");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mELSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ELSE;
		int _saveIndex;
		
		match("else");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mENDIF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ENDIF;
		int _saveIndex;
		
		match("endif");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPRINT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PRINT;
		int _saveIndex;
		
		match("print");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOPEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OPEN;
		int _saveIndex;
		
		match("open");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCLOSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CLOSE;
		int _saveIndex;
		
		match("close");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mINQUIRE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INQUIRE;
		int _saveIndex;
		
		match("inquire");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBACKSPACE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BACKSPACE;
		int _saveIndex;
		
		match("backspace");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mENDFILE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ENDFILE;
		int _saveIndex;
		
		match("endfile");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mREWIND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = REWIND;
		int _saveIndex;
		
		match("rewind");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mFORMAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FORMAT;
		int _saveIndex;
		
		match("format");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LET;
		int _saveIndex;
		
		match("let");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALL;
		int _saveIndex;
		
		match("call");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRETURN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RETURN;
		int _saveIndex;
		
		match("return");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOLLAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOLLAR;
		int _saveIndex;
		
		match('$');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMA;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LPAREN;
		int _saveIndex;
		
		match('(');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RPAREN;
		int _saveIndex;
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COLON;
		int _saveIndex;
		
		match(':');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ASSIGN;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUS;
		int _saveIndex;
		
		match('-');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUS;
		int _saveIndex;
		
		match('+');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIV;
		int _saveIndex;
		
		match('/');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR;
		int _saveIndex;
		
		if (!(getColumn() != 1))
		  throw new SemanticException("getColumn() != 1");
		match('*');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPOWER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = POWER;
		int _saveIndex;
		
		if (!(getColumn() != 1))
		  throw new SemanticException("getColumn() != 1");
		match("**");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LNOT;
		int _saveIndex;
		
		match(".not.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LAND;
		int _saveIndex;
		
		match(".and.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LOR;
		int _saveIndex;
		
		match(".or.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQV;
		int _saveIndex;
		
		match(".eqv.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNEQV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NEQV;
		int _saveIndex;
		
		match(".neqv.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mXOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = XOR;
		int _saveIndex;
		
		match(".xor.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EOR;
		int _saveIndex;
		
		match(".eor.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LT;
		int _saveIndex;
		
		match(".lt.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LE;
		int _saveIndex;
		
		match(".le.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GT;
		int _saveIndex;
		
		match(".gt.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GE;
		int _saveIndex;
		
		match(".ge.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NE;
		int _saveIndex;
		
		match(".ne.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQ;
		int _saveIndex;
		
		match(".eq.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTRUE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TRUE;
		int _saveIndex;
		
		match(".true.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mFALSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FALSE;
		int _saveIndex;
		
		match(".false.");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCONTINUATION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CONTINUATION;
		int _saveIndex;
		
		{
		match(_tokenSet_0);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEOS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EOS;
		int _saveIndex;
		
		{
		int _cnt486=0;
		_loop486:
		do {
			if ((LA(1)=='\n'||LA(1)=='\r')) {
				{
				if ((LA(1)=='\n')) {
					match('\n');
				}
				else if ((LA(1)=='\r')) {
					match('\r');
					{
					if ((LA(1)=='\n') && (true) && (true) && (true)) {
						match('\n');
					}
					else {
					}
					
					}
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else {
				if ( _cnt486>=1 ) { break _loop486; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt486++;
		} while (true);
		}
		{
		boolean synPredMatched489 = false;
		if (((LA(1)==' '))) {
			int _m489 = mark();
			synPredMatched489 = true;
			inputState.guessing++;
			try {
				{
				match("     ");
				mCONTINUATION(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched489 = false;
			}
			rewind(_m489);
inputState.guessing--;
		}
		if ( synPredMatched489 ) {
			match("     ");
			mCONTINUATION(false);
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else {
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;
		
		mWHITE(false);
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mWHITE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WHITE;
		int _saveIndex;
		
		{
		if ((LA(1)==' ')) {
			match(' ');
		}
		else if ((LA(1)=='\t')) {
			match('\t');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMENT;
		int _saveIndex;
		
		if (!(getColumn() == 1))
		  throw new SemanticException("getColumn() == 1");
		{
		if ((LA(1)=='c')) {
			match('c');
		}
		else if ((LA(1)=='*')) {
			match('*');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		if (((_tokenSet_1.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\u007f')) && (true) && (true))&&(LA(1) != '%' || LA(2) != '&')) {
			{
			int _cnt498=0;
			_loop498:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					mNOTNL(false);
				}
				else {
					if ( _cnt498>=1 ) { break _loop498; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt498++;
			} while (true);
			}
		}
		else if ((LA(1)=='\n'||LA(1)=='\r'||LA(1)=='%') && (true) && (true) && (true)) {
			{
			if ((LA(1)=='%')) {
				match('%');
				match('&');
				{
				_loop496:
				do {
					if ((_tokenSet_1.member(LA(1)))) {
						mNOTNL(false);
					}
					else {
						break _loop496;
					}
					
				} while (true);
				}
			}
			else if ((LA(1)=='\n'||LA(1)=='\r')) {
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		int _cnt502=0;
		_loop502:
		do {
			if ((LA(1)=='\n'||LA(1)=='\r')) {
				{
				if ((LA(1)=='\n')) {
					match('\n');
				}
				else if ((LA(1)=='\r')) {
					match('\r');
					{
					if ((LA(1)=='\n') && (true) && (true) && (true)) {
						match('\n');
					}
					else {
					}
					
					}
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else {
				if ( _cnt502>=1 ) { break _loop502; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt502++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNOTNL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NOTNL;
		int _saveIndex;
		
		{
		match(_tokenSet_1);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSCON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SCON;
		int _saveIndex;
		
		match('\'');
		{
		_loop512:
		do {
			if ((LA(1)=='\'') && (LA(2)=='\'')) {
				match('\'');
				match('\'');
			}
			else if ((_tokenSet_2.member(LA(1)))) {
				{
				match(_tokenSet_2);
				}
			}
			else {
				boolean synPredMatched509 = false;
				if (((LA(1)=='\n'||LA(1)=='\r'))) {
					int _m509 = mark();
					synPredMatched509 = true;
					inputState.guessing++;
					try {
						{
						{
						if ((LA(1)=='\n')) {
							match('\n');
						}
						else if ((LA(1)=='\r')) {
							match('\r');
							{
							if ((LA(1)=='\n')) {
								match('\n');
							}
							else if ((LA(1)==' ')) {
							}
							else {
								throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
							}
							
							}
						}
						else {
							throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
						}
						
						}
						match("     ");
						mCONTINUATION(false);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched509 = false;
					}
					rewind(_m509);
inputState.guessing--;
				}
				if ( synPredMatched509 ) {
					{
					if ((LA(1)=='\n')) {
						match('\n');
					}
					else if ((LA(1)=='\r')) {
						match('\r');
						{
						if ((LA(1)=='\n')) {
							match('\n');
						}
						else if ((LA(1)==' ')) {
						}
						else {
							throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
						}
						
						}
					}
					else {
						throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
					}
					
					}
					match("     ");
					mCONTINUATION(false);
				}
				else {
					break _loop512;
				}
				}
			} while (true);
			}
			match('\'');
			if ( inputState.guessing==0 ) {
				
						String str = new String(text.getBuffer(),_begin,text.length()-_begin);
						str = str.substring(1, str.length()-1);
						str = str.replaceAll("''", "'");
						str = str.replaceAll("(\n|\r|\r\n)     [^0 ]", "");
						text.setLength(_begin); text.append(str);
					
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	public final void mICON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ICON;
		int _saveIndex;
		int counter=0;
		
		counter=mINTVAL(false);
		{
		if ((LA(1)=='h')) {
			match('h');
			{
			_loop516:
			do {
				if (((_tokenSet_1.member(LA(1))))&&(counter>0)) {
					mNOTNL(false);
					if ( inputState.guessing==0 ) {
						counter--;
					}
				}
				else {
					break _loop516;
				}
				
			} while (true);
			}
			if (!(counter==0))
			  throw new SemanticException("counter==0");
			if ( inputState.guessing==0 ) {
				
							_ttype = HOLLERITH;
							String str = new String(text.getBuffer(),_begin,text.length()-_begin);
							str = str.replaceFirst("([0-9])+h", "");
							text.setLength(_begin); text.append(str);
						
			}
		}
		else {
			boolean synPredMatched520 = false;
			if (((LA(1)=='.'))) {
				int _m520 = mark();
				synPredMatched520 = true;
				inputState.guessing++;
				try {
					{
					match('.');
					{
					if ((LA(1)=='d'||LA(1)=='e') && (_tokenSet_3.member(LA(2)))) {
						mEXPON(false);
					}
					else if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mNUM(false);
					}
					else if ((_tokenSet_4.member(LA(1))) && (true)) {
						{
						match(_tokenSet_4);
						}
					}
					else {
						throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
					}
					
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched520 = false;
				}
				rewind(_m520);
inputState.guessing--;
			}
			if ( synPredMatched520 ) {
				match('.');
				{
				_loop522:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mNUM(false);
					}
					else {
						break _loop522;
					}
					
				} while (true);
				}
				{
				if ((LA(1)=='d'||LA(1)=='e')) {
					mEXPON(false);
				}
				else {
				}
				
				}
				if ( inputState.guessing==0 ) {
					_ttype = RCON;
				}
			}
			else if ((LA(1)=='x')) {
				match('x');
				if ( inputState.guessing==0 ) {
					_ttype = XCON;
				}
			}
			else if ((LA(1)=='p')) {
				match('p');
				if ( inputState.guessing==0 ) {
					_ttype = PCON;
				}
			}
			else if (( true )&&(getColumn()<=6)) {
				if ( inputState.guessing==0 ) {
					_ttype = LABEL;
				}
			}
			else {
			}
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	protected final int  mINTVAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int val=0;
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INTVAL;
		int _saveIndex;
		
		{
		int _cnt555=0;
		_loop555:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				mNUM(false);
			}
			else {
				if ( _cnt555>=1 ) { break _loop555; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt555++;
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			val=Integer.parseInt(new String(text.getBuffer(),_begin,text.length()-_begin));
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
		return val;
	}
	
	protected final void mNUM(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUM;
		int _saveIndex;
		
		{
		matchRange('0','9');
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mEXPON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EXPON;
		int _saveIndex;
		
		{
		if ((LA(1)=='e')) {
			match('e');
		}
		else if ((LA(1)=='d')) {
			match('d');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		if ((LA(1)=='+'||LA(1)=='-')) {
			mSIGN(false);
		}
		else if (((LA(1) >= '0' && LA(1) <= '9'))) {
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		{
		int _cnt574=0;
		_loop574:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				mNUM(false);
			}
			else {
				if ( _cnt574>=1 ) { break _loop574; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt574++;
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
					String str = new String(text.getBuffer(),_begin,text.length()-_begin);
					str = str.replaceAll("^[dD]", "0e");
					str = str.replaceAll("[dD]", "e");
					text.setLength(_begin); text.append(str);
				
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRCON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RCON;
		int _saveIndex;
		
		match('.');
		{
		_loop526:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				mNUM(false);
			}
			else {
				break _loop526;
			}
			
		} while (true);
		}
		{
		if ((LA(1)=='d'||LA(1)=='e')) {
			mEXPON(false);
		}
		else {
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mZCON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ZCON;
		int _saveIndex;
		
		match('z');
		match('\'');
		{
		int _cnt530=0;
		_loop530:
		do {
			if ((_tokenSet_5.member(LA(1)))) {
				mHEX(false);
			}
			else {
				if ( _cnt530>=1 ) { break _loop530; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt530++;
		} while (true);
		}
		match('\'');
		if ( inputState.guessing==0 ) {
			
					String str = new String(text.getBuffer(),_begin,text.length()-_begin);
					str = str.substring(2,str.length() - 1);
					text.setLength(_begin); text.append(str);
				
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mHEX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HEX;
		int _saveIndex;
		
		{
		if (((LA(1) >= '0' && LA(1) <= '9'))) {
			mNUM(false);
		}
		else if (((LA(1) >= 'a' && LA(1) <= 'f'))) {
			matchRange('a','f');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NAME;
		int _saveIndex;
		
		boolean synPredMatched536 = false;
		if (((LA(1)=='d'||LA(1)=='e'||LA(1)=='f'||LA(1)=='g'||LA(1)=='i') && ((LA(2) >= '0' && LA(2) <= '9')) && (_tokenSet_6.member(LA(3))) && (_tokenSet_6.member(LA(4))))) {
			int _m536 = mark();
			synPredMatched536 = true;
			inputState.guessing++;
			try {
				{
				{
				if ((LA(1)=='i')) {
					match('i');
				}
				else if ((LA(1)=='f')) {
					match('f');
				}
				else if ((LA(1)=='d')) {
					match('d');
				}
				else if ((LA(1)=='g')) {
					match('g');
				}
				else if ((LA(1)=='e')) {
					match('e');
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
				{
				int _cnt535=0;
				_loop535:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mNUM(false);
					}
					else {
						if ( _cnt535>=1 ) { break _loop535; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt535++;
				} while (true);
				}
				match('.');
				}
			}
			catch (RecognitionException pe) {
				synPredMatched536 = false;
			}
			rewind(_m536);
inputState.guessing--;
		}
		if ( synPredMatched536 ) {
			mFDESC(false);
			if ( inputState.guessing==0 ) {
				_ttype = FCON;
			}
		}
		else if (((LA(1) >= 'a' && LA(1) <= 'z')) && (true) && (true) && (true)) {
			mALPHA(false);
			{
			_loop538:
			do {
				if ((_tokenSet_7.member(LA(1)))) {
					mALNUM(false);
				}
				else {
					break _loop538;
				}
				
			} while (true);
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mFDESC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FDESC;
		int _saveIndex;
		
		if ((LA(1)=='d'||LA(1)=='f'||LA(1)=='i')) {
			{
			if ((LA(1)=='i')) {
				match('i');
			}
			else if ((LA(1)=='f')) {
				match('f');
			}
			else if ((LA(1)=='d')) {
				match('d');
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			{
			int _cnt559=0;
			_loop559:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mNUM(false);
				}
				else {
					if ( _cnt559>=1 ) { break _loop559; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt559++;
			} while (true);
			}
			match('.');
			{
			int _cnt561=0;
			_loop561:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mNUM(false);
				}
				else {
					if ( _cnt561>=1 ) { break _loop561; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt561++;
			} while (true);
			}
		}
		else if ((LA(1)=='e'||LA(1)=='g')) {
			{
			if ((LA(1)=='e')) {
				match('e');
			}
			else if ((LA(1)=='g')) {
				match('g');
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			{
			int _cnt564=0;
			_loop564:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mNUM(false);
				}
				else {
					if ( _cnt564>=1 ) { break _loop564; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt564++;
			} while (true);
			}
			match('.');
			{
			int _cnt566=0;
			_loop566:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					mNUM(false);
				}
				else {
					if ( _cnt566>=1 ) { break _loop566; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt566++;
			} while (true);
			}
			{
			if ((LA(1)=='e')) {
				match('e');
				{
				int _cnt569=0;
				_loop569:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						mNUM(false);
					}
					else {
						if ( _cnt569>=1 ) { break _loop569; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt569++;
				} while (true);
				}
			}
			else {
			}
			
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mALPHA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ALPHA;
		int _saveIndex;
		
		{
		matchRange('a','z');
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mALNUM(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ALNUM;
		int _saveIndex;
		
		{
		if (((LA(1) >= 'a' && LA(1) <= 'z'))) {
			mALPHA(false);
		}
		else if (((LA(1) >= '0' && LA(1) <= '9'))) {
			mNUM(false);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SIGN;
		int _saveIndex;
		
		{
		if ((LA(1)=='+')) {
			match('+');
		}
		else if ((LA(1)=='-')) {
			match('-');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { -281479271677953L, -1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { -9217L, -1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { -549755823105L, -1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 287992881640112128L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { -287948901175001089L, -76790862746484737L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 287948901175001088L, 541165879296L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 288019269919178752L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 287948901175001088L, 576460743713488896L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	
	}

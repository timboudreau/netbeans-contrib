/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

//package org.netbeans.modules.corba.idl.parser;
package org.netbeans.modules.corba.idl.src;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.netbeans.modules.corba.utils.Assertion;

public class IDLParserTokenManager implements IDLParserConstants
{
private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x8000000000L) != 0L)
            return 40;
         if ((active0 & 0xfffffc0033f8c800L) != 0L || (active1 & 0x1ffccL) != 0L)
         {
            jjmatchedKind = 81;
            return 9;
         }
         if ((active0 & 0x200L) != 0L)
            return 7;
         return -1;
      case 1:
         if ((active0 & 0x10008000L) != 0L || (active1 & 0x1000L) != 0L)
            return 9;
         if ((active0 & 0xfffffc0023f84800L) != 0L || (active1 & 0x1efccL) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 81;
               jjmatchedPos = 1;
            }
            return 9;
         }
         return -1;
      case 2:
         if ((active0 & 0xff7ffc0023f8c800L) != 0L || (active1 & 0x1f7ccL) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 2;
            return 9;
         }
         if ((active0 & 0x80000000000000L) != 0L || (active1 & 0x800L) != 0L)
            return 9;
         return -1;
      case 3:
         if ((active0 & 0xaf76f80023f8c800L) != 0L || (active1 & 0x1f3ccL) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 3;
            return 9;
         }
         if ((active0 & 0x5009040000000000L) != 0L || (active1 & 0x400L) != 0L)
            return 9;
         return -1;
      case 4:
         if ((active0 & 0xab24b00003f8c800L) != 0L || (active1 & 0x163ccL) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 4;
            return 9;
         }
         if ((active0 & 0x452480020000000L) != 0L || (active1 & 0x9000L) != 0L)
            return 9;
         return -1;
      case 5:
         if ((active0 & 0xa02410000368c000L) != 0L || (active1 & 0x141c8L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 5;
            return 9;
         }
         if ((active0 & 0xb00a00000900800L) != 0L || (active1 & 0x2204L) != 0L)
            return 9;
         return -1;
      case 6:
         if ((active0 & 0x800400000068c000L) != 0L || (active1 & 0x101c0L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 6;
            return 9;
         }
         if ((active0 & 0x2020100003000000L) != 0L || (active1 & 0x4008L) != 0L)
            return 9;
         return -1;
      case 7:
         if ((active0 & 0x288000L) != 0L || (active1 & 0x10180L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 7;
            return 9;
         }
         if ((active0 & 0x8004000000404000L) != 0L || (active1 & 0x40L) != 0L)
            return 9;
         return -1;
      case 8:
         if ((active0 & 0x200000L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 8;
            return 9;
         }
         if ((active0 & 0x88000L) != 0L || (active1 & 0x10180L) != 0L)
            return 9;
         return -1;
      case 9:
         if ((active0 & 0x200000L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 9;
            return 9;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0, long active1)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 35:
         return jjStartNfaWithStates_0(0, 9, 7);
      case 37:
         return jjStopAtPos(0, 40);
      case 38:
         return jjStopAtPos(0, 33);
      case 40:
         return jjStopAtPos(0, 26);
      case 41:
         return jjStopAtPos(0, 27);
      case 42:
         return jjStopAtPos(0, 38);
      case 43:
         return jjStopAtPos(0, 36);
      case 44:
         return jjStopAtPos(0, 17);
      case 45:
         return jjStopAtPos(0, 37);
      case 47:
         return jjStartNfaWithStates_0(0, 39, 40);
      case 58:
         jjmatchedKind = 16;
         return jjMoveStringLiteralDfa1_0(0x40000L, 0x0L);
      case 59:
         return jjStopAtPos(0, 10);
      case 60:
         jjmatchedKind = 64;
         return jjMoveStringLiteralDfa1_0(0x800000000L, 0x0L);
      case 61:
         return jjStopAtPos(0, 30);
      case 62:
         jjmatchedKind = 65;
         return jjMoveStringLiteralDfa1_0(0x400000000L, 0x0L);
      case 70:
         return jjMoveStringLiteralDfa1_0(0x80000000000L, 0x0L);
      case 79:
         return jjMoveStringLiteralDfa1_0(0x100000000000000L, 0x0L);
      case 84:
         return jjMoveStringLiteralDfa1_0(0x40000000000L, 0x0L);
      case 86:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x10000L);
      case 91:
         return jjStopAtPos(0, 68);
      case 93:
         return jjStopAtPos(0, 69);
      case 94:
         return jjStopAtPos(0, 32);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x80000000004000L, 0x80L);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x20000000000000L, 0x0L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x1008000020100000L, 0x4000L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x2000800000000000L, 0x0L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x4000000000000000L, 0x100L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x400002000000L, 0x8000L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x10008000L, 0x1000L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x1000000000000L, 0x0L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x800L, 0x0L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x200000000000L, 0x0L);
      case 111:
         return jjMoveStringLiteralDfa1_0(0x40000000000000L, 0xa00L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0x1800000L, 0x0L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x2040L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x8a02000000400000L, 0x4L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x100000200000L, 0x0L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x404000000000000L, 0x0L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x80000L, 0x400L);
      case 119:
         return jjMoveStringLiteralDfa1_0(0x10000000000000L, 0x8L);
      case 123:
         return jjStopAtPos(0, 12);
      case 124:
         return jjStopAtPos(0, 31);
      case 125:
         return jjStopAtPos(0, 13);
      case 126:
         return jjStopAtPos(0, 41);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0, long active1)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0, active1);
      return 1;
   }
   switch(curChar)
   {
      case 58:
         if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(1, 18);
         break;
      case 60:
         if ((active0 & 0x800000000L) != 0L)
            return jjStopAtPos(1, 35);
         break;
      case 62:
         if ((active0 & 0x400000000L) != 0L)
            return jjStopAtPos(1, 34);
         break;
      case 65:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000000000L, active1, 0L);
      case 82:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000200002080000L, active1, 0x12000L);
      case 98:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000000004000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa2_0(active0, 0x50000000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0xa000000000000000L, active1, 0x40L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0xa000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x8000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x400000000000L, active1, 0L);
      case 110:
         if ((active0 & 0x10000000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_0(active0, 0x4484000000008000L, active1, 0x1200L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x21800020000800L, active1, 0x4400L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x1200000L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x8L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000000000L, active1, 0x84L);
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0xd00000L, active1, 0x800L);
      case 119:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000000000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x100L);
      case 121:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0, active1);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(0, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0, active1);
      return 2;
   }
   switch(curChar)
   {
      case 76:
         return jjMoveStringLiteralDfa3_0(active0, 0x80000000000L, active1, 0L);
      case 85:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000000000000L, active1, 0x40L);
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000000L, active1, 0x100L);
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x800L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x200L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000000000000000L, active1, 0L);
      case 104:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0xc00000001000000L, active1, 0x2400L);
      case 106:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x80000L, active1, 0x10000L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000020000000L, active1, 0x4000L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x22400000000000L, active1, 0x1000L);
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000400000L, active1, 0L);
      case 113:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000000000000000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000000000000L, active1, 0x4L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x1004000000104000L, active1, 0L);
      case 116:
         if ((active1 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(2, 75, 9);
         return jjMoveStringLiteralDfa3_0(active0, 0x40200000008000L, active1, 0x88L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000800000200000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x8000L);
      case 121:
         if ((active0 & 0x80000000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 55, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(1, active0, active1);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(1, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0, active1);
      return 3;
   }
   switch(curChar)
   {
      case 69:
         if ((active0 & 0x40000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 42, 9);
         break;
      case 83:
         return jjMoveStringLiteralDfa4_0(active0, 0x80000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x2010400000000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000000000L, active1, 0L);
      case 100:
         if ((active1 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(3, 74, 9);
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x40L);
      case 101:
         if ((active0 & 0x1000000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 60, 9);
         return jjMoveStringLiteralDfa4_0(active0, 0x140100000008000L, active1, 0x8100L);
      case 103:
         if ((active0 & 0x1000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 48, 9);
         break;
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x4200000000000L, active1, 0x4L);
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000000800000L, active1, 0L);
      case 109:
         if ((active0 & 0x4000000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 62, 9);
         break;
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000L, active1, 0L);
      case 114:
         if ((active0 & 0x8000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 51, 9);
         return jjMoveStringLiteralDfa4_0(active0, 0x2000000000000L, active1, 0x88L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000000L, active1, 0x2000L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000002104000L, active1, 0x4000L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x8200000000080800L, active1, 0x11000L);
      case 118:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000L, active1, 0L);
      case 119:
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0, active1);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(2, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0, active1);
      return 4;
   }
   switch(curChar)
   {
      case 69:
         if ((active0 & 0x80000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 43, 9);
         break;
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000L, active1, 0x200L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0xb00000000200000L, active1, 0L);
      case 100:
         if ((active1 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(4, 79, 9);
         return jjMoveStringLiteralDfa5_0(active0, 0x100000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x8020000000080000L, active1, 0x16000L);
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x800000L, active1, 0x88L);
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x800000000800L, active1, 0L);
      case 110:
         if ((active0 & 0x400000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 58, 9);
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x4L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x2500000L, active1, 0x40L);
      case 112:
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x100L);
      case 114:
         if ((active0 & 0x10000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 52, 9);
         return jjMoveStringLiteralDfa5_0(active0, 0xc000L, active1, 0L);
      case 116:
         if ((active0 & 0x20000000L) != 0L)
            return jjStartNfaWithStates_0(4, 29, 9);
         else if ((active0 & 0x400000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 46, 9);
         else if ((active0 & 0x2000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 49, 9);
         else if ((active0 & 0x40000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 54, 9);
         else if ((active1 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(4, 76, 9);
         break;
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000000000000000L, active1, 0L);
      case 118:
         return jjMoveStringLiteralDfa5_0(active0, 0x200000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0, active1);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(3, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0, active1);
      return 5;
   }
   switch(curChar)
   {
      case 66:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x10000L);
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000000204000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x80L);
      case 99:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(5, 23, 9);
         break;
      case 101:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(5, 11, 9);
         else if ((active0 & 0x200000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 45, 9);
         else if ((active0 & 0x800000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 47, 9);
         return jjMoveStringLiteralDfa6_0(active0, 0x100000000000L, active1, 0L);
      case 102:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000L, active1, 0L);
      case 103:
         if ((active1 & 0x4L) != 0L)
            return jjStartNfaWithStates_0(5, 66, 9);
         break;
      case 104:
         if ((active0 & 0x800000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 59, 9);
         break;
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000000000000000L, active1, 0L);
      case 109:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(5, 20, 9);
         break;
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x8004000000000000L, active1, 0x48L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x2400000L, active1, 0L);
      case 115:
         if ((active1 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(5, 77, 9);
         break;
      case 116:
         if ((active0 & 0x100000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 56, 9);
         else if ((active0 & 0x200000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 57, 9);
         return jjMoveStringLiteralDfa6_0(active0, 0x1080000L, active1, 0x100L);
      case 120:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x4000L);
      case 121:
         if ((active1 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(5, 73, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(4, active0, active1);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(4, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0, active1);
      return 6;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000L, active1, 0x10000L);
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000000000004000L, active1, 0L);
      case 101:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(6, 24, 9);
         return jjMoveStringLiteralDfa7_0(active0, 0x4000000000000L, active1, 0L);
      case 102:
         if ((active0 & 0x100000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 44, 9);
         break;
      case 103:
         if ((active1 & 0x8L) != 0L)
            return jjStartNfaWithStates_0(6, 67, 9);
         break;
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x100L);
      case 108:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x40L);
      case 110:
         if ((active0 & 0x20000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 53, 9);
         break;
      case 116:
         if ((active0 & 0x2000000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 61, 9);
         else if ((active1 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(6, 78, 9);
         return jjMoveStringLiteralDfa7_0(active0, 0x600000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x80L);
      case 121:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(6, 25, 9);
         return jjMoveStringLiteralDfa7_0(active0, 0x80000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0, active1);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(5, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0, active1);
      return 7;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa8_0(active0, 0x200000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x8000L, active1, 0L);
      case 100:
         if ((active0 & 0x4000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 50, 9);
         break;
      case 101:
         if ((active0 & 0x8000000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 63, 9);
         break;
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x100L);
      case 112:
         return jjMoveStringLiteralDfa8_0(active0, 0x80000L, active1, 0L);
      case 115:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(7, 22, 9);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x10000L);
      case 116:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(7, 14, 9);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x80L);
      case 121:
         if ((active1 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(7, 70, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(6, active0, active1);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(6, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0, active1);
      return 8;
   }
   switch(curChar)
   {
      case 98:
         return jjMoveStringLiteralDfa9_0(active0, 0x200000L, active1, 0L);
      case 101:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(8, 15, 9);
         else if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(8, 19, 9);
         else if ((active1 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(8, 71, 9);
         else if ((active1 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(8, 80, 9);
         break;
      case 110:
         if ((active1 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(8, 72, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(7, active0, active1);
}
private final int jjMoveStringLiteralDfa9_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(7, old0, old1); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0, 0L);
      return 9;
   }
   switch(curChar)
   {
      case 108:
         return jjMoveStringLiteralDfa10_0(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0, 0L);
}
private final int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0, 0L);
      return 10;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(10, 21, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(9, active0, 0L);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 130;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 13);
                  else if (curChar == 34)
                     jjCheckNAddStates(14, 18);
                  else if (curChar == 46)
                     jjCheckNAddTwoStates(52, 57);
                  else if (curChar == 47)
                     jjAddStates(19, 21);
                  else if (curChar == 39)
                     jjAddStates(22, 23);
                  else if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 7;
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 83)
                        kind = 83;
                     jjCheckNAddTwoStates(21, 22);
                  }
                  else if (curChar == 48)
                     jjAddStates(24, 25);
                  if (curChar == 48)
                  {
                     if (kind > 82)
                        kind = 82;
                     jjCheckNAddTwoStates(18, 19);
                  }
                  break;
               case 40:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(46, 47);
                  else if (curChar == 47)
                  {
                     if (kind > 6)
                        kind = 6;
                     jjCheckNAdd(44);
                  }
                  if (curChar == 47)
                     jjCheckNAddTwoStates(41, 42);
                  break;
               case 2:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 9:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 15:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 17:
                  if (curChar != 48)
                     break;
                  if (kind > 82)
                     kind = 82;
                  jjCheckNAddTwoStates(18, 19);
                  break;
               case 18:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 82)
                     kind = 82;
                  jjCheckNAddTwoStates(18, 19);
                  break;
               case 20:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 83)
                     kind = 83;
                  jjCheckNAddTwoStates(21, 22);
                  break;
               case 21:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 83)
                     kind = 83;
                  jjCheckNAddTwoStates(21, 22);
                  break;
               case 23:
                  if (curChar == 39)
                     jjAddStates(22, 23);
                  break;
               case 24:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(25);
                  break;
               case 25:
                  if (curChar == 39 && kind > 87)
                     kind = 87;
                  break;
               case 27:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAdd(25);
                  break;
               case 28:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(29, 30);
                  break;
               case 29:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(25);
                  break;
               case 30:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(29);
                  break;
               case 32:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(25);
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 34:
                  if (curChar == 48)
                     jjAddStates(24, 25);
                  break;
               case 36:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjAddStates(26, 27);
                  break;
               case 39:
                  if (curChar == 47)
                     jjAddStates(19, 21);
                  break;
               case 41:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     jjCheckNAddTwoStates(41, 42);
                  break;
               case 42:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 43:
                  if (curChar != 47)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(44);
                  break;
               case 44:
                  if ((0xfffffffffffffbffL & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(44);
                  break;
               case 45:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 46:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 47:
                  if (curChar == 42)
                     jjAddStates(28, 29);
                  break;
               case 48:
                  if ((0xffff7fffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(49, 47);
                  break;
               case 49:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(49, 47);
                  break;
               case 50:
                  if (curChar == 47 && kind > 7)
                     kind = 7;
                  break;
               case 51:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(52, 57);
                  break;
               case 52:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddStates(30, 32);
                  break;
               case 54:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(55);
                  break;
               case 55:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddTwoStates(55, 56);
                  break;
               case 57:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 59:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 13);
                  break;
               case 60:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 61:
                  if (curChar != 46)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddStates(33, 35);
                  break;
               case 62:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddStates(33, 35);
                  break;
               case 63:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(63, 64);
                  break;
               case 64:
                  if (curChar == 46)
                     jjCheckNAdd(52);
                  break;
               case 65:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(65, 66);
                  break;
               case 67:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(68);
                  break;
               case 68:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 86)
                     kind = 86;
                  jjCheckNAddTwoStates(68, 69);
                  break;
               case 70:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(70, 71);
                  break;
               case 71:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(72, 58);
                  break;
               case 72:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(72, 58);
                  break;
               case 73:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(73, 74);
                  break;
               case 74:
                  if (curChar == 46)
                     jjCheckNAdd(57);
                  break;
               case 75:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(75, 58);
                  break;
               case 76:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(76, 77);
                  break;
               case 77:
                  if (curChar == 46)
                     jjCheckNAdd(78);
                  break;
               case 78:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 92)
                     kind = 92;
                  jjCheckNAdd(78);
                  break;
               case 80:
                  if (curChar == 39)
                     jjAddStates(36, 37);
                  break;
               case 81:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(82);
                  break;
               case 82:
                  if (curChar == 39 && kind > 88)
                     kind = 88;
                  break;
               case 84:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAdd(82);
                  break;
               case 85:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(86, 87);
                  break;
               case 86:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(82);
                  break;
               case 87:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(86);
                  break;
               case 89:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(82);
                  break;
               case 90:
               case 92:
               case 94:
               case 97:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(89);
                  break;
               case 93:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 94;
                  break;
               case 95:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 96;
                  break;
               case 96:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 97;
                  break;
               case 98:
                  if (curChar == 34)
                     jjCheckNAddStates(38, 40);
                  break;
               case 99:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 101:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 102:
                  if (curChar != 34)
                     break;
                  if (kind > 90)
                     kind = 90;
                  jjstateSet[jjnewStateCnt++] = 98;
                  break;
               case 103:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(104, 105);
                  break;
               case 104:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 105:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(104);
                  break;
               case 107:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 108:
               case 110:
               case 112:
               case 115:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(107);
                  break;
               case 111:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 112;
                  break;
               case 113:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 114;
                  break;
               case 114:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 115;
                  break;
               case 116:
                  if (curChar == 34)
                     jjCheckNAddStates(14, 18);
                  break;
               case 117:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 119:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 120:
                  if (curChar != 34)
                     break;
                  if (kind > 89)
                     kind = 89;
                  jjstateSet[jjnewStateCnt++] = 121;
                  break;
               case 121:
                  if (curChar == 34)
                     jjCheckNAddStates(41, 43);
                  break;
               case 122:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(123, 124);
                  break;
               case 123:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 124:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(123);
                  break;
               case 126:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 127:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 126;
                  break;
               case 128:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(128, 129);
                  break;
               case 129:
                  if (curChar == 34 && kind > 93)
                     kind = 93;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 81)
                        kind = 81;
                     jjCheckNAdd(9);
                  }
                  else if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 13;
                  if (curChar == 76)
                     jjAddStates(44, 45);
                  else if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 1:
                  if (curChar != 97)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(2);
                  break;
               case 2:
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(2);
                  break;
               case 3:
                  if (curChar == 109)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 4:
                  if (curChar == 103)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 5:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 6:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 7:
                  if (curChar == 112)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 8:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(9);
                  break;
               case 9:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(9);
                  break;
               case 10:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 11:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(12);
                  break;
               case 12:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(12);
                  break;
               case 13:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 14:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(15);
                  break;
               case 15:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(15);
                  break;
               case 16:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 19:
                  if ((0x20100000201000L & l) != 0L && kind > 82)
                     kind = 82;
                  break;
               case 22:
                  if ((0x20100000201000L & l) != 0L && kind > 83)
                     kind = 83;
                  break;
               case 24:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(25);
                  break;
               case 26:
                  if (curChar == 92)
                     jjAddStates(46, 48);
                  break;
               case 27:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(25);
                  break;
               case 31:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(32, 33);
                  break;
               case 32:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(25);
                  break;
               case 33:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(32);
                  break;
               case 35:
                  if (curChar == 120)
                     jjCheckNAdd(36);
                  break;
               case 36:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAddTwoStates(36, 37);
                  break;
               case 37:
                  if ((0x20100000201000L & l) != 0L && kind > 84)
                     kind = 84;
                  break;
               case 38:
                  if (curChar == 88)
                     jjCheckNAdd(36);
                  break;
               case 41:
                  jjAddStates(49, 50);
                  break;
               case 44:
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 44;
                  break;
               case 46:
                  jjCheckNAddTwoStates(46, 47);
                  break;
               case 48:
               case 49:
                  jjCheckNAddTwoStates(49, 47);
                  break;
               case 53:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(51, 52);
                  break;
               case 56:
                  if ((0x104000001040L & l) != 0L && kind > 85)
                     kind = 85;
                  break;
               case 58:
                  if ((0x1000000010L & l) != 0L && kind > 91)
                     kind = 91;
                  break;
               case 66:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(53, 54);
                  break;
               case 69:
                  if ((0x104000001040L & l) != 0L && kind > 86)
                     kind = 86;
                  break;
               case 79:
                  if (curChar == 76)
                     jjAddStates(44, 45);
                  break;
               case 81:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(82);
                  break;
               case 83:
                  if (curChar == 92)
                     jjAddStates(55, 58);
                  break;
               case 84:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(82);
                  break;
               case 88:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(89, 90);
                  break;
               case 89:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(82);
                  break;
               case 90:
               case 92:
               case 94:
               case 97:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(89);
                  break;
               case 91:
                  if (curChar == 117)
                     jjCheckNAddStates(59, 62);
                  break;
               case 93:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 94;
                  break;
               case 95:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 96;
                  break;
               case 96:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 97;
                  break;
               case 99:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 100:
                  if (curChar == 92)
                     jjAddStates(63, 66);
                  break;
               case 101:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 106:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(107, 108);
                  break;
               case 107:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 108:
               case 110:
               case 112:
               case 115:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(107);
                  break;
               case 109:
                  if (curChar == 117)
                     jjCheckNAddStates(67, 70);
                  break;
               case 111:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 112;
                  break;
               case 113:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 114;
                  break;
               case 114:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 115;
                  break;
               case 117:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 118:
                  if (curChar == 92)
                     jjAddStates(71, 73);
                  break;
               case 119:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 125:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(126, 127);
                  break;
               case 126:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 127:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(126);
                  break;
               case 128:
                  jjAddStates(74, 75);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 24:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 41:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(49, 50);
                  break;
               case 44:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 44;
                  break;
               case 46:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 48:
               case 49:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(49, 47);
                  break;
               case 81:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 82;
                  break;
               case 99:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(38, 40);
                  break;
               case 117:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(41, 43);
                  break;
               case 128:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(74, 75);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 130 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   60, 61, 63, 64, 65, 66, 70, 71, 73, 74, 75, 58, 76, 77, 117, 118, 
   120, 128, 129, 40, 43, 45, 24, 26, 35, 38, 36, 37, 48, 50, 52, 53, 
   56, 62, 53, 56, 81, 83, 99, 100, 102, 117, 118, 120, 80, 98, 27, 28, 
   31, 41, 42, 54, 55, 67, 68, 84, 85, 88, 91, 89, 92, 93, 95, 101, 
   103, 106, 109, 107, 110, 111, 113, 119, 122, 125, 128, 129, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, "\43", "\73", 
"\155\157\144\165\154\145", "\173", "\175", "\141\142\163\164\162\141\143\164", 
"\151\156\164\145\162\146\141\143\145", "\72", "\54", "\72\72", "\166\141\154\165\145\164\171\160\145", 
"\143\165\163\164\157\155", "\164\162\165\156\143\141\164\141\142\154\145", 
"\163\165\160\160\157\162\164\163", "\160\165\142\154\151\143", "\160\162\151\166\141\164\145", 
"\146\141\143\164\157\162\171", "\50", "\51", "\151\156", "\143\157\156\163\164", "\75", "\174", "\136", 
"\46", "\76\76", "\74\74", "\53", "\55", "\52", "\57", "\45", "\176", 
"\124\122\125\105", "\106\101\114\123\105", "\164\171\160\145\144\145\146", 
"\156\141\164\151\166\145", "\146\154\157\141\164", "\144\157\165\142\154\145", "\154\157\156\147", 
"\163\150\157\162\164", "\165\156\163\151\147\156\145\144", "\143\150\141\162", 
"\167\143\150\141\162", "\142\157\157\154\145\141\156", "\157\143\164\145\164", "\141\156\171", 
"\117\142\152\145\143\164", "\163\164\162\165\143\164", "\165\156\151\157\156", 
"\163\167\151\164\143\150", "\143\141\163\145", "\144\145\146\141\165\154\164", "\145\156\165\155", 
"\163\145\161\165\145\156\143\145", "\74", "\76", "\163\164\162\151\156\147", "\167\163\164\162\151\156\147", 
"\133", "\135", "\162\145\141\144\157\156\154\171", 
"\141\164\164\162\151\142\165\164\145", "\145\170\143\145\160\164\151\157\156", "\157\156\145\167\141\171", 
"\166\157\151\144", "\157\165\164", "\151\156\157\165\164", "\162\141\151\163\145\163", 
"\143\157\156\164\145\170\164", "\146\151\170\145\144", "\126\141\154\165\145\102\141\163\145", null, null, 
null, null, null, null, null, null, null, null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0xfffffffffffffe01L, 0x3fffffffL, 
};
static final long[] jjtoSkip = {
   0x1feL, 0x0L, 
};
private ASCII_CharStream input_stream;
private final int[] jjrounds = new int[130];
private final int[] jjstateSet = new int[260];
protected char curChar;
public IDLParserTokenManager(ASCII_CharStream stream)
{
   if (ASCII_CharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public IDLParserTokenManager(ASCII_CharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(ASCII_CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 130; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(ASCII_CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

private final Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public final Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}

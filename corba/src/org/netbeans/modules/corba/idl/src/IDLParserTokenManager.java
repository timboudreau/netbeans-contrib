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

public class IDLParserTokenManager implements IDLParserConstants
{
private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0xfffffc0033f8c800L) != 0L || (active1 & 0x1ffccL) != 0L)
         {
            jjmatchedKind = 81;
            return 1;
         }
         if ((active0 & 0x8000000000L) != 0L)
            return 43;
         return -1;
      case 1:
         if ((active0 & 0x10008000L) != 0L || (active1 & 0x1000L) != 0L)
            return 1;
         if ((active0 & 0xfffffc0023f84800L) != 0L || (active1 & 0x1efccL) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 81;
               jjmatchedPos = 1;
            }
            return 1;
         }
         return -1;
      case 2:
         if ((active0 & 0x80000000000000L) != 0L || (active1 & 0x800L) != 0L)
            return 1;
         if ((active0 & 0xff7ffc0023f8c800L) != 0L || (active1 & 0x1f7ccL) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 2;
            return 1;
         }
         return -1;
      case 3:
         if ((active0 & 0x5009040000000000L) != 0L || (active1 & 0x400L) != 0L)
            return 1;
         if ((active0 & 0xaf76f80023f8c800L) != 0L || (active1 & 0x1f3ccL) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 3;
            return 1;
         }
         return -1;
      case 4:
         if ((active0 & 0x452480020000000L) != 0L || (active1 & 0x9000L) != 0L)
            return 1;
         if ((active0 & 0xab24b00003f8c800L) != 0L || (active1 & 0x163ccL) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 4;
            return 1;
         }
         return -1;
      case 5:
         if ((active0 & 0xb00a00000900800L) != 0L || (active1 & 0x2204L) != 0L)
            return 1;
         if ((active0 & 0xa02410000368c000L) != 0L || (active1 & 0x141c8L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 5;
            return 1;
         }
         return -1;
      case 6:
         if ((active0 & 0x800400000068c000L) != 0L || (active1 & 0x101c0L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 6;
            return 1;
         }
         if ((active0 & 0x2020100003000000L) != 0L || (active1 & 0x4008L) != 0L)
            return 1;
         return -1;
      case 7:
         if ((active0 & 0x288000L) != 0L || (active1 & 0x10180L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 7;
            return 1;
         }
         if ((active0 & 0x8004000000404000L) != 0L || (active1 & 0x40L) != 0L)
            return 1;
         return -1;
      case 8:
         if ((active0 & 0x200000L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 8;
            return 1;
         }
         if ((active0 & 0x88000L) != 0L || (active1 & 0x10180L) != 0L)
            return 1;
         return -1;
      case 9:
         if ((active0 & 0x200000L) != 0L)
         {
            jjmatchedKind = 81;
            jjmatchedPos = 9;
            return 1;
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
         return jjStartNfaWithStates_0(0, 39, 43);
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
            return jjStartNfaWithStates_0(2, 75, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x40200000008000L, active1, 0x88L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000800000200000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x8000L);
      case 121:
         if ((active0 & 0x80000000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 55, 1);
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
            return jjStartNfaWithStates_0(3, 42, 1);
         break;
      case 83:
         return jjMoveStringLiteralDfa4_0(active0, 0x80000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x2010400000000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000000000L, active1, 0L);
      case 100:
         if ((active1 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(3, 74, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x40L);
      case 101:
         if ((active0 & 0x1000000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 60, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x140100000008000L, active1, 0x8100L);
      case 103:
         if ((active0 & 0x1000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 48, 1);
         break;
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x4200000000000L, active1, 0x4L);
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000000800000L, active1, 0L);
      case 109:
         if ((active0 & 0x4000000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 62, 1);
         break;
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000L, active1, 0L);
      case 114:
         if ((active0 & 0x8000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 51, 1);
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
            return jjStartNfaWithStates_0(4, 43, 1);
         break;
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000L, active1, 0x200L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0xb00000000200000L, active1, 0L);
      case 100:
         if ((active1 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(4, 79, 1);
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
            return jjStartNfaWithStates_0(4, 58, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x4L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x2500000L, active1, 0x40L);
      case 112:
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x100L);
      case 114:
         if ((active0 & 0x10000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 52, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0xc000L, active1, 0L);
      case 116:
         if ((active0 & 0x20000000L) != 0L)
            return jjStartNfaWithStates_0(4, 29, 1);
         else if ((active0 & 0x400000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 46, 1);
         else if ((active0 & 0x2000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 49, 1);
         else if ((active0 & 0x40000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 54, 1);
         else if ((active1 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(4, 76, 1);
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
            return jjStartNfaWithStates_0(5, 23, 1);
         break;
      case 101:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(5, 11, 1);
         else if ((active0 & 0x200000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 45, 1);
         else if ((active0 & 0x800000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 47, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x100000000000L, active1, 0L);
      case 102:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000L, active1, 0L);
      case 103:
         if ((active1 & 0x4L) != 0L)
            return jjStartNfaWithStates_0(5, 66, 1);
         break;
      case 104:
         if ((active0 & 0x800000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 59, 1);
         break;
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000000000000000L, active1, 0L);
      case 109:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(5, 20, 1);
         break;
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x8004000000000000L, active1, 0x48L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x2400000L, active1, 0L);
      case 115:
         if ((active1 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(5, 77, 1);
         break;
      case 116:
         if ((active0 & 0x100000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 56, 1);
         else if ((active0 & 0x200000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 57, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x1080000L, active1, 0x100L);
      case 120:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x4000L);
      case 121:
         if ((active1 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(5, 73, 1);
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
            return jjStartNfaWithStates_0(6, 24, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x4000000000000L, active1, 0L);
      case 102:
         if ((active0 & 0x100000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 44, 1);
         break;
      case 103:
         if ((active1 & 0x8L) != 0L)
            return jjStartNfaWithStates_0(6, 67, 1);
         break;
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x100L);
      case 108:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x40L);
      case 110:
         if ((active0 & 0x20000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 53, 1);
         break;
      case 116:
         if ((active0 & 0x2000000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 61, 1);
         else if ((active1 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(6, 78, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x600000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x80L);
      case 121:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(6, 25, 1);
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
            return jjStartNfaWithStates_0(7, 50, 1);
         break;
      case 101:
         if ((active0 & 0x8000000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 63, 1);
         break;
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x100L);
      case 112:
         return jjMoveStringLiteralDfa8_0(active0, 0x80000L, active1, 0L);
      case 115:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(7, 22, 1);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x10000L);
      case 116:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(7, 14, 1);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x80L);
      case 121:
         if ((active1 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(7, 70, 1);
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
            return jjStartNfaWithStates_0(8, 15, 1);
         else if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(8, 19, 1);
         else if ((active1 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(8, 71, 1);
         else if ((active1 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(8, 80, 1);
         break;
      case 110:
         if ((active1 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(8, 72, 1);
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
            return jjStartNfaWithStates_0(10, 21, 1);
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
   jjnewStateCnt = 131;
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
                     jjCheckNAddStates(0, 11);
                  else if (curChar == 46)
                     jjCheckNAddTwoStates(70, 75);
                  else if (curChar == 35)
                  {
                     if (kind > 9)
                        kind = 9;
                     jjCheckNAddStates(12, 15);
                  }
                  else if (curChar == 47)
                     jjAddStates(16, 18);
                  else if (curChar == 34)
                     jjCheckNAddStates(19, 21);
                  else if (curChar == 39)
                     jjAddStates(22, 23);
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 83)
                        kind = 83;
                     jjCheckNAddTwoStates(13, 14);
                  }
                  else if (curChar == 48)
                     jjAddStates(24, 25);
                  if (curChar == 48)
                  {
                     if (kind > 82)
                        kind = 82;
                     jjCheckNAddTwoStates(10, 11);
                  }
                  break;
               case 43:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(49, 50);
                  else if (curChar == 47)
                  {
                     if (kind > 6)
                        kind = 6;
                     jjCheckNAdd(47);
                  }
                  if (curChar == 47)
                     jjCheckNAddTwoStates(44, 45);
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 4:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 7:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 9:
                  if (curChar != 48)
                     break;
                  if (kind > 82)
                     kind = 82;
                  jjCheckNAddTwoStates(10, 11);
                  break;
               case 10:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 82)
                     kind = 82;
                  jjCheckNAddTwoStates(10, 11);
                  break;
               case 12:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 83)
                     kind = 83;
                  jjCheckNAddTwoStates(13, 14);
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 83)
                     kind = 83;
                  jjCheckNAddTwoStates(13, 14);
                  break;
               case 15:
                  if (curChar == 39)
                     jjAddStates(22, 23);
                  break;
               case 16:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 17:
                  if (curChar == 39 && kind > 87)
                     kind = 87;
                  break;
               case 19:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 20:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 21:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 22:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(21);
                  break;
               case 24:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 25:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 26:
                  if (curChar == 34)
                     jjCheckNAddStates(19, 21);
                  break;
               case 27:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 29:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 30:
                  if (curChar != 34)
                     break;
                  if (kind > 89)
                     kind = 89;
                  jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 31:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(32, 33);
                  break;
               case 32:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 33:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(32);
                  break;
               case 35:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 36:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 35;
                  break;
               case 37:
                  if (curChar == 48)
                     jjAddStates(24, 25);
                  break;
               case 39:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjAddStates(26, 27);
                  break;
               case 42:
                  if (curChar == 47)
                     jjAddStates(16, 18);
                  break;
               case 44:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     jjCheckNAddTwoStates(44, 45);
                  break;
               case 45:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 46:
                  if (curChar != 47)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(47);
                  break;
               case 47:
                  if ((0xfffffffffffffbffL & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(47);
                  break;
               case 48:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(49, 50);
                  break;
               case 49:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(49, 50);
                  break;
               case 50:
                  if (curChar == 42)
                     jjAddStates(28, 29);
                  break;
               case 51:
                  if ((0xffff7fffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(52, 50);
                  break;
               case 52:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(52, 50);
                  break;
               case 53:
                  if (curChar == 47 && kind > 7)
                     kind = 7;
                  break;
               case 54:
                  if (curChar != 35)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjCheckNAddStates(12, 15);
                  break;
               case 55:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 56:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(30, 34);
                  break;
               case 57:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(35, 38);
                  break;
               case 58:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(58, 59);
                  break;
               case 59:
                  if (curChar == 34)
                     jjCheckNAdd(60);
                  break;
               case 60:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 61:
                  if (curChar == 34)
                     jjCheckNAddStates(39, 41);
                  break;
               case 62:
                  if (curChar == 10 && kind > 8)
                     kind = 8;
                  break;
               case 63:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(42, 45);
                  break;
               case 64:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(46, 48);
                  break;
               case 65:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(65, 62);
                  break;
               case 66:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(49, 53);
                  break;
               case 67:
                  if ((0xf7ffffe700000200L & l) == 0L)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjCheckNAddTwoStates(67, 68);
                  break;
               case 68:
                  if (curChar != 10)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjCheckNAdd(68);
                  break;
               case 69:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(70, 75);
                  break;
               case 70:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddStates(54, 56);
                  break;
               case 72:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(73);
                  break;
               case 73:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddTwoStates(73, 74);
                  break;
               case 75:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(75, 76);
                  break;
               case 77:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 11);
                  break;
               case 78:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(78, 79);
                  break;
               case 79:
                  if (curChar != 46)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddStates(57, 59);
                  break;
               case 80:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddStates(57, 59);
                  break;
               case 81:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(81, 82);
                  break;
               case 82:
                  if (curChar == 46)
                     jjCheckNAdd(70);
                  break;
               case 83:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(83, 84);
                  break;
               case 85:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(86);
                  break;
               case 86:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 86)
                     kind = 86;
                  jjCheckNAddTwoStates(86, 87);
                  break;
               case 88:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(88, 89);
                  break;
               case 89:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(90, 76);
                  break;
               case 90:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(90, 76);
                  break;
               case 91:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(91, 92);
                  break;
               case 92:
                  if (curChar == 46)
                     jjCheckNAdd(75);
                  break;
               case 93:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(93, 76);
                  break;
               case 95:
                  if (curChar == 39)
                     jjAddStates(60, 61);
                  break;
               case 96:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(97);
                  break;
               case 97:
                  if (curChar == 39 && kind > 88)
                     kind = 88;
                  break;
               case 99:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAdd(97);
                  break;
               case 100:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(101, 102);
                  break;
               case 101:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(97);
                  break;
               case 102:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(101);
                  break;
               case 104:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(97);
                  break;
               case 105:
               case 107:
               case 109:
               case 112:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(104);
                  break;
               case 108:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 109;
                  break;
               case 110:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 111;
                  break;
               case 111:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 112;
                  break;
               case 113:
                  if (curChar == 34)
                     jjCheckNAddStates(62, 64);
                  break;
               case 114:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(62, 64);
                  break;
               case 116:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAddStates(62, 64);
                  break;
               case 117:
                  if (curChar != 34)
                     break;
                  if (kind > 90)
                     kind = 90;
                  jjstateSet[jjnewStateCnt++] = 113;
                  break;
               case 118:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(119, 120);
                  break;
               case 119:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(62, 64);
                  break;
               case 120:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(119);
                  break;
               case 122:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(62, 64);
                  break;
               case 123:
               case 125:
               case 127:
               case 130:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(122);
                  break;
               case 126:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 127;
                  break;
               case 128:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 129;
                  break;
               case 129:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 130;
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
                     jjCheckNAdd(1);
                  }
                  else if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 5;
                  if (curChar == 76)
                     jjAddStates(65, 66);
                  else if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(1);
                  break;
               case 2:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 3:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(4);
                  break;
               case 4:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(4);
                  break;
               case 5:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 6:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(7);
                  break;
               case 7:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAdd(7);
                  break;
               case 8:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 11:
                  if ((0x20100000201000L & l) != 0L && kind > 82)
                     kind = 82;
                  break;
               case 14:
                  if ((0x20100000201000L & l) != 0L && kind > 83)
                     kind = 83;
                  break;
               case 16:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 18:
                  if (curChar == 92)
                     jjAddStates(67, 69);
                  break;
               case 19:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 23:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(24, 25);
                  break;
               case 24:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 25:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(24);
                  break;
               case 27:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 28:
                  if (curChar == 92)
                     jjAddStates(70, 72);
                  break;
               case 29:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 34:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 35:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(19, 21);
                  break;
               case 36:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(35);
                  break;
               case 38:
                  if (curChar == 120)
                     jjCheckNAdd(39);
                  break;
               case 39:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAddTwoStates(39, 40);
                  break;
               case 40:
                  if ((0x20100000201000L & l) != 0L && kind > 84)
                     kind = 84;
                  break;
               case 41:
                  if (curChar == 88)
                     jjCheckNAdd(39);
                  break;
               case 44:
                  jjAddStates(73, 74);
                  break;
               case 47:
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 47;
                  break;
               case 49:
                  jjCheckNAddTwoStates(49, 50);
                  break;
               case 51:
               case 52:
                  jjCheckNAddTwoStates(52, 50);
                  break;
               case 57:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjAddStates(35, 38);
                  break;
               case 60:
                  jjAddStates(75, 76);
                  break;
               case 67:
                  if ((0x7ffffffffffffffeL & l) == 0L)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjAddStates(77, 78);
                  break;
               case 71:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(79, 80);
                  break;
               case 74:
                  if ((0x104000001040L & l) != 0L && kind > 85)
                     kind = 85;
                  break;
               case 76:
                  if ((0x1000000010L & l) != 0L && kind > 91)
                     kind = 91;
                  break;
               case 84:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(81, 82);
                  break;
               case 87:
                  if ((0x104000001040L & l) != 0L && kind > 86)
                     kind = 86;
                  break;
               case 94:
                  if (curChar == 76)
                     jjAddStates(65, 66);
                  break;
               case 96:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(97);
                  break;
               case 98:
                  if (curChar == 92)
                     jjAddStates(83, 86);
                  break;
               case 99:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(97);
                  break;
               case 103:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(104, 105);
                  break;
               case 104:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(97);
                  break;
               case 105:
               case 107:
               case 109:
               case 112:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(104);
                  break;
               case 106:
                  if (curChar == 117)
                     jjCheckNAddStates(87, 90);
                  break;
               case 108:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 109;
                  break;
               case 110:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 111;
                  break;
               case 111:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 112;
                  break;
               case 114:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(62, 64);
                  break;
               case 115:
                  if (curChar == 92)
                     jjAddStates(91, 94);
                  break;
               case 116:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(62, 64);
                  break;
               case 121:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(122, 123);
                  break;
               case 122:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(62, 64);
                  break;
               case 123:
               case 125:
               case 127:
               case 130:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(122);
                  break;
               case 124:
                  if (curChar == 117)
                     jjCheckNAddStates(95, 98);
                  break;
               case 126:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 127;
                  break;
               case 128:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 129;
                  break;
               case 129:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 130;
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
               case 16:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 27:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(19, 21);
                  break;
               case 44:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(73, 74);
                  break;
               case 47:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 47;
                  break;
               case 49:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(49, 50);
                  break;
               case 51:
               case 52:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(52, 50);
                  break;
               case 60:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(75, 76);
                  break;
               case 96:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 97;
                  break;
               case 114:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(62, 64);
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
      if ((i = jjnewStateCnt) == (startsAt = 131 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   78, 79, 81, 82, 83, 84, 88, 89, 91, 92, 93, 76, 55, 56, 67, 68, 
   43, 46, 48, 27, 28, 30, 16, 18, 38, 41, 39, 40, 51, 53, 56, 57, 
   58, 59, 62, 57, 58, 59, 62, 62, 63, 66, 64, 65, 62, 63, 64, 65, 
   62, 64, 65, 62, 63, 66, 70, 71, 74, 80, 71, 74, 96, 98, 114, 115, 
   117, 95, 113, 19, 20, 23, 29, 31, 34, 44, 45, 60, 61, 67, 68, 72, 
   73, 85, 86, 99, 100, 103, 106, 104, 107, 108, 110, 116, 118, 121, 124, 122, 
   125, 126, 128, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, "\73", 
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
null, null, null, null, null, null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0xfffffffffffffc01L, 0xfffffffL, 
};
static final long[] jjtoSkip = {
   0x3feL, 0x0L, 
};
private ASCII_CharStream input_stream;
private final int[] jjrounds = new int[131];
private final int[] jjstateSet = new int[262];
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
   for (i = 131; i-- > 0;)
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

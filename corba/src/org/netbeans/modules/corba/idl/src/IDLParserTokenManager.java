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
         if ((active0 & 0xffffe0019fc65e00L) != 0L || (active1 & 0xffe67L) != 0L)
         {
            jjmatchedKind = 84;
            return 1;
         }
         if ((active0 & 0x40000000000L) != 0L)
            return 32;
         return -1;
      case 1:
         if ((active0 & 0xffffe0011fc25a00L) != 0L || (active1 & 0xf7e67L) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 84;
               jjmatchedPos = 1;
            }
            return 1;
         }
         if ((active0 & 0x80040400L) != 0L || (active1 & 0x8000L) != 0L)
            return 1;
         return -1;
      case 2:
         if ((active0 & 0x400000000000000L) != 0L || (active1 & 0x4000L) != 0L)
            return 1;
         if ((active0 & 0xfbffe0011fc65a00L) != 0L || (active1 & 0xfbe67L) != 0L)
         {
            jjmatchedKind = 84;
            jjmatchedPos = 2;
            return 1;
         }
         return -1;
      case 3:
         if ((active0 & 0x8048200000000000L) != 0L || (active1 & 0x2002L) != 0L)
            return 1;
         if ((active0 & 0x7bb7c0011fc65a00L) != 0L || (active1 & 0xf9e65L) != 0L)
         {
            jjmatchedKind = 84;
            jjmatchedPos = 3;
            return 1;
         }
         return -1;
      case 4:
         if ((active0 & 0x2292400100000000L) != 0L || (active1 & 0x48000L) != 0L)
            return 1;
         if ((active0 & 0x592580001fc65a00L) != 0L || (active1 & 0xb1e65L) != 0L)
         {
            jjmatchedKind = 84;
            jjmatchedPos = 4;
            return 1;
         }
         return -1;
      case 5:
         if ((active0 & 0x5805000004804a00L) != 0L || (active1 & 0x11020L) != 0L)
            return 1;
         if ((active0 & 0x12080001b461000L) != 0L || (active1 & 0xa0e45L) != 0L)
         {
            jjmatchedKind = 84;
            jjmatchedPos = 5;
            return 1;
         }
         return -1;
      case 6:
         if ((active0 & 0x100800018001000L) != 0L || (active1 & 0x20041L) != 0L)
            return 1;
         if ((active0 & 0x20000003460000L) != 0L || (active1 & 0x80e04L) != 0L)
         {
            jjmatchedKind = 84;
            jjmatchedPos = 6;
            return 1;
         }
         return -1;
      case 7:
         if ((active0 & 0x20000002020000L) != 0L || (active1 & 0x204L) != 0L)
            return 1;
         if ((active0 & 0x1440000L) != 0L || (active1 & 0x80c00L) != 0L)
         {
            jjmatchedKind = 84;
            jjmatchedPos = 7;
            return 1;
         }
         return -1;
      case 8:
         if ((active0 & 0x440000L) != 0L || (active1 & 0x80c00L) != 0L)
            return 1;
         if ((active0 & 0x1000000L) != 0L)
         {
            jjmatchedKind = 84;
            jjmatchedPos = 8;
            return 1;
         }
         return -1;
      case 9:
         if ((active0 & 0x1000000L) != 0L)
         {
            jjmatchedKind = 84;
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
      case 35:
         return jjStopAtPos(0, 8);
      case 37:
         return jjStopAtPos(0, 43);
      case 38:
         return jjStopAtPos(0, 36);
      case 40:
         return jjStopAtPos(0, 29);
      case 41:
         return jjStopAtPos(0, 30);
      case 42:
         return jjStopAtPos(0, 41);
      case 43:
         return jjStopAtPos(0, 39);
      case 44:
         return jjStopAtPos(0, 20);
      case 45:
         return jjStopAtPos(0, 40);
      case 47:
         return jjStartNfaWithStates_0(0, 42, 32);
      case 58:
         jjmatchedKind = 19;
         return jjMoveStringLiteralDfa1_0(0x200000L, 0x0L);
      case 59:
         return jjStopAtPos(0, 13);
      case 60:
         jjmatchedKind = 67;
         return jjMoveStringLiteralDfa1_0(0x4000000000L, 0x0L);
      case 61:
         return jjStopAtPos(0, 33);
      case 62:
         jjmatchedKind = 68;
         return jjMoveStringLiteralDfa1_0(0x2000000000L, 0x0L);
      case 70:
         return jjMoveStringLiteralDfa1_0(0x400000000000L, 0x0L);
      case 73:
         return jjMoveStringLiteralDfa1_0(0x400L, 0x0L);
      case 79:
         return jjMoveStringLiteralDfa1_0(0x800000000000000L, 0x0L);
      case 84:
         return jjMoveStringLiteralDfa1_0(0x200000000000L, 0x0L);
      case 86:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x80000L);
      case 91:
         return jjStopAtPos(0, 71);
      case 93:
         return jjStopAtPos(0, 72);
      case 94:
         return jjStopAtPos(0, 35);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x400000000020000L, 0x400L);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x100000000000000L, 0x0L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x8040000100800000L, 0x20000L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x4000000000000L, 0x1L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x802L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x2000010000000L, 0x40000L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x80040000L, 0x8000L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x8000000000000L, 0x0L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x4000L, 0x0L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x1000000000000L, 0x0L);
      case 111:
         return jjMoveStringLiteralDfa1_0(0x200000000000000L, 0x5000L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0xc000a00L, 0x0L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x10200L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x5010000002000000L, 0x24L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x800001000000L, 0x0L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x2020000000000000L, 0x0L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x401000L, 0x2000L);
      case 119:
         return jjMoveStringLiteralDfa1_0(0x80000000000000L, 0x40L);
      case 123:
         return jjStopAtPos(0, 15);
      case 124:
         return jjStopAtPos(0, 34);
      case 125:
         return jjStopAtPos(0, 16);
      case 126:
         return jjStopAtPos(0, 44);
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
         if ((active0 & 0x200000L) != 0L)
            return jjStopAtPos(1, 21);
         break;
      case 60:
         if ((active0 & 0x4000000000L) != 0L)
            return jjStopAtPos(1, 38);
         break;
      case 62:
         if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(1, 37);
         break;
      case 65:
         return jjMoveStringLiteralDfa2_0(active0, 0x400000000000L, active1, 0L);
      case 68:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(1, 10, 1);
         break;
      case 82:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x8001000010400000L, active1, 0x90000L);
      case 98:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000020000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa2_0(active0, 0x280000000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000L, active1, 0x205L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x50000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x40000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000000000000L, active1, 0L);
      case 110:
         if ((active0 & 0x80000000L) != 0L)
         {
            jjmatchedKind = 31;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_0(active0, 0x2420000000040000L, active1, 0x9002L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x10c000100004000L, active1, 0x22000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x9000a00L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x40L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000000000000000L, active1, 0x420L);
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x6800000L, active1, 0x4000L);
      case 119:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000000000000000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x800L);
      case 121:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000000L, active1, 0L);
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
         return jjMoveStringLiteralDfa3_0(active0, 0x400000000000L, active1, 0L);
      case 85:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000000000200L, active1, 0x200L);
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000000L, active1, 0x800L);
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x800L, active1, 0x1000L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x1L);
      case 104:
         return jjMoveStringLiteralDfa3_0(active0, 0x80000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x6000000008000000L, active1, 0x12000L);
      case 106:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000L, active1, 0x80000L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000100000000L, active1, 0x20000L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x112000000000000L, active1, 0x8000L);
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x800002000000L, active1, 0L);
      case 113:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x4L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000000001000L, active1, 0x20L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x8020000000820000L, active1, 0L);
      case 116:
         if ((active1 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(2, 78, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x201000000040000L, active1, 0x440L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000001000000L, active1, 0x2L);
      case 120:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x40000L);
      case 121:
         if ((active0 & 0x400000000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 58, 1);
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
         if ((active0 & 0x200000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 45, 1);
         break;
      case 83:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x82000000000000L, active1, 0x1L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000000000000L, active1, 0L);
      case 100:
         if ((active1 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(3, 77, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x200L);
      case 101:
         if ((active0 & 0x8000000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 63, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0xa00800000040000L, active1, 0x40800L);
      case 102:
         return jjMoveStringLiteralDfa4_0(active0, 0x800L, active1, 0L);
      case 103:
         if ((active0 & 0x8000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 51, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x200L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x21000000000000L, active1, 0x20L);
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x100000004000000L, active1, 0L);
      case 109:
         if ((active1 & 0x2L) != 0L)
            return jjStartNfaWithStates_0(3, 65, 1);
         break;
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000000000000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000000L, active1, 0L);
      case 114:
         if ((active0 & 0x40000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 54, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x10000000000000L, active1, 0x440L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x100001000L, active1, 0x10000L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000000010820000L, active1, 0x20000L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000000404000L, active1, 0x88004L);
      case 118:
         return jjMoveStringLiteralDfa4_0(active0, 0x8000000L, active1, 0L);
      case 119:
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x1000L);
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
         if ((active0 & 0x400000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 46, 1);
         break;
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000000L, active1, 0x1000L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x5800000001000000L, active1, 0L);
      case 100:
         if ((active1 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(4, 82, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0x800000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000000400000L, active1, 0xb0004L);
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x4001800L, active1, 0x440L);
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000000004000L, active1, 0L);
      case 109:
         return jjMoveStringLiteralDfa5_0(active0, 0x200L, active1, 0L);
      case 110:
         if ((active0 & 0x2000000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 61, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x20L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x12800000L, active1, 0x200L);
      case 112:
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x800L);
      case 114:
         if ((active0 & 0x80000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 55, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0x60000L, active1, 0L);
      case 116:
         if ((active0 & 0x100000000L) != 0L)
            return jjStartNfaWithStates_0(4, 32, 1);
         else if ((active0 & 0x2000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 49, 1);
         else if ((active0 & 0x10000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 52, 1);
         else if ((active0 & 0x200000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 57, 1);
         else if ((active1 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(4, 79, 1);
         break;
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x1L);
      case 118:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000000000L, active1, 0L);
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
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x80000L);
      case 97:
         if ((active0 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(5, 9, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x100000001020000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x400L);
      case 99:
         if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_0(5, 26, 1);
         break;
      case 101:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(5, 14, 1);
         else if ((active0 & 0x1000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 48, 1);
         else if ((active0 & 0x4000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 50, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x800000000000L, active1, 0L);
      case 102:
         return jjMoveStringLiteralDfa6_0(active0, 0x40000L, active1, 0L);
      case 103:
         if ((active1 & 0x20L) != 0L)
            return jjStartNfaWithStates_0(5, 69, 1);
         break;
      case 104:
         if ((active0 & 0x4000000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 62, 1);
         break;
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x1L);
      case 109:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(5, 23, 1);
         break;
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000000000000L, active1, 0x244L);
      case 111:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x12000000L, active1, 0L);
      case 115:
         if ((active1 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(5, 80, 1);
         break;
      case 116:
         if ((active0 & 0x800000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 59, 1);
         else if ((active0 & 0x1000000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 60, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x8400000L, active1, 0x800L);
      case 120:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(5, 11, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x20000L);
      case 121:
         if ((active1 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(5, 76, 1);
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
         return jjMoveStringLiteralDfa7_0(active0, 0x40000L, active1, 0x80000L);
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x20000L, active1, 0x4L);
      case 101:
         if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_0(6, 27, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x20000000000000L, active1, 0L);
      case 102:
         if ((active0 & 0x800000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 47, 1);
         break;
      case 103:
         if ((active1 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(6, 70, 1);
         break;
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x800L);
      case 108:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x200L);
      case 110:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(6, 12, 1);
         else if ((active0 & 0x100000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 56, 1);
         break;
      case 116:
         if ((active1 & 0x1L) != 0L)
            return jjStartNfaWithStates_0(6, 64, 1);
         else if ((active1 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(6, 81, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x3000000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x400L);
      case 121:
         if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_0(6, 28, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x400000L, active1, 0L);
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
         return jjMoveStringLiteralDfa8_0(active0, 0x1000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x40000L, active1, 0L);
      case 100:
         if ((active0 & 0x20000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 53, 1);
         break;
      case 101:
         if ((active1 & 0x4L) != 0L)
            return jjStartNfaWithStates_0(7, 66, 1);
         break;
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x800L);
      case 112:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000L, active1, 0L);
      case 115:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(7, 25, 1);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x80000L);
      case 116:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(7, 17, 1);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x400L);
      case 121:
         if ((active1 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(7, 73, 1);
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
         return jjMoveStringLiteralDfa9_0(active0, 0x1000000L, active1, 0L);
      case 101:
         if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(8, 18, 1);
         else if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(8, 22, 1);
         else if ((active1 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(8, 74, 1);
         else if ((active1 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(8, 83, 1);
         break;
      case 110:
         if ((active1 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(8, 75, 1);
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
         return jjMoveStringLiteralDfa10_0(active0, 0x1000000L);
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
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(10, 24, 1);
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
   jjnewStateCnt = 122;
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
               case 32:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(38, 39);
                  else if (curChar == 47)
                  {
                     if (kind > 6)
                        kind = 6;
                     jjCheckNAdd(36);
                  }
                  if (curChar == 47)
                     jjCheckNAddTwoStates(33, 34);
                  break;
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 13);
                  else if (curChar == 34)
                     jjCheckNAddStates(14, 18);
                  else if (curChar == 46)
                     jjCheckNAddTwoStates(44, 49);
                  else if (curChar == 47)
                     jjAddStates(19, 21);
                  else if (curChar == 39)
                     jjAddStates(22, 23);
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 86)
                        kind = 86;
                     jjCheckNAddTwoStates(13, 14);
                  }
                  else if (curChar == 48)
                     jjAddStates(24, 25);
                  if (curChar == 48)
                  {
                     if (kind > 85)
                        kind = 85;
                     jjCheckNAddTwoStates(10, 11);
                  }
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 4:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 7:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 9:
                  if (curChar != 48)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddTwoStates(10, 11);
                  break;
               case 10:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddTwoStates(10, 11);
                  break;
               case 12:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 86)
                     kind = 86;
                  jjCheckNAddTwoStates(13, 14);
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 86)
                     kind = 86;
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
                  if (curChar == 39 && kind > 90)
                     kind = 90;
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
                  if (curChar == 48)
                     jjAddStates(24, 25);
                  break;
               case 28:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 87)
                     kind = 87;
                  jjAddStates(26, 27);
                  break;
               case 31:
                  if (curChar == 47)
                     jjAddStates(19, 21);
                  break;
               case 33:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     jjCheckNAddTwoStates(33, 34);
                  break;
               case 34:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 35:
                  if (curChar != 47)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(36);
                  break;
               case 36:
                  if ((0xfffffffffffffbffL & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(36);
                  break;
               case 37:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(38, 39);
                  break;
               case 38:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(38, 39);
                  break;
               case 39:
                  if (curChar == 42)
                     jjAddStates(28, 29);
                  break;
               case 40:
                  if ((0xffff7fffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(41, 39);
                  break;
               case 41:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(41, 39);
                  break;
               case 42:
                  if (curChar == 47 && kind > 7)
                     kind = 7;
                  break;
               case 43:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(44, 49);
                  break;
               case 44:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 88)
                     kind = 88;
                  jjCheckNAddStates(30, 32);
                  break;
               case 46:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(47);
                  break;
               case 47:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 88)
                     kind = 88;
                  jjCheckNAddTwoStates(47, 48);
                  break;
               case 49:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(49, 50);
                  break;
               case 51:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 13);
                  break;
               case 52:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(52, 53);
                  break;
               case 53:
                  if (curChar != 46)
                     break;
                  if (kind > 88)
                     kind = 88;
                  jjCheckNAddStates(33, 35);
                  break;
               case 54:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 88)
                     kind = 88;
                  jjCheckNAddStates(33, 35);
                  break;
               case 55:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 56:
                  if (curChar == 46)
                     jjCheckNAdd(44);
                  break;
               case 57:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 59:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(60);
                  break;
               case 60:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 89)
                     kind = 89;
                  jjCheckNAddTwoStates(60, 61);
                  break;
               case 62:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(62, 63);
                  break;
               case 63:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(64, 50);
                  break;
               case 64:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(64, 50);
                  break;
               case 65:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(65, 66);
                  break;
               case 66:
                  if (curChar == 46)
                     jjCheckNAdd(49);
                  break;
               case 67:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(67, 50);
                  break;
               case 68:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(68, 69);
                  break;
               case 69:
                  if (curChar == 46)
                     jjCheckNAdd(70);
                  break;
               case 70:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 95)
                     kind = 95;
                  jjCheckNAdd(70);
                  break;
               case 72:
                  if (curChar == 39)
                     jjAddStates(36, 37);
                  break;
               case 73:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(74);
                  break;
               case 74:
                  if (curChar == 39 && kind > 91)
                     kind = 91;
                  break;
               case 76:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAdd(74);
                  break;
               case 77:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(78, 79);
                  break;
               case 78:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(74);
                  break;
               case 79:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(78);
                  break;
               case 81:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(74);
                  break;
               case 82:
               case 84:
               case 86:
               case 89:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(81);
                  break;
               case 85:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 86;
                  break;
               case 87:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 88;
                  break;
               case 88:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 89;
                  break;
               case 90:
                  if (curChar == 34)
                     jjCheckNAddStates(38, 40);
                  break;
               case 91:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 93:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 94:
                  if (curChar != 34)
                     break;
                  if (kind > 93)
                     kind = 93;
                  jjstateSet[jjnewStateCnt++] = 90;
                  break;
               case 95:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(96, 97);
                  break;
               case 96:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 97:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(96);
                  break;
               case 99:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 100:
               case 102:
               case 104:
               case 107:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAdd(99);
                  break;
               case 103:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 104;
                  break;
               case 105:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 106;
                  break;
               case 106:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 107;
                  break;
               case 108:
                  if (curChar == 34)
                     jjCheckNAddStates(14, 18);
                  break;
               case 109:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 111:
                  if ((0x80ff008400000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 112:
                  if (curChar != 34)
                     break;
                  if (kind > 92)
                     kind = 92;
                  jjstateSet[jjnewStateCnt++] = 113;
                  break;
               case 113:
                  if (curChar == 34)
                     jjCheckNAddStates(41, 43);
                  break;
               case 114:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(115, 116);
                  break;
               case 115:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 116:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(115);
                  break;
               case 118:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 119:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 118;
                  break;
               case 120:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(120, 121);
                  break;
               case 121:
                  if (curChar == 34 && kind > 96)
                     kind = 96;
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
                     if (kind > 84)
                        kind = 84;
                     jjCheckNAdd(1);
                  }
                  else if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 5;
                  if (curChar == 76)
                     jjAddStates(44, 45);
                  else if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAdd(1);
                  break;
               case 2:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 3:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAdd(4);
                  break;
               case 4:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAdd(4);
                  break;
               case 5:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 6:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAdd(7);
                  break;
               case 7:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAdd(7);
                  break;
               case 8:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 11:
                  if ((0x20100000201000L & l) != 0L && kind > 85)
                     kind = 85;
                  break;
               case 14:
                  if ((0x20100000201000L & l) != 0L && kind > 86)
                     kind = 86;
                  break;
               case 16:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(17);
                  break;
               case 18:
                  if (curChar == 92)
                     jjAddStates(46, 48);
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
                  if (curChar == 120)
                     jjCheckNAdd(28);
                  break;
               case 28:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 87)
                     kind = 87;
                  jjCheckNAddTwoStates(28, 29);
                  break;
               case 29:
                  if ((0x20100000201000L & l) != 0L && kind > 87)
                     kind = 87;
                  break;
               case 30:
                  if (curChar == 88)
                     jjCheckNAdd(28);
                  break;
               case 33:
                  jjAddStates(49, 50);
                  break;
               case 36:
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 38:
                  jjCheckNAddTwoStates(38, 39);
                  break;
               case 40:
               case 41:
                  jjCheckNAddTwoStates(41, 39);
                  break;
               case 45:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(51, 52);
                  break;
               case 48:
                  if ((0x104000001040L & l) != 0L && kind > 88)
                     kind = 88;
                  break;
               case 50:
                  if ((0x1000000010L & l) != 0L && kind > 94)
                     kind = 94;
                  break;
               case 58:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(53, 54);
                  break;
               case 61:
                  if ((0x104000001040L & l) != 0L && kind > 89)
                     kind = 89;
                  break;
               case 71:
                  if (curChar == 76)
                     jjAddStates(44, 45);
                  break;
               case 73:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(74);
                  break;
               case 75:
                  if (curChar == 92)
                     jjAddStates(55, 58);
                  break;
               case 76:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(74);
                  break;
               case 80:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(81, 82);
                  break;
               case 81:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(74);
                  break;
               case 82:
               case 84:
               case 86:
               case 89:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(81);
                  break;
               case 83:
                  if (curChar == 117)
                     jjCheckNAddStates(59, 62);
                  break;
               case 85:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 86;
                  break;
               case 87:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 88;
                  break;
               case 88:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 89;
                  break;
               case 91:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 92:
                  if (curChar == 92)
                     jjAddStates(63, 66);
                  break;
               case 93:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 98:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(99, 100);
                  break;
               case 99:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(38, 40);
                  break;
               case 100:
               case 102:
               case 104:
               case 107:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(99);
                  break;
               case 101:
                  if (curChar == 117)
                     jjCheckNAddStates(67, 70);
                  break;
               case 103:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 104;
                  break;
               case 105:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 106;
                  break;
               case 106:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 107;
                  break;
               case 109:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 110:
                  if (curChar == 92)
                     jjAddStates(71, 73);
                  break;
               case 111:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 117:
                  if (curChar == 120)
                     jjCheckNAddTwoStates(118, 119);
                  break;
               case 118:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(41, 43);
                  break;
               case 119:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAdd(118);
                  break;
               case 120:
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
               case 16:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 33:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(49, 50);
                  break;
               case 36:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 38:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(38, 39);
                  break;
               case 40:
               case 41:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(41, 39);
                  break;
               case 73:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 74;
                  break;
               case 91:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(38, 40);
                  break;
               case 109:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(41, 43);
                  break;
               case 120:
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
      if ((i = jjnewStateCnt) == (startsAt = 122 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   52, 53, 55, 56, 57, 58, 62, 63, 65, 66, 67, 50, 68, 69, 109, 110, 
   112, 120, 121, 32, 35, 37, 16, 18, 27, 30, 28, 29, 40, 42, 44, 45, 
   48, 54, 45, 48, 73, 75, 91, 92, 94, 109, 110, 112, 72, 90, 19, 20, 
   23, 33, 34, 46, 47, 59, 60, 76, 77, 80, 83, 81, 84, 85, 87, 93, 
   95, 98, 101, 99, 102, 103, 105, 111, 114, 117, 120, 121, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, "\43", 
"\160\162\141\147\155\141", "\111\104", "\160\162\145\146\151\170", "\166\145\162\163\151\157\156", "\73", 
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
   0xffffffffffffff01L, 0x1ffffffffL, 
};
static final long[] jjtoSkip = {
   0xfeL, 0x0L, 
};
private ASCII_CharStream input_stream;
private final int[] jjrounds = new int[122];
private final int[] jjstateSet = new int[244];
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
   for (i = 122; i-- > 0;)
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

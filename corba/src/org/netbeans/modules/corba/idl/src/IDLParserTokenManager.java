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

public class IDLParserTokenManager implements IDLParserConstants
{
private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x4000000000L) != 0L)
            return 14;
         if ((active0 & 0x7ffffe0019fc6400L) != 0L || (active1 & 0xffe6L) != 0L)
         {
            jjmatchedKind = 80;
            return 1;
         }
         return -1;
      case 1:
         if ((active0 & 0x7ffffe0011fc2400L) != 0L || (active1 & 0xf7e6L) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 80;
               jjmatchedPos = 1;
            }
            return 1;
         }
         if ((active0 & 0x8004000L) != 0L || (active1 & 0x800L) != 0L)
            return 1;
         return -1;
      case 2:
         if ((active0 & 0x7fbffe0011fc6400L) != 0L || (active1 & 0xfbe6L) != 0L)
         {
            jjmatchedKind = 80;
            jjmatchedPos = 2;
            return 1;
         }
         if ((active0 & 0x40000000000000L) != 0L || (active1 & 0x400L) != 0L)
            return 1;
         return -1;
      case 3:
         if ((active0 & 0x2804820000000000L) != 0L || (active1 & 0x200L) != 0L)
            return 1;
         if ((active0 & 0x57bb7c0011fc6400L) != 0L || (active1 & 0xf9e6L) != 0L)
         {
            jjmatchedKind = 80;
            jjmatchedPos = 3;
            return 1;
         }
         return -1;
      case 4:
         if ((active0 & 0x229240010000000L) != 0L || (active1 & 0x4800L) != 0L)
            return 1;
         if ((active0 & 0x5592580001fc6400L) != 0L || (active1 & 0xb1e6L) != 0L)
         {
            jjmatchedKind = 80;
            jjmatchedPos = 4;
            return 1;
         }
         return -1;
      case 5:
         if ((active0 & 0x5012080001b46000L) != 0L || (active1 & 0xa0e4L) != 0L)
         {
            jjmatchedKind = 80;
            jjmatchedPos = 5;
            return 1;
         }
         if ((active0 & 0x580500000480400L) != 0L || (active1 & 0x1102L) != 0L)
            return 1;
         return -1;
      case 6:
         if ((active0 & 0x1010080001800000L) != 0L || (active1 & 0x2004L) != 0L)
            return 1;
         if ((active0 & 0x4002000000346000L) != 0L || (active1 & 0x80e0L) != 0L)
         {
            jjmatchedKind = 80;
            jjmatchedPos = 6;
            return 1;
         }
         return -1;
      case 7:
         if ((active0 & 0x4002000000202000L) != 0L || (active1 & 0x20L) != 0L)
            return 1;
         if ((active0 & 0x144000L) != 0L || (active1 & 0x80c0L) != 0L)
         {
            jjmatchedKind = 80;
            jjmatchedPos = 7;
            return 1;
         }
         return -1;
      case 8:
         if ((active0 & 0x44000L) != 0L || (active1 & 0x80c0L) != 0L)
            return 1;
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 80;
            jjmatchedPos = 8;
            return 1;
         }
         return -1;
      case 9:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 80;
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
         return jjStopAtPos(0, 39);
      case 38:
         return jjStopAtPos(0, 32);
      case 40:
         return jjStopAtPos(0, 25);
      case 41:
         return jjStopAtPos(0, 26);
      case 42:
         return jjStopAtPos(0, 37);
      case 43:
         return jjStopAtPos(0, 35);
      case 44:
         return jjStopAtPos(0, 16);
      case 45:
         return jjStopAtPos(0, 36);
      case 47:
         return jjStartNfaWithStates_0(0, 38, 14);
      case 58:
         jjmatchedKind = 15;
         return jjMoveStringLiteralDfa1_0(0x20000L, 0x0L);
      case 59:
         return jjStopAtPos(0, 9);
      case 60:
         jjmatchedKind = 63;
         return jjMoveStringLiteralDfa1_0(0x400000000L, 0x0L);
      case 61:
         return jjStopAtPos(0, 29);
      case 62:
         jjmatchedKind = 64;
         return jjMoveStringLiteralDfa1_0(0x200000000L, 0x0L);
      case 70:
         return jjMoveStringLiteralDfa1_0(0x40000000000L, 0x0L);
      case 79:
         return jjMoveStringLiteralDfa1_0(0x80000000000000L, 0x0L);
      case 84:
         return jjMoveStringLiteralDfa1_0(0x20000000000L, 0x0L);
      case 86:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x8000L);
      case 91:
         return jjStopAtPos(0, 67);
      case 93:
         return jjStopAtPos(0, 68);
      case 94:
         return jjStopAtPos(0, 31);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x40000000002000L, 0x40L);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x10000000000000L, 0x0L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x804000010080000L, 0x2000L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x1000400000000000L, 0x0L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x2000000000000000L, 0x80L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x200001000000L, 0x4000L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x8004000L, 0x800L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x800000000000L, 0x0L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x400L, 0x0L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x100000000000L, 0x0L);
      case 111:
         return jjMoveStringLiteralDfa1_0(0x20000000000000L, 0x500L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0xc00000L, 0x0L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x1020L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x4501000000200000L, 0x2L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x80000100000L, 0x0L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x202000000000000L, 0x0L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x40000L, 0x200L);
      case 119:
         return jjMoveStringLiteralDfa1_0(0x8000000000000L, 0x4L);
      case 123:
         return jjStopAtPos(0, 11);
      case 124:
         return jjStopAtPos(0, 30);
      case 125:
         return jjStopAtPos(0, 12);
      case 126:
         return jjStopAtPos(0, 40);
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
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(1, 17);
         break;
      case 60:
         if ((active0 & 0x400000000L) != 0L)
            return jjStopAtPos(1, 34);
         break;
      case 62:
         if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(1, 33);
         break;
      case 65:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000000000L, active1, 0L);
      case 82:
         return jjMoveStringLiteralDfa2_0(active0, 0x20000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x800100001040000L, active1, 0x9000L);
      case 98:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000000002000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa2_0(active0, 0x28000000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x5000000000000000L, active1, 0x20L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x5000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x4000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000000L, active1, 0L);
      case 110:
         if ((active0 & 0x8000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_0(active0, 0x2242000000004000L, active1, 0x900L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x10c00010000400L, active1, 0x2200L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x900000L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x4L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000000000000L, active1, 0x42L);
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x680000L, active1, 0x400L);
      case 119:
         return jjMoveStringLiteralDfa2_0(active0, 0x400000000000000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0x80L);
      case 121:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000000000L, active1, 0L);
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
         return jjMoveStringLiteralDfa3_0(active0, 0x40000000000L, active1, 0L);
      case 85:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000000000000L, active1, 0x20L);
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000L, active1, 0x80L);
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x400L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x100L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000000000000L, active1, 0L);
      case 104:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x600000000800000L, active1, 0x1200L);
      case 106:
         return jjMoveStringLiteralDfa3_0(active0, 0x80000000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000L, active1, 0x8000L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x800010000000L, active1, 0x2000L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x11200000000000L, active1, 0x800L);
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x80000200000L, active1, 0L);
      case 113:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000000000000000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000000000000L, active1, 0x2L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x802000000082000L, active1, 0L);
      case 116:
         if ((active1 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(2, 74, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x20100000004000L, active1, 0x44L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000400000100000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x4000L);
      case 121:
         if ((active0 & 0x40000000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 54, 1);
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
         if ((active0 & 0x20000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 41, 1);
         break;
      case 83:
         return jjMoveStringLiteralDfa4_0(active0, 0x40000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x1008200000000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000000L, active1, 0L);
      case 100:
         if ((active1 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(3, 73, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x20L);
      case 101:
         if ((active0 & 0x800000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 59, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0xa0080000004000L, active1, 0x4080L);
      case 103:
         if ((active0 & 0x800000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 47, 1);
         break;
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x2100000000000L, active1, 0x2L);
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000000400000L, active1, 0L);
      case 109:
         if ((active0 & 0x2000000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 61, 1);
         break;
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x100000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000000000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000L, active1, 0L);
      case 114:
         if ((active0 & 0x4000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 50, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000000000L, active1, 0x44L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000000L, active1, 0x1000L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000001082000L, active1, 0x2000L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x4100000000040400L, active1, 0x8800L);
      case 118:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000L, active1, 0L);
      case 119:
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x100L);
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
         if ((active0 & 0x40000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 42, 1);
         break;
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x800000L, active1, 0x100L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x580000000100000L, active1, 0L);
      case 100:
         if ((active1 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(4, 78, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0x80000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x4010000000040000L, active1, 0xb000L);
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000L, active1, 0x44L);
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000400L, active1, 0L);
      case 110:
         if ((active0 & 0x200000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 57, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x2L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x1280000L, active1, 0x20L);
      case 112:
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x80L);
      case 114:
         if ((active0 & 0x8000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 51, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0x6000L, active1, 0L);
      case 116:
         if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_0(4, 28, 1);
         else if ((active0 & 0x200000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 45, 1);
         else if ((active0 & 0x1000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 48, 1);
         else if ((active0 & 0x20000000000000L) != 0L)
            return jjStartNfaWithStates_0(4, 53, 1);
         else if ((active1 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(4, 75, 1);
         break;
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000000000000L, active1, 0L);
      case 118:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000000000L, active1, 0L);
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
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x8000L);
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x10000000102000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x40L);
      case 99:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(5, 22, 1);
         break;
      case 101:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(5, 10, 1);
         else if ((active0 & 0x100000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 44, 1);
         else if ((active0 & 0x400000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 46, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x80000000000L, active1, 0L);
      case 102:
         return jjMoveStringLiteralDfa6_0(active0, 0x4000L, active1, 0L);
      case 103:
         if ((active1 & 0x2L) != 0L)
            return jjStartNfaWithStates_0(5, 65, 1);
         break;
      case 104:
         if ((active0 & 0x400000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 58, 1);
         break;
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000000000000000L, active1, 0L);
      case 109:
         if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(5, 19, 1);
         break;
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x4002000000000000L, active1, 0x24L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x1200000L, active1, 0L);
      case 115:
         if ((active1 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(5, 76, 1);
         break;
      case 116:
         if ((active0 & 0x80000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 55, 1);
         else if ((active0 & 0x100000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 56, 1);
         return jjMoveStringLiteralDfa6_0(active0, 0x840000L, active1, 0x80L);
      case 120:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x2000L);
      case 121:
         if ((active1 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(5, 72, 1);
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
         return jjMoveStringLiteralDfa7_0(active0, 0x4000L, active1, 0x8000L);
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x4000000000002000L, active1, 0L);
      case 101:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(6, 23, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x2000000000000L, active1, 0L);
      case 102:
         if ((active0 & 0x80000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 43, 1);
         break;
      case 103:
         if ((active1 & 0x4L) != 0L)
            return jjStartNfaWithStates_0(6, 66, 1);
         break;
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x80L);
      case 108:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x20L);
      case 110:
         if ((active0 & 0x10000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 52, 1);
         break;
      case 116:
         if ((active0 & 0x1000000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 60, 1);
         else if ((active1 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(6, 77, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x300000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x40L);
      case 121:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(6, 24, 1);
         return jjMoveStringLiteralDfa7_0(active0, 0x40000L, active1, 0L);
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
         return jjMoveStringLiteralDfa8_0(active0, 0x100000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x4000L, active1, 0L);
      case 100:
         if ((active0 & 0x2000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 49, 1);
         break;
      case 101:
         if ((active0 & 0x4000000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 62, 1);
         break;
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x80L);
      case 112:
         return jjMoveStringLiteralDfa8_0(active0, 0x40000L, active1, 0L);
      case 115:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(7, 21, 1);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x8000L);
      case 116:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(7, 13, 1);
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x40L);
      case 121:
         if ((active1 & 0x20L) != 0L)
            return jjStartNfaWithStates_0(7, 69, 1);
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
         return jjMoveStringLiteralDfa9_0(active0, 0x100000L, active1, 0L);
      case 101:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(8, 14, 1);
         else if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(8, 18, 1);
         else if ((active1 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(8, 70, 1);
         else if ((active1 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(8, 79, 1);
         break;
      case 110:
         if ((active1 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(8, 71, 1);
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
         return jjMoveStringLiteralDfa10_0(active0, 0x100000L);
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
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(10, 20, 1);
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
   jjnewStateCnt = 120;
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
                     jjCheckNAddStates(0, 9);
                  else if (curChar == 34)
                     jjCheckNAddStates(10, 15);
                  else if (curChar == 39)
                     jjCheckNAddStates(16, 19);
                  else if (curChar == 46)
                     jjCheckNAddTwoStates(42, 47);
                  else if (curChar == 35)
                     jjCheckNAddStates(20, 23);
                  else if (curChar == 47)
                     jjAddStates(24, 25);
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 82)
                        kind = 82;
                     jjCheckNAddTwoStates(6, 7);
                  }
                  else if (curChar == 48)
                     jjAddStates(26, 27);
                  if (curChar == 48)
                  {
                     if (kind > 81)
                        kind = 81;
                     jjCheckNAddTwoStates(3, 4);
                  }
                  break;
               case 14:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(18, 19);
                  else if (curChar == 47)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 80)
                     kind = 80;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 2:
                  if (curChar != 48)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAddTwoStates(3, 4);
                  break;
               case 3:
                  if ((0xff000000000000L & l) == 0L)
                     break;
                  if (kind > 81)
                     kind = 81;
                  jjCheckNAddTwoStates(3, 4);
                  break;
               case 5:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 82)
                     kind = 82;
                  jjCheckNAddTwoStates(6, 7);
                  break;
               case 6:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 82)
                     kind = 82;
                  jjCheckNAddTwoStates(6, 7);
                  break;
               case 8:
                  if (curChar == 48)
                     jjAddStates(26, 27);
                  break;
               case 10:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 83)
                     kind = 83;
                  jjAddStates(28, 29);
                  break;
               case 13:
                  if (curChar == 47)
                     jjAddStates(24, 25);
                  break;
               case 15:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     jjCheckNAddTwoStates(15, 16);
                  break;
               case 16:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 17:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(18, 19);
                  break;
               case 18:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(18, 19);
                  break;
               case 19:
                  if (curChar == 42)
                     jjAddStates(30, 31);
                  break;
               case 20:
                  if ((0xffff7fffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(21, 19);
                  break;
               case 21:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(21, 19);
                  break;
               case 22:
                  if (curChar == 47 && kind > 6)
                     kind = 6;
                  break;
               case 23:
                  if (curChar == 35)
                     jjCheckNAddStates(20, 23);
                  break;
               case 24:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(24, 25);
                  break;
               case 25:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(32, 36);
                  break;
               case 26:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(37, 40);
                  break;
               case 27:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(27, 28);
                  break;
               case 28:
                  if (curChar == 34)
                     jjCheckNAdd(29);
                  break;
               case 29:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(29, 30);
                  break;
               case 30:
                  if (curChar == 34)
                     jjCheckNAddStates(41, 43);
                  break;
               case 31:
                  if (curChar == 10 && kind > 7)
                     kind = 7;
                  break;
               case 32:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(44, 47);
                  break;
               case 33:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(48, 50);
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(34, 31);
                  break;
               case 35:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(51, 55);
                  break;
               case 36:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(36, 37);
                  break;
               case 37:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddStates(56, 59);
                  break;
               case 38:
                  if ((0x100000200L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddStates(60, 62);
                  break;
               case 39:
                  if ((0x53ffc00500000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(39, 40);
                  break;
               case 40:
                  if (curChar != 10)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(40);
                  break;
               case 41:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(42, 47);
                  break;
               case 42:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAddStates(63, 65);
                  break;
               case 44:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(45);
                  break;
               case 45:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAddTwoStates(45, 46);
                  break;
               case 47:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(47, 48);
                  break;
               case 49:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(0, 9);
                  break;
               case 50:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 51:
                  if (curChar != 46)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAddStates(66, 68);
                  break;
               case 52:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 84)
                     kind = 84;
                  jjCheckNAddStates(66, 68);
                  break;
               case 53:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(53, 54);
                  break;
               case 54:
                  if (curChar == 46)
                     jjCheckNAdd(42);
                  break;
               case 55:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 57:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(58);
                  break;
               case 58:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 85)
                     kind = 85;
                  jjCheckNAddTwoStates(58, 59);
                  break;
               case 60:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 61:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(62, 48);
                  break;
               case 62:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(62, 48);
                  break;
               case 63:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(63, 64);
                  break;
               case 64:
                  if (curChar == 46)
                     jjCheckNAdd(47);
                  break;
               case 65:
                  if (curChar == 39)
                     jjCheckNAddStates(16, 19);
                  break;
               case 66:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(67);
                  break;
               case 67:
                  if (curChar == 39 && kind > 86)
                     kind = 86;
                  break;
               case 69:
                  if ((0x8000008400000000L & l) != 0L)
                     jjCheckNAdd(67);
                  break;
               case 70:
                  if (curChar == 48)
                     jjCheckNAddTwoStates(71, 67);
                  break;
               case 71:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(71, 67);
                  break;
               case 72:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(73, 67);
                  break;
               case 73:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(73, 67);
                  break;
               case 74:
                  if (curChar == 48)
                     jjAddStates(69, 70);
                  break;
               case 76:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(76, 67);
                  break;
               case 78:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAdd(79);
                  break;
               case 79:
                  if (curChar == 39 && kind > 87)
                     kind = 87;
                  break;
               case 81:
                  if ((0x8000008400000000L & l) != 0L)
                     jjCheckNAdd(79);
                  break;
               case 82:
                  if (curChar == 48)
                     jjCheckNAddTwoStates(83, 79);
                  break;
               case 83:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(83, 79);
                  break;
               case 84:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(85, 79);
                  break;
               case 85:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(85, 79);
                  break;
               case 87:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(87, 79);
                  break;
               case 88:
                  if (curChar == 48)
                     jjAddStates(71, 72);
                  break;
               case 90:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(90, 79);
                  break;
               case 93:
                  if (curChar == 39)
                     jjCheckNAddTwoStates(78, 80);
                  break;
               case 94:
                  if (curChar == 34)
                     jjCheckNAddStates(73, 75);
                  break;
               case 95:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(73, 75);
                  break;
               case 97:
                  if ((0x8000008400000000L & l) != 0L)
                     jjCheckNAddStates(73, 75);
                  break;
               case 98:
                  if (curChar == 34 && kind > 89)
                     kind = 89;
                  break;
               case 99:
                  if (curChar == 48)
                     jjCheckNAddStates(76, 79);
                  break;
               case 100:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(76, 79);
                  break;
               case 101:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAddStates(80, 83);
                  break;
               case 102:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(80, 83);
                  break;
               case 103:
                  if (curChar == 48)
                     jjAddStates(84, 85);
                  break;
               case 105:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(86, 89);
                  break;
               case 107:
                  if (curChar == 34)
                     jjCheckNAddStates(10, 15);
                  break;
               case 108:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(90, 92);
                  break;
               case 110:
                  if ((0x8000008400000000L & l) != 0L)
                     jjCheckNAddStates(90, 92);
                  break;
               case 111:
                  if (curChar == 34 && kind > 88)
                     kind = 88;
                  break;
               case 112:
                  if (curChar == 48)
                     jjCheckNAddStates(93, 96);
                  break;
               case 113:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(93, 96);
                  break;
               case 114:
                  if ((0x3fe000000000000L & l) != 0L)
                     jjCheckNAddStates(97, 100);
                  break;
               case 115:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(97, 100);
                  break;
               case 116:
                  if (curChar == 48)
                     jjAddStates(101, 102);
                  break;
               case 118:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(103, 106);
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
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 80)
                        kind = 80;
                     jjCheckNAdd(1);
                  }
                  if (curChar == 76)
                     jjAddStates(107, 108);
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 80)
                     kind = 80;
                  jjCheckNAdd(1);
                  break;
               case 4:
                  if ((0x20100000201000L & l) != 0L && kind > 81)
                     kind = 81;
                  break;
               case 7:
                  if ((0x20100000201000L & l) != 0L && kind > 82)
                     kind = 82;
                  break;
               case 9:
                  if (curChar == 120)
                     jjCheckNAdd(10);
                  break;
               case 10:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 83)
                     kind = 83;
                  jjCheckNAddTwoStates(10, 11);
                  break;
               case 11:
                  if ((0x20100000201000L & l) != 0L && kind > 83)
                     kind = 83;
                  break;
               case 12:
                  if (curChar == 88)
                     jjCheckNAdd(10);
                  break;
               case 15:
                  jjAddStates(109, 110);
                  break;
               case 18:
                  jjCheckNAddTwoStates(18, 19);
                  break;
               case 20:
               case 21:
                  jjCheckNAddTwoStates(21, 19);
                  break;
               case 26:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjAddStates(37, 40);
                  break;
               case 29:
                  jjAddStates(111, 112);
                  break;
               case 37:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddStates(56, 59);
                  break;
               case 39:
                  if ((0x7fffffe97fffffeL & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddTwoStates(39, 40);
                  break;
               case 43:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(113, 114);
                  break;
               case 46:
                  if ((0x104000001040L & l) != 0L && kind > 84)
                     kind = 84;
                  break;
               case 48:
                  if ((0x1000000010L & l) != 0L && kind > 90)
                     kind = 90;
                  break;
               case 56:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(115, 116);
                  break;
               case 59:
                  if ((0x104000001040L & l) != 0L && kind > 85)
                     kind = 85;
                  break;
               case 66:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(67);
                  break;
               case 68:
                  if (curChar == 92)
                     jjAddStates(117, 120);
                  break;
               case 69:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(67);
                  break;
               case 75:
                  if (curChar == 120)
                     jjCheckNAdd(76);
                  break;
               case 76:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddTwoStates(76, 67);
                  break;
               case 77:
                  if (curChar == 88)
                     jjCheckNAdd(76);
                  break;
               case 78:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAdd(79);
                  break;
               case 80:
                  if (curChar == 92)
                     jjAddStates(121, 125);
                  break;
               case 81:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAdd(79);
                  break;
               case 86:
                  if ((0x20000000200000L & l) != 0L)
                     jjCheckNAdd(87);
                  break;
               case 87:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddTwoStates(87, 79);
                  break;
               case 89:
                  if (curChar == 120)
                     jjCheckNAdd(90);
                  break;
               case 90:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddTwoStates(90, 79);
                  break;
               case 91:
                  if (curChar == 88)
                     jjCheckNAdd(90);
                  break;
               case 92:
                  if (curChar == 76)
                     jjAddStates(107, 108);
                  break;
               case 95:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(73, 75);
                  break;
               case 96:
                  if (curChar == 92)
                     jjAddStates(126, 129);
                  break;
               case 97:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(73, 75);
                  break;
               case 104:
                  if (curChar == 120)
                     jjCheckNAdd(105);
                  break;
               case 105:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(86, 89);
                  break;
               case 106:
                  if (curChar == 88)
                     jjCheckNAdd(105);
                  break;
               case 108:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(90, 92);
                  break;
               case 109:
                  if (curChar == 92)
                     jjAddStates(130, 133);
                  break;
               case 110:
                  if ((0x54404610000000L & l) != 0L)
                     jjCheckNAddStates(90, 92);
                  break;
               case 117:
                  if (curChar == 120)
                     jjCheckNAdd(118);
                  break;
               case 118:
                  if ((0x7e0000007eL & l) != 0L)
                     jjCheckNAddStates(103, 106);
                  break;
               case 119:
                  if (curChar == 88)
                     jjCheckNAdd(118);
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
               case 15:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(109, 110);
                  break;
               case 18:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(18, 19);
                  break;
               case 20:
               case 21:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(21, 19);
                  break;
               case 29:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(111, 112);
                  break;
               case 66:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 67;
                  break;
               case 78:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjstateSet[jjnewStateCnt++] = 79;
                  break;
               case 95:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(73, 75);
                  break;
               case 108:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(90, 92);
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
      if ((i = jjnewStateCnt) == (startsAt = 120 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   50, 51, 53, 54, 55, 56, 60, 61, 63, 64, 108, 109, 111, 95, 96, 98, 
   66, 68, 78, 80, 24, 25, 36, 37, 14, 17, 9, 12, 10, 11, 20, 22, 
   25, 26, 27, 28, 31, 26, 27, 28, 31, 31, 32, 35, 33, 34, 31, 32, 
   33, 34, 31, 33, 34, 31, 32, 35, 37, 38, 39, 40, 38, 39, 40, 42, 
   43, 46, 52, 43, 46, 75, 77, 89, 91, 95, 96, 98, 95, 96, 100, 98, 
   95, 96, 102, 98, 104, 106, 95, 96, 105, 98, 108, 109, 111, 108, 109, 113, 
   111, 108, 109, 115, 111, 117, 119, 108, 109, 118, 111, 93, 94, 15, 16, 29, 
   30, 44, 45, 57, 58, 69, 70, 72, 74, 81, 82, 84, 86, 88, 97, 99, 
   101, 103, 110, 112, 114, 116, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, "\73", 
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
   0xfffffffffffffe01L, 0x7ffffffL, 
};
static final long[] jjtoSkip = {
   0x1feL, 0x0L, 
};
private ASCII_CharStream input_stream;
private final int[] jjrounds = new int[120];
private final int[] jjstateSet = new int[240];
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
   for (i = 120; i-- > 0;)
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

   try { 
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

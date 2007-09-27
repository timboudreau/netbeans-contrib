/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.latex.ui.viewer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.latex.model.platform.FilePosition;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class DVIParser {

    private RandomAccessFile source;
    private static Map<Integer, Instruction> code2Instruction;

    static {
        code2Instruction = new HashMap();

        for (int cntr = 0; cntr < 128; cntr++) {
            code2Instruction.put(cntr, new StaticLengthInstruction(0));
        }

        add(128, 1, 4);
        code2Instruction.put(132, new StaticLengthInstruction(8));
        add(133, 1, 4);
        code2Instruction.put(137, new StaticLengthInstruction(8));
        code2Instruction.put(138, new StaticLengthInstruction(0));
        code2Instruction.put(139, new BOPInstruction());
        code2Instruction.put(140, new EOPInstruction());
        code2Instruction.put(141, new StaticLengthInstruction(0));
        code2Instruction.put(142, new StaticLengthInstruction(0));
        add(143, 1, 4);
        add(147, 0, 4);
        add(152, 0, 4);
        add(157, 1, 4);
        add(161, 0, 4);
        add(166, 0, 4);
        add(235, 1, 4);
        code2Instruction.put(239, new SpecialInstruction(1));
        code2Instruction.put(240, new SpecialInstruction(2));
        code2Instruction.put(241, new SpecialInstruction(3));
        code2Instruction.put(242, new SpecialInstruction(4));
        code2Instruction.put(243, new FontDefInstruction(1));
        code2Instruction.put(244, new FontDefInstruction(2));
        code2Instruction.put(245, new FontDefInstruction(3));
        code2Instruction.put(246, new FontDefInstruction(4));
        code2Instruction.put(247, new PreInstruction());
        code2Instruction.put(248, new PostInstruction());

        for (int cntr = 171; cntr <= 234; cntr++) {
            code2Instruction.put(cntr, new StaticLengthInstruction(0));
        }
    }

    private static void add(int codeStart, int bytesStart, int bytesEnd) {
        for (int cntr = bytesStart; cntr <= bytesEnd; cntr++) {
            code2Instruction.put(codeStart + cntr - bytesStart, new StaticLengthInstruction(cntr));
        }
    }

    /** Creates a new instance of DVIParser */
    public DVIParser() {
    }

    public List<DVIPageDescription> parse(File file) throws IOException {
        source = new RandomAccessFile(file, "r");

        boolean finished = false;
        List<DVIPageDescription> result = new ArrayList();
        List<FilePosition> positions = null;
        int pageNumber = 1;
        FileObject fileObject = FileUtil.toFileObject(file);

        while (!finished) {
            int instructionCode = source.readUnsignedByte();

            Instruction instruction = code2Instruction.get(instructionCode);

            if (instruction == null) {
                throw new IOException("Unknown code: " + instructionCode);
            }

            if (instruction instanceof BOPInstruction) {
                positions = new ArrayList<FilePosition>();
//                System.err.println("BOP: " + ((BOPInstruction) instruction).getRealPageNumber());
            }
//            System.err.println("o:" + instructionCode);

            if (instruction instanceof SpecialInstruction) {
                FilePosition position = ((SpecialInstruction) instruction).getPosition(fileObject);

                if (position != null) {
                    positions.add(position);
                }
            }

            if (instruction instanceof EOPInstruction) {
                DVIPageDescription page = new DVIPageDescription(pageNumber++, positions);

                result.add(page);
            }

            instruction.skip(source);

            finished = instruction instanceof PostInstruction;
        }

        return result;
    }

    private static abstract class Instruction {
        public abstract void skip(RandomAccessFile source) throws IOException;
    }

    private static class StaticLengthInstruction extends Instruction {

        private int length;

        public StaticLengthInstruction(int length) {
            this.length = length;
        }

        public void skip(RandomAccessFile source) throws IOException {
            if (source.skipBytes(length) != length) {
                throw new IOException("length=" + length);
            }
        }
    }

    private static class EOPInstruction extends StaticLengthInstruction {

        public EOPInstruction() {
            super(0);
        }

        public void skip(RandomAccessFile source) throws IOException {
            super.skip(source);
//            System.err.println("EOP");
        }

    }

    private static class BOPInstruction extends Instruction {

        private int realPageNumber;

        public BOPInstruction() {
            super();
        }

        public void skip(RandomAccessFile source) throws IOException {
//            for (int cntr = 0; cntr < 10; cntr++) {
//                System.err.println("count[" + cntr + "]=" + readInt(source, 4));
//            }

            source.skipBytes(44);
//            System.err.println("BOP");
        }

        public int getRealPageNumber() {
            return realPageNumber;
        }
    }

    private static class SpecialInstruction extends Instruction {
        private int length;
        private int line;
        private String fileName;

        private FilePosition position;

        public SpecialInstruction(int length) {
            this.length = length;
        }

        public void skip(RandomAccessFile source) throws IOException {
            fileName = null;

            int toRead = readInt(source, length);
            byte[] b = new byte[toRead];

            source.read(b);

            String specialText = new String(b);
            Object[] result = null;

            for (int cntr = 0; cntr < f.length; cntr++) {
                ParsePosition pos = new ParsePosition(0);

                result = f[cntr].parse(specialText, pos);

                if (pos.getIndex() != 0) {
                    break;
                }
            }

            if (result != null) {
                line = ((Long) result[0]).intValue();
                fileName = (String) result[1];
            }
        }

        public FilePosition getPosition(FileObject relativeTo) {
            if (fileName == null)
                return null;
            
            FileObject parent = relativeTo.getParent();

            return new FilePosition(parent.getFileObject(fileName), line, 0);
        }

        private static final MessageFormat[] f = new MessageFormat[] {
            new MessageFormat("src:{1}:{0,number}"),
            new MessageFormat("src:{0,number} {1}"),
            new MessageFormat("src:{0,number} *{1}"),
            new MessageFormat("src:{0,number}{1}"),
        };

    }

    private static class FontDefInstruction extends Instruction {
        private int length;

        public FontDefInstruction(int length) {
            this.length = length;
        }

        public void skip(RandomAccessFile source) throws IOException {
            source.skipBytes(12 + length);
            int toRead = readInt(source, 1);
            source.skipBytes(1);
            source.skipBytes(toRead + 1);
        }

    }

    private static class PreInstruction extends Instruction {

        public PreInstruction() {
        }

        public void skip(RandomAccessFile source) throws IOException {
            source.skipBytes(13);
            int toRead = readInt(source, 1);
            source.skipBytes(toRead);
        }

    }

    private static class PostInstruction extends StaticLengthInstruction {

        public PostInstruction() {
            super(28);
        }

    }

    private static int readInt(RandomAccessFile file, int length) throws IOException {
        int result = 0;

        while (length-- > 0) {
            result = (result << 8) + file.readUnsignedByte();
        }

        return result;
    }
}

/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;

/**
 *
 * @author Jan Lahoda
 */
public class TrieDictionary implements Dictionary {

    private byte[] array;
    private ByteBuffer buffer;

    /** Creates a new instance of TrieDictionary */
    private TrieDictionary(byte[] array) {
        this.array = array;
        this.buffer = null;
    }

    private TrieDictionary(File data) throws IOException {
        this.array = null;

        FileInputStream ins = new FileInputStream(data);
        FileChannel channel = ins.getChannel();
        
        try {
            this.buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
        } finally {
            channel.close();
            ins.close();
        }
    }

    public ValidityType validateWord(CharSequence word) {
        String wordString = word.toString();
        ValidityType type = validateWordImpl(wordString.toLowerCase());

        if (type == ValidityType.INVALID) {
            type = validateWordImpl(wordString);
        }

        return type;
    }

    private ValidityType validateWordImpl(CharSequence word) {
        int node = findNode(word, 0, 4);

        if (node == (-1))
            return ValidityType.INVALID;

        if (readByte(node) == 0x01) {
            return ValidityType.VALID;
        }

        return ValidityType.PREFIX_OF_VALID;
    }

    public List<String> findValidWordsForPrefix(CharSequence word) {
        List<String> result = new ArrayList<String>();
        int node = findNode(word, 0, 4);
        
        if (node == (-1))
            return Collections.emptyList();
        
        return findValidWordsForPrefix(new StringBuffer(word), node, result);
    }

    public List<String> findProposals(CharSequence pattern) {
        List<String> result = new ArrayList<String>();
        
        return findProposals(pattern, 2, 4, new StringBuffer(), result);
    }
    
    private List<String> findProposals(CharSequence pattern, int maxDistance, int node, StringBuffer word, List<String> result) {
        int entries = readInt(node + 1);
        
        for (int currentEntry = 0; currentEntry < entries; currentEntry++) {
            char ac = readChar(node + 5 + currentEntry * 6);
            
            word.append(ac);
            
            int distance = distance(pattern, word);
            int targetNode = node + readInt(node + 5 + currentEntry * 6 + 2);
            
            if (distance < maxDistance) {
                if (readByte(targetNode) == 0x01) {
                    result.add(word.toString());
                }
            }
                
            if ((distance - (pattern.length() - word.length())) < maxDistance) {
                findProposals(pattern, maxDistance, targetNode, word, result);
            }
            
            word.deleteCharAt(word.length() - 1);
        }
        
        return result;
    }

    private List<String> findValidWordsForPrefix(StringBuffer foundSoFar, int node, List<String> result) {
        int entries = readInt(node + 1);
        
        for (int currentEntry = 0; currentEntry < entries; currentEntry++) {
            char ac = readChar(node + 5 + currentEntry * 6);
            
            foundSoFar.append(ac);
            
            int targetNode = node + readInt(node + 5 + currentEntry * 6 + 2);
            
            if (readByte(targetNode) == 0x01) {
                result.add(foundSoFar.toString());
            }
                
            findValidWordsForPrefix(foundSoFar, targetNode, result);
            
            foundSoFar.deleteCharAt(foundSoFar.length() - 1);
        }
        
        return result;
    }
    
    private int findNode(CharSequence word, int currentCharOffset, int currentNode) {
        if (word.length() <= currentCharOffset)
            return currentNode;

        char c = word.charAt(currentCharOffset);
        int entries = readInt(currentNode + 1);

        for (int currentEntry = 0; currentEntry < entries; currentEntry++) {
            char ac = readChar(currentNode + 5 + currentEntry * 6);

            if (ac == c) {
                int newNodeOffset = readInt(currentNode + 5 + currentEntry * 6 + 2);

                int newNode = currentNode + newNodeOffset;

                return findNode(word, currentCharOffset + 1, newNode);
            }
        }

        return -1;
    }

    private static final int CURRENT_TRIE_DICTIONARY_VERSION = 1;

    private static final File CACHE_DIR = new File(new File(new File(System.getProperty("netbeans.user"), "var"), "cache"), "dict");

    public static Dictionary getDictionary(String suffix, List<URL> sources) throws IOException {
        File trie = new File(CACHE_DIR, "dictionary" + suffix + ".trie" + CURRENT_TRIE_DICTIONARY_VERSION);

        if (!trie.exists()) {
            byte[] array = constructTrie(sources);

            trie.getParentFile().mkdirs();

            OutputStream out = new FileOutputStream(trie);

            try {
                out.write(array);
            } finally {
                out.close();
            }
        }

        return new TrieDictionary(trie);
    }

    private static int toUnsigned(byte b) {
        if (b < 0) {
            return 256 + b;
        }

        return b;
    }

    private int readInt(int pos) {
        return (toUnsigned(readByte(pos + 0)) << 24) + (toUnsigned(readByte(pos + 1)) << 16) + (toUnsigned(readByte(pos + 2)) << 8) + toUnsigned(readByte(pos + 3));
    }

    private char readChar(int pos) {
        return (char) ((readByte(pos + 0) << 8) + readByte(pos + 1));
    }

    private byte readByte(int pos) {
        if (buffer != null) {
            return buffer.get(pos);
        } else {
            return array[pos];
        }
    }

    private static boolean compareChars(char c1, char c2) {
        return c1 == c2 || Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }
    
    private static int distance(CharSequence pattern, CharSequence word) {
        int[] old = new int[pattern.length() + 1];
        int[] current = new int[pattern.length() + 1];
        int[] oldLength = new int[pattern.length() + 1];
        int[] length = new int[pattern.length() + 1];
        
        for (int cntr = 0; cntr < old.length; cntr++) {
            old[cntr] = pattern.length() + 1;//cntr;
            oldLength[cntr] = (-1);
        }
        
        current[0] = old[0] = oldLength[0] = length[0] = 0;
        
        int currentIndex = 0;
        
        while (currentIndex < word.length()) {
            for (int cntr = 0; cntr < pattern.length(); cntr++) {
                int insert = old[cntr + 1] + 1;
                int delete = current[cntr] + 1;
                int replace = old[cntr] + (compareChars(pattern.charAt(cntr), word.charAt(currentIndex)) ? 0 : 1);
                
                if (insert < delete) {
                    if (insert < replace) {
                        current[cntr + 1] = insert;
                        length[cntr + 1] = oldLength[cntr + 1] + 1;
                    } else {
                        current[cntr + 1] = replace;
                        length[cntr + 1] = oldLength[cntr] + 1;
                    }
                } else {
                    if (delete < replace) {
                        current[cntr + 1] = delete;
                        length[cntr + 1] = length[cntr];
                    } else {
                        current[cntr + 1] = replace;
                        length[cntr + 1] = oldLength[cntr] + 1;
                    }
                }
            }
            
            currentIndex++;
            
            int[] temp = old;
            
            old = current;
            current = temp;
            
            temp = oldLength;
            
            oldLength = length;
            length = temp;
        }
        
        return old[pattern.length()];
    }
    
    public static byte[] constructTrie(List<URL> sources) throws IOException {
        SortedSet<String> data = new TreeSet<String>();

        for (URL u : sources) {
            BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream(), "UTF-8"));
            
            try {
                String line;
                
                while ((line = in.readLine()) != null) {
                    data.add(line);
                }
            } finally {
                //TODO: wrap in try - catch:
                in.close();
            }
        }
        
        return constructTrieData(data);
    }
    
    public static byte[] constructTrieData(SortedSet<String> data) throws IOException {
        ByteArray array = new ByteArray();

        array.put(0, CURRENT_TRIE_DICTIONARY_VERSION);

        int length = encodeOneLayer(array, 4, 0, data);
        byte[] result = new byte[length];
        
        System.arraycopy(array.data, 0, result, 0, length);
        
        return result;
    }

    public static TrieDictionary constructTrie(SortedSet<String> data) throws IOException {
        return new TrieDictionary(constructTrieData(data));
    }
    
    private static int encodeOneLayer(ByteArray array, int currentPointer, int currentChar, SortedSet<String> data) {
        Map<Character, SortedSet<String>> char2Words = new TreeMap();
        boolean representsFullWord = !data.isEmpty() && data.first().length() <= currentChar;
        Iterator<String> dataIt = data.iterator();

        if (representsFullWord) {
            dataIt.next();
        }

        while (dataIt.hasNext()) {
            String word = dataIt.next();
            char c = word.charAt(currentChar);
            SortedSet<String> words = char2Words.get(c);

            if (words == null) {
                char2Words.put(c, words = new TreeSet<String>());
            }

            words.add(word);
        }

        int entries = char2Words.size();

        //write flags:
        byte flags = 0x00;

        if (representsFullWord) {
            flags = 0x01;
        }

        array.put(currentPointer, flags);
        array.put(currentPointer + 1, entries);

        int currentEntry = 0;
        int childPointer = currentPointer + 5 + entries * 6;

        for (Entry<Character, SortedSet<String>> e : char2Words.entrySet()) {
            array.put(currentPointer + 5 + currentEntry * 6, e.getKey());
            array.put(currentPointer + 5 + currentEntry * 6 + 2, childPointer - currentPointer);

            childPointer = encodeOneLayer(array, childPointer, currentChar + 1, e.getValue());

            currentEntry++;
        }

        return childPointer;
    }

    private static class ByteArray {

        byte[] data = new byte[0];

        private void assureCapacity(int size) {
            if (data.length < size) {
                byte[] newData = new byte[size + 1024 * 1024];

                System.arraycopy(data, 0, newData, 0, data.length);

                data = newData;
            }
        }
        public void put(int pos, char what) {
            assureCapacity(pos + 2);

            int whatInt = what;

            data[pos] = (byte) (whatInt >> 8);
            data[pos + 1] = (byte) (whatInt & 0xFF);
        }

        public void put(int pos, byte what) {
            assureCapacity(pos + 1);

            data[pos] = what;
        }

        public void put(int pos, int what) {
            assureCapacity(pos + 4);
            
            data[pos + 0] = (byte) ((what >> 24) & 0xFF);
            data[pos + 1] = (byte) ((what >> 16) & 0xFF);
            data[pos + 2] = (byte) ((what >>  8) & 0xFF);
            data[pos + 3] = (byte) ((what >>  0) & 0xFF);
        }
    }
}

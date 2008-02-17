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
 */
package org.netbeans.modules.erlang.platform.node;

import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpConnection;
import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpNode;
import com.ericsson.otp.erlang.OtpPeer;
import com.ericsson.otp.erlang.OtpSelf;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ParseException;
import org.netbeans.modules.erlang.platform.api.RubyInstallation;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlyBirdNode {
    private static OtpNode erlyBirdNode;
    private static OtpSelf erlyBirdSelf;
    private static OtpPeer erlyBirdBackEndPeer;
    private static OtpConnection connectionToBackEndPeer;
    
    private static final String ERLYBIRD_NODE_NAME = "jerlybird";
    private static final String ERLYBIRD_BACKEND_NODE_NAME = "erlybirdback";
    private static final String COOKIE = "erlybirdcool";
    private static Process erlyBirdBackEndNodeProcess;
    
    private ErlyBirdNode() {
    }
    
    private static OtpNode getNode() {
        if (erlyBirdNode == null) {
            try {
                erlyBirdNode = new OtpNode(ERLYBIRD_NODE_NAME);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return erlyBirdNode;
    }
    
    private static OtpSelf getSelf() {
        if (erlyBirdSelf == null) {
            try {
                erlyBirdSelf = new OtpSelf(ERLYBIRD_NODE_NAME, COOKIE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return erlyBirdSelf;
    }
    
    private static OtpPeer getBackEndPeer() {
        if (erlyBirdBackEndNodeProcess == null) {
            launchErlyBirdBackEndNode();
        }
        
        if (erlyBirdBackEndPeer == null) {
            erlyBirdBackEndPeer = new OtpPeer(ERLYBIRD_BACKEND_NODE_NAME + "@" + getLocalHostName());
        }
        
        return erlyBirdBackEndPeer;
    }
    
    private static void launchErlyBirdBackEndNode() {
        if (erlyBirdBackEndNodeProcess != null) {
            return;
        }
        
        File exeFile = new File(RubyInstallation.getInstance().getRuby());
        if (! exeFile.exists()) {
            /** erl installation path is not set properly */
            return;
        }
        
        try {
            /** It seems ProcessBuilder cannot handle argv properly, so we use Runtime directly */
            StringBuilder command = new StringBuilder()
                    .append(exeFile.getAbsolutePath())
                    .append(" -noshell")
                    .append(" -sname ").append(ERLYBIRD_BACKEND_NODE_NAME)
                    .append(" -setcookie ").append(COOKIE);
            erlyBirdBackEndNodeProcess = Runtime.getRuntime().exec(command.toString());
            
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        } finally {
        }
    }
    
    public static void stopErlyBirdBackEndNode() {
        if (erlyBirdBackEndNodeProcess != null) {
            erlyBirdBackEndNodeProcess.destroy();
        }
    }
    
    private static OtpConnection getConnection() {
        boolean willReConnect = false;
        if (connectionToBackEndPeer == null) {
            willReConnect = true;
        } else {
            if (! connectionToBackEndPeer.isConnected()) {
                connectionToBackEndPeer.close();
                connectionToBackEndPeer = null;
                willReConnect = true;
            }
        }
        
        if (willReConnect) {
            final int MAX_TRY_TIMES = 20;
            for (int i = 0; i < MAX_TRY_TIMES; i++) {
                try {
                    connectionToBackEndPeer = getSelf().connect(getBackEndPeer());
                    if (connectionToBackEndPeer != null) {
                        break;
                    }
                } catch (IOException ex) {
                    if (i == MAX_TRY_TIMES - 1) ex.printStackTrace();
                } catch (OtpAuthException ex) {
                    if (i == MAX_TRY_TIMES - 1) ex.printStackTrace();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
        }
        
        return connectionToBackEndPeer;
    }
    
    public static OtpErlangObject rpcCall(String mod, String fun, OtpErlangObject[] args) {
        OtpConnection connection = getConnection();
        if (connection == null) {
            return null;
        }
        
        OtpErlangObject result = null;
        try {
            connection.sendRPC(mod, fun, args);
            result = connection.receive();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (OtpErlangExit ex) {
            ex.printStackTrace();
        } catch (OtpAuthException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    private static OtpErlangAtom ok = new OtpErlangAtom("ok");
    private static OtpErlangAtom error = new OtpErlangAtom("error");
    /** @TODO handle include path */
    public static ASTNode parse(CharSequence cs) throws ParseException {
        ASTNode rootNode = null;
        
        OtpErlangString otpStr = new OtpErlangString(cs.toString());
        OtpConnection connection = getConnection();
        try {
            connection.sendRPC("erl_scan", "string", new OtpErlangObject[] {otpStr});
            OtpErlangTuple scanResult = (OtpErlangTuple)connection.receiveRPC();
            if (scanResult.elementAt(0).equals(ok)) {
                OtpErlangList tokens = (OtpErlangList)scanResult.elementAt(1);
                Collection<OtpErlangList> forms = splitForms(tokens);
                for (OtpErlangList form : forms) {
                    connection.sendRPC("erl_parse", "parse_form", new OtpErlangObject[] {form});
                    OtpErlangTuple parseResult = (OtpErlangTuple)connection.receiveRPC();
                    /** parseResult: {ok, AbsForm} | {error, ErrorInfo}, where AbsForm = term() */
                    if (parseResult.elementAt(0).equals(ok)) {
                        OtpErlangObject absform = parseResult.elementAt(1);
                    } else {
                        System.out.println("Parse Error: " + parseResult.elementAt(1).toString());
                    }
                }
            } else {
                System.out.println("Scan Error: " + scanResult.elementAt(1).toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (OtpErlangExit ex) {
            ex.printStackTrace();
        } catch (OtpAuthException ex) {
            ex.printStackTrace();
        }
        
        return rootNode;
    }
    
    /**
     * Tokens = [{atom(),Line}|{atom(),Line,term()}]
     */
    private static List<OtpErlangList> splitFormsBuf = new ArrayList<OtpErlangList>();
    private static Collection<OtpErlangTuple> tokenCollectorBuf = new ArrayList<OtpErlangTuple>(50);
    private static Collection<OtpErlangList> splitForms(OtpErlangList tokens) {
        splitFormsBuf.clear();
        tokenCollectorBuf.clear();
        for (int i = 0; i < tokens.elements().length; i++) {
            OtpErlangTuple token = (OtpErlangTuple)tokens.elementAt(i);
            tokenCollectorBuf.add(token);
            if (token.elementAt(0).equals("dot")) {
                OtpErlangTuple[] formTokenArray = new OtpErlangTuple[tokenCollectorBuf.size()];
                OtpErlangList formTokenList = new OtpErlangList(tokenCollectorBuf.toArray(formTokenArray));
                splitFormsBuf.add(formTokenList);
                tokenCollectorBuf.clear();
            }
        }
        return splitFormsBuf;
    }
    
    
    private static String getLocalHostName() {
        String localHostName = "localhost";
        if (Utilities.isWindows()) {
            try {
                localHostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
        }
        
        return localHostName;
    }
    
    public static void testConnection() {
        OtpConnection connection = getConnection();
        try {
            connection.sendRPC("erlang", "date", new OtpErlangList());
            OtpErlangObject received = connection.receiveRPC();
            System.out.println("Date from ErlyBird back end node: " + received.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (OtpErlangExit ex) {
            ex.printStackTrace();
        } catch (OtpAuthException ex) {
            ex.printStackTrace();
        }
    }
}




/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
 *
 * Contributor(s): Libor Martinek.
 */
package org.netbeans.modules.remotefs.ftpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.remotefs.core.RemoteClient;
import org.netbeans.modules.remotefs.core.LogInfo;
import org.netbeans.modules.remotefs.core.RemoteFileAttributes;
import org.netbeans.modules.remotefs.core.RemoteFileName;

/**  This class connects to FTP server.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPClient implements RemoteClient {

    /** An empty array of File attributes. */
    private static final FTPFileAttributes[] EMPTY_LIST = new FTPFileAttributes[0];
    /** Control connection stream */
    private BufferedReader in;
    /** Control connection stream */
    private PrintWriter out;
    /** Log stream */
    private PrintWriter log = null;
    /** Socket */
    private Socket socket;
    /** Server socket for data connection */
    private ServerSocket serversocket;
    /** FTP Response */
    private FTPResponse response;
    /** Host name */
    private String host;
    /** Port number */
    private int port;
    /** User name */
    private String user;
    /** Password */
    private String password;
    /** Type of serer system */
    private String serversystem = null;
    /** Is connected to server? */
    private boolean connected = false;
    /** Object used during reconnection */
    private Reconnect reconn = null;
    /** Type of data transfer mode */
    private boolean passiveMode = false;
    /** Default FTP port number */
    public final static int DEFAULT_PORT = 21;
    /** Size of buffer */
    private final static int BUFFER = 1024;
    /** Timeout */
    private final static int TIMEOUT = 60000;
    private String startdir = "/";

    /** Create new FTPClient with this login information
     * @param loginfo
     */
    public FTPClient(FTPLogInfo loginfo) {
        this.host = loginfo.getHost();
        this.port = loginfo.getPort();
        this.user = loginfo.getUser();
        this.password = loginfo.getPassword();
        this.startdir = loginfo.getRootFolder();
        setPassiveMode(loginfo.isPassiveMode());
    }

    //***************************************************************************
    /** Compare this login information.
     * @return 0 if login informations are equal;
     *         1 if login informations refer to the same resource but can't be uses to login;
     *        -1 if login informations are different
     * @param loginfo
     */
    public int compare(LogInfo loginfo) {
        if (!(loginfo instanceof FTPLogInfo)) {
            return -1;
        }
        if (host.equals(((FTPLogInfo) loginfo).getHost()) && port == ((FTPLogInfo) loginfo).getPort() && user.equals(((FTPLogInfo) loginfo).getUser())) {
            if (password.equals(((FTPLogInfo) loginfo).getPassword())) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }

    //***************************************************************************
    /** Interface for notify of reconnection. */
    public interface Reconnect {

        /**
         * @param mess message with reason of closed connection
         * @return whether connection should be restored
         */
        public boolean notifyReconnect(String mess);
    }

    //***************************************************************************
    /** Sets reconnect object
     * @param ro reconnect object
     */
    public void setReconnect(Reconnect ro) {
        reconn = ro;
    }


   //***************************************************************************
    /** Whether passive mode is set.
     * @return Value of property passiveMode.
     */
    public boolean isPassiveMode() {
        return passiveMode;
    }

    //***************************************************************************
    /** Set the passive mode.
     * @param passiveMode New value of property passiveMode.
     */
    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    //***************************************************************************
    /** Gets last respond from server
     * @return last response
     */
    protected FTPResponse getResponse() {
        return response;
    }

    //***************************************************************************
    /** Read response from server
     * @throws IOException if any error occured
     */
    protected void setResponse() throws IOException {
        response = new FTPResponse(in);
        response.writeLog(log);
    }

    //***************************************************************************
    /** Test whether server system is Unix type
     * @return true in case of unix system
     */
    protected boolean isUnixType() {
        if (serversystem != null) {
            return serversystem.toUpperCase().startsWith("UNIX");
        }
        return false;
    }

    //***************************************************************************
    /** Get server root filename
     * @return root */
    public RemoteFileName getRoot() {
        return FTPFileName.getRoot();
    }

    //***************************************************************************
    /** Connects to host
     * @throws IOException if any error occured
     */
    public synchronized void connect() throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(TIMEOUT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        // read response
        setResponse();
        // wait until service is ready
        while (getResponse().isPositivePreliminary()) {
            setResponse();
        }
        // is ready for new user?
        if (!getResponse().isPositiveCompletion()) {
            throw new FTPException(getResponse());
        }

        login();
        binary();
    }

    //***************************************************************************
    /** Reconnect
     * @throws IOException
     */
    public void reconnect() throws IOException {
        close();
        connect();
    }

    //***************************************************************************
    /** Sends command through control connection to host
     * @param command command to send
     * @throws IOException
     */
    protected synchronized void sendCommand(String command) throws IOException {
        out.println(command);
        out.flush();
        if (command.toLowerCase().startsWith("pass")) {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE,"PASS *****");
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE,command);
        }
        // read response
        setResponse();
    }

    //***************************************************************************
    /** Sends command and check response for PositiveCompletion reply. In case of other reply, FTPException is thrown
     * @param command command
     * @param retry  whether command should be repeated in case of lost connection
     * @throws IOException
     */
    protected synchronized void processSimpleCommand(String command, boolean retry) throws IOException {
        processSimpleCommand(command, FTPResponse.POSITIVE_COMPLETION, retry);
    }

    //***************************************************************************
    /** Sends command and check response for required reply
     * @reply acceptable reply (first digit); if other reply is returned, FTPException is thrown
     * @param command command
     * @param reply expected reply
     * @param retry whether command should be repeated in case of lost connection
     * @throws IOException
     */
    protected synchronized void processSimpleCommand(String command, int reply, boolean retry) throws IOException {
        try {
            sendCommand(command);
            if (getResponse().getFirstDigit() != reply) {
                throw new FTPException(getResponse());
            }
        } catch (IOException e) {
            if (retry && reconn != null && connLostException(e)) {
                if (reconn.notifyReconnect(e.toString())) {
                    reconnect();
                    processSimpleCommand(command, reply, false);
                } else {
                    return;
                }
            } else {
                throw e;
            }
        }
    }

    //***************************************************************************
    /** Test whether the exception means that connection was lost. 
     */
    private boolean connLostException(IOException e) {
        boolean lost = (e instanceof SocketException || (e instanceof FTPException && ((FTPException) e).getResponse().getCode() == 421));
        if (lost) {
            connected = false;
        }
        return lost;
    }

    //***************************************************************************
    /** Login to server
     * @throws IOException
     */
    protected synchronized void login() throws IOException {
        // send USER command
        sendCommand("USER " + user);
        if (getResponse().isPositiveCompletion()) {
            return;
        } // password isn't required
        // is password expected?
        if (!getResponse().isPositiveIntermediate()) {
            throw new FTPException(getResponse());
        }
        processSimpleCommand("PASS " + password, false);
        connected = true;
//        processSimpleCommand("CWD " + startdir, false);
        // find out server system
        processSimpleCommand("SYST", false);
        if (getResponse().getResponse().length() >= 5) {
            serversystem = getResponse().getResponse().substring(4);
        }
    }

    //***************************************************************************
    /** Test whether client is connected to server
     * @return true if client is connected to server
     */
    public boolean isConnected() {
        return connected;
    }

    //***************************************************************************
    /** Opens data connection and send command 
     * @param command command to send
     * @throws IOException
     * @return created data socket
     */
    protected synchronized Socket openData(String command) throws IOException {
        // use passive or active mode acording to passiveMode property
        if (isPassiveMode()) {
            return openDataPassive(command);
        } else {
            return openDataActive(command);
        }
    }

    //***************************************************************************
    /** Opens data connection and send command
     * @param command command to send
     * @throws IOException
     * @return created data socket
     */
    protected synchronized Socket openDataActive(String command) throws IOException {
        // create server socket
        serversocket = new ServerSocket(0);
        // find out the port where server listen
        int lPort = serversocket.getLocalPort();
        // prepare parameter for PORT command
        String param = InetAddress.getLocalHost().getHostAddress().replace('.', ',') + "," + (lPort / 256) + "," + (lPort % 256);
        // send port command
        processSimpleCommand("PORT " + param, false);
        // send desired command
        processSimpleCommand(command, FTPResponse.POSITIVE_PRELIMINARY, false);
        //listen for a connection
        Socket datasocket = serversocket.accept();
        return datasocket;
    }

    //***************************************************************************
    /** Opens data connection and send command
     * @param command command to send
     * @throws IOException
     * @return created data socket
     */
    protected synchronized Socket openDataPassive(String command) throws IOException {
        // send PASSIVE command
        processSimpleCommand("PASV", false);
        // get response
        String resp = getResponse().getResponse();
        // get host and port from response
        String lHost = "";
        int lPort = 0;
        boolean success = false;
        if (getResponse().getCode() == 227) {
            // host and port are commonly in bar
            int openbar = resp.indexOf('(');
            int closebar = resp.indexOf(')');
            if (openbar > 3 && closebar > 3 && openbar < closebar) {
                // hostport = host and port separared by commas
                String hostport = resp.substring(openbar + 1, closebar);
                StringTokenizer st = new StringTokenizer(hostport, ",");
                if (st.countTokens() == 6) {
                    // get host
                    for (int i = 0; i < 4; i++) {
                        lHost = lHost + st.nextToken() + (i < 3 ? "." : "");
                    }
                    // count port
                    try {
                        lPort = Integer.parseInt(st.nextToken()) * 256 + Integer.parseInt(st.nextToken());
                        success = true;
                    } // reading wasn't successful
                    catch (NumberFormatException e) {
                        success = false;
                    }
                }
            }
        }
        // readinf host and port not successful
        if (!success) {
            throw new IOException("Can't recognize PASV command response. Use active mode.\n" + response);
        }
        // create new socket
        Socket datasocket = new Socket(lHost, lPort);
        // set timeout
        datasocket.setSoTimeout(TIMEOUT);
        // send desired command
        processSimpleCommand(command, FTPResponse.POSITIVE_PRELIMINARY, false);
        return datasocket;
    }

    //***************************************************************************
    /** Close data connection
     * @param datasocket data socket to close
     * @throws IOException
     */
    protected void closeData(Socket datasocket) throws IOException {
        while (getResponse().isPositivePreliminary()) {
            setResponse();
        }
        if (!getResponse().isPositiveCompletion()) {
            throw new FTPException(getResponse());
        }
        // close all
        datasocket.close();
        if (serversocket != null) {
            serversocket.close();
        }
    }

    //***************************************************************************
    /** Get file from server.
     * @param what
     * @param where
     * @throws IOException
     */
    public synchronized void get(RemoteFileName what, File where) throws IOException {
        int count = 0;
        while (count++ < 2) {
            try {
                // open data connection
                Socket datasocket = openData("RETR " + ((FTPFileName) what).getFullName());
                // open streams
                InputStream datain = datasocket.getInputStream();
                OutputStream fileout = new FileOutputStream(where);

                // copy data
                byte[] buffer = new byte[BUFFER];
                int len;
                while ((len = datain.read(buffer)) != -1) {
                    fileout.write(buffer, 0, len);
                }
                // close stream
                datain.close();
                fileout.close();
                // close data stream
                closeData(datasocket);
                break;
            } catch (IOException se) {
                if (reconn != null && count < 2 && connLostException(se)) {
                    if (reconn.notifyReconnect(se.toString())) {
                        reconnect();
                    } else {
                        return;
                    }
                } else {
                    throw se;
                }
            }
        }
    }

    //***************************************************************************
    /** Put file to server
     * @param what
     * @param where
     * @throws IOException
     */
    public synchronized void put(File what, RemoteFileName where) throws IOException {
        int count = 0;
        while (count++ < 2) {
            try {
                // open data connection
                Socket datasocket = openData("STOR " + ((FTPFileName) where).getFullName());
                // open streams
                InputStream filein = new FileInputStream(what);
                OutputStream dataout = datasocket.getOutputStream();

                // copy data
                byte[] buffer = new byte[BUFFER];
                int len;
                while ((len = filein.read(buffer)) != -1) {
                    dataout.write(buffer, 0, len);
                }
                // close streams
                dataout.close();
                filein.close();
                // close data
                closeData(datasocket);
                break;
            } catch (IOException se) {
                if (reconn != null && count < 2 && connLostException(se)) {
                    if (reconn.notifyReconnect(se.toString())) {
                        reconnect();
                    } else {
                        return;
                    }
                } else {
                    throw se;
                }
            }
        }
    }

    //***************************************************************************
    /** Get file list of directory
     */
    /*
    private synchronized String[] dir(String directory) throws IOException {
    StringBuffer sbuffer = new StringBuffer();
    int count = 0;
    while (count++ < 2) {
    try { 
    // open data connection
    Socket datasocket = openData(directory==null?"NLST":"NLST "+directory);
    // open stream
    InputStreamReader datain = new InputStreamReader(datasocket.getInputStream());
    // read list
    char[] buffer = new char[BUFFER];
    int len;
    while ((len = datain.read(buffer)) != -1) {
    sbuffer.append(buffer, 0, len);
    }
    // close stream
    datain.close();
    // close data connection
    closeData(datasocket);  	
    break;
    }
    catch (IOException se) {
    if (reconn!=null && count < 2 && connLostException(se))
    if (reconn.notifyReconnect(se.toString())) reconnect();
    else return new String[0];
    else throw se;
    }  
    }
    // convert string tokenizer to array of strings
    StringTokenizer stoken = new StringTokenizer(sbuffer.toString(),"\r\n");
    String list[] = new String[stoken.countTokens()];
    int counttoken=0;
    while (stoken.hasMoreTokens()) 
    list[counttoken++]=stoken.nextToken();
    return list;
    }	
     */
    //***************************************************************************
  /* Parse list obtained from server for file attributes
     */
    private FTPFileAttributes[] parseList(StringTokenizer stoken, String name) {
        //TODO - soft links, /dev dir, dir with spaces
        StringTokenizer line;
        FTPFileAttributes attrib[] = null;
        FTPFileAttributes at = null;
        String word;
        boolean skip = false;
        boolean error = false;
        int count = 0;
        int col;
        String month = null;
        int day = 0;
        while (stoken.hasMoreTokens()) {
            String tmp;
            line = new StringTokenizer(tmp = stoken.nextToken());
            col = 0;
            at = new FTPFileAttributes();
            while (line.hasMoreTokens()) {
                word = line.nextToken();
                switch (col) {
                    case 0:
                        if (count == 0) {
                            if (word.equals("total")) {
                                skip = true;
                                break;
                            }
                        }
                        if (word.startsWith("d")) {
                            at.setIsDirectory(true);
                        } else if (word.startsWith("-")) {
                            at.setIsDirectory(false);
                        } else {
                            skip = true;
                        }
                        at.setRights(word.substring(1));
                        break;
                    case 1:
                        try {
                            at.setLinks(Integer.parseInt(word));
                        } catch (NumberFormatException e) {
                            error = true;
                        }
                        break;
                    case 2:
                        at.setUser(word);
                        break;
                    case 3:
                        at.setGroup(word);
                        break;
                    case 4:
                        try {
                            at.setSize(Integer.parseInt(word));
                        } catch (NumberFormatException e) {
                            error = true;
                        }
                        break;
                    case 5:
                        month = word;
                        break;
                    case 6:
                        try {
                            day = Integer.parseInt(word);
                        } catch (NumberFormatException e) {
                            error = true;
                        }
                        if (day < 1 || day > 31) {
                            error = true;
                        }
                        break;
                    case 7:
                        if (!at.setDate(month, day, word)) {
                            error = true;
                        }
                        break;
                    case 8:
                        at.setName(new FTPFileName(name, word));
                        if ((word.equals(".") || word.equals("..")) && count <= 2) {
                            skip = true;
                        }
                        break;

                }
                if (skip || error) {
                    break;
                }
                col++;
            }
            if (skip || error) {
                if (error) {
                    System.out.println("NOT RECOGNIZED: " + tmp);
                }
                skip = false;
                error = false;
            } else {
                if (attrib == null) {
                    attrib = new FTPFileAttributes[stoken.countTokens() + 1];
                }
                attrib[count] = at;
                count++;
            }
        }
        if (attrib == null) {
//      quick workaround - this method should not return null
//      if (!anyerror)
            attrib = EMPTY_LIST;
        }
        return attrib;
    }

    /** List Parser interface 
     * @deprecated never used
     */
    @Deprecated
    public interface ListParser {

        /** Parsers list
         * @param list
         * @return
         */
        @Deprecated
        public RemoteFileAttributes parseList(StringTokenizer list);
    }

    /** Add list parser
     * @param parser
     */
    @Deprecated
    public void addParser(ListParser parser) {
        
    }

    /** Add list parser for specified type
     * @param type
     * @param parser
     */
    @Deprecated
    public void addParser(String type, ListParser parser) {
        
    }

    /** Remove list parser
     * @param parser
     * @return
     */
    @Deprecated
    public boolean removeParser(ListParser parser) {
        return false;
    }

    /** Remove all list parsers */
    @Deprecated
    public void removeAllParsers() {
    }

    //***************************************************************************
    /** Return list of files in directory
     * @param directory
     * @throws IOException
     * @return
     */
    public synchronized RemoteFileAttributes[] list(RemoteFileName directory) throws IOException {
        StringBuffer sbuffer = new StringBuffer();
        int count = 0;
        if (!isConnected()) {
            return new RemoteFileAttributes[0];
        }
        while (count++ < 2) {
            try {
                Socket datasocket = openData(directory == null ? "LIST" : "LIST " + ((FTPFileName) directory).getFullName());
                // open stream
                InputStreamReader datain = new InputStreamReader(datasocket.getInputStream());

                char[] buffer = new char[BUFFER];
                int len;
                while ((len = datain.read(buffer)) != -1) {
                    sbuffer.append(buffer, 0, len);
                }
                datain.close();
                closeData(datasocket);
                break;
            } catch (IOException se) {
                if (reconn != null && count < 2 && connLostException(se)) {
                    if (reconn.notifyReconnect(se.toString())) {
                        reconnect();
                    } else {
                        return new RemoteFileAttributes[0];
                    }
                } else {
                    throw se;
                }
            }
        }
        StringTokenizer stoken = new StringTokenizer(sbuffer.toString(), "\r\n");
        return parseList(stoken, ((FTPFileName) directory).getFullName());
    }

    //***************************************************************************
    /** Rename file
     * @param from
     * @param to
     * @throws IOException
     */
    public synchronized void rename(RemoteFileName from, String to) throws IOException {
        FTPFileName newname = new FTPFileName(((FTPFileName) from).getDirectory(), to);
        int count = 0;
        while (count++ < 2) {
            try {
                processSimpleCommand("RNFR " + from.getFullName(), 3, false);
                processSimpleCommand("RNTO " + newname.getFullName(), 2, false);
                break;
            } catch (IOException se) {
                if (reconn != null && count < 2 && connLostException(se)) {
                    if (reconn.notifyReconnect(se.toString())) {
                        reconnect();
                    } else {
                        return;
                    }
                } else {
                    throw se;
                }
            }
        }
    }

    //***************************************************************************
    /** Delete directory
     * @param path
     * @throws IOException
     */
    public void delete(RemoteFileName path) throws IOException {
        processSimpleCommand("DELE " + ((FTPFileName) path).getFullName(), true);
    }

    //***************************************************************************
    /** Make directory
     * @param name
     * @throws IOException
     */
    public void mkdir(RemoteFileName name) throws IOException {
        processSimpleCommand("MKD " + name.getFullName(), true);
    }

    //***************************************************************************
    /** Remove directory
     * @param path
     * @throws IOException
     */
    public void rmdir(RemoteFileName path) throws IOException {
        processSimpleCommand("RMD " + ((FTPFileName) path).getFullName(), true);
    }

    //***************************************************************************
    /** Print working directory
     * @throws IOException
     */
    public void pwd() throws IOException {
        processSimpleCommand("PWD", true);
    }

    //***************************************************************************
    /** Change working directory
     * @param path
     * @throws IOException
     */
    public void cwd(String path) throws IOException {
        processSimpleCommand("CWD " + path, true);
    }

    //***************************************************************************
    /** Test what system server runs on
     * @throws IOException
     */
    public void system() throws IOException {
        processSimpleCommand("SYST", true);
    }

    /***************************************************************************
    /** Set ascii mode
     * @throws IOException
     */
    public void ascii() throws IOException {
        processSimpleCommand("TYPE A", true);
    }

    //***************************************************************************
    /** Set binary mode
     * @throws IOException
     */
    public void binary() throws IOException {
        processSimpleCommand("TYPE I", true);
    }

    //***************************************************************************
    /** Disconnect from server */
    public synchronized void disconnect() {
        try {
            sendCommand("QUIT");
        } catch (IOException e) {
        }
        close();
    }

    //***************************************************************************
    /** Close connection with server */
    public synchronized void close() {
        //System.out.println("Closing connection to server "+host);
        try {
            connected = false;
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
        }
    }
/* ###########################################################################   
    DEPRECATED METHODS
  ########################################################################### */
 //***************************************************************************
    /** Sets Log to PrintWriter
     * @deprecated : Java Logger is used instead
     * @param log
     * 
     */
    @Deprecated
    public void setLog(PrintWriter log) {
       
    }

    //***************************************************************************
    /** Sets Log to OutputStream
     * @deprecated : Java Logger is used instead
     * @param log
     */
    @Deprecated
    public void setLog(OutputStream log) {
        
    }

    //***************************************************************************
    /** Sets Log to FileDescriptor. In this case, log outputstream is automatically set during connecting
     * @deprecated : Java Logger is used instead
     * @param logfile 
     * @throws IOException  
     */
    @Deprecated
    public void setLog(File logfile) throws IOException {
       
    }

 
}

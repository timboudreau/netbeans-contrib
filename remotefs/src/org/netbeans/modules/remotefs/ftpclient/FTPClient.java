/*                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version 
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is RemoteFS. The Initial Developer of the Original
 * Code is Libor Martinek. Portions created by Libor Martinek are 
 * Copyright (C) 2000. All Rights Reserved.
 * 
 * Contributor(s): Libor Martinek. 
 */

package org.netbeans.modules.remotefs.ftpclient;

import org.netbeans.modules.remotefs.core.RemoteClient;
import org.netbeans.modules.remotefs.core.LogInfo;
import org.netbeans.modules.remotefs.core.RemoteFileAttributes;
import java.io.*;
import java.net.*;
import java.util.*;

/**  This class connects to FTP server.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPClient implements RemoteClient  {

  /** Control connection stream */
  private BufferedReader in;
  /** Control connection stream */
  private PrintWriter out;
  
  /** Log stream */
  private PrintWriter log = null;
  
  /** Log file descriptor */
  private File logfile = null;
  private RandomAccessFile rafile = null;

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
 
  /** Create new FTPClient with this login information
   * @param loginfo
   */
  public FTPClient(FTPLogInfo loginfo) {
    this.host = loginfo.getHost();
    this.port = loginfo.getPort();
    this.user = loginfo.getUser();
    this.password = loginfo.getPassword();
  }

  //***************************************************************************
  /** Compare this login information.
   * @return 0 if login information are equal;
   *         1 if login information refer to the same resource but can't be uses to login;
   *        -1 if login information are different
   * @param loginfo
   */
  public int compare(LogInfo loginfo) {
   if (!(loginfo instanceof FTPLogInfo)) return -1;
   if (host.equals(((FTPLogInfo)loginfo).getHost()) && port == ((FTPLogInfo)loginfo).getPort()
                                                  && user.equals(((FTPLogInfo)loginfo).getUser()))
       if (password.equals(((FTPLogInfo)loginfo).getPassword())) {
          return 0;
        }   
        else  return 1; 
    else  return -1; 
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
  /** Writes text to log stream */
  private void writeLog(String text) {
    if (log != null) {
      log.println(text);
      log.flush();
    }
  }
 
  //***************************************************************************
  /** Sets Log to PrintWriter
   * @param log
   */
  public void setLog (PrintWriter log) {
    this.log = log;
  }
 
  //***************************************************************************
  /** Sets Log to OutputStream
   * @param log
   */
  public void setLog (OutputStream log) {
    this.log = new PrintWriter(new OutputStreamWriter(log));
    this.log.println("\n---------------------------------------------------------------------------\n"+
                     "FTP Log Session: "+new java.util.Date().toString()+
                     "\n---------------------------------------------------------------------------");
  }

  //***************************************************************************
  /** Sets Log to FileDescriptor. In this case, log outputstream is automatically set during connecting
   * @param logFD
   */
  public void setLog (File logfile) throws IOException {
    if (!logfile.exists()) {
        logfile.createNewFile();
    }
    this.logfile = logfile;
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
     if (serversystem!=null)  return serversystem.toUpperCase().startsWith("UNIX TYPE: L8");
     return false;
  }
  
  //***************************************************************************
  /** Connects to host
   * @throws IOException if any error occured
   */
  public synchronized void connect () throws IOException {
    if (log == null  &&  logfile !=null) {
         rafile = new RandomAccessFile(logfile,"rw");
         rafile.seek(rafile.length());
         setLog(new FileOutputStream(rafile.getFD()));
    }
    socket = new Socket (host,port);
    socket.setSoTimeout(TIMEOUT);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream());
    // read response
    setResponse();
    // wait until service is ready
    while (getResponse().isPositivePreliminary()) setResponse();
    // is ready for new user?
    if (!getResponse().isPositiveCompletion()) throw new FTPException(getResponse());
    
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
    if (command.toLowerCase().startsWith("pass")) writeLog("PASS *****");
    else writeLog(command);
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
    processSimpleCommand(command,FTPResponse.POSITIVE_COMPLETION,retry);
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
      if (getResponse().getFirstDigit()!=reply) throw new FTPException(getResponse());
    }
    catch (IOException e) {
      if (retry && reconn!=null && connLostException(e)) 
         if (reconn.notifyReconnect(e.toString())) {
            reconnect();
            processSimpleCommand(command,reply,false);
         }
         else 
            return;
      else throw e;
    }   
  }

  //***************************************************************************
  /** Test whether the exception means that connection was lost. 
   */
  private boolean connLostException(IOException e) {
    boolean lost = (e instanceof SocketException || (e instanceof FTPException && ((FTPException)e).getResponse().getCode()==421));
    if (lost) connected = false;
    return lost;
  }  
 
  //***************************************************************************
  /** Login to server
   * @throws IOException
   */
  protected synchronized void login() throws IOException {
    // send USER command
    sendCommand("USER "+user);
    if (getResponse().isPositiveCompletion()) return; // password isn't required
    // is password expected?
    if (!getResponse().isPositiveIntermediate()) throw new FTPException(getResponse());
    processSimpleCommand("PASS "+password,false);
    connected = true;
    // find out server system
    processSimpleCommand("SYST",false);
    if (getResponse().getResponse().length() >= 5) serversystem=getResponse().getResponse().substring(4);
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
    if (isPassiveMode()) return openDataPassive(command);
    else return openDataActive(command);
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
    int port = serversocket.getLocalPort();
    // prepare parameter for PORT command
    String param = InetAddress.getLocalHost().getHostAddress().replace('.',',')
                                   + "," + (port/256) + "," + (port%256);
    // send port command
    processSimpleCommand("PORT "+param,false);
    // send desired command
    processSimpleCommand(command,FTPResponse.POSITIVE_PRELIMINARY,false);
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
    processSimpleCommand("PASV",false);
    // get response
    String resp = getResponse().getResponse();
    // get host and port from response
    String host = "";
    int port = 0;
    boolean success = false;
    if (getResponse().getCode() == 227) {
      // host and port are commonly in bar
      int openbar = resp.indexOf('(');
      int closebar = resp.indexOf(')');
      if (openbar > 3 && closebar > 3 && openbar < closebar) {
        // hostport = host and port separared by commas
        String hostport = resp.substring (openbar+1,closebar);
        StringTokenizer st = new StringTokenizer(hostport,",");
        if (st.countTokens() == 6) {
           // get host
           for(int i=0;i<4;i++) 
              host = host + st.nextToken() + (i<3?".":"");
           // count port
           try { port = Integer.parseInt(st.nextToken()) * 256 + Integer.parseInt(st.nextToken()); 
                 success = true;
           }
           // reading wasn't successful
           catch(NumberFormatException e) { success = false; }
        }
      }
    }
    // readinf host and port not successful
    if (!success) throw new IOException("Can't recognize PASV command response. Use active mode.\n"+response);
    // create new socket
    Socket datasocket = new Socket (host,port);
    // set timeout
    datasocket.setSoTimeout(TIMEOUT);
    // send desired command
    processSimpleCommand(command,FTPResponse.POSITIVE_PRELIMINARY,false);
    return datasocket;
  }
  
  //***************************************************************************
  /** Close data connection
   * @param datasocket data socket to close
   * @throws IOException
   */
  protected void closeData(Socket datasocket) throws IOException {
    while (getResponse().isPositivePreliminary()) setResponse();
    if (!getResponse().isPositiveCompletion()) throw new FTPException(getResponse());
    // close all
    datasocket.close();
    if (serversocket != null) serversocket.close();
  }
 
  //***************************************************************************
  /** Get file from server.
   * @param accesspath
   * @param what
   * @param where
   * @throws IOException
   */
  public synchronized void get(String accesspath, String what, File where) throws IOException {
    throw new Error("Not implemented yet");
  }
  
  //***************************************************************************
  /** Get file from server.
   * @param what
   * @param where
   * @throws IOException
   */
  public synchronized void get(String what, File where) throws IOException {
   int count = 0;
    while (count++ < 2) {
      try { 
          // open data connection
          Socket datasocket = openData("RETR "+what);
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
      }
      catch (IOException se) {
        if (reconn!=null && count < 2 && connLostException(se))
          if (reconn.notifyReconnect(se.toString()))
             reconnect();
          else 
             return;
        else throw se;
      }  
    }
  }
 
  /***************************************************************************
  //** Put file to server
   * @param what
   * @param accesspath
   * @param where
   * @throws IOException
   */
  public synchronized void put(File what, String accesspath, String where) throws IOException {
    throw new Error("Not implemented yet");
  }
  
  //***************************************************************************
  /** Put file to server
   * @param what
   * @param where
   * @throws IOException
   */
  public synchronized void put(File what,  String where) throws IOException {
    int count = 0;
    while (count++ < 2) {
      try { 
          // open data connection
          Socket datasocket = openData("STOR "+where);
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
      }
      catch (IOException se) {
        if (reconn!=null && count < 2 && connLostException(se))
          if (reconn.notifyReconnect(se.toString())) reconnect();
          else return;
        else throw se;
      }  
    }
  }
 
  //***************************************************************************
  /** Get file list of directory
    */
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
   
  //***************************************************************************
  /* Parse list obtained from server for file attributes
   */
  private FTPFileAttributes[] parseList(StringTokenizer stoken) {
    //TODO - soft links, /dev dir, dir with spaces
    StringTokenizer line;
    FTPFileAttributes attrib[] = null;
    FTPFileAttributes at = null; 
    String word;
    boolean skip = false;
    boolean error = false;
    boolean anyerror = false;
    int count=0;
    int col;
    String month = null;
    int day = 0;
    int lines = stoken.countTokens();
    while (stoken.hasMoreTokens()) {
        String tmp;
        line = new StringTokenizer(tmp=stoken.nextToken());  
        col=0;
        at = new FTPFileAttributes();
        while (line.hasMoreTokens()) {
          word = line.nextToken();
          switch (col) {
            case 0: if (count==0) {
                       if (word.equals("total")) {
                          skip=true;
                          break;
                       }
                    } 
                    if (word.startsWith("d")) 
                          at.setIsDirectory(true);
                    else
                        if (word.startsWith("-")) 
                              at.setIsDirectory(false);
                        else  
                              skip = true;
                    at.setRights(word.substring(1));
                    break;
            case 1: try { at.setLinks(Integer.parseInt(word)); }
                    catch (NumberFormatException e) { error = true; }
                    break;
            case 2: at.setUser(word); break;
            case 3: at.setGroup(word); break;
            case 4: try { at.setSize(Integer.parseInt(word)); }
                    catch (NumberFormatException e) { error = true; }
                    break;
            case 5: month=word; break;
            case 6: try { day=Integer.parseInt(word); }
                    catch (NumberFormatException e) { error = true; }
                    if (day < 1 || day > 31) error = true;
                    break;
            case 7: if (!at.setDate(month,day,word)) error = true; 
                    break;
            case 8: at.setName(word); 
                    if ((word.equals(".") || word.equals("..")) && count <= 2) 
                        skip = true;
                    break;
                    
          }
          if (skip || error) break;
          col++;  
        } 
        if (skip || error) {
            if (error) { 
              anyerror = true;
              System.out.println("NOT RECOGNIZED: "+tmp);
            } 
            skip = false;
            error = false;
        }    
        else {
             if (attrib == null) attrib = new FTPFileAttributes[stoken.countTokens()+1];
             attrib[count] = at;
             count++;  
        }
    }      
    if (attrib == null) {
      if (!anyerror)
          attrib = new FTPFileAttributes[0];
    }  
    return attrib;
  }
  
  /** List Parser interface */
  public interface ListParser {
     /** Parsers list
     * @param list
     * @return
     */
     public RemoteFileAttributes parseList(StringTokenizer list);
  }
  
  /** Add list parser
   * @param parser
   */
  public void addParser(ListParser parser) {
    throw new Error("Not implemented yet");
  }
  /** Add list parser for specified type
   * @param type
   * @param parser
   */
  public void addParser(String type, ListParser parser) {
    throw new Error("Not implemented yet");
  }

  /** Remove list parser
   * @param parser
   * @return
   */
  public boolean removeParser(ListParser parser) {
    return false;
  }
  /** Remove all list parsers */
  public void removeAllParsers() {
  }
  
  //***************************************************************************
  /** Return list of files in directory
   * @param accesspath
   * @param dirname
   * @throws IOException
   * @return
   */
  public synchronized RemoteFileAttributes[] list(String accesspath, String dirname) throws IOException {
    throw new Error("Not implemented yet");
  }

  //***************************************************************************
  /** Return list of files in directory
   * @param directory
   * @throws IOException
   * @return
   */
  public synchronized RemoteFileAttributes[] list(String directory) throws IOException {
    StringBuffer sbuffer = new StringBuffer();
    int count = 0;
    if (!isConnected()) return null;
    while (count++ < 2) {
      try { 
            Socket datasocket = openData(directory==null?"LIST":"LIST "+directory);
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
      }
      catch (IOException se) {
        if (reconn!=null && count < 2 && connLostException(se))
           if (reconn.notifyReconnect(se.toString())) reconnect();
           else return new RemoteFileAttributes[0];
        else throw se;
      }  
    }
    StringTokenizer stoken = new StringTokenizer(sbuffer.toString(),"\r\n");
    return parseList(stoken);
  }	
  
  //***************************************************************************
  /** Rename file
   * @param fromaccesspath
   * @param fromname
   * @param toaccesspath
   * @param toname
   * @throws IOException
   */
  public synchronized void rename(String fromaccesspath, String fromname, String toaccesspath, String toname) throws IOException {
    throw new Error("Not implemented yet");
  }
 
  //***************************************************************************
  /** Rename file
   * @param from
   * @param to
   * @throws IOException
   */
  public synchronized void rename(String from, String to) throws IOException {
    int count = 0;
    while (count++ < 2) {
      try { 
        processSimpleCommand("RNFR "+from,3,false);
        processSimpleCommand("RNTO "+to,2,false); 	
        break;
      }
      catch (IOException se) {
        if (reconn!=null && count < 2 && connLostException(se))
          if (reconn.notifyReconnect(se.toString())) reconnect();
          else return;
        else throw se;
      }  
    }
  }
  
  //***************************************************************************
  /** Delete directory
   * @param path
   * @throws IOException
   */
  public void delete(String path) throws IOException {
    processSimpleCommand("DELE "+path,true);
  }
 
  //***************************************************************************
  /** Delete directory
   * @param accesspath
   * @param name
   * @throws IOException
   */
  public void delete(String accesspath, String name) throws IOException {
    throw new Error("Not implemented yet");
  }
 
  //***************************************************************************
  /** Make directory
   * @param path
   * @throws IOException
   */
  public void mkdir(String path) throws IOException {
    processSimpleCommand("MKD "+path,true);
  } 
  
  //***************************************************************************
  /** Make directory
   * @param accesspath
   * @param name
   * @throws IOException
   */
  public void mkdir(String accesspath, String name) throws IOException {
    throw new Error("Not implemented yet");
  } 
  
  //***************************************************************************
  /** Remove directory
   * @param path
   * @throws IOException
   */
  public void rmdir(String path) throws IOException {
    processSimpleCommand("RMD "+path,true);
  } 
  
  //***************************************************************************
  /** Remove directory
   * @param accesspath
   * @param name
   * @throws IOException
   */
  public void rmdir(String accesspath, String name) throws IOException {
    throw new Error("Not implemented yet");
  } 

  //***************************************************************************
  /** Print working directory
   * @throws IOException
   */
  public void pwd() throws IOException {
    processSimpleCommand("PWD",true);
  } 

  //***************************************************************************
  /** Change working directory
   * @param path
   * @throws IOException
   */
  public void cwd(String path) throws IOException {
    processSimpleCommand("CWD "+path,true);
  } 

  //***************************************************************************
  /** Test what system server runs on
   * @throws IOException
   */
  public void system() throws IOException {
    processSimpleCommand("SYST",true);
  }
  
  /***************************************************************************
  /** Set ascii mode
   * @throws IOException
   */
  public void ascii() throws IOException {
    processSimpleCommand("TYPE A",true);
  }
  
  //***************************************************************************
  /** Set binary mode
   * @throws IOException
   */
  public void binary() throws IOException {
    processSimpleCommand("TYPE I",true);
  }
  
  //***************************************************************************
  /** Disconnect from server */
  public synchronized void disconnect()  {
    try { 
      sendCommand("QUIT");  
    }
    catch (IOException e) { }
    close();
  }  
    
  //***************************************************************************
  /** Close connection with server */
  public synchronized void close()  {
    //System.out.println("Closing connection to server "+host);
    try {
      connected = false;
      if (in != null) { in.close();  in = null; }
      if (out != null) { out.close(); out = null; }
      if (socket != null) { socket.close(); socket = null; }
      if (log != null) { log.close(); log = null; }
      if (rafile != null) { rafile.close(); rafile = null; }
    }  
    catch(IOException e) { }
  }
}
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

import java.io.*;

/** FTP Response class that reads and stores response from FTP server.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPResponse extends Object  {
 
  /** FTP Response digit code. */
  private int code;
  /** Text message */
  private String response;
  
  /** Positive Preliminary response */
  public static final int POSITIVE_PRELIMINARY = 1;
  /** Positive Completion resposne */
  public static final int POSITIVE_COMPLETION  = 2;
  /** Positive Intermediate response */
  public static final int POSITIVE_INTERMEDIATE = 3;
  /** Treansient Negative Completion response */
  public static final int TRANSIENT_NEGATIVE_COMPLETION = 4;
  /** Permanent Negative Completion */
  public static final int PERMANENT_NEGATIVE_COMPLETION = 5;
  
  
  /** Creates new FTPResponse and reads response from server.
   * @param in BufferedReader to read response from
   * @throws IOException
   */
  public FTPResponse(BufferedReader in) throws IOException {
    String line = in.readLine();
    // Get digit code
    String stringcode = line.substring(0,3);
    try { 
      code = Integer.parseInt(stringcode); 
    }
    catch (NumberFormatException e) { 
      code = 0;
    }
    // Get Response
    if (line.length()>=4 && line.charAt(3)=='-') {
       StringBuffer multiline = new StringBuffer();
       multiline.append(line);
       do {
         line = in.readLine();
         multiline.append("\n");
         multiline.append(line);
       } while (!line.startsWith(stringcode) || line.startsWith(stringcode+"-"));
       response=multiline.toString();
    } 
    else {
       response=line; 
    }
  }
  
  /** Writes response from FT server to Log stream
   * @param log
   */
  protected void writeLog(PrintWriter log) {
    if (log != null) {
      log.println(response);
      log.flush();
    }
  }
  
  /** Returns response.
   * @return String response
   */
  public String getResponse() {
    return response;
  }
  
  /** Returns response
   * @return String response
   */
  public String toString() {
    return response;
  }

  /** Returns code of response.
   * @return code
   */
  public int getCode() {
    return code;
  }
  
  /** Returns first digit of response code.
   * @return first digit of code
   */
  public int getFirstDigit() {
    return getCode()/100;
  }
  
  /** Returns second digit of response code.
   * @return second digit of code
   */
  public int getSecondDigit() {
    return (getCode()%100)/10;
  }
  
  /** Returns third digit of response code.
   * @return third digit of code
   */
  public int getThirdDigit() {
    return getCode()%10;
  }
  
  /** Test whether this response is Positive Preliminary.
   * @return
   */
  public boolean isPositivePreliminary() {
    return getFirstDigit() == POSITIVE_PRELIMINARY;
  }
  
  /** Test whether this response is Positive Completion.
   * @return
   */
  public boolean isPositiveCompletion() {
    return getFirstDigit() == POSITIVE_COMPLETION;
  }
  
  /** Test whether this response is Positive Intermediate.
   * @return
   */
  public boolean isPositiveIntermediate() {
    return getFirstDigit() == POSITIVE_INTERMEDIATE;
  }
  
  /** Test whether this response is Transient Negative Completion.
   * @return
   */
  public boolean isTransientNegativeCompletion() {
    return getFirstDigit() == TRANSIENT_NEGATIVE_COMPLETION;
  }
  
  /** Test whether this response is Permament Negative Completion.
   * @return 
   */
  public boolean isPermanentNegativeCompletion() {
    return getFirstDigit() == PERMANENT_NEGATIVE_COMPLETION;
  }

}

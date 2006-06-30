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
 * The Original Software is the DocSup module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
#ifdef __WIN32__
# define MINGW
#else
# define UNIX
#endif

#ifdef UNIX
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#endif

#ifdef MINGW
#include <winsock2.h>
#endif

#include <unistd.h>
#include <strings.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>

#include <getopt.h>

#define DELIMITER " : "

#define SERVER_ADRESS "127.0.0.1"
#define SERVER_PORT   12401

void print_help(const char *exe_name) {
    fprintf(stderr, "Usage: %s [OPTIONS] [DATA]\n", exe_name);
    fprintf(stderr, "Sends specified data through a socket to a given server address and port.\n\n");
    fprintf(stderr, "  -s, --server\t\tthe server address to use [defaults to %s]\n", SERVER_ADRESS); 
    fprintf(stderr, "  -p, --port\t\tthe server port to use [defaults to %d]\n", SERVER_PORT); 
    fprintf(stderr, "  -h, --help\t\tprints this help\n"); 
    fprintf(stderr, "  -V, --version\t\tprints version\n"); 
    fprintf(stderr, "  -v, --verbose\t\ttries to be more verbose\n");
    fprintf(stderr, "\n"); 
}

int main(int argc, char *argv[]) {
    int sock;
    char *server_adress = SERVER_ADRESS;
    int   server_port   = SERVER_PORT;
    
    const option options[] = {
       { "server", true, NULL, 's'},
       { "port", true, NULL, 'p'},
       { "help", false, NULL, 'h'},
       { "version", false, NULL, 'V'},
       { "verbose", false, NULL, 'v'},
       { NULL, 0, NULL, 0 } };
       
    bool finish = false;
    bool should_fail = false;
    bool should_end = false;
    bool verbose = false;
    
    while (!finish) {
       switch (getopt_long(argc, argv, "s:p:hvV", options, NULL)) {
           case 's':
	      if (server_adress != SERVER_ADRESS)
	         free(server_adress);
	      
	      server_adress = strdup(optarg);
	      break;
	      
	   case 'p':
	      {
	      if (*optarg == '\0') {
	         fprintf(stderr, "--port command requires an argument specifying the port.\n");
		 should_fail = true;
		 break;
	      }
	      
	      char *end;
	      int dest_port = strtol(optarg, &end, 0);
	      
	      if (*end != '\0') {
	          fprintf(stderr, "Incorrect port specification: \"%s\".\n", optarg);
		  should_fail = true;
		  break;
	      }
	      
	      server_port = dest_port;
	      break;
	      }
	      
	   case 'h':
	      print_help(argv[0]);
	      should_end = true;
	      break;
	      
	  case 'v':
	      verbose = true;
	      break;
	  
	  case 'V':
	      fprintf(stderr, "PLACEHOLDER: this should print out client version.\n");
	      should_end = true;
	      break;
	  
	  case (-1):
	      finish = true;
	      break;
          
	  case '?':
              fprintf(stderr, "Unknown option.");
	      should_fail = true;
	      break;
       }
    }
    
    if (should_fail) {
        return 1;
    }
    
    if (should_end) {
        return 0;
    }

    if (verbose)
        printf("Creating socket...");
	
    if ((sock = socket(PF_INET, SOCK_STREAM, 0)) == (-1)) {
        fprintf(stderr, "An error occured during \"socket\": errno=%d\n", errno);
        return 1;
    }
    
    if (verbose)
        printf("done.\n");

    sockaddr_in addr;
    
    memset((char *) &addr, 0, sizeof(addr));
    addr.sin_family      = AF_INET;
    
#ifdef UNIX
    in_addr target_adress;
    
    if (!inet_aton(server_adress, &target_adress)) {
        printf("The adress \"%s\" is not valid internet adress.\n", server_adress);
	return 1;
    }
    
    addr.sin_addr.s_addr = target_adress.s_addr;
#endif
#ifdef MINGW
    if ((addr.sin_addr.s_addr = inet_addr(server_adress)) == INADDR_NONE) {
        printf("The adress \"%s\" is not valid internet adress.\n", server_adress);
	return 1;
    }
#endif
    addr.sin_port        = htons(server_port);
    
    if (connect(sock, (sockaddr *) &addr, sizeof(addr)) < 0) {
        printf("An error occured during \"connect\": errno=%d\n", errno);
        return 1;
    }

    int count = optind;
    
    if (verbose) {
        printf("%s: starting to write arguments into the socket.\n", argv[0]);
    }
    
    while (count < argc) {
        if (verbose) {
            printf("%s: count=%d, value=%s, length=%d\n", argv[0], count, argv[count], strlen(argv[count]));
	}
	
//        write(sock, argv[count], strlen(argv[count]));
        send(sock, argv[count], strlen(argv[count]), 0);
	
	count++;
	
	if (count < argc)
	   send(sock, DELIMITER, strlen(DELIMITER), 0);
//	   write(sock, DELIMITER, strlen(DELIMITER));
    }

    close(sock);
    
    return 0;
}


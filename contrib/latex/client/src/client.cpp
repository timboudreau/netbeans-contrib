/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the DocSup module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
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

void print_help(void) {
    fprintf(stderr, "PLACEHOLDER: help\n");
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
	      
	      if (end != '\0') {
	          fprintf(stderr, "Incorrect port specification: \"%s\".\n", optarg);
		  should_fail = true;
		  break;
	      }
	      
	      server_port = dest_port;
	      break;
	      }
	      
	   case 'h':
	      print_help();
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

    fprintf(stderr, "Creating socket...");
    if ((sock = socket(PF_INET, SOCK_STREAM, 0)) == (-1)) {
        fprintf(stderr, "An error occured during \"socket\": errno=%d\n", errno);
        return 1;
    }
    
    fprintf(stderr, "done.\n");

    sockaddr_in addr;
    
    bzero((char *) &addr, sizeof(addr));
    addr.sin_family      = AF_INET;
    
    in_addr target_adress;
    
    if (!inet_aton(server_adress, &target_adress)) {
        printf("The adress \"%s\" is not valid internet adress.\n", server_adress);
	return 1;
    }
    
    addr.sin_addr.s_addr = target_adress.s_addr;
    addr.sin_port        = htons(SERVER_PORT);
    
    if (connect(sock, (sockaddr *) &addr, sizeof(addr)) < 0) {
        printf("An error occured during \"connect\": errno=%d\n", errno);
        return 1;
    }

    int count = 1;
    
    while (count < argc) {
        write(sock, argv[count], strlen(argv[count]));
	
	count++;
	
	if (count < argc)
	   write(sock, DELIMITER, strlen(DELIMITER));
    }

    close(sock);
    
    return 0;
}


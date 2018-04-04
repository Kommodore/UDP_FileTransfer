#include <stdio.h>
#include <stdlib.h>
#include <string.h>  
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h> 
#include <netinet/in.h> 
#include <arpa/inet.h> 

#define SRV_PORT 8999
#define MAXLINE 512

int main(int argc, char **argv) {
    
    int sock_fd;
    struct sockaddr_in srv_addr, cli_addr;

    char out[] = "Hallo Jonas!\0";
    size_t out_len = strlen(out);

    if((sock_fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        printf("Konnte Socket nicht oeffnen!\n");
        exit(1);
    }

    memset ((void *)&cli_addr, '\0', sizeof(cli_addr)); 
	cli_addr.sin_family = AF_INET; 
	cli_addr.sin_addr.s_addr = htonl(INADDR_ANY); // NOLINT
	if (bind(sock_fd, (struct sockaddr *)&cli_addr, sizeof(cli_addr))<0){ 
        printf("Konnte Socket binden!\n");
        exit(1);
    } 
	
    memset ((void *)&srv_addr, '\0', sizeof(srv_addr)); 
	srv_addr.sin_family = AF_INET; 
	srv_addr.sin_addr.s_addr = inet_addr(argv[1]); 
	srv_addr.sin_port = htons(SRV_PORT); // NOLINT

    if(sendto(sock_fd, out, out_len, 0, (struct sockaddr *)&srv_addr, sizeof(srv_addr)) != out_len)
    {
        printf("Fehler beim Schreiben des Sockets!\n");
        exit(1);
    }

    printf("Daten gesendet!\n");

	close (sock_fd); 
	exit (0); 

}
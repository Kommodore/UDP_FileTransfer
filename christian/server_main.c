#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <string.h>
#include <netinet/in.h>

#define SERVER_PORT 8999

typedef struct SocketInfo
{   
    int sock_fd;
    socklen_t addr_len;
    struct sockaddr_in addr;
} SocketInfo;

void start_server(SocketInfo *server)
{
    if((server->sock_fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        printf("Could not open UDP-Socket!\n");
        exit(1);
    }
    memset ((void *)&server->addr, '\0', sizeof(server->addr));
	server->addr.sin_family = AF_INET; 
	server->addr.sin_addr.s_addr = htonl(INADDR_ANY); 
	server->addr.sin_port = htons(SERVER_PORT); 
    server->addr_len = sizeof(server->addr);
	if (bind (server->sock_fd, (struct sockaddr *)&server->addr, server->addr_len) < 0 ) 
    { 
			printf("Could not bind local address!\n"); 
            exit(1);
	} 
	printf ("UDP Server: ready ...\n"); 
}

int main(int argc, char** argv)
{
    int bytes_recv;
    char in[256];
    char out[] = "Message recived!\0";

    char buffer_one[256];
    char buffer_two[256];
    char buffer_three[256];


    SocketInfo server_info;
    SocketInfo client_info;

    start_server(&server_info);

    for(;;)
    {
        memset(in, '\0', sizeof(in));
        memset(out, '\0', sizeof(out));

        memset(buffer_one, '\0', sizeof(out));
        memset(buffer_two, '\0', sizeof(out));
        memset(buffer_three, '\0', sizeof(out));


        client_info.addr_len = sizeof(client_info.addr);

        if((bytes_recv = recvfrom(server_info.sock_fd, in, 256, 0, (struct sockaddr *)&client_info.addr, &client_info.addr_len)) < 0)
        {
            printf("An Error occurred while reading!\n");
            exit(1);
        }

        printf("MSG: %s\n", in);

        sprintf("%s %s %s", buffer_one, buffer_two, buffer_three);
        
        if(strcmp(buffer_three, "data") == 0)
        {
            printf("Worked!\n");
        }

        if((sendto(server_info.sock_fd, out, strlen(out), 0, (struct sockaddr *)&client_info.addr, client_info.addr_len)) < 0)
        {
            printf("An Error occurred while sending!\n");
            exit(1);
        }
        
    }

    return 0;
}
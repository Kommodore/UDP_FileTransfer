#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h> 

#define SERVER_PORT 8999

typedef struct SocketInfo
{   
    int sock_fd;
    socklen_t addr_len;
    struct sockaddr_in addr;
} SocketInfo;

void start_client(SocketInfo *client, SocketInfo *server, char *ip)
{
    if ((client->sock_fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) 
    { 
		printf("Kann Datagram-Socket nicht oeffnen!\n"); 
        exit(1);
    } 
    memset((void *)&client->addr, '\0', sizeof(client->addr));
    client->addr.sin_family = AF_INET;
    client->addr.sin_addr.s_addr = htonl(INADDR_ANY);
    client->addr_len = sizeof(client->addr);
    if(bind(client->sock_fd, (struct sockaddr *)&client->addr, sizeof(client->addr)) < 0)
    {
        printf("Could not bind local address!\n");
        exit(1);
    }

    memset((void *)&server->addr, '\0', sizeof(server->addr));
    server->addr.sin_family = AF_INET;
    server->addr.sin_addr.s_addr = inet_addr(ip);
    server->addr.sin_port = htons(SERVER_PORT);
    server->addr_len = sizeof(server->addr);

    printf("UDP Client: ready...\n");
}

int main(int argc, char** argv)
{
    char in[256];
    char out[] = "Client sending data!\0";

    SocketInfo server_info, client_info;

    if(argc != 2)
    {
        printf("Usage: ./client <ip_addr>\n");
        exit(1);
    }

    printf("IP: %s\n", argv[1]);
    
    start_client(&client_info, &server_info, argv[1]);

    if(sendto(client_info.sock_fd, out, strlen(out), 0, (struct sockaddr *)&server_info.addr, server_info.addr_len) < 0)
    {
        printf("Failed to send data\n");
        exit(1);   
    }

    for(;;)
    {
        if(recvfrom(client_info.sock_fd, in, 256, 0, (struct sockaddr *)NULL, (socklen_t*)NULL) < 0)
        {
            printf("Failed to recive data!");
            exit(1);
        }

        printf("MSG: %s\n", in);
    }
    
    close(client_info.sock_fd);
    return 0;
}
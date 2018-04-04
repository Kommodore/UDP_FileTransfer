#include <stdio.h>
#include <zconf.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <string.h>
#include <netinet/in.h>

#define SERVER_PORT 8999

typedef struct SocketInfo
{
    int sock_fd;
    int addr_len;
    struct sockaddr_in addr;
} SocketInfo;

typedef struct ConnectionInfo
{
    char fileSize[256];
    unsigned long chunk_size;
} ConnectionInfo;

void start_server(SocketInfo *server, int port)
{
    int reuse = 1;

    if((server->sock_fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 || setsockopt(server->sock_fd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse)) < 0)
    {
        printf("Cant initialize socket\n");
        exit(1);
    }

    memset((void*)&server->addr, '\0', sizeof(server->addr));
    server->addr_len = sizeof(server->addr);
    server->addr.sin_family = AF_INET;
    server->addr.sin_addr.s_addr = htonl(INADDR_ANY); // NOLINT
    server->addr.sin_port = htons(port); // NOLINT
    if(bind(server->sock_fd, (struct sockaddr*) &server->addr, sizeof(server->addr)) < 0)
    {
        printf("Can't open socket on %d, is socket already in use?\n", port);
        exit(1);
    }
}

int main() {
    SocketInfo server_info;
    SocketInfo client_info;

    start_server(&server_info, SERVER_PORT);
    printf("Server started on port %d\n", SERVER_PORT);
    listen(server_info.sock_fd, 5);

    #pragma clang diagnostic push
    #pragma clang diagnostic ignored "-Wmissing-noreturn"
    for(;;)
    {
        size_t MAXLINE = 9001;
        char in[MAXLINE];
        client_info.addr_len = sizeof(client_info.addr);

        ssize_t n = recvfrom(server_info.sock_fd, in, MAXLINE, 0, (struct sockaddr*)& client_info.addr, (socklen_t *)&client_info.addr_len);

       printf("%s\n", in);
       sscanf(in, "%s;%u;%s", );

       close(client_info.sock_fd);
    }
    #pragma clang diagnostic pop
}
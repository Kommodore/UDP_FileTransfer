#include <stdio.h>
#include <zconf.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <string.h>
#include <netinet/in.h>

#define SERVER_PORT 8999
#define MAX_CONNECTIONS 256

unsigned int static_count = 0;

typedef struct SocketInfo
{
    int sock_fd;
    int addr_len;
    struct sockaddr_in addr;
} SocketInfo;

typedef struct SessionInfo
{
    int session_id;
    char filename[256];
    unsigned int chunk_size;
    unsigned int session_closed;
    SocketInfo socketinfo;
} SessionInfo;

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

void establish_connection(SessionInfo* client, SessionInfo* new_client)
{
    char out[256];
    int i;
    for(i = 0; i < MAX_CONNECTIONS; i++)
    {
        if(client[i].session_closed == 1)
        {
            strcat(client[i].filename, new_client->filename);
            client[i].chunk_size = new_client->chunk_size;
            client[i].session_id = static_count++;
            client[i].session_closed = 0;
            sprintf(out, "%s;%u", "HSOSSTP_SIDXX", client[i].session_id);
            break;
        }
    }

    if(i == MAX_CONNECTIONS)
    {
        strcpy(out, "HSOSSTP_ERROR;NOS");
    }

    if(sendto(client[i].socketinfo.sock_fd, out, client[i].chunk_size, 0, (struct sockaddr*) &client[i].socketinfo.addr, sizeof(client[i].socketinfo.addr)) != client[i].chunk_size)
    {
        printf("Couldn't send data to client with session id %u", client[i].session_id);
    }
}

int main()
{
    SocketInfo server;
    SocketInfo reciever;
    SessionInfo client;
    char method[256];
    SessionInfo client_list[MAX_CONNECTIONS];

    for(int i = 0; i < MAX_CONNECTIONS; i++){
        client_list[i].session_closed = 1;
    }

    start_server(&server, SERVER_PORT);
    printf("Server started on port %d\n", SERVER_PORT);
    listen(server.sock_fd, 5);

    #pragma clang diagnostic push
    #pragma clang diagnostic ignored "-Wmissing-noreturn"
    for(;;)
    {
        size_t MAXLINE = 9001;
        char in[MAXLINE];
        reciever.addr_len = sizeof(reciever.addr);

        ssize_t n = recvfrom(server.sock_fd, in, MAXLINE, 0, (struct sockaddr*)& reciever.addr, (socklen_t *)&reciever.addr_len);

        sscanf(in, "%s;%u;%s", method, &client.chunk_size, client.filename); // NOLINT

        printf("Requested Method: %s\nTransmitting %s with %u bytes per chunk",method, client.filename, client.chunk_size);

        if(strcmp("HSOSSTP_INITX", method) == 0){
            establish_connection(client_list, &client);
        }

        close(reciever.sock_fd);
    }
    #pragma clang diagnostic pop
}
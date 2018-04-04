#include <stdio.h>
#include <zconf.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <string.h>
#include <netinet/in.h>

typedef struct SocketInfo
{
    int sock_fd;
    int addr_len;
    struct sockaddr_in addr;
} SocketInfo;

void start_server(SocketInfo *server, const char *dir, int port)
{
    int reuse = 1;

    if(chdir(dir) != 0)
    {
        printf("Can't start server in \"%s\", is directory existing?\n", dir);
        exit(1);
    }

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

int main(int argc, char **argv) {
    int pid;

    int server_port;
    char docroot[256];

    SocketInfo server_info;
    SocketInfo client_info;

    if(argc < 3)
    {
        printf("Usage: httpServ <documentRoot> <port>\n");
        exit(1);
    }
    printf("");
    strcpy(docroot, argv[1]);
    server_port = atoi(argv[2]); // NOLINT

    start_server(&server_info, docroot, server_port);
    printf("Server started in %s on port %d\n", docroot, server_port);
    listen(server_info.sock_fd, 5);
    for(;;)
    {
        size_t MAXLINE = 9001;
        char in[MAXLINE];
        client_info.addr_len = sizeof(client_info.addr);

        ssize_t n = recvfrom(server_info.sock_fd, in, MAXLINE, 0, (struct sockaddr*)& client_info.addr, (socklen_t *)&client_info.addr_len);

       printf("%s\n", in);

        if((pid = fork()) < 0)
        {
            printf("Failed to fork()\n");
            exit(1);
        }
        else if(pid == 0)
        {
            //NOTE: Hier sind wir definitiv im Kindprozess
            close(server_info.sock_fd);
            //handle_connection(&client_info);
            exit(0);
        } //NOTE: Hier nicht mehr!

        close(client_info.sock_fd);
    }
    return 0;
}
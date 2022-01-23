#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <unistd.h>
#include <pthread.h>
#include <cstdlib>

int LOBBY_SIZE = 5;
//gcc program.c -lpthread -Wall -o outfile Remember to link pthread bib

void *socketThread(void *arg)
{
    
        
}

int main()
{
    int serverSocket, newSocket;
    struct sockaddr_in serverAddr;
    struct sockaddr_storage serverStorage;
    socklen_t addr_size;

    //Create the socket.
    serverSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(1313);
    serverAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);

    //Bind the address struct to the socket
    bind(serverSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr));

    if (listen(serverSocket, 10) == 0)
        printf("Listening\n");
    else
        printf("Error\n");

    pthread_t thread_id, thread_1, thread_2;

    for (int i = 0; i < 2; i++)
    {
        //Accept call creates a new socket for the incoming connection
        addr_size = sizeof(serverStorage);
        newSocket = accept(serverSocket, (struct sockaddr *)&serverStorage, &addr_size);

        char size = LOBBY_SIZE + '0';

        /*if (pthread_create(&thread_id, NULL, socketThread, (void *)&newSocket) != 0)
        {
            printf("Failed to create thread.\n");
        }*/
        int n = send(newSocket, &size, sizeof(char), 0);
        printf("Send message: size: %d, content: %c.\n", n, size);

        //pthread_detach(thread_id);
    }
    close(newSocket);
    printf("Socket closed.\n");
    return 0;
}

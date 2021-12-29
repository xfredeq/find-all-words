#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <unistd.h>
#include <pthread.h>
//gcc program.c -lpthread -Wall -o outfile Remember to link pthread bib

void *socketThread(void *arg)
{
  int newSocket = *((int *)arg);
  int n;
  char client_message[256];
  for (;;)
  {
    strcpy(client_message, "");
    n = recv(newSocket, client_message, 256, 0);
    printf("%s\n", client_message);
    if (n < 1)
    {
      break;
    }

    char *message = (char*)malloc(sizeof(client_message));

    strcpy(message, client_message);

    send(newSocket, message, strlen(message), 0);

    memset(&client_message, 0, sizeof(client_message));

    free(message);
  }
  pthread_exit(NULL);
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
  serverAddr.sin_port = htons(1100);
  serverAddr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);

  //Bind the address struct to the socket
  bind(serverSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr));

  if (listen(serverSocket, 10) == 0)
    printf("Listening\n");
  else
    printf("Error\n");

  pthread_t thread_id, thread_1, thread_2;

  while (1)
  {
    //Accept call creates a new socket for the incoming connection
    addr_size = sizeof(serverStorage);
    newSocket = accept(serverSocket, (struct sockaddr *)&serverStorage, &addr_size);

    if (pthread_create(&thread_id, NULL, socketThread, (void *)&newSocket) != 0)
      printf("Failed to create thread\n");

    pthread_detach(thread_id);
  }
  return 0;
}

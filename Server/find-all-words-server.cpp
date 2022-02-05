#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <thread>
#include <signal.h>
#include <unordered_set>
#include <error.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/epoll.h>

int lobbySize;

int serverSocket;

std::unordered_set<int> clientFds;


void stop_server(int);
int readArgument(char *txt, bool type);
void setReuseAddr(int sock);
void acceptUsers();

using namespace std;
int main(int argc, char **argv)
{
    if (argc < 3)
        error(1, 0, "Need 2 arg (port, lobby size)");
    auto port = readArgument(argv[1], true);
    lobbySize = readArgument(argv[2], false);

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1)
        error(1, errno, "socket failed");

    signal(SIGINT, stop_server);
    signal(SIGPIPE, SIG_IGN);

    setReuseAddr(serverSocket);

    sockaddr_in serverAddr{.sin_family=AF_INET, .sin_port=htons((short) port), .sin_addr={INADDR_ANY}};
	int res = bind(serverSocket, (sockaddr*) &serverAddr, sizeof(serverAddr));
	if(res) error(1, errno, "bind failed");

	// enter listening mode
	res = listen(serverSocket, 1);
	if(res) error(1, errno, "listen failed");

    cout<<port<<" "<<lobbySize<<" "<<serverSocket<<endl;

    thread t1(acceptUsers);

    t1.join();

}

void stop_server(int)
{
    //for (int clientFd : clientFds)
     //   close(clientFd);
    close(serverSocket);
    printf("Closing server\n");
    exit(0);


}

void setReuseAddr(int sock){
	const int one = 1;
	int res = setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &one, sizeof(one));
	if(res) error(1,errno, "setsockopt(reuse addr) failed");
}

int readArgument(char *txt, bool type)
{
    char *ptr;
    auto arg = strtol(txt, &ptr, 10);
    if (type)
    {
        if (*ptr != 0 || arg < 1 || (arg > ((1 << 16) - 1)))
            error(1, 0, "illegal argument %s", txt);
        return arg;
    }
    else
    {
    }
    if (*ptr != 0 || arg < 2 || (arg > 8))
        error(1, 0, "illegal argument %s", txt);
    return arg;
}

void acceptUsers() {
while(true){
		// prepare placeholders for client address
		sockaddr_in clientAddr{0};
		socklen_t clientAddrSize = sizeof(clientAddr);

		// accept new connection
		auto clientFd = accept(serverSocket, (sockaddr*) &clientAddr, &clientAddrSize);
		if(clientFd == -1) error(1, errno, "accept failed");

		// add client to all clients set
		clientFds.insert(clientFd);

		// tell who has connected
		printf("new connection from: %s:%hu (fd: %d)\n", inet_ntoa(clientAddr.sin_addr), ntohs(clientAddr.sin_port), clientFd);
	}
}
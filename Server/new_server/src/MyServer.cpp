#include "MyServer.h"

int lobbySize;
int lobbyNumber;

int serverSocket;
int mainEpollFd;

unordered_set<Player *> freePlayers;
unordered_set<Lobby *> lobbies;

class : Handler
{
public:
    virtual void handleEvent(uint32_t events) override
    {
        cout << "handler" << endl;
        if (events & EPOLLIN)
        {
            sockaddr_in clientAddr{};
            socklen_t clientAddrSize = sizeof(clientAddr);

            auto clientFd = accept(serverSocket, (sockaddr *)&clientAddr, &clientAddrSize);
            if (clientFd == -1)
                error(1, errno, "accept failed");

            printf("new connection from: %s:%hu (fd: %d)\n", inet_ntoa(clientAddr.sin_addr), ntohs(clientAddr.sin_port), clientFd);

            Player *player = new Player(clientFd);
            player->startHandler();
            freePlayers.insert(player);
        }
        if (events & ~EPOLLIN)
        {
            error(0, errno, "Event %x on server socket", events);
            stop_server(SIGINT);
        }
    }
} serverHandler;


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

    sockaddr_in serverAddr{.sin_family = AF_INET, .sin_port = htons((short)port), .sin_addr = {INADDR_ANY}};
    int res = bind(serverSocket, (sockaddr *)&serverAddr, sizeof(serverAddr));
    if (res)
        error(1, errno, "bind failed");

    // enter listening mode
    res = listen(serverSocket, 1);
    if (res)
        error(1, errno, "listen failed");

    cout << port << " " << lobbySize << " " << serverSocket << endl;

    lobbyNumber = 1;

    mainEpollFd = epoll_create1(0);
    cout << "main epoll: " << mainEpollFd << endl;
    epoll_event ee{EPOLLIN, {.ptr = &serverHandler}};
    epoll_ctl(mainEpollFd, EPOLL_CTL_ADD, serverSocket, &ee);

    while (true)
    {
        if (-1 == epoll_wait(mainEpollFd, &ee, 1, -1) && errno != EINTR)
        {
            error(0, errno, "epoll_wait failed");
            stop_server(SIGINT);
        }
        if (ee.data.ptr == &serverHandler)
        {
            ((Handler *)ee.data.ptr)->handleEvent(ee.events);
        }
        cout << "event" << endl;
    }
}

void stop_server(int)
{
    for (auto player : freePlayers)
        close(player->fd());
    close(serverSocket);
    printf("Closing server\n");
    exit(0);
}

void setReuseAddr(int sock)
{
    const int one = 1;
    int res = setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &one, sizeof(one));
    if (res)
        error(1, errno, "setsockopt(reuse addr) failed");
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

void sendToAllBut(int fd, char *buffer, int count)
{
    auto it = freePlayers.begin();
    while (it != freePlayers.end())
    {
        Player *player = *it;
        it++;
        if (player->fd() != fd)
            player->write(buffer, count);
    }
}


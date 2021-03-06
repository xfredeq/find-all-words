#include "MyServer.h"

int lobbySize;
int lobbyNumber;

int roundsNumber = 3;
int roundDuration = 180;
int wordInterval = 3;

int serverSocket;
int mainEpollFd;

unordered_set<Player *> freePlayers;
unordered_set<Lobby *> lobbies;

class : Handler
{
public:
    virtual void handleEvent(uint32_t events) override
    {
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

    if (argc < 5)
        error(1, 0, "Need 4 arg (address, port, lobby size, roundsNumber)");

    auto port = readArgument(argv[2], false);
    lobbySize = readArgument(argv[3], true);
    roundsNumber = readArgument(argv[4], true);

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1)
        error(1, errno, "socket failed");

    signal(SIGINT, stop_server);
    signal(SIGPIPE, SIG_IGN);

    setReuseAddr(serverSocket);

    sockaddr_in serverAddr{.sin_family = AF_INET, .sin_port = htons((short)port), .sin_addr = {inet_addr(argv[1])}};
    int res = bind(serverSocket, (sockaddr *)&serverAddr, sizeof(serverAddr));
    if (res)
        error(1, errno, "bind failed");

    res = listen(serverSocket, 1);
    if (res)
        error(1, errno, "listen failed");


    lobbyNumber = 1;

    mainEpollFd = epoll_create1(0);
    epoll_event ee{EPOLLIN, {.ptr = &serverHandler}};
    epoll_ctl(mainEpollFd, EPOLL_CTL_ADD, serverSocket, &ee);

    while (true)
    {
        if (-1 == epoll_wait(mainEpollFd, &ee, 1, -1) && errno != EINTR)
        {
            error(0, errno, "epoll_wait failed");
            stop_server(SIGINT);
        }
        ((Handler *)ee.data.ptr)->handleEvent(ee.events);
    }
}

void stop_server(int)
{
    for (auto player : freePlayers)
        close(player->fd());
    close(serverSocket);
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
    if (!type)
    {
        if (*ptr != 0 || arg < 1 || (arg > ((1 << 16) - 1)))
            error(1, 0, "illegal argument %s", txt);
        return arg;
    }
    else
    {
        if (*ptr != 0 || arg < 2 || (arg > 9))
            error(1, 0, "illegal argument %s", txt);
        return arg;
    }
}

string constructLobbiesMessage()
{
    bool tmp = false;
    int count = 0;
    string m = "";
    for (auto lobby : lobbies)
    {
        if (!lobby->gameInProgress() && lobby->getPlayersNumber() < lobbySize)
        {
            count++;
            tmp = true;
            m += "NUMBER_" + to_string(lobby->getNumber()) + "_PLAYERS_" + to_string(lobby->getPlayersNumber()) + '_';
        }
    }
    string message = "LOBBIES_COUNT_" + to_string(count) + "_" + m;

    if (!tmp)
    {
        message += "null";
    }

    message += '\n';
    return message;
}

string constructLobbyMessage(Lobby *lobby)
{
    string message = "LOBBY_PLAYERS_" + to_string(lobby->getPlayersNumber()) + "_";
    for (auto player : lobby->getPlayers())
    {
        message += player->getNickname() + "_" + to_string(player->getVote()) + "_";
    }
    message += '\n';
    return message;
}

bool checkNicknameUniquness(char *nickname, Player *p)
{
    for (auto player : freePlayers)
    {
        if (strcmp(nickname, (char *)player->getNickname().c_str()) == 0 && player != p)
        {
            return false;
        }
    }
    for (auto lobby : lobbies)
    {
        for (auto player : lobby->getPlayers())
        {
            if (strcmp(nickname, (char *)player->getNickname().c_str()) == 0)
            {
                return false;
            }
        }
    }
    return true;
}

char getRandomChar()
{
    char c;
    mt19937 gen((std::random_device()()));
    uniform_int_distribution<uint8_t> letter(97, 122);

    c = char(letter(gen));

    return c;
}

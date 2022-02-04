#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <thread>

#include <list>
#include <signal.h>
#include <unordered_set>
#include <error.h>
#include <cstring>
#include <string.h>

#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/epoll.h>
using namespace std;

class Player;
class Lobby;

int lobbySize;
int lobbyNumber;

int serverSocket;
int mainEpollFd;

unordered_set<Player *> freePlayers;
unordered_set<Lobby *> lobbies;

void stop_server(int);
int readArgument(char *txt, bool type);
void setReuseAddr(int sock);
void acceptUsers();

void sendToAllBut(int fd, char *buffer, int count);

struct Handler
{
    virtual ~Handler() {}
    virtual void handleEvent(uint32_t events) = 0;
};

class Player : public Handler
{
    int _fd;
    string nickname;
    struct Buffer
    {
        Buffer() { data = (char *)malloc(len); }
        Buffer(const char *srcData, ssize_t srcLen) : len(srcLen)
        {
            data = (char *)malloc(len);
            memcpy(data, srcData, len);
        }
        ~Buffer() { free(data); }
        Buffer(const Buffer &) = delete;
        void doube()
        {
            len *= 2;
            data = (char *)realloc(data, len);
        }
        ssize_t remaining() { return len - pos; }
        char *dataPos() { return data + pos; }
        char *data;
        ssize_t len = 32;
        ssize_t pos = 0;
    };
    Buffer readBuffer;
    std::list<Buffer> dataToWrite;
    void waitForWrite(bool epollout)
    {
        epoll_event ee{EPOLLIN | EPOLLRDHUP | (epollout ? EPOLLOUT : 0), {.ptr = this}};
        epoll_ctl(mainEpollFd, EPOLL_CTL_MOD, _fd, &ee);
    }

public:
    Player(int fd) : _fd(fd)
    {
        epoll_event ee{EPOLLIN | EPOLLRDHUP, {.ptr = this}};
        epoll_ctl(mainEpollFd, EPOLL_CTL_ADD, _fd, &ee);
    }
    virtual ~Player()
    {
        epoll_ctl(mainEpollFd, EPOLL_CTL_DEL, _fd, nullptr);
        shutdown(_fd, SHUT_RDWR);
        close(_fd);
    }
    int fd() const { return _fd; }

    virtual void handleEvent(uint32_t events) override;

    void write(char *buffer, int count);

    void remove();

    void processRequests(int fd, char *buffer, int length);

    string getNickname();
};

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

            freePlayers.insert(new Player(clientFd));
        }
        if (events & ~EPOLLIN)
        {
            error(0, errno, "Event %x on server socket", events);
            stop_server(SIGINT);
        }
    }
} serverHandler;

class Lobby : Handler
{
private:
    int number;
    int lobbyEpollFd;
    unordered_set<Player *> lobbyPlayers;

public:
    Lobby();
    ~Lobby();

    int getNumber();
    int getPlayersNumber();

    void waitForEvents(epoll_event ee);

    virtual void handleEvent(uint32_t events) override;

    void addPlayer(Player *player);
    void removePlayer(Player *player);
};

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

void acceptUsers()
{
    /*while (true)
    {
        // prepare placeholders for player address
        sockaddr_in clientAddr{0};
        socklen_t clientAddrSize = sizeof(clientAddr);

        // accept new connection
        auto clientFd = accept(serverSocket, (sockaddr *)&clientAddr, &clientAddrSize);
        if (clientFd == -1)
            error(1, errno, "accept failed");

        // add player to all freePlayers set
        clientFds.insert(clientFd);

        // tell who has connected
        printf("new connection from: %s:%hu (fd: %d)\n", inet_ntoa(clientAddr.sin_addr), ntohs(clientAddr.sin_port), clientFd);
    } */
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

void Player::write(char *buffer, int count)
{
    if (dataToWrite.size() != 0)
    {
        dataToWrite.emplace_back(buffer, count);
        return;
    }
    int sent = send(_fd, buffer, count, MSG_DONTWAIT);
    if (sent == count)
        return;
    if (sent == -1)
    {
        if (errno != EWOULDBLOCK && errno != EAGAIN)
        {
            remove();
            return;
        }
        dataToWrite.emplace_back(buffer, count);
    }
    else
    {
        dataToWrite.emplace_back(buffer + sent, count - sent);
    }
    waitForWrite(true);
}

void Player::remove()
{
    printf("removing %d\n", _fd);
    freePlayers.erase(this);
    delete this;
}

string Player::getNickname()
{
    return this->nickname;
}

// "TYP_zadanie_dane_@"

void Player::processRequests(int fd, char *buffer, int length)
{
    string request(buffer, buffer + length);
    char *type = new char[10];
    char *subType = new char[20];

    int index = request.find("_");
    if (index < 1)
    {
        delete type;
        delete subType;
        return;
    }

    strcpy(type, request.substr(0, index).c_str());
    request = request.substr(index + 1);

    index = request.find("_");
    if (index < 1)
    {
        delete type;
        delete subType;
        return;
    }

    strcpy(subType, request.substr(0, index).c_str());
    request = request.substr(index + 1);
    if (strcmp("SET", type) == 0)
    {
        cout << type << endl;
        if (strcmp("NICKNAME", subType) == 0)
        {
            char *nickname = new char[20];

            index = request.find("_");
            if (index < 1)
            {
                delete nickname;
                delete type;
                delete subType;
                return;
            }

            strcpy(nickname, request.substr(0, index).c_str());
            this->nickname = string(nickname);

            string response = "RESPONSE_NICKNAME_" + this->nickname + "\n";
            this->write((char *)response.c_str(), response.length());
        }
    }
    else if (strcmp("GET", type) == 0)
    {
        cout << type << endl;
        if (strcmp("LOBBYSIZE", subType) == 0)
        {
            string response = "RESPONSE_LOBBYSIZE_" + to_string(lobbySize) + "\n";
            this->write((char *)response.c_str(), response.length());
        }
        else if (strcmp("LOBBIES", subType) == 0)
        {
            string response = "RESPONSE_LOBBIES_COUNT_" + to_string(lobbies.size()) + "_";
            for (auto lobby : lobbies)
            {
                response += "NUMBER_" + to_string(lobby->getNumber()) + "_PLAYERS_" + to_string(lobby->getPlayersNumber()) + '_';
            }

            response += '\n';
            this->write((char *)response.c_str(), response.length());
        }
        else if(strcmp("PLAYERS", subType) == 0)
        {
            string response = "RESPONSE_PLAYERS_COUNT_2_MATI_RED_SEBA_GREEN_\n";
            this->write((char *)response.c_str(), response.length());
        }
    }
    else if (strcmp("CREATE", type) == 0)
    {
        if (strcmp("LOBBY", subType) == 0)
        {
            Lobby *lobby = new Lobby();
            lobbies.insert(lobby);
            string response = "RESPONSE_CREATE_LOBBY_SUCCES_" + to_string(lobby->getNumber()) + '\n';
            lobby->addPlayer(this);
            this->write((char *)response.c_str(), response.length());
        }
    }


    delete type;
    delete subType;
}

void Player::handleEvent(uint32_t events)
{
    cout << "player" << endl;
    if (events & EPOLLIN)
    {
        ssize_t count = read(_fd, readBuffer.dataPos(), readBuffer.remaining());
        if (count <= 0)
            events |= EPOLLERR;
        else
        {
            readBuffer.pos += count;
            char *eol = (char *)memchr(readBuffer.data, '@', readBuffer.pos);
            if (eol == nullptr)
            {
                if (0 == readBuffer.remaining())
                    readBuffer.doube();
            }
            else
            {
                do
                {
                    int thismsglen = eol - readBuffer.data + 1;
                    string s(readBuffer.data, readBuffer.data + thismsglen);
                    cout << "length: " << thismsglen << endl;

                    cout << "s: " << s << endl;
                    cout << "buff: " << readBuffer.data << endl;

                    sendToAllBut(_fd, readBuffer.data, thismsglen);
                    processRequests(_fd, readBuffer.data, thismsglen);
                    auto nextmsgslen = readBuffer.pos - thismsglen;
                    memmove(readBuffer.data, eol + 1, nextmsgslen);
                    readBuffer.pos = nextmsgslen;
                } while ((eol = (char *)memchr(readBuffer.data, '@', readBuffer.pos)));
            }
        }
    }
    if (events & EPOLLOUT)
    {
        do
        {
            int remaining = dataToWrite.front().remaining();
            int sent = send(_fd, dataToWrite.front().data + dataToWrite.front().pos, remaining, MSG_DONTWAIT);
            if (sent == remaining)
            {
                dataToWrite.pop_front();
                if (0 == dataToWrite.size())
                {
                    waitForWrite(false);
                    break;
                }
                continue;
            }
            else if (sent == -1)
            {
                if (errno != EWOULDBLOCK && errno != EAGAIN)
                    events |= EPOLLERR;
            }
            else
                dataToWrite.front().pos += sent;
        } while (false);
    }
    if (events & ~(EPOLLIN | EPOLLOUT))
    {
        remove();
    }
}

Lobby::Lobby()
{
    this->lobbyEpollFd = epoll_create1(0);
    this->number = lobbyNumber++;

    /*epoll_event ee{EPOLLIN, {.fd = mainEpollFd}};
    epoll_ctl(this->lobbyEpollFd, EPOLL_CTL_ADD, mainEpollFd, &ee);

    thread waiter(&Lobby::waitForEvents, this, ee);
    waiter.detach();*/
}

Lobby::~Lobby()
{
}

void Lobby::waitForEvents(epoll_event ee)
{
    while (true)
    {
        if (-1 == epoll_wait(this->lobbyEpollFd, &ee, 1, -1) && errno != EINTR)
        {
            error(0, errno, "epoll_wait failed");
            stop_server(SIGINT);
        }
        ((Handler *)ee.data.ptr)->handleEvent(ee.events);
        cout << "event222" << endl;
    }
}

void Lobby::handleEvent(uint32_t events)
{
    if (events & EPOLLIN)
    {
        cout << "lobby event" << endl;
    }
}

void Lobby::addPlayer(Player *player)
{
    /*epoll_ctl(mainEpollFd, EPOLL_CTL_DEL, player->fd(), nullptr);
    epoll_event ee{EPOLLIN | EPOLLRDHUP, {.ptr = this}};
    epoll_ctl(this->lobbyEpollFd, EPOLL_CTL_ADD, player->fd(), &ee);*/

    freePlayers.erase(player);
    this->lobbyPlayers.insert(player);
}

void Lobby::removePlayer(Player *player)
{
    epoll_ctl(this->lobbyEpollFd, EPOLL_CTL_DEL, player->fd(), nullptr);
    epoll_event ee{EPOLLIN | EPOLLRDHUP, {.ptr = player}};
    epoll_ctl(mainEpollFd, EPOLL_CTL_ADD, player->fd(), &ee);

    this->lobbyPlayers.erase(player);
}

int Lobby::getNumber()
{
    return this->number;
}

int Lobby::getPlayersNumber()
{
    return this->lobbyPlayers.size();
}

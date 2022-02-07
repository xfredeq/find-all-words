#ifndef PLAYER_H
#define PLAYER_H

#include "Handler.h"
#include "MyServer.h"

using namespace std;

class Lobby;

class Player : public Handler
{
    int _fd;
    int playerEpollFd;
    string nickname;

    bool inLobby;
    bool inGame;
    bool votedStart;

    int points;
    
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
    list<Buffer> dataToWrite;

    thread handlingThread;

    Lobby *lobby;

    void waitForWrite(bool epollout);

public:
    Player(int fd);
    virtual ~Player();

    int fd() const { return _fd; }

    void waitForEvents();

    void startHandler();

    virtual void handleEvent(uint32_t events) override;

    void write(char *buffer, int count);

    void remove();

    void processRequests(int fd, char *buffer, int length);

    string getNickname();

    void changeLobbyState();

    void changeVoteState();
    bool getVote();
    void setVote(bool vote);

    void setGameState(bool state);

    void notifyAllWaiting();
    void notifyAllInLobby();

    int getPoints();
    void setPoints(int points);



};

#endif
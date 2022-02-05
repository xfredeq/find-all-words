#ifndef LOBBY_H
#define LOBBY_H

#include "MyServer.h"

using namespace std;

class Player;

class Lobby
{
private:
    int number;
    int lobbyEpollFd;
    unordered_set<Player *> lobbyPlayers;

public:
    Lobby(int);
    ~Lobby();

    int getNumber();
    int getPlayersNumber();

    void waitForEvents();

    void handleEvent(epoll_event ee);

    void addPlayer(Player *player);
    void removePlayer(Player *player);
};

#endif
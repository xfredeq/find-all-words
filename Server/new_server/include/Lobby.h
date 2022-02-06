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

    thread countdownThread;
    thread GameThread;

public:
    Lobby(int);
    ~Lobby();

    int getNumber();
    int getPlayersNumber();

    void waitForEvents();

    void handleEvent(epoll_event ee);

    void addPlayer(Player *player);
    void removePlayer(Player *player);

    unordered_set<Player *> getPlayers();

    bool checkGameStart();

    void startCountdownThread();

    void startGame();

    void game();

    void round();

    void calculateResults();

};

#endif
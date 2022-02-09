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

    bool gameStarted;

    unordered_set<Player *> lobbyPlayers;

    unordered_set<char *> guessedWords;

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

    bool gameInProgress();

    void game();

    void round();

    void finalizeGame();

    string checkWord(char *word, Player *player);

    int assesWord(int length);

    bool existsWord(char *w);

    string getRanking();

    void notifyAboutWord(char *word, bool success);

    void notifyAboutRanking();
};

#endif
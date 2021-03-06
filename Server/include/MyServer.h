#ifndef MYSERVER_H
#define MYSERVER_H

#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <thread>
#include <cmath>
#include <random>

#include <list>
#include <signal.h>
#include <unordered_set>
#include <error.h>
#include <cstring>
#include <string.h>
#include <fstream>

#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/epoll.h>

using namespace std;

extern int lobbySize;
extern int lobbyNumber;

extern int roundsNumber;
extern int roundDuration;
extern int wordInterval;

extern int serverSocket;
extern int mainEpollFd;

class Player;
class Lobby;

extern unordered_set<Player *> freePlayers;
extern unordered_set<Lobby *> lobbies;

#include "Lobby.h"
#include "Player.h"

void stop_server(int);

int readArgument(char *txt, bool type);

void setReuseAddr(int sock);

void acceptUsers();

string constructLobbiesMessage();

string constructLobbyMessage(Lobby *lobby);

bool checkNicknameUniquness(char *nickname, Player *p);

char getRandomChar();

#endif
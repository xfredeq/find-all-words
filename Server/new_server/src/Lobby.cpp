#include "Lobby.h"

Lobby::Lobby(int lobbyNumber)
{
    this->lobbyEpollFd = epoll_create1(0);
    this->number = lobbyNumber;
}

Lobby::~Lobby()
{
}

void Lobby::addPlayer(Player *player)
{
    player->changeLobbyState();
    freePlayers.erase(player);
    this->lobbyPlayers.insert(player);
}

void Lobby::removePlayer(Player *player)
{
    player->changeLobbyState();
    this->lobbyPlayers.erase(player);
    freePlayers.insert(player);
}

int Lobby::getNumber()
{
    return this->number;
}

int Lobby::getPlayersNumber()
{
    return this->lobbyPlayers.size();
}
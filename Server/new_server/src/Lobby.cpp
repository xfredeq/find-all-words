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
    player->notifyAllWaiting();
}

void Lobby::removePlayer(Player *player)
{
    player->changeLobbyState();
    this->lobbyPlayers.erase(player);
    freePlayers.insert(player);

    if (this->lobbyPlayers.size() == 0)
    {
        lobbies.erase(this);
        delete this;
    }
    player->notifyAllWaiting();
    
}


int Lobby::getNumber()
{
    return this->number;
}

int Lobby::getPlayersNumber()
{
    return this->lobbyPlayers.size();
}
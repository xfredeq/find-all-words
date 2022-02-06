#include "Lobby.h"

Lobby::Lobby(int lobbyNumber)
{
    this->lobbyEpollFd = epoll_create1(0);
    this->number = lobbyNumber;
}

Lobby::~Lobby()
{
    this->countdownThread.detach();

    this->GameThread.~thread();
}

void Lobby::addPlayer(Player *player)
{
    player->changeLobbyState();
    freePlayers.erase(player);
    this->lobbyPlayers.insert(player);
    player->notifyAllInLobby();
    player->notifyAllWaiting();
}

void Lobby::removePlayer(Player *player)
{
    player->changeLobbyState();
    player->setVote(false);
    this->lobbyPlayers.erase(player);
    freePlayers.insert(player);

    if (this->lobbyPlayers.size() == 0)
    {
        lobbies.erase(this);
        delete this;
        player->notifyAllWaiting();
        return;
    }

    player->notifyAllInLobby();
    player->notifyAllWaiting();
}

bool Lobby::checkGameStart()
{
    int count = 0;
    int size = this->lobbyPlayers.size();
    if (size < 2)
    {
        return false;
    }

    for (auto player : this->lobbyPlayers)
    {
        if (player->getVote())
        {
            count++;
        }
    }
    return count > size / 2;
}

void Lobby::startGame()
{
    cout << "CD THREAD: " << this_thread::get_id() << endl;
    string message = "NOTIFICATION_START_COUNTDOWN_10\n";
    for (auto player : this->lobbyPlayers)
    {
        player->write((char *)message.c_str(), message.size());
        player->changeGameState();
    }
    sleep(10);
    message = "NOTIFICATION_START_GAME" + to_string(roundDuration) +"\n";
    for (auto player : this->lobbyPlayers)
    {
        player->write((char *)message.c_str(), message.size());
    }
    this->GameThread = thread(&Lobby::game, this);
    this->GameThread.detach();
}

void Lobby::startCountdownThread()
{
    this->countdownThread = thread(&Lobby::startGame, this);
}

void Lobby::game()
{
    for (int i = 0; i < roundsNumber; i++)
    {
        thread(&Lobby::round, this).join();
    }

    this->calculateResults();
}

void Lobby::round()
{
    for (int i = 0; i < roundDuration; i += wordInterval)
    {
        char c = getRandomChar();
        cout << i << " " << c << endl;
        string notification = "NOTIFICATION_LETTER_" + to_string(c) + "\n";
        for (auto player : this->lobbyPlayers)
        {
            player->write((char *)notification.c_str(), notification.size());
        }
        sleep(wordInterval);
    }
}

void Lobby::calculateResults()
{
}

int Lobby::getNumber()
{
    return this->number;
}

int Lobby::getPlayersNumber()
{
    return this->lobbyPlayers.size();
}

unordered_set<Player *> Lobby::getPlayers()
{
    return this->lobbyPlayers;
}

#include "Lobby.h"

Lobby::Lobby(int lobbyNumber)
{
    this->lobbyEpollFd = epoll_create1(0);
    this->number = lobbyNumber;
    this->gameStarted = false;
}

Lobby::~Lobby()
{
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
    player->setGameState(false);
    player->setPoints(0);
    this->lobbyPlayers.erase(player);
    freePlayers.insert(player);

    if (this->lobbyPlayers.size() == 0)
    {
        lobbies.erase(this);

        player->notifyAllWaiting();
        delete this;
    }
    else
    {
        player->notifyAllInLobby();
        player->notifyAllWaiting();
    }
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
    this->gameStarted = true;
    return count > size / 2;
}

void Lobby::startGame()
{
    string message = "NOTIFICATION_START_COUNTDOWN_10\n";
    for (auto player : this->lobbyPlayers)
    {
        player->write((char *)message.c_str(), message.size());
        player->setGameState(true);
    }
    sleep(10);
    if (this->lobbyPlayers.size() == 1)
    {
        Player *winner = (Player *)*this->lobbyPlayers.begin();

        string notification = "NOTIFICATION_COUNTDOWN_LEAVE\n";

        winner->write((char *)notification.c_str(), notification.size());
        //this->removePlayer(winner);
        return;
    }

    message = "NOTIFICATION_START_GAME_" + to_string(roundDuration) + "\n";
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
    this->countdownThread.detach();
}

void Lobby::game()
{
    for (int i = 0; i < roundsNumber; i++)
    {
        string notification = "NOTIFICATION_GAME_ROUND_" + to_string(i + 1) + "_STARTS\n";
        for (auto player : this->lobbyPlayers)
        {
            player->write((char *)notification.c_str(), notification.size());
        }

        thread(&Lobby::round, this).join();
        if (this->lobbyPlayers.size() == 1)
        {
            return;
        }

        notification = "NOTIFICATION_GAME_ROUND_" + to_string(i + 1) + "_FINISHED\n";
        for (auto player : this->lobbyPlayers)
        {
            player->write((char *)notification.c_str(), notification.size());
        }
        sleep(5);
    }

    this->finalizeGame();
}

void Lobby::round()
{
    string notification;
    for (int i = 0; i <= roundDuration; i += wordInterval)
    {
        if (this->lobbyPlayers.size() == 1)
        {
            Player *winner = (Player *)*this->lobbyPlayers.begin();

            notification = "NOTIFICATION_GAME_VICTORY_PLACE_1_POINTS_" + to_string(winner->getPoints()) + "\n";

            winner->write((char *)notification.c_str(), notification.size());
            return;
        }
        char c = getRandomChar();
        notification = "NOTIFICATION_GAME_LETTER_" + to_string((char)c) + "\n";
        for (auto player : this->lobbyPlayers)
        {
            player->write((char *)notification.c_str(), notification.size());
        }
        sleep(wordInterval);
    }
}

void Lobby::finalizeGame()
{
    string notification = "NOTIFICATION_GAME_FINISHED\n";
    for (auto player : this->lobbyPlayers)
    {
        player->write((char *)notification.c_str(), notification.size());
    }
    this->notifyAboutRanking();
}

string Lobby::checkWord(char *word, Player *player)
{
    int asses = this->assesWord(strlen(word));
    bool unique = true;
    for (auto w : this->guessedWords)
    {
        if (strcmp(word, w) == 0)
        {
            unique = false;
            break;
        }
    }
    string result;
    if (!unique)
    {
        player->setPoints(player->getPoints() - asses);
        this->notifyAboutWord(word, false);
        this->notifyAboutRanking();
        return "FAILURE_" + to_string(asses) + "\n";
    }
    else
    {
        if (this->existsWord(word))
        {
            this->guessedWords.insert(word);
            player->setPoints(player->getPoints() + asses);
            this->notifyAboutWord(word, true);
            this->notifyAboutRanking();
            return "SUCCESS_" + to_string(asses) + "\n";
        }
        else
        {
            player->setPoints(player->getPoints() - strlen(word));
            this->notifyAboutWord(word, false);
            this->notifyAboutRanking();
            return "FAILURE_" + to_string(strlen(word)) + "\n";
        }
    }
}

int Lobby::assesWord(int length)
{
    if (length > 1)
        return ((length * (log(length) / log(4))) + 1);
    return 0;
}

bool Lobby::existsWord(char *w)
{
    string word(w);
    FILE *p;

    char result[100] = {0};

    string res = "grep ^" + word + "$ /usr/share/dict/words";
    p = popen(res.c_str(), "r");
    fgets(result, sizeof(word), p);
    pclose(p);
    result[strlen(result) - 1] = '\0';
    return strcmp(w, result) == 0;
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

bool Lobby::gameInProgress()
{
    return this->gameStarted;
}

string Lobby::getRanking()
{
    string response = "NOTIFICATION_GAME_PLAYERS_" + to_string(this->lobbyPlayers.size()) + "_";
    for (auto player : this->lobbyPlayers)
    {
        response += player->getNickname() + "_" + to_string(player->getPoints()) + "_";
    }
    response += "\n";
    return response;
}

void Lobby::notifyAboutWord(char *word, bool success)
{
    string notification = "NOTIFICATION_GAME_WORD_";
    if (success)
        notification += "SUCCESS_";
    else
        notification += "FAILURE_";

    notification += word;
    notification += "\n";

    for (auto player : this->lobbyPlayers)
    {
        player->write((char *)notification.c_str(), notification.length());
    }
}

void Lobby::notifyAboutRanking()
{
    string notification = this->getRanking();

    for (auto player : this->lobbyPlayers)
    {
        player->write((char *)notification.c_str(), notification.length());
    }
}
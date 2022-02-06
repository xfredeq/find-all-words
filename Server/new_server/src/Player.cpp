#include "Player.h"

Player::Player(int fd) : _fd(fd), handlingThread()
{
    epoll_ctl(mainEpollFd, EPOLL_CTL_DEL, _fd, nullptr);

    this->playerEpollFd = epoll_create1(0);
    this->lobby = nullptr;
    this->inLobby = false;
    this->inGame = false;
    this->votedStart = false;
    this->points = 0;
}

Player::~Player()
{
    this->handlingThread.detach();
    this->handlingThread.~thread();
    epoll_ctl(mainEpollFd, EPOLL_CTL_DEL, _fd, nullptr);
    epoll_ctl(this->playerEpollFd, EPOLL_CTL_DEL, _fd, nullptr);
    shutdown(_fd, SHUT_RDWR);
    close(_fd);
    close(this->playerEpollFd);
}

void Player::write(char *buffer, int count)
{
    if (dataToWrite.size() != 0)
    {
        dataToWrite.emplace_back(buffer, count);
        return;
    }
    cout << "WRITE: " << this_thread::get_id() << endl;
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
    if (this->inLobby)
    {
        this->lobby->removePlayer(this);
    }
    freePlayers.erase(this);

    // this->handlingThread.~thread();
    delete this;
}

string Player::getNickname()
{
    return this->nickname;
}

void Player::changeLobbyState()
{
    this->inLobby = !this->inLobby;
}

void Player::changeVoteState()
{
    this->votedStart = !this->votedStart;
}

bool Player::getVote()
{
    return this->votedStart;
}

void Player::setVote(bool vote)
{
    this->votedStart = vote;
}

void Player::changeGameState()
{
    this->inGame = !this->inGame;
}

void Player::waitForEvents()
{
    epoll_event ee{EPOLLIN | EPOLLRDHUP, {.ptr = this}};
    epoll_ctl(this->playerEpollFd, EPOLL_CTL_ADD, _fd, &ee);

    while (true)
    {
        if (-1 == epoll_wait(this->playerEpollFd, &ee, 1, -1) && errno != EINTR)
        {
            return;
        }

        if (ee.data.ptr == this)
        {
            cout << "WAITER: " << this_thread::get_id() << endl;
            ((Handler *)ee.data.ptr)->handleEvent(ee.events);
        }
        cout << " player event" << endl;
    }
}

void Player::startHandler()
{
    handlingThread = thread(&Player::waitForEvents, this);
}

void Player::handleEvent(uint32_t events)
{
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

                    cout << "s: " << s << endl;

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

void Player::processRequests(int fd, char *buffer, int length)
{
    // "TYP_zadanie_dane_@"
    cout << "PROCESS: " << this_thread::get_id() << endl;

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

    if (!this->inGame) // PLAYER OUT OF GAME
    {
        if (!this->inLobby) // PLAYER OUT OF LOBBY
        {
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
                    string response = "RESPONSE_NICKNAME_";
                    this->nickname = string(nickname);
                    if (checkNicknameUniquness(nickname, this))
                    {
                        response += "SUCCESS_" + this->nickname + "\n";
                    }
                    else
                    {
                        response += "FAILURE_" + this->nickname + "\n";
                    }
                    this->write((char *)response.c_str(), response.length());
                }
            }
            else if (strcmp("GET", type) == 0)
            {
                if (strcmp("LOBBYSIZE", subType) == 0)
                {
                    string response = "RESPONSE_LOBBYSIZE_" + to_string(lobbySize) + "\n";
                    this->write((char *)response.c_str(), response.length());
                }
                else if (strcmp("LOBBIES", subType) == 0)
                {
                    string response = "RESPONSE_" + constructLobbiesMessage();

                    this->write((char *)response.c_str(), response.length());
                }
            }
            else if (strcmp("LOBBY", type) == 0)
            {
                if (strcmp("CREATE", subType) == 0)
                {
                    Lobby *lobby = new Lobby(lobbyNumber++);
                    lobbies.insert(lobby);
                    string response = "RESPONSE_LOBBY_CREATE_SUCCESS_" + to_string(lobby->getNumber()) + '\n';

                    this->lobby = lobby;
                    lobby->addPlayer(this);
                    this->write((char *)response.c_str(), response.length());
                }
                else if (strcmp("JOIN", subType) == 0)
                {
                    int number;
                    char *nr = new char[10];

                    index = request.find("_");
                    if (index < 1)
                    {
                        delete nr;
                        delete type;
                        delete subType;
                        return;
                    }

                    strcpy(nr, request.substr(0, index).c_str());

                    number = atoi(nr);
                    Lobby *lobby = nullptr;

                    for (auto l : lobbies)
                    {
                        if (l->getNumber() == number)
                        {
                            lobby = l;
                            break;
                        }
                    }
                    string response;

                    if (lobby != nullptr)
                    {
                        response = "RESPONSE_LOBBY_JOIN_SUCCESS_" + to_string(number) + "\n";
                        this->lobby = lobby;
                        this->lobby->addPlayer(this);
                        this->write((char *)response.c_str(), response.length());
                    }
                    else
                    {
                        response = "RESPONSE_LOBBY_JOIN_FAILURE_" + to_string(number) + "\n";
                        this->write((char *)response.c_str(), response.length());
                    }
                }
            }
            else
            {
                string response = "RESPONSE_BAD_REQUEST\n";
                this->write((char *)response.c_str(), response.length());
            }
        }
        else // PLAYER IN LOBBY
        {
            if (strcmp("SUBMIT", type) == 0)
            {
                if (strcmp("WORD", subType) == 0)
                {
                    string response = "RESPONSE_SUBMIT_WORD_SUCCESS\n";
                    this->write((char *)response.c_str(), response.length());
                }
            }

            else if (strcmp("LOBBY", type) == 0)
            {
                if (strcmp("LEAVE", subType) == 0)
                {
                    string response = "RESPONSE_LOBBY_LEAVE_SUCCESS_" + to_string(this->lobby->getNumber()) + "\n";

                    this->write((char *)response.c_str(), response.length());
                    cout << "leave sent: " << response << endl;

                    this->lobby->removePlayer(this);
                    this->lobby = nullptr;
                }
                else if (strcmp("VOTE", subType) == 0)
                {
                    this->changeVoteState();
                    string response = "RESPONSE_LOBBY_VOTE_SUCCESS_" + to_string(this->votedStart) + "\n";
                    this->write((char *)response.c_str(), response.length());
                    this->notifyAllInLobby();

                    if (this->lobby->checkGameStart())
                    {
                        cout << "GAME STARTS" << endl;
                        this->lobby->startCountdownThread();
                    }
                }
                else if (strcmp("PLAYERS", subType) == 0)
                {
                    string response = "NOTIFICATION_" + constructLobbyMessage(this->lobby);
                    this->write((char *)response.c_str(), response.length());
                }

                else
                {
                    string response = "RESPONSE_BAD_REQUEST\n";
                    this->write((char *)response.c_str(), response.length());
                }
            }
            else
            {
                string response = "RESPONSE_BAD_REQUEST\n";
                this->write((char *)response.c_str(), response.length());
            }
        }
    }
    else // PLAYER IN GAME
    {
    }

    delete type;
    delete subType;
}

void Player::notifyAllWaiting()
{
    string notification = "NOTIFICATION_" + constructLobbiesMessage();
    for (auto player : freePlayers)
    {
        player->write((char *)notification.c_str(), notification.length());
    }
}

void Player::notifyAllInLobby()
{
    string notification = "NOTIFICATION_" + constructLobbyMessage(this->lobby);
    for (auto player : this->lobby->getPlayers())
    {
        player->write((char *)notification.c_str(), notification.length());
    }
}

void Player::waitForWrite(bool epollout)
{
    epoll_event ee{EPOLLIN | EPOLLRDHUP | (epollout ? EPOLLOUT : 0), {.ptr = this}};
    epoll_ctl(mainEpollFd, EPOLL_CTL_MOD, _fd, &ee);
}

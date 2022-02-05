#ifndef BUFFER_H
#define BUFFER_H

#include "MyServer.h"

struct Buffer
{
    Buffer();
    Buffer(const char *srcData, ssize_t srcLen);
    ~Buffer();
    Buffer(const Buffer &) = delete;
    void doube();
    ssize_t remaining() { return len - pos; }
    char *dataPos() { return data + pos; }
    char *data;
    ssize_t len = 32;
    ssize_t pos = 0;
};

#endif
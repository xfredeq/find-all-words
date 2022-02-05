#include "Buffer.h"

Buffer::Buffer()
{
    data = (char *)malloc(len);
}

Buffer::Buffer(const char *srcData, ssize_t srcLen) : len(srcLen)
{
    data = (char *)malloc(len);
    memcpy(data, srcData, len);
}

Buffer::~Buffer()
{
    free(data);
}

void Buffer::doube()
{
    len *= 2;
    data = (char *)realloc(data, len);
}



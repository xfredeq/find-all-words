#ifndef HANDLER_H
#define HANDLER_H

#include <iostream>


struct Handler
{
    virtual ~Handler() {}
    virtual void handleEvent(uint32_t events) = 0;
};

#endif
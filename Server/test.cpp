#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <stdlib.h>


using namespace std;

int main(int argc, char **argv){

    string grep = "grep -i ^";
    string dict = "$ /usr/share/dict/words > resp.txt";
    string res = grep + argv[1] + dict;
    system(res.c_str());

    return 0;
}
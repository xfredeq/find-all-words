#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <stdlib.h>
#include <cmath>
#include <fstream>
#include <cstring>

using namespace std;

int assess(int length){
    if(length > 1)return ((length*(log(length)/log(4))) + 1);
        return 0;
}

int check_word(string w){
    FILE *p;
    char word[100];
    int score = 0;
    string grep = "grep -i ^";
    string dict = "$ /usr/share/dict/words";
    string res = grep + w + dict;
    p = popen(res.c_str(), "r");
    fgets(word, sizeof(word), p);
    word[strlen(word)-1]='\0';

    pclose(p);
    score = assess(strlen(word));
    memset(word, 0, strlen(word));
    return score;
}

int main(int argc, char **argv){
    string w="weather";
    printf("%d\n", check_word(w));

}
#include <bits/stdc++.h>
#include <ctype.h>

void normalize_file(char *buf) {
    if (!(*buf))
        return;

    char *olds = buf, *news = buf;
    char *spacestart = buf;
    bool spaces = false;
    for (; *olds; olds++) {
        if (isspace(*olds)) {
            if (*olds == '\n') {
                if (spaces) {
                    news = spacestart;
                    spaces = false;
                }
            }
            else if (!spaces) {
                spacestart = news;
                spaces = true;
            }
         }
        else
            spaces = false;

        *news = *olds;
        news++;
    }

    if (*(news - 1) != '\n') {
        if (spaces) {
            news = spacestart;
        }
        *news = '\n';
        news++;
    }
    
    *news = 0;
    return;
}
/*
int main() {
    char* data = new char[4];
    strcpy(data, " ");
    for (size_t i = 0; i < 4; i++)
        printf("%d\n", data[i]);
    normalize_file(data);
    for (size_t i = 0; i < 4; i++)
        printf("%d\n", data[i]);

    printf("\\n = %d, space = %d", '\n', ' ');
    return 0;
}*/

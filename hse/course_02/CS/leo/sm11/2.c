#include <stdlib.h>
#include <stdio.h>
#include <time.h>

int read_label(FILE *stream, struct tm *label) {
    if (fscanf(stream, "%d/%d/%d", &(label->tm_year), &(label->tm_mon), &(label->tm_mday)) != 3)
        return -1;
    if (fscanf(stream, "%d:%d:%d", &(label->tm_hour), &(label->tm_min), &(label->tm_sec)) != 3)
        return -1;
    label->tm_year -= 1900;
    label->tm_mon--;
    label->tm_isdst = -1;
    return 0;
}

int main(int argc, char **argv) {
    FILE *stream = fopen(argv[1], "r");
    struct tm label1, label2;
    if (read_label(stream, &label1) == -1) {
        fclose(stream);
        return 0;
    }
    
    time_t t1 = mktime(&label1);
    while (read_label(stream, &label2) != -1) {
        time_t t2 = mktime(&label2);
        printf("%ld\n", t2 - t1);
        t1 = t2;
    }

    fclose(stream);
    return 0;
}

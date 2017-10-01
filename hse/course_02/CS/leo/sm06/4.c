#include <stdlib.h>
#include <stdio.h>
#include <string.h>


int __utf8_bytes_reserved(char s) {
    union {
        char ss;
        unsigned char us;
    };
    ss = s;

    int result = 0;
    for (int i = 7; (us >> i) & 1; i-- ) {
        if (i < 4)
            return -1;
        result++;
    }
    
    switch (result) {
        case 0: {
            if (s)
                return 1;
            else
                return -1;
            break;
        }
        case 1: {
            return -1;
            break;
        }
        default: {
            return result;
        }
    }
}

int __utf8_check(int reserved, const char *bytes) {
    union {
        char ss;
        unsigned char us;
    };
    bytes++;
    for (int i = 1; i < reserved; bytes++, i++) {
        ss = *bytes;
        if (!us)
            return 0;
        if ((us >> 6) != 2)
            return 0;
    }
    
    return 1;
}

size_t utf8_len(const char *str) {
    int reserved = 0;
    size_t len = 0;
    while (*str) {
        reserved = __utf8_bytes_reserved(*str);
        switch (reserved) {
            case -1: {
                str++;
                break;
            }
            case 1: {
                len++;
                str++;
                break;
            }
            default: {
                if (!__utf8_check(reserved, str))
                    str++;
                else {
                    len++;
                    str += reserved;
                }
            }
        }
    }
    return len;
}

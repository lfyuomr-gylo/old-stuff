#include <bits/stdc++.h>

// ajknvsdfjvhnskjhk
// kdjbnkvsdjkhsfdjhk
void normalize_path(char *buf) {
    char *newcur = buf, *oldcur = buf;
    bool slflag = false;
    for (;*oldcur; oldcur++) {
        if (*oldcur == '/') {
            if (slflag)
                continue;
            slflag = true;
        }
        else {
            slflag = false;
        }
        *newcur = *oldcur;
        newcur++;
    }
    *newcur = 0;
}
/*
int main() {
    return 0;
}*/

#include <iostream>

void normalize_path(char *buf) {
	char *a = buf, *b = buf;
	while (*a != '\0') {
		++a;
		if (*a != '/' || *b != '/')
			++b;
		*b = *a;
	}
}

/*
int main(int argc, char ** argv) {
    char* a = new char[6];
    a[0] = '/';
    a[1] = '/';
    a[2] = '/';
    a[3] = '/';
    a[4] = '/';
    a[5] = '\0';

    normalize_path(a);

	size_t i = 0;
	while (a[i] != '\0') {
		std::cout << a[i];
		++i;
	}

    return 0;
}
*/

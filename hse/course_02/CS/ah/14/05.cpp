#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

int main() {
	for (int i = 0; i < 3; ++i) {
		long n;
		std::cin >> n;
		// doing multiple `cin`s at the same time is UB.
		// see http://stackoverflow.com/a/18844862
		if (fork() == 0) {
			std::cout << n * n << std::endl;
			exit(0);
		}
	}
	for (int i = 0; i < 3; ++i) {
		wait(NULL);
	}
	return 0;
}
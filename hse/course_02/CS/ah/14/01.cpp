#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

void fc(int i, int n) {
	if (i == n) {
		std::cout << i << "\n";
		return;
	}

	pid_t pid = fork();
	int status;

	if (pid == 0) {
		std::cout << i << " ";
		fflush(stdout);
		fc(i + 1, n);
	} else {
		waitpid(pid, &status, 0);
	}
}

int main() {
	int n;
	std::cin >> n;
	fc(1, n);
	return 0;
}
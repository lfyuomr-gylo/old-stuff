#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

void fc(int depth) {
	int n;
	if (std::cin >> n) {
		pid_t pid = fork();
		if (pid == 0) {
			fc(depth + 1);
		} else if (pid > 0) {
			int status;
			waitpid(pid, &status, 0);
			if (WIFEXITED(status) && WEXITSTATUS(status)) {
				exit(depth ? WEXITSTATUS(status) : 0);
			}
			std::cout << n << std::endl;
			exit(0);
		} else {
			std::cout << -1 << std::endl;
			exit(depth ? 1 : 0);
		}
	}
}

int main() {
	fc(0);
	return 0;
}

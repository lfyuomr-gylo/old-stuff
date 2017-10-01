#include <stdlib.h>
#include <sched.h>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>

sigset_t mask, mask2;

void __SIGINT(int sig) {}

int main() {
	sigemptyset(&mask);
	sigaddset(&mask, SIGINT);
	sigemptyset(&mask2);

	signal(SIGINT, __SIGINT);

	int signc = 0;

	printf("%d\n", getpid());
	fflush(stdout);

	while (1) {
		sigsuspend(&mask2);
		if (signc == 4)
			break;
		printf("%d\n", signc++);
		fflush(stdout);
	}

	return 0;
}

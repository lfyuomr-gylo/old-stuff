#include <stdlib.h>
#include <sched.h>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>

volatile unsigned signc = 0;

void __SIGINT(int sig) {
	if (signc == 4)
		exit(0);
	printf("%d\n", signc++);
	fflush(stdout);
}

int main() {
	signal(SIGINT, __SIGINT);
	printf("%d\n", getpid());
	fflush(stdout);
	while(1)
		pause();
	return 0;
}

#include <stdlib.h>
#include <sched.h>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>

volatile unsigned jt = 0;

void __SIGUSR1(int sig) {
	jt = 0;
}

void __SIGUSR2(int sig) {
	jt = 1;
}
int main() {
	signal(SIGUSR1, __SIGUSR1);
	signal(SIGUSR2, __SIGUSR2);

	printf("%d\n", getpid());
	fflush(stdout);

	long i;
	while (scanf("%ld", &i) == 1) {
		if (jt) {
			printf("%ld\n", i * i);
			fflush(stdout);
		} else {
			printf("%ld\n", -i);
			fflush(stdout);
		}
	}

	return 0;
}

//
// Created by leo on 12.12.15.
//
#include <bits/stdc++.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <wait.h>
#include <signal.h>
#include <pthread.h>

std::atomic<unsigned long long> attempts(0);
std::atomic<unsigned long long> hits(0);
std::vector<std::thread> threads;
unsigned long long iterations;

void function_which_chooses_random_points_in_square_with_size_equal_to_RANDMAX_and_counts_amount_of_hits_into_inscrtbed_circle(
        uint32_t id) {
    unsigned long long my_attempts = 0, my_hits = 0;

    while (my_attempts < iterations) {
        int64_t x = rand_r(&id), y = rand_r(&id);
        if (x * x + y * y <= (int64_t)RAND_MAX * RAND_MAX)
            my_hits++;
        my_attempts++;
    }

    attempts += my_attempts;
    hits += my_hits;
}

int main(int argc, char **argv) {
    unsigned long long tnum;
    sscanf(argv[1], "%llu", &tnum);
    sscanf(argv[2], "%llu", &iterations);


    for (uint32_t i = 0; i < tnum; i++)
        threads.push_back(std::thread(
                function_which_chooses_random_points_in_square_with_size_equal_to_RANDMAX_and_counts_amount_of_hits_into_inscrtbed_circle,
                i));

    for (auto &item : threads) {
        item.join();
    }

    double pi = 4. * hits / attempts;
    printf("%.5f", pi);
    return 0;
}
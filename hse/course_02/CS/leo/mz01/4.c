#include <stdlib.h>
#include <stdio.h>

void merge(double data[], int l, int r, int m) {
    int lcur = 0, rcur = 0, lsize = m - l, rsize = r - m;
    double* lpart = (double*) malloc(sizeof(double) * lsize);
    double* rpart = (double*) malloc(sizeof(double) * rsize);
    for (int i = 0; i < lsize; i++)
        lpart[i] = data[l + i];
    for (int i = 0; i < rsize; i++)
        rpart[i] = data[m + i];

    while (lcur < lsize && rcur < rsize) {
        if (lpart[lcur] > rpart[rcur]) {
            data[l + lcur + rcur] = lpart[lcur];
            lcur++;
        }
        else {
            data[l + lcur + rcur] = rpart[rcur];
            rcur++;
        }
    }
    for (; lcur < lsize; lcur++)
        data[l + lcur + rcur] = lpart[lcur];
    for (; rcur < rsize; rcur++)
        data[l + lcur + rcur] = rpart[rcur];
    
    free(lpart);
    free(rpart);
}

void merge_sort_rec(double data[], int l, int r) {
    if (r - l <= 1)
        return;

    int m = (l + r) / 2;
    merge_sort_rec(data, l, m);
    merge_sort_rec(data, m, r);
    merge(data, l, r, m);
}

void msort(double data[], int size) {
    merge_sort_rec(data, 0, size);
}

int main() {
    int size;
    scanf("%d", &size);
    double* numbers = (double*) malloc(sizeof(double) * size);
    double* sorted = (double*) malloc(sizeof(double) * size);

    for (int i = 0; i < size; i++) {
        scanf("%lf", numbers + i);
        sorted[i] = numbers[i];
    }

    msort(sorted, size);

    for (int i = 0; i < size; i++)
        printf("%d %.10g %.10g\n", i + 1, numbers[i], sorted[i]);

    free(numbers);
    free(sorted);
    return 0;
}

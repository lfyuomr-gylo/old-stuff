unsigned satsum(unsigned v1, unsigned v2) {
    unsigned sum = v1 + v2;
    if (sum < v1 || sum < v2) {
        return ~0;
    }
    return sum;
}

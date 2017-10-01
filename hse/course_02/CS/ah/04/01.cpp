unsigned satsum(unsigned v1, unsigned v2) {
    return v1 + v2 >= v1 && v1 + v2 >= v2 ? v1 + v2 : ~(v1 ^ v1);
}

#include <bits/stdc++.h>

size_t frc_round(uint64_t &frc) {
    size_t overflow = 0u;

    if (frc >> 47 & 1) {
        frc >>= 1;
        overflow++;
    }
    frc &= (uint64_t(1) << 46) - 1;
    frc >>= 22;
    if (frc & 1)
        frc++;
    frc >>= 1;

    if (frc >> 23 & 1) {
        frc >>= 1;
        overflow++;
    }

    return overflow;
}

void fpmulf(const void *src1, const void *src2, void *dst) {
    union {
        struct {
            uint32_t f: 23;
            uint32_t e: 8;
            uint32_t s: 1;
        };
        float num;
    }f1, f2, result;

    f1.num = *((float*) src1);
    f2.num = *((float*) src2);

    uint64_t exp = f1.e + f2.e - 127;
    uint64_t sgn = f1.s ^ f2.s;

    uint64_t frc1 = f1.f, frc2 = f2.f;
    frc1 |= uint64_t(1) << 23;
    frc2 |= uint64_t(1) << 23;
    uint64_t frc = frc1 * frc2;
    exp += frc_round(frc);

    result.s = sgn;
    result.e = exp;
    result.f = frc;
    *((float*) dst) = result.num;
}

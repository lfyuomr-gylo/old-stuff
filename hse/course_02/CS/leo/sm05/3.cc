#include <cstdint>

union FloatBitsAccess {
    float f;
    uint32_t i;
};

bool GetFloatSgn(float f) {
    FloatBitsAccess tr;
    tr.f = f;
    return tr.i >> 31;
}

uint32_t GetFloatExp(float f) {
    FloatBitsAccess tr;
    tr.f = f;
    return (tr.i >> 23) & ((1 << 8) - 1);
}

uint32_t GetFloatFrac(float f) {
    FloatBitsAccess tr;
    tr.f = f;
    return (tr.i & ((1 << 23) - 1));
}

FPClass fpclassf(float value, bool &psign) {
    uint32_t sgn = GetFloatSgn(value),
             exp = GetFloatExp(value),
             frc = GetFloatFrac(value);

    psign = sgn;

    if (!exp && !frc)
        return FPClass::ZERO;

    if (!exp && frc)
        return FPClass::DENORMALIZED;

    if (!frc && exp == (1 << 8) - 1)
        return FPClass::INF;

    if (frc && exp == 0b11111111) {
        psign = 0;
        return FPClass::NAN;
    }

    return FPClass::NORMALIZED;
}

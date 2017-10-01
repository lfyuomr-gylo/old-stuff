package logic

import (
    "fmt"
    "bytes"
)

func Factorize(number int64) map[int64]int64 {
    fmt.Println("Factorizing number ", number)
    powers := make(map[int64]int64)
    for i := int64(2); i * i <= number; i++ {
        fmt.Println("i = ", i)
        for number % i == 0 {
            powers[i]++
            number /= i
        }
    }
    if number > 1 {
        powers[number]++
    }
    fmt.Println("powers: ", powers)
    return powers
}

func FactorizationToString(factorization map[int64]int64) string {
    var buffer bytes.Buffer
    for prime, power := range factorization {
        if buffer.Len() > 0 {
            buffer.WriteString(" * ")
        }
        buffer.WriteString(fmt.Sprintf("%d^%d", prime, power))
    }
    return buffer.String()
}
package authentication

import "golang.org/x/crypto/bcrypt"

type bCryptPasswordEncoder struct {}

func (*bCryptPasswordEncoder) Encode(password string) []byte {
    if encoded, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost); err == nil {
        return encoded
    } else {
        panic(err)
    }
}

func (*bCryptPasswordEncoder) CheckPassword(correctEncodedPassword []byte, rawPassword string) bool {
    return bcrypt.CompareHashAndPassword(correctEncodedPassword, []byte(rawPassword)) == nil
}
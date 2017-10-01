package authentication

import "net/http"
import (
    "github.com/go-redis/redis"
    "encoding/json"
)

type User struct {
    Id       uint
    Login    string
    Password []byte
}

func (this *User) MarshalBinary() ([]byte, error) {
    return json.Marshal(this)
}

func (this *User) UnmarshalBinary(data []byte) error {
    return json.Unmarshal(data, this)
}

type AuthorizedMethodHandler func(User, http.ResponseWriter, *http.Request)

type AuthenticationManager interface {
    CreateUser(login, rawPassword string) (token string, err error)
    AuthorizeUser(login, rawPassword string) (token string, err error)
    AuthenticationInterceptor(handler AuthorizedMethodHandler) http.HandlerFunc
}

type passwordEncoder interface {
    Encode(password string) []byte
    CheckPassword(correctEncodedPassword []byte, rawPassword string) bool
}

var Authorizer AuthenticationManager = &inMemoryAuthenticationManager{
    []byte("fooobar"), // TODO: get this from environmenet
    24,
    &bCryptPasswordEncoder{},
    redis.NewClient(&redis.Options{
        Addr:     "localhost:6379",
        Password: "",
        DB:       0})}

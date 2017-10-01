package authentication

import (
    "net/http"
    "errors"
    "github.com/dgrijalva/jwt-go"
    "time"
    "fmt"
    "github.com/go-redis/redis"
)

type inMemoryAuthenticationManager struct {
    signingKey           []byte
    tokenExpirationHours uint
    encoder              passwordEncoder
    redisClient          *redis.Client
}

func (this *inMemoryAuthenticationManager) CreateUser(login, rawPassword string) (token string, err error) {
    var foundUser = this.userByLogin(login)
    if foundUser != nil {
        return "", errors.New("Specified login is already in use")
    }
    var user = &User{
        this.nextId(),
        login,
        this.encoder.Encode(rawPassword)}
    if ok, err := this.redisClient.HSet("users", fmt.Sprint(user.Id), user).Result(); !ok || err != nil {
        panic(fmt.Sprintf("unexpected result of persisting user. ok: %v, err: %v", ok, err))
    }
    token = this.generateToken(*user)
    return token, nil
}

func (this *inMemoryAuthenticationManager) AuthorizeUser(login, rawPassword string) (token string, err error) {
    var user = this.userByLogin(login)
    if user == nil {
        return "", errors.New("Invalid login/password combination")
    }
    if !this.encoder.CheckPassword(user.Password, rawPassword) {
        return "", errors.New("Invalid login/password combination")
    }
    token = this.generateToken(*user)
    return token, nil
}

func (this *inMemoryAuthenticationManager) AuthenticationInterceptor(handler AuthorizedMethodHandler) http.HandlerFunc {
    return http.HandlerFunc(func(rw http.ResponseWriter, request *http.Request) {
        var tokenCookie, cookieErr = request.Cookie("session_token")
        if cookieErr != nil {
            http.Error(rw, "No token specified", http.StatusUnauthorized)
            return
        }
        fmt.Printf("Cookies: %v", request.Cookies())
        var tokenString = tokenCookie.Value
        fmt.Printf("Token string: %s\n", tokenString)
        var token, err = jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
            if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
                return nil, fmt.Errorf("Unexpected signing method: %v", token.Header["alg"])
            }
            return this.signingKey, nil
        })
        if err != nil {
            http.Error(rw, "Invalid token: "+err.Error(), http.StatusUnauthorized)
            return
        }

        if token != nil {
            var claims = token.Claims.(jwt.MapClaims)
            if expiration, ok := claims["expiration"].(float64); !ok || time.Unix(int64(expiration), 0).Before(time.Now()) {
                http.Error(rw, "Expired token", http.StatusUnauthorized)
                return
            }
            if userId, ok := claims["id"].(float64); ok && userId >= 0 {
                var user = this.userById(uint(userId))
                if user == nil {
                    http.Error(rw, "There is no user with such id", http.StatusUnauthorized)
                    return
                }
                handler(*user, rw, request)
            } else {
                http.Error(rw, fmt.Sprintf("Malformed 'id' value: %v", claims["id"]), http.StatusUnauthorized)
            }
        } else {
            http.Error(rw, "No token specified", http.StatusUnauthorized)
        }
    })
}

func (this *inMemoryAuthenticationManager) userByLogin(login string) *User {
    for kvals, cursor := this.redisClient.HScan("users", 0, "", 1).Val(); cursor != 0;
    kvals, cursor = this.redisClient.HScan("users", cursor, "", 1).Val() {
        for i := 1; i < len(kvals); i += 2 { // since even positions contain keys and odd ones contain values
            var user = &User{}
            if err := user.UnmarshalBinary([]byte(kvals[i])); err != nil {
                panic(err)
            }
            if user.Login == login {
                return user
            }
        }
    }
    return nil
}

func (this *inMemoryAuthenticationManager) userById(id uint) *User {
    var user = &User{}
    if this.redisClient.HGet("users", fmt.Sprint(id)).Scan(user) != nil {
        return nil
    }
    return user
}

func (this *inMemoryAuthenticationManager) generateToken(user User) string {
    var token = jwt.New(jwt.SigningMethodHS256)
    var claims = token.Claims.(jwt.MapClaims)
    claims["id"] = user.Id
    claims["expiration"] = time.Now().Add(time.Hour * 24).Unix()
    token.Claims = claims
    fmt.Println(claims)
    var tokenString, err = token.SignedString(this.signingKey)
    if err != nil {
        panic(err)
    }
    return tokenString
}

func (this *inMemoryAuthenticationManager) nextId() uint {
    var id, err = this.redisClient.Incr("__lastOccupiedId__").Result()
    if err != nil {
        panic(err)
    }
    return uint(id)
}

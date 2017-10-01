package main

import (
    "net/http"
    "github.com/lfyuomr-gylo/compter-networks-hw1/authentication"
    "github.com/gorilla/mux"
    "github.com/gorilla/handlers"
    "os"
    "github.com/lfyuomr-gylo/compter-networks-hw1/logic"
    "strconv"
    "github.com/go-redis/redis"
    "strings"
    "fmt"
    "github.com/sparrc/go-ping"
)

func parseLoginAndPassword(request *http.Request) (string, string, error) {
    var err = request.ParseForm()
    var login = request.PostFormValue("login")
    var password = request.PostFormValue("password")
    if err == nil && login != "" && password != "" {
        return login, password, nil
    } else {
        return "", "", err
    }
}

func parseDbQuery(request *http.Request) (string, error) {
    var err = request.ParseForm()
    var query = request.PostFormValue("query")
    if err == nil && query != "" {
        return query, nil
    } else {
        return "", err
    }
}

func parseNumber(request *http.Request) (int64, error) {
    var err = request.ParseForm()
    var numberString = request.PostFormValue("number")
    if err != nil || numberString == "" {
        return 0, err
    } else {
        return strconv.ParseInt(numberString, 10, 64)
    }
}

func respondBadRequest(rw http.ResponseWriter, explanation string) {
    http.Error(rw, "Bad request: "+explanation, http.StatusBadRequest)
}

func handleSignUp(rw http.ResponseWriter, request *http.Request) {
    var login, password, parseErr = parseLoginAndPassword(request)
    if parseErr != nil {
        respondBadRequest(rw, parseErr.Error())
        return
    } else if login == "" || password == "" {
        respondBadRequest(rw, "Both 'login' and 'password' fields are required")
        return
    }
    var token, err = authentication.Authorizer.CreateUser(login, password)
    if err != nil {
        respondBadRequest(rw, err.Error())
        return
    }
    var cookie = http.Cookie{Name: "session_token", Value: token, Domain: request.URL.Hostname(), Path: "/"}
    http.SetCookie(rw, &cookie)
    rw.Write([]byte("Successfully registered"))
}

func handleSignIn(rw http.ResponseWriter, request *http.Request) {
    var login, password, parseErr = parseLoginAndPassword(request)
    if login == "" || password == "" {
        respondBadRequest(rw, "Both 'login' and 'password' fields are required")
        return
    } else if parseErr != nil {
        respondBadRequest(rw, parseErr.Error())
    }
    var token, err = authentication.Authorizer.AuthorizeUser(login, password)
    if err != nil {
        http.Error(rw, "Incorrect login or passoword", http.StatusUnauthorized)
        return
    }
    var cookie = http.Cookie{Name: "session_token", Value: token, Domain: request.URL.Hostname(), Path: "/"}
    http.SetCookie(rw, &cookie)
    rw.Write([]byte("Successfully signed in"))
}

var redisClient = redis.NewClient(&redis.Options{
    Addr:     "localhost:6379",
    Password: "",
    DB:       1})

func handleDbQuery(user authentication.User, rw http.ResponseWriter, request *http.Request) {
    var query, parseErr = parseDbQuery(request)
    if query == "" {
        respondBadRequest(rw, "'query' field is required")
        return
    } else if parseErr != nil {
        respondBadRequest(rw, parseErr.Error())
        return
    }

    var args = stringSliceToInterfaceSlice(strings.Split(query, " "))
    var command = redis.NewCmd(args...)
    var err = redisClient.Process(command)
    if err != nil {
        respondBadRequest(rw, err.Error())
        return
    } else {
        result, err := command.Result()
        if err != nil {
            respondBadRequest(rw, err.Error())
            return
        } else {
            fmt.Fprintf(rw, "%v", result)
        }
    }
}

func stringSliceToInterfaceSlice(strings []string) []interface{} {
    var result = make([]interface{}, 0)
    for _, str := range strings {
        result = append(result, str)
    }
    return result
}

func handleFactorize(user authentication.User, rw http.ResponseWriter, request *http.Request) {
    var number, parseErr = parseNumber(request)
    if number <= 0 {
        respondBadRequest(rw, "'number' field is required and should be positive")
        return
    } else if parseErr != nil {
        respondBadRequest(rw, parseErr.Error())
        return
    }
    factorization := logic.FactorizationToString(logic.Factorize(number))
    rw.Write([]byte(factorization))
}

func formAuthInterceptor(handler http.Handler) http.HandlerFunc {
    return func(rw http.ResponseWriter, request *http.Request) {
        var path = request.URL.Path
        if path == "/" || path == "/login.html" || path == "/register.html" {
            handler.ServeHTTP(rw, request)
        } else {
            authentication.Authorizer.AuthenticationInterceptor(
                func(user authentication.User, rw http.ResponseWriter, req *http.Request) {
                    handler.ServeHTTP(rw, req)
                }).ServeHTTP(rw, request)
        }
    }
}

func handlePing(user authentication.User, rw http.ResponseWriter, request *http.Request) {
    var segments = strings.Split(request.RemoteAddr, ":")
    var ipAddr = strings.Join(segments[:len(segments) - 1], ":")
    if ipAddr[0] == '[' {
        ipAddr = ipAddr[1:len(ipAddr) - 1]
    }
    fmt.Println("ipAddr: ", ipAddr)
    var pinger, err = ping.NewPinger(ipAddr)
    if err != nil {
        panic(err)
    }
    var logLines = make([]string, 0)
    pinger.Count = 5
    pinger.OnRecv = func(pkg *ping.Packet) {
        var line = fmt.Sprintf("%d bytes from %s: icmp_seq=%d time=%v", pkg.Nbytes, pkg.IPAddr, pkg.Seq, pkg.Rtt)
        logLines = append(logLines, line)
    }
    pinger.OnFinish = func(stats *ping.Statistics) {
        var line = fmt.Sprintf("\n--- %s ping statistics ---\n", stats.Addr)
        logLines = append(logLines, line)
        line = fmt.Sprintf("%d packets transmitted, %d packets received, %v%% packet loss\n",
            stats.PacketsSent, stats.PacketsRecv, stats.PacketLoss)
        logLines = append(logLines, line)
        line = fmt.Sprintf("round-trip min/avg/max/stddev = %v/%v/%v/%v\n",
            stats.MinRtt, stats.AvgRtt, stats.MaxRtt, stats.StdDevRtt)
        logLines = append(logLines, line)
    }

    pinger.Run()
    var result = strings.Join(logLines, "\n")
    rw.Write([]byte(result))
}

func createRouter() http.Handler {
    var router = mux.NewRouter()
    router.HandleFunc("/rest/v1/signup", handleSignUp).Methods(http.MethodPost)
    router.HandleFunc("/rest/v1/signin", handleSignIn).Methods(http.MethodPost)
    router.HandleFunc("/rest/v1/dbquery", authentication.Authorizer.AuthenticationInterceptor(handleDbQuery)).
        Methods(http.MethodPost)
    router.HandleFunc("/rest/v1/ping", authentication.Authorizer.AuthenticationInterceptor(handlePing)).
        Methods(http.MethodPost)
    router.HandleFunc("/rest/v1/factorize", authentication.Authorizer.AuthenticationInterceptor(handleFactorize)).Methods(http.MethodPost)
    router.PathPrefix("/").HandlerFunc(formAuthInterceptor(http.FileServer(http.Dir("./views")))).Methods(http.MethodGet)
    return router
}

func main() {
    var router = createRouter()
    http.ListenAndServe(":8080", handlers.LoggingHandler(os.Stdout, router))
}

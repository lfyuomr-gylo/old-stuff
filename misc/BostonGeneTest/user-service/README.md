##User service

####Запуск приложения

Для сборки и запуска приложения необходим Maven 3.
Приложение можно собрать в fat-jar и запустить командами:
```bash
mvn clean package
java -jar target/user-service-1.0.0.jar
```
Либо командой
```bash
mvn spring-boot:run
```

После этого на порту `8080` поднимется сервис.

####API сервиса
Сервис предоставляет 3 метода:

1. `GET /users?email=<user email>` --- вернуть пользователя с
указанным e-mail.
2. `DELETE /users?email=<user email>` --- удалить пользователя с указанным
 e-mail.
3. `POST /users` --- создать пользователя, заданного в теле запроса
в формате JSON(дата рождения указывается в строковом
представлении по стандарту ISO-8601).

Возможные коды ответа:

| Код ответа |  Пояснение |
|:----------:|:----------:|
| 200 | запрос завершился успешно |
| 400 | некорректно составленный e-mail или нарушение формата объкта User |
| 404 | в системе нет пользователей с указанным e-mail |
| 409 | попытка создать пользователя с e-mail, уже существующим в системе |

####Пример работы сервиса
```bash
curl -i -X POST --header "Content-type: application/json" --data '{ 
    "email": "foo@bar.com", 
    "firstName": "Foo", 
    "lastName": "Bar", 
    "birthDate": "2017-03-07", 
    "password": "1234" }' localhost:8080/users
    
# HTTP/1.1 200 
# Content-Type: application/json;charset=UTF-8
# Transfer-Encoding: chunked
# Date: Mon, 06 Mar 2017 21:44:30 GMT
#
# {
#   "email":"foo@bar.com",
#   "firstName":"Foo",
#   "lastName":"Bar",
#   "birthDate":"2017-03-07",
#   "password":"$2a$10$JvSBhUwI7azh1UBLlCBT9OTayiUJ.jJ6bETVGi57Sdd5neLViF0i2"
# }

!!
# HTTP/1.1 409 
# Content-Type: application/json;charset=UTF-8
# Transfer-Encoding: chunked
# Date: Mon, 06 Mar 2017 21:45:32 GMT
# 
# {"errors":["User with email foo@bar.com already exists."]}

curl -i -X GET localhost:8080/users?email=foo@bar.com
# HTTP/1.1 200 
# Content-Type: application/json;charset=UTF-8
# Transfer-Encoding: chunked
# Date: Mon, 06 Mar 2017 21:46:33 GMT
# 
# > the same json body as for first POST request

curl -i -X GET localhost:8080/users?email=foo@baz.com # несуществующий пользователь
# HTTP/1.1 404 
# Content-Type: application/json;charset=UTF-8
# Transfer-Encoding: chunked
# Date: Mon, 06 Mar 2017 21:47:20 GMT
# 
# {"errors":["No user with email foo@baz.com found."]}
 
curl -i -X GET localhost:8080/users?email=avsjkvsdjhwqn # некорректный e-mail
# HTTP/1.1 400 
# Content-Type: application/json;charset=UTF-8
# Transfer-Encoding: chunked
# Date: Mon, 06 Mar 2017 21:47:51 GMT
# Connection: close
# 
# {"errors":["email определен в неверном формате"]}

curl -i -X DELETE localhost:8080/users?email=foo@bar.com
# HTTP/1.1 200 
# Content-Length: 0
# Date: Mon, 06 Mar 2017 21:48:15 GMT

curl -i -X GET localhost:8080/users?email=foo@bar.com
# HTTP/1.1 404 
# Content-Type: application/json;charset=UTF-8
# Transfer-Encoding: chunked
# Date: Mon, 06 Mar 2017 21:48:41 GMT
# 
# {"errors":["No user with email foo@bar.com found."]}
```

curl -i -X POST -d j_username=1 -d j_password=P@ssw0rd -c cookies.txt http://localhost:8080/cap-web/j_spring_security_check

curl -i --header "Accept:application/json" -X GET -b cookies.txt http://localhost:8080/cap-web/rest/greetinpg


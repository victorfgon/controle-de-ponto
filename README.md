# Controle de Ponto
Backend para bater ponto. No primeiro endpoint o colaborador irá bater um ponto quando entrar, um segundo quando for almoçar, um terceiro quando voltar, e por fim quando terminar o expediente, totalizando 4 pontos. No segundo endpoint o colaborador irá poder ver quantas horas de trabalho ele já fez no mês.

Obs:Seguindo as especificações do desafio no yaml, todas as datas foram salvas no banco como string e foi preciso mexer bastante com transformação de datas e strings, interpretei isso como parte do desafio para dificultar o problema proposto.

# Tecnologias utilizadas:
Java 17
,Spring Boot
,MongoDB
,Swagger
,JUnit
AWS Cloud


# Como executar
1- Clone este repositório

2- Execute o comando mvn clean package na raiz do projeto para gerar o arquivo JAR  e depois o arquivo JAR com o comando java -jar target/controle-de-ponto-0.0.1-SNAPSHOT.jar

3- Ou rode o projeto por uma IDE.

4- Acesse o Swagger em http://localhost:8080/swagger-ui/#/



# Endpoints:

POST /v1/batidas - Bate o ponto

GET /v1/folhas-de-ponto/{mes} - Retorna a folha com todos os pontos naqule mês e as horas trabalhadas


# DNS da aplicação na nuvem:

ec2-3-137-165-64.us-east-2.compute.amazonaws.com:8080

Exemplo: ec2-3-137-165-64.us-east-2.compute.amazonaws.com:8080/v1/batidas


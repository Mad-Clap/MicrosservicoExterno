<h1 align="center">Aluguel de Bicicletas<h1>

# Microsserviço Externo
O serviço de Aluguel de Bicicletas permite o gerenciamento do aluguel de bicicletas disponibilizadas em bicicletários no meio público. 

Ao se cadastrar no serviço e pagar o aluguel de uma bicicleta com um cartão de crédito válido o cliente poderá retirá-la da catraca e a utilizar por uma hora. O cliente pode recolocar a bicicleta em qualquer catraca de qualquer bicicletário fornecido pelo serviço, e caso tenha excedido a hora do aluguel um valor adicional será cobrado no ato de devolução automaticamente.

O microsserviço Externo recebe requisições no padrão REST dos outros microsserviços que compõe o serviço de Aluguel e processa lógicas de verificação de cartão, pagamento e envio de e-mail, se conectando aos serviços externos Mailgun e Getnet para realizar essas funções.

## Funcionalidades

### 1. Validação de cartão de crédito 
Requisita a validação de um número de cartão para a API da instituição de pagamento Getnet.

### 2. Requisições de cobrança
Requisita a cobrança do valor do aluguel de uma bicicleta ou o valor adicional por tempo excedido para a API da instituição de pagamento Getnet.

### 3. Envio de e-mail 
Requisita o envio de um e-mail do serviço ao cliente para a API do serviço de entrega de e-mail Mailgun.

## SonarCloud ☁️
[Externo-PM](https://sonarcloud.io/project/overview?id=Mad-Clap_Externo_PM) 🔗

## Deploy 🚀
[Externo](https://externo-pm.onrender.com) 🔗


## Tecnologias Utilizadas 🛠️
* Linguagem de programação JAVA
* Framework Spring Boot
    - Spring Web
    - Spring Data JPA
* Banco de dados Postgresql Render
* API Getnet
* API Mailgun
* Sonar Cloud
* Docker

## Ambiente de Desenvolvimento 🧰

* SDK Oracle OpenJDK 17.0.6
* IntelliJ IDEA

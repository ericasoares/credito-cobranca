# Crédito e Cobrança 

Sistema de gerenciamento de crédito e cobrança. Ele permite a criação, atualização e consulta de cobranças, além de notificar clientes sobre o status de suas dívidas e postar dívidas ativas em uma fila para processamento.

## 📌 Funcionalidades

### 1️⃣ Postar Dívidas Ativas na Fila
Envia dívidas ativas (com status **PENDENTE** ou **ATRASADO**) para uma fila de processamento.

**Endpoint:**  
`POST /api/v1/cobrancas/postar-dividas-ativas`

---
### 2️⃣ Notificar Cobrança
Envia uma notificação por **SMS** ao cliente sobre o status da cobrança.

**Endpoint:**  
`POST /api/v1/cobrancas/notificar?idCobranca={idCobranca}`

---
### 3️⃣ Criar Cobrança
Cria uma nova cobrança para um cliente.

**Endpoint:**  
`POST /api/v1/cobrancas`

---
### 4️⃣ Atualizar Status da Cobrança
Atualiza o status de uma cobrança (por exemplo, de **PENDENTE** para **ATRASADO**).

**Endpoint:**  
`PUT /api/v1/cobrancas/{idCobranca}/status`

---
### 5️⃣ Listar Dívidas Ativas por Cliente
Retorna todas as dívidas ativas de um cliente.

**Endpoint:**  
`GET /api/v1/cobrancas/dividas-ativas/{idCliente}`

---
### 6️⃣ Listar Todas as Cobranças por Cliente
Retorna todas as cobranças de um cliente, independentemente do status.

**Endpoint:**  
`GET /api/v1/cobrancas/{idCliente}`

---
### 7️⃣ Iniciar Negociação
Inicia a negociação de uma cobrança, alterando seu status para **EM_NEGOCIACAO**.

**Endpoint:**  
`POST /api/v1/cobrancas/{idCobranca}/negociar`

## 🛠 Tecnologias Utilizadas

- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Spring Data JPA**: Para acesso e gerenciamento de dados com MySQL.
- **MySQL**: Banco de dados relacional para armazenamento de cobranças e clientes.
- **RabbitMQ**: Sistema de mensageria para postar dívidas ativas em uma fila.
- **Redis**: Armazenamento em cache para melhorar o desempenho.
- **Twilio**: Integração para envio de notificações por SMS.
- **Lombok**: Para reduzir boilerplate code com anotações como `@Slf4j` e `@Data`.
- **Jakarta Validation**: Para validação de dados de entrada.
- **JUnit 5 e Mockito**: Para testes unitários e de integração.
- **Maven**: Gerenciamento de dependências e build do projeto.


## 🚀 Como Executar o Projeto

### 📌 Pré-requisitos
- Java 17
- MySQL
- RabbitMQ
- Redis
- Conta no Twilio (para envio de SMS)

### ▶️ Passos

1. **Clone o repositório:**

```bash
git clone https://github.com/seu-usuario/credito-cobranca.git
cd credito-cobranca
```

2. **Configure o banco de dados:**

Crie um banco de dados MySQL chamado `credito_cobranca` e atualize as configurações no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/credito_cobranca
spring.datasource.username=seu-usuario
spring.datasource.password=sua-senha
```

3. **Configure o RabbitMQ e Redis:**

Certifique-se de que o **RabbitMQ** e o **Redis** estão rodando localmente ou atualize as configurações no `application.properties`.

4. **Configure o Twilio:**

Adicione suas credenciais do Twilio no `application.properties`:

```properties
twilio.account-sid=seu-account-sid
twilio.auth-token=seu-auth-token
twilio.phone-number=seu-numero-twilio
```

5. **Execute o projeto:**

```bash
mvn spring-boot:run
```

6. **Acesse a API:**

A API estará disponível em [http://localhost:8080/api/v1/cobrancas](http://localhost:8080/api/v1/cobrancas).

## 📡 Exemplos de Requisições

### Criar Cobrança

**Endpoint:** `POST /api/v1/cobrancas`

**Body:**

```json
{
  "idCliente": 1,
  "valor": 1000.0,
  "status": "PENDENTE"
}
```

### Notificar Cobrança

**Endpoint:** `POST /api/v1/cobrancas/notificar?idCobranca=1`

### Listar Dívidas Ativas por Cliente

**Endpoint:** `GET /api/v1/cobrancas/dividas-ativas/1`

## 🧪 Testes

Para executar os testes unitários e de integração, use o comando:

```bash
mvn test
```
---

**🚀 Projeto desenvolvido para facilitar a gestão de crédito e cobrança!**
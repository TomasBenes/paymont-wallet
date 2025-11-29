# 💼 Client Wallet – Technical Assignment (Paymont)

Jednoduchá klientská peněženka pro měny CZK a EUR implementovaná jako:
- REST backend v Spring Boot (Java 17)
- Frontend v React (Vite)
- Databáze PostgreSQL
- Spouštění přes Docker Compose (PostgreSQL + backend + frontend)


====================
FUNKCIONALITY
====================

BACKEND
- Vytvoření peněženky
- Vedení zůstatků pro CZK a EUR
- Vklad (deposit)
- Výběr (withdraw)
- Validace vstupů
- Historie transakcí
- Stav transakce: SUCCESS, FAILED
- Přehled zůstatků
- Globální error handling

FRONTEND
- Vytvoření peněženky
- Přehled zůstatků
- Vklad / výběr
- Historie transakcí
- Validace vstupů v UI
- Chybové a informační hlášky


====================
TECH STACK
====================

Backend: Java 17, Spring Boot, Spring Data JPA  
Frontend: React + Vite  
Databáze: PostgreSQL 16  
ORM: Hibernate  
DevOps: Docker, Docker Compose


====================
SPUŠTĚNÍ CELÉHO STACKU
====================

Z kořenového adresáře backend projektu:

docker compose up -d --build

Po startu:

Frontend: http://localhost:3000
Backend API: http://localhost:8080
PostgreSQL: localhost:5432
DB: walletdb
User: walletuser
Password: walletpass

Zastavení:

docker compose down


====================
LOKÁLNÍ VÝVOJ
====================

PostgreSQL:
docker compose up -d postgres

Backend:
mvn clean install
mvn spring-boot:run

Backend běží na:
http://localhost:8080

Frontend:
cd wallet-frontend
npm install
npm run dev

Frontend běží na:
http://localhost:5173


====================
REST API
====================

Vytvoření peněženky:
POST /api/wallets

Request:
{
"name": "My Wallet"
}

Zůstatky:
GET /api/wallets/{id}/balances

[
{ "currency": "CZK", "amount": 800.00 },
{ "currency": "EUR", "amount": 0.00 }
]

Vklad:
POST /api/wallets/{id}/deposit

{
"currency": "CZK",
"amount": 1000,
"description": "Initial topup"
}

Výběr:
POST /api/wallets/{id}/withdraw

{
"currency": "CZK",
"amount": 200,
"targetAccount": "123456789/0100",
"description": "Test withdrawal"
}

Historie:
GET /api/wallets/{id}/transactions

[
{
"id": 2,
"type": "WITHDRAWAL",
"status": "SUCCESS",
"currency": "CZK",
"amount": 200.00,
"targetAccount": "123456789/0100",
"description": "Test withdrawal",
"balanceAfter": 800.00,
"createdAt": "2025-11-27T21:10:00Z"
}
]


====================
CHYBY
====================

{
"timestamp": "2025-11-27T21:20:00Z",
"status": 400,
"error": "Bad Request",
"message": "Insufficient funds",
"path": "/api/wallets/5/withdraw"
}


====================
DATABÁZE
====================

Tabulky:
- wallet
- wallet_balance
- wallet_transaction


====================
AUTOR
====================

Tomáš Beneš  
Projekt vypracován jako součást výběrového řízení společnosti Paymont

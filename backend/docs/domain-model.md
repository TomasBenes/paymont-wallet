# Domain model – Client Wallet

Cílem aplikace je jednoduchá peněženka pro koncového uživatele, která umožňuje:
- vést zůstatky v měnách CZK a EUR
- provádět vklady (deposit)
- provádět výběry (withdraw)
- zobrazit historii transakcí

---

## Entity

### Wallet

Reprezentuje peněženku jako takovou.

- `id` – jednoznačný identifikátor peněženky
- `name` – název peněženky (například „My React Wallet“)
- `createdAt` – datum a čas založení

Vztahy:
- 1 : N k `WalletBalance`
- 1 : N k `WalletTransaction`

---

### WalletBalance

Reprezentuje zůstatek pro konkrétní měnu v dané peněžence.

- `id`
- `wallet` – reference na `Wallet`
- `currency` – CZK nebo EUR (enum)
- `amount` – aktuální zůstatek (decimal s dvěma desetinnými místy)

Kombinace `(wallet, currency)` je unikátní – v jedné peněžence je maximálně jeden záznam pro CZK a jeden pro EUR.

---

### WalletTransaction

Reprezentuje jednotlivou transakci v peněžence.

- `id`
- `wallet` – reference na `Wallet`
- `type` – `DEPOSIT` nebo `WITHDRAWAL`
- `status` – `SUCCESS` nebo `FAILED`
- `currency` – CZK nebo EUR
- `amount` – částka transakce
- `targetAccount` – cílový účet (pouze u výběru, u vkladu `null`)
- `description` – volitelný popis transakce
- `balanceAfter` – zůstatek po provedení transakce v dané měně
- `createdAt` – datum a čas vytvoření transakce

---

## Základní pravidla

### Vklad (deposit)

1. Vstupy:
    - `walletId`
    - `currency`
    - `amount > 0`
    - `description` (volitelné)
2. Pokud peněženka nemá pro danou měnu ještě zůstatek, vytvoří se nový `WalletBalance` se startem na 0.
3. Částka se přičte k aktuálnímu zůstatku.
4. Vytvoří se záznam `WalletTransaction`:
    - `type = DEPOSIT`
    - `status = SUCCESS`
    - `balanceAfter` = nový zůstatek

---

### Výběr (withdraw)

1. Vstupy:
    - `walletId`
    - `currency`
    - `amount > 0`
    - `targetAccount` – povinný údaj
    - `description` (volitelné)
2. Zkontroluje se existence `Wallet` a `WalletBalance` pro danou měnu.
3. Zkontroluje se, zda je k dispozici dostatečný zůstatek.
    - Pokud ne:
        - vyhodí se doménová výjimka `InsufficientFundsException`
        - transakce se jako FAILED neukládá (nebo se může logovat pouze na backendu).
4. Pokud prostředky stačí:
    - částka se odečte z `WalletBalance`
    - vytvoří se `WalletTransaction`:
        - `type = WITHDRAWAL`
        - `status = SUCCESS`
        - `targetAccount` nastaven
        - `balanceAfter` = nový zůstatek

---

## API a UI

- Backend vystavuje REST API pro:
    - vytvoření peněženky
    - vklad
    - výběr
    - načtení zůstatků
    - načtení historie transakcí
- Frontend (React) pracuje pouze přes API, neobsahuje business logiku:
    - jednoduché formuláře pro deposit / withdraw
    - validace vstupů na úrovni UI (např. amount > 0, povinný target account)
    - přehled zůstatků a seznam transakcí

---

## Poznámka k rozšíření

Do budoucna by šlo snadno doplnit:
- více měn (rozšířením enumu a konfigurace)
- více peněženek pro jednoho uživatele
- autentizace uživatele a vazba `User -> Wallet`
- limity pro výběry a monitoring podezřelých transakcí

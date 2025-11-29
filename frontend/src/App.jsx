import { useState } from "react";
import "./App.css";

function formatAmount(value) {
    if (value == null) return "-";
    const num = Number(value);
    if (Number.isNaN(num)) return value;
    return num.toFixed(2);
}

function App() {
    const [wallet, setWallet] = useState(null);
    const [error, setError] = useState(null);
    const [info, setInfo] = useState(null);

    const [depositAmount, setDepositAmount] = useState("");
    const [depositCurrency, setDepositCurrency] = useState("CZK");
    const [depositDescription, setDepositDescription] = useState("");

    const [withdrawAmount, setWithdrawAmount] = useState("");
    const [withdrawCurrency, setWithdrawCurrency] = useState("CZK");
    const [withdrawTargetAccount, setWithdrawTargetAccount] = useState("");
    const [withdrawDescription, setWithdrawDescription] = useState("");

    const [transactions, setTransactions] = useState([]);

    const clearMessages = () => {
        setError(null);
        setInfo(null);
    };

    const createWallet = async () => {
        clearMessages();

        try {
            const response = await fetch("http://localhost:8080/api/wallets", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name: "My React Wallet" }),
            });

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || "Failed to create wallet");
            }

            const data = await response.json();

            setWallet(data);
            await refreshTransactions(data.id);
            setInfo(`Wallet #${data.id} was created.`);
        } catch (e) {
            setError(e.message);
        }
    };

    const refreshBalancesAndTransactions = async (walletId) => {
        const balancesResponse = await fetch(
            `http://localhost:8080/api/wallets/${walletId}/balances`
        );
        const balancesArray = await balancesResponse.json();
        const balancesMap = {};
        balancesArray.forEach((b) => {
            balancesMap[b.currency] = b.amount;
        });

        setWallet((prev) => (prev ? { ...prev, balances: balancesMap } : prev));

        await refreshTransactions(walletId);
    };

    const refreshTransactions = async (walletId) => {
        const txResponse = await fetch(
            `http://localhost:8080/api/wallets/${walletId}/transactions`
        );
        const txs = await txResponse.json();
        setTransactions(txs);
    };

    const deposit = async () => {
        if (!wallet) return;
        clearMessages();

        const amountNum = Number(depositAmount);
        if (!depositAmount || Number.isNaN(amountNum) || amountNum <= 0) {
            setError("Deposit amount must be a positive number.");
            return;
        }

        try {
            const response = await fetch(
                `http://localhost:8080/api/wallets/${wallet.id}/deposit`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        currency: depositCurrency,
                        amount: amountNum,
                        description: depositDescription,
                    }),
                }
            );

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || "Deposit failed");
            }

            await refreshBalancesAndTransactions(wallet.id);
            setDepositAmount("");
            setDepositDescription("");
            setInfo(
                `Deposited ${amountNum.toFixed(
                    2
                )} ${depositCurrency} to wallet #${wallet.id}.`
            );
        } catch (e) {
            setError(e.message);
        }
    };

    const withdraw = async () => {
        if (!wallet) return;
        clearMessages();

        const amountNum = Number(withdrawAmount);
        if (!withdrawAmount || Number.isNaN(amountNum) || amountNum <= 0) {
            setError("Withdraw amount must be a positive number.");
            return;
        }

        if (!withdrawTargetAccount.trim()) {
            setError("Target account is required for withdrawal.");
            return;
        }

        try {
            const response = await fetch(
                `http://localhost:8080/api/wallets/${wallet.id}/withdraw`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        currency: withdrawCurrency,
                        amount: amountNum,
                        targetAccount: withdrawTargetAccount,
                        description: withdrawDescription,
                    }),
                }
            );

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || "Withdraw failed");
            }

            await refreshBalancesAndTransactions(wallet.id);
            setWithdrawAmount("");
            setWithdrawTargetAccount("");
            setWithdrawDescription("");
            setInfo(
                `Withdrawal of ${amountNum.toFixed(
                    2
                )} ${withdrawCurrency} from wallet #${wallet.id} was successful.`
            );
        } catch (e) {
            setError(e.message);
        }
    };

    return (
        <div className="app-container">
            <h1>Client Wallet</h1>

            {!wallet && (
                <button className="primary-btn" onClick={createWallet}>
                    Create Wallet
                </button>
            )}

            {info && <div className="info-box">{info}</div>}
            {error && <div className="error-box">{error}</div>}

            {wallet && (
                <div className="layout">
                    <div className="wallet-box">
                        <h2>Wallet ID: {wallet.id}</h2>
                        <h3>{wallet.name}</h3>

                        <p className="small">
                            Created:{" "}
                            {wallet.createdAt
                                ? new Date(wallet.createdAt).toLocaleString()
                                : "-"}
                        </p>

                        <h4>Balances</h4>
                        <table className="balance-table">
                            <thead>
                            <tr>
                                <th>Currency</th>
                                <th>Amount</th>
                            </tr>
                            </thead>
                            <tbody>
                            {Object.entries(wallet.balances).map(([currency, amount]) => (
                                <tr key={currency}>
                                    <td>{currency}</td>
                                    <td>{formatAmount(amount)}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>

                        <h4>Deposit</h4>
                        <div className="form-row">
                            <select
                                value={depositCurrency}
                                onChange={(e) => setDepositCurrency(e.target.value)}
                            >
                                <option value="CZK">CZK</option>
                                <option value="EUR">EUR</option>
                            </select>

                            <input
                                type="number"
                                min="0"
                                step="0.01"
                                placeholder="Amount"
                                value={depositAmount}
                                onChange={(e) => setDepositAmount(e.target.value)}
                            />

                            <input
                                type="text"
                                placeholder="Description"
                                value={depositDescription}
                                onChange={(e) => setDepositDescription(e.target.value)}
                            />

                            <button className="primary-btn" onClick={deposit}>
                                Deposit
                            </button>
                        </div>

                        <h4>Withdraw</h4>
                        <div className="form-row">
                            <select
                                value={withdrawCurrency}
                                onChange={(e) => setWithdrawCurrency(e.target.value)}
                            >
                                <option value="CZK">CZK</option>
                                <option value="EUR">EUR</option>
                            </select>

                            <input
                                type="number"
                                min="0"
                                step="0.01"
                                placeholder="Amount"
                                value={withdrawAmount}
                                onChange={(e) => setWithdrawAmount(e.target.value)}
                            />

                            <input
                                type="text"
                                placeholder="Target account"
                                value={withdrawTargetAccount}
                                onChange={(e) => setWithdrawTargetAccount(e.target.value)}
                            />

                            <input
                                type="text"
                                placeholder="Description"
                                value={withdrawDescription}
                                onChange={(e) => setWithdrawDescription(e.target.value)}
                            />

                            <button className="primary-btn" onClick={withdraw}>
                                Withdraw
                            </button>
                        </div>
                    </div>

                    <div className="wallet-box">
                        <h3>Transaction history</h3>
                        {transactions.length === 0 && <p>No transactions yet.</p>}

                        {transactions.length > 0 && (
                            <table className="tx-table">
                                <thead>
                                <tr>
                                    <th>Time</th>
                                    <th>Type</th>
                                    <th>Currency</th>
                                    <th>Amount</th>
                                    <th>Target account</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th>Balance after</th>
                                </tr>
                                </thead>
                                <tbody>
                                {transactions.map((tx) => (
                                    <tr key={tx.id}>
                                        <td>{new Date(tx.createdAt).toLocaleString()}</td>
                                        <td>{tx.type}</td>
                                        <td>{tx.currency}</td>
                                        <td>{formatAmount(tx.amount)}</td>
                                        <td>{tx.targetAccount || "-"}</td>
                                        <td>{tx.description || "-"}</td>
                                        <td>{tx.status}</td>
                                        <td>{formatAmount(tx.balanceAfter)}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>

                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default App;

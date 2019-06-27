// Auto generated code, do not modify
package nxt.http.callers;

import nxt.http.APICall;

public class GetUnconfirmedTransactionsCall extends APICall.Builder<GetUnconfirmedTransactionsCall> {
    private GetUnconfirmedTransactionsCall() {
        super("getUnconfirmedTransactions");
    }

    public static GetUnconfirmedTransactionsCall create(int chain) {
        return new GetUnconfirmedTransactionsCall().param("chain", chain);
    }

    public GetUnconfirmedTransactionsCall requireLastBlock(String requireLastBlock) {
        return param("requireLastBlock", requireLastBlock);
    }

    public GetUnconfirmedTransactionsCall firstIndex(int firstIndex) {
        return param("firstIndex", firstIndex);
    }

    public GetUnconfirmedTransactionsCall lastIndex(int lastIndex) {
        return param("lastIndex", lastIndex);
    }

    public GetUnconfirmedTransactionsCall account(String... account) {
        return param("account", account);
    }

    public GetUnconfirmedTransactionsCall account(long... account) {
        return unsignedLongParam("account", account);
    }

    public GetUnconfirmedTransactionsCall requireBlock(String requireBlock) {
        return param("requireBlock", requireBlock);
    }

    public GetUnconfirmedTransactionsCall adminPassword(String adminPassword) {
        return param("adminPassword", adminPassword);
    }
}

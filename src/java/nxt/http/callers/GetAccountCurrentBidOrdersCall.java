// Auto generated code, do not modify
package nxt.http.callers;

import nxt.http.APICall;

public class GetAccountCurrentBidOrdersCall extends APICall.Builder<GetAccountCurrentBidOrdersCall> {
    private GetAccountCurrentBidOrdersCall() {
        super("getAccountCurrentBidOrders");
    }

    public static GetAccountCurrentBidOrdersCall create(int chain) {
        return new GetAccountCurrentBidOrdersCall().param("chain", chain);
    }

    public GetAccountCurrentBidOrdersCall requireLastBlock(String requireLastBlock) {
        return param("requireLastBlock", requireLastBlock);
    }

    public GetAccountCurrentBidOrdersCall firstIndex(int firstIndex) {
        return param("firstIndex", firstIndex);
    }

    public GetAccountCurrentBidOrdersCall lastIndex(int lastIndex) {
        return param("lastIndex", lastIndex);
    }

    public GetAccountCurrentBidOrdersCall asset(String asset) {
        return param("asset", asset);
    }

    public GetAccountCurrentBidOrdersCall asset(long asset) {
        return unsignedLongParam("asset", asset);
    }

    public GetAccountCurrentBidOrdersCall account(String account) {
        return param("account", account);
    }

    public GetAccountCurrentBidOrdersCall account(long account) {
        return unsignedLongParam("account", account);
    }

    public GetAccountCurrentBidOrdersCall requireBlock(String requireBlock) {
        return param("requireBlock", requireBlock);
    }

    public GetAccountCurrentBidOrdersCall adminPassword(String adminPassword) {
        return param("adminPassword", adminPassword);
    }
}

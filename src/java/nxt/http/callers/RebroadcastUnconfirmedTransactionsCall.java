// Auto generated code, do not modify
package nxt.http.callers;

import java.lang.String;
import nxt.http.APICall;

public class RebroadcastUnconfirmedTransactionsCall extends APICall.Builder<RebroadcastUnconfirmedTransactionsCall> {
    private RebroadcastUnconfirmedTransactionsCall() {
        super("rebroadcastUnconfirmedTransactions");
    }

    public static RebroadcastUnconfirmedTransactionsCall create() {
        return new RebroadcastUnconfirmedTransactionsCall();
    }

    public RebroadcastUnconfirmedTransactionsCall adminPassword(String adminPassword) {
        return param("adminPassword", adminPassword);
    }
}

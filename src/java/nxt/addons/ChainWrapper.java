package nxt.addons;

import nxt.blockchain.Chain;
import nxt.blockchain.ChildChain;
import nxt.blockchain.TransactionType;
import nxt.http.APIEnum;
import nxt.http.APITag;

import java.util.Set;

public class ChainWrapper {

    private final Chain chain;

    ChainWrapper(Chain chain) {
        this.chain = chain;
    }

    public boolean isChildChain() {
        return chain instanceof ChildChain;
    }

    public String getName() {
        return chain.getName();
    }

    public final int getId() {
        return chain.getId();
    }

    public final int getDecimals() {
        return chain.getDecimals();
    }

    public final long getTotalAmount() {
        return chain.getTotalAmount();
    }

    public final long getOneCoin() {
         return chain.ONE_COIN;
    }

    public String getDbSchema() {
        return chain.getDbSchema();
    }

    public final String getSchemaTable(String table) {
        return chain.getSchemaTable(table);
    }

    public boolean isAllowed(TransactionType transactionType) {
        return chain.isAllowed(transactionType);
    };

    public Set<TransactionType> getDisabledTransactionTypes() {
        return chain.getDisabledTransactionTypes();
    };

    public Set<APIEnum> getDisabledAPIs() {
        return chain.getDisabledAPIs();
    }

    public Set<APITag> getDisabledAPITags() {
        return chain.getDisabledAPITags();
    }

    @Override
    public String toString() {
        return chain.toString();
    }

}

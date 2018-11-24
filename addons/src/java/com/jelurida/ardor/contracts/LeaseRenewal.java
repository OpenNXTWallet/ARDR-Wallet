package com.jelurida.ardor.contracts;

import nxt.addons.AbstractContract;
import nxt.addons.BlockContext;
import nxt.addons.JA;
import nxt.addons.JO;
import nxt.http.callers.GetAccountCall;
import nxt.http.callers.GetAccountIdCall;
import nxt.http.callers.GetBlockCall;
import nxt.http.callers.GetBlockchainTransactionsCall;
import nxt.http.callers.LeaseBalanceCall;
import nxt.http.responses.TransactionResponse;

import java.util.ArrayList;
import java.util.List;

import static nxt.account.AccountControlFxtTransactionType.EFFECTIVE_BALANCE_LEASING;

/**
 * TODO lease renewal still needs some work don't deploy it at the moment
 * The LeaseRenewal contract checks the existing lessors of the contract account for lease renewal.
 * If necessarily it will renew the lease if the lessor passphrase is provided in the contract runner configuration.
 */
public class LeaseRenewal extends AbstractContract {

    /**
     * Process new block
     * @param context the block context
     */
    @Override
    public void processBlock(BlockContext context) {
        int leasePeriod = getContractParams().getInt("leasePeriod", 65535);
        int leaseRenewalWarningPeriod = getContractParams().getInt("leaseRenewalWarningPeriod", 2880);
        String lesseeAccount = context.getConfig().getAccount();
        JO getAccountResponse = GetAccountCall.create().account(lesseeAccount).includeLessors(true).call();
        JA lessorsInfo = getAccountResponse.getArray("lessorsInfo");
        List<String> lessorsRS = getAccountResponse.getArray("lessorsRS").values();
        List<String> allLessors = new ArrayList<>();
        List<String> renewalCandidates = new ArrayList<>();
        int leasingDelay = context.getBlockchainConstants().getInt("leasingDelay"); // load blockchain constant
        if (lessorsInfo != null) {
            for (int i=0; i<lessorsInfo.size(); i++) {
                JO lessorInfo = lessorsInfo.get(i);
                String lessorRS = lessorsRS.get(i);
                int currentHeightTo = 0;
                if (lessorInfo.isExist("currentLessee") && lessorInfo.getString("currentLessee").equals(lesseeAccount)) {
                    currentHeightTo = lessorInfo.getInt("currentHeightTo");
                }
                int nextHeightTo = 0;
                if (lessorInfo.isExist("nextLessee") && lessorInfo.getString("nextLessee").equals(lesseeAccount)) {
                    nextHeightTo = lessorInfo.getInt("nextHeightTo");
                }
                int leaseTerminationHeight = Math.max(currentHeightTo, nextHeightTo);
                if (leaseTerminationHeight == 0) {
                    context.logInfoMessage("lessor %s has no lease (should never happen (I think))", lessorRS);
                    continue;
                }
                int blocksUntilRenewal = leaseTerminationHeight - context.getHeight();
                String currentLesseeRS = lessorInfo.getString("currentLesseeRS");
                allLessors.add(lessorRS);
                if (blocksUntilRenewal < leaseRenewalWarningPeriod + leasingDelay) {
                    context.logInfoMessage("Need to renew lease of account %s in %d blocks", currentLesseeRS, blocksUntilRenewal);
                    renewalCandidates.add(lessorRS);
                }
            }
        }

        // Since we don't want to store paasphrases in the contract reference transaction, we store it in the node contract runner configuration
        JA array = context.getContractRunnerConfigParams(getClass().getSimpleName()).getArray("secretPhrases");
        for (String secretPhrase : array.values()) {
            byte[] publicKey = context.getPublicKey(secretPhrase);
            JO getAccountId = GetAccountIdCall.create().secretPhrase(secretPhrase).call();
            if (getAccountId.isExist("errorCode")) {
                context.logInfoMessage("Cannot find account with public key %s", context.toHexString(publicKey));
                continue;
            }
            String accountRS = context.rsAccount(getAccountId.getEntityId("account"));
            if (allLessors.contains(accountRS) && !renewalCandidates.contains(accountRS)) {
                // If the account is already lessor and there is no need to renew the lease
                continue;
            }
            // test that there is no lease in progress for which the stake hasn't matured yet
            int blockTimeStamp = GetBlockCall.create().height(Math.max(context.getBlockchainHeight() - leasingDelay, 0)).getBlock().getTimestamp();
            List<TransactionResponse> previousTransactions = GetBlockchainTransactionsCall.create(context.getParentChain().getId()).
                    account(accountRS).type(EFFECTIVE_BALANCE_LEASING.getType()).subtype(EFFECTIVE_BALANCE_LEASING.getSubtype()).timestamp(blockTimeStamp).getTransactions();
            if (previousTransactions.size() > 0) {
                continue;
            }
            LeaseBalanceCall leaseBalanceCall = LeaseBalanceCall.create(context.getParentChain().getId()).recipient(lesseeAccount).period("" + leasePeriod).secretPhrase(secretPhrase);
            context.createTransaction(leaseBalanceCall);
        }
    }
}

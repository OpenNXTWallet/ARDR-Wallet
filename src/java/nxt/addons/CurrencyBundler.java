/*
 * Copyright © 2016-2019 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of this software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package nxt.addons;

import nxt.blockchain.Bundler;
import nxt.blockchain.ChildTransaction;
import nxt.blockchain.TransactionType;
import nxt.ms.Currency;
import nxt.ms.CurrencyMintingAttachment;
import nxt.ms.CurrencyTransferAttachment;
import nxt.ms.ExchangeAttachment;
import nxt.ms.MonetarySystemTransactionType;
import nxt.ms.PublishExchangeOfferAttachment;
import nxt.ms.ReserveClaimAttachment;
import nxt.ms.ReserveIncreaseAttachment;
import nxt.util.Convert;

public class CurrencyBundler implements Bundler.Filter {

    private long currencyId;

    @Override
    public boolean ok(Bundler bundler, ChildTransaction childTransaction) {
        TransactionType type = childTransaction.getType();
        if (type instanceof MonetarySystemTransactionType) {
            long transactionCurrencyId;
            if (type == MonetarySystemTransactionType.CURRENCY_TRANSFER) {
                CurrencyTransferAttachment attachment = (CurrencyTransferAttachment) childTransaction.getAttachment();
                transactionCurrencyId = attachment.getCurrencyId();
            } else if (type == MonetarySystemTransactionType.EXCHANGE_BUY
                    || type == MonetarySystemTransactionType.EXCHANGE_SELL) {
                ExchangeAttachment attachment = (ExchangeAttachment) childTransaction.getAttachment();
                transactionCurrencyId = attachment.getCurrencyId();
            } else if (type == MonetarySystemTransactionType.PUBLISH_EXCHANGE_OFFER) {
                PublishExchangeOfferAttachment attachment = (PublishExchangeOfferAttachment) childTransaction.getAttachment();
                transactionCurrencyId = attachment.getCurrencyId();
            } else if (type == MonetarySystemTransactionType.RESERVE_INCREASE) {
                ReserveIncreaseAttachment attachment = (ReserveIncreaseAttachment) childTransaction.getAttachment();
                transactionCurrencyId = attachment.getCurrencyId();
            } else if (type == MonetarySystemTransactionType.RESERVE_CLAIM) {
                ReserveClaimAttachment attachment = (ReserveClaimAttachment) childTransaction.getAttachment();
                transactionCurrencyId = attachment.getCurrencyId();
            } else if (type == MonetarySystemTransactionType.CURRENCY_MINTING) {
                CurrencyMintingAttachment attachment = (CurrencyMintingAttachment) childTransaction.getAttachment();
                transactionCurrencyId = attachment.getCurrencyId();
            } else {
                return false;
            }
            return transactionCurrencyId == this.currencyId;
        }
        return false;
    }

    @Override
    public void setParameter(String parameter) {
        long currencyId = Convert.parseUnsignedLong(parameter);
        if (Currency.getCurrency(currencyId) == null) {
            throw new IllegalArgumentException("Unknown currency " + parameter);
        }
        this.currencyId = currencyId;
    }

    @Override
    public String getParameter() {
        return Long.toUnsignedString(this.currencyId);
    }

    @Override
    public String getName() {
        return "CurrencyBundler";
    }

    @Override
    public String getDescription() {
        return "Bundles only transactions for MS currency with ID provided as parameter";
    }
}

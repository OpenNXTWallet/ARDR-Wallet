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

package nxt.http.client;

import nxt.Tester;
import nxt.blockchain.ChildChain;
import nxt.http.APICall;
import org.json.simple.JSONObject;

public class SetAliasBuilder {
    private final APICall.Builder builder = new APICall.Builder("setAlias");
    private long fee;

    public SetAliasBuilder(Tester tester, ChildChain childChain, String aliasName, String aliasUri) {
        builder.param("secretPhrase", tester.getSecretPhrase())
                .param("chain", childChain.getName())
                .param("aliasName", aliasName)
                .param("aliasURI", aliasUri);
    }

    public SetAliasBuilder setFee(long fee) {
        this.fee = fee;
        return this;
    }

    public JSONObject invokeNoError() {
        return builder
                .param("feeNQT", fee)
                .build().invokeNoError();
    }
}
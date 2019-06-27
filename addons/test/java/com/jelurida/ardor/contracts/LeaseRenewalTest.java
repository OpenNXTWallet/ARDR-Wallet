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

package com.jelurida.ardor.contracts;

import nxt.http.APICall;
import nxt.addons.JA;
import nxt.addons.JO;
import org.junit.Assert;
import org.junit.Test;

public class LeaseRenewalTest extends AbstractContractTest {

    @Test
    public void multiLease() {
        JO setupParams = new JO();
        setupParams.put("leasePeriod", 4);
        setupParams.put("leaseRenewalWarningPeriod", 2);
        ContractTestHelper.deployContract(LeaseRenewal.class, setupParams);

        // Contract should submit lease transactions now
        generateBlock();
        // Wait the leasing delay for our stake to mature 2+1 blocks
        generateBlock();
        generateBlock();
        generateBlock();

        APICall apiCall = new APICall.Builder("getAccount").
                param("account", ALICE.getRsAccount()).
                param("includeLessors", "true").
                build();
        JO response = apiCall.getJsonResponse();
        JA lessorsInfo = response.getArray("lessorsInfo");
        Assert.assertEquals(3, lessorsInfo.size());
        for (JO lessor : lessorsInfo.objects()) {
            Assert.assertEquals(5, lessor.getInt("currentHeightFrom"));
            Assert.assertEquals(9, lessor.getInt("currentHeightTo"));
        }

        // Now wait for the lease to progress
        generateBlock(); // Renew lease
        generateBlock(); // Process the new leasing transactions
        generateBlock();
        generateBlock();
        apiCall = new APICall.Builder("getAccount").
                param("account", ALICE.getRsAccount()).
                param("includeLessors", "true").
                build();
        response = apiCall.getJsonResponse();
        lessorsInfo = response.getArray("lessorsInfo");
        Assert.assertEquals(3, lessorsInfo.size());
        for (JO lessor : lessorsInfo.objects()) {
            Assert.assertEquals(9, lessor.getInt("currentHeightFrom"));
            Assert.assertEquals(13, lessor.getInt("currentHeightTo"));
        }
    }

}

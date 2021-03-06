/*
 * Copyright © 2013-2016 The Nxt Core Developers.
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

package nxt.account;

import nxt.Constants;
import nxt.NxtException;
import nxt.blockchain.Attachment;
import nxt.blockchain.TransactionType;
import nxt.util.Convert;
import nxt.util.bbh.StringRw;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;

import static nxt.util.bbh.LengthRwPrimitiveType.BYTE;
import static nxt.util.bbh.LengthRwPrimitiveType.SHORT;

public final class AccountInfoAttachment extends Attachment.AbstractAttachment {

    public static final StringRw NAME_RW = new StringRw(BYTE, Constants.MAX_ACCOUNT_NAME_LENGTH);
    public static final StringRw DESCRIPTION_RW = new StringRw(SHORT, Constants.MAX_ACCOUNT_DESCRIPTION_LENGTH);

    private final String name;
    private final String description;

    AccountInfoAttachment(ByteBuffer buffer) throws NxtException.NotValidException {
        super(buffer);
        this.name = NAME_RW.readFromBuffer(buffer);
        this.description = DESCRIPTION_RW.readFromBuffer(buffer);
    }

    AccountInfoAttachment(JSONObject attachmentData) {
        super(attachmentData);
        this.name = Convert.nullToEmpty((String) attachmentData.get("name"));
        this.description = Convert.nullToEmpty((String) attachmentData.get("description"));
    }

    public AccountInfoAttachment(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    protected int getMySize() {
        return NAME_RW.getSize(name) + DESCRIPTION_RW.getSize(description);
    }

    @Override
    protected void putMyBytes(ByteBuffer buffer) {
        NAME_RW.writeToBuffer(name, buffer);
        DESCRIPTION_RW.writeToBuffer(description, buffer);
    }

    @Override
    protected void putMyJSON(JSONObject attachment) {
        attachment.put("name", name);
        attachment.put("description", description);
    }

    @Override
    public TransactionType getTransactionType() {
        return AccountPropertyTransactionType.ACCOUNT_INFO;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}

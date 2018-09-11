/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016-2018 Jelurida IP B.V.
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

package nxt.ae;

import nxt.Nxt;
import nxt.blockchain.ChildChain;
import nxt.blockchain.Transaction;
import nxt.crypto.Crypto;
import nxt.db.DbClause;
import nxt.db.DbIterator;
import nxt.db.DbKey;
import nxt.db.EntityDbTable;
import nxt.util.Convert;
import nxt.util.Listener;
import nxt.util.Listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AssetHistory {

    public enum Event {
        ASSET_DELETE, ASSET_INCREASE
    }

    private static final Listeners<AssetHistory,Event> listeners = new Listeners<>();

    private static final DbKey.HashKeyFactory<AssetHistory> assetHistoryDbKeyFactory = new DbKey.HashKeyFactory<AssetHistory>("full_hash", "id") {

        @Override
        public DbKey newKey(AssetHistory assetHistory) {
            return assetHistory.dbKey;
        }

    };

    private static final EntityDbTable<AssetHistory> assetHistoryTable = new EntityDbTable<AssetHistory>("public.asset_history", assetHistoryDbKeyFactory) {

        @Override
        protected AssetHistory load(Connection con, ResultSet rs, DbKey dbKey) throws SQLException {
            return new AssetHistory(rs, dbKey);
        }

        @Override
        protected void save(Connection con, AssetHistory assetHistory) throws SQLException {
            assetHistory.save(con);
        }

    };

    public static boolean addListener(Listener<AssetHistory> listener, Event eventType) {
        return listeners.addListener(listener, eventType);
    }

    public static boolean removeListener(Listener<AssetHistory> listener, Event eventType) {
        return listeners.removeListener(listener, eventType);
    }

    private static final DbClause deletesClause = new DbClause.LongClause("quantity", DbClause.Op.LT, 0L);
    private static final DbClause increasesClause = new DbClause.LongClause("quantity", DbClause.Op.GT, 0L);

    public static DbIterator<AssetHistory> getAssetHistory(long assetId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("asset_id", assetId), from, to, " ORDER BY asset_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAccountAssetHistory(long accountId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("account_id", accountId), from, to, " ORDER BY account_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAccountAssetHistory(long accountId, long assetId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("account_id", accountId).and(new DbClause.LongClause("asset_id", assetId)), from, to,
                " ORDER BY asset_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAssetDeletes(long assetId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("asset_id", assetId).and(deletesClause), from, to,
                " ORDER BY asset_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAccountAssetDeletes(long accountId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("account_id", accountId).and(deletesClause), from, to,
                " ORDER BY account_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAccountAssetDeletes(long accountId, long assetId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("account_id", accountId).and(new DbClause.LongClause("asset_id", assetId))
                .and(deletesClause), from, to, " ORDER BY asset_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAssetIncreases(long assetId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("asset_id", assetId).and(increasesClause), from, to,
                " ORDER BY asset_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAccountAssetIncreases(long accountId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("account_id", accountId).and(increasesClause), from, to,
                " ORDER BY account_id, height DESC ");
    }

    public static DbIterator<AssetHistory> getAccountAssetIncreases(long accountId, long assetId, int from, int to) {
        return assetHistoryTable.getManyBy(new DbClause.LongClause("account_id", accountId).and(new DbClause.LongClause("asset_id", assetId))
                .and(increasesClause), from, to, " ORDER BY asset_id, height DESC ");
    }

    static AssetHistory addAssetDelete(Transaction transaction, long assetId, long quantityQNT) {
        AssetHistory assetDelete = new AssetHistory(transaction, assetId, -quantityQNT);
        assetHistoryTable.insert(assetDelete);
        listeners.notify(assetDelete, Event.ASSET_DELETE);
        return assetDelete;
    }

    static AssetHistory addAssetIncrease(Transaction transaction, long assetId, long quantityQNT) {
        AssetHistory assetIncrease = new AssetHistory(transaction, assetId, quantityQNT);
        assetHistoryTable.insert(assetIncrease);
        listeners.notify(assetIncrease, Event.ASSET_INCREASE);
        return assetIncrease;
    }

    static AssetHistory addAssetIncrease(long assetId, long accountId, long quantityQNT) {
        AssetHistory assetIncrease = new AssetHistory(assetId, accountId, quantityQNT);
        assetHistoryTable.insert(assetIncrease);
        listeners.notify(assetIncrease, Event.ASSET_INCREASE);
        return assetIncrease;
    }

    public static void init() {}


    private final long id;
    private final byte[] hash;
    private final DbKey dbKey;
    private final int chainId;
    private final long assetId;
    private final int height;
    private final long accountId;
    private final long quantityQNT;
    private final int timestamp;

    private AssetHistory(Transaction transaction, long assetId, long quantityQNT) {
        this.id = transaction.getId();
        this.hash = transaction.getFullHash();
        this.dbKey = assetHistoryDbKeyFactory.newKey(this.hash, this.id);
        this.chainId = transaction.getChain().getId();
        this.assetId = assetId;
        this.accountId = transaction.getSenderId();
        this.quantityQNT = quantityQNT;
        this.timestamp = Nxt.getBlockchain().getLastBlockTimestamp();
        this.height = Nxt.getBlockchain().getHeight();
    }

    private AssetHistory(ResultSet rs, DbKey dbKey) throws SQLException {
        this.id = rs.getLong("id");
        this.hash = rs.getBytes("full_hash");
        this.dbKey = dbKey;
        this.chainId = rs.getInt("chain_id");
        this.assetId = rs.getLong("asset_id");
        this.accountId = rs.getLong("account_id");
        this.quantityQNT = rs.getLong("quantity");
        this.timestamp = rs.getInt("timestamp");
        this.height = rs.getInt("height");
    }

    private AssetHistory(long assetId, long accountId, long quantityQNT) {
        this.id = assetId;
        this.hash = Crypto.sha256().digest(Convert.toBytes(id));
        this.dbKey = assetHistoryDbKeyFactory.newKey(this.hash, this.id);
        this.chainId = ChildChain.IGNIS.getId();
        this.assetId = assetId;
        this.accountId = accountId;
        this.quantityQNT = quantityQNT;
        this.height = 0;
        this.timestamp = 0;
    }

    private void save(Connection con) throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO asset_history (id, full_hash, chain_id, asset_id, "
                + "account_id, quantity, timestamp, height) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            int i = 0;
            pstmt.setLong(++i, this.id);
            pstmt.setBytes(++i, this.hash);
            pstmt.setInt(++i, this.chainId);
            pstmt.setLong(++i, this.assetId);
            pstmt.setLong(++i, this.accountId);
            pstmt.setLong(++i, this.quantityQNT);
            pstmt.setInt(++i, this.timestamp);
            pstmt.setInt(++i, this.height);
            pstmt.executeUpdate();
        }
    }

    public long getId() {
        return id;
    }

    public byte[] getFullHash() {
        return hash;
    }

    public int getChainId() {
        return chainId;
    }

    public long getAssetId() { return assetId; }

    public long getAccountId() {
        return accountId;
    }

    public long getQuantityQNT() { return quantityQNT; }

    public int getTimestamp() {
        return timestamp;
    }

    public int getHeight() {
        return height;
    }

}

package jp.kinakomochi.mochieconomy.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MochiVoteData extends WorldSavedData {

    public static final String DATA_NAME =
            "mochieconomy_vote_data";

    private final List<PlayerVoteData> players =
            new ArrayList<>();

    public MochiVoteData() {
        super(DATA_NAME);
    }

    public MochiVoteData(String name) {
        super(name);
    }

    /**
     * MapStorageから投票データを取得する。
     *
     * WorldSavedDataはOverworldのMapStorageに
     * 保存する。
     */
    public static MochiVoteData get(MapStorage storage) {

        if (storage == null) {
            return null;
        }

        MochiVoteData data =
                (MochiVoteData) storage.getOrLoadData(
                        MochiVoteData.class,
                        DATA_NAME
                );

        if (data == null) {

            data = new MochiVoteData();

            storage.setData(
                    DATA_NAME,
                    data
            );
        }

        return data;
    }

    /**
     * プレイヤーのデータを取得する。
     *
     * 存在しない場合は新規作成する。
     */
    public PlayerVoteData getPlayer(
            UUID uuid,
            String name
    ) {

        if (uuid == null) {
            return null;
        }

        for (PlayerVoteData data : players) {

            if (uuid.equals(data.getUuid())) {

                if (name != null
                        && !name.isEmpty()
                        && !name.equals(data.getName())) {

                    data.setName(name);
                    markDirty();
                }

                return data;
            }
        }

        PlayerVoteData data =
                new PlayerVoteData(
                        uuid,
                        name == null ? "" : name
                );

        players.add(data);

        markDirty();

        return data;
    }

    /**
     * 保存されている全プレイヤー情報を取得する。
     */
    public List<PlayerVoteData> getPlayers() {

        return new ArrayList<>(
                players
        );
    }

    /**
     * 連続投票日数の多い順に取得する。
     *
     * 同数の場合は名前順。
     */
    public List<PlayerVoteData>
    getPlayersSortedByStreak() {

        List<PlayerVoteData> result =
                new ArrayList<>(
                        players
                );

        result.sort(
                new Comparator<PlayerVoteData>() {

                    @Override
                    public int compare(
                            PlayerVoteData a,
                            PlayerVoteData b
                    ) {

                        int streakCompare =
                                Long.compare(
                                        b.getStreak(),
                                        a.getStreak()
                                );

                        if (streakCompare != 0) {
                            return streakCompare;
                        }

                        return a.getName()
                                .compareToIgnoreCase(
                                        b.getName()
                                );
                    }
                }
        );

        return result;
    }

    /**
     * NBTから読み込む。
     */
    @Override
    public void readFromNBT(
            NBTTagCompound nbt
    ) {

        players.clear();

        if (!nbt.hasKey(
                "Players",
                9
        )) {
            return;
        }

        NBTTagList playerList =
                nbt.getTagList(
                        "Players",
                        10
                );

        for (int i = 0;
             i < playerList.tagCount();
             i++) {

            NBTTagCompound playerTag =
                    playerList.getCompoundTagAt(i);

            String uuidString =
                    playerTag.getString(
                            "UUID"
                    );

            if (uuidString.isEmpty()) {
                continue;
            }

            try {

                UUID uuid =
                        UUID.fromString(
                                uuidString
                        );

                String name =
                        playerTag.getString(
                                "Name"
                        );

                long streak =
                        playerTag.getLong(
                                "Streak"
                        );

                long balance =
                        playerTag.getLong(
                                "Balance"
                        );

                String lastVoteDate =
                        playerTag.getString(
                                "LastVoteDate"
                        );

                players.add(
                        new PlayerVoteData(
                                uuid,
                                name,
                                streak,
                                balance,
                                lastVoteDate
                        )
                );

            } catch (IllegalArgumentException ignored) {
                /*
                 * 壊れたUUIDを持つデータは
                 * 読み飛ばす。
                 */
            }
        }
    }

    /**
     * NBTへ保存する。
     */
    @Override
    public NBTTagCompound writeToNBT(
            NBTTagCompound nbt
    ) {

        NBTTagList playerList =
                new NBTTagList();

        for (PlayerVoteData data : players) {

            NBTTagCompound playerTag =
                    new NBTTagCompound();

            playerTag.setString(
                    "UUID",
                    data.getUuid().toString()
            );

            playerTag.setString(
                    "Name",
                    data.getName()
            );

            playerTag.setLong(
                    "Streak",
                    data.getStreak()
            );

            playerTag.setLong(
                    "Balance",
                    data.getBalance()
            );

            playerTag.setString(
                    "LastVoteDate",
                    data.getLastVoteDate()
            );

            playerList.appendTag(
                    playerTag
            );
        }

        nbt.setTag(
                "Players",
                playerList
        );

        return nbt;
    }

    /**
     * 1プレイヤー分の投票情報。
     */
    public static class PlayerVoteData {

        private final UUID uuid;

        private String name;

        private long streak;

        private long balance;

        private String lastVoteDate;

        public PlayerVoteData(
                UUID uuid,
                String name
        ) {

            this(
                    uuid,
                    name,
                    0L,
                    0L,
                    ""
            );
        }

        public PlayerVoteData(
                UUID uuid,
                String name,
                long streak,
                long balance,
                String lastVoteDate
        ) {

            this.uuid = uuid;
            this.name = name;
            this.streak = streak;
            this.balance = balance;
            this.lastVoteDate =
                    lastVoteDate;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(
                String name
        ) {
            this.name = name;
        }

        public long getStreak() {
            return streak;
        }

        public void setStreak(
                long streak
        ) {
            this.streak = streak;
        }

        public long getBalance() {
            return balance;
        }

        public void setBalance(
                long balance
        ) {
            this.balance = balance;
        }

        public String getLastVoteDate() {
            return lastVoteDate;
        }

        public void setLastVoteDate(
                String lastVoteDate
        ) {
            this.lastVoteDate =
                    lastVoteDate;
        }
    }
}
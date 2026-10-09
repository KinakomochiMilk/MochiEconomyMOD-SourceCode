package jp.kinakomochi.mochieconomy.api;

import jp.kinakomochi.mochieconomy.data.MochiVoteData;
import jp.kinakomochi.mochieconomy.data.MochiVoteData.PlayerVoteData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public final class MochiVoteAPI {

    private static final Logger LOGGER =
            LogManager.getLogger("MochiEconomy");

    private static final ZoneId TIME_ZONE =
            ZoneId.of("Asia/Tokyo");

    private static final long EARLY_REWARD = 2L;

    private static final long NORMAL_REWARD = 10L;

    private MochiVoteAPI() {
    }

    /**
     * サーバーの投票データを取得する。
     */
    public static MochiVoteData getData(
            MinecraftServer server
    ) {

        if (server == null) {
            return null;
        }

        World world =
                server.getWorld(0);

        if (world == null) {
            return null;
        }

        MapStorage storage =
                world.getMapStorage();

        if (storage == null) {
            return null;
        }

        return MochiVoteData.get(storage);
    }

    /**
     * プレイヤーの投票データを取得する。
     *
     * 存在しない場合は新規作成する。
     */
    public static PlayerVoteData getPlayerData(
            MinecraftServer server,
            UUID uuid,
            String name
    ) {

        if (server == null || uuid == null) {
            return null;
        }

        MochiVoteData data =
                getData(server);

        if (data == null) {
            return null;
        }

        return data.getPlayer(
                uuid,
                name
        );
    }

    /**
     * プレイヤーの現在の投票報酬残高を取得する。
     */
    public static long getBalance(
            MinecraftServer server,
            UUID uuid,
            String name
    ) {

        PlayerVoteData data =
                getPlayerData(
                        server,
                        uuid,
                        name
                );

        if (data == null) {
            return 0L;
        }

        return data.getBalance();
    }

    /**
     * プレイヤーの連続投票日数を取得する。
     */
    public static long getStreak(
            MinecraftServer server,
            UUID uuid,
            String name
    ) {

        PlayerVoteData data =
                getPlayerData(
                        server,
                        uuid,
                        name
                );

        if (data == null) {
            return 0L;
        }

        return data.getStreak();
    }

    /**
     * 投票を1回受け付ける。
     *
     * 投票者が正しく特定できていることを前提とする。
     *
     * 1～7日目:
     *     2コイン
     *
     * 8日目以降:
     *     10コイン
     *
     * 同じ日に複数回投票しても、
     * 連続投票日数と報酬は増加しない。
     *
     * @return 今回の投票によって加算された報酬。
     */
    public static long addVote(
            MinecraftServer server,
            UUID uuid,
            String name,
            String service
    ) {

        if (server == null || uuid == null) {
            return 0L;
        }

        MochiVoteData data =
                getData(server);

        if (data == null) {
            LOGGER.error(
                    "Unable to access vote data."
            );

            return 0L;
        }

        PlayerVoteData player =
                data.getPlayer(
                        uuid,
                        name
                );

        if (player == null) {
            return 0L;
        }

        LocalDate today =
                LocalDate.now(
                        TIME_ZONE
                );

        String todayString =
                today.toString();

        String lastVoteDate =
                player.getLastVoteDate();

        /*
         * 同日に複数回投票した場合。
         *
         * 投票そのものはログに残すが、
         * 報酬は二重に与えない。
         */
        if (todayString.equals(
                lastVoteDate
        )) {

            LOGGER.info(
                    "Vote received again on the same day: " +
                            "player={} service={} reward=0 streak={} balance={}",
                    name,
                    service,
                    player.getStreak(),
                    player.getBalance()
            );

            return 0L;
        }

        long newStreak;

        /*
         * 初回投票。
         */
        if (lastVoteDate == null
                || lastVoteDate.isEmpty()) {

            newStreak = 1L;

        } else {

            try {

                LocalDate previousDate =
                        LocalDate.parse(
                                lastVoteDate
                        );

                LocalDate expectedDate =
                        today.minusDays(1L);

                /*
                 * 前日にも投票していた場合、
                 * 連続投票日数を増やす。
                 */
                if (previousDate.equals(
                        expectedDate
                )) {

                    newStreak =
                            player.getStreak() + 1L;

                } else {

                    /*
                     * 1日以上空いていた場合は
                     * 連続投票をリセット。
                     */
                    newStreak = 1L;
                }

            } catch (Exception ignored) {

                /*
                 * 保存されている日付が壊れていた場合は、
                 * 安全側に倒して新しい連続投票として扱う。
                 */
                newStreak = 1L;
            }
        }

        long reward;

        if (newStreak <= 7L) {
            reward = EARLY_REWARD;
        } else {
            reward = NORMAL_REWARD;
        }

        long oldBalance =
                player.getBalance();

        long newBalance;

        /*
         * longのオーバーフローを防ぐ。
         */
        if (Long.MAX_VALUE - oldBalance < reward) {

            LOGGER.error(
                    "Vote balance overflow prevented: " +
                            "player={} oldBalance={} reward={}",
                    name,
                    oldBalance,
                    reward
            );

            return 0L;
        }

        newBalance =
                oldBalance + reward;

        player.setStreak(
                newStreak
        );

        player.setBalance(
                newBalance
        );

        player.setLastVoteDate(
                todayString
        );

        data.markDirty();

        LOGGER.info(
                "Vote received: " +
                        "player={} uuid={} service={} " +
                        "streak={} reward={} balance={} ",
                name,
                uuid,
                service,
                newStreak,
                reward,
                newBalance
        );

        return reward;
    }

    /**
     * オンラインプレイヤーから投票を受け付ける。
     */
    public static long addVote(
            MinecraftServer server,
            EntityPlayer player,
            String service
    ) {

        if (player == null) {
            return 0L;
        }

        return addVote(
                server,
                player.getUniqueID(),
                player.getName(),
                service
        );
    }

    /**
     * 投票報酬をプレイヤーへ受け取らせる。
     *
     * 実際のMinecraftアイテムへの変換は
     * MochiEconomyAPI.deposit() に任せる。
     *
     * インベントリへ完全に入れられなかった場合は
     * 内部残高を変更しない。
     */
    public static boolean claim(
            MinecraftServer server,
            EntityPlayer player,
            long amount
    ) {

        if (server == null
                || player == null
                || amount <= 0L) {

            return false;
        }

        UUID uuid =
                player.getUniqueID();

        PlayerVoteData data =
                getPlayerData(
                        server,
                        uuid,
                        player.getName()
                );

        if (data == null) {
            return false;
        }

        long balance =
                data.getBalance();

        if (balance < amount) {

            LOGGER.info(
                    "Economy claim rejected: " +
                            "player={} requested={} balance={}",
                    player.getName(),
                    amount,
                    balance
            );

            return false;
        }

        /*
         * 先にMinecraftのインベントリへ
         * 実際のコインを入れる。
         */
        long deposited =
                MochiEconomyAPI.deposit(
                        player,
                        amount
                );

        /*
         * 完全に入らなかった場合は、
         * 内部残高を一切変更しない。
         */
        if (deposited != amount) {

            LOGGER.info(
                    "Economy claim failed: " +
                            "player={} requested={} deposited={} balance={}",
                    player.getName(),
                    amount,
                    deposited,
                    balance
            );

            return false;
        }

        long newBalance =
                balance - amount;

        data.setBalance(
                newBalance
        );

        MochiVoteData voteData =
                getData(server);

        if (voteData != null) {
            voteData.markDirty();
        }

        LOGGER.info(
                "Economy claim successful: " +
                        "player={} amount={} balance={} -> {}",
                player.getName(),
                amount,
                balance,
                newBalance
        );

        return true;
    }

    /**
     * プレイヤー自身の投票情報を取得する。
     */
    public static PlayerVoteData getStatus(
            MinecraftServer server,
            EntityPlayer player
    ) {

        if (player == null) {
            return null;
        }

        return getPlayerData(
                server,
                player.getUniqueID(),
                player.getName()
        );
    }

    /**
     * 連続投票日数順のランキングを取得する。
     */
    public static List<PlayerVoteData>
    getRanking(
            MinecraftServer server
    ) {

        MochiVoteData data =
                getData(server);

        if (data == null) {
            return new java.util.ArrayList<>();
        }

        return data.getPlayersSortedByStreak();
    }

    /**
     * 指定プレイヤーの報酬残高を直接変更する。
     *
     * 通常のプレイヤー操作では使用せず、
     * デバッグコマンドから使用することを想定。
     */
    public static boolean addBalance(
            MinecraftServer server,
            UUID uuid,
            String name,
            long amount
    ) {

        if (server == null
                || uuid == null
                || amount <= 0L) {

            return false;
        }

        PlayerVoteData data =
                getPlayerData(
                        server,
                        uuid,
                        name
                );

        if (data == null) {
            return false;
        }

        long oldBalance =
                data.getBalance();

        if (Long.MAX_VALUE - oldBalance < amount) {
            return false;
        }

        long newBalance =
                oldBalance + amount;

        data.setBalance(
                newBalance
        );

        MochiVoteData voteData =
                getData(server);

        if (voteData != null) {
            voteData.markDirty();
        }

        LOGGER.info(
                "DEBUG balance add: " +
                        "player={} uuid={} amount={} balance={} -> {}",
                name,
                uuid,
                amount,
                oldBalance,
                newBalance
        );

        return true;
    }

    /**
     * 指定プレイヤーの報酬残高を設定する。
     *
     * デバッグ用。
     */
    public static boolean setBalance(
            MinecraftServer server,
            UUID uuid,
            String name,
            long amount
    ) {

        if (server == null
                || uuid == null
                || amount < 0L) {

            return false;
        }

        PlayerVoteData data =
                getPlayerData(
                        server,
                        uuid,
                        name
                );

        if (data == null) {
            return false;
        }

        long oldBalance =
                data.getBalance();

        data.setBalance(
                amount
        );

        MochiVoteData voteData =
                getData(server);

        if (voteData != null) {
            voteData.markDirty();
        }

        LOGGER.info(
                "DEBUG balance set: " +
                        "player={} uuid={} balance={} -> {}",
                name,
                uuid,
                oldBalance,
                amount
        );

        return true;
    }

    /**
     * 指定プレイヤーの連続投票日数を設定する。
     *
     * デバッグ用。
     */
    public static boolean setStreak(
            MinecraftServer server,
            UUID uuid,
            String name,
            long streak
    ) {

        if (server == null
                || uuid == null
                || streak < 0L) {

            return false;
        }

        PlayerVoteData data =
                getPlayerData(
                        server,
                        uuid,
                        name
                );

        if (data == null) {
            return false;
        }

        long oldStreak =
                data.getStreak();

        data.setStreak(
                streak
        );

        MochiVoteData voteData =
                getData(server);

        if (voteData != null) {
            voteData.markDirty();
        }

        LOGGER.info(
                "DEBUG streak set: " +
                        "player={} uuid={} streak={} -> {}",
                name,
                uuid,
                oldStreak,
                streak
        );

        return true;
    }

    /**
     * 指定プレイヤーの投票データを初期状態へ戻す。
     *
     * デバッグ用。
     */
    public static boolean reset(
            MinecraftServer server,
            UUID uuid,
            String name
    ) {

        if (server == null
                || uuid == null) {

            return false;
        }

        PlayerVoteData data =
                getPlayerData(
                        server,
                        uuid,
                        name
                );

        if (data == null) {
            return false;
        }

        data.setStreak(0L);
        data.setBalance(0L);
        data.setLastVoteDate("");

        MochiVoteData voteData =
                getData(server);

        if (voteData != null) {
            voteData.markDirty();
        }

        LOGGER.info(
                "DEBUG vote data reset: " +
                        "player={} uuid={}",
                name,
                uuid
        );

        return true;
    }
}
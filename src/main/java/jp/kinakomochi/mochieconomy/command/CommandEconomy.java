package jp.kinakomochi.mochieconomy.command;

import jp.kinakomochi.mochieconomy.api.MochiVoteAPI;
import jp.kinakomochi.mochieconomy.data.MochiVoteData;
import jp.kinakomochi.mochieconomy.data.MochiVoteData.PlayerVoteData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandEconomy extends CommandBase {

    private static final int PAGE_SIZE = 10;

    @Override
    public String getName() {
        return "economy";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/economy <status|get|server|debug|forcode>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        String subCommand =
                args[0].toLowerCase();

        switch (subCommand) {

            case "status":
                executeStatus(server, sender, args);
                break;

            case "get":
                executeGet(server, sender, args);
                break;

            case "server":
                executeServer(server, sender, args);
                break;

            case "debug":
                executeDebug(server, sender, args);
                break;

            case "forcode":
                executeForCode(server, sender, args);
                break;

            default:
                sendHelp(sender);
                break;
        }
    }

    /*
     * /economy status
     */
    private void executeStatus(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        EntityPlayer player =
                getCommandSenderAsPlayer(sender);

        if (args.length != 1) {
            throw new WrongUsageException(
                    "/economy status"
            );
        }

        PlayerVoteData data =
                MochiVoteAPI.getStatus(
                        server,
                        player
                );

        if (data == null) {
            sender.sendMessage(
                    message(
                            TextFormatting.RED +
                                    "投票データを取得できませんでした。"
                    )
            );
            return;
        }

        sender.sendMessage(
                message(
                        TextFormatting.GOLD +
                                "===== Kinakomochi Economy ====="
                )
        );

        sender.sendMessage(
                message(
                        TextFormatting.YELLOW +
                                "投票報酬残高: " +
                                TextFormatting.WHITE +
                                data.getBalance()
                )
        );

        sender.sendMessage(
                message(
                        TextFormatting.YELLOW +
                                "連続投票日数: " +
                                TextFormatting.WHITE +
                                data.getStreak()
                )
        );

        String lastVote =
                data.getLastVoteDate();

        if (lastVote == null
                || lastVote.isEmpty()) {

            lastVote = "なし";
        }

        sender.sendMessage(
                message(
                        TextFormatting.YELLOW +
                                "最終投票日: " +
                                TextFormatting.WHITE +
                                lastVote
                )
        );
    }

    /*
     * /economy get <amount>
     */
    private void executeGet(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        EntityPlayer player =
                getCommandSenderAsPlayer(sender);

        if (args.length != 2) {
            throw new WrongUsageException(
                    "/economy get <amount>"
            );
        }

        long amount =
                parseAmount(args[1]);

        if (amount <= 0L) {
            throw new CommandException(
                    "受け取る金額は1以上にしてください。"
            );
        }

        PlayerVoteData data =
                MochiVoteAPI.getStatus(
                        server,
                        player
                );

        if (data == null) {
            throw new CommandException(
                    "投票データを取得できませんでした。"
            );
        }

        long balance =
                data.getBalance();

        if (balance < amount) {

            sender.sendMessage(
                    message(
                            TextFormatting.RED +
                                    "投票報酬残高が不足しています。"
                    )
            );

            sender.sendMessage(
                    message(
                            TextFormatting.YELLOW +
                                    "現在の残高: " +
                                    TextFormatting.WHITE +
                                    balance
                    )
            );

            return;
        }

        boolean success =
                MochiVoteAPI.claim(
                        server,
                        player,
                        amount
                );

        if (!success) {

            sender.sendMessage(
                    message(
                            TextFormatting.RED +
                                    "コインを受け取れませんでした。"
                    )
            );

            sender.sendMessage(
                    message(
                            TextFormatting.GRAY +
                                    "インベントリの空き容量を確認してください。"
                    )
            );

            return;
        }

        sender.sendMessage(
                message(
                        TextFormatting.GREEN +
                                "投票報酬から " +
                                amount +
                                " コインを受け取りました。"
                )
        );
    }

    /*
     * /economy server status [page]
     */
    private void executeServer(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length < 2
                || !args[1].equalsIgnoreCase("status")) {

            throw new WrongUsageException(
                    "/economy server status [page]"
            );
        }

        int page = 1;

        if (args.length >= 3) {

            try {
                page = Integer.parseInt(
                        args[2]
                );
            } catch (NumberFormatException e) {
                throw new CommandException(
                        "ページ番号が正しくありません。"
                );
            }
        }

        if (page < 1) {
            page = 1;
        }

        List<PlayerVoteData> ranking =
                MochiVoteAPI.getRanking(
                        server
                );

        int total =
                ranking.size();

        int totalPages =
                Math.max(
                        1,
                        (total + PAGE_SIZE - 1)
                                / PAGE_SIZE
                );

        if (page > totalPages) {
            page = totalPages;
        }

        sender.sendMessage(
                message(
                        TextFormatting.GOLD +
                                "===== 投票ランキング ====="
                )
        );

        sender.sendMessage(
                message(
                        TextFormatting.GRAY +
                                "ページ " +
                                page +
                                " / " +
                                totalPages
                )
        );

        if (ranking.isEmpty()) {

            sender.sendMessage(
                    message(
                            TextFormatting.GRAY +
                                    "まだ投票データがありません。"
                    )
            );

            return;
        }

        int start =
                (page - 1) * PAGE_SIZE;

        int end =
                Math.min(
                        start + PAGE_SIZE,
                        total
                );

        for (int i = start; i < end; i++) {

            PlayerVoteData data =
                    ranking.get(i);

            int rank =
                    i + 1;

            TextComponentString line =
                    new TextComponentString(
                            String.format(
                                    "%2d. %s - %d日",
                                    rank,
                                    data.getName(),
                                    data.getStreak()
                            )
                    );

            line.getStyle().setColor(
                    rank == 1
                            ? TextFormatting.GOLD
                            : rank == 2
                              ? TextFormatting.YELLOW
                              : rank == 3
                                ? TextFormatting.GREEN
                                : TextFormatting.WHITE
            );

            sender.sendMessage(line);
        }

        sendPageControls(
                sender,
                page,
                totalPages
        );
    }

    /*
     * /economy debug ...
     */
    private void executeDebug(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (!sender.canUseCommand(
                2,
                getName()
        )) {

            throw new CommandException(
                    "このコマンドはOP専用です。"
            );
        }

        if (args.length < 2) {
            sendDebugHelp(sender);
            return;
        }

        String action =
                args[1].toLowerCase();

        switch (action) {

            case "status":
                debugStatus(
                        server,
                        sender,
                        args
                );
                break;

            case "add":
                debugAdd(
                        server,
                        sender,
                        args
                );
                break;

            case "set":
                debugSet(
                        server,
                        sender,
                        args
                );
                break;

            case "streak":
                debugStreak(
                        server,
                        sender,
                        args
                );
                break;

            case "reset":
                debugReset(
                        server,
                        sender,
                        args
                );
                break;

            default:
                sendDebugHelp(sender);
                break;
        }
    }

    /*
     * /economy forcode add <player>
     *
     * Votifierなどの外部システムから呼び出すための内部コマンド。
     *
     * 実行可能:
     * - サーバーコンソール
     * - OP
     *
     * 通常プレイヤーからは実行不可。
     */
    private void executeForCode(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length != 3
                || !args[1].equalsIgnoreCase("add")) {

            throw new WrongUsageException(
                    "/economy forcode add <player>"
            );
        }

        /*
         * プレイヤーから直接実行された場合は、
         * OPでなければ拒否する。
         *
         * サーバーコンソールからの実行は許可する。
         */
        if (sender instanceof EntityPlayer
                && !sender.canUseCommand(
                2,
                getName()
        )) {

            throw new CommandException(
                    "このコマンドは実行できません。"
            );
        }

        String playerName =
                args[2];

        EntityPlayer player =
                server.getPlayerList()
                        .getPlayerByUsername(
                                playerName
                        );

        if (player == null) {

            sender.sendMessage(
                    message(
                            TextFormatting.RED +
                                    "プレイヤーがオンラインではありません: " +
                                    playerName
                    )
            );

            return;
        }

        long reward =
                MochiVoteAPI.addVote(
                        server,
                        player,
                        "forcode"
                );

        PlayerVoteData data =
                MochiVoteAPI.getStatus(
                        server,
                        player
                );

        if (reward <= 0L) {

            sender.sendMessage(
                    message(
                            TextFormatting.YELLOW +
                                    "投票報酬は追加されませんでした: " +
                                    playerName
                    )
            );

            if (data != null
                    && data.getLastVoteDate() != null
                    && !data.getLastVoteDate().isEmpty()) {

                sender.sendMessage(
                        message(
                                TextFormatting.GRAY +
                                        "現在の連続投票日数: " +
                                        data.getStreak() +
                                        "日 / 残高: " +
                                        data.getBalance()
                        )
                );
            }

            return;
        }

        sender.sendMessage(
                message(
                        TextFormatting.GREEN +
                                "投票を記録しました: " +
                                playerName +
                                " +" +
                                reward +
                                " コイン"
                )
        );

        if (data != null) {

            sender.sendMessage(
                    message(
                            TextFormatting.GRAY +
                                    "連続投票日数: " +
                                    data.getStreak() +
                                    "日 / 残高: " +
                                    data.getBalance()
                    )
            );
        }
    }

    /*
     * /economy debug status <player>
     */
    private void debugStatus(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length != 3) {
            throw new WrongUsageException(
                    "/economy debug status <player>"
            );
        }

        PlayerVoteData data =
                findPlayerData(
                        server,
                        args[2]
                );

        if (data == null) {
            throw new CommandException(
                    "プレイヤーの投票データが見つかりません。"
            );
        }

        sendDebugData(
                sender,
                data
        );
    }

    /*
     * /economy debug add <player> <amount>
     */
    private void debugAdd(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length != 4) {
            throw new WrongUsageException(
                    "/economy debug add <player> <amount>"
            );
        }

        PlayerVoteData data =
                findPlayerData(
                        server,
                        args[2]
                );

        if (data == null) {
            throw new CommandException(
                    "プレイヤーの投票データが見つかりません。"
            );
        }

        long amount =
                parseAmount(args[3]);

        if (amount <= 0L) {
            throw new CommandException(
                    "金額は1以上にしてください。"
            );
        }

        boolean success =
                MochiVoteAPI.addBalance(
                        server,
                        data.getUuid(),
                        data.getName(),
                        amount
                );

        if (!success) {
            throw new CommandException(
                    "残高を追加できませんでした。"
            );
        }

        sender.sendMessage(
                message(
                        TextFormatting.GREEN +
                                "残高を " +
                                amount +
                                " 追加しました。"
                )
        );
    }

    /*
     * /economy debug set <player> <amount>
     */
    private void debugSet(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length != 4) {
            throw new WrongUsageException(
                    "/economy debug set <player> <amount>"
            );
        }

        PlayerVoteData data =
                findPlayerData(
                        server,
                        args[2]
                );

        if (data == null) {
            throw new CommandException(
                    "プレイヤーの投票データが見つかりません。"
            );
        }

        long amount =
                parseAmount(args[3]);

        if (amount < 0L) {
            throw new CommandException(
                    "金額は0以上にしてください。"
            );
        }

        boolean success =
                MochiVoteAPI.setBalance(
                        server,
                        data.getUuid(),
                        data.getName(),
                        amount
                );

        if (!success) {
            throw new CommandException(
                    "残高を設定できませんでした。"
            );
        }

        sender.sendMessage(
                message(
                        TextFormatting.GREEN +
                                "残高を " +
                                amount +
                                " に設定しました。"
                )
        );
    }

    /*
     * /economy debug streak <player> <days>
     */
    private void debugStreak(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length != 4) {
            throw new WrongUsageException(
                    "/economy debug streak <player> <days>"
            );
        }

        PlayerVoteData data =
                findPlayerData(
                        server,
                        args[2]
                );

        if (data == null) {
            throw new CommandException(
                    "プレイヤーの投票データが見つかりません。"
            );
        }

        long days =
                parseAmount(args[3]);

        if (days < 0L) {
            throw new CommandException(
                    "日数は0以上にしてください。"
            );
        }

        boolean success =
                MochiVoteAPI.setStreak(
                        server,
                        data.getUuid(),
                        data.getName(),
                        days
                );

        if (!success) {
            throw new CommandException(
                    "連続投票日数を設定できませんでした。"
            );
        }

        sender.sendMessage(
                message(
                        TextFormatting.GREEN +
                                "連続投票日数を " +
                                days +
                                " に設定しました。"
                )
        );
    }

    /*
     * /economy debug reset <player>
     */
    private void debugReset(
            MinecraftServer server,
            ICommandSender sender,
            String[] args
    ) throws CommandException {

        if (args.length != 3) {
            throw new WrongUsageException(
                    "/economy debug reset <player>"
            );
        }

        PlayerVoteData data =
                findPlayerData(
                        server,
                        args[2]
                );

        if (data == null) {
            throw new CommandException(
                    "プレイヤーの投票データが見つかりません。"
            );
        }

        boolean success =
                MochiVoteAPI.reset(
                        server,
                        data.getUuid(),
                        data.getName()
                );

        if (!success) {
            throw new CommandException(
                    "投票データをリセットできませんでした。"
            );
        }

        sender.sendMessage(
                message(
                        TextFormatting.GREEN +
                                "投票データをリセットしました。"
                )
        );
    }

    /*
     * プレイヤー名から保存済みデータを検索。
     */
    private PlayerVoteData findPlayerData(
            MinecraftServer server,
            String name
    ) {

        List<PlayerVoteData> players =
                MochiVoteAPI.getRanking(
                        server
                );

        for (PlayerVoteData data : players) {

            if (data.getName()
                    .equalsIgnoreCase(name)) {

                return data;
            }
        }

        /*
         * 現在オンラインのプレイヤーなら、
         * 保存データがまだ無くても作成する。
         */
        EntityPlayer onlinePlayer =
                server.getPlayerList()
                        .getPlayerByUsername(name);

        if (onlinePlayer != null) {

            return MochiVoteAPI.getPlayerData(
                    server,
                    onlinePlayer.getUniqueID(),
                    onlinePlayer.getName()
            );
        }

        return null;
    }

    /*
     * デバッグ情報表示。
     */
    private void sendDebugData(
            ICommandSender sender,
            PlayerVoteData data
    ) {

        sender.sendMessage(
                message(
                        TextFormatting.GOLD +
                                "===== Vote Debug ====="
                )
        );

        sender.sendMessage(
                message(
                        "Name: " +
                                data.getName()
                )
        );

        sender.sendMessage(
                message(
                        "UUID: " +
                                data.getUuid()
                )
        );

        sender.sendMessage(
                message(
                        "Streak: " +
                                data.getStreak()
                )
        );

        sender.sendMessage(
                message(
                        "Balance: " +
                                data.getBalance()
                )
        );

        sender.sendMessage(
                message(
                        "Last Vote: " +
                                (
                                        data.getLastVoteDate()
                                                .isEmpty()
                                                ? "none"
                                                : data.getLastVoteDate()
                                )
                )
        );
    }

    /*
     * ページ切り替えボタン。
     */
    private void sendPageControls(
            ICommandSender sender,
            int page,
            int totalPages
    ) {

        if (totalPages <= 1) {
            return;
        }

        TextComponentString line =
                new TextComponentString("");

        if (page > 1) {

            TextComponentString previous =
                    new TextComponentString(
                            "[前のページ]"
                    );

            previous.setStyle(
                    new Style()
                            .setColor(
                                    TextFormatting.AQUA
                            )
                            .setClickEvent(
                                    new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            "/economy server status " +
                                                    (page - 1)
                                    )
                            )
            );

            line.appendSibling(
                    previous
            );
        }

        if (page > 1 && page < totalPages) {
            line.appendText(" ");
        }

        if (page < totalPages) {

            TextComponentString next =
                    new TextComponentString(
                            "[次のページ]"
                    );

            next.setStyle(
                    new Style()
                            .setColor(
                                    TextFormatting.AQUA
                            )
                            .setClickEvent(
                                    new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            "/economy server status " +
                                                    (page + 1)
                                    )
                            )
            );

            line.appendSibling(
                    next
            );
        }

        sender.sendMessage(line);
    }

    /*
     * ヘルプ。
     */
    private void sendHelp(
            ICommandSender sender
    ) {

        sender.sendMessage(
                message(
                        TextFormatting.GOLD +
                                "===== Economy ====="
                )
        );

        sender.sendMessage(
                message(
                        TextFormatting.YELLOW +
                                "/economy status"
                )
        );

        sender.sendMessage(
                message(
                        TextFormatting.YELLOW +
                                "/economy get <amount>"
                )
        );

        sender.sendMessage(
                message(
                        TextFormatting.YELLOW +
                                "/economy server status [page]"
                )
        );

        sender.sendMessage(
                message(
                        TextFormatting.GRAY +
                                "管理者: /economy debug"
                )
        );
    }

    /*
     * デバッグヘルプ。
     */
    private void sendDebugHelp(
            ICommandSender sender
    ) {

        sender.sendMessage(
                message(
                        TextFormatting.GOLD +
                                "===== Economy Debug ====="
                )
        );

        sender.sendMessage(
                message(
                        "/economy debug status <player>"
                )
        );

        sender.sendMessage(
                message(
                        "/economy debug add <player> <amount>"
                )
        );

        sender.sendMessage(
                message(
                        "/economy debug set <player> <amount>"
                )
        );

        sender.sendMessage(
                message(
                        "/economy debug streak <player> <days>"
                )
        );

        sender.sendMessage(
                message(
                        "/economy debug reset <player>"
                )
        );
    }

    /*
     * 金額を安全にlongへ変換。
     */
    private long parseAmount(
            String value
    ) throws CommandException {

        try {

            return Long.parseLong(
                    value
            );

        } catch (NumberFormatException e) {

            throw new CommandException(
                    "数値が正しくありません: " +
                            value
            );
        }
    }

    /*
     * 文字列メッセージ生成。
     */
    private ITextComponent message(
            String text
    ) {

        return new TextComponentString(
                text
        );
    }

    @Override
    public List<String> getTabCompletions(
            MinecraftServer server,
            ICommandSender sender,
            String[] args,
            BlockPos targetPos
    ) {

        if (args.length == 1) {

            return getListOfStringsMatchingLastWord(
                    args,
                    "status",
                    "get",
                    "server",
                    "debug",
                    "forcode"
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("server")) {

            return getListOfStringsMatchingLastWord(
                    args,
                    "status"
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("debug")) {

            if (!sender.canUseCommand(
                    2,
                    getName()
            )) {
                return new ArrayList<>();
            }

            return getListOfStringsMatchingLastWord(
                    args,
                    "status",
                    "add",
                    "set",
                    "streak",
                    "reset"
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("forcode")) {

            /*
             * forcodeは外部システム用。
             * 一般プレイヤーには候補を表示しない。
             */
            if (sender instanceof EntityPlayer
                    && !sender.canUseCommand(
                    2,
                    getName()
            )) {
                return new ArrayList<>();
            }

            return getListOfStringsMatchingLastWord(
                    args,
                    "add"
            );
        }

        if (args.length == 3
                && args[0].equalsIgnoreCase("forcode")
                && args[1].equalsIgnoreCase("add")) {

            if (sender instanceof EntityPlayer
                    && !sender.canUseCommand(
                    2,
                    getName()
            )) {
                return new ArrayList<>();
            }

            return getListOfStringsMatchingLastWord(
                    args,
                    server.getOnlinePlayerNames()
            );
        }

        return new ArrayList<>();
    }
}
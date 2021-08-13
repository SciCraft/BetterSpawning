package ca.fxco.betterspawning;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class BetterSpawning implements ModInitializer {

    public static Algorithm currentAlgorithm = Algorithm.vanilla;

    public static final String TXT_PREFIX = "§8[§5Better§6Spawning§8] §f";

    public static final String[] descriptions = new String[]{
            "The vanilla spawning behaviour",
            "Faster Vanilla spawning by skipping empty sub chunks",
            "curved yes"
    };

    @Override
    public void onInitialize() {}

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {

        LiteralArgumentBuilder<ServerCommandSource> betterSpawning = CommandManager.literal("betterspawning");
        for (Algorithm algorithm : Algorithm.values()) {
            betterSpawning.then(
                CommandManager.literal(algorithm.name)
                    .then(CommandManager.literal("about").executes(c -> {
                        sendToPlayer(c.getSource().getPlayer(), descriptions[algorithm.ordinal()]);
                        return 1;
                    }))
                    .executes((c) -> {
                        if (algorithm.name.equals(currentAlgorithm.name)) {
                            sendToPlayer(c.getSource().getPlayer(), "Algorithm §5"+algorithm.name+" §fis already being used!");
                        } else {
                            currentAlgorithm = algorithm;
                            sendToPlayer(c.getSource().getPlayer(), "Changed current algorithm to §5"+algorithm.name);
                        }
                        return 1;
                    })
            );
        }
        betterSpawning.executes((c) -> {
            sendToPlayer(c.getSource().getPlayer(), "The current algorithm is §5"+currentAlgorithm.name);
            return 1;
        });
        dispatcher.register(betterSpawning);
    }

    public static void sendToPlayer(ServerPlayerEntity player, String str) {
        if (player != null) {
            player.sendMessage(new LiteralText(TXT_PREFIX+str), false);
        }
    }

    public enum Algorithm {
        vanilla("vanilla", true),
        emptySubChunkOptimization("emptySubChunkOptimization", true),
        bellCurve("bellCurve", false);

        public final String name;
        public final boolean isVanilla;

        Algorithm(String name, boolean isVanilla) {
            this.name = name;
            this.isVanilla = isVanilla;
        }
    }
}
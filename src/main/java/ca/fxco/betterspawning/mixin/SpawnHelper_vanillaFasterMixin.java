package ca.fxco.betterspawning.mixin;

import ca.fxco.betterspawning.BetterSpawning;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SpawnHelper.class)
public class SpawnHelper_vanillaFasterMixin {

    @Shadow public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, SpawnHelper.Checker checker, SpawnHelper.Runner runner) {}

    @Shadow private static BlockPos method_37843(World world, WorldChunk worldChunk, int i) { return (BlockPos) BlockPos.ZERO;}

    @Inject(
            method= "spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void fasterSpawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, SpawnHelper.Checker checker, SpawnHelper.Runner runner, CallbackInfo ci){
        if (BetterSpawning.currentAlgorithm == BetterSpawning.Algorithm.emptySubChunkOptimization) {
            for(int i = world.getTopY() - 16; i >= world.getBottomY(); i -= 16) {
                ChunkSection chunkSection = chunk.getSectionArray()[chunk.getSectionIndex(i)];
                if (chunkSection != WorldChunk.EMPTY_SECTION && !chunkSection.isEmpty()) {
                    if (!(world.getRandom().nextFloat() > 0.24F)) {
                        BlockPos blockPos = method_37843(world, chunk, i);
                        spawnEntitiesInChunk(group, world, chunk, blockPos, checker, runner);
                    }
                }
            }
            ci.cancel();
        }
    }
}
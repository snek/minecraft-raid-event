package net.example.raid_event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
// Import for Config needs to be added if Config class is restored
// import net.example.raid_event.Config;

@Environment(EnvType.SERVER)
public class RaidMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Mod initialization logic (e.g., config loading, command registration) can go here.
        // Removed placeholder event registrations as Mixin handles the hit detection.
        System.out.println("Raid Event Mod Initialized!");
    }

    // Renamed and made static to be called from Mixin
    public static ActionResult handleArrowHit(ArrowEntity arrow, BlockHitResult hitResult) {
        if (!(arrow.getWorld() instanceof ServerWorld serverWorld)) { return ActionResult.PASS; }
        if (hitResult.getBlockPos() == null) { return ActionResult.PASS; }
        
        if (serverWorld.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.BELL) {
            serverWorld.getPlayers().forEach(p -> p.sendMessage(Text.literal("Arrow hit bell!"), false));
                        
            Entity owner = arrow.getOwner();
            if (!(owner instanceof ServerPlayerEntity player)) { 
                serverWorld.getPlayers().forEach(p -> p.sendMessage(Text.literal("Arrow not shot by player."), false));
                return ActionResult.PASS; 
            }
            
            if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) { 
                serverWorld.getPlayers().forEach(p -> p.sendMessage(Text.literal("Mob spawning disabled."), false));
                return ActionResult.PASS; 
            }
            
            serverWorld.getPlayers().forEach(p -> p.sendMessage(Text.literal("Starting raid at bell location!"), false));
            serverWorld.getRaidManager().startRaid((ServerPlayerEntity) player, hitResult.getBlockPos());  // Fixed arguments
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}

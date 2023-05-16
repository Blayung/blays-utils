package com.blay.blaysutils.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import com.blay.blaysutils.BlaysUtils;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Inject(at = @At("RETURN"), method = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	private void inject(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        for(String line : BlaysUtils.config.hello_text) {
            line=line.replace("%%", "\uf420");
            line=line.replace("%p", player.getName().getString());
            line=line.replace("\uf420", "%");
            player.sendMessage(Text.literal(line));
        }
	}
}

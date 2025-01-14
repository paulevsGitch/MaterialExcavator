package paulevs.materialexcavator.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.living.player.ServerPlayer;
import net.minecraft.packet.login.LoginRequestPacket;
import net.minecraft.server.network.ServerPacketHandler;
import net.modificationstation.stationapi.api.network.ModdedPacketHandler;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.materialexcavator.packet.ModOnServerPacket;

@Mixin(ServerPacketHandler.class)
public class ServerPacketHandlerMixin {
	@Inject(method = "complete", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/entity/living/player/ServerPlayer;method_317()V",
		shift = Shift.AFTER
	))
	private void materialexcavator_onPlayerLogin(LoginRequestPacket login, CallbackInfo info, @Local ServerPlayer player) {
		if (!(ServerPacketHandler.class.cast(this) instanceof ModdedPacketHandler handler)) return;
		if (!handler.getMods().containsKey("MaterialExcavator")) return;
		PacketHelper.sendTo(player, new ModOnServerPacket());
	}
}

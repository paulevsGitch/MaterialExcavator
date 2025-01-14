package paulevs.materialexcavator.mixin.server;

import net.minecraft.entity.living.player.ServerPlayer;
import net.minecraft.server.network.ServerPlayerPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerPacketHandler.class)
public interface ServerPlayerPacketHandlerAccessor {
	@Accessor("serverPlayer")
	ServerPlayer materialexcavator_getServerPlayer();
}

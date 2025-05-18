package paulevs.materialexcavator.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.ServerPlayer;
import net.minecraft.packet.AbstractPacket;
import net.minecraft.packet.PacketHandler;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import paulevs.materialexcavator.mixin.server.ServerPlayerPacketHandlerAccessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ExcavatorModePacket extends AbstractPacket implements ManagedPacket<ExcavatorModePacket> {
	public static final PacketType<ExcavatorModePacket> TYPE = PacketType.builder(true, true, ExcavatorModePacket::new).build();
	private boolean mode;
	
	public ExcavatorModePacket() {}
	
	public ExcavatorModePacket(boolean mode) {
		this.mode = mode;
	}
	
	@Override
	public void read(DataInputStream in) {
		try {
			mode = in.readBoolean();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void write(DataOutputStream out) {
		try {
			out.writeBoolean(mode);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void apply(PacketHandler handler) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			applyOnServer(handler);
		}
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			applyOnClient();
		}
	}
	
	@Override
	public int length() {
		return 1;
	}
	
	@Override
	public PacketType<ExcavatorModePacket> getType() {
		return TYPE;
	}
	
	@Environment(EnvType.SERVER)
	private void applyOnServer(PacketHandler handler) {
		ServerPlayerPacketHandlerAccessor accessor = (ServerPlayerPacketHandlerAccessor) handler;
		ServerPlayer player = accessor.materialexcavator_getServerPlayer();
		player.materialexcavator_setExcavationMode(mode);
		PacketHelper.sendTo(player, new ExcavatorModePacket(mode));
	}
	
	@Environment(EnvType.CLIENT)
	private void applyOnClient() {
		@SuppressWarnings("deprecation")
		Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
		minecraft.player.materialexcavator_setExcavationMode(mode);
	}
}

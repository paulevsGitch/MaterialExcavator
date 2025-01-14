package paulevs.materialexcavator.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.packet.AbstractPacket;
import net.minecraft.packet.PacketHandler;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import paulevs.materialexcavator.MaterialExcavator;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ModOnServerPacket extends AbstractPacket implements ManagedPacket<ModOnServerPacket> {
	public static final PacketType<ModOnServerPacket> TYPE = PacketType.builder(true, true, ModOnServerPacket::new).build();
	
	public ModOnServerPacket() {}
	
	@Override
	public void read(DataInputStream in) {}
	
	@Override
	public void write(DataOutputStream out) {}
	
	@Override
	public void apply(PacketHandler handler) {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
		MaterialExcavator.isPresent = true;
		@SuppressWarnings("deprecation")
		Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
		minecraft.player.materialexcavator_setExcavationMode(false);
	}
	
	@Override
	public int length() {
		return 0;
	}
	
	@Override
	public PacketType<ModOnServerPacket> getType() {
		return TYPE;
	}
}

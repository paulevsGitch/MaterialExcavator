package paulevs.materialexcavator.listener;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Identifier;
import paulevs.materialexcavator.packet.ExcavatorModePacket;
import paulevs.materialexcavator.packet.ModOnServerPacket;

public class CommonListener {
	@EventListener
	public static void onPacketsRegister(PacketRegisterEvent event) {
		Registry.register(PacketTypeRegistry.INSTANCE, Identifier.of("materialexcavator:mode"), ExcavatorModePacket.TYPE);
		Registry.register(PacketTypeRegistry.INSTANCE, Identifier.of("materialexcavator:is_present"), ModOnServerPacket.TYPE);
	}
}

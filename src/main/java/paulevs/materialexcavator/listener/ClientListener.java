package paulevs.materialexcavator.listener;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent.Environment;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import paulevs.materialexcavator.MaterialExcavator;
import paulevs.materialexcavator.packet.ExcavatorModePacket;

public class ClientListener {
	private static final KeyBinding KEY = new KeyBinding("key.materialexcavator.name", Keyboard.KEY_GRAVE);
	
	@EventListener
	public void onKeyRegister(KeyBindingRegisterEvent event) {
		event.keyBindings.add(KEY);
	}
	
	@EventListener
	public void onKeyPress(KeyStateChangedEvent event) {
		if (event.environment == Environment.IN_GAME && Keyboard.getEventKey() == KEY.key && Keyboard.getEventKeyState()) {
			@SuppressWarnings("deprecation")
			Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
			if (minecraft.level.isRemote && !MaterialExcavator.isPresent) return;
			boolean mode = !minecraft.player.materialexcavator_isInExcavationMode();
			minecraft.player.materialexcavator_setExcavationMode(mode);
			if (mode) MaterialExcavator.startBreaking = true;
			if (minecraft.level.isRemote) {
				PacketHelper.send(new ExcavatorModePacket(mode));
			}
		}
	}
}

package paulevs.materialexcavator;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent.Environment;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import org.lwjgl.input.Keyboard;

public class ClientListener {
	private static final KeyBinding KEY = new KeyBinding("materiaexcavator", Keyboard.KEY_GRAVE);
	
	@EventListener
	public void onKeyRegister(KeyBindingRegisterEvent event) {
		event.keyBindings.add(KEY);
	}
	
	@EventListener
	public void onKeyPress(KeyStateChangedEvent event) {
		if (event.environment == Environment.IN_GAME && Keyboard.getEventKey() == KEY.key && Keyboard.getEventKeyState()) {
			@SuppressWarnings("deprecation")
			Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
			boolean mode = minecraft.player.materialexcavator_isInExcavationMode();
			minecraft.player.materialexcavator_setExcavationMode(!mode);
		}
	}
}

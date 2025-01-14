package paulevs.materialexcavator;

import net.modificationstation.stationapi.api.util.Util;

public interface ExcavatorPlayer {
	default boolean materialexcavator_isInExcavationMode() {
		return Util.assertImpl();
	}
	
	default void materialexcavator_setExcavationMode(boolean mode) {
		Util.assertImpl();
	}
}

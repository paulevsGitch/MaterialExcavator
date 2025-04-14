package paulevs.materialexcavator.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.modificationstation.stationapi.impl.item.ToolEffectivenessImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import paulevs.materialexcavator.MaterialExcavator;

@Mixin(ToolEffectivenessImpl.class)
public class ToolEffectivenessImplMixin {
	@ModifyReturnValue(method = "getMiningSpeedMultiplier", at = @At("RETURN"))
	private static float materialexcavator_changeStrength(float original) {
		return MaterialExcavator.scaleSpeed(original);
	}
}

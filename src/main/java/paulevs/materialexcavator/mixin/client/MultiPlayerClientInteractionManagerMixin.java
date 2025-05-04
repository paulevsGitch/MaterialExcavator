package paulevs.materialexcavator.mixin.client;

import net.minecraft.client.MultiPlayerClientInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.materialexcavator.MaterialExcavator;

@Mixin(MultiPlayerClientInteractionManager.class)
public class MultiPlayerClientInteractionManagerMixin {
	@Shadow private float oldHardness;
	@Shadow private float hardness;
	
	@Inject(method = "tick", at = @At("TAIL"))
	private void materialexcavator_onTick(CallbackInfo info) {
		if (MaterialExcavator.startBreaking) {
			MaterialExcavator.startBreaking = false;
			oldHardness = 0;
			hardness = 0;
		}
	}
}

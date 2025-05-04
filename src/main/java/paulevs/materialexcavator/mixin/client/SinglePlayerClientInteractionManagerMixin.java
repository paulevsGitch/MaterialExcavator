package paulevs.materialexcavator.mixin.client;

import net.minecraft.client.SinglePlayerClientInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.materialexcavator.MaterialExcavator;

@Mixin(SinglePlayerClientInteractionManager.class)
public class SinglePlayerClientInteractionManagerMixin {
	@Shadow private float oldDamage;
	@Shadow private float damage;
	
	@Inject(method = "tick", at = @At("TAIL"))
	private void materialexcavator_onTick(CallbackInfo info) {
		if (MaterialExcavator.startBreaking) {
			MaterialExcavator.startBreaking = false;
			oldDamage = 0;
			damage = 0;
		}
	}
}

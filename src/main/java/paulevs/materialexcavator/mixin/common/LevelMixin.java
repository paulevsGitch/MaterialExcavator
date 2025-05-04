package paulevs.materialexcavator.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.technical.ItemEntity;
import net.minecraft.level.Level;
import net.minecraft.util.maths.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.materialexcavator.MaterialExcavator;

@Mixin(Level.class)
public class LevelMixin {
	/*@Inject(method = "spawnEntity", at = @At("HEAD"))
	private void materialexcavator_spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if (entity.level.isRemote || !(entity instanceof ItemEntity)) return;
		BlockPos pos = MaterialExcavator.START_POS;
		entity.setPosition(pos.x, pos.y, pos.z);
	}*/
}

package paulevs.materialexcavator.mixin.common;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import paulevs.materialexcavator.ExcavatorPlayer;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ExcavatorPlayer {
	@Unique private boolean materialexcavator_mode;
	
	public PlayerEntityMixin(Level level) {
		super(level);
	}
	
	@Override
	public boolean materialexcavator_isInExcavationMode() {
		return materialexcavator_mode;
	}
	
	@Override
	public void materialexcavator_setExcavationMode(boolean mode) {
		materialexcavator_mode = mode;
	}
}

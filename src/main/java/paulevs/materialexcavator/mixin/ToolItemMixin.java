package paulevs.materialexcavator.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tool.ToolItem;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.StationItem;
import net.modificationstation.stationapi.api.item.tool.StationTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.materialexcavator.MaterialExcavator;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin implements StationItem, StationTool {
	@Override
	public boolean preMine(ItemStack stack, BlockState blockState, int x, int y, int z, int side, PlayerEntity player) {
		if (player.materialexcavator_isInExcavationMode()) {
			MaterialExcavator.targetPlayer = player;
			MaterialExcavator.updatePositions(x, y, z, blockState, stack);
		}
		else MaterialExcavator.targetPlayer = null;
		return true;
	}
	
	@ModifyReturnValue(method = "getStrengthOnBlock", at = @At("RETURN"))
	private float materialexcavator_changeStrength(float original) {
		return MaterialExcavator.getStrength(original);
	}
	
	@Inject(method = "postMine", at = @At("HEAD"))
	private void materialexcavator_onPostMine(
		ItemStack stack, int blockID, int x, int y, int z, LivingEntity entity, CallbackInfoReturnable<Boolean> info
	) {
		MaterialExcavator.breakBlocks(stack);
	}
}

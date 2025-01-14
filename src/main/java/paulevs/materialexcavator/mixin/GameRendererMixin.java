package paulevs.materialexcavator.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LevelRenderer;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import paulevs.materialexcavator.MaterialExcavator;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@WrapOperation(method = "delta", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/LevelRenderer;renderBlockOutline(Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/util/hit/HitResult;ILnet/minecraft/item/ItemStack;F)V"
	))
	private void materialexcavator_renderOutline(
		LevelRenderer renderer, PlayerEntity player, HitResult hit, int flag, ItemStack stack, float delta, Operation<Void> original
	) {
		MaterialExcavator.targetPlayer = player;
		if (!MaterialExcavator.renderOutlines(hit, stack, delta)) {
			original.call(renderer, player, hit, flag, stack, delta);
		}
	}
}

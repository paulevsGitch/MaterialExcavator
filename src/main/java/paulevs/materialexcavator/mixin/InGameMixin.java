package paulevs.materialexcavator.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.InGame;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGame.class)
public abstract class InGameMixin {
	@Unique private static final ItemStack MATERIALEXCAVATOR_ICON_STACK = new ItemStack(Item.ironPickaxe);
	
	@Shadow private Minecraft minecraft;
	@Shadow private static ItemRenderer itemRenderer;
	
	@Shadow protected abstract void renderHotbarSlot(int slot, int x, int y, float f);
	
	@Inject(method = "renderHud", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/render/RenderHelper;disableLighting()V",
		ordinal = 0,
		shift = Shift.BEFORE
	))
	private void materialexcavator_renderIcon(
		float delta, boolean i, int j, int par4, CallbackInfo info,
		@Local(ordinal = 2) int width,
		@Local(ordinal = 3) int height
	) {
		if (!minecraft.player.materialexcavator_isInExcavationMode()) return;
		
		int px = (width >> 1) - 113;
		int py = height - 20;
		
		itemRenderer.coloriseItem = false;
		GL11.glColor3f(1.0F, 0.0F, 1.0F);
		GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_REPLACE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_RGB, GL13.GL_PREVIOUS);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_RGB, GL11.GL_TEXTURE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
		
		for (byte n = 0; n < 4; n++) {
			int offset = ((n & 1) << 1) - 1;
			itemRenderer.renderStackInGUI(
				minecraft.textRenderer,
				minecraft.textureManager,
				MATERIALEXCAVATOR_ICON_STACK,
				n < 2 ? px + offset : px,
				n > 1 ? py + offset : py
			);
		}
		
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_ALPHA, GL13.GL_PREVIOUS);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_ALPHA, GL11.GL_TEXTURE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
		
		itemRenderer.coloriseItem = true;
		
		itemRenderer.renderStackInGUI(
			minecraft.textRenderer,
			minecraft.textureManager,
			MATERIALEXCAVATOR_ICON_STACK,
			px, py
		);
	}
}

package paulevs.materialexcavator;

import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.maths.BlockPos;
import net.minecraft.util.maths.Box;
import net.minecraft.util.maths.Vec3D;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.item.tool.StationTool;
import net.modificationstation.stationapi.api.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class MaterialExcavator {
	private static final List<Line> LINES = new ArrayList<>();
	private static final List<BlockPos> POSITIONS = new ArrayList<>();
	public static PlayerEntity targetPlayer;
	private static BlockState target;
	
	@Environment(EnvType.CLIENT)
	private static int updateTick;
	
	@Environment(EnvType.CLIENT)
	public static boolean isPresent;
	
	public static void updatePositions(int x, int y, int z, BlockState target, ItemStack stack) {
		MaterialExcavator.target = target;
		
		POSITIONS.clear();
		if (targetPlayer.level.isRemote && !isPresent) return;
		
		StationTool tool = (StationTool) stack.getType();
		if (!target.isIn(tool.getEffectiveBlocks(stack))) return;
		
		int miningLevel = tool.getMaterial(stack).getMiningLevel();
		int radius = (miningLevel << 3) + 1;
		int maxCount = (miningLevel << 4) + 16;
		maxCount = Math.min(maxCount, stack.getType().getMaxDamage(stack) - stack.getDamage());
		
		FloodFillSearch.getBlocks(targetPlayer.level, x, y, z, target, radius, POSITIONS);
		if (POSITIONS.size() > maxCount) {
			POSITIONS.sort((p1, p2) -> {
				int dx = p1.x - x;
				int dy = p1.y - y;
				int dz = p1.z - z;
				
				int d1 = dx * dx + dy * dy + dz * dz;
				
				dx = p2.x - x;
				dy = p2.y - y;
				dz = p2.z - z;
				
				int d2 = dx * dx + dy * dy + dz * dz;
				
				return Integer.compare(d1, d2);
			});
			POSITIONS.subList(maxCount, POSITIONS.size()).clear();
		}
	}
	
	public static void breakBlocks(ItemStack stack) {
		if (targetPlayer == null || !targetPlayer.materialexcavator_isInExcavationMode()) return;
		
		Level level = targetPlayer.level;
		if (level.isRemote) return;
		
		for (BlockPos pos : POSITIONS) {
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() != target.getBlock()) continue;
			
			int meta = level.getBlockMeta(pos.x, pos.y, pos.z);
			state.getBlock().afterBreak(level, targetPlayer, pos.x, pos.y, pos.z, state, meta);
			level.setBlockStateWithNotify(pos, States.AIR.get());
			
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				int dx = (int) targetPlayer.x - pos.x;
				int dy = (int) targetPlayer.y - pos.y;
				int dz = (int) targetPlayer.z - pos.z;
				
				if (dx * dx + dy * dy + dz * dz < 512) {
					BlockSounds sounds = state.getBlock().sounds;
					level.playSound(
						pos.x + 0.5,
						pos.y + 0.5,
						pos.z + 0.5,
						sounds.getBreakSound(),
						sounds.getVolume() * 0.5F,
						sounds.getPitch()
					);
					
					@SuppressWarnings("deprecation")
					Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
					minecraft.particleManager.addBlockBreakParticles(pos.x, pos.y, pos.z, state.getBlock().id, 0);
				}
			}
			
			stack.applyDamage(1, null);
			if (stack.getDamage() > stack.getType().getMaxDamage(stack)) {
				break;
			}
		}
		
		targetPlayer = null;
	}
	
	@Environment(EnvType.CLIENT)
	public static boolean renderOutlines(HitResult hit, ItemStack stack, float delta) {
		if (targetPlayer.level.isRemote && !isPresent) return false;
		if (!targetPlayer.materialexcavator_isInExcavationMode()) return false;
		if (stack == null || !(stack.getType() instanceof StationTool)) return false;
		
		if ((updateTick++ & 7) == 0) {
			target = targetPlayer.level.getBlockState(hit.x, hit.y, hit.z);
			updatePositions(hit.x, hit.y, hit.z, target, stack);
			updateOutlines(hit.x, hit.y, hit.z);
		}
		
		if (POSITIONS.size() < 2) return false;
		renderOutlines(hit.x, hit.y, hit.z, delta);
		
		return true;
	}
	
	public static float scaleSpeed(float speed) {
		if (targetPlayer != null && targetPlayer.materialexcavator_isInExcavationMode() && POSITIONS.size() > 1) {
			ItemStack stack = targetPlayer.getHeldItem();
			if (stack != null && stack.getType() instanceof StationTool && stack.getDamage() < stack.getType().getMaxDamage(stack)) {
				speed /= POSITIONS.size();//Math.min(POSITIONS.size() * 0.5F + 0.5F, 10.0F);
			}
		}
		return speed;
	}
	
	@Environment(EnvType.CLIENT)
	private static void updateOutlines(int x, int y, int z) {
		LINES.clear();
		addBoundingBox(x, y, z);
		for (BlockPos pos : POSITIONS) {
			addBoundingBox(pos.x, pos.y, pos.z);
		}
	}
	
	private static void addBoundingBox(int x, int y, int z) {
		Block block = targetPlayer.level.getBlockState(x, y, z).getBlock();
		block.updateBoundingBox(targetPlayer.level, x, y, z);
		Box box = block.getOutlineShape(targetPlayer.level, x, y, z);
		
		addLine(box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ);
		addLine(box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ);
		addLine(box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ);
		addLine(box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ);
		
		addLine(box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ);
		addLine(box.minX, box.maxY, box.maxZ, box.maxX, box.maxY, box.maxZ);
		addLine(box.minX, box.maxY, box.minZ, box.minX, box.maxY, box.maxZ);
		addLine(box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ);
		
		addLine(box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ);
		addLine(box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ);
		addLine(box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ);
		addLine(box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ);
	}
	
	@Environment(EnvType.CLIENT)
	private static void renderOutlines(int x, int y, int z, float delta) {
		double dx = MathHelper.lerp(delta, targetPlayer.prevRenderX, targetPlayer.x);
		double dy = MathHelper.lerp(delta, targetPlayer.prevRenderY, targetPlayer.y);
		double dz = MathHelper.lerp(delta, targetPlayer.prevRenderZ, targetPlayer.z);
		
		float dx2 = (float) (x - dx);
		float dy2 = (float) (y - dy);
		float dz2 = (float) (z - dz);
		float l = 0.01F / MathHelper.sqrt(dx2 * dx2 + dy2 * dy2 + dz2 * dz2);
		dx += dx2 * l;
		dy += dy2 * l;
		dz += dz2 * l;
		
		GL11.glColor4f(1.0F, 0.0F, 1.0F, 1.0F);
		GL11.glLineWidth(2.0F);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		
		Tessellator tessellator = Tessellator.INSTANCE;
		tessellator.start(GL11.GL_LINES);
		
		for (Line line : LINES) {
			Vec3D a = line.left();
			Vec3D b = line.right();
			tessellator.addVertex(a.x - dx, a.y - dy, a.z - dz);
			tessellator.addVertex(b.x - dx, b.y - dy, b.z - dz);
		}
		
		tessellator.render();
		
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	private static void addLine(double x1, double y1, double z1, double x2, double y2, double z2) {
		Line line = new Line(Vec3D.make(x1, y1, z1), Vec3D.make(x2, y2, z2));
		int index = LINES.indexOf(line);
		if (index != -1) LINES.remove(index);
		else LINES.add(line);
	}
	
	private static class Line extends ObjectObjectImmutablePair<Vec3D, Vec3D> {
		public Line(Vec3D left, Vec3D right) {
			super(left, right);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Line line)) return false;
			if (isSameVector(right, line.right) && isSameVector(left, line.left)) return true;
			return isSameVector(right, line.left) && isSameVector(left, line.right);
		}
		
		private static boolean isSameVector(Vec3D a, Vec3D b) {
			return isSameDouble(a.x, b.x) && isSameDouble(a.y, b.y) && isSameDouble(a.z, b.z);
		}
		
		private static boolean isSameDouble(double a, double b) {
			return Math.abs(a - b) < 0.005;
		}
	}
}

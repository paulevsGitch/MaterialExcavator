package paulevs.materialexcavator;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.level.Level;
import net.minecraft.util.maths.BlockPos;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FloodFillSearch {
	private static final Map<BlockState, List<Set<BlockState>>> PATTERNS = new Reference2ReferenceOpenHashMap<>();
	private static final IntSet POSITIONS = new IntOpenHashSet();
	private static final IntList[] BUFFERS = new IntList[] {
		new IntArrayList(),
		new IntArrayList()
	};
	
	private static byte bufferIndex;
	private static int centerX;
	private static int centerY;
	private static int centerZ;
	
	protected static void registerPattern(Set<BlockState> pattern) {
		if (pattern.size() < 2) return;
		for (BlockState state : pattern) {
			List<Set<BlockState>> list = PATTERNS.computeIfAbsent(state, s -> new ArrayList<>());
			list.add(pattern);
		}
	}
	
	public static void getBlocks(Level level, int x, int y, int z, BlockState target, int radius, List<BlockPos> output) {
		centerX = x - 512;
		centerY = y - 512;
		centerZ = z - 512;
		
		IntList startPositions = BUFFERS[bufferIndex];
		startPositions.clear();
		
		output.add(new BlockPos(x, y, z));
		int startIndex = getIndex(x, y, z);
		startPositions.add(startIndex);
		POSITIONS.add(startIndex);
		
		List<Set<BlockState>> patterns = PATTERNS.get(target);
		
		while (!startPositions.isEmpty()) {
			bufferIndex = (byte) ((bufferIndex + 1) & 1);
			IntList endPositions = BUFFERS[bufferIndex];
			endPositions.clear();
			
			for (int index : startPositions) {
				int sx = getX(index);
				int sy = getY(index);
				int sz = getZ(index);
				
				if (MaterialExcavator.EXTENDED_AREA.getValue()) {
					for (byte i = 0; i < 27; i++) {
						if (i == 13) continue;
						int px = sx + (i % 3) - 1;
						if (Math.abs(px - x) > radius) continue;
						int py = sy + ((i / 3) % 3) - 1;
						if (Math.abs(py - y) > radius) continue;
						int pz = sz + i / 9 - 1;
						if (Math.abs(pz - z) > radius) continue;
						int leafIndex = getIndex(px, py, pz);
						if (POSITIONS.contains(leafIndex)) continue;
						BlockState state = level.getBlockState(px, py, pz);
						if (state.getBlock() != target.getBlock() && !isInPattern(state, patterns)) continue;
						output.add(new BlockPos(px, py, pz));
						endPositions.add(leafIndex);
						POSITIONS.add(leafIndex);
					}
				}
				else {
					for (byte i = 0; i < 6; i++) {
						Direction dir = Direction.byId(i);
						int px = sx + dir.getOffsetX();
						if (Math.abs(px - x) > radius) continue;
						int py = sy + dir.getOffsetY();
						if (Math.abs(py - y) > radius) continue;
						int pz = sz + dir.getOffsetZ();
						if (Math.abs(pz - z) > radius) continue;
						int leafIndex = getIndex(px, py, pz);
						if (POSITIONS.contains(leafIndex)) continue;
						BlockState state = level.getBlockState(px, py, pz);
						if (state.getBlock() != target.getBlock() && !isInPattern(state, patterns)) continue;
						output.add(new BlockPos(px, py, pz));
						endPositions.add(leafIndex);
						POSITIONS.add(leafIndex);
					}
				}
			}
			
			startPositions = endPositions;
		}
		
		POSITIONS.clear();
	}
	
	private static boolean isInPattern(BlockState state, List<Set<BlockState>> patterns) {
		if (patterns != null) {
			for (Set<BlockState> pattern : patterns) {
				if (pattern.contains(state)) return true;
			}
		}
		return false;
	}
	
	private static int getIndex(int x, int y, int z) {
		return ((x - centerX) & 1023) << 20 | ((y - centerY) & 1023) << 10 | (z - centerZ) & 1023;
	}
	
	private static int getX(int index) {
		return (index >> 20) + centerX;
	}
	
	private static int getY(int index) {
		return ((index >> 10) & 1023) + centerY;
	}
	
	private static int getZ(int index) {
		return (index & 1023) + centerZ;
	}
}

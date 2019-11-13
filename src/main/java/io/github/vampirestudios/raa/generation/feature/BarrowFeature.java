package io.github.vampirestudios.raa.generation.feature;

import io.github.vampirestudios.raa.RandomlyAddingAnything;
import io.github.vampirestudios.raa.generation.dimensions.DimensionData;
import io.github.vampirestudios.raa.utils.FeatureUtils;
import io.github.vampirestudios.raa.utils.OctaveOpenSimplexNoise;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

import java.util.Random;

public class BarrowFeature extends Feature<DefaultFeatureConfig> {
	
	private static final OctaveOpenSimplexNoise offsetNoise = new OctaveOpenSimplexNoise(new Random(0), 2, 30D, 4D, 2D);
	private static BlockState STONE;
	private static final BlockState AIR = Blocks.AIR.getDefaultState();

	private static final EntityType<?> SKELETON = EntityType.SKELETON;
	
	private static final Identifier LOOT_TABLE = new Identifier(RandomlyAddingAnything.MOD_ID, "chests/barrow");
	
	public BarrowFeature(DimensionData dimensionData) {
		super(DefaultFeatureConfig::deserialize);
		STONE = Registry.BLOCK.get(new Identifier(RandomlyAddingAnything.MOD_ID, dimensionData.getName().toLowerCase() + "_stone")).getDefaultState();
	}
	
	@Override
	public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random rand, BlockPos pos, DefaultFeatureConfig config) {
		if (world.getBlockState(pos.add(0, -1, 0)).isAir() || !world.getBlockState(pos.add(0, -1, 0)).isOpaque())
			return true;
		final BiomeSource source = chunkGenerator.getBiomeSource();
		
		return this.generate(world, rand, pos, (x, y, z) -> source.getStoredBiome(x, y, z).getSurfaceConfig());
	}
	
	private boolean generate(IWorld world, Random rand, BlockPos pos, Coordinate3iFunction<SurfaceConfig> configFunction) {
		int centreX = pos.getX() + rand.nextInt(16) - 8;
		int centreZ = pos.getZ() + rand.nextInt(16) - 8;
		int lowY = pos.getY() - 3;
		
		int radius = rand.nextInt(6) + 7;
		int height = rand.nextInt(4) + 6;
		
		double radiusSquared = radius * radius;
		
		Vec3d origin = new Vec3d(centreX, 0, centreZ);
		
		BlockPos.Mutable posMutable = new BlockPos.Mutable();
		
		for (int xOffset = -radius; xOffset <= radius; ++xOffset) {
			int x = centreX + xOffset;
			
			for (int zOffset = -radius; zOffset <= radius; ++zOffset) {
				int z = centreZ + zOffset;
				
				Vec3d position = new Vec3d(x, 0, z);
				double sqrDistTo = position.squaredDistanceTo(origin);
				if (sqrDistTo <= radiusSquared) {
					double progress = MathHelper.perlinFade(sqrDistTo / radiusSquared);
					int heightOffset = (int) MathHelper.lerp(progress, height, 0);
					heightOffset += (int) MathHelper.lerp(progress, offsetNoise.sample(x, z), 0);
					
					posMutable.setX(x);
					posMutable.setZ(z);
					
					this.generateBarrowColumn(world, rand, lowY, heightOffset, posMutable, configFunction.get(x, lowY + heightOffset, z));
				}
			}
		}
		return true;
	}
	
	private void generateBarrowColumn(IWorld world, Random rand, int lowY, int heightOffset, BlockPos.Mutable pos, SurfaceConfig surfaceConfig) {
		int upperY = lowY + heightOffset;
		
		for (int y = upperY; y >= lowY; --y) {
			pos.setY(y);
			if (y == upperY) {
				world.setBlockState(pos, surfaceConfig.getTopMaterial(), 19);
			} else if (y > upperY - 3) {
				world.setBlockState(pos, surfaceConfig.getUnderMaterial(), 19);
			} else if (y == lowY + 2 && rand.nextInt(32) == 0) {
				if (rand.nextInt(3) == 0) {
					FeatureUtils.setLootChest(world, pos, LOOT_TABLE, rand);
				} else {
					FeatureUtils.setSpawner(world, pos, SKELETON);
				}
			} else {
				world.setBlockState(pos, y <= lowY + 1 ? STONE : AIR, 19);
			}
		}
	}
	
	private interface Coordinate2iFunction<T> {
		T get(int x, int z);
	}

	private interface Coordinate3iFunction<T> {
		T get(int x, int y, int z);
	}
	
}
package fluke.stygian.world.genlayers;

import fluke.stygian.Stygian;
import fluke.stygian.config.Configs;
import fluke.stygian.world.BiomeRegistrar;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GenLayerEndBiomes extends GenLayer {
    private final int SKY_ID;
    private final int END_FOREST_ID;
    private final int END_VOLCANO_ID;
    private final int PLACEHOLDER;
    private final static int MAIN_ISLAND_SIZE;

    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    static {
        MAIN_ISLAND_SIZE = (int) (80 / Math.pow(2, (Configs.worldgen.endBiomeSize - 1)));
    }

    public GenLayerEndBiomes(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
        SKY_ID = Biome.getIdForBiome(Biomes.SKY);
        END_FOREST_ID = Biome.getIdForBiome(BiomeRegistrar.END_JUNGLE);
        END_VOLCANO_ID = Biome.getIdForBiome(BiomeRegistrar.END_VOLCANO);
        PLACEHOLDER = SKY_ID;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] inLayer = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] outLayer = IntCache.getIntCache(areaWidth * areaHeight);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < areaHeight; ++i) {
            for (int j = 0; j < areaWidth; ++j) {
                final int x = j + areaX;
                final int y = i + areaY;
                final int index = j + i * areaWidth;

                executor.submit(() -> {
                    initChunkSeed((long) x, (long) y);
                    int biomeInt = inLayer[index];

                    if (biomeInt == 0 || (x < MAIN_ISLAND_SIZE && x > -MAIN_ISLAND_SIZE && y < MAIN_ISLAND_SIZE && y > -MAIN_ISLAND_SIZE)) {
                        outLayer[index] = SKY_ID;
                    } else if (biomeInt == 1) {
                        outLayer[index] = END_FOREST_ID;
                    } else if (biomeInt == 3) {
                        outLayer[index] = PLACEHOLDER;
                    } else if (biomeInt == 4) {
                        outLayer[index] = END_VOLCANO_ID;
                    } else {
                        Stygian.LOGGER.warn("Invalid biome id: " + biomeInt + " found in genlayer");
                        outLayer[index] = SKY_ID;
                    }

                    return null;
                });
            }
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return outLayer;
    }
}

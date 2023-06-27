package fluke.stygian.world.genlayers;

import fluke.stygian.config.Configs;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenLayerReduceFrequency extends GenLayer {
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public GenLayerReduceFrequency(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int i = areaX - 1;
        int j = areaY - 1;
        int k = areaWidth + 2;
        int l = areaHeight + 2;
        int[] inLayer = this.parent.getInts(i, j, k, l);
        int[] outLayer = IntCache.getIntCache(areaWidth * areaHeight);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int z = 0; z < areaHeight; ++z) {
            for (int x = 0; x < areaWidth; ++x) {
                final int index = x + 1 + (z + 1) * k;
                final int current = inLayer[index];

                int finalX = x;
                int finalZ = z;
                tasks.add(() -> {
                    outLayer[finalX + finalZ * areaWidth] = current;
                    initChunkSeed((long) (finalX + areaX), (long) (finalZ + areaY));

                    int randomValue = nextInt(Integer.MAX_VALUE);
                    int threshold = (int) (Configs.worldgen.biomeReducer * Integer.MAX_VALUE / 100.0);

                    if (current != 0 && randomValue < threshold) {
                        outLayer[finalX + finalZ * areaWidth] = 0;
                    }

                    return null;
                });
            }
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return outLayer;
    }
}

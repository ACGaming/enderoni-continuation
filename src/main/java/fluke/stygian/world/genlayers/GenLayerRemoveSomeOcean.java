package fluke.stygian.world.genlayers;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GenLayerRemoveSomeOcean extends GenLayer {
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    private Random cachedRandom;
    private final Object lock = new Object();

    public GenLayerRemoveSomeOcean(long seed, GenLayer parent) {
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

        for (int z = 0; z < areaHeight; ++z) {
            for (int x = 0; x < areaWidth; ++x) {
                int index = x + 1 + (z + 1) * k;
                int current = inLayer[index];
                outLayer[x + z * areaWidth] = current;

                if (current == 0 && checkNeighbors(inLayer, index, k)) {
                    outLayer[x + z * areaWidth] = 1;
                }
            }
        }

        return outLayer;
    }

    private boolean checkNeighbors(int[] inLayer, int index, int k) {
        int north = inLayer[index - k];
        int east = inLayer[index + 1];
        int west = inLayer[index - 1];
        int south = inLayer[index + k];

        return north == 0 && east == 0 && west == 0 && south == 0 && getCachedRandom().nextInt(10) == 0;
    }

    private Random getCachedRandom() {
        if (cachedRandom == null) {
            synchronized (lock) {
                if (cachedRandom == null) {
                    cachedRandom = new Random(nextInt(Integer.MAX_VALUE));
                }
            }
        }
        return cachedRandom;
    }

    @Override
    public void initWorldGenSeed(long seed) {
        super.initWorldGenSeed(seed);
        executor.shutdownNow();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock) {
            cachedRandom = null;
        }
    }

    @Override
    public void initChunkSeed(long chunkX, long chunkZ) {
        super.initChunkSeed(chunkX, chunkZ);
        synchronized (lock) {
            cachedRandom = null;
        }
    }
    @Override
    protected void finalize() throws Throwable {
        try {
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } finally {
            super.finalize();
        }
    }
}

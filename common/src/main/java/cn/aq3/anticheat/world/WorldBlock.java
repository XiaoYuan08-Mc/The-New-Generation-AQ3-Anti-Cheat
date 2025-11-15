package cn.aq3.anticheat.world;

/**
 * 世界方块表示
 * World block representation
 */
public class WorldBlock {
    private final int x, y, z;
    private final String material;
    private final boolean isWaterlogged;
    
    public WorldBlock(int x, int y, int z, String material, boolean isWaterlogged) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
        this.isWaterlogged = isWaterlogged;
    }
    
    // Getters
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public boolean isWaterlogged() {
        return isWaterlogged;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        WorldBlock that = (WorldBlock) obj;
        return x == that.x && y == that.y && z == that.z;
    }
    
    @Override
    public int hashCode() {
        return (x << 16) ^ (y << 8) ^ z;
    }
}
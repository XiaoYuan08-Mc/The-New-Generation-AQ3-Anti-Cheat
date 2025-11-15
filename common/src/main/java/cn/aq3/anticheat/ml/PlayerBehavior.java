package cn.aq3.anticheat.ml;

/**
 * 玩家行为特征
 * Player behavior features
 */
public class PlayerBehavior {
    private double movementSpeed;
    private double movementConsistency;
    private double lookSpeed;
    private double lookConsistency;
    private double interactionFrequency;
    
    // Getters and setters
    public double getMovementSpeed() {
        return movementSpeed;
    }
    
    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
    
    public double getMovementConsistency() {
        return movementConsistency;
    }
    
    public void setMovementConsistency(double movementConsistency) {
        this.movementConsistency = movementConsistency;
    }
    
    public double getLookSpeed() {
        return lookSpeed;
    }
    
    public void setLookSpeed(double lookSpeed) {
        this.lookSpeed = lookSpeed;
    }
    
    public double getLookConsistency() {
        return lookConsistency;
    }
    
    public void setLookConsistency(double lookConsistency) {
        this.lookConsistency = lookConsistency;
    }
    
    public double getInteractionFrequency() {
        return interactionFrequency;
    }
    
    public void setInteractionFrequency(double interactionFrequency) {
        this.interactionFrequency = interactionFrequency;
    }
}
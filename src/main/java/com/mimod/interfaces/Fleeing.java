package com.mimod.interfaces;

public interface Fleeing {
    boolean isFleeing();
    void setFleeing(boolean fleeing);
    void setAttackCooldown(int ticks);
    int getFleeingTime();
    int getMaxAttackCooldown();
}
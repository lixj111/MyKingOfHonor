package com.sxt;

public class TurretRed extends Turret{
    public TurretRed(GameFrame gameFrame) {
        super(gameFrame);
        setImg("img/turretRed.png");
    }

    public TurretRed(GameFrame gameFrame,int x,int y){
        super(gameFrame,x,y);
        setImg("img/turretRed.png");

    }
}

package com.sxt;

public class TurretBlue extends Turret{
    public TurretBlue(GameFrame gameFrame) {
        super(gameFrame);
        setImg("img/turretBlue.png");
    }

    public TurretBlue(GameFrame gameFrame,int x,int y){
        super(gameFrame,x,y);
        setImg("img/turretBlue.png");
    }
}

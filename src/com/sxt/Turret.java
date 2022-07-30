package com.sxt;

import java.awt.*;
import java.util.ArrayList;

public class Turret extends GameObject{
    ArrayList<Turret> turretList = new ArrayList<Turret>();
    public Turret turretBlue1;
    public Turret turretBlue2;
    public Turret turretBlue3;
    public Turret turretBlueBase;
    public Turret turretRed1;
    public Turret turretRed2;
    public Turret turretRed3;
    public Turret turretRedBase;

/*          1400 4125
            2175 4275
            3200 4275
            4600 4300
            5500 3600
            5575 2525
            5550 1925
            5275 1400*/

    public Turret(GameFrame gameFrame) {
        super(gameFrame);
        //setImg("");
        turretList.add(turretBlueBase = new TurretBlue(gameFrame,1410,4140));
        turretList.add(turretBlue1 = new TurretBlue(gameFrame,2175,4275));
        turretList.add(turretBlue2 = new TurretBlue(gameFrame,3200,4275));
        turretList.add(turretBlue3 = new TurretBlue(gameFrame,4600,4300));
        turretList.add(turretRedBase = new TurretRed(gameFrame,5275,1410));
        turretList.add(turretRed1 = new TurretRed(gameFrame,5525,1925));
        turretList.add(turretRed2 = new TurretRed(gameFrame,5550,2525));
        turretList.add(turretRed3 = new TurretRed(gameFrame,5600,3600));
    }

    public Turret(GameFrame gameFrame, int x, int y){
        super(gameFrame,x,y);
        setHp(6000);
        setCurrenHp(getHp());
        setAttackCoolDownTime(1000);
        setDis(300);
        //setImg("");
    }

    public void addTurret(){
        /*
        * 防御塔保护机制：前面的防御塔被摧毁后才能攻击后续的防御塔
        * 添加到objList例里的防御塔都会被绘制出来，但是双方攻击（redList、blueList）列表中只添加存活的防御塔的第一个
        * */
        //blueList
        //最外围的塔被摧毁 && 二塔存活 && 二塔没加入blueList
        if (!turretBlue3.isAlive() && turretBlue2.isAlive()
                && gameFrame.blueList.indexOf(gameFrame.turret.turretBlue2) == -1){
            gameFrame.blueList.add(gameFrame.turret.turretBlue2);
        }
        //二塔被摧毁 && 高地塔存活 && 高地塔没加入blueList
        if (!turretBlue2.isAlive() && turretBlue1.isAlive()
                && gameFrame.blueList.indexOf(gameFrame.turret.turretBlue1) == -1){
            gameFrame.blueList.add(gameFrame.turret.turretBlue1);
        }
        //高地塔被摧毁 && 水晶存活 && 水晶没加入blueList
        if (!turretBlue1.isAlive() && turretBlueBase.isAlive()
                && gameFrame.blueList.indexOf(gameFrame.turret.turretBlueBase) == -1){
            gameFrame.blueList.add(gameFrame.turret.turretBlueBase);
        }
        if (!turretBlueBase.isAlive()){
            gameFrame.state=3;//游戏失败
        }

        //redList
        //最外围的塔被摧毁 && 二塔存活 && 二塔没加入redList
        if (!turretRed3.isAlive() && turretRed2.isAlive()
                && gameFrame.redList.indexOf(gameFrame.turret.turretRed2) == -1){
            gameFrame.redList.add(gameFrame.turret.turretRed2);
        }
        //二塔被摧毁 && 高地塔存活 && 高地塔没加入redList
        if (!turretRed2.isAlive() && turretRed1.isAlive()
                && gameFrame.redList.indexOf(gameFrame.turret.turretRed1) == -1){
            gameFrame.redList.add(gameFrame.turret.turretRed1);
        }
        //高地塔被摧毁 && 水晶存活 && 水晶没加入redList
        if (!turretRed1.isAlive() && turretRedBase.isAlive()
                && gameFrame.redList.indexOf(gameFrame.turret.turretRedBase) == -1){
            gameFrame.redList.add(gameFrame.turret.turretRedBase);
        }
        if (!turretRedBase.isAlive()){
            gameFrame.state=2;//游戏胜利
        }
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(getX()-20,getY()-25,150,120);
    }

    @Override
    public void paintSelf(Graphics g) {
        if (getCurrenHp() <= 0) {
            setAlive(false);
            gameFrame.removeList.add(this);
            if (this instanceof TurretBlue) {
                gameFrame.blueList.remove(this);
            } else {
                gameFrame.redList.remove(this);
            }
        } else {
            //添加生命值
            if (this instanceof TurretBlue) {
                this.addHp(g, 120, 130, 230, 15, Color.GREEN);
                attack(gameFrame.redList);
            } else {
                this.addHp(g, 120, 130, 230, 15, Color.RED);
                attack(gameFrame.blueList);
            }
            g.drawImage(getImg(), getX() - 150, getY() - 120, null);
            //g.setColor(Color.RED);
            g.fillOval(getX(), getY(), 10, 10);
            g.drawRect(getX() - 130, getY() - 110, 250, 220);
            g.drawOval(getX() - 300, getY() - 300, 600, 600);
        }
    }
}

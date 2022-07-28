package com.sxt;

import java.awt.*;

public class Bullet extends GameObject{
    //发射子弹的游戏元素
    GameObject attacker;
    //子弹目标
    GameObject target;
    //攻击力
    private int ad;

    public Bullet(){
        super();
    }

    public Bullet(GameFrame gameFrame,GameObject attacker,GameObject target,int ad,int spd) {
        super(gameFrame, attacker.getX(), attacker.getY());
        this.attacker=attacker;
        this.target=target;
        setAd(ad);
        setSpd(spd);
    }

    //带图片的字典，例如妲己的二技能
    public Bullet(GameFrame gameFrame,GameObject attacker,GameObject target,int ad,int spd,String img) {
        super(gameFrame, attacker.getX(), attacker.getY());
        this.attacker=attacker;
        this.target=target;
        setImg(img);
        setAd(ad);
        setSpd(spd);
    }

    public void move(){
        //子弹与目标碰撞后，子弹消失，目标减血
        //Boolean test1=revIntersectsRec(getRec(),target.getRec());
//调试bug，玩家getRec()方法没有正确绘制矩形，使玩家的矩形区域长和宽都为0
//        int a = getX();
//        int b = getY();
//        int w1 = getRec().width;
//        int h1 = getRec().height;
//        int c = target.getX();
//        int d = target.getY();
//        int w2 = target.getRec().width;
//        int h2 = target.getRec().height;

        if(revIntersectsRec(getRec(),target.getRec())){
            target.setCurrenHp(target.getCurrenHp()-getAd());
            gameFrame.removeList.add(this);
        }
        int dis = (int) getDis(getX(),getY(), target.getX(), target.getY());
        try {
            if(dis<=0){dis = 1;}
            int xSpeed = (int) (getSpd() * (target.getX() - getX()) / dis);//x轴分速度速度=speed*cos（α）
            int ySpeed = (int) (getSpd() * (target.getY() - getY()) / dis);//y轴分速度=speed*sin（α）
            setX(getX()+xSpeed);
            setY(getY()+ySpeed);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(getX()-5,getY()-5,10,10);
    }

    @Override
    public void paintSelf(Graphics g) {
        g.drawImage(getImg(),getX()-16,getY()-16,null);
        g.setColor(Color.BLACK);
        g.fillOval(getX()-5,getY()-5,10,10);
        g.drawRect(getX()-5,getY()-5,10,10);
        move();
    }

    public int getAd() {
        return ad;
    }

    public void setAd(int ad) {
        this.ad = ad;
    }
}

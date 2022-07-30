package com.sxt.beast;

import com.sxt.*;


import java.awt.*;
import java.util.ArrayList;

public class Beast extends GameObject {
    //野怪列表
    public ArrayList<Beast> beastList = new ArrayList<Beast>();

    //矩形尺寸
    int width;
    int height;

    //初始坐标
    int initialX;
    int initialY;

    //是否有攻击性
    public boolean isAggressive = true;

    //区分野怪的变量
    Beast beast;

    public Beast(GameFrame gameFrame) {
        super(gameFrame);
        beastList.add(new RedBuff(gameFrame,3475,3750));
        //熊、鸟、蜥蜴、狼、蓝buff
    }

    public Beast(GameFrame gameFrame, int x, int y) {
        super(gameFrame, x, y);
        setHp(2000);
        setCurrenHp(getHp());
        beast = this;
        setSpd(20);
        setAttackCoolDownTime(1000);
        initialX = getX();
        initialY = getY();
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(getX()-width/2,getY()-height/2,width,height);
    }

    @Override
    public void paintSelf(Graphics g) {
        if (getCurrenHp()<=0){
            setAlive(false);
            gameFrame.removeList.add(this);//不同包之间的变量要是protected或public，private和default不行
            gameFrame.beast.beastList.remove(this);
            new ReviveCD().start();
        }else{
            //添加生命值
            addHp(g,width/2,80,width,20,Color.GREEN);
            g.drawImage(getImg(),getX()-width/2,getY()-height/2,null);
            g.setColor(Color.RED);
            g.drawOval(getX()-getDis(),getY()-getDis(),2*getDis(),2*getDis());
            move();
        }
    }

    //向目标移动
    public void moveToTarget(){
        double dis1 = getDis(getX(),getY(),getTarget().getX(),getTarget().getY());//与目标距离
        double dis2 = getDis(getX(),getY(),initialX,initialY);//与初生位置距离
        if (dis1>300 || dis2>250){
            isAggressive = false;
            setHasTarget(false);
        }else {
            int xSpeed = (int) (getSpd() * (getTarget().getX() - getX()) / dis1);//x轴分速度速度=speed*cos（α）
            int ySpeed = (int) (getSpd() * (getTarget().getY() - getY()) / dis1);//y轴分速度=speed*sin（α）
            setX(getX()+xSpeed);
            setY(getY()+ySpeed);
        }
    }

    //向出生地移动
    public void moveToInitialLocation(){
        double dis = getDis(getX(),getY(),initialX,initialY);//与初生位置距离
        if(dis < getSpd()){
            setX(initialX);
            setY(initialY);
            isAggressive = true;
        }else {
            int xSpeed = (int) (getSpd() * (initialX - getX()) / dis);//x轴分速度速度=speed*cos（α）
            int ySpeed = (int) (getSpd() * (initialY - getY()) / dis);//y轴分速度=speed*sin（α）
            setX(getX()+xSpeed);
            setY(getY()+ySpeed);
        }
    }

    /*
    * 野怪移动
    * 判断是否有目标(攻击源既是目标)及野怪的仇恨值（有没有攻击性：拉脱到回到出生点之前这段时间没有攻击性，其余都有）
    *   true：判断是否在攻击范围内
    *     true：发射子弹，线程开始
    *     false：向目标移动，若中途离开出生点距离大于250或与目标距离大于300，不再有攻击性
    *   false：没有目标，回到原地，自动回血
    * */
    public void move(){
        //isAggressive一开始是true，但是没给isHasTarget赋true（hasTarget默认值为false）
        //attack(gameFrame.blueList)添加到move(),move()再添加到paintself()
        //……最终在gameObject的setCurrentHp()中实现
        //有目标 && 有攻击性
        if (isAggressive && isHasTarget()){
            //不在范围内
            if (!revIntersectsRecCir(getTarget().getRec(),getX(),getY(),getDis())){
                moveToTarget();
            }else if(isAttackCoolDown() && isAlive()){//在范围内攻击,冷却完成且存活
                Bullet bullet = new Bullet(gameFrame,this,getTarget(),500,50);
                gameFrame.objList.add(bullet);
                new AttackCD().start();
            }
        }else {
            moveToInitialLocation();
            if(getCurrenHp()<getHp()){
                if (getCurrenHp() + 100 > getHp()) {
                    setCurrenHp(getHp());
                } else {
                    setCurrenHp(getCurrenHp() + 100);
                }//即时回到原地，依然move，只是不动，血量依然回复
            }
        }
    }

    class AttackCD extends Thread{
        public void run(){
            setAttackCoolDown(false);
            try{
                Thread.sleep(getAttackCoolDownTime());
            }catch (Exception e){
                e.printStackTrace();
            }
            setAttackCoolDown(true);
            this.stop();
        }
    }

    private class ReviveCD extends Thread{
        public void run(){
            try{
                Thread.sleep(10000);
            }catch (Exception e){
                e.printStackTrace();
            }
            Beast reviveBeast;
            if(beast instanceof RedBuff){
                reviveBeast = new RedBuff(gameFrame,3475,3750);
            }else{
                reviveBeast = new RedBuff(gameFrame,3475,3750);//实际上要变成其他野怪
            }
            gameFrame.objList.add(reviveBeast);//不同包之间的变量要是protected或public，private和default不行
            gameFrame.beast.beastList.add(reviveBeast);
        }
    }
}

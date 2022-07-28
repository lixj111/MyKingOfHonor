package com.sxt;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class ChampionDaJi extends Champion{

    //技能是否处于释放状态
    boolean ifAbilityOneReleased = false;
    boolean ifAbilityTwoReleased = false;

    //鼠标监视器
    MouseMonitor m;
    //一技能多边形
    Polygon p;
    //一技能三角函数
    double cos;
    double sin;
    //一技能已经攻击过的目标
    ArrayList<GameObject> attacked;
    //一技能移动步数
    int step = 0;

    //技能二目标
    GameObject abilityTwoTarget;
    //二技能子弹
    Bullet abilityTwoBullet;

    //三技能子弹列表
    Bullet[] bulletList = {new Bullet(),new Bullet(),new Bullet(),new Bullet(),new Bullet()};

    public ChampionDaJi(GameFrame gameFrame) {
        super(gameFrame);
        abilityOne = Toolkit.getDefaultToolkit().getImage("img/DaJi/a1.png");
        abilityTwo = Toolkit.getDefaultToolkit().getImage("img/DaJi/a2.png");
        abilityThree = Toolkit.getDefaultToolkit().getImage("img/DaJi/a3.png");
        coolDownTimeOne=6000;
        coolDownTimeTwo=8000;
        coolDownTimeThree=10000;
    }

    public void exit(){
        this.gameFrame.removeMouseListener(m);
    }

    //攻击效果成功，但是多边形没绘制出来-->已解决
    public void abilityOneMove(){
        //每次移动50，移动八次，攻击距离400
        p.translate((int)(50*cos),-(int)(50*sin));//将多边形平移
        for(GameObject redObj: gameFrame.redList){
            //是红色小兵 && 发生碰撞 && 没在attacked列表
            //int test =attacked.indexOf(redObj);
            //boolean test1 = p.intersects(redObj.getRec());
            //p-->[x=700,y=261,width=91,height=104]
            //p-->[x=5188,y=3958,width=45,height=55]
            //p的坐标基于英雄和屏幕（1400*700），redObj.getRec()基于背景（6628,4900）
            //p.intersects(redObj.getRec())不可能取true，两者不会碰撞
            //原因：设置多边形四个顶点时，使用了基于英雄屏幕的X和Y,要改为player.getX(Y)
            if(redObj instanceof MinionRed && p.intersects(redObj.getRec()) && attacked.indexOf(redObj) == -1){
            //if(redObj instanceof MinionRed && attacked.indexOf(redObj) == -1){
                //小兵扣血，加入到attacked列表
                redObj.setCurrenHp(redObj.getCurrenHp() - 400);//一技能攻击力400
                attacked.add(redObj);
            }
        }
    }

//    点击一技能启动鼠标监视器，再点击屏幕上的任意一点确定攻击的方向，发动攻击
    @Override
    public void abilityOne() {
        if(coolDownOne){
            m = new MouseMonitor();
            p = new Polygon();
            gameFrame.addMouseListener(m);
            attacked = new ArrayList<GameObject>();
        }
    }

//    类似子弹攻击目标
    @Override
    public void abilityTwo() {
        if(coolDownTwo){
            boolean find =false;
            for(GameObject redObj: gameFrame.redList){
                //是红色小兵 && 距离小于250 && 目标存活
                if (redObj instanceof MinionRed && revIntersectsRecCir(redObj.getRec(),getX(),getY(),250) && redObj.isAlive()){
                    abilityTwoBullet = new Bullet(gameFrame, this, redObj, 250,60,"img/DaJi/abilityTwoBullet.png");
                    gameFrame.objList.add(abilityTwoBullet);
                    abilityTwoTarget = redObj;
                    ifAbilityTwoReleased = true;
                    find = true;
                    break;
                }
            }
            if(find){
                new AbilityTwoCD().start();
                find = false;
            }
        }
    }

    /*
    * 点击技能释放三技能
    * 先将技能范围内目标存储到target List中
    * 提前定义五个子弹
    * 技能释放时初始化五个子弹
    * 子弹目标从target List中随机选择
    * 如果期间目标死亡，制作一个目标替身，生命值设置为true
    * 子弹与目标或替身碰撞后消失
    * */
    @Override
    public void abilityThree() {
        //三技能冷却完成
        if (coolDownThree){
            //创建列表存储目标
            ArrayList<GameObject> targetList = new ArrayList<GameObject>();
            //遍历redList
            for(int i = 0; i < gameFrame.redList.size(); i++){
                GameObject target = gameFrame.redList.get(i);
                //是红色小兵 && 在攻击范围 && 存活
                if (target instanceof MinionRed && revIntersectsRecCir(target.getRec(), getX(), getY(), 250) && target.isAlive()){
                    targetList.add(target);
                }
            }
            //找到目标
            if (targetList.size() != 0){
                //初始化五个子弹，随机攻击列表里的目标
                Random random = new Random();
                int count = 0;
                while(count < 5){
                    int r = random.nextInt(targetList.size());
                    if (!targetList.get(r).isAlive()){
                        //目标死亡，制作替身
                        GameObject substitute = targetList.get(r);
                        substitute.setAlive(true);
                        bulletList[count] = new Bullet(gameFrame, this, substitute, 250,60,"img/DaJi/abilityTwoBullet.png");
                    }else {
                        bulletList[count] = new Bullet(gameFrame, this, targetList.get(r), 250,60,"img/DaJi/abilityTwoBullet.png");
                    }
                    count++;
                }
                new AbilityThreeBulletCD().start();
                new AbilityThreeCD().start();
            }
        }

    }

    @Override
    public void abilityEffect(Graphics g) {
        if(ifAbilityOneReleased){
            g.setColor(Color.RED);
            g.drawPolygon(p);
            g.fillPolygon(p);
            abilityOneMove();
            step++;
            if(step == 8) {
                step = 0;
                ifAbilityOneReleased = false;
            }
        }
        if(ifAbilityTwoReleased){//只达到了停止移动，还可以攻击，不是眩晕效果--->达到了眩晕效果，小兵不能移动的情况下不会攻击
            new AbilityTwoControlCD().start();
            ifAbilityTwoReleased = true;
        }
    }

    //计算攻击时间，一技能线程
    class AbilityOneCD extends Thread{
        public void run(){
            //将技能一设置为冷却状态
            coolDownOne = false;
            try{
                //不休息6秒，改为休息六次一秒
                //Thread.sleep(coolDownTimeOne);
                int one = coolDownTimeOne;
                while(one>0){
                    Thread.sleep(1000);
                    one-=1000;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //将技能一设置为攻击状态
            coolDownOne = true;
            this.stop();
        }
    }

    //计算攻击时间，2技能线程
    class AbilityTwoCD extends Thread{
        public void run(){
            //将技能二设置为冷却状态
            coolDownTwo = false;
            try{
                //不休息8秒，改为休息8次一秒
                //Thread.sleep(coolDownTimeTwo);
                int two = coolDownTimeTwo;
                while(two>0){
                    Thread.sleep(1000);
                    two-=1000;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //将技能二设置为攻击状态
            coolDownTwo = true;
            this.stop();
        }
    }

    //技能二控制时间
    class AbilityTwoControlCD extends Thread{
        public void run(){
            abilityTwoTarget.beControlled = true;
            //线程休眠
            try{
                Thread.sleep(10000);
            }catch (Exception e){
                e.printStackTrace();
            }
            abilityTwoTarget.beControlled = false;
            this.stop();
        }
    }

    //计算攻击时间，3技能线程
    class AbilityThreeCD extends Thread{
        public void run(){
            //将技能二设置为冷却状态
            coolDownThree = false;
            try{
                //不休息10秒，改为休息10次一秒
                //Thread.sleep(coolDownTimeThree);
                int three = coolDownTimeThree;
                while(three>0){
                    Thread.sleep(1000);
                    three-=1000;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //将技能3设置为攻击状态
            coolDownThree = true;
            this.stop();
        }
    }

    //技能三子弹间隔
    class AbilityThreeBulletCD extends Thread{
        public void run(){
            try{
                gameFrame.objList.add(bulletList[0]);
                Thread.sleep(200);
                gameFrame.objList.add(bulletList[1]);
                Thread.sleep(200);
                gameFrame.objList.add(bulletList[2]);
                Thread.sleep(200);
                gameFrame.objList.add(bulletList[3]);
                Thread.sleep(200);
                gameFrame.objList.add(bulletList[4]);
            }catch (Exception e){
                e.printStackTrace();
            }
            this.stop();
        }
    }

    //鼠标监视器
    private class MouseMonitor extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e){//鼠标点击时
            int mouseX = e.getX(), mouseY = e.getY(), playerX = 700, playerY = 350;
            double dis = getDis(mouseX,mouseY,playerX,playerY);
            //三角函数
            cos = (mouseX - playerX) / dis;
            sin = -(mouseY - playerY) / dis;//取负，Y轴是和普通坐标系相反的
            //求距离差
            //技能效果是一个矩形，长为120，宽为20，移动距离为dis
            int difY = (int)(60*cos);
            int difX = (int)(60*sin);
            //构建矩形,按同一个方向（顺时针或逆时针）
            p.addPoint(getX()-difX,getY()-difY);
            p.addPoint(getX()+difX,getY()+difY);
            p.addPoint(getX()+difX+(int)(20*cos),getY()+difY-(int)(20*sin));
            p.addPoint(getX()-difX+(int)(20*cos),getY()-difY-(int)(20*sin));
            exit();
            new AbilityOneCD().start();
            ifAbilityOneReleased = true;
        }
    }
}

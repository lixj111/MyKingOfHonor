package com.sxt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ChampionHouYi extends Champion{

    //技能是否处于释放状态
    boolean ifAbilityTwoReleased = false;
    boolean ifAbilityThreeReleased = false;

    //鼠标监视器
    MouseMonitorTwo m2;
    MouseMonitor m;

    //二技能坐标
    int abilityTwoX;
    int abilityTwoY;

    //3技能多边形
    Polygon p;
    //3技能三角函数
    double cos;
    double sin;
    //3技能击中的目标
    GameObject abilityThreeTarget;
    //三技能是否出界
    boolean ifXOutside;
    boolean ifYOutside;
    //3技能移动步数
    int step = 0;

    public ChampionHouYi(GameFrame gameFrame) {
        super(gameFrame);
        abilityOne = Toolkit.getDefaultToolkit().getImage("img/HouYi/a1.png");
        abilityTwo = Toolkit.getDefaultToolkit().getImage("img/HouYi/a2.png");
        abilityThree = Toolkit.getDefaultToolkit().getImage("img/HouYi/a3.png");
        classical = Toolkit.getDefaultToolkit().getImage("img/HouYi/classical.jpg");
        coolDownTimeOne=6000;
        coolDownTimeTwo=8000;
        coolDownTimeThree=10000;
    }

    public ChampionHouYi(GameFrame gameFrame, int i, int j) {
        super(gameFrame,i,j);
    }

    public void abilityThreeMove(){
        //每次移动50，移动八次，攻击距离400
        p.translate((int)(50*cos),-(int)(50*sin));//将多边形平移
        for(GameObject redObj: gameFrame.redList){
            //是红色小兵 && 发生碰撞
            if(redObj instanceof MinionRed && p.intersects(redObj.getRec())){
                //击中目标后，造成眩晕并减血，多边形消失
                redObj.setCurrenHp(redObj.getCurrenHp() - 400);
                abilityThreeTarget = redObj;
                new AbilityThreeControlCD().start();
                ifAbilityThreeReleased = false;
            }
        }
        if (!ifYOutside){
            for (int x:p.xpoints){
                if(x<0 || x>= 6628){
                    ifXOutside = true;
                    break;
                }
            }
            for (int y:p.ypoints){
                if(y<0 || y>=4900){
                    ifYOutside = true;
                    break;
                }
            }
        }
    }

    public void abilityOneAttack(){
        //目标列表，最多三个
        ArrayList<GameObject> targets = new ArrayList<GameObject>();
        //遍历地方列表找到目标
        for(GameObject redObj:gameFrame.redList){
            //是红色小兵，在攻击范围，存活
            if(redObj instanceof MinionRed && revIntersectsRecCir(redObj.getRec(),getX(),getY(),250) && redObj.isAlive()){
                targets.add(redObj);
                if (targets.size() == 3){
                    break;//有三个就退出，不到三个执行完
                }
            }
            for(int i=0; i< targets.size();i++){
                Bullet bullet;
                if (i==0){//第一发400，其余50
                    bullet = new Bullet(gameFrame,this,targets.get(i),400,50);
                }else{
                    bullet = new Bullet(gameFrame,this,targets.get(i),200,50);
                }
                gameFrame.objList.add(bullet);
            }
        }
        //new AttackCD().start();
    }

    public void abilityTwoAttack(){
        for (GameObject redObj:gameFrame.redList){
            //红色小兵 && 与大圆相交
            if (redObj instanceof MinionRed && revIntersectsRecCir(redObj.getRec(), abilityTwoX,abilityTwoY,60)){
                redObj.setCurrenHp(redObj.getCurrenHp()-200);
                //红色小兵 && 与小圆相交
                if (redObj instanceof MinionRed && revIntersectsRecCir(redObj.getRec(), abilityTwoX,abilityTwoY,30)){
                    redObj.setCurrenHp(redObj.getCurrenHp()-200);
                }
            }

        }
    }

    /*
    * 制作游戏界面攻击按钮的替身，  替换攻击按钮  新的攻击按钮事件：1、定义目标列表  2、添加目标，最多三个
    * 3、目标列表第一个造成400伤害，其余200  4、持续5秒，结束后替换攻击按钮
    * */
    @Override
    public void abilityOne() {
        new AbilityOneDuration().start();
        new AbilityOneCD().start();
    }

    /*
    * 技能二 点击按钮释放二技能  点击技能范围内任意位置 生成一个直径120的大圈和一个直径60的小圈 大圈伤害400 小圈额外伤害200
    * */
    @Override
    public void abilityTwo() {
        m2 = new MouseMonitorTwo();
        gameFrame.addMouseListener(m2);
        abilityTwoX = 0;
        abilityTwoY = 0;
    }

    @Override
    public void abilityThree() {
        if(coolDownThree){
            m = new ChampionHouYi.MouseMonitor();
            p = new Polygon();
            gameFrame.addMouseListener(m);
            ifXOutside = false;
            ifYOutside = false;
        }
    }

    @Override
    public void abilityEffect(Graphics g) {
        if (ifAbilityThreeReleased) {
            g.setColor(Color.RED);
            g.drawPolygon(p);
            g.fillPolygon(p);
            abilityThreeMove();
            //如果出界，技能结束
            if (ifXOutside || ifYOutside) {
                ifAbilityThreeReleased = false;
                p = new Polygon();
            }
        }
        if (ifAbilityTwoReleased){
            g.setColor(Color.RED);
            g.fillOval(abilityTwoX - 60,abilityTwoY - 60,120,120);
            g.setColor(Color.BLACK);
            g.fillOval(abilityTwoX - 30,abilityTwoY - 30,60,60);
            abilityTwoAttack();
            //技能结束
            abilityTwoX = 0;
            abilityTwoY = 0;
            ifAbilityTwoReleased = false;
            new AbilityTwoCD().start();
        }
    }

    //计算攻击时间，1技能线程，1技能CD
    class AbilityOneCD extends Thread{
        public void run(){
            //将技能1设置为冷却状态
            coolDownOne = false;
            try{
                //不休息x秒，改为休息x次一秒
                //Thread.sleep(coolDownTimeOne);
                int one = coolDownTimeOne;
                while(one>0){
                    Thread.sleep(1000);
                    one-=1000;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //将技能1设置为攻击状态
            coolDownOne = true;
            this.stop();
        }
    }

    //计算一技能攻击时间,一技能普攻的间隔
    class AttackCD extends Thread{
        public void run(){
            //将攻击功能设置为冷却状态
            setAttackCoolDown(false);
            try{
                Thread.sleep(getAttackCoolDownTime());
            }catch (Exception e){
                e.printStackTrace();
            }
            //将攻击功能设置为攻击状态
            setAttackCoolDown(true);
            this.stop();
        }
    }

    //一技能效果持续时间————扣掉原有普攻键，用替身替代
    class AbilityOneDuration extends Thread{
        public void run() {
            JButton substitute = gameFrame.attackButton;//替身
            gameFrame.remove(gameFrame.attackButton);

            JButton attackButtonSubstitute = new JButton();
            attackButtonSubstitute.setSize(115, 130);
            attackButtonSubstitute.setLocation(1180, 445);
            //按钮事件
            attackButtonSubstitute.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    abilityOneAttack();
                }
            });
            gameFrame.add(attackButtonSubstitute);

            //休眠5秒后，换回原来的攻击键
            try{
                Thread.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
            gameFrame.remove(attackButtonSubstitute);
            gameFrame.add(substitute);

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

    //技能3控制时间
    class AbilityThreeControlCD extends Thread{
        public void run(){
            abilityThreeTarget.beControlled = true;
            //线程休眠
            try{
                Thread.sleep(10000);
            }catch (Exception e){
                e.printStackTrace();
            }
            abilityThreeTarget.beControlled = false;
            this.stop();
        }
    }

    public void exit(MouseAdapter ma){
        this.gameFrame.removeMouseListener(ma);
    }

    //二技能鼠标监视器
    private class MouseMonitorTwo extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e){//鼠标点击时
            int mouseX = e.getX(), mouseY = e.getY(), playerX = 700, playerY = 350;
            double dis = getDis(mouseX,mouseY,playerX,playerY);
            //点击位置在技能范围内
            if (dis <= 250){
                abilityTwoX = mouseX - playerX + getX();//窗口坐标转换为地图坐标
                abilityTwoY = mouseY - playerY + getY();
            }
            ifAbilityTwoReleased = true;
            exit(this);
        }
    }

    //三技能鼠标监视器
    private class MouseMonitor extends MouseAdapter {
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

            //System.out.println(p.xpoints.toString());
            //System.out.println(p.ypoints.toString());

            exit(this);
            new AbilityThreeCD().start();
            ifAbilityThreeReleased = true;
        }
    }
}

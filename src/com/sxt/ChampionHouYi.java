package com.sxt;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ChampionHouYi extends Champion{

    //技能是否处于释放状态
    boolean ifAbilityThreeReleased = false;


    //鼠标监视器
    ChampionHouYi.MouseMonitor m;
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
        coolDownTimeOne=6000;
        coolDownTimeTwo=8000;
        coolDownTimeThree=10000;
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

    @Override
    public void abilityOne() {

    }

    @Override
    public void abilityTwo() {

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

    public void exit(){
        this.gameFrame.removeMouseListener(m);
    }

    //鼠标监视器
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


            exit();
            new AbilityThreeCD().start();
            ifAbilityThreeReleased = true;
        }
    }
}

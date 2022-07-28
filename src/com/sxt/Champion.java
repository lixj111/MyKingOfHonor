package com.sxt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public abstract class Champion extends GameObject{

    //移动
    public boolean up,down,left,right;
    //键盘事件实现攻击和技能
    public boolean a;

    //技能图片
    Image abilityOne;
    Image abilityTwo;
    Image abilityThree;

    //技能冷却时间
    int coolDownTimeOne;
    int coolDownTimeTwo;
    int coolDownTimeThree;

    //技能是否冷却完成
    boolean coolDownOne = true;
    boolean coolDownTwo = true;
    boolean coolDownThree = true;


    //移动图片
    static String[] imgs = new String[4];
    int moveCount = 1;

    static {
        for(int i=1;i<4;i++){
            imgs[i] = "img/00"+i+".png";
        }
    }

    public Champion(GameFrame gameFrame) {
        super(gameFrame);
        setImg("img/stand.png");
        setX(950);
        setY(4450);
        setSpd(25);
        setHp(100000);
        setCurrenHp(getHp());
        setDis(500);
        setAttackCoolDownTime(500);
    }

    public Champion(GameFrame gameFrame, int x, int y) {
        super(gameFrame, x, y);
    }

    public void KeyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_D){
            right=true;
        }
        if(key == KeyEvent.VK_A){
            left=true;
        }
        if(key == KeyEvent.VK_W){
            up=true;
        }
        if(key == KeyEvent.VK_S){
            down=true;
        }
        if(key == KeyEvent.VK_K){
            a=true;
        }

    }

    public void KeyReleased(KeyEvent e){
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_D){
            right=false;
        }
        if(key == KeyEvent.VK_A){
            left=false;
        }
        if(key == KeyEvent.VK_W){
            up=false;
        }
        if(key == KeyEvent.VK_S){
            down=false;
        }
        if(key == KeyEvent.VK_K){
            a=false;
        }
    }

    public void move(){
        if(up){
            setY(getY()-getSpd());
        }
        if(down){
            setY(getY()+getSpd());
        }
        if(left){
            setX(getX()-getSpd());
        }
        if(right){
            setX(getX()+getSpd());
        }
        if(up||down||left||right){
            setImg(imgs[moveCount]);
            moveCount++;
            if(moveCount==4) {
                moveCount=1;
            }
        }else {
            setImg("img/stand.png");
        }
        if(a){
            this.attack(gameFrame.redList);
        }
    }

    /*
    * 添加三个技能按钮
    * */
    public void addButton(){
        JButton button1 = new JButton();
        button1.setSize(100,100);
        button1.setLocation(1035,475);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //触发一技能
                abilityOne();
            }
        });

        JButton button2 = new JButton();
        button2.setSize(100,100);
        button2.setLocation(1080,360);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //触发2技能
                abilityTwo();
            }
        });

        JButton button3 = new JButton();
        button3.setSize(102,100);
        button3.setLocation(1195,300);
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //触发3技能
                abilityThree();
            }
        });

        //吸引仇恨值
        JButton button4 = new JButton();
        button4.setSize(100,100);
        button4.setLocation(1195,100);
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //触发3技能

            }
        });

        gameFrame.add(button1);
        gameFrame.add(button2);
        gameFrame.add(button3);
        gameFrame.add(button4);
    }

    public abstract void abilityOne();
    public abstract void abilityTwo();
    public abstract void abilityThree();
    public abstract void abilityEffect(Graphics g);

    @Override
    public Rectangle getRec() {
        return new Rectangle(getX()-20,getY()-50,50,110);
    }

    @Override
    public void paintSelf(Graphics g) {
        //生命值小于等于0，不在绘制
        if(getCurrenHp()<=0){
            setAlive(false);
            gameFrame.removeList.add(this);
        }else {
            addHp(g,20,60,50,8,Color.GREEN);
            g.drawImage(getImg(),getX()-25,getY()-45,null);//画图以图像左上角为坐标
            //画笔颜色
            g.setColor(Color.GREEN);
            //绘制中心圆点
            g.fillOval(getX(),getY(),10,10);
            //绘制矩形边框
            g.drawRect(getX()-20,getY()-50,50,110);
            //攻击范围
            g.drawOval(getX()-getDis(),getY()-getDis(),2*getDis(),2*getDis());
            //绘制技能图片
            g.drawImage(abilityOne,getX()+340,getY()+160,null);
            g.drawImage(abilityTwo,getX()+380,getY()+40,null);
            g.drawImage(abilityThree,getX()+500,getY()-20,null);

            /*
            4
            [672, 728, 745, 689]
            [298, 402, 393, 289]
            int []x={672, 728, 745, 689};
            int []y={298, 402, 393, 289};
            g.setColor(Color.RED);
            g.drawPolygon(x,y,4);
            g.fillPolygon(testP);
            */

            move();
            abilityEffect(g);
        }
    }
}

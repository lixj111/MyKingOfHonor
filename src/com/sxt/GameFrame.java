package com.sxt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

//创建一个窗口，监测鼠标和键盘事件
public class GameFrame extends JFrame {

    //游戏模式 0：选择英雄；1：游戏开始；2：游戏胜利；3：游戏失败
    int state = 0;

    //窗体尺寸
    private int windowWidth = 1400;
    private int windowHeight = 700;

    //双缓冲技术解决屏幕上图片闪烁
    private Image offScreenImage = null;

    //初始化游戏背景
    Background background = new Background(this);

    //攻击键图片
    private Image attackKey = Toolkit.getDefaultToolkit().getImage("img/attack.png");

    //胜利失败界面
    private Image victory = Toolkit.getDefaultToolkit().getImage("img/victory.png");
    private Image defeat = Toolkit.getDefaultToolkit().getImage("img/defeat.png");

    //初始化英雄和玩家
    //Champion player = new ChampionHouYi(this);
    Champion player;
    //Champion champion = new Champion(this,950,4450);//不能实例化Champion了，因为增加了abstract属性
    Champion champion = new ChampionHouYi(this,950,4450);

    //双方小兵
    MinionBlue mb = new MinionBlue(this);
    MinionRed mr = new MinionRed(this);

    //防御塔
    Turret turret =new Turret(this);

    //攻击按钮
    JButton attackButton;
    //英雄按钮
    JButton championButton;

    //游戏元素列表——批量元素
    ArrayList<GameObject> objList = new ArrayList<GameObject>();
    ArrayList<GameObject> blueList = new ArrayList<GameObject>();
    ArrayList<GameObject> redList = new ArrayList<GameObject>();
    ArrayList<GameObject> removeList = new ArrayList<GameObject>();//要删除的元素的列表
    public void launch(){
        //设置尺寸
        setSize(windowWidth,windowHeight);
        //窗口居中
        setLocationRelativeTo(null);
        //关闭事件————关闭窗口后程序自动停止运行
        setDefaultCloseOperation(3);
        //用户不能调整窗口大小
        setResizable(false);
        //窗口标题
        setTitle("MyKingOfHonor");
        //窗口可见
        setVisible(true);
        //添加键盘监视器
        this.addKeyListener(new GameFrame.KeyMonitor());
        //添加游戏元素
        objList.add(background);
        objList.addAll(turret.turretList);
        //objList.add(player);
        /*for (int i = 0; i<4; i++){
            blueList.add(turret.turretList.get(i));
        }
        for (int i = 4; i<8; i++){
            redList.add(turret.turretList.get(i));
        }*/
        //只添加到最外围的塔
        blueList.add(turret.turretList.get(3));
        redList.add(turret.turretList.get(7));

        //鼠标按钮事件——攻击按钮
        attackButton = new JButton();
        attackButton.setSize(115,130);
        attackButton.setLocation(1180,445);
        //按钮事件
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.attack(redList);
            }
        });
        this.add(attackButton);//按钮添加到界面中
        if(player!=null) {
            player.addButton();
        }


        //重绘元素
        while(true){
            if (state == 1) {
                //添加小兵
                //mr.createMinion(this, redList);
                //mb.createMinion(this,blueList);
                //添加防御塔（防御塔保护机制）
                turret.addTurret();
            }
            repaint();
            try{
                Thread.sleep(50);//刷新频率50
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void paint(Graphics g){//Graphics是画笔工具
        //System.out.println(turret.turretList.get(7).getX());
        //System.out.println(turret.turretList.get(7).getY());
        //System.out.println(player.getX()+" "+player.getY());
        if(offScreenImage == null){
            offScreenImage = this.createImage(6628,4900);//背景图片尺寸
        }
        Graphics gImage = offScreenImage.getGraphics();
        if (state == 0){
            for (int i = 0; i<champion.championList.size();i++){
                //添加绘制图片
                Image classical = champion.championList.get(i).classical;
                gImage.drawImage(classical,i*300+50,80,null);

                //添加按钮
                JButton championButton = new JButton();
                championButton.setSize(196,260);
                championButton.setLocation(i*300+50,50);
                int a = i;
                championButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        state = 1;
                        player = champion.championList.get(a);
                        objList.add(player);
                    }
                });
                this.add(championButton);
            }

            //吸引仇恨值
            JButton buttonTest = new JButton();
            buttonTest.setSize(300,300);
            buttonTest.setLocation(700,50);
            buttonTest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //

                }
            });
            this.add(buttonTest);
        }else if(state == 1) {
            //把championButton去掉！！！
            //this.removeAll();
            for (int i = 0; i < objList.size(); i++) {
                objList.get(i).paintSelf(gImage);
            }
            //绘制攻击键
            gImage.drawImage(attackKey, player.getX() + 470, player.getY() + 110, null);

            //优化：把技能1、2、3添加到双缓存背景图片中

        /*background.paintSelf(gImage);
        player.paintSelf(gImage);//尺寸太大
        mr.paintSelf(gImage);
        mb.paintSelf(gImage);*/

            //等遍历完元素列表之后再将要删除的移除
            objList.removeAll(removeList);
        }else if (state == 2){//游戏胜利
            gImage.drawImage(victory, 450, 220, null);
        }else if(state == 3){//游戏失败
            gImage.drawImage(defeat, 450, 220, null);
        }

        if (state != 1){
            g.drawImage(offScreenImage, 0, 0, null);
        }else {
            g.drawImage(offScreenImage, -player.getX() + 700, -player.getY() + 350, null);
        }
        /*
        添加按钮后不能再调用键盘事件移动，因为程序的焦点变成了按钮  this.requestFocus()重新把焦点改变到游戏界面上
        */
        this.requestFocus();
    }

    //main
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
        gameFrame.launch();
    }

    //键盘事件
    class KeyMonitor extends KeyAdapter{
        //按下事件
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();//获得键值
            player.KeyPressed(e);
        }

        //松开事件
        @Override
        public void keyReleased(KeyEvent e) {
            player.KeyReleased(e);
        }
    }
}

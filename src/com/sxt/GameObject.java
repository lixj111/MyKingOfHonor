package com.sxt;

import com.sxt.beast.Beast;

import java.awt.*;
import java.util.ArrayList;

//游戏父类
public abstract class GameObject {
    //坐标
    private int x;
    private int y;

    //图片
    Image img;

    //游戏界面
    public GameFrame gameFrame;

    //速度
    private int spd;

    //初始生命值
    private int hp;

    //当前生命值
    private int currenHp;

    //攻击目标
    private GameObject target;
    //是否有目标
    private boolean hasTarget = false;
    //攻击距离
    private int dis;
    //攻击时间间隔
    private int attackCoolDownTime;
    //攻击是否冷却完成
    private boolean isAttackCoolDown = true;

    //是否存活
    private boolean alive = true;
    //是否被控制
    boolean beControlled = false;


    public GameObject(GameFrame gameFrame){
        this.gameFrame = gameFrame;
    }

    public GameObject(GameFrame gameFrame, int x, int y) {
        this.gameFrame = gameFrame;
        this.x = x;
        this.y = y;
    }

    public GameObject() {

    }

    //返回游戏元素矩形
    public abstract Rectangle getRec();

    //绘制游戏元素
    public abstract void paintSelf(Graphics g);

    //添加生命值
    public void addHp(Graphics g,int difX,int difY,int width,int height,Color color){
        //外部轮廓是黑色矩形
        g.setColor(Color.BLACK);
        g.drawRect(getX()-difX,getY()-difY,width,height);
        //绘制生命条
        g.setColor(color);
        //g.fillRect(getX()-difX,getY()-difY,(int)(width),height);
        //除数不为0
        try{
            g.fillRect(getX()-difX,getY()-difY,(int)(width*getCurrenHp()/getHp()),height);
        }catch(Exception e) {
            throw e;
        }

    }

    //获去两点之间的距离
    public double getDis(int x1,int y1,int x2,int y2){
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }

    //矩形间碰撞检测
    public boolean revIntersectsRec(Rectangle r1,Rectangle r2){
        return r1.intersects(r2);//现成函数
    }

    //矩形与圆形碰撞检测——矩形的四个顶点至少有一个在原型内——不科学，有待改进
    public boolean revIntersectsRecCir(Rectangle r,int x,int y,int dis){
        if (//矩形r的坐标矩形的左上角，但不是绘制出来的矩形的左上角，绘制的做了平移处理
                getDis(x,y,r.x-(r.width/2),r.y-(r.height/2))<dis ||
                getDis(x,y,r.x+(r.width/2),r.y-(r.height/2))<dis ||
                getDis(x,y,r.x-(r.width/2),r.y+(r.height/2))<dis ||
                getDis(x,y,r.x+(r.width/2),r.y+(r.height/2))<dis
        ){
            return true;
        }
        return false;
        /*???
        if (Math.abs(x-r.x) < dis
        ){
            return true;
        }
        return false;*/
    }

    //攻击方法
    public void attack(ArrayList<GameObject> gameObjList){
        if(hasTarget){
            //目标离开防御塔后，重新寻找目标
            if(!revIntersectsRecCir(target.getRec(),getX(),getY(),getDis())){
                setHasTarget(false);
            }else if(!target.isAlive()){//目标死了，停止攻击
                setHasTarget(false);
            }
            else if(isAttackCoolDown() && isAlive()){
                //System.out.println("shoot");
                Bullet bullet = null;
                //防御塔攻击
                if(Turret.class.isAssignableFrom((getClass()))){//isAssignaleFrom:判断后面的类是否是前面类的子类
                    bullet = new Bullet(gameFrame,this,getTarget(),500,50);
                }
                //小兵攻击
                if(Minion.class.isAssignableFrom((getClass()))){
                    bullet = new Bullet(gameFrame,this,getTarget(),200,25);
                }
                /*if(MinionRed.class.isAssignableFrom((getClass()))){
                    bullet = new Bullet(gameFrame,this,getTarget(),100,25);
                }*/
                //玩家攻击
                if(Champion.class.isAssignableFrom((getClass()))){
                    bullet = new Bullet(gameFrame,this,getTarget(),1000,50);
                }
                gameFrame.objList.add(bullet);
                //线程开始
                new AttackCD().start();
            }
        }else {
            //遍历列表——玩家不在蓝色方列表
            for(GameObject obj:gameObjList){
                //判断攻击范围（圆形）与敌方（矩形）是否相交
                if(revIntersectsRecCir(obj.getRec(),getX(),getY(),getDis())){
                    //找到目标，变量赋值，跳出循环
                    target=obj;
                    hasTarget=true;
                    break;
                }
            }
            //判断玩家是否在范围
            if(!hasTarget && gameObjList == gameFrame.blueList){
                //判断攻击范围（圆形）与敌方（矩形）是否相交
                if(revIntersectsRecCir(gameFrame.player.getRec(),getX(),getY(),getDis())){
                    //找到目标，变量赋值
                    target=gameFrame.player;
                    hasTarget=true;
                }
            }
            //判断野怪是否在范围，只有玩家可以攻击野怪
            if(Champion.class.isAssignableFrom(getClass())){
                for(GameObject obj:gameFrame.beast.beastList){
                    //判断攻击范围（圆形）与敌方（矩形）是否相交
                    if(revIntersectsRecCir(obj.getRec(),getX(),getY(),getDis())){
                        //找到目标，变量赋值，跳出循环
                        target=obj;
                        hasTarget=true;
                        break;
                    }
                }
            }

        }
    }

    //计算攻击时间
    class AttackCD extends Thread{
        public void run(){
            //将攻击功能设置为冷却状态
            setAttackCoolDown(false);
            try{
                Thread.sleep(attackCoolDownTime);
            }catch (Exception e){
                e.printStackTrace();
            }
            //将攻击功能设置为攻击状态
            setAttackCoolDown(true);
            this.stop();
        }
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Image getImg() {
        return img;
    }

    //手动修改
    public void setImg(String img) {
        this.img = Toolkit.getDefaultToolkit().getImage(img);
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getCurrenHp() {
        return currenHp;
    }

    public void setCurrenHp(int currenHp) {
        //是否受到伤害
        if (currenHp<getCurrenHp()){
            //是野怪类
            if (Beast.class.isAssignableFrom(getClass())){
                setTarget(gameFrame.player);
                setHasTarget(true);
            }
        }
        this.currenHp = currenHp;
    }

    public GameObject getTarget() {
        return target;
    }

    public void setTarget(GameObject target) {
        this.target = target;
    }

    public boolean isHasTarget() {
        return hasTarget;
    }

    public void setHasTarget(boolean hasTarget) {
        this.hasTarget = hasTarget;
    }

    public int getDis() {
        return dis;
    }

    public void setDis(int dis) {
        this.dis = dis;
    }

    public int getAttackCoolDownTime() {
        return attackCoolDownTime;
    }

    public void setAttackCoolDownTime(int attackCoolDownTime) {
        this.attackCoolDownTime = attackCoolDownTime;
    }

    public boolean isAttackCoolDown() {
        return isAttackCoolDown;
    }

    public void setAttackCoolDown(boolean attackCoolDown) {
        isAttackCoolDown = attackCoolDown;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}

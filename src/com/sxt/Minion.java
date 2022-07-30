package com.sxt;

import java.awt.*;
import java.util.ArrayList;

public abstract class Minion extends GameObject{

    //是否生成下一个小兵
    private boolean nextMinion = true;
    //是否生成下一波小兵
    private boolean nextLine = true;
    //小兵数目
    private int minionCount = 0;
    //是否检测到目标——检测范围200，攻击范围100
    private boolean ifFindTarget = false;



    public Minion(GameFrame gameFrame) {
        super(gameFrame);
        setHp(1600);
        setCurrenHp(getHp());
        setDis(100);
        setAttackCoolDownTime(1000);
    }

    /*
    1575  4275
    4925  4275

    5450  1550
    5450  3950
    */

    public abstract void move(ArrayList<GameObject> objList);

    public void findTarget(ArrayList<GameObject> objList){
        for(GameObject obj:objList){
            if(revIntersectsRecCir(obj.getRec(),getX(),getY(),200)){
                setTarget(obj);
                setIfFindTarget(true);
                break;
            }
        }
        if(objList == gameFrame.blueList){//如果攻击列表是蓝色列表，要单独判断和玩家的关系，达到优先攻击小兵，后攻击玩家的效果
            if(revIntersectsRecCir(gameFrame.player.getRec(),getX(),getY(),200)){
                setTarget(gameFrame.player);
                setIfFindTarget(true);
            }
        }
    }

    public void moveToTarget(){
        //避免除数为零，可以用try、catch，也可以把除数设为double类型，不要求除数不为零
        /*int dis = (int) getDis(getX(),getY(), getTarget().getX(), getTarget().getY());
        try {
            if(dis<=0){
                dis = 1;
            }
            int xSpeed = (int) (getSpd() * (getTarget().getX() - getX()) / dis);//x轴分速度速度=speed*cos（α）
            int ySpeed = (int) (getSpd() * (getTarget().getY() - getY()) / dis);//y轴分速度=speed*sin（α）
            setX(getX()+xSpeed);
            setY(getY()+ySpeed);
        }catch (Exception e){
            throw e;
        }*/

        double dis = getDis(getX(),getY(), getTarget().getX(), getTarget().getY());
        int xSpeed = (int) (getSpd() * (getTarget().getX() - getX()) / dis);//x轴分速度速度=speed*cos（α）
        int ySpeed = (int) (getSpd() * (getTarget().getY() - getY()) / dis);//y轴分速度=speed*sin（α）
        //要碰到时就停一下
        if (!hitMinion(getX()+xSpeed,getY(),gameFrame.objList)) {
            setX(getX() + xSpeed);
        }
        if (!hitMinion(getX(),getY()+ySpeed,gameFrame.objList)) {
            setY(getY() + ySpeed);
        }
    }

    public void createMinion(GameFrame gameFrame, ArrayList<GameObject> minionList){
        if(nextLine){
            if(nextMinion){
                //蓝色方小兵
                if(minionList == this.gameFrame.blueList){
                    MinionBlue mb = new MinionBlue(gameFrame);
                    gameFrame.objList.add(mb);
                    minionList.add(mb);
                }
                //红方小兵
                else {
                    MinionRed mr = new MinionRed(gameFrame);
                    gameFrame.objList.add(mr);
                    minionList.add(mr);
                }
                minionCount++;
                new NextMinion().start();
            }
            if (minionCount == 3){
                minionCount = 0;
                new NextLine().start();
            }
        }
    }

    /*
    * 小兵碰撞，不让同类（同一方的小兵）的小兵碰在一块？？？要碰到时就停一下
    * @param x: 下一步的横坐标
    * @param y: 下一步的纵坐标
    * @param objList: 小兵列表
    * @return 下一步位置与其他小兵是否碰撞
    * */
    public boolean hitMinion(int x,int y,ArrayList<GameObject> objList){
        //创建新的矩形区域作为预判后小兵下一步的位置
        Rectangle r = new Rectangle(getX()-16,getY()-16,45,55);
        for (GameObject obj:objList){
            //是相同类 && 不是自身--->相同类：是MinionRed/MinionBlue
            if (obj.getClass()==this.getClass() && obj != this){
                if(r.intersects(obj.getRec())){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Rectangle getRec() {
        return new Rectangle(getX()-16,getY()-16,45,55);
    }

    @Override
    public void paintSelf(Graphics g) {
        //生命值小于等于0，不在绘制
        if (getCurrenHp() <= 0) {
            setAlive(false);
            gameFrame.removeList.add(this);
            if (this instanceof MinionBlue) {
                gameFrame.blueList.remove(this);
            } else {
                gameFrame.redList.remove(this);
            }
        } else {
            //添加生命值
            if (this instanceof MinionBlue) {
                this.addHp(g, 20, 30, 45, 5, Color.GREEN);
            } else {
                this.addHp(g, 20, 30, 45, 5, Color.RED);
            }
            g.drawImage(getImg(), getX() - 20, getY() - 25, null);
            g.setColor(Color.RED);
            g.fillOval(getX(), getY(), 10, 10);
            g.drawRect(getX() - 20, getY() - 25, 45, 55);
            g.drawOval(getX()-200,getY()-200,400,400);//画圆也是像画矩形一样从左上角开始画…………
            if (!beControlled) {
                if (this instanceof MinionBlue) {
                    move(gameFrame.redList);
                } else {
                    move(gameFrame.blueList);
                }
            }
        }
    }

    //生成新线程，计算下一个小兵的生成时间
    class NextMinion extends Thread{
        public void run(){
            nextMinion = false;
            //休眠0.75秒生成下一个小兵
            try{
                Thread.sleep(750);
            }catch (Exception e){
                e.printStackTrace();
            }
            nextMinion = true;
            //线程终止
            this.stop();
        }
    }

    //生成新线程，计算下一波小兵的生成时间
    class NextLine extends Thread{
        public void run(){
            nextLine = false;
            //休眠20秒生成下一波小兵
            try{
                Thread.sleep(20000);
            }catch (Exception e){
                e.printStackTrace();
            }
            nextLine = true;
            //线程终止
            this.stop();
        }
    }

    public boolean isIfFindTarget() {
        return ifFindTarget;
    }

    public void setIfFindTarget(boolean ifFindTarget) {
        this.ifFindTarget = ifFindTarget;
    }
}

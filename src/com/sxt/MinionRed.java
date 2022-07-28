package com.sxt;

import java.util.ArrayList;

public class MinionRed extends Minion{
    public MinionRed(GameFrame gameFrame) {
        super(gameFrame);
        setImg("img/minion/Red.png");
        setX(5425);
        setY(1345);

    }

    @Override
    public void move(ArrayList<GameObject> objList) {
        if(isIfFindTarget() && getTarget().isAlive()){
            //BUG:一波兵线消灭另一波兵线后，target只是不被绘制，但并没有消失,碰撞检测函数依然是true，无法将setIfFindTarget(false)
            //debug:attack方法中目标死了，将setHasTarget（false），但无法无法将setIfFindTarget(false)，所以产生的效果是围绕着foundTarget移动而不攻击
            //debug:判定条件：isIfFindTarget() && getTarget().isAlive()
            //debug:在attack攻击方法中，判定如果是minion类，目标死了，setHasTarget(false)的同时setIfFindTarget(false)
            //离开检测范围
            if(!revIntersectsRecCir(getTarget().getRec(),getX(),getY(),200)){
                setIfFindTarget(false);
            }
            //没有离开检测范围就向目标移动，借鉴子弹类向目标移动
            else {
                //进入攻击范围后停止移动，发射子弹
                if(!isHasTarget()){
                    moveToTarget();
                }
                attack(objList);
            }
        }else {
            findTarget(objList);
            //原路线移动——可扩大点范围避免小兵回不到原路线
            if (getY() >= 1345 && getY() < 3775) {
                setSpd(9);
                setY(getY() + getSpd());
            } else if (getY() >= 3500 && getY() < 4275) {
                setSpd(10);
                setX(getX() - getSpd());
                setY(getY() + getSpd());
            } else if (getY() >= 4200 && getX() > 1685) {
                setSpd(12);
                setX(getX() - getSpd());
            }
        }
    }

    /*
    1685  4275
    4925  4275
    3240/12=270
    500   500/10=50
          2430/9=270
    5425  3775
    5425  1345
    */


}

package com.sxt;

import java.util.ArrayList;

public class MinionBlue extends Minion{
    public MinionBlue(GameFrame gameFrame) {
        super(gameFrame);
        setImg("img/minion/Blue.png");
        setX(1685);
        setY(4275);
    }

    @Override
    public void move(ArrayList<GameObject> objList) {
        if(isIfFindTarget() && getTarget().isAlive()){
            //BUG:一波兵线消灭另一波兵线后，target只是不被绘制，但并没有消失,碰撞检测函数依然是true，无法将setIfFindTarget(false)
            //debug:判定条件：isIfFindTarget() && getTarget().isAlive()
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
            //原路线移动
            if (getX() >= 1685 && getX() < 4925) {
                setSpd(12);
                setX(getX() + getSpd());
            } else if (getX() <= 5425) {
                setSpd(10);
                setX(getX() + getSpd());
                setY(getY() - getSpd());
            } else{
                setSpd(9);
                setY(getY() - getSpd());
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

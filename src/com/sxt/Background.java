package com.sxt;

import java.awt.*;

public class Background extends GameObject{
    public Background(GameFrame gameFrame){
        super(gameFrame);
    }
    Image bg = Toolkit.getDefaultToolkit().getImage("img/map.jpg");//"img/map/jpg"

    @Override
    public Rectangle getRec() {
        return null;
    }

    //绘制方法
    public void paintSelf(Graphics g){
        g.drawImage(bg,0,0,null);
    }
}

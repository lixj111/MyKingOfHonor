package com.sxt.beast;

import com.sxt.GameFrame;

public class RedBuff extends Beast{
    public RedBuff(GameFrame gameFrame) {
        super(gameFrame);
    }

    public RedBuff(GameFrame gameFrame, int x, int y) {
        super(gameFrame, x, y);
        setImg("img/red.jpg");
        width = 100;
        height = 80;
        setDis(100);
    }
}

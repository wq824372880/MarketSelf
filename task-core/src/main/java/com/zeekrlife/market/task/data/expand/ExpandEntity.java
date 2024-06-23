package com.zeekrlife.market.task.data.expand;

import java.io.Serializable;


public class ExpandEntity implements Serializable {
    private static final long serialVersionUID = 8789148587569567297L;

    //拓展类型
    protected int type;

    public ExpandEntity() {
    }

    public ExpandEntity(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ExpandEntity{" +
                "type=" + type +
                '}';
    }
}

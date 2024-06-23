package com.shubham.app.snakeladder.game.create;

public class Snake {

    private Integer head;
    private Integer tail;

    public Snake() {
    }

    public Snake(Integer start, Integer tail) {
        this.head = start;
        this.tail = tail;
    }

    public Integer getHead() {
        return head;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public Integer getTail() {
        return tail;
    }

    public void setTail(Integer tail) {
        this.tail = tail;
    }

    @Override
    public String toString() {
        return "Snake{" + "start=" + head + ", end=" + tail + '}';
    }
}

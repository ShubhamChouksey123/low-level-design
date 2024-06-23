package com.shubham.app.snakeladder.game.create;

public class Ladder {

    private Integer start;
    private Integer end;

    public Ladder() {
    }

    public Ladder(Integer start, Integer end) {
        this.start = start;
        this.end = end;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Ladder{" + "start=" + start + ", end=" + end + '}';
    }
}

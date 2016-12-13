package com.dijiaapp.eatserviceapp.kaizhuo;

import com.dijiaapp.eatserviceapp.data.Seat;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by wjy on 16/9/21.
 */
public class EnterActivityEvent {
    private Class gotoClass;
    private Seat seat;
    private String id;
    public EnterActivityEvent(Class gotoClass,Seat seat) {
        this.gotoClass = gotoClass;
        this.seat = seat;
    }

    public EnterActivityEvent(Class gotoClass, Seat seat, String id) {
        this(gotoClass,seat);
        this.id = id;
    }

    public Class getGotoClass() {
        return gotoClass;
    }

    public Seat getSeat() {
        return seat;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EnterActivityEvent{" +
                "gotoClass=" + gotoClass +
                ", seat=" + seat +
                ", id='" + id + '\'' +
                '}';
    }
}

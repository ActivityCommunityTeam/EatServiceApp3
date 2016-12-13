package com.dijiaapp.eatserviceapp.data.source;

import com.dijiaapp.eatserviceapp.data.Seat;

import java.util.List;

import rx.Observable;

/**
 * Created by wjy on 16/8/30.
 * Seat数据的接口
 */
public interface SeatsDataSource {
    Observable<List<Seat>> getSeats(long shopId);

    Observable<Seat> getSeat(String seatId);

    void refreshSeats();

    void saveSeat(Seat seat);

    void deleteAllSeats();

    void deleteSeat();

    void updateSeat(long id,String useStatus);
    Observable<String> getStatus(long id);
}

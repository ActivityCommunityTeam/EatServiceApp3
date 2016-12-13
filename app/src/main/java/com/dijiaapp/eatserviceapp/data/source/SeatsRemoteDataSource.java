package com.dijiaapp.eatserviceapp.data.source;

import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.network.Network;

import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by wjy on 16/8/30.
 *
 */
public class SeatsRemoteDataSource implements SeatsDataSource {
    private static SeatsRemoteDataSource INSTANCE;
    private final static Map<String,Seat> SEATS_SERVICE_DATA = null;

    @Override
    public Observable<List<Seat>> getSeats(long shopId) {
        return Network.getSeatService().listSeats(shopId);
    }

    @Override
    public Observable<Seat> getSeat(String seatId) {
        return null;
    }

    @Override
    public void refreshSeats() {

    }

    @Override
    public void saveSeat(Seat seat) {

    }

    @Override
    public void deleteAllSeats() {

    }

    @Override
    public void deleteSeat() {

    }

    @Override
    public void updateSeat(long id, String useStatus) {

    }

    @Override
    public Observable<String> getStatus(long id) {
        return null;
    }
}

package com.dijiaapp.eatserviceapp.kaizhuo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.Seat;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.data.source.SeatsRemoteDataSource;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SeatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeatFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "type";
    private static List<Seat> mSeats;
    @BindView(R.id.seat_recyclerview)
    RecyclerView mSeatRecyclerview;

    private int type;
    private Unbinder unbinder;
    private SeatRecyclerviewAdapter seatRecyclerviewAdapter;
    private Subscription subscription;
    private Realm realm;
    private long hotelId;



    public SeatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SeatFragment.
     */
    public static SeatFragment newInstance(int type) {
        SeatFragment fragment = new SeatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);


        }
    }
    public static Seat getSeat(int seatId){
        for (int i = 0; i < mSeats.size(); i++) {
            if (mSeats.get(i).getSeatId()==seatId){
                return mSeats.get(i);
            }
        }
        return null;
    }
    public static Seat getSeat_order(String seatName){
        for (int i = 0; i < mSeats.size(); i++) {
            if (mSeats.get(i).getSeatName().equals(seatName)){
                return mSeats.get(i);
            }
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        hotelId = userInfo.getHotelId();
        Log.i("Daniel","-----------onStart");
        setList();
    }

    private void setList() {
        SeatsRemoteDataSource seatsRemoteDataSource = new SeatsRemoteDataSource();
        subscription = seatsRemoteDataSource.getSeats(hotelId).subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<Seat>, Observable<Seat>>() {
                    @Override
                    public Observable<Seat> call(List<Seat> seats) {
                        mSeats = seats;
                        return Observable.from(seats);
                    }
                })
                .filter(new Func1<Seat, Boolean>() {
                    @Override
                    public Boolean call(Seat seat) {
                        switch (type) {
                            case 1:
                                return seat.getSeatType().equals("01");
                            case 2:
                                return seat.getSeatType().equals("02");
                        }
                        return true;
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Seat>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @DebugLog
                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.toString());
                    }

                    @DebugLog
                    @Override
                    public void onNext(List<Seat> seats) {
                        Log.i("Daniel","-----------onNext");
                        if(mSeats==null){
                            mSeats= seats;
                        }
                        seatRecyclerviewAdapter.setSeatList(seats);
                    }
                });
    }
    @DebugLog
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the food_listitem for this fragment
        View view = inflater.inflate(R.layout.fragment_seat, container, false);
        unbinder = ButterKnife.bind(this, view);
        seatRecyclerviewAdapter = new SeatRecyclerviewAdapter(getContext());
        mSeatRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mSeatRecyclerview.setAdapter(seatRecyclerviewAdapter);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

    }
}

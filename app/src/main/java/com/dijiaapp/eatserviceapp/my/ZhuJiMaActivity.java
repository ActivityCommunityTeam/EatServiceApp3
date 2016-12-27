package com.dijiaapp.eatserviceapp.my;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.dijiaapp.eatserviceapp.R;
import com.dijiaapp.eatserviceapp.data.MemoryCode;
import com.dijiaapp.eatserviceapp.data.UserInfo;
import com.dijiaapp.eatserviceapp.network.Network;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ZhuJiMaActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.zhujima_list)
    RecyclerView zhujimaList;
    private Realm realm;
//    private List<MemoryCode> mMemoryCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhu_ji_ma);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        getDatas();
        setToolBar();

    }

    private void getDatas() {
        UserInfo userInfo = realm.where(UserInfo.class).findFirst();
        Long _holtelId = userInfo.getHotelId();
        Network.getFoodService().getMemoryCode(_holtelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MemoryCode>>() {
                    @Override
                    public void call(List<MemoryCode> memoryCodes) {
                        Log.i("Daniel","---memoryCodes.size()---"+memoryCodes.size());
//                        if (mMemoryCodes==null){
//                            mMemoryCodes = new ArrayList<MemoryCode>();
//                        }
//                        mMemoryCodes=memoryCodes;
                        setAdapter(memoryCodes);
                    }
                });

    }

    private void setAdapter(List<MemoryCode> memoryCodes) {
        zhujimaList.setLayoutManager(new LinearLayoutManager(this));
        ZhuJiMaAdapter adapter = new ZhuJiMaAdapter(memoryCodes);
        zhujimaList.setAdapter(adapter);
    }

    private void setToolBar() {
        toolbar.setTitle("菜品助记码对照表");
        toolbar.setNavigationIcon(R.drawable.barcode__back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}

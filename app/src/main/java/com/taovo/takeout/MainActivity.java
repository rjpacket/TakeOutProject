package com.taovo.takeout;

import android.os.Bundle;

import com.rjp.navigationview.NavigationView;
import com.rjp.navigationview.TabModel;
import com.taovo.takeout.activity.BaseActivity;
import com.taovo.takeout.http.Api;
import com.taovo.takeout.util.LL;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.navigation_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        ArrayList<TabModel> tabModels = new ArrayList<>();
        tabModels.add(new TabModel("外卖", R.mipmap.ic_launcher));
        tabModels.add(new TabModel("发现", R.mipmap.ic_launcher));
        tabModels.add(new TabModel("订单", R.mipmap.ic_launcher));
        tabModels.add(new TabModel("我的", R.mipmap.ic_launcher));
        navigationView.setTabs(tabModels);

        Api.getInstance().getObject(this, "appLaunch", new JSONObject(), 30 * 1000, false, "", new Api.GetObjectCallBack<BannerModel>() {

            @Override
            public void onSuccess(BannerModel result) {
                LL.e("-----请求对象---->", result.getClientUpdateInfo().toString());
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });

        Api.getInstance().getList(this, "bannerList", new JSONObject(), 30 * 1000, false, "", new Api.GetListCallBack<BannerModel>() {
            @Override
            public void onSuccess(List<BannerModel> result) {
                for (BannerModel bannerModel : result) {
                    LL.e("-----请求列表---->", bannerModel.getName());
                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });
    }
}

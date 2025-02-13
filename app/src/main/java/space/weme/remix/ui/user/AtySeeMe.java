package space.weme.remix.ui.user;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import space.weme.remix.R;
import space.weme.remix.model.FriendData;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/5.
 * liujilong.me@gmail.com
 */
public class AtySeeMe extends SwipeActivity {
    private static final String TAG = "AtySeeMe";
    RecyclerView mRecycler;
    // todo load more and refresh
    FriendAdapter adapter;

    List<FriendData> friendDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_see_me);

        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        mRecycler = (RecyclerView) findViewById(R.id.aty_see_me_recycler);
        adapter = new FriendAdapter(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(adapter);

        friendDataList = new ArrayList<>();
        getFollowers(1);
    }

    private void getFollowers(int page){
        ArrayMap<String, String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("page",String.format("%d", page));
        param.put("direction", "followers");
        OkHttpUtils.post(StrUtils.GET_FOLLOWERS_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtySeeMe.this, s);
                if (j == null) {
                    return;
                }
                JSONArray result = j.optJSONArray("result");
                for (int i = 0; i < result.length(); i++) {
                    friendDataList.add(FriendData.fromJSON(result.optJSONObject(i)));
                }
                adapter.setList(friendDataList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }
}

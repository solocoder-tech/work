//package com.example.mytakeout.ui.activity;
//
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.os.Message;
//import android.widget.ImageView;
//
//import com.example.mytakeout.R;
//import com.example.mytakeout.base.BaseActivity;
//import com.example.mytakeout.base.BaseApplication;
//import com.example.mytakeout.utils.FileUtils;
//import com.example.mytakeout.utils.LogUtils;
//import com.example.zhu.ld_map_lib.LdMapUtils;
//import com.example.zhu.ld_map_lib.SweepArea;
//import com.example.zhu.ld_map_lib.SweepMap;
////import com.squareup.leakcanary.RefWatcher;
//
//import java.io.InputStream;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import retrofit2.http.FieldMap;
//
///**
// * 创建时间：2018/12/8  11:11
// * 作者：5#
// * 描述：内存泄露分析实例
// *    画扫地机地图
// */
//public class LeakCanaryActivity extends BaseActivity {
//    @BindView(R.id.map_iv)
//    ImageView mapIv;
//
//    private Bitmap mBitmap;
//    private Point mPoint;//充电桩世界坐标点
//
//
//    @Override
//    protected void handleMessage(Message msg) {
//        super.handleMessage(msg);
//        switch (msg.what){
//            case 100:
//                mapIv.setImageBitmap(mBitmap);
//                break;
//        }
//    }
//
//    @Override
//    protected void initViews() {
//        setCustomView(R.layout.activity_leak_canary, true, "LeakCanary");
//        ButterKnife.bind(this);
//
//        mPoint = new Point(-3868, 2094);
//    }
//
//    @Override
//    protected void initDatas() {
//        LeakThread leakThread = new LeakThread();
//        leakThread.start();
//    }
//
//    @Override
//    protected void initEvents() {
//
//    }
//
//    class LeakThread extends Thread {
//
//        @Override
//        public void run() {
//            try {
//                InputStream open = getResources().getAssets().open("map.txt");
//                LogUtils.sysout("----111111-----" );
//                String mapStr = FileUtils.ReadFile(open);
//                LogUtils.sysout("----2222222-----" + mapStr);
////                mBitmap = MapFactory.getMapByStream(mapStr, 0, 0, 0);
////                LdMapUtils.getMapByJson(mapStr)
//                mBitmap = LdMapUtils.getMapByJson(mapStr);
//                LogUtils.sysout("----333333-----" );
//                mHandler.sendEmptyMessage(100);
//                LogUtils.e("sleep");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //activity中没有必要，fragment需要
//        RefWatcher refWatcher = BaseApplication.getRefWatcher(this);//1
//        refWatcher.watch(this);
//    }
//}

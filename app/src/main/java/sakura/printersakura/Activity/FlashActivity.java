package sakura.printersakura.Activity;

import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sakura.printersakura.App;
import sakura.printersakura.Bean.CmdEvent;
import sakura.printersakura.R;
import sakura.printersakura.base.BaseActivity;
import sakura.printersakura.httprequset.HTTP;
import sakura.printersakura.utils.BluetoothManager;
import sakura.printersakura.utils.SPUtil;
import sakura.printersakura.utils.Utils;

/**
 * sakura.printersakura.Activity
 *
 * @author 赵磊
 * @date 2017/11/22
 * 功能描述：
 */
public class FlashActivity extends BaseActivity {

    private HTTP http;

    @Override
    protected void ready() {
        super.ready();
        fullScreen();
    }

    @Override
    protected int setthislayout() {
        return R.layout.activity_flash;
    }

    @Override
    protected void initListener() {
        //注册EventBus
        if (!EventBus.getDefault().isRegistered(context)) {
            EventBus.getDefault().register(context);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BluetoothManager.isBluetoothSupported()) {
                    if (!BluetoothManager.isBluetoothEnabled()) {
                        boolean b = BluetoothManager.turnOnBluetooth();
                        if (b) {
                            Toast.makeText(context, "蓝牙打开成功", Toast.LENGTH_SHORT).show();
                            gotoLogin();
                        } else {
                            //跳转到系统 Bluetooth 设置
                            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                            Toast.makeText(context, "请先打开蓝牙", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        gotoLogin();
                    }
                } else {
                    Toast.makeText(context, "蓝牙不可用", Toast.LENGTH_SHORT).show();
                }

            }
        }, 2500);
    }


    private String account;
    private String password;

    private void gotoLogin() {
        //检测网络
        boolean connected = Utils.isConnected(context);
        if (connected) {
            //获取账户缓存
            account = (String) SPUtil.get(context, "account", "");
            password = (String) SPUtil.get(context, "password", "");
            //历史记录存在
            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
                http.login(account, password, context);
            } else {
                //历史记录不存在
                activityLogin();
            }
        } else {
            if (context != null) {
                Toast.makeText(context, "网路未连接", Toast.LENGTH_SHORT).show();
                activityLogin();
            }
        }
    }

    //跳转登录界面
    private void activityLogin() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    @Override
    protected void initData() {
        http = new HTTP();
    }


    private void gotoAppStart() {
        startActivity(new Intent(context, AppStart.class));
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CmdEvent event) {
        if (!TextUtils.isEmpty(event.getMsg())) {
            if ("appstart".equals(event.getMsg())) {
                gotoAppStart();
            }
            if ("tologin".equals(event.getMsg())) {
                activityLogin();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getQueues().cancelAll("login");
        //反注册EventBus
        EventBus.getDefault().unregister(context);
        System.gc();
    }
}

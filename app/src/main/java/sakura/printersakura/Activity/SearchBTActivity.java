package sakura.printersakura.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.List;

import sakura.printersakura.R;
import sakura.printersakura.myprinter.Global;
import sakura.printersakura.myprinter.WorkService;
import sakura.printersakura.utils.DataUtils;
import sakura.printersakura.utils.SPUtil;
import sakura.printersakura.utils.TimeUtils;

public class SearchBTActivity extends Activity implements OnClickListener {

    private LinearLayout linearlayoutdevices;
    private ProgressBar progressBarSearchStatus;
    private ProgressDialog dialog;

    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;

    private static Handler mHandler = null;
    private static String TAG = "SearchBTActivity";
    private ImageView img_search;
    private RelativeLayout rl_search;
    private Button buttonSearch;
    private ScrollView scrollView1;
    private RotateAnimation rotate;
    private TextView tv_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbt);
        initView();
        findViewById(R.id.buttonSearch).setOnClickListener(this);
        progressBarSearchStatus = (ProgressBar) findViewById(R.id.progressBarSearchStatus);
        linearlayoutdevices = (LinearLayout) findViewById(R.id.linearlayoutdevices);
        dialog = new ProgressDialog(this);
        initBroadcast();
        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);
        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(1000);//设置动画持续周期
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);//执行前的等待时间
        img_search.setAnimation(rotate);


        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .setDeniedMessage("权限申请")
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(SearchBTActivity.this, "应用需要这些权限保证功能", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.delHandler(mHandler);
        mHandler = null;
        uninitBroadcast();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.rl_search:
                tv_search.setText("正在搜索");
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (null == adapter) {
                    finish();
                    break;
                }
                if (!adapter.isEnabled()) {
                    if (adapter.enable()) {
                        while (!adapter.isEnabled())
                            ;
                        Log.v(TAG, "Enable BluetoothAdapter");
                    } else {
                        finish();
                        break;
                    }
                }
                if (null != WorkService.workThread) {
                    WorkService.workThread.disconnectBt();
                    TimeUtils.WaitMs(10);
                }
                adapter.cancelDiscovery();
                linearlayoutdevices.removeAllViews();
                TimeUtils.WaitMs(10);
                adapter.startDiscovery();
                break;
            default:
                break;
        }
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null) {
                        return;
                    }
                    final String address = device.getAddress();
                    String name = device.getName();
                    if (name == null) {
                        name = "BT";
                    } else if (name.equals(address)) {
                        name = "BT";
                    }
                    Button button = new Button(context);
                    button.setText(name + ": " + address);
                    button.setGravity(Gravity.CENTER_VERTICAL
                            | Gravity.LEFT);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            WorkService.workThread.disconnectBt();
                            // 只有没有连接且没有在用，这个才能改变状态
                            dialog.setMessage(Global.toast_connecting + " "
                                    + address);
                            dialog.setIndeterminate(true);
                            dialog.setCancelable(false);
                            dialog.show();
                            SPUtil.putAndApply(context, "address", address);
                            WorkService.workThread.connectBt(address);

                        }
                    });
                    button.getBackground().setAlpha(100);
                    linearlayoutdevices.addView(button);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                    progressBarSearchStatus.setIndeterminate(true);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    tv_search.setText("搜索完成");
                    progressBarSearchStatus.setIndeterminate(false);
                }

            }

        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void initView() {
        img_search = (ImageView) findViewById(R.id.img_search);
        rl_search = (RelativeLayout) findViewById(R.id.rl_search);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        rl_search.setOnClickListener(this);
        tv_search = (TextView) findViewById(R.id.tv_search);
    }

    public static class MHandler extends Handler {

        WeakReference<SearchBTActivity> mActivity;

        MHandler(SearchBTActivity activity) {
            mActivity = new WeakReference<SearchBTActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchBTActivity theActivity = mActivity.get();
            switch (msg.what) {
                /**
                 * DrawerService 的 onStartCommand会发送这个消息
                 */
                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT:
                    int result = msg.arg1;
                    Toast.makeText(
                            theActivity,
                            (result == 1) ? Global.toast_success
                                    : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Connect Result: " + result);
                    theActivity.dialog.cancel();
                    if (1 == result) {
                        PrintTest(" *\n" +
                                " ━━━聚宝盆━━━\n" +
                                " ━━━聚宝盆━━━\n" +
                                " ━━━聚宝盆━━━\n" +
                                " ━━━聚宝盆━━━" + "\r\n\r\n\r\n");
                        theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                        theActivity.finish();
                    }
                    break;
                default:
                    break;

            }
        }

        public static void PrintTest(String str) {

            // 加三行换行，避免走纸
            byte[] buf = new byte[0];
            try {
                buf = DataUtils.byteArraysToBytes(new byte[][]{str.getBytes("GBK")});
                if (WorkService.workThread.isConnected()) {
                    Bundle data = new Bundle();
                    data.putByteArray(Global.BYTESPARA1, buf);
                    data.putInt(Global.INTPARA1, 0);
                    data.putInt(Global.INTPARA2, buf.length);
                    WorkService.workThread.handleCmd(Global.CMD_WRITE, data);
                } else {

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

}

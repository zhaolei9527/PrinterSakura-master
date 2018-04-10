package sakura.printersakura.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import sakura.printersakura.R;
import sakura.printersakura.myprinter.Global;
import sakura.printersakura.myprinter.WorkService;
import sakura.printersakura.utils.FileUtils;
import sakura.printersakura.utils.SPUtil;
import sakura.printersakura.view.CommomDialog;

public class AppStart extends Activity {

    private MHandler mHandler;

    private void InitGlobalString() {
        Global.toast_success = getString(R.string.toast_success);
        Global.toast_fail = getString(R.string.toast_fail);
        Global.toast_notconnect = getString(R.string.toast_notconnect);
    }


    private ProgressDialog dialog;

    private void handleSendRaw(Intent intent) {
        Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (textUri != null) {
            // Update UI to reflect text being shared
            if (WorkService.workThread.isConnected()) {
                String path = textUri.getPath();
                byte buffer[] = FileUtils.ReadToMem(path);
                // Toast.makeText(this, "length:" + buffer.length,
                // Toast.LENGTH_LONG).show();
                Bundle data = new Bundle();
                data.putByteArray(Global.BYTESPARA1, buffer);
                data.putInt(Global.INTPARA1, 0);
                data.putInt(Global.INTPARA2, buffer.length);
                data.putInt(Global.INTPARA3, 256);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);

            } else {
                Toast.makeText(this, Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }

            // finish();
        }
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            } else {
                handleSendRaw(intent);
            }
        }
    }

    private void handleSendText(Intent intent) {
        Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (textUri != null) {
            // Update UI to reflect text being shared

            if (WorkService.workThread.isConnected()) {
                byte[] buffer = {0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x39, 0x01}; // 设置中文，切换双字节编码。
                Bundle data = new Bundle();
                data.putByteArray(Global.BYTESPARA1, buffer);
                data.putInt(Global.INTPARA1, 0);
                data.putInt(Global.INTPARA2, buffer.length);
                WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
            }
            if (WorkService.workThread.isConnected()) {
                String path = textUri.getPath();
                String strText = FileUtils.ReadToString(path);
                byte buffer[] = strText.getBytes();

                Bundle data = new Bundle();
                data.putByteArray(Global.BYTESPARA1, buffer);
                data.putInt(Global.INTPARA1, 0);
                data.putInt(Global.INTPARA2, buffer.length);
                data.putInt(Global.INTPARA3, 128);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_WRITE_BT_FLOWCONTROL, data);

            } else {
                Toast.makeText(this, Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.MediaColumns.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null,
                null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String path = getRealPathFromURI(imageUri);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            opts.inJustDecodeBounds = false;
            if (opts.outWidth > 1200) {
                opts.inSampleSize = opts.outWidth / 1200;
            }

            Bitmap mBitmap = BitmapFactory.decodeFile(path);

            if (mBitmap != null) {
                if (WorkService.workThread.isConnected()) {
                    Bundle data = new Bundle();
                    data.putParcelable(Global.PARCE1, mBitmap);
                    data.putInt(Global.INTPARA1, 384);
                    data.putInt(Global.INTPARA2, 0);
                    WorkService.workThread.handleCmd(
                            Global.CMD_POS_PRINTPICTURE, data);
                } else {
                    Toast.makeText(this, Global.toast_notconnect,
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
                    /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start_private);
        dialog = new ProgressDialog(this);
        // 初始化字符串资源
        InitGlobalString();
        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);
        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }
        handleIntent(getIntent());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String address = (String) SPUtil.get(AppStart.this, "address", "");
                if (TextUtils.isEmpty(address)) {
                    Intent intent = new Intent(AppStart.this, SearchBTActivity.class);
                    startActivity(intent);
                    AppStart.this.finish();
                } else {
                    connectBT(address);
                }
            }
        }, 1000);
    }

    private void connectBT(String address) {
        // TODO Auto-generated method stub
        WorkService.workThread.disconnectBt();
        // 只有没有连接且没有在用，这个才能改变状态
        dialog.setMessage(Global.toast_connecting + " "
                + address);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        WorkService.workThread.connectBt(address);
    }

    class MHandler extends Handler {
        WeakReference<AppStart> mActivity;
        MHandler(AppStart activity) {
            mActivity = new WeakReference<AppStart>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final AppStart theActivity = mActivity.get();
            switch (msg.what) {
                /**
                 * DrawerService 的 onStartCommand会发送这个消息
                 */
                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT:
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v("appstart", "Connect Result: " + result);
                    theActivity.dialog.cancel();
                    if (result == 1) {
                        theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                        theActivity.finish();
                    } else {
                        new CommomDialog(theActivity, R.style.dialog, "请确认已开启蓝牙打印设备？", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (!confirm) {
                                    dialog.dismiss();
                                    finish();
                                } else {
                                    dialog.dismiss();
                                    String address = (String) SPUtil.get(AppStart.this, "address", "");
                                    connectBT(address);
                                }
                            }
                        }).setTitle("提示").show();
                    }
                    break;
                default:
                    break;
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove the handler
        WorkService.delHandler(mHandler);
        mHandler = null;
    }
}

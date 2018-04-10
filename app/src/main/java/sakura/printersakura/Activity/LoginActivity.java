package sakura.printersakura.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sakura.printersakura.App;
import sakura.printersakura.Bean.CmdEvent;
import sakura.printersakura.R;
import sakura.printersakura.base.BaseActivity;
import sakura.printersakura.httprequset.HTTP;
import sakura.printersakura.utils.SPUtil;
import sakura.printersakura.utils.Utils;

/**
 * sakura.printersakura.Activity
 *
 * @author 赵磊
 * @date 2017/11/22
 * 功能描述：
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private HTTP http;
    private Dialog dialog;


    @Override
    protected void ready() {
        super.ready();
        fullScreen();
    }

    @Override
    protected int setthislayout() {
        return R.layout.activcity_login;
    }

    @Override
    protected void initListener() {
        //注册EventBus
        if (!EventBus.getDefault().isRegistered(context)) {
            EventBus.getDefault().register(context);
        }
    }

    @OnClick(R.id.btn_login)
    void submit() {

        if (TextUtils.isEmpty(etAccount.getText().toString())) {
            Toast.makeText(context, etAccount.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            Toast.makeText(context, etPassword.getHint().toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utils.isConnected(context)) {
            dialog = Utils.showLoadingDialog(context);
            if (!dialog.isShowing()) {
                dialog.show();
            }
            http.login(etAccount.getText().toString(), etPassword.getText().toString(), context);
        }

    }

    @Override
    protected void initData() {
        http = new HTTP();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void gotoAppStart() {
        startActivity(new Intent(context, AppStart.class));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CmdEvent event) {
        if (!TextUtils.isEmpty(event.getMsg())) {
            dialog.dismiss();
            if ("appstart".equals(event.getMsg())) {
                SPUtil.putAndApply(context, "account", etAccount.getText().toString());
                SPUtil.putAndApply(context, "password", etPassword.getText().toString());
                gotoAppStart();
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




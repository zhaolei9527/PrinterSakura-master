package sakura.printersakura.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import sakura.printersakura.App;
import sakura.printersakura.Bean.ListsBean;
import sakura.printersakura.R;
import sakura.printersakura.base.BaseActivity;
import sakura.printersakura.httprequset.HTTP;
import sakura.printersakura.myprinter.Global;
import sakura.printersakura.myprinter.WorkService;
import sakura.printersakura.utils.DataUtils;
import sakura.printersakura.utils.SPUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_caipiao_name;
    private TextView tv_time;
    private TextView tv_username;
    private TextView tv_number;
    private TextView tv_caipiao_max;
    private TextView tv_money;
    private TextView tv_day;
    private TextView tv_refresh;
    private TextView tv_log;
    private HTTP http;
    private String account;
    private String userid;
    private String qishu;
    private LinearLayout ll_content;

    //数据更新速率

    private int Delayed = 5000;
    private Runnable r;
    private StringBuffer stringBuffer;
    private TextView tv_exit;
    private EditText et_title;
    private TextView tv_title;

    @Override
    protected void onDestroy() {
        WorkService.workThread.disconnectBt();
        super.onDestroy();
        App.getQueues().cancelAll("lists");
        //反注册EventBus
        EventBus.getDefault().unregister(context);
        mHandler.removeCallbacks(r);
        System.gc();
    }

    @Override
    protected int setthislayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initListener() {
        //注册EventBus
        if (!EventBus.getDefault().isRegistered(context)) {
            EventBus.getDefault().register(context);
        }
        initView();
        tv_log.setOnClickListener(this);
        tv_refresh.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        http = new HTTP();
        account = (String) SPUtil.get(context, "account", "");
        userid = (String) SPUtil.get(context, "userid", "");
        qishu = (String) SPUtil.get(context, "qishu", "");
        http.lists(userid, account, qishu, context);
        r = new Runnable() {
            @Override
            public void run() {
                http.lists(userid, account, qishu, context);
                mHandler.postDelayed(r, Delayed);
            }
        };
        mHandler.postDelayed(r, Delayed);
    }

    boolean isshow = false;

    private void initView() {
        final TextView tv_set = (TextView) findViewById(R.id.tv_set);

        tv_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isshow) {
                    tv_set.setText("编辑");
                    tv_title.setVisibility(View.VISIBLE);
                    tv_title.setText(et_title.getText().toString());
                    et_title.setText("");
                    et_title.setVisibility(View.GONE);
                } else {
                    tv_set.setText("保存");
                    tv_title.setVisibility(View.GONE);
                    et_title.setVisibility(View.VISIBLE);
                }
                isshow = !isshow;
            }
        });

        et_title = (EditText) findViewById(R.id.et_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_caipiao_name = (TextView) findViewById(R.id.tv_caipiao_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_caipiao_max = (TextView) findViewById(R.id.tv_caipiao_max);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_day = (TextView) findViewById(R.id.tv_day);
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        tv_log = (TextView) findViewById(R.id.tv_log);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        tv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("提示")//设置对话框标题
                        .setMessage("确定退出吗？")//设置显示的内容
                        .setPositiveButton("退出", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                //确定按钮的响应事件
                                dialog.dismiss();
                                SPUtil.remove(context, "account");
                                SPUtil.remove(context, "password");
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                                WorkService.workThread.disconnectBt();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                        dialog.dismiss();
                    }
                }).show();//在按键响应事件中显示此对话框
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ListsBean listsBean) {
        if ("200".equals(String.valueOf(listsBean.getCode()))) {
            tv_time.setText("" + listsBean.getTime());
            tv_username.setText("" + listsBean.getUsername());
            tv_number.setText("" + listsBean.getDid());
            tv_caipiao_max.setText("笔数：" + listsBean.getCount());
            tv_money.setText("￥" + listsBean.getMoney());
            tv_day.setText("*第" + qishu + "期，3天内有效");
            ll_content.removeAllViews();
            stringBuffer = new StringBuffer();
            for (int i = 0; i < listsBean.getData().size(); i++) {
                View itemLayout = View.inflate(context, R.layout.item_layout, null);
                TextView tv_caipiao_number = itemLayout.findViewById(R.id.tv_caipiao_number);
                TextView tv_caipiao_peilv = itemLayout.findViewById(R.id.tv_caipiao_peilv);
                TextView tv_caipiao_jine = itemLayout.findViewById(R.id.tv_caipiao_jine);
                tv_caipiao_number.setText("" + listsBean.getData().get(i).getMingxi_2() + listsBean.getData().get(i).getMingxi_3());
                tv_caipiao_peilv.setText("赔率1：" + listsBean.getData().get(i).getOdds());
                tv_caipiao_jine.setText("金额：" + listsBean.getData().get(i).getMoney());
                ll_content.addView(itemLayout);
                int length = listsBean.getData().get(i).getMingxi_2().length();
                int length1 = listsBean.getData().get(i).getMingxi_3().length();
                int length2 = listsBean.getData().get(i).getOdds().length();
                stringBuffer.append(listsBean.getData().get(i).getMingxi_2() + listsBean.getData().get(i).getMingxi_3());
                if (length + length1 < 11) {
                    for (int i1 = 0; i1 < 11 - (length + length1); i1++) {
                        stringBuffer.append(" ");
                    }
                }
                stringBuffer.append(listsBean.getData().get(i).getOdds());
                if (length2 < 11) {
                    for (int i1 = 0; i1 < 11 - length2; i1++) {
                        stringBuffer.append(" ");
                    }
                }
                stringBuffer.append(listsBean.getData().get(i).getMoney() + "\n");
            }

        } else {
            tv_time.setText("");
            tv_username.setText("");
            tv_number.setText("");
            tv_caipiao_max.setText("");
            tv_money.setText("");
            tv_day.setText("暂无订单信息");
            ll_content.removeAllViews();
        }
    }

    void PrintTest() {
        String title = tv_title.getText().toString() + "\n" + "━━━七星彩━━━\n";
        String str =
                "购买时间：" + tv_time.getText() + "\n" +
                        "会员名：" + tv_username.getText() + "\n" +
                        "编号：" + tv_number.getText() + "\n" +
                        "-------------------------------\n" +
                        "号码       赔率       金额\n" +
                        "-------------------------------\n" + stringBuffer +
                        "-------------------------------\n" +
                        tv_caipiao_max.getText() + "   总金额  " + tv_money.getText() + "\n" +
                        "-------------------------------\n";
        String bottom = tv_day.getText() + "\r\n\r\n\r\n"+"\r\n\r\n\r\n";


        SearchBTActivity.MHandler.PrintTest(title);
        SearchBTActivity.MHandler.PrintTest(str);
        SearchBTActivity.MHandler.PrintTest(bottom);

//        加三行换行，避免走纸
//        byte[] tmp2 = {0x1b, 0x21, 0x01};
//        try {
//            if (WorkService.workThread.isConnected()) {
//                Bundle data = new Bundle();
//                Bundle dataAlign = new Bundle();
//                Bundle dataTextOut = new Bundle();
//                Bundle dataTextOut2 = new Bundle();
//                data.putByteArray(Global.STRPARA1, DataUtils.byteArraysToBytes(new byte[][]{str.getBytes("GBK")}));
//                data.putString(Global.STRPARA2, "GBK");
//                data.putInt(Global.INTPARA1, 0);
//                data.putInt(Global.INTPARA2, 0);
//                data.putInt(Global.INTPARA3, 0);
//                data.putInt(Global.INTPARA4, 0);
//                dataTextOut.putByteArray(Global.STRPARA1, DataUtils.byteArraysToBytes(new byte[][]{title.getBytes("GBK")}));
//                dataTextOut.putString(Global.STRPARA2, "GBK");
//                dataTextOut.putInt(Global.INTPARA1, 0);
//                dataTextOut.putInt(Global.INTPARA2, 0);
//                dataTextOut.putInt(Global.INTPARA3, 0);
//                dataTextOut.putInt(Global.INTPARA4, 0);
//                dataTextOut2.putByteArray(Global.STRPARA1, DataUtils.byteArraysToBytes(new byte[][]{bottom.getBytes("GBK")}));
//                dataTextOut2.putString(Global.STRPARA2, "GBK");
//                dataTextOut2.putInt(Global.INTPARA1, 0);
//                dataTextOut2.putInt(Global.INTPARA2, 0);
//                dataTextOut2.putInt(Global.INTPARA3, 0);
//                dataTextOut2.putInt(Global.INTPARA4, 0);

//                dataAlign.putInt(Global.INTPARA1, 1);
//                dataAlign.putInt(Global.INTPARA2, 1);
//                dataAlign.putInt(Global.INTPARA3, 0);
//                dataAlign.putInt(Global.INTPARA4, 0);
//
//
//                WorkService.workThread.handleCmd(Global.CMD_POS_SALIGN,
//                        dataAlign);
//
//                WorkService.workThread.handleCmd(Global.CMD_POS_STEXTOUT,
//                        dataTextOut);
//
//                WorkService.workThread.handleCmd(Global.CMD_POS_STEXTOUT, data);

//                WorkService.workThread.handleCmd(Global.CMD_POS_STEXTOUT,
//                        dataTextOut2);
//
//            } else {
//                Toast.makeText(context, Global.toast_notconnect,
//                        Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, Global.toast_notconnect,
//                    Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_log:
                if (!"暂无订单信息".equals(tv_day.getText().toString())) {
                    PrintTest();
                } else {
                    Toast.makeText(context, "暂无订单信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_refresh:
                http.lists(userid, account, qishu, context);
                break;
            default:
                break;
        }
    }
}

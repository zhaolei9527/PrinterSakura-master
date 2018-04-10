package sakura.printersakura.httprequset;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindString;
import sakura.printersakura.Bean.CmdEvent;
import sakura.printersakura.Bean.ListsBean;
import sakura.printersakura.Bean.LoginBean;
import sakura.printersakura.R;
import sakura.printersakura.Volley.VolleyInterface;
import sakura.printersakura.Volley.VolleyRequest;
import sakura.printersakura.utils.SPUtil;
import sakura.printersakura.utils.UrlUtils;

/**
 * sakura.printersakura.HTTPRequset
 *
 * @author 赵磊
 * @date 2018/1/10
 * 功能描述：
 */
public class HTTP {

    @BindString(R.string.Abnormalserver)
    String Abnormalserver;

    /**
     * 登录
     */
    public void login(String uname, String upassword, final Context context) {
        HashMap<String, String> params = new HashMap<>(2);
        params.put("uname", uname);
        params.put("upassword", upassword);
        VolleyRequest.RequestPost(context, UrlUtils.BASE_URL + "login", "login", params, new VolleyInterface(context) {
            @Override
            public void onMySuccess(String result) {
                Log.e("RegisterActivity", result);
                try {
                    LoginBean loginBean = new Gson().fromJson(result, LoginBean.class);
                    if ("0".equals(loginBean.getCode())) {
                        SPUtil.putAndApply(context, "qishu", loginBean.getQishu());
                        SPUtil.putAndApply(context, "userid", loginBean.getUser().getUid());
                        EventBus.getDefault().post(
                                new CmdEvent("appstart"));
                    } else {
                        EventBus.getDefault().post(
                                new CmdEvent("tologin"));
                        Toast.makeText(context, loginBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    loginBean = null;
                    result = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, Abnormalserver, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMyError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, Abnormalserver, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 订单获取
     */
    public void lists(String userid, String unames, String qishu, final Context context) {
        HashMap<String, String> params = new HashMap<>(3);
        params.put("userid", userid);
        params.put("unames", unames);
        params.put("qishu", qishu);
        Log.e("HTTP", "params:" + params);
        VolleyRequest.RequestPost(context, UrlUtils.BASE_URL + "lists", "lists", params, new VolleyInterface(context) {
            @Override
            public void onMySuccess(String result) {
                Log.e("RegisterActivity", result);
                try {
                    ListsBean listsBean = new Gson().fromJson(result, ListsBean.class);
                    EventBus.getDefault().post(
                            listsBean);
                    listsBean = null;
                    result = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, Abnormalserver, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMyError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, Abnormalserver, Toast.LENGTH_SHORT).show();
            }
        });
    }


}

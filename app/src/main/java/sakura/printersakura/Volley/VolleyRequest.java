package sakura.printersakura.Volley;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

import sakura.printersakura.App;
import sakura.printersakura.R;
import sakura.printersakura.utils.Utils;


/**
 * Created by 赵磊 on 2017/7/12.
 */

public class VolleyRequest {
    public static StringRequest request;
    private Context context;

    public static void RequestGet(Context context, String url, String tag, VolleyInterface vif) {
        App.getQueues().cancelAll(tag);
        request = new StringRequest(Request.Method.GET, url, vif.loadingListener(), vif.errorListener());
        //tag设置
        request.setTag(tag);
        //网络状态检测
        if (Utils.isConnected(context)) {
            App.getQueues().add(request);
        } else {
            Toast.makeText(context, context.getString(R.string.Networkexception), Toast.LENGTH_SHORT).show();
        }
    }

    public static void RequestPost(Context context, String url, String tag, final Map<String, String> params, VolleyInterface vif) {
        App.getQueues().cancelAll(tag);
        request = new StringRequest(Request.Method.POST, url, vif.loadingListener(), vif.errorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //tag设置
        request.setTag(tag);
        //网络状态检测
        if (Utils.isConnected(context)) {
            App.getQueues().add(request);
        } else {
            Toast.makeText(context, context.getString(R.string.Networkexception), Toast.LENGTH_SHORT).show();
        }
    }

    public static void RequestPost(Context context, String url, final Map<String, String> params, VolleyInterface vif) {
        request = new StringRequest(Request.Method.POST, url, vif.loadingListener(), vif.errorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //网络状态检测
        if (Utils.isConnected(context)) {
            App.getQueues().add(request);
        } else {
            Toast.makeText(context, context.getString(R.string.Networkexception), Toast.LENGTH_SHORT).show();
        }
    }


}
package sakura.printersakura.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.badoo.mobile.util.WeakHandler;

import butterknife.ButterKnife;


/**
 * ━━━━━━神兽保佑━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━代码无BUG━━━━━━
 * 神兽保佑,代码无bug
 * Code is far away from bug with the animal protecting
 */

/**
 * BaseActivity
 *
 * @author 赵磊
 * @date 2017-10-26-15-38
 * 功能描述：Activity基类，简化activity初始化流程，整理整体代码
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected WeakHandler mHandler;
    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ready();
        setContentView(setthislayout());
        ButterKnife.bind(this);
        context = this;
        mHandler = new WeakHandler();
        initListener();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 调用显示全屏Activity--必须在**ready()**方法中调用生效！！！
     */
    protected void fullScreen() {
            /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    protected void ready() {

    }

    /**
     * 嵌入界面布局资源
     *
     * @return 界面资源id
     */
    protected abstract int setthislayout();

    /**
     * 初始化监听
     */
    protected abstract void initListener();


    /**
     * 初始化数据
     */
    protected abstract void initData();


}
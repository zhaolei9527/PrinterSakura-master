package sakura.printersakura.Bean;

/**
 * sakura.printersakura.Bean
 *
 * @author 赵磊
 * @date 2018/1/10
 * 功能描述：
 */
public class CmdEvent {
    private String mMsg;
    private String mType;

    public CmdEvent(String msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
        mType = "";
    }

    public CmdEvent(String msg, String type) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
        mType = type;
    }

    public String getMsg() {
        return mMsg;
    }


    public String getmType() {
        return mType;
    }

}

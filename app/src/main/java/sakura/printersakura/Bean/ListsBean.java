package sakura.printersakura.Bean;

import java.util.List;

/**
 * sakura.printersakura.Bean
 *
 * @author 赵磊
 * @date 2018/1/10
 * 功能描述：
 */
public class ListsBean {

    /**
     * moneys : -2
     * moneys1 : 24
     * data : [{"odds":"45","mingxi_2":"126","mingxi_3":"现","addtime":"1515390372","did":"201801081346124100","money":1,"id":"232"},{"odds":"7300","mingxi_2":"1288","mingxi_3":"定","addtime":"1515390161","did":"201801081342418859","money":1,"id":"231"},{"odds":"7300","mingxi_2":"1688","mingxi_3":"定","addtime":"1515389078","did":"201801081324386128","money":1,"id":"230"}]
     * code : 200
     * count : 3  笔数
     * money : 3  总金额
     * time : 2018-01-08 13:24  购买时间
     * did : 201801081324386128  编号
     * moneys2 : -26
     * username : fc023  会员名
     */

    private String moneys;
    private String moneys1;
    private String code;
    private String count;
    private String money;
    private String time;
    private String did;
    private String moneys2;
    private String username;
    private List<DataBean> data;

    public String getMoneys() {
        return moneys;
    }

    public void setMoneys(String moneys) {
        this.moneys = moneys;
    }

    public String getMoneys1() {
        return moneys1;
    }

    public void setMoneys1(String moneys1) {
        this.moneys1 = moneys1;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getMoneys2() {
        return moneys2;
    }

    public void setMoneys2(String moneys2) {
        this.moneys2 = moneys2;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * odds : 45   赔率
         * mingxi_2 : 126  号码
         * mingxi_3 : 现    类型
         * addtime : 1515390372
         * did : 201801081346124100
         * money : 1  金额
         * id : 232
         */

        private String odds;
        private String mingxi_2;
        private String mingxi_3;
        private String addtime;
        private String did;
        private String money;
        private String id;

        public String getOdds() {
            return odds;
        }

        public void setOdds(String odds) {
            this.odds = odds;
        }

        public String getMingxi_2() {
            return mingxi_2;
        }

        public void setMingxi_2(String mingxi_2) {
            this.mingxi_2 = mingxi_2;
        }

        public String getMingxi_3() {
            return mingxi_3;
        }

        public void setMingxi_3(String mingxi_3) {
            this.mingxi_3 = mingxi_3;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

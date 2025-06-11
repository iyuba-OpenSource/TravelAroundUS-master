package com.iyuba.core.lil.model.remote.bean.ad;

import java.io.Serializable;

/**
 * @title: 广告信息
 * @date: 2023/12/13 16:21
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Ad_result implements Serializable {

    /**
     * result : 1
     * data : {"id":"2000","adId":"新概念英语ads1上线版","startuppic_StartDate":"2023-03-22","startuppic_EndDate":"2023-12-31","startuppic":"upload/1698994214911.png","type":"ads1","startuppic_Url":"https://apps.iyuba.cn/mall/teachercourse.html","classNum":"0"}
     */

    private String result;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 2000
         * adId : 新概念英语ads1上线版
         * startuppic_StartDate : 2023-03-22
         * startuppic_EndDate : 2023-12-31
         * startuppic : upload/1698994214911.png
         * type : ads1
         * startuppic_Url : https://apps.iyuba.cn/mall/teachercourse.html
         * classNum : 0
         * title :
         */

        private String id;
        private String adId;
        private String startuppic_StartDate;
        private String startuppic_EndDate;
        private String startuppic;
        private String type;
        private String startuppic_Url;
        private String classNum;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getStartuppic_StartDate() {
            return startuppic_StartDate;
        }

        public void setStartuppic_StartDate(String startuppic_StartDate) {
            this.startuppic_StartDate = startuppic_StartDate;
        }

        public String getStartuppic_EndDate() {
            return startuppic_EndDate;
        }

        public void setStartuppic_EndDate(String startuppic_EndDate) {
            this.startuppic_EndDate = startuppic_EndDate;
        }

        public String getStartuppic() {
            return startuppic;
        }

        public void setStartuppic(String startuppic) {
            this.startuppic = startuppic;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartuppic_Url() {
            return startuppic_Url;
        }

        public void setStartuppic_Url(String startuppic_Url) {
            this.startuppic_Url = startuppic_Url;
        }

        public String getClassNum() {
            return classNum;
        }

        public void setClassNum(String classNum) {
            this.classNum = classNum;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}

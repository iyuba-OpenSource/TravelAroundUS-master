package com.iyuba.concept2.api.data;

import java.util.Date;
import java.util.List;

public class EvaluateRecordResponse {

    private String ResultCode;
    private String Message;
    private int PageNumber;
    private int TotalPage;
    private int FirstPage;
    private int PrevPage;
    private int NextPage;
    private int LastPage;
    private int AddScore;
    private int Counts;
    private List<Data> data;

    public void setResultCode(String ResultCode) {
        this.ResultCode = ResultCode;
    }

    public String getResultCode() {
        return ResultCode;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return Message;
    }

    public void setPageNumber(int PageNumber) {
        this.PageNumber = PageNumber;
    }

    public int getPageNumber() {
        return PageNumber;
    }

    public void setTotalPage(int TotalPage) {
        this.TotalPage = TotalPage;
    }

    public int getTotalPage() {
        return TotalPage;
    }

    public void setFirstPage(int FirstPage) {
        this.FirstPage = FirstPage;
    }

    public int getFirstPage() {
        return FirstPage;
    }

    public void setPrevPage(int PrevPage) {
        this.PrevPage = PrevPage;
    }

    public int getPrevPage() {
        return PrevPage;
    }

    public void setNextPage(int NextPage) {
        this.NextPage = NextPage;
    }

    public int getNextPage() {
        return NextPage;
    }

    public void setLastPage(int LastPage) {
        this.LastPage = LastPage;
    }

    public int getLastPage() {
        return LastPage;
    }

    public void setAddScore(int AddScore) {
        this.AddScore = AddScore;
    }

    public int getAddScore() {
        return AddScore;
    }

    public void setCounts(int Counts) {
        this.Counts = Counts;
    }

    public int getCounts() {
        return Counts;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "EvaluateRecordResponse{" +
                "ResultCode='" + ResultCode + '\'' +
                ", Message='" + Message + '\'' +
                ", PageNumber=" + PageNumber +
                ", TotalPage=" + TotalPage +
                ", FirstPage=" + FirstPage +
                ", PrevPage=" + PrevPage +
                ", NextPage=" + NextPage +
                ", LastPage=" + LastPage +
                ", AddScore=" + AddScore +
                ", Counts=" + Counts +
                ", data=" + data +
                '}';
    }

    public class Data {

        private String ImgSrc;
        private String image;
        private int backId;
        private String backList;
        private String UserName;
        private String ShuoShuoType;
        private String ShuoShuo;
        private String TopicCategory;
        private String title;
        private String CreateDate;
        private String score;
        private String paraid;
        private String topicid;
        private String againstCount;
        private String videoUrl;
        private String Userid;
        private String agreeCount;
        private String id;
        private String idIndex;
        private String vip;

        public void setImgSrc(String ImgSrc) {
            this.ImgSrc = ImgSrc;
        }

        public String getImgSrc() {
            return ImgSrc;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImage() {
            return image;
        }

        public void setBackId(int backId) {
            this.backId = backId;
        }

        public int getBackId() {
            return backId;
        }

        public void setBackList(String backList) {
            this.backList = backList;
        }

        public String getBackList() {
            return backList;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getUserName() {
            return UserName;
        }

        public void setShuoShuoType(String ShuoShuoType) {
            this.ShuoShuoType = ShuoShuoType;
        }

        public String getShuoShuoType() {
            return ShuoShuoType;
        }

        public void setShuoShuo(String ShuoShuo) {
            this.ShuoShuo = ShuoShuo;
        }

        public String getShuoShuo() {
            return ShuoShuo;
        }

        public void setTopicCategory(String TopicCategory) {
            this.TopicCategory = TopicCategory;
        }

        public String getTopicCategory() {
            return TopicCategory;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setCreateDate(String CreateDate) {
            this.CreateDate = CreateDate;
        }

        public String getCreateDate() {
            return CreateDate;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getScore() {
            return score;
        }

        public void setParaid(String paraid) {
            this.paraid = paraid;
        }

        public String getParaid() {
            return paraid;
        }

        public void setTopicid(String topicid) {
            this.topicid = topicid;
        }

        public String getTopicid() {
            return topicid;
        }

        public void setAgainstCount(String againstCount) {
            this.againstCount = againstCount;
        }

        public String getAgainstCount() {
            return againstCount;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setUserid(String Userid) {
            this.Userid = Userid;
        }

        public String getUserid() {
            return Userid;
        }

        public void setAgreeCount(String agreeCount) {
            this.agreeCount = agreeCount;
        }

        public String getAgreeCount() {
            return agreeCount;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setIdIndex(String idIndex) {
            this.idIndex = idIndex;
        }

        public String getIdIndex() {
            return idIndex;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public String getVip() {
            return vip;
        }

    }

}



package com.iyuba.core.common.retrofitapi.result;

import java.util.List;

public class QQSupportResult {

    public int result;
    public List<QQData> data;

    public int getResult() {
        return result;
    }

    public List<QQData> getData() {
        return data;
    }

    public static class QQData {
        public String editor;
        public String technician;
        public String manager;

        public String getEditor() {
            return editor;
        }

        public String getTechnician() {
            return technician;
        }

        public String getManager() {
            return manager;
        }
    }
}

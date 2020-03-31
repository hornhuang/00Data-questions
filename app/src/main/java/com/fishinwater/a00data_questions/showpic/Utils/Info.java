package com.fishinwater.a00data_questions.showpic.Utils;

/**
 * @author fishinwater-1999
 * @date :2020/3/31 14:29
 */
public class Info {
    /**
     * errno : 0
     * message :
     * data : {"code":"200","msg":"操作成功!","res":"https://tkmp.tmtreading.cn/addons/nets_haojk/cache/71c13eb73bc5bb9e3f8c1657f4 190de0.jpg"}
     */
    private int errno;
    private String message;
    private DataBean data;

    public Info() {
        data = new DataBean();
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * code : 200
         * msg : 操作成功!
         * res : https://tkmp.tmtreading.cn/addons/nets_haojk/cache/71c13eb73bc5bb9e3f8c1657f4 190de0.jpg
         */

        private String code;
        private String msg;
        private String res;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getRes() {
            return res;
        }

        public void setRes(String res) {
            this.res = res;
        }
    }
}

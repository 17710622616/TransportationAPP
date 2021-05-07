package com.youcoupon.john_li.transportationapp.TMSModel;

public class TestModel {
    /**
     * tbk_tpwd_create_response : {"data":{"model":"8緮置内容￥hxlgccEumBB￥达开?τao寶?或掂击炼接 https://m.tb.cn/h.VvkiWSm 至浏.览览.器【618超级红包】","password_simple":"￥hxlgccEumBB￥"}}
     */

    private TbkTpwdCreateResponseBean tbk_tpwd_create_response;

    public TbkTpwdCreateResponseBean getTbk_tpwd_create_response() {
        return tbk_tpwd_create_response;
    }

    public void setTbk_tpwd_create_response(TbkTpwdCreateResponseBean tbk_tpwd_create_response) {
        this.tbk_tpwd_create_response = tbk_tpwd_create_response;
    }

    public static class TbkTpwdCreateResponseBean {
        /**
         * data : {"model":"8緮置内容￥hxlgccEumBB￥达开?τao寶?或掂击炼接 https://m.tb.cn/h.VvkiWSm 至浏.览览.器【618超级红包】","password_simple":"￥hxlgccEumBB￥"}
         */

        private DataBean data;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * model : 8緮置内容￥hxlgccEumBB￥达开?τao寶?或掂击炼接 https://m.tb.cn/h.VvkiWSm 至浏.览览.器【618超级红包】
             * password_simple : ￥hxlgccEumBB￥
             */

            private String model;
            private String password_simple;

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getPassword_simple() {
                return password_simple;
            }

            public void setPassword_simple(String password_simple) {
                this.password_simple = password_simple;
            }
        }
    }
}

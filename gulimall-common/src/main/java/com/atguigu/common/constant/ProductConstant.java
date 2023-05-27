package com.atguigu.common.constant;

import lombok.Data;

/**
 * @author lihaohui
 * @date 2023/5/21
 */
public class ProductConstant {
    public enum AttrEnum {
        /**
         * 属性：基本属性
         */
        ATTR_TYPE_BASE(1,"基本属性"),
        /**
         * 属性：销售属性
         */
        ATTR_TYPE_SALE(0,"销售属性"),

        SEARCH_TYPE(1, "是否需要检索-需要"),

        UN_SEARCH_TYPE(0, "是否需要检索-不需要");


        private int code;

        private String msg;

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }
    }

    public enum StatusEnum {
        NEW_SPU(0, "新建"), SPU_UP(1, "商品上架"), SPU_DOWN(2, "商品下架");

        private int code;
        private String msg;

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}

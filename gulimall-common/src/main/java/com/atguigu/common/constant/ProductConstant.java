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
        ATTR_TYPE_SALE(0,"销售属性");

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
}

package com.chiu.naishuawangnextbackend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum UserRoleEnum {

    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     *
     * @param value 枚举的value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
         if(ObjectUtil.isEmpty(value)){
             return null;
         }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.getValue().equals(value)) {
                return userRoleEnum;
            }
        }
        //使用map就将顺序查找变成了直接查找，效率更高
        Map<String, UserRoleEnum> map = new HashMap<>();
        map.put("admin",ADMIN);
        return null;

        }
}

package com.vip.microservice.commons.base.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author echo
 * @title: BooleanEnum
 * @date 2023/3/16 17:49
 */
@RequiredArgsConstructor
public enum BooleanEnum {

    NEGATIVE( 0, false, "否"),

    POSITIVE(1, true, "是");

    @Getter
    private final Integer value;

    @Getter
    private final boolean bool;

    @Getter
    private final String desc;

    public static BooleanEnum valueOf(Integer value){
        for (BooleanEnum enums : BooleanEnum.values()) {
            if (enums.getValue().equals(value)){
                return enums;
            }
        }
        return null;
    }
}
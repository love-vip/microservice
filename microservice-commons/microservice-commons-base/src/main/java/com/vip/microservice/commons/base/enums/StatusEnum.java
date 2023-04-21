package com.vip.microservice.commons.base.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author echo
 * @title: StatusEnum
 * @date 2023/3/16 9:50
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StatusEnum {

    VALID(0, "生效"),

    INVALID(1, "失效");

    @Getter
    private Integer status;

    @Getter
    private String desc;

    /**
     * Gets enum.
     *
     * @param status the status
     *
     * @return the enum
     */
    public static StatusEnum statusOf(int status) {
        for (StatusEnum ele : StatusEnum.values()) {
            if (ele.getStatus() == status) {
                return ele;
            }
        }
        return null;
    }

}

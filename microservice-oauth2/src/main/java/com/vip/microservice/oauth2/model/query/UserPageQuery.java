package com.vip.microservice.oauth2.model.query;

import com.vip.microservice.commons.base.dto.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author echo
 * @title: UserPageQuery
 * @date 2023/3/16 13:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends BaseQuery {

    private String username;
}

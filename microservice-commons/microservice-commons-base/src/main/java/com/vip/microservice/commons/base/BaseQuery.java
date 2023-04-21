package com.vip.microservice.commons.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author echo
 * @title: BaseQuery
 * @date 2023/3/16 15:27
 */
@Data
public class BaseQuery implements Serializable {

	private static final long serialVersionUID = 3319698607712846427L;

	/**
	 * 当前页
	 */
	@Schema(title = "当前页", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Integer pageNum = 1;

	/**
	 * 每页条数
	 */
	@Schema(title = "每页条数", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Integer pageSize = 10;

}

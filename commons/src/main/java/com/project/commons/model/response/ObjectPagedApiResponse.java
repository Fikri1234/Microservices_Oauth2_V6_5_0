package com.project.commons.model.response;

import lombok.Data;

@Data
public class ObjectPagedApiResponse {
	private String status;
	private String message;
	private Object data;
	private Integer page;
	private Integer totalPages;
	private Long totalElement;
}

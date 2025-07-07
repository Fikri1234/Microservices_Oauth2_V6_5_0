package com.project.commons.controller;


import com.project.commons.model.response.ObjectApiResponse;
import com.project.commons.model.response.ObjectPagedApiResponse;
import com.project.commons.utils.MessageUtil;
import org.springframework.data.domain.Page;

public class BaseController {

	protected String getDefaultStatusSuccessMessage() {
		return MessageUtil.getMessage("msg.success");
	}

	protected String getDefaultSuccessMessage() {
		return MessageUtil.getMessage("msg.data.get.success");
	}
	
	public ObjectApiResponse responseApi(Object data) {

		ObjectApiResponse dto = new ObjectApiResponse();
		dto.setStatus(getDefaultStatusSuccessMessage());
		dto.setMessage(getDefaultSuccessMessage());
		dto.setData(data);
		
		return dto;
	}
	
	public <T> ObjectPagedApiResponse responseApiPaged(Page<T> page) {

		ObjectPagedApiResponse dto = new ObjectPagedApiResponse();
		dto.setStatus(getDefaultStatusSuccessMessage());
		dto.setMessage(getDefaultSuccessMessage());
		dto.setData(page.getContent());
		dto.setPage(page.getNumber());
		dto.setTotalElement(page.getTotalElements());
		dto.setTotalPages(page.getTotalPages());
		
		return dto;
	}

}

package com.project.commons.controller;


import com.project.commons.model.enums.MethodMessage;
import com.project.commons.model.enums.StatusConstant;
import com.project.commons.model.response.ObjectApiResponse;
import com.project.commons.model.response.ObjectPagedApiResponse;
import com.project.commons.utils.ResourceBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class BaseController {

	private final ResourceBundle resourceBundle;
	
	public ObjectApiResponse responseApi(Object data) {

		ObjectApiResponse dto = new ObjectApiResponse();
		if (StringUtils.hasText(dto.getStatus()) || StringUtils.hasText(dto.getMessage())) {
			dto.setStatus(StatusConstant.SUCCESS.getEn());
			dto.setMessage(resourceBundle.getMessage("msg.data.get.success"));
		}
		dto.setData(data);
		
		return dto;
	}
	
	public <T> ObjectPagedApiResponse responseApiPaged(Page<T> page) {

		ObjectPagedApiResponse dto = new ObjectPagedApiResponse();
		if (StringUtils.hasText(dto.getStatus()) || StringUtils.hasText(dto.getMessage())) {
			dto.setStatus(StatusConstant.SUCCESS.getEn());
			dto.setMessage(resourceBundle.getMessage("msg.data.get.success"));
		}
		dto.setData(page.getContent());
		dto.setPage(page.getNumber());
		dto.setTotalElement(page.getTotalElements());
		dto.setTotalPages(page.getTotalPages());
		
		return dto;
	}

}

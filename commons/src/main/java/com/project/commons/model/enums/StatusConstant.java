package com.project.commons.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * Created by user on Jul, 2024
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum StatusConstant {

    OK("Ok", "Ok"),
    SUCCESS("Berhasil", "Success"),
    PERMISSION_DENIED("Izin ditolak", "Permission Denied"),
    FAILED("Gagal", "Failed"),
    ERROR("Error", "Error"),
    ;

    private String in;
    private String en;

    public static StatusConstant compare(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        for (StatusConstant statusConstant : values()) {
            if (statusConstant.name().equals(name)) {
                return statusConstant;
            }
        }
        return null;
    }
}

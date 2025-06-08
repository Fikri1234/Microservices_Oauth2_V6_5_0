package com.project.commons.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by user on 16:03 18/05/2025, 2025
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum GenderEnum {

    MALE("Male", "Laki-laki"),
    FEMMALE("Female", "Perempuan"),
    ;

    private String nameEn;
    private String nameId;
}

package com.umbrella.dto.img;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteS3FileByFileNameDto {

    @NotNull
    private String awsFileName;
}

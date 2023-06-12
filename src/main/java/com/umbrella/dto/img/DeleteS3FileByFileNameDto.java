package com.umbrella.dto.img;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class DeleteS3FileByFileNameDto {

    @NotNull
    private String awsFileName;
}

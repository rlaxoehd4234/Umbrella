package com.umbrella.controller;

import com.umbrella.dto.img.DeleteS3FileByFileNameDto;
import com.umbrella.dto.img.ImgDto;
import com.umbrella.service.Impl.AwsS3UploadServiceImpl;
import com.umbrella.service.Impl.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

    private final AwsS3UploadServiceImpl awsS3UploadService;
    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ImgDto imgUpload(@RequestParam MultipartFile image) throws IOException {
        return fileUploadService.uploadImage(image, "post");
    }

    @PostMapping("/delete")
    public void imgDelete(@Valid @RequestBody DeleteS3FileByFileNameDto dto){
        awsS3UploadService.deleteFile(dto.getAwsFileName());
    }


}

package com.umbrella.service.Impl;

import com.umbrella.dto.img.ImgDto;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.ObjectMetadata;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AwsS3UploadServiceImpl awsS3UploadService;

    public ImgDto uploadImage(MultipartFile file, String dirName) throws IOException { // TODO : Exception Handling

        String fileName = dirName + "/" + createFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        InputStream inputStream = file.getInputStream();


        // 원본 이미지 업로드
        awsS3UploadService.uploadFile(inputStream, objectMetadata, fileName);

        // 리사이징 하는 이미지
        if(Objects.equals(dirName, "post")){

            // 리사이징 이미지 파일 이름
            String resizedFileName = fileName.replace(dirName, dirName+ "_resized");

            BufferedImage srcImage = ImageIO.read(file.getInputStream());
            int srcHeight = srcImage.getHeight();
            int srcWidth = srcImage.getWidth();
            double dWidth;
            double dHeight;

            if (srcWidth == srcHeight) {
                dWidth = 290;
                dHeight = 290;
            } else if (srcWidth > srcHeight) {
                dWidth = 290;
                dHeight = ((double) srcHeight / (double) srcWidth) * 290;
            } else {
                dHeight = 290;
                dWidth = ((double) srcWidth / (double) srcHeight) * 290;
            }

            Image imgTarget = srcImage.getScaledInstance((int) dWidth, (int) dHeight, Image.SCALE_SMOOTH);
            int pixels[] = new int[(int) (dWidth * dHeight)];
            PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, (int) dWidth, (int) dHeight, pixels, 0, (int) dWidth);
            try {
                pg.grabPixels();
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }
            BufferedImage destImg;
            if(checkImageType(file).toUpperCase().equals("png")){
                destImg = new BufferedImage((int) dWidth, (int) dHeight, BufferedImage.TYPE_INT_ARGB);
            }else{
                destImg = new BufferedImage((int) dWidth, (int) dHeight, BufferedImage.TYPE_INT_RGB);
            }
            destImg.setRGB(0, 0, (int) dWidth, (int) dHeight, pixels, 0, (int) dWidth);

            ByteArrayOutputStream uploadOs = new ByteArrayOutputStream();
            ImageIO.write(destImg,  checkImageType(file), uploadOs);

            InputStream is = new ByteArrayInputStream(uploadOs.toByteArray());
            ObjectMetadata ob = new ObjectMetadata();
            ob.setContentType(checkImageType(file));
            ob.setContentLength(uploadOs.size());


            // 리사이징 이미지 업로드
            awsS3UploadService.uploadFile(is,ob,resizedFileName);

        }


        // 원본 이미지 정보만 저장
        return ImgDto.builder()
                .imgName(fileName)
                .imgUrl(awsS3UploadService.getFileUrl(fileName))
                .build();

    }

    //이미지 파일명 중복 방지
    private String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 이미지 파일 형식이 올바른지 아닌지 검사
    private String getFileExtension(String fileName){

        if(fileName.length() == 0){
            System.out.println("이미지 업로드 실패"); // TODO : Exception Handling
        }

        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");

        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        // .부터 시작하는 문자열을 리턴

        if(!fileValidate.contains(idxFileName)){
            System.out.println("이미지 파일 형식이 잘못되었음"); // TODO : Exception Handling
        }

        return fileName.substring(fileName.lastIndexOf(".")); // 확장자 리턴
    }

    // 이미지 확장자 분리 - 라사이징 이미지용
    private String checkImageType(MultipartFile file) throws IOException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());

        if (!mimeType.startsWith("image/")) {
            System.out.println("잘못된 이미지 형식"); // TODO : Exception Handling
        }

        return mimeType.substring(6);
    }


}

package com.umbrella.service.Impl;

import com.umbrella.domain.Post.Post;
import com.umbrella.domain.PostImg.PostImg;
import com.umbrella.domain.PostImg.PostImgRepository;
import com.umbrella.dto.post.PostSaveRequestDto;
import com.umbrella.dto.post.PostUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostImgServiceImpl {

    private final PostImgRepository postImgRepository;
    private final AwsS3UploadServiceImpl awsS3UploadService;

    public void createPostImg(PostSaveRequestDto requestDto, Post post){

        List<PostImg> postImgList = new ArrayList<>();

        if(requestDto.getFileNameList() != null) {

            for(String fileName : requestDto.getFileNameList()){
                PostImg postImg = PostImg.builder()
                        .post(post) // 연결관계 생성
                        .board(post.getBoard())
                        .workSpace(post.getBoard().getWorkSpace())
                        .imgName(fileName)
                        .build();

                postImgList.add(postImg);
            }

        }

        postImgRepository.saveAll(postImgList);
    }


    // 새롭게 추가되는 이미지 -> DB에 저장
    // 새롭게 삭제되는 이미지 -> DB에 삭제
    public void updatePostImg(Post post, PostUpdateRequestDto requestDto){

        List<PostImg> postImgList = new ArrayList<>();

        // 새로운 이미지 추가
        if(requestDto.getAddedFileNameList() != null){
            for(String fileName : requestDto.getAddedFileNameList()){
                PostImg postImg = PostImg.builder()
                        .post(post) // 연결관계 생성
                        .board(post.getBoard())
                        .workSpace(post.getBoard().getWorkSpace())
                        .imgName(fileName)
                        .build();

                postImgList.add(postImg);
            }
            postImgRepository.saveAll(postImgList);
        }

        // 삭제되는 이미지
        if(requestDto.getDeletedFileNameList() != null){

            for(String fileName : requestDto.getDeletedFileNameList()){
                postImgRepository.deleteByImgName(fileName);
            }

        }

    }


    // 1. PostId로 postImg 엔터티들 갖고오기
    // 2. for문 돌아가면서 AwsS3에 삭제 요청 날리기
    // 3. DB에 삭제 쿼리 날리기
    public void postImgDeletedByPostId(Long postId){
        List<PostImg> postImgList = postImgRepository.findAllByPostId(postId);

        for(PostImg postImg : postImgList){
            awsS3UploadService.deleteFile(postImg.getImgName());
            awsS3UploadService.deleteFile(postImg.getImgName()
                    .replace("post/","post-resized/"));
        }

        postImgRepository.deleteAllByPostId(postId);
    }

    // 1. boardId 로 postImg 엔터티들 갖고오기
    // 2. for문 돌아가면서 AwsS3에 삭제 요청 날리기
    // 3. DB에 벌크성 삭제 쿼리 날리기
    public void postImgDeletedByBoardId(Long boardId){
        List<PostImg> postImgList = postImgRepository.findAllByBoard_Id(boardId);

        for(PostImg postImg : postImgList){
            awsS3UploadService.deleteFile(postImg.getImgName());
            awsS3UploadService.deleteFile(postImg.getImgName()
                    .replace("post/","post-resized/"));
        }

        postImgRepository.deleteAllByBoardId(boardId);
    }

    // 1. boardId 로 postImg 엔터티들 갖고오기
    // 2. for문 돌아가면서 AwsS3에 삭제 요청 날리기
    // 3. DB에 벌크성 삭제 쿼리 날리기
    public void postImgDeletedByWorkspaceId(Long workspaceId){

        List<PostImg> postImgList = postImgRepository.findAllByWorkSpaceId(workspaceId);

        for(PostImg postImg : postImgList){
            awsS3UploadService.deleteFile(postImg.getImgName());
            awsS3UploadService.deleteFile(postImg.getImgName()
                    .replace("post/","post-resized/"));
        }
        postImgRepository.deleteAllByWorkSpaceId(workspaceId);
    }


}

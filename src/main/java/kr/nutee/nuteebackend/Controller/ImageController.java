package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.PostShowResponse;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Service.S3Service;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RefreshScope
@RequestMapping(path = "/sns/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final S3Service s3Service;

    /*
        이미지 S3에 업로드
     */
    @PostMapping("")
    public ResponseEntity<ResponseResource> uploadImages(MultipartHttpServletRequest mtfRequest) {
        List<MultipartFile> fileList = mtfRequest.getFiles("images");
        List<String> srcList = new ArrayList<>();
        for (MultipartFile files : fileList) {
            try {
                String imgPath = s3Service.upload(files);
                srcList.add(imgPath);
            } catch (IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(srcList)
            .build();

        ResponseResource resource = new ResponseResource(response, ImageController.class);
        return ResponseEntity.ok().body(resource);
    }

}

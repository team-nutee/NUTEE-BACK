package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Service.S3Service;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;


@RestController
@RequestMapping(path = "/sns/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class ImageController {

    private final S3Service s3Service;

    /*
        이미지 S3에 업로드
     */
    @PostMapping("")
    public List<String> uploadImages(MultipartHttpServletRequest mtfRequest) {
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
        return srcList;
    }

}

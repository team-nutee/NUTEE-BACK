package kr.nutee.nuteebackend.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/sns/upload",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class ImageController {

    @Value("${my.upload.path}")
    String uploadPath;

    @RequestMapping(value = "")
    public List<String> uploadImage(MultipartHttpServletRequest mtfRequest) {
        List<MultipartFile> fileList = mtfRequest.getFiles("files");
        List<String> srcList = new ArrayList<>();

        for (MultipartFile mf : fileList) {
            String originFileName = mf.getOriginalFilename(); // 원본 파일 명
            long fileSize = mf.getSize(); // 파일 사이즈

            log.info("originFileName : " + originFileName);
            log.info("fileSize : " + fileSize);

            String safeFile = uploadPath + System.currentTimeMillis() + "_" + originFileName;
            try {
                mf.transferTo(new File(safeFile));
                srcList.add(safeFile);
            } catch (IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return srcList;
    }


}

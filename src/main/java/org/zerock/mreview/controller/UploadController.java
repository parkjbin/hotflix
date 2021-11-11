package org.zerock.mreview.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.mreview.dto.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
public class UploadController {

    @Value("${org.zerock.upload.path}") // application.properties 파일에 설정된 변수
    private String uploadPath;
    
    // ResponseEntity<> : 클라이언트에게 View의 정보가 아닌 HTTP 정보만을 반환해야 할때
    // HTTP 상태 코드 표 : https://developer.mozilla.org/ko/docs/Web/HTTP/Status
    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) { // 파일 업로드
        // UploadResultDTO 클래스 타입의 List 컬렉션 객체 생성
        List<UploadResultDTO> resultDTOList = new ArrayList<>();

        for(MultipartFile uploadFile : uploadFiles) {
            // 이미지 파일만 업로드 가능, 즉 이미지 파일인지 아닌지 검사
            if(uploadFile.getContentType().startsWith("image") == false) {
                // log.warn(): 처리 가능한 문제, 향후 시스템 에러의 원인이 될 수 있는 경고성 메시지를 나타냄
                log.warn("이 파일은 이미지 타입이 아닙니다.");
                // HttpStatus.FORBIDDEN: HTTP 403(상태 코드) Forbidden 클라이언트 오류 상태 응답 코드는 서버에 요청이 전달되었지만, 권한 때문에 거절되었다는 것을 의미
                // 로그인 로직(틀린 비밀번호로 로그인 행위)처럼 반응하여 재인증(re-authenticating)을 하더라도 지속적으로 접속을 거절
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // 실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
            String originalName = uploadFile.getOriginalFilename();
            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

            log.info("fileName: " + fileName);

            // 날짜 폴더 생성, 하단 makeFolder() 구현함
            String folderPath = makeFolder();

            // UUID(고유 ID)
            String uuid = UUID.randomUUID().toString();

            // 저장할 파일 이름 중간에 "_"를 이용해서 구분
            // File.separator: 구분자를 문자열로 가져옴, 여기서는 \
            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

            Path savePath = Paths.get(saveName);

            try {
                // transferTo(): 원하는 위치에 원본 파일 저장, 실제 이미지 저장
                uploadFile.transferTo(savePath);

                // 섬네일 생성
                String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator + "s_" + uuid + "_" + fileName;
                // 섬네일 파일 이름은 중간에 s_로 시작하도록
                File thumbnailFile = new File(thumbnailSaveName);
                // 섬네일 생성
                Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);

                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // HttpStatus.OK: Http 200(상태 코드) 요청이 성공적으로 되었습니다
        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }

    // 날짜 폴더 생성, 개발자 생성 일반 메소드
    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);
        // 폴더 생성, uploadPath: application.properties 파일에서 설정한 경로
        File uploadPathFolder = new File(uploadPath, folderPath);

        // exists(): 경로에 폴더의 존재 여부
        // mkdirs(): 한 번에 여러 디렉토리를 생성, 해당 경로에 폴더가 없다면 생성
        if(uploadPathFolder.exists() == false) {
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName, String size) { // JSON으로 반환된 업로드 이미지 출력하기

        ResponseEntity<byte[]> result = null;

        try {
            // URLDecoder.decode(): 인터넷을 통해 전송할 수 있는 형식화된 문자를 원래의 문자열로 변환
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("fileName: " + srcFileName);

            File file = new File(uploadPath + File.separator + srcFileName);
            if(size != null && size.equals("1")) {
                // 원본 이미지 파일 객체 생성
                file = new File(file.getParent(), file.getName().substring(2));
            }
            log.info("file: " + file);

            // HTTP 요청 또는 응답 헤더를 나타내는 데이터 구조로, 문자열 헤더 이름을 문자열 값 목록에 매핑하고 일반적인 애플리케이션 수준 데이터 유형에 대한 접근자를 제공
            HttpHeaders header = new HttpHeaders();

            // MIME타입 처리
            // header.add(): 헤더 이름에 대한 값 목록에 헤더 값을 추가
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            // 파일 데이터 처리, FileCopyUtils.copyToByteArray(): 파일 및 스트림 복사를 위한 간단한 유틸리티 방법
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (Exception e) {
            // 요청을 처리하는 중 문제가 발생한 경우
            log.error(e.getMessage());
            e.printStackTrace();
            // HttpStatus.INTERNAL_SERVER_ERROR: HTTP 500 Internal Server Error 서버 에러 응답 코드는 요청을 처리하는 과정에서 서버가 예상하지 못한 상황에 놓였다는 것을 나타냅니다.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @PostMapping("removeFile")
    public ResponseEntity<Boolean> removeFile(String fileName) {
        String srcFileName = null;

        try {
            srcFileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(uploadPath + File.separator + srcFileName);
            boolean result = file.delete();

            File thumbnail = new File(file.getParent(), "s_" + file.getName());

            result = thumbnail.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

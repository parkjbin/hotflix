package org.zerock.mreview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieImageDTO {
    private String uuid;
    private String imgName;
    private String path;

    // 영화 이미지(원본)의 전체 경로 가져오기
    public String getImageURL() {
        try{
            return URLEncoder.encode(path + "/" + uuid + "_" + imgName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 영화 이미지(썸네일)의 전체 경로 가져오기
    public String getThumbnailURL() {
        try {
            return URLEncoder.encode(path+ "/s_" + uuid + "_" + imgName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}

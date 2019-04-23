package applications.pixiv.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: guohezuzi
 * \* Date: 2019-02-08
 * \* Time: 下午11:42
 * \* Description:图片对象
 * \
 */
@Data
public class Illust {
    @JsonProperty(value = "illust_id")
    private Long illustId;
    private String title;
    private String url;
    private String width;
    private String height;
    private int rank;
    @JsonFormat(pattern = "yyyy年MM月dd日 HH:mm")
    private Date date;
}

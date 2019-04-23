package applications.pixiv.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: guohezuzi
 * \* Date: 2019-02-08
 * \* Time: 下午6:58
 * \* Description:图片
 * \
 */
@Data
public class IllustDto {
    private String mode;
    private String content;

    @JsonFormat(pattern = "yyyyMMdd")
    private Date date;

    @JsonProperty(value = "contents")
    private List<Illust> illustList;
}

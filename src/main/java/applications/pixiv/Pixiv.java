package applications.pixiv;

import applications.pixiv.domain.Illust;
import applications.pixiv.domain.IllustDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import util.proxy.SocksProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: guohezuzi
 * \* Date: 2019-02-05
 * \* Time: 下午4:37
 * \* Description:p站爬虫（每日）
 * \
 */
public class Pixiv {
    private static CloseableHttpClient httpClient = SocksProxy.getProxyClient();
    private static final String DIR_PATH = "data/pixiv/";
    private static StringBuilder saveDir = new StringBuilder();

    enum ModeEnum {
        /*
         * 每月
         * */
        monthly,
        weekly,
        daily,
        male,
        female,
        original,
        ;
    }

    /**
     * 获取p站排名页面的图片 monthly weekly daily male female original(原创)
     * 1.获取图片url
     * 2.获取图片数据
     * 3.去重提取图片
     */
    public static void getRankPictures(ModeEnum mode) {
        String url = "https://www.pixiv.net/ranking.php?mode=" + mode + "&p=1&format=json";
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            IllustDto illustDto = mapper.readValue(httpEntity.getContent(), IllustDto.class);
            EntityUtils.consume(httpEntity);
            response.close();
            // create save dir
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
            Date date = new Date();
            saveDir.append(DIR_PATH).append("rank").append(simpleDateFormat.format(date)).append("/").append(mode).append("/");
            Path path = Paths.get(saveDir.toString());
            if (!Files.exists(path)) {
                try {
                    Files.createDirectory(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Illust> illusts = illustDto.getIllustList();
            int size = illusts.size();
            //每个线程下载10张图片
            int threadNum = (int) Math.ceil(size / 10.0);
            for (int i = 0; i < threadNum; i++) {
                int end = (i + 1) * 10;
                if (end > size) {
                    end = size;
                }
                List<Illust> subIllust = illusts.subList(i * 10, end);
                DownloadThread downloadThread = new DownloadThread(subIllust);
                downloadThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class DownloadThread extends Thread {
        private final List<Illust> illusts;

        DownloadThread(List<Illust> illusts) {
            this.illusts = illusts;
        }

        @Override
        public void run() {
            illusts.forEach((e) -> {
                long id = e.getIllustId();
                String title = e.getTitle().replaceAll("/", " ");
                String filename = "rank" + e.getRank() + "-" + title;
                String refUrl = "https://www.pixiv.net/member_illust.php?mode=medium&illust_id=" + id;
                String illustUrl = e.getUrl().replace("c/240x480/img-master", "img-original").
                        replace("_master1200", "");
                savePixiv(illustUrl, refUrl, filename);
            });
        }
    }

    /**
     * 保存图片默认后缀.jpg 目录为data/pixiv
     */
    private static void savePixiv(String url, String ref, String filename) {
        savePixiv(url, ref, filename, ".jpg");
    }

    /**
     * 保存图片
     */
    private static void savePixiv(String url, String ref, String filename, String suffix) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("referer", ref);
        String filePath = saveDir + filename + suffix;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    System.out.println("线程:" + Thread.currentThread().getName() + "开始下载:" + url);
                    FileOutputStream out = new FileOutputStream(file);
                    HttpEntity entity = httpResponse.getEntity();
                    entity.writeTo(out);
                    out.close();
                    httpResponse.close();
                } else {
                    httpResponse.close();
                    String pngSuffix = ".png";
                    if (!suffix.equals(pngSuffix)) {
                        String newUrl = url.replace(suffix, pngSuffix);
                        savePixiv(newUrl, ref, filename, pngSuffix);
                    } else {
                        System.out.println("unknown suffix");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        if (args.length == 1) {
//            ModeEnum modeEnum = ModeEnum.valueOf(args[0]);
//            Pixiv.getRankPictures(modeEnum);
//        }else {
//            System.out.println("Error arg length");
//        }

        ModeEnum modeEnum = ModeEnum.valueOf("weekly");
        Pixiv.getRankPictures(modeEnum);
    }
}

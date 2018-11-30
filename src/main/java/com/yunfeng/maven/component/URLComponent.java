package com.yunfeng.maven.component;

import com.yunfeng.maven.controller.IndexController;
import com.yunfeng.maven.entity.MavenProxyUrl;
import com.yunfeng.maven.service.MavenService;
import com.yunfeng.maven.util.StreamUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import static javax.servlet.http.HttpServletResponse.SC_OK;

@Component("urlComponent")
public class URLComponent {

    @Resource(name = "mavenService")
    private MavenService mavenService;

    @Resource(name = "fileComponent")
    private FileComponent fileComponent;

    @Resource(name = "logComponent")
    private LogComponent logComponent;

    /**
     * @param url      download url : com/xxx/xxx/1.x.x/xxx-1.x.x.jar
     * @param fileName save file
     * @return successful
     */
    public int downloadFromURL(String url, String fileName) {
        int success = 0;
        Collection<MavenProxyUrl> c = mavenService.getProxyUrls();
        for (MavenProxyUrl mavenProxyUrl : c) {
            String basePath = mavenProxyUrl.getUrl();
            success = download(basePath + url, fileName);
            if (success == SC_OK) {
                break;
            }
        }
        return success;
    }

    private int download(String url, String fileName) {
        HttpURLConnection conn = null;
        InputStream in = null;
        FileOutputStream fos = null;
        int code = 0;
        final String tempFileName = fileName + "_temp";
        int size = 0;// content file size
        int readSize = 0;// all read size
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);//5
            conn.setReadTimeout(5000);
//            conn.setDoOutput(true);// 设置允许输出
            conn.setRequestMethod("GET");
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//            conn.setRequestProperty("Charset", "UTF-8");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            /* 服务器返回的响应码 */
            code = conn.getResponseCode();
            if (code == 200) {
                File downloadFile = new File(fileName);
                if (!downloadFile.getParentFile().exists()) {
                    boolean mkdir = downloadFile.getParentFile().mkdirs();
                    if (!mkdir) {
                        logComponent.e("mkdirs failed: " + url);
                    }
                }
                in = conn.getInputStream();
                size = conn.getContentLength();
                fos = new FileOutputStream(tempFileName);
                byte[] buff = new byte[1024];
                int temp;
                while ((temp = in.read(buff)) != -1) {
                    fos.write(buff, 0, temp);
                    readSize += temp;
                }
            } else {
                logComponent.e("download :" + url + ", failed: " + code);
            }
        } catch (Exception e) {
            logComponent.e("download :" + url + ", failed: " + e.getMessage());
        } finally {
            StreamUtils.closeOutputStream(fos);
            StreamUtils.closeInputStream(in);
            if (conn != null) {
                conn.disconnect();
            }
            if (size > 0 && readSize == size) {
                fileComponent.renameFile(tempFileName, fileName);
            } else {
                logComponent.e("download size not match: " + size + ", context size: " + readSize);
                code = HttpServletResponse.SC_NOT_FOUND;
            }
        }
        return code;
    }


}

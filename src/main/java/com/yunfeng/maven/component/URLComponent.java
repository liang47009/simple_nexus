package com.yunfeng.maven.component;

import com.yunfeng.maven.controller.IndexController;
import com.yunfeng.maven.entity.MavenProxyUrl;
import com.yunfeng.maven.service.MavenService;
import com.yunfeng.maven.util.StreamUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("urlComponent")
public class URLComponent {

    @Resource(name = "mavenService")
    private MavenService mavenService;

    @Resource(name = "fileComponent")
    private FileComponent fileComponent;

    @Resource(name = "logComponent")
    private LogComponent logComponent;

    private Map<String, Object> cache = new HashMap<>();

    /**
     * @param url       download url : com/xxx/xxx/1.x.x/xxx-1.x.x.jar
     * @param directory save directory
     * @param fileName  save file
     * @return successful
     */
    public boolean downloadFromURL(String url, String directory, String fileName) {
        boolean success = false;
        Object ret = cache.get(url);
        if (ret == null) {
            ret = new Object();
            logComponent.w("getlock null");
            synchronized (ret) {
                logComponent.w("getlocked null");
                cache.put(url, ret);
                Collection<MavenProxyUrl> c = mavenService.getProxyUrls();
                for (MavenProxyUrl mavenProxyUrl : c) {
                    String basePath = mavenProxyUrl.getUrl();
                    success = download(basePath + url, IndexController.BASE_PATH + directory + "/", fileName);
                    if (success) {
                        break;
                    }
                }
                cache.remove(url);
            }
        } else {
            logComponent.w("getlock exists");
            synchronized (ret) {
                logComponent.w("getlocked exists");
            }
        }
        return success;
    }

    private boolean download(String url, String directory, String fileName) {
        boolean success = false;
        HttpsURLConnection conn = null;
        InputStream in = null;
        FileOutputStream fos = null;
        final String realFileName = directory + fileName;
        final String tempFileName = realFileName + "_temp";
        int size = 0;// content file size
        int readSize = 0;// all read size
        try {
            conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);//5
            conn.setReadTimeout(5000);
//            conn.setDoOutput(true);// 设置允许输出
            conn.setRequestMethod("GET");
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//            conn.setRequestProperty("Charset", "UTF-8");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            /* 服务器返回的响应码 */
            int code = conn.getResponseCode();
            if (code == 200) {
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
                success = fileComponent.renameFile(tempFileName, realFileName);
            } else {
                logComponent.e("download size not match: " + size + ", context size: " + readSize);
            }
        }
        return success;
    }


}

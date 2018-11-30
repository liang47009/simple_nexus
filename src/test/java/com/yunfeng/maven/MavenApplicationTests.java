package com.yunfeng.maven;

import com.yunfeng.maven.component.LogComponent;
import com.yunfeng.maven.component.URLComponent;
import com.yunfeng.maven.util.StreamUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.*;

import static javax.servlet.http.HttpServletResponse.SC_OK;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MavenApplicationTests {

    private static final String BASE_URL = "/nexus/content/groups/public/";

    public static final String BASE_PATH = "repository/";

    @Resource(name = "urlComponent")
    private URLComponent urlComponent;

    @Resource(name = "logComponent")
    private LogComponent logComponent;

    @Test
    public void contextLoads() {
//        System.setProperty("http.proxyHost", "172.17.36.97");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("http.nonProxyHosts", "192.168.3.249 | 192.168.3.100");
        StringBuffer url = new StringBuffer("http://172.17.14.68:8081/nexus/content/groups/public/io/reactivex/rxjava2/rxandroid/2.0.1/rxandroid-2.0.1-sources.jar");
        int position = url.indexOf(BASE_URL) + BASE_URL.length();
        url.delete(0, position);

        InputStream in = null;
        OutputStream out = null;
        int downloadRet = 0;
        try {
            String filePath = BASE_PATH + url.toString();
            File repoFile = new File(filePath);
            if (!repoFile.exists()) {
                downloadRet = urlComponent.downloadFromURL(url.toString(), filePath);
            }
            if (downloadRet == SC_OK) {
                in = new FileInputStream(repoFile);
                int len = 0;
                byte[] buffer = new byte[1024];
//                out = response.getOutputStream();
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            } else {
                // download failed
                logComponent.e("download failed:" + url);
            }
        } catch (Exception e) {
            logComponent.e("index :" + url + " failed:" + e.getMessage());
        } finally {
            if (downloadRet != SC_OK) {
                try {
//                    response.sendError(downloadRet, "download file error!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            StreamUtils.closeOutputStream(out);
            StreamUtils.closeInputStream(in);
        }
    }

}

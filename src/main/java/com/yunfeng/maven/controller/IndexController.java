package com.yunfeng.maven.controller;

import com.yunfeng.maven.component.LogComponent;
import com.yunfeng.maven.component.URLComponent;
import com.yunfeng.maven.entity.MavenProxyUrl;
import com.yunfeng.maven.service.MavenService;
import com.yunfeng.maven.util.StreamUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

@Controller
public class IndexController {

    private static final String BASE_URL = "/nexus/content/groups/public/";

    public static final String BASE_PATH = "repository/";

    @Resource(name = "mavenService")
    private MavenService mavenService;

    @Resource(name = "logComponent")
    private LogComponent logComponent;

    @Resource(name = "urlComponent")
    private URLComponent urlComponent;

    @RequestMapping(value = BASE_URL + "**")
    public void index(HttpServletRequest request, HttpServletResponse response, Model model) {
        StringBuffer url = request.getRequestURL();
        int position = url.indexOf(BASE_URL) + BASE_URL.length();
        url.delete(0, position);

        position = url.lastIndexOf("/");
        if (position < 0) {
            return;
        }
        String directory = url.substring(0, position);
        String fileName = url.substring(position + 1, url.length());

        File base_direcotry = new File(BASE_PATH + directory);
        if (!base_direcotry.exists()) {
            boolean mkdir = base_direcotry.mkdirs();
            if (!mkdir) {
                logComponent.e("mkdirs failed: " + url);
            }
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            boolean downloadRet = false;
            File repoFile = new File(BASE_PATH + url.toString());
            if (!repoFile.exists()) {
                downloadRet = urlComponent.downloadFromURL(url.toString(), directory, fileName);
            } else {
                downloadRet = true;
            }
            if (downloadRet) {
                in = new FileInputStream(repoFile);
                int len = 0;
                byte[] buffer = new byte[1024];
                out = response.getOutputStream();
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
            StreamUtils.closeOutputStream(out);
            StreamUtils.closeInputStream(in);
        }
    }

    @RequestMapping(value = "/maven", produces = "text/html")
    public String manager(HttpServletRequest request, Model model) {
        Collection<MavenProxyUrl> c = mavenService.getProxyUrls();
        model.addAttribute("urls", c);
        return "manager";
    }

    @RequestMapping(value = "/maven/add")
    public void addUrl(HttpServletRequest request, Model model) {
        String name = request.getParameter("name");
        String url = request.getParameter("url");
        mavenService.add(new MavenProxyUrl(name, url));
    }

    @RequestMapping(value = "/maven/remove")
    public void removeUrl(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        mavenService.remove(Integer.valueOf(id));
    }
}

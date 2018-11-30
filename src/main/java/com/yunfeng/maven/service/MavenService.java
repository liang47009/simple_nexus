package com.yunfeng.maven.service;

import com.yunfeng.maven.entity.MavenProxyUrl;
import com.yunfeng.maven.repository.MavenRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

@Service
public class MavenService {

    @Resource(name = "mavenRepository")
    private MavenRepository mavenRepository;

    public Collection<MavenProxyUrl> getProxyUrls() {
        Collection<MavenProxyUrl> c = mavenRepository.findAll();
        if (c.isEmpty()) {
            c.add(new MavenProxyUrl("mavenRepo1", "https://repo1.maven.org/maven2/"));
            c.add(new MavenProxyUrl("mavenRepo", "http://repo1.maven.org/maven2/"));
            c.add(new MavenProxyUrl("sonatypeRepo", "http://oss.sonatype.org/content/repositories/releases/"));
            c.add(new MavenProxyUrl("apacheSnapshots", "https://repository.apache.org/content/repositories/snapshots/"));
            c.add(new MavenProxyUrl("mavenCenter", "http://central.maven.org/maven2/"));
            c.add(new MavenProxyUrl("jcenter", "https://jcenter.bintray.com/"));
            c.add(new MavenProxyUrl("google", "https://dl.google.com/dl/android/maven2/"));
            c.add(new MavenProxyUrl("ironsource-mobile", "https://dl.bintray.com/ironsource-mobile/android-sdk/"));
            c.add(new MavenProxyUrl("ironsource-mobile-adapter", "http://dl.bintray.com/ironsource-mobile/android-adapters/"));
            mavenRepository.saveAll(c);
        }
        return c;
    }

    public void add(MavenProxyUrl mavenProxyUrl) {
        if (mavenRepository.findByName(mavenProxyUrl.getName()) != null) {
            return;
        }
        mavenRepository.save(mavenProxyUrl);
    }

    public void remove(int id) {
        mavenRepository.deleteById(id);
    }
}

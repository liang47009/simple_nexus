package com.yunfeng.maven.repository;

import com.yunfeng.maven.entity.MavenProxyUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("mavenRepository")
public interface MavenRepository extends JpaRepository<MavenProxyUrl, Integer> {

    @Query("from MavenProxyUrl where name=:name")
    MavenRepository findByName(@Param("name") String name);
}

package com.yunfeng.maven.component;

import org.springframework.stereotype.Component;

import java.io.File;

@Component("fileComponent")
public class FileComponent {

    public boolean renameFile(String file, String toFile) {
        File toBeRenamed = new File(file);
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            return false;
        }
        File newFile = new File(toFile);
        //修改文件名
        return toBeRenamed.renameTo(newFile);
    }

}

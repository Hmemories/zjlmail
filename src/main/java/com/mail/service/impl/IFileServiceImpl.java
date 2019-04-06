package com.mail.service.impl;

import com.google.common.collect.Lists;
import com.mail.service.IFileService;
import com.mail.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
@Slf4j
public class IFileServiceImpl implements IFileService {
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //扩展名
        //adc.jpg
        String fileExtensionName =fileName.substring(fileName.lastIndexOf(".")+1);
        //为了避免文件名重复
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        log.info("开始上传文件,上传文件名为:{},上传文件路径:{},新文件名：:{]" , fileName,path,uploadFileName);
        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);
                //文件已经上传成功

            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器上`

            targetFile.delete();
            //删除targetFile
        } catch (IOException e) {
            log.error("文件上传异常",e);
            return null;
        }
        return targetFile.getName();
    }
}

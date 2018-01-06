package cn.tsukasalwq.service.impl;

import cn.tsukasalwq.service.IFileService;
import cn.tsukasalwq.utils.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        // 扩展名
        // abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},新文件名:{}", fileName, uploadFileName);

        File fIleDir = new File(path);
        if (!fIleDir.exists()) {
            fIleDir.setWritable(true);
            fIleDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            // 文件已经上传成功

            // 2017/9/2 将targetFile上传到我们的FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            // 2017/9/2 上传完成后删除upload下面的文件夹
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上产文件异常", e);
            return null;
        }

        return targetFile.getName();
    }
}

package li.fyun.commons.file.impl;

import li.fyun.commons.file.FileInfo;
import li.fyun.commons.file.YunwenFileUploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
public class LocalFileStorage extends AbstractFileStorage implements ServletContextAware {

    @Resource
    private YunwenFileUploadProperties fileUploadProperties;
    private String basePath;

    @Override
    public FileInfo put(MultipartFile file) throws IOException {
        // 按天存放文件
        String todayFolder = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String folderName = basePath + FILE_SEPARATOR + todayFolder;
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }

        String uploadFileKey = this.getUploadFileKey(file.getOriginalFilename());
        String targetFile = todayFolder + FILE_SEPARATOR + uploadFileKey;
        File localFile = new File(basePath + FILE_SEPARATOR + targetFile);
        try {
            file.transferTo(localFile);
            log.info("文件已上传，原名称{}，目标名称{}。", file.getOriginalFilename(), targetFile);
            return new FileInfo(
                    uploadFileKey, file.getSize(), file.getContentType(),
                    fileUploadProperties.getWebUploadPath() + FILE_SEPARATOR + targetFile
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IOException("文件上传失败。");
        }
    }

    @Override
    public MultipartFile get(String filename) throws IOException, NoSuchMethodException {
        throw new NoSuchMethodException();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        basePath = servletContext.getRealPath(fileUploadProperties.getWebUploadPath());
        log.debug("basePath = {}", basePath);
    }

    @Override
    public String getRealPath(String filename) throws NoSuchMethodException {
        return basePath + filename;
    }

}

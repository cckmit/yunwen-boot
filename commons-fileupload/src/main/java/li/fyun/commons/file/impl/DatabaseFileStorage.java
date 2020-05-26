package li.fyun.commons.file.impl;

import li.fyun.commons.file.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DatabaseFileStorage extends AbstractFileStorage {

    @Resource
    private AttachmentRepository attachmentDao;

    @Override
    public FileInfo put(MultipartFile file) throws IOException {
        Attachment attachment = new Attachment(file);
        attachment.setFileName(this.getUploadFileKey(file.getOriginalFilename()));
        attachmentDao.save(attachment);
        return new FileInfo(
                attachment.getFileName(), file.getSize(), file.getContentType(),
                FILE_PATH_PREFIX + FILE_SEPARATOR + attachment.getId()
        );
    }

    @Override
    public MultipartFile get(String filename) throws IOException {
        Attachment attachment = attachmentDao.getOne(Long.parseLong(filename));
        if (attachment != null) {
            return new SimpleMultipartFile(
                    attachment.getFileName(),
                    attachment.getFileName(),
                    attachment.getContentType(),
                    attachment.getContent());
        } else {
            throw new FileNotFoundException();
        }
    }

}

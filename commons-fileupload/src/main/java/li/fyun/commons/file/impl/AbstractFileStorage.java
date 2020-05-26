package li.fyun.commons.file.impl;

import com.google.common.io.Files;
import li.fyun.commons.core.utils.CodecUtils;
import li.fyun.commons.file.FileStorage;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public abstract class AbstractFileStorage implements FileStorage {

    @Override
    public void validate(MultipartFile file, String[] allowContentTypes) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("不能上传空文件");
        }

        String contentType = file.getContentType();
        if (!PatternMatchUtils.simpleMatch(allowContentTypes, contentType)) {
            throw new IOException("非法文件类型，不允许上传。");
        }
    }

    protected String getUploadFileKey(String filename) {
        String file = CodecUtils.base64Encode(filename + "-" + System.currentTimeMillis());
        return file + "." + Files.getFileExtension(filename);
    }

    @Override
    public String getRealPath(String filename) throws NoSuchMethodException {
        throw new NoSuchMethodException();
    }

}

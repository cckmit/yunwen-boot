package li.fyun.commons.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {

    String FILE_PATH_PREFIX = "/file-upload";
    String FILE_SEPARATOR = "/";

    FileInfo put(MultipartFile file) throws IOException;

    MultipartFile get(String filename) throws IOException, NoSuchMethodException;

    void validate(MultipartFile file, String[] allowFileTypes) throws IOException;

    String getRealPath(String filename) throws IOException, NoSuchMethodException;

}

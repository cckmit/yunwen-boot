package li.fyun.commons.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfo {

    private String fileName;
    private long fileSize;
    private String contentType;
    private String filePath;

}

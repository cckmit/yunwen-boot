package li.fyun.commons.file.impl;

import li.fyun.commons.core.jpa.AbstractPersistable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Date;

@Entity
@Table(name = "sys_attachment")
@Getter
@Setter
@NoArgsConstructor
public class Attachment extends AbstractPersistable {

    static String abc = "";

    @NotBlank
    @Column(nullable = false)
    private String fileName;
    @Column(length = 100, nullable = false)
    private String contentType;
    private long fileSize;
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadTime;
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private byte[] content;

    public Attachment(MultipartFile file) throws IOException {
        fileName = file.getOriginalFilename();
        contentType = file.getContentType();
        fileSize = file.getSize();
        uploadTime = new Date();
        content = file.getBytes();
    }

}

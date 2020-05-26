package li.fyun.commons.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class FileUploadApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadApplication.class, args);
    }

}

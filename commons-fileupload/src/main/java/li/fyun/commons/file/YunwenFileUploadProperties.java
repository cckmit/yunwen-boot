package li.fyun.commons.file;

import li.fyun.commons.core.utils.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource(
        value = {"classpath:application.yml", "classpath:fileupload.yml"},
        ignoreResourceNotFound = true,
        factory = YamlPropertySourceFactory.class
)
@ConfigurationProperties(prefix = "app.fileupload")
public class YunwenFileUploadProperties {

    private String[] allowContentTypes;
    private String[] openWithBrowserContentTypes;
    private String webUploadPath;
    private boolean storePathReturnWithContext;
    private String storage;
    private String aliyunOssAccessKey;
    private String aliyunOssSecret;
    private String aliyunOssBucket;
    private String aliyunOssBucketDomain;
    private String aliyunOssRegionInternetEndpoint;
    private String aliyunOssRegionIntranetEndpoint;
    private boolean aliyunOssUseIntranetRegion;

}

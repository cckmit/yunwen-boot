package li.fyun.commons.file.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import li.fyun.commons.file.FileInfo;
import li.fyun.commons.file.YunwenFileUploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;

@Slf4j
public class AliyunOssStorage extends AbstractFileStorage {

    private OSS ossClient;
    private String bucketName;
    private String bucketDomain;

    @Autowired
    public AliyunOssStorage(@Autowired YunwenFileUploadProperties fileUploadProperties) {
        this.bucketName = fileUploadProperties.getAliyunOssBucket();
        this.bucketDomain = fileUploadProperties.getAliyunOssBucketDomain();
        String endpoint = fileUploadProperties.getAliyunOssRegionInternetEndpoint();
        if (fileUploadProperties.isAliyunOssUseIntranetRegion()) {
            endpoint = fileUploadProperties.getAliyunOssRegionIntranetEndpoint();
        }
        this.ossClient = new OSSClientBuilder().build(
                endpoint,
                fileUploadProperties.getAliyunOssAccessKey(),
                fileUploadProperties.getAliyunOssSecret());
        if (!ossClient.doesBucketExist(bucketName)) {
            ossClient.createBucket(bucketName);
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            ossClient.createBucket(createBucketRequest);
        }
    }

    @Override
    public FileInfo put(MultipartFile file) throws IOException {
        try (InputStream content = file.getInputStream()) {
            String dateKey = DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
            String key = dateKey + this.getUploadFileKey(file.getOriginalFilename());
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getSize());
            ossClient.putObject(new PutObjectRequest(bucketName, key, content, meta));
            log.debug("上传文件 {} 到阿里云存储完成，存储后文件名 {}", file.getName(), key);
            return new FileInfo(
                    key, file.getSize(), file.getContentType(),
                    this.bucketDomain + key
            );
        }
    }

    @Override
    public MultipartFile get(String filename) throws IOException, NoSuchMethodException {
        throw new NoSuchMethodException();
    }

}

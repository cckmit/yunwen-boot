package li.fyun.commons.file;

import com.google.common.base.Charsets;
import li.fyun.commons.core.controller.BaseController;
import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CachePolicy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileUploadController extends BaseController {

    @Resource
    private FileStorage fileStorage;
    @Resource
    private YunwenFileUploadProperties fileUploadProperties;

    @PostMapping(value = FileStorage.FILE_PATH_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
    public FileInfo upload(@RequestParam("file") MultipartFile file, ServletRequest request) throws IOException {
        fileStorage.validate(file, fileUploadProperties.getAllowContentTypes());
        FileInfo fileInfo = fileStorage.put(file);
        if (fileUploadProperties.isStorePathReturnWithContext()) {
            fileInfo.setFilePath(request.getServletContext().getContextPath() + fileInfo.getFilePath());
        }
        return fileInfo;
    }

    @CacheControl(maxAge = 60 * 60 * 24, policy = {CachePolicy.PUBLIC})
    @GetMapping(value = FileStorage.FILE_PATH_PREFIX + "/{filename}")
    public ResponseEntity<byte[]> get(@PathVariable String filename, HttpServletRequest request)
            throws IOException, NoSuchMethodException {
        MultipartFile file = fileStorage.get(filename);
        String contentType = file.getContentType();
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(file.getSize());

        if (!ArrayUtils.contains(fileUploadProperties.getOpenWithBrowserContentTypes(),
                StringUtils.substringBefore(contentType, "/"))) {
            String suggestFilename = suggestFilename(request, file.getName());
            bodyBuilder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + suggestFilename)
                    .header("X-Suggested-Filename", suggestFilename);
        }
        return bodyBuilder.body(file.getBytes());
    }

    protected String suggestFilename(HttpServletRequest request, String fileName) {
        try {
            String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
            if (StringUtils.contains(userAgent, "MSIE")
                    || StringUtils.contains(userAgent, "Trident")
                    || StringUtils.contains(userAgent, "Edge")) {//IE 浏览器
                return URLEncoder.encode(fileName, Charsets.UTF_8.name());
            } else {
                return new String(fileName.getBytes(Charsets.UTF_8.name()), Charsets.ISO_8859_1);
            }
        } catch (UnsupportedEncodingException ex) {
            return fileName;
        }
    }

    @CacheControl(maxAge = 60 * 60 * 24, policy = {CachePolicy.PUBLIC})
    @GetMapping(value = "/real-path/{filename}/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getRealPath(@PathVariable String filename, ServletRequest request)
            throws IOException, NoSuchMethodException {
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        String real = fileStorage.getRealPath(FileStorage.FILE_SEPARATOR + arguments);
        return new HashMap<String, String>() {{
            put("filename", StringUtils.substringAfter(path, "/real-path"));
            put("realPath", real);
        }};
    }

}

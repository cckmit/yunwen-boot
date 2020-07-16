package li.fyun.commons.jwt;

import li.fyun.commons.core.utils.ResponseWrapper;
import li.fyun.commons.security.service.CaptchaService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImageCaptchaFilter extends OncePerRequestFilter {

    @Resource
    private CaptchaService captchaService;
    @Resource
    private JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String captchaKey = request.getParameter(CaptchaService.REQ_PARAM_CAPTCHA_KEY);
        String captchaValue = request.getParameter(CaptchaService.REQ_PARAM_CAPTCHA_VALUE);
        try {
            captchaService.validateImageCaptcha(captchaKey, captchaValue);
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            ResponseWrapper.error(ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI())
                    .print(response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());
        return !ArrayUtils.contains(jwtProperties.getNeedImageCaptchaUrls(), uri);
    }

}

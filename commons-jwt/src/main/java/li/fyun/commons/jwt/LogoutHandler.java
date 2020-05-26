package li.fyun.commons.jwt;

import com.google.common.base.Charsets;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        String json = "{}";
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=" + Charsets.UTF_8.name());
        response.setContentLength(json.getBytes().length);
        ServletOutputStream out = response.getOutputStream();
        out.write(json.getBytes());
        out.flush();
        out.close();
    }

}

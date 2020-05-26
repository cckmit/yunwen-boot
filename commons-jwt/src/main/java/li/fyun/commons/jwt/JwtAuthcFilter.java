package li.fyun.commons.jwt;

import li.fyun.commons.core.utils.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthcFilter extends OncePerRequestFilter {

    @Autowired
    private TokenAuthcService tokenAuthcService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        try {
            Authentication authentication = tokenAuthcService.getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ae) {
            printError(request, response, ae);
        } catch (ServletException se) {
            Throwable ex = se.getRootCause();
            printError(request, response, ex);
        }
    }

    private void printError(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws IOException {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof AuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        }
        ErrorResponse errorResponse = new ErrorResponse(ex, httpStatus.value(), request.getRequestURI());
        errorResponse.print(response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }
}

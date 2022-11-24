package chatApp.filter;

import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static chatApp.Utilities.Utility.*;

@Component
public class AuthFilter extends GenericFilterBean {

    @Autowired
    AuthService authService;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            String auth = req.getParameter("token");
            String path = req.getRequestURI();
            if (permissionPathsForAll.stream().noneMatch(path::contains)) {
                if(auth.equals("null")){
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authorization header needed");
                    return;
                }
                else if(!authService.getKeyTokensValEmails().containsKey(auth)){
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                    return;
                }
                else{
                    String userEmail = authService.getKeyTokensValEmails().get(auth);
                    if(!auth.equals(authService.getKeyEmailsValTokens().get(userEmail))){
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                        return;
                    }
                }
            }
            chain.doFilter(request, response);
    }
}
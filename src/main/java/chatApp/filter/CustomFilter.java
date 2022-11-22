package chatApp.filter;

import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CustomFilter extends GenericFilterBean {

    AuthService authService = AuthService.getInstance();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
            String auth = ((HttpServletRequest) request).getHeader("token");
            HttpServletResponse res = (HttpServletResponse) response;

//            if(auth == null || authService == null){
//                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authorization header needed");
//                return ;
//            }
//            else if(authService.getKeyTokensValEmails().isEmpty() || authService.getKeyEmailsValTokens().isEmpty()){
//                res.setStatus(404);
//                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
//                return ;
//            }
//            else if(authService.getKeyTokensValEmails().containsKey(auth)){
//                String userEmail = authService.getKeyTokensValEmails().get(auth);
//                if(!auth.equals(authService.getKeyEmailsValTokens().get(userEmail))){
//                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
//                    return ;
//                }
//            }

            chain.doFilter(request, response);
    }
}
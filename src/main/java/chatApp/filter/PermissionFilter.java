package chatApp.filter;

import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@Component
public class PermissionFilter extends GenericFilterBean {

    @Autowired
    AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String auth = req.getHeader("token");
        String path = req.getRequestURI();
        List<String> paths = new ArrayList<>();
        paths.add("/sign");
        paths.add("/ws");
        paths.add("/chat");
        if (paths.stream().noneMatch(path::contains)) {
            if (auth == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authorization header needed");
            } else if (!authService.getKeyTokensValEmails().containsKey(auth)) {
                String userEmail = authService.getKeyTokensValEmails().get(auth);
                if (!auth.equals(authService.getKeyEmailsValTokens().get(userEmail))) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                }
                User dbUser = userRepository.findByEmail(userEmail);
                if (dbUser.getType() == UserType.GUEST) {
                    if (!"/chat".equals(path)) {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}

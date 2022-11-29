package chatApp.filter;

import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:9000");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods",
                "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, PATCH, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");

        String auth = req.getParameter("token");
        String path = req.getRequestURI();
        if (permissionPathsForAll.stream().noneMatch(path::contains)) {
            if (auth == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authorization header needed");
                throw new IllegalAccessError("Not Authorized");
            }
            else if (!authService.getKeyTokensValEmails().containsKey(auth)) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                throw new IllegalAccessError("Not Authorized");
            }
            else{
                String userEmail = authService.getKeyTokensValEmails().get(auth);
                if (!auth.equals(authService.getKeyEmailsValTokens().get(userEmail))) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                    throw new IllegalAccessError("Not Authorized");
                }
                User dbUser = userRepository.findByEmail(userEmail);
                if (dbUser.getType() == UserType.GUEST) {
                    if (permissionPathsForGuest.stream().noneMatch(path::contains)) {
                        res.addHeader("SC_UNAUTHORIZED", "Provided Information is Invalid");
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Provided Information is Invalid");
                        throw new IllegalAccessError("Not Authorized");
                    }
                }
                if (dbUser.getType() == UserType.REGISTERED) {
                    if (noPermissionsPathsForRegistered.stream().anyMatch(path::contains)) {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not Authorized");
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

}

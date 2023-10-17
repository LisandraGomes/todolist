package br.com.lisandra.todolist.Filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.lisandra.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/task/")) {

            var authorization = request.getHeader("Authorization");
            var user_encode = authorization.substring("Basic".length()).trim();
            System.out.println("userenconde:"+user_encode);
            byte[] user_decode = Base64.getDecoder().decode(user_encode);
            System.out.println("userDecode:"+ user_decode);
            String[] authString = new String(user_decode).split(":");
            System.out.println(authString);

            String username = authString[0];
            String password = authString[1];

            var finduser = this.userRepository.findByUsername(username);
            if (finduser == null) {
                response.sendError(401);
            } else {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), finduser.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("idUser", finduser.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } 
        else 
        {
            filterChain.doFilter(request, response);
        }
    }

}

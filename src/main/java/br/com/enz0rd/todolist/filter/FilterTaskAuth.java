package br.com.enz0rd.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.enz0rd.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    public IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if(!servletPath.startsWith("/tasks/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Pega autenticação
        var auth = request.getHeader("Authorization");

        if(auth == null) {
            response.sendError(401);
            return;
        }

        System.out.println("Authorization: " + auth);
        var token = auth.substring("Basic".length()).trim();
        var decodedToken = new String(java.util.Base64.getDecoder().decode(token));
        String[] credentials = decodedToken.split(":");
        String username = credentials[0];
        String password = credentials[1];

        // Valida user
        var user = this.userRepository.findByUsername(username);
        if(user == null) {
            response.sendError(401);
            return;
        }

        // Valida senha
        var result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray());
        if(!result.verified) {
            response.sendError(401);
            return;
        }

        // Segue
        request.setAttribute("userId", user.getId());
        filterChain.doFilter(request, response);
    }
}

package com.learnsecurityjwt.learnsecurityjwt.jwt;


import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwTokenUtil jwTokenUtil;

    //
    @Override //ham nay se xet dieu kien de duoc truy cap he thong
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            if (isBypassToken((request))){ // check token pass ?
                filterChain.doFilter(request,response);//enable bypass
                return;
            }
            //Khi client muốn request vào tài nguyên bảo vệ, nó sẽ gửi JWT trong header của HTTP request:
            final String auHeader=request.getHeader("Authorization"); // Lấy authorization (bearer…token)
            if(auHeader == null || !auHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
                return;
            }
            final String token = auHeader.substring(7); // lấy nguyên phần token ra
            final String email=jwTokenUtil.extractEmail(token); // lấy email (usename) từ token để sau xác thực
            if(email!=null && SecurityContextHolder.getContext().getAuthentication()== null){ // check xem đã được xác thực chưa ?
                UserEntity userDetails= (UserEntity) userDetailsService.loadUserByUsername(email); //lấy thông tin UserEntity (đã được implements từ UserDetail) dưới hệ thống thông qua loadUserByUsername(email), để xác thực với user trong token
                if(jwTokenUtil.validateToken(token, userDetails)){ //check email(usename) của user trong hệ thống và user trong token khớp, token còn hiệu lực ?
                    UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()); //Tạo Đối tượng UsernamePa...Token để xác thực người dùng với hệ thống Spring Security.
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Đối tượng này sẽ được đặt vào SecurityContext để Spring Security có thể biết rằng người dùng đã được xác thực và có các quyền gì.
                }
            }
            filterChain.doFilter(request,response);
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
        }
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request){

        final List<Pair<String,String>> bypassTokens= Arrays.asList(
                //Request khong can token
                Pair.of("/api/home","GET"),
                Pair.of("/user/login","POST"),
                Pair.of("/user/register","POST")
        );
        for(Pair<String,String> bypassToken: bypassTokens){ //check xem request có nằm trong ds các request pass token không
            if(request.getServletPath().contains((bypassToken.getFirst())) && request.getMethod().equals(bypassToken.getSecond())){
                return true;
            }
        }
        return false;
    }
}

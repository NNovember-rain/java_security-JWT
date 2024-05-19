package com.learnsecurityjwt.learnsecurityjwt.jwt;

import com.learnsecurityjwt.learnsecurityjwt.models.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwTokenUtil {
    @Value("${jwt.expiration}")
    private int expiredTime; // save to an enviroment variable

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(UserEntity userEntity) throws Exception{
        // đối tượng User được đưa vào sercurity thì có các properties gọi chung là các claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEntity.getEmail());
        // khi tạo token trả ve String có thể bị Exception
        try{
            String token= Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userEntity.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + expiredTime*1000L)) // thoi han
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256) // signWith là kí , nghĩa là khi sinh ra 1 token thì chúng ta sẽ có 1 câu hỏi bảo mật được gọi là secrectKey,
                    .compact();                                          // chính sau này dịch cái token để lấy các claims thông qua nó
            return token;
        }catch (Exception e){
            throw  new InvalidParameterException("Cannot generate jwt token,error:"+e.getMessage());
        }

    }

    private Key getSignInKey() {
        //chuyển đổi secretKey thành một mảng byte
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        //Dòng này nhận mảng byte và tạo ra một khóa HMAC SHA-256 từ nó.
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // extracAll claim
    private Claims extractAllclaims(String token ) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //  Lấy một claim từ extracAll claim
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolve) { // lấy claim tương ứng chỉ cần truyền nó vào claimsResolve
        final Claims claims = extractAllclaims(token);
        return claimsResolve.apply(claims);
    }

    // check expriation token
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token,Claims::getExpiration);
        return expiration.before(new Date());
    }

    // lay thong tin email của user tu claim  thong qua token
    public String extractEmail(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    // check thong tin token tu request client co hop le voi thong tin duoi DB cua UserDetails ? va con hieu luc hay khong ?
    public boolean validateToken(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}



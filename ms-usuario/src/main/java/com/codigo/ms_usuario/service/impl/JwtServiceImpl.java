package com.codigo.ms_usuario.service.impl;

import com.codigo.ms_usuario.aggregates.constants.Constants;
import com.codigo.ms_usuario.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${key.signature}")
    public String keySignature;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(addClaim(userDetails))
                .claim("ROLE",userDetails.getAuthorities())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 120000))
                .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String setKeySignature(String keySignature) {
        this.keySignature = keySignature;
        return keySignature;
    }

    public Key getSingKey(){
        byte[] key = Decoders.BASE64.decode(keySignature);
        return Keys.hmacShaKeyFor(key);

    }

    private Claims extracAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSingKey()).build()
                .parseClaimsJwt(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResult){
        final Claims claims = extracAllClaims(token);
        return claimResult.apply(claims);
    }

    public boolean isTokenExpired(String token){
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

    private Map<String, Object> addClaim(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        claims.put(Constants.CLAVE_AccountNonLocked,userDetails.isAccountNonLocked());
        claims.put(Constants.CLAVE_AccountNonExpired,userDetails.isAccountNonExpired());
        claims.put(Constants.CLAVE_CredentialsNonExpired,userDetails.isCredentialsNonExpired());
        claims.put(Constants.CLAVE_Enabled,userDetails.isEnabled());
        return claims;
    }
}

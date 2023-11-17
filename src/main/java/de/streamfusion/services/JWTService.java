package de.streamfusion.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JWTService {
    public String extractUsername(String jwToken) {
        return "";
    }

    private Claims extractAllClaims(String jwToken) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey).build().parseClaimsJwt(jwToken).getBody();
    }
}

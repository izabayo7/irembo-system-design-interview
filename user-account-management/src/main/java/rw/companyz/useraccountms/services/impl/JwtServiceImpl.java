package rw.companyz.useraccountms.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rw.companyz.useraccountms.models.UserAccount;
import rw.companyz.useraccountms.models.enums.ELoginStatus;
import rw.companyz.useraccountms.repositories.IUserRepository;
import rw.companyz.useraccountms.security.dtos.CustomUserDTO;
import rw.companyz.useraccountms.services.IJwtService;
import rw.companyz.useraccountms.utils.AuthTokenWrapper;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements IJwtService {
    @Value("${token.security.key}")
    private String jwtSigningKey;

    @Value("${token.security.expirationInMs}")
    private int jwtExpirationInMs;

    private final IUserRepository userRepository;

    private final AuthTokenWrapper authTokenWrapper;


    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }

    @Override
    public UUID extractSessionId(String token) {
        Claims claims = extractAllClaims(token);
        String sessionId = claims.get("sessionId", String.class);
        if(sessionId != null && !sessionId.isBlank()){
            return UUID.fromString(sessionId);
        }
        return null;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        UserAccount userById = userRepository.findByEmailAddress(userDetails.getUsername()).get();

        CustomUserDTO customUserDTO = new CustomUserDTO(userById);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (GrantedAuthority role :userDetails.getAuthorities()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        UUID sessionId = UUID.randomUUID();

        String token = Jwts .builder() .setId(userById.getId()+"")
                .setSubject(userDetails.getUsername()+  "")
                .claim("authorities",grantedAuthorities)
                .claim("user", customUserDTO)
                .claim("sessionId", sessionId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

        saveUserTokenAndLogoutOtherDevices(userById, sessionId);
        return token;
    }

    private void saveUserTokenAndLogoutOtherDevices(UserAccount user, UUID sessionId){
        user.setSessionId(sessionId);
        user.setLoginStatus(ELoginStatus.ACTIVE);

        userRepository.save(user);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    @Override
    public CustomUserDTO extractLoggedInUser() {
        String token = authTokenWrapper.getAuthToken();
        Claims claims = extractAllClaims(token);
        ObjectMapper mapper = new ObjectMapper();

        CustomUserDTO myUser = mapper.convertValue(claims.get("user"), CustomUserDTO.class);

        if (myUser != null) {
            return myUser;
        }
        return null;
    }


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

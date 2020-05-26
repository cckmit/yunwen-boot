package li.fyun.commons.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Payload;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtClaimsHandler {

    private JwtProperties jwtProperties;
    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    public JwtClaimsHandler(@Autowired JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.algorithm = Algorithm.HMAC512(jwtProperties.getTokenSecretKey());
        this.jwtVerifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
    }

    @SuppressWarnings("unchecked")
    public String compactJwt(String name, Date expiration, Collection<GrantedAuthority> authorities,
                             Map<String, Object>... extraClaims) {
        List grants = Lists.newArrayList();
        CollectionUtils.addAll(authorities);

        JWTCreator.Builder jwt = JWT.create()
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(new Date())
                .withExpiresAt(expiration)
                .withSubject(name)
                .withClaim("authorities", grants);

        // 附加额外信息
        if (ArrayUtils.isNotEmpty(extraClaims)) {
            for (Map<String, Object> extraClaim : extraClaims) {
                if (MapUtils.isNotEmpty(extraClaim)) {
                    for (String key : extraClaim.keySet()) {
                        jwt.withClaim(key, extraClaim.get(key).toString());
                    }
                }
            }
        }
        return jwt.sign(algorithm);
    }

    public Payload getPayload(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        return jwtVerifier.verify(token);
    }

}

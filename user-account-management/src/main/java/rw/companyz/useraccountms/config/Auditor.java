package rw.companyz.useraccountms.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Log4j2
public class Auditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        try{
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
                return Optional.of("SYSTEM");
            }
            return Optional.of(authentication.getName());
        }catch (Exception e){
            log.error("Error getting current auditor", e);
        }
        return Optional.of("SYSTEM");
    }
}

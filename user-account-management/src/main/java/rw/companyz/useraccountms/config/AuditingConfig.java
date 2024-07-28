package rw.companyz.useraccountms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditor")
public class AuditingConfig {
    @Bean
    public AuditorAware<String> auditor() {
        return new Auditor();
    }
}

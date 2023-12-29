package com.fastcampus.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;


@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean // 어디팅 할 때 사람 이름을 잡아주기 위한
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("solhe"); //TODO: 스프링 시큐리티 인증 기능 구현 이후 수정 필요 (아이디? 닉네임? 뭐가 되었던.)
    }
}

package com.example.data;

import com.example.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Устанавливаем вьюху логина
        setLoginView(http, LoginView.class);

        // Базовая настройка Vaadin
        super.configure(http);

        // Редирект после успешного входа
        http.formLogin(form -> form.defaultSuccessUrl("/", true));

        // Игнорируем CSRF для H2 и регистрации (на всякий случай)
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .ignoringRequestMatchers(new AntPathRequestMatcher("/registration"))
        );

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ВОТ ЭТО ГЛАВНОЕ. Это вырезает регистрацию из-под фильтров охраны совсем.
        web.ignoring().requestMatchers(
                new AntPathRequestMatcher("/registration"),
                new AntPathRequestMatcher("/line-awesome/**"),
                new AntPathRequestMatcher("/h2-console/**")
        );
        super.configure(web);
    }
}
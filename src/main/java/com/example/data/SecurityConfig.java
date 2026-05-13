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
        // Kirjautumisnäkymän asentaminen
        setLoginView(http, LoginView.class);

        // Vaadin-Perusasetukset
        super.configure(http);

        // Uudelleenohjaus onnistuneen kirjautumisen jälkeen
        http.formLogin(form -> form.defaultSuccessUrl("/", true));

        // CSRF: n huomiotta jättäminen H2: lle ja rekisteröinnille (kaiken varalta)
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .ignoringRequestMatchers(new AntPathRequestMatcher("/registration"))
        );

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //SE ON PÄÄASIA. Tämä leikkaa rekisteröinnin pois suojaussuodattimien alta kokonaan.
        web.ignoring().requestMatchers(
                new AntPathRequestMatcher("/registration"),
                new AntPathRequestMatcher("/line-awesome/**"),
                new AntPathRequestMatcher("/h2-console/**")
        );
        super.configure(web);
    }
}
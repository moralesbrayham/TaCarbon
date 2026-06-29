package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.SwingUtilities;

@Configuration
@SpringBootApplication
@ComponentScan(basePackages = "org.example")
@EnableJpaRepositories(basePackages = "org.example.repository")
@EntityScan(basePackages = "org.example.model")
public class App {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false"); // ✅ permite ventanas Swing

        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);

        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = context.getBean(LoginForm.class);
            loginForm.setVisible(true);
        });
    }
}

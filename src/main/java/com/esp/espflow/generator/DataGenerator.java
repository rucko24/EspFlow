package com.esp.espflow.generator;

import com.esp.espflow.entity.User;
import com.esp.espflow.dto.WizardEspDto;
import com.esp.espflow.enums.Role;
import com.esp.espflow.service.respository.UserRepository;
import com.esp.espflow.service.respository.impl.WizardEspService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_AVATAR_USER;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_FLASH_ESP_VIEW;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;

/**
 * initial configuration
 */
@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadSettings(final WizardEspService service) {
        return args -> {

            var wizardReadFlashView = WizardEspDto.builder()
                    .id(1L)
                    .name(WIZARD_READ_FLASH_ESP_VIEW)
                    .isWizardEnabled(true)
                    .build();

            var wizardFlashView = WizardEspDto.builder()
                    .id(2L)
                    .name(WIZARD_FLASH_ESP_VIEW)
                    .isWizardEnabled(true)
                    .build();

            service.saveAll(List.of(wizardReadFlashView, wizardFlashView));

        };
    }


    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository,
                                      @Value("${login.access-name}") String espflowUser, @Value("${login.access-password}") String password) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            User user = new User();
            user.setName("invitado");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode(password));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName(espflowUser);
            admin.setUsername("espflow");
            admin.setHashedPassword(passwordEncoder.encode(password));
            admin.setProfilePictureUrl(FRONTEND_IMAGES_AVATAR_USER + "esp01s.jpeg");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);

        };
    }

}
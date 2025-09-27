package com.esp.espflow.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.blockhound.BlockHound;

/**
 * @author rubn
 */
@Configuration
@ConditionalOnProperty(name = "blockhound.enable", havingValue = "true")
public class BlockHoundConfiguration {

    @Bean
    public String enableBlockHound() {
        BlockHound.install();
        return "BlockHound is enabled";
    }

}

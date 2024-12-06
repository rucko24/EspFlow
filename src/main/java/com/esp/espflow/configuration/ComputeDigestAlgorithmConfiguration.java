package com.esp.espflow.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author rubn
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "compute")
public class ComputeDigestAlgorithmConfiguration {

    private String digestAlgorithm;

}

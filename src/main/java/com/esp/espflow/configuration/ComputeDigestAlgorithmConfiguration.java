package com.esp.espflow.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author rubn
 */
@Configuration
@ConfigurationProperties(prefix = "compute")
public class ComputeDigestAlgorithmConfiguration {

    private String digestAlgorithm;

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }
}

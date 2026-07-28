package com.gitinbits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Git in Bits — Proof of Concept Backend
 *
 * <p>Authenticates users via GitHub OAuth2 and exposes GitHub repository
 * metadata through clean REST APIs. This PoC validates whether GitHub's
 * public API exposes sufficient data for the future Git in Bits platform.
 *
 * <p>Architecture is intentionally modular to support future layers:
 * Normalization → Analysis → AI → Visualization.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class GitInBitsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitInBitsApplication.class, args);
    }
}

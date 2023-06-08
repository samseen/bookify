package com.samseen.bookify.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@Slf4j
public class EnvironmentService {

    public Environment environment;

    public EnvironmentService(Environment environment) {
        this.environment = environment;
    }

    public boolean isProduction() {
        return Stream.of(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("production"));
    }

    public boolean isDev() {
        return Stream.of(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("dev") ||
                        profile.equalsIgnoreCase("development"));
    }

    public boolean isLocal() {
        return Stream.of(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("local") ||
                        profile.equalsIgnoreCase("localdev"));
    }

    public boolean isLocalOrDev() {
        return isLocal() || isDev();
    }
}

package com.save.link.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AzureHttpConfig {

    @Bean
    public RestClient azureRestClient(
            RestClient.Builder builder,
            @Value("${azure.external-id.subdomain}") String subdomain
    ) {
        String baseUrl = "https://" + subdomain
                + ".ciamlogin.com/"
                + subdomain + ".onmicrosoft.com";

        return builder
                .baseUrl(baseUrl)
                .build();
    }
}

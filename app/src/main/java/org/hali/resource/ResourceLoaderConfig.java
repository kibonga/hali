package org.hali.resource;

import org.hali.App;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceLoaderConfig {

    @Bean
    public ResourceLoader classpathResourceLoader() {
        return new ClasspathResourceLoader(App.class);
    }
}

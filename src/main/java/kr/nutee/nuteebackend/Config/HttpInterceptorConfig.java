package kr.nutee.nuteebackend.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class HttpInterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private HttpInterceptor httpInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(httpInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/public/**");
    }
}

package org.jieunsys;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.*;
 
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebsocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
 
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getHandler(), "/wsinit");
    }
 
    @Bean
    public WebSocketHandler getHandler() {
        return new HelloWebSocketHandler();
    }
 
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}

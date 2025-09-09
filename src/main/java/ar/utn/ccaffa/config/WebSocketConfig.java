package ar.utn.ccaffa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Habilita un broker en memoria para enviar mensajes a los clientes en destinos que comiencen con "/topic"
        config.enableSimpleBroker("/topic");
        // Define el prefijo para los mensajes que se dirigen desde los clientes al servidor (ej. @MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Registra el endpoint "/ws" para que los clientes se conecten.
        // withSockJS() proporciona un fallback para navegadores que no soportan WebSockets.
        registry.addEndpoint("/ws", "/api/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}

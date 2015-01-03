package websocket;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import websocket.afrs.AfrsEndpoint;

public class AfrsConfig implements ServerApplicationConfig {

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(
            Set<Class<? extends Endpoint>> scanned) {

        Set<ServerEndpointConfig> result = new HashSet<>();

        if (scanned.contains(AfrsEndpoint.class)) {
            result.add(ServerEndpointConfig.Builder.create(
                    AfrsEndpoint.class,
                    "/websocket/afrsProgrammatic").build());
        }

        return result;
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        Set<Class<?>> results = new HashSet<>();
        for (Class<?> clazz : scanned) {
            if (clazz.getPackage().getName().startsWith("websocket.")) {
                results.add(clazz);
            }
        }
        return results;
    }
}
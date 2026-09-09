package ru.deckbridge;

import com.mojang.logging.LogUtils;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import ru.deckbridge.compat.ControllingCompat;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Mod(DeckBridgeInput.MOD_ID)
@Mod.EventBusSubscriber(
        modid = DeckBridgeInput.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class DeckBridgeInput {

    public static final String MOD_ID = "deckbridge";

    private static final Logger LOGGER = LogUtils.getLogger();

    private HttpServer httpServer;

    public DeckBridgeInput() {
        LOGGER.info("=================================");
        LOGGER.info("DeckBridge Input Loaded");
        LOGGER.info("=================================");

        startHttpServer();
        registerOptionalCompat();
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        D6KeyMappings.register(event);
        LOGGER.info("DeckBridge D6 virtual key mappings registered");
    }

    private void registerOptionalCompat() {
        if (ModList.get().isLoaded("controlling")) {
            ControllingCompat.register();
        }
    }

    private void startHttpServer() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 4567), 0);

            httpServer.createContext("/d6/press", exchange -> {
                String keyId = extractKeyId(exchange.getRequestURI().getPath());

                Minecraft.getInstance().execute(() -> {
                    try {
                        int id = Integer.parseInt(keyId);
                        D6VirtualKeyManager.press(id);
                    } catch (NumberFormatException e) {
                        LOGGER.warn("Invalid D6 key id: {}", keyId);
                    }
                });

                sendTextResponse(exchange, "OK D6_BUTTON_" + keyId + " pressed");
            });

            httpServer.createContext("/d6/release", exchange -> {
                String keyId = extractKeyId(exchange.getRequestURI().getPath());

                Minecraft.getInstance().execute(() -> {
                    try {
                        int id = Integer.parseInt(keyId);
                        D6VirtualKeyManager.release(id);
                    } catch (NumberFormatException e) {
                        LOGGER.warn("Invalid D6 key id: {}", keyId);
                    }
                });

                sendTextResponse(exchange, "OK D6_BUTTON_" + keyId + " released");
            });

            httpServer.createContext("/d6/health", exchange -> {
                sendTextResponse(exchange, "DeckBridge OK");
            });

            httpServer.start();

            LOGGER.info("DeckBridge HTTP server started on http://127.0.0.1:4567");
        } catch (Exception e) {
            LOGGER.error("Failed to start DeckBridge HTTP server", e);
        }
    }

    private String extractKeyId(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 4) {
            return parts[3];
        }

        return "unknown";
    }

    private void sendTextResponse(com.sun.net.httpserver.HttpExchange exchange, String response) {
        try {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");

            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to send HTTP response", e);
        }
    }
}
package com.github.codecentric;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AstraClientTest {

  private final Gson mapper = new Gson();

  @Container
  @SuppressWarnings("rawtypes")
  public GenericContainer stargate =
      new GenericContainer(DockerImageName.parse("stargateio/stargate-4_0:v1.0.25"))
          .withExposedPorts(8081, 8082)
          .withEnv("CLUSTER_NAME", "stargate")
          .withEnv("CLUSTER_VERSION", "4.0")
          .withEnv("DEVELOPER_MODE", "true")
          .withEnv("SIMPLE_SNITCH", "true")
          .withEnv("ENABLE_AUTH", "false");

  private AstraClient createAstraClient() {
    return new AstraClient(
        URI.create(String.format("http://%s:%s", stargate.getContainerIpAddress(), stargate.getMappedPort(8082))),
        getAuthToken(),
        "serverless_astra_graalvm");
  }

  @Test
  public void shouldPersistAndRetrieveOrder() throws IOException {
    AstraClient astraClient = createAstraClient();
    astraClient.ensureNamespaceExists();

    Order order = new Order();
    order.setProductName("Goggly Eyes");
    order.setProductQuantity(27);
    order.setProductPrice(99);

    Order savedOrder = astraClient.saveOrder(order);
    Optional<Order> result = astraClient.getOrder(savedOrder.getOrderId());

    assertTrue(result.isPresent());
    assertEquals(order, result.get());
  }

  public String getAuthToken() {
    try {
      Response response =
          Request.post(String.format("http://%s:%s/v1/auth", stargate.getContainerIpAddress(), stargate.getMappedPort(8081)))
              .body(
                  HttpEntities.create(
                      "{\"username\":\"cassandra\", \"password\":\"cassandra\"}",
                      ContentType.APPLICATION_JSON))
              .execute();
      Map<String, String> authResult =
          mapper.fromJson(
              response.returnContent().asString(UTF_8),
              new TypeToken<Map<String, String>>() {}.getType());
      return authResult.get("authToken");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

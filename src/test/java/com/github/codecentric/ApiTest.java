package com.github.codecentric;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
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
public class ApiTest {

  private static final Gson gson = new Gson();

  @Container
  @SuppressWarnings("rawtypes")
  public GenericContainer stargate =
      new GenericContainer(DockerImageName.parse("stargateio/stargate-4_0:v1.0.25"))
          .withExposedPorts(8081, 8082)
          .withEnv("CLUSTER_NAME", "stargate")
          .withEnv("CLUSTER_VERSION", "4.0")
          .withEnv("DEVELOPER_MODE", "true")
          .withEnv("SIMPLE_SNITCH", "true")
          .withEnv("ENABLE_AUTH", "true");

  private String loadAuthToken() throws IOException {
    Response response =
        Request.post(
                String.format(
                    "http://%s:%d/v1/auth", stargate.getHost(), stargate.getMappedPort(8081)))
            .body(
                HttpEntities.create(
                    "{\"username\":\"cassandra\", \"password\":\"cassandra\"}",
                    ContentType.APPLICATION_JSON))
            .execute();

    return (String) gson.fromJson(response.returnContent().asString(), Map.class).get("authToken");
  }

  @Test
  public void shouldLoadAuthTokenFromStargate() throws IOException {
    String authToken = loadAuthToken();

    assertFalse(authToken.isBlank());
  }
}

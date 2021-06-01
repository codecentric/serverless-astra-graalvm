package com.github.codecentric;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class ApiTest {

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

  @Test
  public void testCassandra() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder(
                URI.create(
                    String.format(
                        "http://%s:%d", stargate.getHost(), stargate.getMappedPort(8082))))
            .header("accept", "application/json")
            .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    System.out.println(response.body());
  }
}

package com.github.codecentric;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class ApiTest {
  private final Network cassandraNetwork = Network.newNetwork();

  @Container
  @SuppressWarnings("rawtypes")
  public GenericContainer cassandra =
      new GenericContainer("cassandra:4.0")
          .withEnv("CASSANDRA_CLUSTER_NAME", "stargate")
          .withNetwork(cassandraNetwork)
          .withNetworkAliases("cassandra");

  @Container
  @SuppressWarnings("rawtypes")
  public GenericContainer stargate =
      new GenericContainer(DockerImageName.parse("stargateio/stargate-4_0:v1.0.25"))
          .dependsOn(cassandra)
          .withExposedPorts(8081, 8082)
          .withEnv("CLUSTER_NAME", "stargate")
          .withEnv("CLUSTER_VERSION", "4.0")
          .withEnv("SEED", "cassandra")
          .withEnv("SIMPLE_SNITCH", "true")
          .withEnv("ENABLE_AUTH", "true")
          .withNetwork(cassandraNetwork)
          .withNetworkAliases("stargate");

  @Test
  @Disabled(
      "Stargate fails to start with error: CassandraRoleManager.java:369 - Setup task failed with error, rescheduling")
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
    assertThat(response.body(), is("It's alive"));
  }
}

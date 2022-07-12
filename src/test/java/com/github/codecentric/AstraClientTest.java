package com.github.codecentric;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class AstraClientTest {

  @Container
  @SuppressWarnings("rawtypes")
  GenericContainer stargate =
      new GenericContainer(DockerImageName.parse("stargateio/stargate-4_0:v1.0.25"))
          .withExposedPorts(8081, 8082)
          .withEnv("CLUSTER_NAME", "stargate")
          .withEnv("CLUSTER_VERSION", "4.0")
          .withEnv("DEVELOPER_MODE", "true")
          .withEnv("SIMPLE_SNITCH", "true");

  @RegisterExtension
  AstraTestExtension testExtension = new AstraTestExtension(stargate,
      "serverless_astra_graalvm");

  @Test
  public void shouldPersistAndRetrieveOrder() throws IOException {
    AstraClient astraClient = testExtension.getClient();

    Order order = new Order();
    order.setProductName("Goggly Eyes");
    order.setProductQuantity(27);
    order.setProductPrice(99);

    Order savedOrder = astraClient.saveOrder(order);
    Optional<Order> result = astraClient.getOrder(savedOrder.getOrderId());

    assertThat(result).hasValue(order);
  }
}

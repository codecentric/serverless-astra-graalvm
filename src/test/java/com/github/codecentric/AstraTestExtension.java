package com.github.codecentric;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.HttpEntities;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

@SuppressWarnings("rawtypes")
public class AstraTestExtension implements BeforeEachCallback {

  private final Gson mapper = new Gson();
  private final GenericContainer stargate;
  private final String namespace;
  private volatile AstraClient client;
  private volatile URI astraUri;
  private volatile String authToken;

  public AstraTestExtension(GenericContainer stargate, String namespace) {
    this.stargate = stargate;
    this.namespace = namespace;
  }

  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    astraUri = URI.create(String.format("http://%s:%s",
        stargate.getContainerIpAddress(), stargate.getMappedPort(8082)));
    authToken = generateAuthToken();
    client = new AstraClient(astraUri, authToken, namespace);
    ensureNamespaceExists();
  }

  public void ensureNamespaceExists() throws IOException {
    Request.post(String.format("%s/v2/schemas/namespaces", astraUri))
        .body(HttpEntities.create(
            String.format("{\"name\":\"%s\"}", namespace),
            ContentType.APPLICATION_JSON))
        .addHeader("X-Cassandra-Token", authToken)
        .execute();
  }

  public AstraClient getClient() {
    return client;
  }

  public String generateAuthToken() {
    try {
      Response response =
          Request.post(String.format("http://%s:%s/v1/auth",
                  stargate.getContainerIpAddress(), stargate.getMappedPort(8081)))
              .body(HttpEntities.create(
                  "{\"username\":\"cassandra\", \"password\":\"cassandra\"}",
                  ContentType.APPLICATION_JSON))
              .execute();
      Map<String, String> authResult =
          mapper.fromJson(
              response.returnContent().asString(UTF_8),
              new TypeToken<Map<String, String>>() {
              }.getType());
      return authResult.get("authToken");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

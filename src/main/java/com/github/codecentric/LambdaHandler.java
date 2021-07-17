package com.github.codecentric;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, LambdaResponse> {

  private static final ObjectMapper mapper = new ObjectMapper();

  public LambdaHandler() {
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  }

  @Override
  public LambdaResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
    String astraUrl = System.getenv("ASTRA_URL");
    String astraToken = System.getenv("ASTRA_TOKEN");
    String astraNamespace = System.getenv("ASTRA_NAMESPACE");
    CassandraClient client = new CassandraClient(URI.create(astraUrl), astraToken, astraNamespace);

    if (astraUrl.isBlank()) System.out.println("Astra url is NOT set.");
    if (astraToken.isBlank()) System.out.println("Astra token is NOT set.");
    if (astraNamespace.isBlank()) System.out.println("Astra namespace is NOT set.");

    LambdaRequest request = null;
    try {
      byte[] decodedRequest;
      if (input.getIsBase64Encoded()) {
        decodedRequest = Base64.decodeBase64(input.getBody());
      } else {
        decodedRequest = input.getBody().getBytes(StandardCharsets.UTF_8);
      }

      request = mapper.readValue(decodedRequest, LambdaRequest.class);

      client.saveOrder(request.getOrder());
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not save input '"
              + input
              + "' as order '"
              + request.getOrder()
              + "' at "
              + astraUrl
              + " with namespace "
              + astraNamespace,
          e);
    }
    return new LambdaResponse();
  }
}

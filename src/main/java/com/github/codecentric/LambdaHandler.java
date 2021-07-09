package com.github.codecentric;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.io.IOException;
import java.net.URI;

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
    Order order = new Order();

    try {
      LambdaRequest request = mapper.readValue(input.getBody(), LambdaRequest.class);

      client.saveOrder(request.getOrder());
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not save input '"
              + input
              + "' as order '"
              + order
              + "' at "
              + astraUrl
              + " with namespace "
              + astraNamespace,
          e);
    }
    return new LambdaResponse();
  }
}

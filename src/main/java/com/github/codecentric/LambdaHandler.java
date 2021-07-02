package com.github.codecentric;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, String>, LambdaResponse> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public LambdaResponse handleRequest(Map<String, String> input, Context context) {
    String astraUrl = System.getenv("ASTRA_URL");
    String astraToken = System.getenv("ASTRA_TOKEN");
    String astraNamespace = System.getenv("ASTRA_NAMESPACE");
    CassandraClient client = new CassandraClient(URI.create(astraUrl), astraToken, astraNamespace);
    Order order = new Order();

    try {
      order.setProductName(input.get("product_name"));
      order.setProductPrice(Integer.parseInt(input.get("product_price")));
      order.setProductQuantity(Integer.parseInt(input.get("product_quantity")));

      client.saveOrder(order);
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

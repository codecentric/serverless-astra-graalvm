package com.github.codecentric;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaHandler implements RequestHandler<Object, Object> {

  @Override
  public Object handleRequest(Object input, Context context) {
    return "OK";
  }
}
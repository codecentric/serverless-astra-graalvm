output "aws_api_gateway_endpoint_url" {
  description = "URL of the AWS API gateway endpoint"
  value       = aws_apigatewayv2_api.api-gateway.api_endpoint
}

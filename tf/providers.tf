provider "astra" {
  token = var.astra_client_token
}

provider "aws" {
  region = "eu-west-1"
  profile = var.aws_profile
}
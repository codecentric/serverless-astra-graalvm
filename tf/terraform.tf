terraform {
  backend "remote" {
    organization = "frosner"

    workspaces {
      name = "serverless-astra-graalvm"
    }
  }

  required_providers {
    astra = {
      source = "datastax/astra"
      version = "0.0.3-pre"
    }
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
  }

}


data "external" "git-rev-parse" {
  program = [
    "${path.module}/git-rev-parse.sh"
  ]
}

locals {
  git-short-sha = data.external.git-rev-parse.result.sha
}
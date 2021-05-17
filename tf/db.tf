resource "astra_database" "main" {
  name           = "serverless-astra-graalvm"
  keyspace       = "main"
  cloud_provider = "gcp"
  region         = "europe-west1"
}

data "astra_secure_connect_bundle_url" "main" {
  database_id = astra_database.main.id
}
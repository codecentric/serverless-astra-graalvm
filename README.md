# Serverless Astra GraalVM

## Deployment

### Prerequisites

- [gotf](https://github.com/craftypath/gotf)

### Initialization

```bash
gotf -m tf login
echo 'astra_client_token = "AstraCS:ZZZ' > tf/secrets.auto.tfvars
echo 'astra_db_client_id = "XXX"' >> tf/secrets.auto.tfvars
echo 'astra_db_client_secret = "YYY"' >> tf/secrets.auto.tfvars
echo 'astra_db_client_token = "AstraCS:ZZZ"' >> tf/secrets.auto.tfvars
gotf -m tf init
```

### Apply

```bash
gotf -m tf apply
```

### Destroy

```bash
gotf -m tf destroy
```
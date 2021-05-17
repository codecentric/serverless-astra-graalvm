# Serverless Astra GraalVM

## Deployment

### Prerequisites

- [gotf](https://github.com/craftypath/gotf)

### Initialization

```bash
gotf -m tf login
echo 'astra_client_token = "AstraCS:xxxxxxxx' > tf/secrets.auto.tfvars
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
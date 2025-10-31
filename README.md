# Serviço de Compra de Passagens Aéreas Tolerante a Falhas

Projeto da matéria de Tolerância a Falhas em Sistemas de Software (IMD A305) da UFRN.

## Especificação dos Containers

O sistema é composto por 4 microserviços em containers Docker:

- **imd-travel**: Serviço principal que executa a compra de passagens (porta 8080)
- **airline-hub**: Gerencia as operações relacionadas a voos (porta 8081)
- **exchange**: Serviço de câmbio para conversão de moedas (porta 8082)
- **fidelity**: Gerencia o programa de fidelidade/bônus (porta 8083)

## Como Rodar o Sistema

1. Certifique-se de ter o Docker e Docker Compose instalados
2. Na pasta raiz do projeto, execute:
```
docker compose up
```

Após subir o sistema, os serviços estarão disponíveis em:
- http://localhost:8080 → imd-travel
- http://localhost:8081 → Airlines Hub
- http://localhost:8082 → Exchange
- http://localhost:8083 → Fidelity


## Executando um microserviço isolado

1. Certifique-se de ter o Docker instalado
2. Entre na pasta do microserviço, e.g. no caso do microserviço airlines-hub
```
cd airlines-hub
docker build -t airlines-hub .
docker run -p 8081:8080 airlines-hub
```
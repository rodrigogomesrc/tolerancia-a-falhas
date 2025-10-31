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
> docker compose up
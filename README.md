# Moneyverse

<p align="left"> 
<!-- Languages & Backend --> 
<img src="https://img.shields.io/badge/Java_21-ED8B00?logo=openjdk&logoColor=white" /> 
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring&logoColor=white" /> 
<img src="https://img.shields.io/badge/Spring_Cloud-6DB33F?logo=spring&logoColor=white" /> 
<img src="https://img.shields.io/badge/gRPC-5A3E85?logo=grpc&logoColor=white" /> 

<!-- Frontend --> 
<img src="https://img.shields.io/badge/Angular_19-DD0031?logo=angular&logoColor=white" /> 
<img src="https://shields.io/badge/TypeScript-3178C6?logo=TypeScript&logoColor=FFF&style=flat-square" /> 
<img src="https://img.shields.io/badge/PrimeNG-003C8F?logo=primefaces&logoColor=white" /> 
<img src="https://img.shields.io/badge/TailwindCSS-06B6D4?logo=tailwindcss&logoColor=white" /> 

<!-- Infrastructure --> 
<img src="https://img.shields.io/badge/Kubernetes-326CE5?logo=kubernetes&logoColor=white" /> 
<img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white" /> 
<img src="https://img.shields.io/badge/Helm-0F1689?logo=helm&logoColor=white" /> 
<img src="https://img.shields.io/badge/Kind-3E5FB5?logo=kubernetes&logoColor=white" /> 

<!-- Messaging & Databases --> 
<img src="https://img.shields.io/badge/Apache_Kafka-231F20?logo=apachekafka&logoColor=white" /> 
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white" /> 
<img src="https://img.shields.io/badge/ClickHouse-FFCC01?logo=clickhouse&logoColor=black" /> 
<img src="https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white" /> 

<!-- Security --> 
<img src="https://img.shields.io/badge/Keycloak-2050C4?logo=keycloak&logoColor=white" /> 

<!-- DevOps --> 
<img src="https://img.shields.io/badge/GitHub_Actions-2088FF?logo=githubactions&logoColor=white" /> 
<img src="https://img.shields.io/badge/SonarQube-4E9BCD?logo=sonarqube&logoColor=white" /> 
<img src="https://img.shields.io/badge/Infisical-4F46E5?logo=infisical&logoColor=white" /> 
</p>

Moneyverse is a **full-stack, microservices-based personal finance platform** that allows users to manage accounts,
transactions, budgets, and analyze their financial health.

Built as a production-grade project, it showcases expertise across **distributed systems, event-driven architecture,
Kubernetes, DevOps, and modern Angular frontend development**.

## ✨ Features

* Track bank accounts, balances, and real-time updates (SSE)
* Manage incomes, expenses, transfers, and tagging
* Define and monitor budgets with category-based spending
* Multi-currency support
* Interactive analytics dashboards powered by ClickHouse
* Secure authentication and authorization via Keycloak
* Event-driven microservices with Kafka
* Kubernetes-native deployment using Helm

## 🚀 Architecture Overview

Moneyverse follows a distributed microservices architecture composed of:

* **User Management** — Handles user profiles and preferences and integrates with Keycloak for authentication and
  authorization.
* **Account Management** — Manages user accounts, balances, and real-time updates via SSE.
* **Transaction Management** — Core ledger service for incomes, expenses, transfers, tags, and recurring subscriptions.
* **Budget Management** — Enables creation and maintenance of budgets and spending categories.
* **Currency Management** — Stores and manages currency metadata and FX definitions.
* **Analytics** — High-performance analytics service powered by ClickHouse for dashboards and aggregated statistics.
* **API Gateway** — Built with [KrakenD](https://www.krakend.io/), acting as the single entry point for all client
  requests.

### Frontend (Angular)

A modern, responsive Single Page Application (SPA) built with:

* **Angular 19** & **TypeScript 5.7**
* **PrimeNG** & **TailwindCSS** for UI components and styling.
* **Apache ECharts** for rich data visualization.
* **Keycloak Angular** for secure OAuth2/OIDC authentication.

### Infrastructure & Data

* **Data Storage**: PostgreSQL (relational), ClickHouse (analytics), Redis (caching)
* **Messaging**: Apache Kafka for event-driven communication.
* **Identity Provider**: Keycloak.
* **Orchestration**: Kubernetes (with Helm charts).

## 🔧 Getting Started

### Prerequisites

* [**Java JDK 21**](https://adoptium.net/en-GB/temurin/releases)
* [**Node.js 24+**](https://nodejs.org/en)
* [**Docker**](https://www.docker.com/products/docker-desktop/)
* **Kubernetes** (e.g., [Kind](https://kind.sigs.k8s.io/))
* [**Helm**](https://helm.sh/)
* (Optional) [k9s](https://k9scli.io/)

After installing these tools, follow the setup steps below.

### 🏗️ Create the Kubernetes Cluster

*You only need to perform this setup once.*

1. Create a folder (optional)

```bash
   mkdir moneyverse && cd moneyverse
```

2. Download the Kind cluster configuration file `moneyverse-infrastructure/cluster/config.yaml`

3. Create the Kubernetes cluster using the configuration file:

```bash
   kind create cluster --config config.yaml
```

4. Create the namespace (you may choose another name):

```bash
   kubectl create namespace moneyverse
```

5. Install the [Infisical Secret Operator](https://infisical.com/docs/integrations/platforms/kubernetes/overview):

```bash
    helm repo add infisical-helm-charts 'https://dl.cloudsmith.io/public/infisical/helm-charts/helm/charts/'
    helm repo update
    
    helm install --generate-name infisical-helm-charts/secrets-operator -n moneyverse
```

### ▶️ Running Moneyverse Locally

From the same directory:

1. Pull and extract the Helm chart:

``` bash
    helm pull oci://registry-1.docker.io/lfrat/moneyverse-chart --version 0.1.0 --untar
```

2. Deploy Moneyverse

``` bash
    helm install moneyverse ./moneyverse-chart -f ./moneyverse-chart/environments/demo/values.yaml -n moneyverse
```

Deployment usually completes within 3–5 minutes. You can monitor the deployment in real-time using *k9s*.

Once ready, the application will be available at:
👉 http://localhost:30000/

If you want to stop the deployment, run `helm uninstall moneyverse -n moneyverse`.

## 🔑 Demo User Credentials
A demo user is already preconfigured in the system.
You can log in using:
* **Email**: `demo@demo.it`
* **Password**: `demo`

This account includes sample data, so you can explore the application's features immediately.

## 📘 What I Learned

This project allowed me to gain hands-on experience with:

* Designing **scalable microservices** in a real-world architecture
* Implementing **event-driven workflows** using Kafka
* Using **ClickHouse** for analytical workloads
* Managing **Kubernetes deployments** via Helm
* Building **CI/CD pipelines** with GitHub Actions
* Implementing **SSE** and **gRPC** for efficient service communication
* Developing a modern **Angular 19** SPA with modular architecture
* Secure authentication using **Keycloak & OAuth2/OIDC**
* Structuring a cloud-native app with **observability** and **maintainability** in mind

## 📄 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

## ✍️ Authors

- [@Lorenzo Fratini](https://www.github.com/lorenzofratini1998)
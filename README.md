

Projeto Integrador IV
Aplicativo de gerenciamento e organização de medicamentos para idosos

📱 Sobre o Projeto

O iCare é um aplicativo desenvolvido para auxiliar idosos e seus cuidadores no controle e organização de medicamentos.
Ele permite cadastrar remédios, horários, notificações e informações importantes, garantindo mais segurança e autonomia no tratamento.


Aplicativo mobile desenvolvido em Kotlin utilizando Jetpack Compose

Servidor em Java, responsável pela lógica de autenticação, controle de usuários e comunicação com o app

Servidor de banco de dados MongoDB, para armazenamento de perfis, prescrições e registros de medicamentos

👥 Integrantes

Flavio Augusto Dario de Moraes — RA [inserir RA]

Gabriel Henrique Pera Coelho — RA 24012508

Henrique Monteiro da Silva — RA [inserir RA]

Narayan Fonseca Jakowatz — RA 24018023

Pedro Tiezo Sales Shimizu — RA 24005158

🎯 Objetivo do Sistema

Criar um organizador digital de medicamentos para idosos, que seja:

Simples de usar

Acessível

Seguro

Integrado com notificações e lembretes

O app facilita o acompanhamento de tratamentos e evita esquecimentos, trazendo mais qualidade de vida e autonomia.

🏗️ Arquitetura do Projeto
1. Aplicativo Android (Kotlin + Jetpack Compose)

Interface moderna e responsiva usando Compose

Tela de cadastro e login

Cadastro de medicamentos

Lembretes e notificações via Android AlarmManager / WorkManager

Sincronização com servidor Java

Perfil do usuário

2. Servidor Java

API REST desenvolvida em Java

Controle de autenticação e autorização

Endpoints para CRUD de usuários e medicamentos

Comunicação com o MongoDB

3. Banco de Dados MongoDB

Armazena:

Perfis de usuários

Lista de medicamentos

Agendamentos

Histórico de uso

Estruturado para alto desempenho e flexibilidade

🚀 Tecnologias Utilizadas
Mobile

Kotlin

Jetpack Compose

Retrofit

Coroutines / Flow

Material Design 3

Backend

Java

Spring Boot (ou tecnologia equivalente escolhida pelo grupo)

JWT para autenticação

MongoDB Driver

Banco de Dados

MongoDB Community Server

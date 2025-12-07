**Projeto Integrador IV Aplicativo de gerenciamento e organização de medicamentos**


 **Sobre o Projeto**

O iCare é um aplicativo desenvolvido para auxiliar no controle e organização de medicamentos. Ele permite cadastrar remédios, horários e informações importantes, garantindo mais segurança e autonomia no tratamento.

Aplicativo mobile desenvolvido em Kotlin utilizando Jetpack Compose

Servidor em Java, responsável pela lógica de autenticação, controle de usuários e comunicação com o app

Banco de dados MongoDB, para armazenamento de cadastros.


 **Integrantes**

Flavio Augusto Dario de Moraes — RA 24008418

Gabriel Henrique Pera Coelho — RA 24012508

Henrique Monteiro da Silva — (procurado vivo ou morto)

Narayan Fonseca Jakowatz — RA 24018023

Pedro Tiezo Sales Shimizu — RA 24005158



**Objetivo do Sistema****

Criar um organizador digital de medicamentos, que seja:

Simples de usar

Acessível

Seguro

Integrado com notificações e lembretes (FUTURAMENTE)

O app facilita o acompanhamento de tratamentos, trazendo mais qualidade de vida e autonomia.



**Arquitetura do Projeto**

Aplicativo Android (Kotlin + Jetpack Compose)

Tela de cadastro e login

Cadastro de medicamentos

Botão SOS

Tela de Ajustes

Servidor Java que valida CPF, salva no Banco de Dados MongoDB verifica o cadastro e autentica o login.

Perfil do usuário

Servidor Java
API REST desenvolvida em Java

Controle de autenticação e autorização

Endpoints para CRUD de usuários e medicamentos

Comunicação com o MongoDB

Banco de Dados MongoDB
Armazena:

Login

Cadastro

Armazenado localmente:

Medicamentos 

Agendamentos 



**Tecnologias Utilizadas**

Kotlin

Jetpack Compose

Retrofit

Coroutines / Flow

Material Design 3

Backend - Java

WebSocket

MongoDB Driver

Banco de Dados

MongoDB Community Server


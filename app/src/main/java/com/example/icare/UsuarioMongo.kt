package com.example.icare

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

// Essa classe define o JSON que será enviado para o banco
data class UsuarioMongo(
    @BsonId
    val id: ObjectId = ObjectId(), // Gera ID automático
    val nome: String,
    val cpf: String,
    val dataNascimento: String,
    val email: String
)
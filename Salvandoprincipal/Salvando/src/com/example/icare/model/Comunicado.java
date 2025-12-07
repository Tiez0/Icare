package com.example.icare.model;

import java.io.*;
import java.io.Serializable;
// No cliente aparece "implements Serializable, Cloneable", no servidor apenas Serializable.
// Usar a versão mais completa (Cliente) é seguro para ambos.
public class Comunicado implements Serializable, Cloneable {
    static final long serialVersionUID = 1L;
}
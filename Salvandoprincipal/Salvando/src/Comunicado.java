import java.io.*;
// No cliente aparece "implements Serializable, Cloneable", no servidor apenas Serializable.
// Usar a versão mais completa (Cliente) é seguro para ambos.
public class Comunicado implements Serializable, Cloneable {}
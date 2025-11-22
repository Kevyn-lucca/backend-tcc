package factory;

import org.mindrot.jbcrypt.BCrypt;

public class HashFactory {

    public static String hashSenha(String senhaPura) {
        return BCrypt.hashpw(senhaPura, BCrypt.gensalt());
    }

    public static boolean verificarSenha(String senhaPura, String hashArmazenado) {
        return BCrypt.checkpw(senhaPura, hashArmazenado);
    }
}

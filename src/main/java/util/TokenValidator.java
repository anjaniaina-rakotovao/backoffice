package util;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import model.TokenModel;

public class TokenValidator {

    public static boolean isValid(HttpServletRequest request) {
        String token = request.getParameter("token");
        try {
            boolean ok = TokenModel.isValid(token);
            if (!ok) {
                request.setAttribute("errorMessage", "Token invalide ou expiré");
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur interne lors de la validation du token");
            return false;
        }
    }
}

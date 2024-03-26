package iut.dam.sae_dam.errors;

import android.content.Context;

import java.util.HashMap;

import iut.dam.sae_dam.R;

public class ErrorMessages {

    private static HashMap<Errors, Integer> errorMessages;

    static {
        errorMessages = new HashMap<>();
        errorMessages.put(Errors.EMPTY, R.string.errorEmptyField);
        errorMessages.put(Errors.INVALID_MAIL_FORMAT, R.string.errorInvalidMailFormat);
        errorMessages.put(Errors.INVALID_PASSWORD_FORMAT, R.string.errorInvalidPasswordFormat);
        errorMessages.put(Errors.INVALID_PASSWORD_CONFIRMATION, R.string.errorInvalidPasswordConfirmation);
        errorMessages.put(Errors.ACCOUNT_ALREADY_EXISTS, R.string.errorAccountAlreadyExists);
    }

    public static String getErrorMessage(Context context, Errors error) {
        int errorMessageResId = errorMessages.get(error);
        return context.getString(errorMessageResId);
    }
}

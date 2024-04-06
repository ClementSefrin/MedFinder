package iut.dam.sae_dam.errors;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import iut.dam.sae_dam.R;

public class ErrorManager {
    private static HashMap<Errors, Integer> errorMessages;

    static {
        errorMessages = new HashMap<>();
        errorMessages.put(Errors.EMPTY, 0);
        errorMessages.put(Errors.EMPTY_FIELD, R.string.errorEmptyField);
        errorMessages.put(Errors.INVALID_MAIL_FORMAT, R.string.errorInvalidMailFormat);
        errorMessages.put(Errors.INVALID_PASSWORD_FORMAT, R.string.errorInvalidPasswordFormat);
        errorMessages.put(Errors.INVALID_PASSWORD_CONFIRMATION, R.string.errorInvalidPasswordConfirmation);
        errorMessages.put(Errors.ACCOUNT_ALREADY_EXISTS, R.string.errorAccountAlreadyExists);
        errorMessages.put(Errors.INVALID_MAIL_PASSWORD, R.string.errorInvalidMailPassword);
        errorMessages.put(Errors.INVALID_QUESTION_ANSWER, R.string.errorInvalidQuestionAnswer);
        errorMessages.put(Errors.INVALID_NEW_PASSWORD, R.string.errorInvalidNewPassword);
        errorMessages.put(Errors.NO_ACCOUNT_FOUND, R.string.errorNoAccountFound);
        errorMessages.put(Errors.UNKNOWN_MEDICINE, R.string.errorUnknownMedicine);
        errorMessages.put(Errors.UNKNOWN_PHARMACY, R.string.errorUnknownPharmacy);
        errorMessages.put(Errors.UNKNOWN_CITY, R.string.errorUnknownCity);
        errorMessages.put(Errors.INVALID_OLD_PASSWORD, R.string.errorInvalidOldPassword);
        errorMessages.put(Errors.SAME_PASSWORD, R.string.errorSamePassword);
    }

    public static String getErrorMessage(Context context, Errors error) {
        int errorMessageResId = errorMessages.get(error);
        return errorMessageResId == 0 ? "" : context.getString(errorMessageResId);
    }

    public static void updateBorder(Context context, HashMap<View, Errors> errors, HashMap<View, TextView> errorMessagesViews) {
        for (View field : errorMessagesViews.keySet()) {
            if (errors.containsKey(field)) {
                field.setBackgroundResource(R.drawable.invalid_edittext_border);
                errorMessagesViews.get(field).setText(ErrorManager.getErrorMessage(context, errors.get(field)));
                errorMessagesViews.get(field).setVisibility(View.VISIBLE);
            } else {
                field.setBackgroundResource(R.drawable.valid_edittext_border);
                errorMessagesViews.get(field).setVisibility(View.GONE);
            }
        }
    }

    public static boolean checkEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean checkPasswordVerify(String mdp, String mdpVerify) {
        return mdpVerify.equals(mdp);
    }

    public static boolean checkPassword(String mdp) {
        String mdpPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(mdpPattern);
        Matcher matcher = pattern.matcher(mdp);
        return matcher.matches() && mdp.length() <= 30;
    }
}

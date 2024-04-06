package iut.dam.sae_dam.ui.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import iut.dam.sae_dam.MedFind;
import iut.dam.sae_dam.R;
import iut.dam.sae_dam.activities.ForgotPasswordActivity;
import iut.dam.sae_dam.data.DatabaseConnection;
import iut.dam.sae_dam.errors.ErrorManager;
import iut.dam.sae_dam.errors.Errors;

public class ChangePasswordDialog extends Dialog {
    private EditText oldPasswordET, newPasswordET, passwordVerifyEt, secretAnswerET;
    private Spinner secretQuestionSP;
    private TextView errorOldPasswordTV, errorNewPasswordTV, errorPasswordVerifyTV, errorSecretQuestion, errorSecretAnswerTV;
    private HashMap<View, TextView> errorMessagesViews;
    private HashMap<View, Errors> errors;
    private Button changeBTN, forgotPasswordBTN, cancelBTN;
    private ImageButton passwordVisibilityBTN;
    private Activity activity;
    private Intent intent;

    public ChangePasswordDialog(@NonNull Context context, Activity activity) {
        super(context);
        setContentView(R.layout.dialog_change_password);
        this.activity = activity;
        intent = activity.getIntent();
        errorMessagesViews = new HashMap<>();
        errors = new HashMap<>();
        getViews();

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        changeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errors = getErrors();
                if (errors.isEmpty()) {
                    new ChangePassword().execute();
                } else {
                    ErrorManager.updateBorder(activity, errors, errorMessagesViews);
                }
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        passwordVisibilityBTN.setOnClickListener(v -> {
            changePasswordVisibility();
        });

        oldPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MedFind.getMaxCharLimit()) {
                    oldPasswordET.setText(s.subSequence(0, MedFind.getMaxCharLimit()));
                    oldPasswordET.setSelection(MedFind.getMaxCharLimit());
                }
            }
        });

        newPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MedFind.getMaxCharLimit()) {
                    newPasswordET.setText(s.subSequence(0, MedFind.getMaxCharLimit()));
                    newPasswordET.setSelection(MedFind.getMaxCharLimit());
                }
            }
        });

        passwordVerifyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MedFind.getMaxCharLimit()) {
                    passwordVerifyEt.setText(s.subSequence(0, MedFind.getMaxCharLimit()));
                    passwordVerifyEt.setSelection(MedFind.getMaxCharLimit());
                }
            }
        });

        forgotPasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Intent intent = new Intent(activity, ForgotPasswordActivity.class);
                activity.startActivity(intent);
            }
        });
    }


    private class ChangePassword extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection connection = DatabaseConnection.getConnection();

                String query = "UPDATE user SET Password = ?, QuestionSecrete = ?, ReponseSecrete = ? WHERE Id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newPasswordET.getText().toString());
                int selectedPosition = secretQuestionSP.getSelectedItemPosition();
                preparedStatement.setInt(2, selectedPosition);
                preparedStatement.setString(3, secretAnswerET.getText().toString());
                preparedStatement.setInt(4, intent.getIntExtra("userId", 0));

                preparedStatement.executeUpdate();


                DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int userId = intent.getIntExtra("userId", 0);
            boolean admin = intent.getBooleanExtra("admin", false);
            int city = intent.getIntExtra("city", -1);

            intent.putExtra("password", newPasswordET.getText().toString());
            intent.putExtra("secretQuestion", intent.getIntExtra("secretQuestion", -1));
            intent.putExtra("secretAnswer", intent.getStringExtra("secretAnswer"));
            activity.setIntent(intent);
            dismiss();
        }
    }

    private HashMap<View, Errors> getErrors() {
        HashMap<View, Errors> errors = new HashMap<>();
        String oldPassword = intent.getStringExtra("password");
        if (!oldPassword.equals(oldPasswordET.getText().toString())) {
            errors.put(oldPasswordET, Errors.INVALID_OLD_PASSWORD);
        }
        if (!ErrorManager.checkPassword(newPasswordET.getText().toString())) {
            errors.put(newPasswordET, Errors.INVALID_PASSWORD_FORMAT);
            errors.put(passwordVerifyEt, Errors.EMPTY);
        } else if (oldPassword.equals(newPasswordET.getText().toString())) {
            errors.put(newPasswordET, Errors.SAME_PASSWORD);
            errors.put(passwordVerifyEt, Errors.EMPTY);
        } else if (!ErrorManager.checkPasswordVerify(newPasswordET.getText().toString(), passwordVerifyEt.getText().toString())) {
            errors.put(passwordVerifyEt, Errors.INVALID_PASSWORD_CONFIRMATION);
        }
        if (secretQuestionSP.getSelectedItemPosition() == 0) {
            errors.put(secretQuestionSP, Errors.EMPTY_FIELD);
        }
        if (secretAnswerET.getText().toString().isEmpty()) {
            errors.put(secretAnswerET, Errors.EMPTY_FIELD);
        }
        return errors;
    }

    private void getViews() {
        oldPasswordET = findViewById(R.id.changePassword_oldPasswordET);
        newPasswordET = findViewById(R.id.changePassword_newPasswordET);
        passwordVerifyEt = findViewById(R.id.changePassword_verifyPasswordET);
        secretQuestionSP = findViewById(R.id.changePassword_secretQuestionSP);
        secretQuestionSP.setSelection(intent.getIntExtra("secretQuestion", -1));
        secretAnswerET = findViewById(R.id.changePassword_secretAnswerET);
        secretAnswerET.setText(intent.getStringExtra("secretAnswer"));

        errorOldPasswordTV = findViewById(R.id.changePassword_errorOldPasswordTV);
        errorNewPasswordTV = findViewById(R.id.changePassword_errorNewPasswordTV);
        errorPasswordVerifyTV = findViewById(R.id.changePassword_errorVerifyPasswordTV);
        errorSecretQuestion = findViewById(R.id.changePassword_errorSecretQuestionTV);
        errorSecretAnswerTV = findViewById(R.id.changePassword_errorSecretAnswerTV);

        changeBTN = findViewById(R.id.changePassword_changeBTN);
        cancelBTN = findViewById(R.id.changePassword_cancelBTN);
        forgotPasswordBTN = findViewById(R.id.changePassword_forgotPasswordBTN);
        passwordVisibilityBTN = findViewById(R.id.changePassword_passwordVisibilityBTN);

        errorMessagesViews.put(oldPasswordET, errorOldPasswordTV);
        errorMessagesViews.put(newPasswordET, errorNewPasswordTV);
        errorMessagesViews.put(passwordVerifyEt, errorPasswordVerifyTV);
        errorMessagesViews.put(secretQuestionSP, errorSecretQuestion);
        errorMessagesViews.put(secretAnswerET, errorSecretAnswerTV);

        for (View view : errorMessagesViews.keySet()) {
            errorMessagesViews.get(view).setVisibility(View.GONE);
        }
    }

    private void changePasswordVisibility() {
        int oldPasswordSelectionStart = oldPasswordET.getSelectionStart();
        int oldPasswordSelectionEnd = oldPasswordET.getSelectionEnd();

        int newPasswordSelectionStart = newPasswordET.getSelectionStart();
        int newPasswordSelectionEnd = newPasswordET.getSelectionEnd();

        int passwordVerifySelectionStart = passwordVerifyEt.getSelectionStart();
        int passwordVerifySelectionEnd = passwordVerifyEt.getSelectionEnd();

        if (oldPasswordET.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            oldPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            newPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVerifyEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_hide_password);
        } else {
            oldPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            newPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVerifyEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisibilityBTN.setBackgroundResource(R.drawable.ic_show_password);
        }
        oldPasswordET.setSelection(oldPasswordSelectionStart, oldPasswordSelectionEnd);
        newPasswordET.setSelection(newPasswordSelectionStart, newPasswordSelectionEnd);
        passwordVerifyEt.setSelection(passwordVerifySelectionStart, passwordVerifySelectionEnd);
    }
}

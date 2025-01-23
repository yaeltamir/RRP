package com.example.recipereach;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class SigninFragment extends Fragment {

    private FirebaseAuth auth;

    private EditText emailEditText, passwordEditText, usernameEditText;
    private Button signInButton;
    private ProgressBar progressBar;

    public SigninFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        usernameEditText = view.findViewById(R.id.username);
        signInButton = view.findViewById(R.id.registerButton);
        progressBar = view.findViewById(R.id.progressBar);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        usernameEditText.addTextChangedListener(textWatcher);

        signInButton.setOnClickListener(v -> signInUser(view));

        return view;
    }

    private void validateFields() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isPasswordValid = password.length() >= 6;
        boolean isUsernameValid = !TextUtils.isEmpty(username);

        if (!isEmailValid) {
            emailEditText.setError("מייל לא תקין");
        }

        if (!isPasswordValid) {
            passwordEditText.setError("סיסמא חייבת להיות לפחות 6 תווים");
        }

        if (!isUsernameValid) {
            usernameEditText.setError("שם משתמש לא יכול להיות ריק");
        }

        signInButton.setEnabled(isEmailValid && isPasswordValid && isUsernameValid);
    }

    private void signInUser(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(R.id.action_SignInFragment_to_LoginFragment);
                    } else {
                        Toast.makeText(getActivity(), "הרישום נכשל: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}

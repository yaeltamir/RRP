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

    private FirebaseAuth auth; // Firebase authentication instance
    private EditText emailEditText, passwordEditText, usernameEditText; // Input fields
    private Button signInButton; // Sign-in button
    private ProgressBar progressBar; // Progress indicator

    public SigninFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance(); // Initialize Firebase authentication
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        // Initialize UI components
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        usernameEditText = view.findViewById(R.id.username);
        signInButton = view.findViewById(R.id.registerButton);
        progressBar = view.findViewById(R.id.progressBar);

        // TextWatcher to validate fields as user types
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields(); // Validate input fields on text change
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Attach TextWatcher to input fields
        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        usernameEditText.addTextChangedListener(textWatcher);

        // Set click listener for the sign-in button
        signInButton.setOnClickListener(v -> signInUser(view));

        return view;
    }

    /**
     * Validates user input fields and enables/disables the sign-in button accordingly.
     */
    private void validateFields() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        // Validate email format
        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        // Password should be at least 6 characters long
        boolean isPasswordValid = password.length() >= 6;
        // Username should not be empty
        boolean isUsernameValid = !TextUtils.isEmpty(username);

        // Show error messages if validation fails
        if (!isEmailValid) {
            emailEditText.setError("כתובת אימייל לא תקינה");
        }
        if (!isPasswordValid) {
            passwordEditText.setError("סיסמא חייבת להיות לפחות 6 תווים");
        }
        if (!isUsernameValid) {
            usernameEditText.setError("שם משתמש לא יכול להיות \"\"");
        }

        // Enable sign-in button only if all fields are valid
        signInButton.setEnabled(isEmailValid && isPasswordValid && isUsernameValid);
    }

    /**
     * Registers a new user using Firebase Authentication.
     * @param view The current view to navigate after successful registration.
     */
    private void signInUser(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE); // Show progress indicator

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE); // Hide progress indicator

                    if (task.isSuccessful()) {
                        // Show success message
                        Toast.makeText(getActivity(), "Registration successful!", Toast.LENGTH_SHORT).show();
                        // Navigate to login screen
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(R.id.action_SignInFragment_to_LoginFragment);
                    } else {
                        // Show error message
                        Toast.makeText(getActivity(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
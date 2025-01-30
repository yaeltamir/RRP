package com.example.recipereach.ui.login;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipereach.HomeViewActivity;
import com.example.recipereach.databinding.FragmentLoginBinding;

import com.example.recipereach.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;  // ViewModel to manage login data
    private FragmentLoginBinding binding;  // Binding to access views

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;  // Username input field
        final EditText passwordEditText = binding.password;  // Password input field
        final Button loginButton = binding.login;  // Login button
        final Button SigninButton = binding.Signin;  // Navigate to SignUp button
        final ProgressBar loadingProgressBar = binding.loading;  // Loading progress bar

        // Observe login form state changes
        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());  // Enable login button if data is valid
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));  // Show username error
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));  // Show password error
                }
            }
        });

        // Observe login result changes
        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);  // Hide progress bar
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());  // Show login failure message
                }
                if (loginResult.getSuccess() != null) {
                    // Update UI with successful login
                }
            }
        });

        // TextWatcher for validating login data as text changes
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Validate login data after text changes
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);  // Add listener to username field
        passwordEditText.addTextChangedListener(afterTextChangedListener);  // Add listener to password field

        // Handle "done" action on the password field
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform login when done
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);  // Show progress bar

                String email = usernameEditText.getText().toString().trim();  // Get email input
                String password = passwordEditText.getText().toString().trim();  // Get password input

                // Validate input fields
                if (TextUtils.isEmpty(email)) {
                    usernameEditText.setError("Email is required");
                    loadingProgressBar.setVisibility(View.GONE);  // Hide progress bar
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    loadingProgressBar.setVisibility(View.GONE);  // Hide progress bar
                    return;
                }

                // Attempt to sign in with Firebase Authentication
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            loadingProgressBar.setVisibility(View.GONE);  // Hide progress bar
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    // On successful login, navigate to HomeActivity
                                    Intent intent = new Intent(requireContext(), HomeViewActivity.class);
                                    intent.putExtra("USERNAME", user.getUid());
                                    startActivity(intent);
                                    requireActivity().finish();  // Close current screen
                                }
                            } else {
                                // Show error message if login fails
                                Toast.makeText(requireContext(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Handle sign-in button click (navigate to SignUp fragment)
        SigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_LoginFragment_to_SignInFragment);  // Navigate to SignUp
            }
        });
    }

    // Show a welcome message after successful login
    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    // Show an error message if login fails
    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Clear binding reference
    }
}

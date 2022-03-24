package com.felixstanley.makanmoerahandroid.fragment.account

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.databinding.FragmentAccountBinding
import com.felixstanley.makanmoerahandroid.databinding.FragmentLoginBinding
import com.felixstanley.makanmoerahandroid.fragment.AbstractFragment
import com.felixstanley.makanmoerahandroid.network.api.NetworkApi
import com.felixstanley.makanmoerahandroid.utility.Utility
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class AccountFragment : AbstractFragment() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginViewModelFactory: LoginViewModelFactory

    private lateinit var fragmentAccountBinding: FragmentAccountBinding
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var accountViewModelFactory: AccountViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val sharedPreferencesLoginEmailKey = Constants.SHARED_PREFERENCES_LOGIN_EMAIL_KEY
        val sharedPreferencesLoginPasswordKey = Constants.SHARED_PREFERENCES_LOGIN_PASSWORD_KEY
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)

        if (Utility.credentialsExistAtSharedPreferences(sharedPreferences)) {
            // User has login, Inflate Account Fragment
            fragmentAccountBinding = FragmentAccountBinding.inflate(inflater)

            // Initialize ViewModelFactory & ViewModel
            accountViewModelFactory = AccountViewModelFactory(
                NetworkApi.userService,
                sharedPreferences,
                sharedPreferencesLoginEmailKey,
                sharedPreferencesLoginPasswordKey
            )
            accountViewModel =
                ViewModelProvider(this, accountViewModelFactory).get(AccountViewModel::class.java)

            // Initialize Data Binding Variables
            fragmentAccountBinding.lifecycleOwner = this
            fragmentAccountBinding.accountViewModel = accountViewModel

            initializeLoggedInUser()
            initializeLogoutCompletedBehavior()
            initializeLogoutFailureBehavior()
            initializeReloginFailureBehavior()

            return fragmentAccountBinding.root
        } else {
            // User has yet to login / has log out, Inflate Login Fragment
            fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)

            // Initialize ViewModelFactory & ViewModel
            loginViewModelFactory = LoginViewModelFactory(
                NetworkApi.userService,
                sharedPreferences,
                sharedPreferencesLoginEmailKey,
                sharedPreferencesLoginPasswordKey
            )
            loginViewModel = ViewModelProvider(this, loginViewModelFactory)
                .get(LoginViewModel::class.java)

            // Initialize Data Binding Variables
            fragmentLoginBinding.lifecycleOwner = this
            fragmentLoginBinding.loginViewModel = loginViewModel

            initializeLoginEmailEditTextValidation()
            initializeLoginPasswordEditTextValidation()
            initializeLoginButtonBehavior()
            initializeLoginCompletedBehavior()
            initializeLoginFailureBehavior()
            initializeOAuth2LoginBehavior()

            return fragmentLoginBinding.root
        }
    }

    private fun initializeLoggedInUser() {
        accountViewModel.loggedInUser.observe(viewLifecycleOwner, { it ->
            fragmentAccountBinding.loggedInUser = it
            fragmentAccountBinding.executePendingBindings()
        })
    }

    private fun initializeLogoutCompletedBehavior() {
        accountViewModel.logoutCompleted.observe(viewLifecycleOwner, { it ->
            if (it == true) {
                // Reset Logout Completed Flag
                accountViewModel.resetLogoutCompletedFlag()
                // Logout is Complete, Reload this fragment so that login information is displayed
                reloadCurrentFragment()
            }
        })
    }

    private fun initializeLogoutFailureBehavior() {
        accountViewModel.logoutFailure.observe(viewLifecycleOwner, { it ->
            if (it != null) {
                when (it) {
                    LogoutFailure.LOGOUT_GENERAL_EXCEPTION -> showAccountFragmentFailureSnackbar(
                        getString(R.string.account_fragment_logout_failure_general_exception_error_message)
                    )
                }
                // Reset Logout Failure Flag Afterwards
                accountViewModel.resetLogoutFailureFlag()
            }
        })
    }

    private fun initializeReloginFailureBehavior() {
        accountViewModel.reloginFailure.observe(viewLifecycleOwner, { it ->
            if (it != null) {
                when (it) {
                    LoginFailure.INVALID_CREDENTIALS, LoginFailure.FAILED_RECAPTCHA,
                    LoginFailure.EMPTY_RECAPTCHA_RESPONSE -> showAccountFragmentFailureSnackbar(
                        getString(R.string.account_fragment_relogin_failure_general_exception_error_message)
                    )
                }
                // Reset Relogin Failure Flag Afterwards
                accountViewModel.resetReloginFailureFlag()
            }
        })
    }

    private fun showAccountFragmentFailureSnackbar(failureMessage: String) {
        val logoutButton = fragmentAccountBinding.logoutButton
        Snackbar.make(logoutButton, failureMessage, Snackbar.LENGTH_SHORT)
            .setAnchorView(bottomNavigationView).show()
    }

    private fun initializeLoginEmailEditTextValidation() {
        val loginEmailInputLayout = fragmentLoginBinding.loginEmailInputLayout
        val loginEmailInputEditText = loginEmailInputLayout.editText

        loginEmailInputEditText?.doAfterTextChanged { text: Editable? ->
            if (validateEmailString(text.toString())) {
                // If Validation passes, remove error text
                loginEmailInputLayout.error = null
            } else {
                // Add Error Message Otherwise
                addEmailErrorMessage(text.toString())
            }
        }
    }

    private fun initializeLoginPasswordEditTextValidation() {
        val loginPasswordInputLayout = fragmentLoginBinding.loginPasswordInputLayout
        val loginPasswordInputEditText = loginPasswordInputLayout.editText

        loginPasswordInputEditText?.doAfterTextChanged { text: Editable? ->
            if (!text.toString().isNullOrBlank()) {
                // If Validation passes, remove error text
                loginPasswordInputLayout.error = null
            } else {
                // Add Error Message Otherwise
                addPasswordErrorMessage(text.toString())
            }
        }
    }

    private fun initializeLoginButtonBehavior() {
        loginViewModel.loginButtonClicked.observe(viewLifecycleOwner, { it ->
            if (it == true) {
                // Login Button Is Clicked, Validate Email String & Password
                val email = fragmentLoginBinding.loginEmailInputLayout.editText?.text.toString()
                val password =
                    fragmentLoginBinding.loginPasswordInputLayout.editText?.text.toString()
                if (validateEmailString(email) && !password.isNullOrBlank()) {
                    // Validation passes, proceed to login
                    loginViewModel.login(email, password)
                } else {
                    addEmailErrorMessage(email)
                    addPasswordErrorMessage(password)
                }
                loginViewModel.resetLoginButtonClickedFlag()
            }
        })
    }

    private fun initializeLoginCompletedBehavior() {
        loginViewModel.loginCompleted.observe(viewLifecycleOwner, { it ->
            if (it == true) {
                // Reset Login Completed Flag
                loginViewModel.resetLoginCompletedFlag()
                // Login is Complete, Reload this fragment so that account information is displayed
                reloadCurrentFragment()
            }
        })
    }

    private fun initializeLoginFailureBehavior() {
        loginViewModel.loginFailure.observe(viewLifecycleOwner, { it ->
            if (it != null) {
                when (it) {
                    LoginFailure.INVALID_CREDENTIALS -> showLoginFailureSnackbar(
                        getString(R.string.login_fragment_login_failure_invalid_credentials_error_message)
                    )
                    LoginFailure.FAILED_RECAPTCHA -> showLoginFailureSnackbar(
                        getString(R.string.login_fragment_login_failure_failed_recaptcha_error_message)
                    )
                    LoginFailure.EMPTY_RECAPTCHA_RESPONSE -> showLoginFailureSnackbar(
                        getString(R.string.login_fragment_login_failure_empty_recaptcha_response_error_message)
                    )
                }
                // Reset Login Failure Flag Afterwards
                loginViewModel.resetLoginFailureFlag()
            }
        })
    }

    private fun showLoginFailureSnackbar(failureMessage: String) {
        val loginButton = fragmentLoginBinding.loginButton
        Snackbar.make(loginButton, failureMessage, Snackbar.LENGTH_SHORT)
            .setAnchorView(bottomNavigationView).show()
    }

    private fun addEmailErrorMessage(email: String?) {
        val loginEmailInputLayout = fragmentLoginBinding.loginEmailInputLayout

        // Add Corresponding Error Message depending on validation error
        if (email.isNullOrBlank()) {
            loginEmailInputLayout.error =
                getString(R.string.login_fragment_empty_email_error_message)
        } else if (!validateEmailString(email)) {
            loginEmailInputLayout.error =
                getString(R.string.login_fragment_invalid_email_error_message)
        }
    }

    private fun addPasswordErrorMessage(password: String?) {
        val loginPasswordInputLayout = fragmentLoginBinding.loginPasswordInputLayout

        // Add Corresponding Error Message depending on validation error
        if (password.isNullOrBlank()) {
            loginPasswordInputLayout.error =
                getString(R.string.login_fragment_empty_password_error_message)
        }
    }

    private fun initializeOAuth2LoginBehavior() {
        loginViewModel.oAuth2LoginButtonClicked.observe(viewLifecycleOwner, { it ->
            if (it != null) {
                // Reset OAuth2LoginButtonClicked Flag before calling Custom Tabs Intent
                // to Open Authorization URL
                loginViewModel.resetOAuth2LoginButtonClickedFlag()
                callCustomTabsIntent(it)
            }
        })
    }
}
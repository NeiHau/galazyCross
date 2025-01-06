package com.hakutogames.galaxycross.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.hakutogames.galaxycross.repository.BillingRepositoryImpl
import com.hakutogames.galaxycross.ui.theme.GalaxyCrossTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var billingRepository: BillingRepositoryImpl

    // GoogleSignInClientを後で初期化
    private lateinit var googleSignInClient: GoogleSignInClient

    private var onSignInSuccessAction: (() -> Unit)? = null

    private var lastKnownUserEmail: String? = null

    // Activity Result APIを使う場合のLauncher
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        // Google Sign-In 結果を受け取る
        Log.d("MainActivity", "signInLauncher callback called. result=$result")
        handleSignInResult(result.resultCode, result.data)
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate: start")

        // BillingClient 初期化
        billingRepository.initializeBillingClient()

        // GoogleSignInOptionsを設定（メールアドレス取得など）
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // emailを取得
                // .requestIdToken("your-client-id.apps.googleusercontent.com") // 必要ならIDトークンも
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
        } catch (e: ApiException) {
            Log.d("MainActivity", "サインインエラーが起きました：$e")
            return
        }

        // すでにログイン済みの場合のアカウントを取得（nullの可能性あり）
        val alreadyLoggedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (alreadyLoggedInAccount != null) {
            // すでにログイン済み
            Log.d("MainActivity", "Already logged in: ${alreadyLoggedInAccount.email}")
            onLoggedIn(alreadyLoggedInAccount)
        }

        // UIセットアップ
        enableEdgeToEdge()
        setContent {
            GalaxyCrossTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val navController = rememberNavController()

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) {
                    AppNavigation(
                        navController = navController,
                        onShowSnackbar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        },
                        onGoogleSignInRequested = { actionAfterSignIn ->
                            Log.d("MainActivity", "onGoogleSignInRequested called!")
                            doGoogleSignIn(actionAfterSignIn)
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 現在の端末での最終ログイン状態を取得
        val currentAccount = GoogleSignIn.getLastSignedInAccount(this)
        val currentEmail = currentAccount?.email

        if (currentEmail != lastKnownUserEmail) {
            // ここで「アカウントが切り替わっている」と判定
            // 例: アプリ側が保持していたサインイン状態をリセット
            Log.d("MainActivity", "Detected account change. Sign-out or re-auth.")
            signOut()

            // 必要に応じて再サインインフローを開始
            // doGoogleSignIn()
        }

        // 最新のメールアドレスを保持
        lastKnownUserEmail = currentEmail
    }

    /**
     * サインインフローを開始。
     * サインイン完了後には [actionAfterSignIn] を呼び出す。
     */
    private fun doGoogleSignIn(actionAfterSignIn: () -> Unit) {
        Log.d("MainActivity", "doGoogleSignIn: launch signIn intent.")
        onSignInSuccessAction = actionAfterSignIn
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    /**
     * Google Sign-In の結果を受け取る
     */
    private fun handleSignInResult(resultCode: Int, data: Intent?) {
        Log.d("MainActivity", "handleSignInResult called! resultCode=$resultCode, data=$data")
        if (resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("MainActivity", "SignIn succeeded: account=${account.email}")
                onLoggedIn(account)

                // サインイン完了後に呼ぶコールバックがあればここで実行
                Log.d("MainActivity", "Calling onSignInSuccessAction?.invoke()")
                onSignInSuccessAction?.invoke()
                onSignInSuccessAction = null
            } catch (e: ApiException) {
                Log.e("MainActivity", "SignIn Failed in catch: ${e.statusCode}, ${e.localizedMessage}")
                onSignInSuccessAction = null
            }
        } else {
            Log.d("MainActivity", "SignIn Canceled or resultCode != OK")
            onSignInSuccessAction = null
        }
    }

    /**
     * ログイン成功後の処理
     *  - Billing用に使うアカウントIDを取得し、アプリ側で保持
     */
    private fun onLoggedIn(account: GoogleSignInAccount) {
        val userEmail = account.email
        Log.d("MainActivity", "Google Sign-In Success: email=$userEmail")
        // BillingFlowParams.setObfuscatedAccountId() にセットするためのID(例:メールアドレス)を保存
        // （BillingRepositoryImplにsetterを用意している想定）
        billingRepository.setCurrentAccountId(userEmail)
    }

//    /**
//     * サインアウト（必要に応じて実装）
//     */
    private fun signOut() {
        googleSignInClient.signOut()
            .addOnCompleteListener {
                // サインアウト後の処理
            }
    }
}

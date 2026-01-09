package dev.gamified.GamifiedPlatform.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Controller temporário para receber callbacks OAuth2 e exibir tokens.
 * Em produção, o frontend deve fazer isso.
 */
@Controller
@Hidden // Ocultar do Swagger
public class OAuth2CallbackController {

    @GetMapping("/oauth2/redirect")
    public void oauth2Callback(
            @RequestParam(required = false) String accessToken,
            @RequestParam(required = false) String refreshToken,
            @RequestParam(required = false) String tokenType,
            @RequestParam(required = false) String error,
            HttpServletResponse response) throws IOException {

        if (error != null) {
            // Se houve erro, exibir mensagem
            response.setContentType("text/html");
            response.getWriter().write("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>OAuth2 Login - Error</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            max-width: 800px;
                            margin: 50px auto;
                            padding: 20px;
                            background: #f5f5f5;
                        }
                        .error-container {
                            background: white;
                            padding: 30px;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                            border-left: 4px solid #dc3545;
                        }
                        h1 { color: #dc3545; }
                        .error { color: #dc3545; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="error-container">
                        <h1> OAuth2 Login Failed</h1>
                        <p class="error"><strong>Error:</strong> """ + error + """
                        </p>
                        <p><a href="http://localhost:8080/swagger-ui.html">← Back to Swagger</a></p>
                    </div>
                </body>
                </html>
                """);
            return;
        }

        // Se sucesso, exibir os tokens
        response.setContentType("text/html");
        response.getWriter().write("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>OAuth2 Login - Success</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        max-width: 800px;
                        margin: 50px auto;
                        padding: 20px;
                        background: #f5f5f5;
                    }
                    .success-container {
                        background: white;
                        padding: 30px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        border-left: 4px solid #28a745;
                    }
                    h1 { color: #28a745; }
                    .token-box {
                        background: #f8f9fa;
                        padding: 15px;
                        border-radius: 4px;
                        margin: 15px 0;
                        word-break: break-all;
                        font-family: 'Courier New', monospace;
                        font-size: 12px;
                    }
                    .label {
                        font-weight: bold;
                        color: #495057;
                        margin-bottom: 5px;
                    }
                    button {
                        background: #007bff;
                        color: white;
                        border: none;
                        padding: 10px 20px;
                        border-radius: 4px;
                        cursor: pointer;
                        margin: 5px;
                    }
                    button:hover {
                        background: #0056b3;
                    }
                    .info {
                        background: #d1ecf1;
                        padding: 15px;
                        border-radius: 4px;
                        margin: 20px 0;
                        border-left: 4px solid #17a2b8;
                    }
                </style>
            </head>
            <body>
                <div class="success-container">
                    <h1> OAuth2 Login Successful!</h1>
                    
                    <div class="info">
                        <strong> Congratulations!</strong> Your OAuth2 login was successful. 
                        Use the tokens below to authenticate your requests.
                    </div>
                    
                    <div class="label">Access Token (expires in 15 minutes):</div>
                    <div class="token-box" id="accessToken">""" + accessToken + """
                    </div>
                    <button onclick="copyToken('accessToken')">📋 Copy Access Token</button>
                    
                    <div class="label" style="margin-top: 20px;">Refresh Token (expires in 7 days):</div>
                    <div class="token-box" id="refreshToken">""" + refreshToken + """
                    </div>
                    <button onclick="copyToken('refreshToken')">📋 Copy Refresh Token</button>
                    
                    <div class="info" style="margin-top: 30px;">
                        <strong> How to use:</strong>
                        <ol>
                            <li>Copy the Access Token</li>
                            <li>Go to <a href="http://localhost:8080/swagger-ui.html" target="_blank">Swagger UI</a></li>
                            <li>Click the "Authorize" button 🔒</li>
                            <li>Paste the token (without "Bearer " prefix)</li>
                            <li>Test your authenticated endpoints!</li>
                        </ol>
                    </div>
                    
                    <div style="margin-top: 20px;">
                        <a href="http://localhost:8080/swagger-ui.html">
                            <button style="background: #28a745;"> Go to Swagger UI</button>
                        </a>
                    </div>
                </div>
                
                <script>
                    function copyToken(elementId) {
                        const text = document.getElementById(elementId).innerText;
                        navigator.clipboard.writeText(text).then(() => {
                            alert('Token copied to clipboard!');
                        });
                    }
                    
                    // Auto-store tokens in localStorage for convenience
                    localStorage.setItem('accessToken', '""" + (accessToken != null ? accessToken : "") + """
');
                    localStorage.setItem('refreshToken', '""" + (refreshToken != null ? refreshToken : "") + """
');
                    console.log('Tokens stored in localStorage');
                </script>
            </body>
            </html>
            """);
    }
}


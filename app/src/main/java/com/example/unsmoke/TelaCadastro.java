package com.example.unsmoke;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TelaCadastro extends AppCompatActivity {

    EditText nomeCompleto, telefoneCadastro, emailCadastro, senhaCadastro;
    CheckBox mostrarSenhaCadastro;
    Button criarConta;
    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        nomeCompleto = findViewById(R.id.nomeCompleto);
        telefoneCadastro = findViewById(R.id.telefone);
        emailCadastro = findViewById(R.id.emailCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        mostrarSenhaCadastro = findViewById(R.id.mostrarSenhaCadastro);
        criarConta = findViewById(R.id.criarConta);

        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nomeCompleto.getText().length() == 0){
                    nomeCompleto.setError("Voc?? precisa inserir o seu nome para se cadastrar!");
                }
                else if (emailCadastro.getText().length() < 5){
                    emailCadastro.setError("Voc?? precisa inserir um email v??lido!");
                }
                else if (senhaCadastro.getText().length() < 8){
                    senhaCadastro.setError("A sua deve ter pelo menos 8 caracteres!");
                }
                else{
                    CadastrarUsuario(v);
                }
            }
        });
    }

    public void CadastrarUsuario(View v){

        String email = emailCadastro.getText().toString();
        String senha = senhaCadastro.getText().toString();

        Intent irTelaContaCriada = new Intent(this, TelaPadraoUso.class);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    SalvarDadosUsuario();

                    startActivity(irTelaContaCriada);
                }else{
                    String erro;
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erro = "Digite uma senha com no m??nimo 6 caracteres!";
                    }catch (FirebaseAuthUserCollisionException e){
                        erro = "Esta conta de email j?? est?? cadastrada!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "Email inv??lido";
                    }catch (Exception e){
                        erro = "Erro ao cadastrar o usu??rio";
                    }

                    Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    private void SalvarDadosUsuario(){

        String nome = nomeCompleto.getText().toString();
        String telefone = telefoneCadastro.getText().toString();
        String email = emailCadastro.getText().toString();
        String senha = senhaCadastro.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("Nome", nome);
        usuarios.put("Telefone", telefone);
        usuarios.put("Email", email);
        usuarios.put("Senha", senha);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ns = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informa????es pessoais");
        ns.set(usuarios);
    }

    public void mostrarSenha(View m) {
        if (mostrarSenhaCadastro.isChecked()){
            senhaCadastro.setInputType(InputType.TYPE_CLASS_TEXT);
        }else{
            senhaCadastro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
}
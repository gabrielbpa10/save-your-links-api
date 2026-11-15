const senha = document.getElementById("password");
const confirmarSenha = document.getElementById("confirmPassword");
const ajuda = document.getElementById("confirmar-senha-ajuda");

function validarSenhas() {
    const diferentes = confirmarSenha.value !== ""
        && senha.value !== confirmarSenha.value;
    const mensagem = diferentes ? "As senhas devem ser iguais." : "";

    confirmarSenha.setCustomValidity(mensagem);
    confirmarSenha.setAttribute("aria-invalid", String(diferentes));
    ajuda.textContent = mensagem;
}

senha.addEventListener("input", validarSenhas);
confirmarSenha.addEventListener("input", validarSenhas);
confirmarSenha.form.addEventListener("submit", (event) => {
    validarSenhas();
    if (!confirmarSenha.form.reportValidity()) {
        event.preventDefault();
    }
});

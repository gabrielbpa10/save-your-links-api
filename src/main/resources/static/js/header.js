const botaoCadastrar = document.querySelector("#button-cadastro-link");

botaoCadastrar.addEventListener("click", () => {
    window.location.href = botaoCadastrar.dataset.url;
});
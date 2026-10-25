function abrirModalExcluirLink(idLink) {
    document.querySelector("#id-link-excluir").value = idLink;

    const modal = document.querySelector("#modal-excluir-link");
    bootstrap.Modal.getOrCreateInstance(modal).show();
}

document.querySelectorAll(".trash-link button[data-link-id]").forEach((botao) => {
    botao.addEventListener("click", () => {
        abrirModalExcluirLink(botao.dataset.linkId);
    });
});

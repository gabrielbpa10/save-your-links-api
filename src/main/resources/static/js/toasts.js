document.querySelectorAll(".toast").forEach((elemento) => {
    bootstrap.Toast.getOrCreateInstance(elemento, {
        autohide: true,
        delay: 5000
    }).show();
});

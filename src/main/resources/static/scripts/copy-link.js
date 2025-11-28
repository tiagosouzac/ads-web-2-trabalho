document.addEventListener("DOMContentLoaded", function () {
  const copyBtn = document.getElementById("copy-link-btn");
  if (copyBtn) {
    copyBtn.addEventListener("click", function () {
      const linkElement = document.getElementById("event-link");
      if (linkElement) {
        const link = linkElement.textContent;
        navigator.clipboard
          .writeText(link)
          .then(function () {
            const span = copyBtn.querySelector("span");
            if (span) {
              const originalText = span.textContent;
              span.textContent = "Copiado!";
              setTimeout(function () {
                span.textContent = originalText;
              }, 2000);
            }
          })
          .catch(function (err) {
            console.error("Erro ao copiar link: ", err);
          });
      }
    });
  }
});

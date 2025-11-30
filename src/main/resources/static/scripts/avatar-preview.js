document.addEventListener("DOMContentLoaded", function () {
  const avatarInput = document.getElementById("avatar");
  const avatarLabel = document.getElementById("avatar-selector");

  avatarInput.addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        avatarLabel.innerHTML = `<img src="${e.target.result}" alt="Avatar Preview" class="size-32 rounded-full object-cover">`;
      };
      reader.readAsDataURL(file);
    } else {
      // Reset to original SVG if no file selected
      avatarLabel.innerHTML = `
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-user-icon lucide-user size-12 text-neutral-400">
                    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                    <circle cx="12" cy="7" r="4" />
                </svg>
            `;
    }
  });

  const logoInput = document.getElementById("logo");
  const logoLabel = document.getElementById("logo-selector");

  if (logoInput && logoLabel) {
    logoInput.addEventListener("change", function (event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
          logoLabel.innerHTML = `<img src="${e.target.result}" alt="Logo Preview" class="size-32 object-cover">`;
        };
        reader.readAsDataURL(file);
      } else {
        // Reset to original SVG if no file selected
        logoLabel.innerHTML = `
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-building size-12 text-neutral-400">
                      <path d="m6 22 3-3 3 3" />
                      <path d="M6 18v-5a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v5" />
                      <path d="M14 18v-5a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v5" />
                      <path d="M16 6h.01" />
                      <path d="M16 2h.01" />
                      <path d="M12 6h.01" />
                      <path d="M12 2h.01" />
                      <path d="M8 6h.01" />
                      <path d="M8 2h.01" />
                      <path d="M4 10h16" />
                      <path d="M10 10v.01" />
                      <path d="M14 10v.01" />
                  </svg>
              `;
      }
    });
  }
});

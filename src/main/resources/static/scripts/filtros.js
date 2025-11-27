// document.addEventListener("DOMContentLoaded", function () {
//   const dropdown = document.getElementById("category-dropdown");
//   const button = document.getElementById("category-button");
//   const menu = document.getElementById("category-menu");
//   const arrow = document.getElementById("category-arrow");

//   const dateDropdown = document.getElementById("date-dropdown");
//   const dateButton = document.getElementById("date-button");
//   const dateMenu = document.getElementById("date-menu");
//   const dateArrow = document.getElementById("date-arrow");
//   const dateOptions = document.querySelectorAll(".date-option");
//   const dateSelected = document.getElementById("date-selected");
//   const dateInput = document.getElementById("date-input");

//   const localDropdown = document.getElementById("local-dropdown");
//   const localButton = document.getElementById("local-button");
//   const localMenu = document.getElementById("local-menu");
//   const localArrow = document.getElementById("local-arrow");

//   let isCategoryOpen = false;
//   let isDateOpen = false;
//   let isLocalOpen = false;

//   function initializeCategoryDropdown() {
//     setupCategoryMenuStyles();
//     setupCategoryEventListeners();
//   }

//   function setupCategoryMenuStyles() {
//     menu.style.opacity = "0";
//     menu.style.transform = "translateY(-10px)";
//     menu.style.transition = "opacity 200ms ease, transform 200ms ease";
//   }

//   function setupCategoryEventListeners() {
//     button.addEventListener("click", handleCategoryButtonClick);
//     document.addEventListener("click", handleOutsideClick);
//     document.addEventListener("keydown", handleKeyPress);
//   }

//   function openCategoryDropdown() {
//     closeDateDropdown();
//     closeLocalDropdown();
//     isCategoryOpen = true;
//     menu.classList.remove("hidden");
//     arrow.classList.add("rotate-180");

//     setTimeout(() => {
//       menu.style.opacity = "1";
//       menu.style.transform = "translateY(0)";
//     }, 10);
//   }

//   function closeCategoryDropdown() {
//     if (!isCategoryOpen) return;

//     isCategoryOpen = false;
//     menu.style.opacity = "0";
//     menu.style.transform = "translateY(-10px)";
//     arrow.classList.remove("rotate-180");

//     setTimeout(() => {
//       menu.classList.add("hidden");
//     }, 200);
//   }

//   function handleCategoryButtonClick(e) {
//     e.stopPropagation();
//     toggleCategoryDropdown();
//   }

//   function toggleCategoryDropdown() {
//     if (isCategoryOpen) {
//       closeCategoryDropdown();
//     } else {
//       openCategoryDropdown();
//     }
//   }

//   function initializeLocalDropdown() {
//     setupLocalMenuStyles();
//     setupLocalEventListeners();
//   }

//   function setupLocalMenuStyles() {
//     localMenu.style.opacity = "0";
//     localMenu.style.transform = "translateY(-10px)";
//     localMenu.style.transition = "opacity 200ms ease, transform 200ms ease";
//   }

//   function setupLocalEventListeners() {
//     localButton.addEventListener("click", handleLocalButtonClick);
//   }

//   function openLocalDropdown() {
//     closeCategoryDropdown();
//     closeDateDropdown();
//     isLocalOpen = true;
//     localMenu.classList.remove("hidden");
//     localArrow.classList.add("rotate-180");

//     setTimeout(() => {
//       localMenu.style.opacity = "1";
//       localMenu.style.transform = "translateY(0)";
//     }, 10);
//   }

//   function closeLocalDropdown() {
//     if (!isLocalOpen) return;

//     isLocalOpen = false;
//     localMenu.style.opacity = "0";
//     localMenu.style.transform = "translateY(-10px)";
//     localArrow.classList.remove("rotate-180");

//     setTimeout(() => {
//       localMenu.classList.add("hidden");
//     }, 200);
//   }

//   function handleLocalButtonClick(e) {
//     e.stopPropagation();
//     toggleLocalDropdown();
//   }

//   function toggleLocalDropdown() {
//     if (isLocalOpen) {
//       closeLocalDropdown();
//     } else {
//       openLocalDropdown();
//     }
//   }

//   function initializeDateDropdown() {
//     setupDateMenuStyles();
//     setupDateEventListeners();
//   }

//   function setupDateMenuStyles() {
//     dateMenu.style.opacity = "0";
//     dateMenu.style.transform = "translateY(-10px)";
//     dateMenu.style.transition = "opacity 200ms ease, transform 200ms ease";
//   }

//   function setupDateEventListeners() {
//     dateButton.addEventListener("click", handleDateButtonClick);
//     dateInput.addEventListener("change", handleDateInputChange);
//     dateOptions.forEach((option) => {
//       option.addEventListener("click", handleDateSelection);
//     });
//   }

//   function openDateDropdown() {
//     closeCategoryDropdown();
//     closeLocalDropdown();
//     isDateOpen = true;
//     dateMenu.classList.remove("hidden");
//     dateArrow.classList.add("rotate-180");

//     setTimeout(() => {
//       dateMenu.style.opacity = "1";
//       dateMenu.style.transform = "translateY(0)";
//     }, 10);
//   }

//   function closeDateDropdown() {
//     if (!isDateOpen) return;

//     isDateOpen = false;
//     dateMenu.style.opacity = "0";
//     dateMenu.style.transform = "translateY(-10px)";
//     dateArrow.classList.remove("rotate-180");

//     setTimeout(() => {
//       dateMenu.classList.add("hidden");
//     }, 200);
//   }

//   function handleDateButtonClick(e) {
//     e.stopPropagation();
//     toggleDateDropdown();
//   }

//   function handleDateInputChange(e) {
//     const selectedDate = e.target.value;
//     const urlParams = new URLSearchParams(window.location.search);
//     if (selectedDate) {
//       urlParams.set('dataInicio', selectedDate);
//     } else {
//       urlParams.delete('dataInicio');
//     }
//     window.location.href = '/eventos/buscar?' + urlParams.toString();
//   }

//   function handleDateSelection(e) {
//     e.stopPropagation();
//     const value = this.getAttribute("data-value");

//     let dateValue = "";
//     const today = new Date();
//     switch (value) {
//       case "":
//         const urlParams = new URLSearchParams(window.location.search);
//         urlParams.delete('dataInicio');
//         window.location.href = '/eventos/buscar?' + urlParams.toString();
//         return;
//       case "today":
//         dateValue = today.toISOString().split("T")[0];
//         break;
//       case "tomorrow":
//         const tomorrow = new Date(today);
//         tomorrow.setDate(today.getDate() + 1);
//         dateValue = tomorrow.toISOString().split("T")[0];
//         break;
//       case "this-week":
//         // Set to Monday of this week
//         const monday = new Date(today);
//         monday.setDate(today.getDate() - today.getDay() + 1);
//         dateValue = monday.toISOString().split("T")[0];
//         break;
//       case "this-weekend":
//         // Set to Saturday of this week
//         const saturday = new Date(today);
//         saturday.setDate(today.getDate() - today.getDay() + 6);
//         dateValue = saturday.toISOString().split("T")[0];
//         break;
//       case "next-week":
//         // Set to Monday of next week
//         const nextMonday = new Date(today);
//         nextMonday.setDate(today.getDate() - today.getDay() + 8);
//         dateValue = nextMonday.toISOString().split("T")[0];
//         break;
//       default:
//         dateValue = "";
//     }

//     if (dateValue) {
//       const urlParams = new URLSearchParams(window.location.search);
//       urlParams.set('dataInicio', dateValue);
//       window.location.href = '/eventos/buscar?' + urlParams.toString();
//     }
//   }

//   function updateSelectedDate(text) {
//     dateSelected.textContent = text;
//   }

//   function highlightSelectedDateOption(selectedOption) {
//     dateOptions.forEach((opt) => {
//       opt.classList.remove("bg-blue-50", "text-blue-600");
//     });
//     selectedOption.classList.add("bg-blue-50", "text-blue-600");
//   }

//   function toggleDateDropdown() {
//     if (isDateOpen) {
//       closeDateDropdown();
//     } else {
//       openDateDropdown();
//     }
//   }

//   function handleOutsideClick(e) {
//     if (
//       !dropdown.contains(e.target) &&
//       !dateDropdown.contains(e.target) &&
//       !localDropdown.contains(e.target)
//     ) {
//       closeCategoryDropdown();
//       closeDateDropdown();
//       closeLocalDropdown();
//     }
//   }

//   function handleKeyPress(e) {
//     if (e.key === "Escape") {
//       if (isCategoryOpen) closeCategoryDropdown();
//       if (isDateOpen) closeDateDropdown();
//       if (isLocalOpen) closeLocalDropdown();
//     }
//   }

//   initializeCategoryDropdown();
//   initializeDateDropdown();
//   initializeLocalDropdown();
// });

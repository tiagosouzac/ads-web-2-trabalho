// Percentage of the item that has to be inside the container
// for it it be considered as inside the container
const THRESHOLD = 0.6;

const intersectionX = (element, container) => {
  const delta = container.width / 1_000;

  if (element.right < container.left - delta) {
    return 0.0;
  }

  if (element.left > container.right + delta) {
    return 0.0;
  }

  if (element.left < container.left - delta) {
    return element.right - container.left + delta;
  }

  if (element.right > container.right + delta) {
    return container.right - element.left + delta;
  }

  return element.width;
};

const isHTMLElement = (x) => typeof x.offsetLeft === "number";

function setup({ slider }) {
  const container = slider.querySelector("[data-slides]");
  const slides = slider.querySelectorAll("[data-slides] > *");
  const prev = slider.querySelector("[data-prev-slide]");
  const next = slider.querySelector("[data-next-slide]");
  const infinite = Boolean(slider.hasAttribute("data-infinite"));
  const interval = Number(slider.getAttribute("data-interval"));

  const getElementsInsideContainer = () => {
    const indices = [];
    const sliderRect = slider.getBoundingClientRect();

    for (let index = 0; index < slides.length; index++) {
      const item = slides.item(index);
      const rect = item.getBoundingClientRect();

      const ratio = intersectionX(rect, sliderRect) / rect.width;

      if (ratio > THRESHOLD) {
        indices.push(index);
      }
    }

    return indices;
  };

  const goToItem = (index) => {
    const item = slides.item(index);

    if (!isHTMLElement(item)) {
      console.warn(
        `Element at index ${index} is not an html element. Skipping carousel`
      );

      return;
    }

    container.scrollTo({
      top: 0,
      behavior: "smooth",
      left: item.offsetLeft - slider.offsetLeft,
    });
  };

  const onClickPrev = () => {
    const indices = getElementsInsideContainer();
    const itemsPerPage = indices.length;
    const isShowingFirst = indices[0] === 0;
    const pageIndex = Math.floor(indices[indices.length - 1] / itemsPerPage);

    goToItem(
      isShowingFirst ? slides.length - 1 : (pageIndex - 1) * itemsPerPage
    );
  };

  const onClickNext = () => {
    const indices = getElementsInsideContainer();
    const itemsPerPage = indices.length;
    const isShowingLast = indices[indices.length - 1] === slides.length - 1;
    const pageIndex = Math.floor(indices[0] / itemsPerPage);

    goToItem(isShowingLast ? 0 : (pageIndex + 1) * itemsPerPage);
  };

  const observer = new IntersectionObserver(
    (elements) =>
      elements.forEach((item) => {
        const index = Array.from(slides).indexOf(item.target);

        if (!infinite) {
          if (index === 0) {
            if (item.isIntersecting) {
              prev.setAttribute("disabled", "");
            } else {
              prev.removeAttribute("disabled");
            }
          }

          if (index === slides.length - 1) {
            if (item.isIntersecting) {
              next.setAttribute("disabled", "");
            } else {
              next.removeAttribute("disabled");
            }
          }
        }
      }),
    { threshold: THRESHOLD, root: slider }
  );

  slides.forEach((slide) => observer.observe(slide));

  prev.addEventListener("click", onClickPrev);
  next.addEventListener("click", onClickNext);

  interval && setInterval(onClickNext, interval);
}

window.addEventListener("load", () => {
  const sliders = document.querySelectorAll("[data-slider]");

  for (const slider of sliders) {
    setup({ slider });
  }
});

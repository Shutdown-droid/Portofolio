const mobileMenuButton = document.getElementById("mobile-menu-button");
const mobileMenu = document.getElementById("mobile-menu");

if (mobileMenuButton && mobileMenu) {
  mobileMenuButton.addEventListener("click", () => {

    mobileMenu.classList.toggle("hidden");
  });
} else {
  console.warn("Mobile menu button or menu element not found. Mobile menu functionality might not work.");
}

const carousel = document.getElementById("carousel");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");

if (carousel && prevBtn && nextBtn) {
  const items = carousel.querySelectorAll(".carousel-item");

  const scrollAmount = 336;

  function updateActiveItem() {
    const carouselRect = carousel.getBoundingClientRect();
    const carouselCenter = carouselRect.left + carouselRect.width / 2;

    let closestIndex = 0;
    let minDistance = Infinity; 

    items.forEach((item, index) => {
      const itemRect = item.getBoundingClientRect();
      const itemCenter = itemRect.left + itemRect.width / 2;
      const distance = Math.abs(carouselCenter - itemCenter); 

      if (distance < minDistance) {
        minDistance = distance;
        closestIndex = index;
      }
    });

    items.forEach((item, index) => {
      if (index === closestIndex) {
        item.classList.add("active");
      } else {
        item.classList.remove("active");
      }
    });
  }

  updateActiveItem();

  let debounceTimeout;
  carousel.addEventListener("scroll", () => {
    clearTimeout(debounceTimeout);
    debounceTimeout = setTimeout(() => {
      updateActiveItem();
    }, 100); 
  });

  prevBtn.addEventListener("click", () => {
    carousel.scrollBy({ left: -scrollAmount, behavior: "smooth" });
  });

  nextBtn.addEventListener("click", () => {
    carousel.scrollBy({ left: scrollAmount, behavior: "smooth" });
  });

} else {
  console.warn("Carousel elements (carousel, prevBtn, or nextBtn) not found. Carousel functionality might not work.");
}
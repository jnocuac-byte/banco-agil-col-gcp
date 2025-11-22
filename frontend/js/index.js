window.addEventListener('scroll', () => {
  const scrollY = window.scrollY;
  const scrollX = window.scrollX;
  const objeto = document.getElementById('objeto-scroll');

  if (!objeto) return;

  // Controla cuánto se mueve en X y Y
  const desplazamientoX = scrollY * 1.2; // se mueve más rápido a la derecha
  const desplazamientoY = scrollY * 1.5; // mismo ritmo hacia abajo

  // Mantén el centrado original y suma desplazamiento
  objeto.style.transform = `translate(calc(-50% + ${desplazamientoX}px), calc(-50% + ${desplazamientoX}px))`;
});
document.addEventListener("DOMContentLoaded", () => {
  const observerOptions = {
    threshold: 0.4
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add("visible");
      }
    });
  }, observerOptions);

  const pcContainer = document.querySelector(".pc-container");
  const celContainer = document.querySelector(".cel-container");

  if (pcContainer) observer.observe(pcContainer);
  if (celContainer) observer.observe(celContainer);
});

document.addEventListener("DOMContentLoaded", () => {
  const h2Elements = document.querySelectorAll("h2");
  const pElements = document.querySelectorAll("p");

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add("visible");
          observer.unobserve(entry.target); // solo animar una vez
        }
      });
    },
    {
      threshold: 0.2, // 20% visible
    }
  );

  h2Elements.forEach(h2 => observer.observe(h2));
  pElements.forEach(p => observer.observe(p));
});



//-------------------------SECCION 4 ANIMACIONES GSAP-------------------------//

document.addEventListener("DOMContentLoaded", () => {
  if (typeof gsap === "undefined" || typeof ScrollTrigger === "undefined") {
        console.error("❌ GSAP o ScrollTrigger no están cargados correctamente.");
        return;
    }

    gsap.registerPlugin(ScrollTrigger);

    // ========= ANIMACIÓN SECCIÓN 4 (Tarjetas) =========

    // 👈 Definimos la media query para móviles/tablets pequeñas
    const isMobile = window.matchMedia("(max-width: 768px)").matches;

    // Solo ejecuta la animación si NO es un dispositivo móvil (o es más grande que 768px)
    if (!isMobile) {
        // ======== ANIMACIÓN SCROLL TARJETAS (SOLO EN DESKTOP) ========
        const tlTarjetas = gsap.timeline({
            scrollTrigger: {
                trigger: ".parte-4",
                start: "top top",
                end: "+=5500", 
                scrub: true,
                pin: true,
                anticipatePin: 1,
                // markers: true,
            },
        });

        // --- 1. Entrada de la tarjeta plateada (desde la derecha) ---
        tlTarjetas.fromTo(
            ".tarjeta-plateada",
            {
                x: "100%", 
                opacity: 0,
                rotateY: 0,
                rotateX: 0,
            },
            {
                x: "0%", 
                opacity: 1,
                duration: 2,
                ease: "power2.out",
            }
        )
        // --- 2. Rotación 3D de la plateada y preparación para las siguientes ---
        .to(".tarjeta-plateada", {
            rotateY: 40,
            rotateX: 60,
            scale: 0.9,
            duration: 1.5,
            ease: "power1.inOut",
        })
        // --- 3. Entrada de la tarjeta amarilla (desde abajo al centro) ---
        .fromTo(
            ".tarjeta-amarilla",
            {
                y: 200, 
                x: 0, 
                opacity: 0,
                rotateY: 150,
                rotateX: 60,
            },
            {
                y: 0, 
                x: -150, 
                opacity: 1,
                rotateY: 0,
                rotateX: 0,
                scale: 1,
                duration: 1.5,
                ease: "power2.out",
            },
            "<0.5" 
        )
        // --- 4. Entrada de la tarjeta negra (desde abajo al centro) ---
        .fromTo(
            ".tarjeta-negra",
            {
                y: 300, 
                x: 0, 
                opacity: 0,
                rotateY: 150,
                rotateX: 60,
            },
            {
                y: 0, 
                x: 150, 
                opacity: 1,
                rotateY: 0,
                rotateX: 0,
                scale: 1,
                duration: 1.5,
                ease: "power2.out",
            },
            "<0.3"
        )
        // --- 5. Tarjeta plateada final (movimiento final y escala) ---
        .to(".tarjeta-plateada", {
            x: 0,
            y: -100, 
            scale: 1.1,
            rotateY: 0,
            rotateX: 0,
            duration: 1,
            ease: "power1.inOut",
        }, "<"); 
        
    } else {
        // 👈 Si es móvil, nos aseguramos de que el pin no arruine el layout
        gsap.set(".parte-4", { clearProps: "all" });
    }

  // ========= ANIMACIÓN GLOBAL DE TITULOS (h2) =========
  const h2Elements = document.querySelectorAll("h2");

  h2Elements.forEach((h2) => {
    gsap.fromTo(
      h2,
      { y: 40, opacity: 0 },
      {
        y: 0,
        opacity: 1,
        duration: 1,
        ease: "power2.out",
        scrollTrigger: {
          trigger: h2,
          start: "top 90%",
          toggleActions: "play none none none",
        },
      }
    );
  });
});
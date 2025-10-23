import { useEffect } from "react";
import gsap from "gsap";
import "./preloader.css"; // Make sure your SVG and container styles are here

interface PreloaderProps {
  onFinish?: () => void;
}

export default function Preloader({ onFinish }: PreloaderProps) {
  useEffect(() => {
    // Fade out preloader after animation
    gsap.fromTo(
      ".logo-name",
      { y: 50, opacity: 0 },
      { y: 0, opacity: 1, duration: 2, delay: 0.5 }
    );

    gsap.fromTo(
      ".loading-page",
      { opacity: 1 },
      {
        opacity: 0,
        duration: 1.5,
        delay: 3.5,
        onComplete: () => {
          if (onFinish) onFinish();
        },
      }
    );
  }, [onFinish]);

  return (
    <>
      <div className="loading-page">
        <svg id="svg" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1500 600">
          <path className="letter" d="M0 0v600h100V0H0z" />
          <path
            className="letter"
            d="M130 0v600h100V0h-100z
               M130 0h220v100H130z
               M130 250h160v100H130z
               M130 500h220v100H130z"
          />
          <path className="letter" d="M430 0v600h100V0h-100z" />
          <path
            className="letter"
            d="M560 0v600h100V0h-100z
               M560 0h220v200H560z"
          />
          <path
            className="letter"
            d="M860 0v600h100V0h-100z
               M860 500h220v100H860z"
          />
        </svg>

        <div className="name-container">
          <div className="logo-name">Iyappan Engineering Industries Pvt. Ltd.</div>
        </div>
      </div>

      {/* Welcome text container */}
      <div className="container">
        <p style={{ fontSize: "2.5rem", fontWeight: "bold", textAlign: "center", color: "#1a3a8a" }}>
        </p>
      </div>
    </>
  );
}

import React, { useState, useEffect } from "react";

const Greeting: React.FC = () => {
  const [greeting, setGreeting] = useState("");

  useEffect(() => {
    const updateGreeting = () => {
      const hour = new Date().getHours();
      if (hour >= 5 && hour < 12) {
        setGreeting("Good Morning");
      } else if (hour >= 12 && hour < 17) {
        setGreeting("Good Noon");
      } else {
        setGreeting("Good Evening");
      }
    };

    updateGreeting();
    const interval = setInterval(updateGreeting, 60 * 1000);
    return () => clearInterval(interval);
  }, []);

  return (
    <h1
      style={{
        fontSize: "2.5rem",
    color: "#0d47a1",   // dark blue
        textAlign: "center",
        marginBottom: "1.5rem",
        fontWeight: 700,
        fontFamily: "'Baskerville', serif", // changed font style
      }}
    >
      {greeting}, Welcome to the Dashboard!
    </h1>
  );
};

export default Greeting;

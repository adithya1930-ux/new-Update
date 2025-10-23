import { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import Chart from "chart.js/auto";

export default function Dashboard() {
  const [showCharts, setShowCharts] = useState(false);
  const [greeting, setGreeting] = useState("");

  const poChartRef = useRef<HTMLCanvasElement | null>(null);
  const soChartRef = useRef<HTMLCanvasElement | null>(null);
  const woChartRef = useRef<HTMLCanvasElement | null>(null);
  const lineChartRef = useRef<HTMLCanvasElement | null>(null);

  // Stats data
  const stats = [
    { title: "Total Orders", value: 120 },
    { title: "Pending", value: 35 },
    { title: "Shipped", value: 65 },
    { title: "Delivered", value: 20 },
  ];

  // Determine greeting based on time
  useEffect(() => {
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 12) setGreeting("Good Morning");
    else if (hour >= 12 && hour < 17) setGreeting("Good Afternoon");
    else setGreeting("Good Evening");
  }, []);

  // Initialize charts after welcome animation
  useEffect(() => {
    if (!showCharts) return;

    const charts: { [key: string]: Chart } = {};

    if (poChartRef.current) {
      charts["po"] = new Chart(poChartRef.current, {
        type: "pie",
        data: {
          labels: ["Approved", "Pending", "Rejected"],
          datasets: [{ data: [20, 10, 5], backgroundColor: ["#4e9af1", "#3498db", "#e74c3c"] }],
        },
        options: { plugins: { legend: { position: "bottom" } }, animation: { animateScale: true } },
      });
    }

    if (soChartRef.current) {
      charts["so"] = new Chart(soChartRef.current, {
        type: "doughnut",
        data: {
          labels: ["Completed", "Processing", "Cancelled"],
          datasets: [{ data: [30, 15, 8], backgroundColor: ["#2ecc71", "#2980b9", "#e67e22"] }],
        },
        options: { plugins: { legend: { position: "bottom" } }, animation: { animateScale: true } },
      });
    }

    if (woChartRef.current) {
      charts["wo"] = new Chart(woChartRef.current, {
        type: "bar",
        data: {
          labels: ["Completed", "In Progress", "Delayed"],
          datasets: [{ label: "Work Orders", data: [25, 12, 6], backgroundColor: ["#1abc9c", "#3498db", "#c0392b"] }],
        },
        options: {
          plugins: { legend: { display: false } },
          scales: { y: { beginAtZero: true } },
          animation: { duration: 1500, easing: "easeOutBounce" },
        },
      });
    }

    if (lineChartRef.current) {
      charts["line"] = new Chart(lineChartRef.current, {
        type: "line",
        data: {
          labels: ["Week 1", "Week 2", "Week 3", "Week 4"],
          datasets: [
            {
              label: "Total Orders",
              data: [40, 60, 90, 120],
              borderColor: "#2980b9",
              backgroundColor: "rgba(41, 128, 185, 0.2)",
              fill: true,
              tension: 0.4,
              pointBackgroundColor: "#2980b9",
              pointRadius: 6,
              pointHoverRadius: 10,
            },
          ],
        },
        options: { plugins: { legend: { position: "bottom" } }, animation: { duration: 1200 } },
      });
    }

    return () => Object.values(charts).forEach(c => c.destroy());
  }, [showCharts]);

  return (
    <div className="dashboard-content">
      {/* Welcome text */}
      <motion.div
        className="dashboard-welcome"
        initial={{ scale: 0, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 1.2, ease: "easeOut" }}
        onAnimationComplete={() => setShowCharts(true)}
      >
        <h1 style={{ fontSize: "3rem", color: "#1a3a8a", fontWeight: "bold", textAlign: "center" }}>
          {greeting}, Welcome to Dashboard!
        </h1>
      </motion.div>

      {/* Stats Cards */}
      {showCharts && (
        <motion.section
          className="cards"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 1 }}
        >
          {stats.map((stat, idx) => (
            <div className="card" key={idx}>
              <h3>{stat.title}</h3>
              <p>{stat.value}</p>
            </div>
          ))}
        </motion.section>
      )}

      {/* Charts section */}
      {showCharts && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 1 }}
        >
          <section className="cards">
            <div className="card">
              <h3>Purchase Orders</h3>
              <canvas ref={poChartRef} />
            </div>
            <div className="card">
              <h3>Sales Orders</h3>
              <canvas ref={soChartRef} />
            </div>
            <div className="card">
              <h3>Work Orders</h3>
              <canvas ref={woChartRef} />
            </div>
          </section>

          <section className="chart">
            <h2>Total Orders This Month</h2>
            <canvas ref={lineChartRef}></canvas>
          </section>
        </motion.div>
      )}
    </div>
  );
}

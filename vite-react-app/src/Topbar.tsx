import { useLocation } from "react-router-dom";

export default function Topbar() {
  const location = useLocation();
  const pageTitle = location.pathname === "/orders" ? "Orders" : "Dashboard";

  return (
    <header className="topbar">
      <h1>{pageTitle}</h1>
      <div className="user">👤 Admin</div>
    </header>
  );
}

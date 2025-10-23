import { useState } from "react";
import { ThreeDots } from "react-loader-spinner";

function Orders() {
  const [orders, setOrders] = useState([
    { id: "ORD-001", customer: "John", status: "Completed", amount: "$120" },
    { id: "ORD-002", customer: "Alice", status: "Pending", amount: "$200" },
    { id: "ORD-003", customer: "Mike", status: "Cancelled", amount: "$90" },
  ]);

  const [loadingId, setLoadingId] = useState<string | null>(null);

  const handleClick = (id: string) => {
    setLoadingId(id);

    setTimeout(() => {
      // Update the order status dynamically
      setOrders((prevOrders) =>
        prevOrders.map((o) =>
          o.id === id
            ? {
                ...o,
                status:
                  o.status === "Pending"
                    ? "Completed"
                    : o.status === "Completed"
                    ? "Completed"
                    : o.status,
              }
            : o
        )
      );
      setLoadingId(null);
    }, 1500); // simulate API call
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "Completed":
        return "green";
      case "Pending":
        return "orange";
      case "Cancelled":
        return "red";
      default:
        return "gray";
    }
  };

  return (
    <div className="orders-page">
      <h2>Orders List</h2>
      <table className="orders-table">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Customer</th>
            <th>Status</th>
            <th>Amount</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.customer}</td>
              <td style={{ color: getStatusColor(o.status), fontWeight: "600" }}>
                {o.status}
              </td>
              <td>{o.amount}</td>
              <td>
                <button
                  onClick={() => handleClick(o.id)}
                  className="btn"
                  disabled={loadingId === o.id || o.status === "Completed"}
                  style={{ cursor: loadingId === o.id ? "not-allowed" : "pointer" }}
                >
                  {loadingId === o.id ? (
                    <ThreeDots height="20" width="30" color="#fff" />
                  ) : o.status === "Completed" ? (
                    "✓ Done"
                  ) : (
                    "Process"
                  )}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Orders;

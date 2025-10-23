import { useEffect, useState, useCallback } from "react";
import { useApi } from "./ApiContext";
import { pipe } from "fp-ts/lib/function";
import * as E from "fp-ts/lib/Either";


interface QuoteData {
  label: string;
  value: number;
}

const QuoteItemAmounts: React.FC = () => {
  const { get: apiGet } = useApi();
  const [quoteData, setQuoteData] = useState<QuoteData[]>([]);
  const [totalAmount, setTotalAmount] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const result = await pipe(apiGet("/services/ListQuoteItemAmounts"))();
      if (E.isLeft(result)) throw new Error(result.left.message);

      const json = await result.right.json();

      const list: any[] = Array.isArray(json.data?.quoteItemAmounts)
        ? json.data.quoteItemAmounts
        : Array.isArray(json)
        ? json
        : [];

      const mappedData: QuoteData[] = list.map((item: any, idx: number) => ({
        label: item.label || `Quote ${idx + 1}`,
        value: item.value ?? 0,
      }));

      setQuoteData(mappedData);

      const total: number =
        typeof json.data?.totalQuoteAmount === "number"
          ? json.data.totalQuoteAmount
          : mappedData.reduce((sum: number, item: QuoteData) => sum + item.value, 0);

      setTotalAmount(total);
    } catch (err: any) {
      console.error(err);
      setError(err?.message || "Error fetching quote amounts");
    } finally {
      setLoading(false);
    }
  }, [apiGet]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  if (loading) return <div>Loading Quote Item Amounts...</div>;
  if (error) return <div style={{ color: "red" }}>{error}</div>;

  return (
    <div className="stat-box">
      <h2 className="stat-title">Quote Item Amounts</h2>
<div
  style={{
    fontSize: "1.8rem",          // same as .stat-value
    fontWeight: 700,
    color: "#0d9488",            // teal color from your CSS
    marginTop: "0.5rem",
    transition: "transform 0.3s ease, color 0.3s ease",
    cursor: "default",
    display: "inline-block",
  }}
  onMouseEnter={(e) => {
    (e.currentTarget as HTMLDivElement).style.transform = "scale(1.15)";
    (e.currentTarget as HTMLDivElement).style.color = "#7c3aed"; // violet hover
  }}
  onMouseLeave={(e) => {
    (e.currentTarget as HTMLDivElement).style.transform = "scale(1)";
    (e.currentTarget as HTMLDivElement).style.color = "#0d9488"; // revert teal
  }}
>
  Total Quote Amount: {totalAmount.toLocaleString("en-US", {
    style: "currency",
    currency: "USD",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}
</div>


      {quoteData.length > 0 && (
        <table className="stat-table">
          <thead>
            <tr>
              <th>Label</th>
              <th>Amount</th>
            </tr>
          </thead>
          <tbody>
            {quoteData.map((item, idx) => (
              <tr key={idx}>
                <td>{item.label}</td>
                <td>{item.value}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default QuoteItemAmounts;

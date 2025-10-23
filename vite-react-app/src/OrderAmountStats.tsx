import { useEffect, useState, useCallback } from "react";
import { useApi } from "./ApiContext";
import { pipe } from "fp-ts/lib/function";
import * as E from "fp-ts/lib/Either";

interface AmountData {
  label: string;
  value: number;
}

const OrderAmountStats: React.FC = () => {
  const { get: apiGet } = useApi();
  const [amountData, setAmountData] = useState<AmountData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const result = await pipe(apiGet("/services/ListOrderAmounts"))();
      if (E.isLeft(result)) throw new Error(result.left.message);

      const json = await result.right.json();
      const list = json.data?.orderAmountList || [];

      const mappedData = list.map((item: any, idx: number) => ({
        label: item.label || `Order ${idx + 1}`,
        value: item.value ?? 0,
      }));

      setAmountData(mappedData);
    } catch (err: any) {
      console.error(err);
      setError(err.message || "Error fetching order amounts");
    } finally {
      setLoading(false);
    }
  }, [apiGet]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  if (loading) return <div>Loading Order Amounts...</div>;
  if (error) return <div style={{ color: "red" }}>{error}</div>;

  return (
    <div className="stat-box">
      <h2 className="stat-title">Order Amounts</h2>
      <div className="stat-inner">
        {amountData.length > 0 ? (
          amountData.map((item, idx) => (
            <div className="stat-column" key={idx}>
              <p className="stat-label">{item.label}</p>
              <p className="stat-value">{item.value}</p>
            </div>
          ))
        ) : (
          <div>No data available</div>
        )}
      </div>
    </div>
  );
};

export default OrderAmountStats;

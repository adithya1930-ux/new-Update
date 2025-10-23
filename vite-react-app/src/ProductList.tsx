import { useEffect, useState } from "react";
import { useApi } from "./ApiContext";  // adjust path if ApiContext is in another folder

function ProductList() {
  const api = useApi();
  const [products, setProducts] = useState<string[]>([]);

  useEffect(() => {
    const fetchProducts = async () => {
      const task = api.get("entity/Product?fields=productId"); 
      const result = await task(); // run TaskEither

      if (result._tag === "Right") {
        const data = await result.right.json();
        // OFBiz REST returns: { count, data: [ {productId: "XXX"}, ... ] }
        setProducts(data.data.map((p: any) => p.productId));
      } else {
        console.error("Error fetching products:", result.left);
      }
    };

    fetchProducts();
  }, [api]);

  return (
    <div>
      <h2 className="text-xl font-bold">Product IDs</h2>
      <ul>
        {products.map((id) => (
          <li key={id}>{id}</li>
        ))}
      </ul>
    </div>
  );
}

export default ProductList;

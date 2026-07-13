"use client";

import { getStocks } from "@/lib/api";
import { usePolling } from "@/lib/usePolling";

export function StockTable() {
  const { data: stocks, error } = usePolling(getStocks);

  return (
    <section className="rounded-lg border border-black/10 p-4 dark:border-white/15">
      <h2 className="mb-3 font-semibold">종목</h2>
      {error && <p className="text-sm text-red-600 dark:text-red-400">{error}</p>}
      {!error && !stocks && <p className="text-sm text-zinc-500">불러오는 중...</p>}
      {stocks && (
        <table className="w-full text-sm">
          <thead className="text-left text-zinc-500">
            <tr>
              <th className="pb-2">코드</th>
              <th className="pb-2">이름</th>
              <th className="pb-2 text-right">현재가</th>
              <th className="pb-2 text-right">거래량</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map((stock) => (
              <tr key={stock.id} className="border-t border-black/5 dark:border-white/10">
                <td className="py-1.5">{stock.code}</td>
                <td className="py-1.5">{stock.name}</td>
                <td className="py-1.5 text-right">{stock.currentPrice.toLocaleString()}</td>
                <td className="py-1.5 text-right">{stock.volume.toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

"use client";

import { getOrders } from "@/lib/api";
import { usePolling } from "@/lib/usePolling";

export function OrderFeed() {
  const { data: orders, error } = usePolling(getOrders);

  return (
    <section className="rounded-lg border border-black/10 p-4 dark:border-white/15">
      <h2 className="mb-3 font-semibold">체결 내역</h2>
      {error && <p className="text-sm text-red-600 dark:text-red-400">{error}</p>}
      {!error && !orders && <p className="text-sm text-zinc-500">불러오는 중...</p>}
      {orders && orders.length === 0 && (
        <p className="text-sm text-zinc-500">아직 체결된 주문이 없습니다.</p>
      )}
      {orders && orders.length > 0 && (
        <table className="w-full text-sm">
          <thead className="text-left text-zinc-500">
            <tr>
              <th className="pb-2">에이전트</th>
              <th className="pb-2">종목</th>
              <th className="pb-2">구분</th>
              <th className="pb-2 text-right">수량</th>
              <th className="pb-2 text-right">체결가</th>
            </tr>
          </thead>
          <tbody>
            {[...orders]
              .sort((a, b) => b.id - a.id)
              .slice(0, 20)
              .map((order) => (
                <tr key={order.id} className="border-t border-black/5 dark:border-white/10">
                  <td className="py-1.5">{order.agentName}</td>
                  <td className="py-1.5">{order.stockCode}</td>
                  <td
                    className={`py-1.5 ${
                      order.type === "BUY"
                        ? "text-red-600 dark:text-red-400"
                        : "text-blue-600 dark:text-blue-400"
                    }`}
                  >
                    {order.type === "BUY" ? "매수" : "매도"}
                  </td>
                  <td className="py-1.5 text-right">{order.quantity.toLocaleString()}</td>
                  <td className="py-1.5 text-right">{order.price.toLocaleString()}</td>
                </tr>
              ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

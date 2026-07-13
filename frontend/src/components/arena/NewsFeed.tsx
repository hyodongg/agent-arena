"use client";

import { getNews } from "@/lib/api";
import { usePolling } from "@/lib/usePolling";

export function NewsFeed() {
  const { data: news, error } = usePolling(getNews);

  return (
    <section className="rounded-lg border border-black/10 p-4 dark:border-white/15">
      <h2 className="mb-3 font-semibold">뉴스</h2>
      {error && <p className="text-sm text-red-600 dark:text-red-400">{error}</p>}
      {!error && !news && <p className="text-sm text-zinc-500">불러오는 중...</p>}
      {news && news.length === 0 && (
        <p className="text-sm text-zinc-500">아직 주입된 뉴스가 없습니다.</p>
      )}
      {news && news.length > 0 && (
        <ul className="flex flex-col gap-2 text-sm">
          {news.map((item) => (
            <li key={item.id} className="flex items-center gap-2 border-t border-black/5 pt-2 first:border-t-0 first:pt-0 dark:border-white/10">
              <span
                className={`shrink-0 rounded px-1.5 py-0.5 text-xs ${
                  item.sentiment === "POSITIVE"
                    ? "bg-red-100 text-red-700 dark:bg-red-950 dark:text-red-300"
                    : "bg-blue-100 text-blue-700 dark:bg-blue-950 dark:text-blue-300"
                }`}
              >
                {item.relatedStockCode}
              </span>
              <span className="flex-1">{item.title}</span>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

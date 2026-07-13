"use client";

import { getBettings } from "@/lib/api";
import { usePolling } from "@/lib/usePolling";
import type { BettingStatus } from "@/lib/types";

const STATUS_LABEL: Record<BettingStatus, string> = {
  IN_PROGRESS: "진행중",
  WON: "적중",
  LOST: "미적중",
};

const STATUS_CLASS: Record<BettingStatus, string> = {
  IN_PROGRESS: "text-zinc-500",
  WON: "text-red-600 dark:text-red-400",
  LOST: "text-blue-600 dark:text-blue-400",
};

export function MyBettings({ userId }: { userId: number }) {
  const { data: bettings, error } = usePolling(() => getBettings(userId));

  return (
    <section className="rounded-lg border border-black/10 p-4 dark:border-white/15">
      <h2 className="mb-3 font-semibold">내 배팅</h2>
      {error && <p className="text-sm text-red-600 dark:text-red-400">{error}</p>}
      {!error && !bettings && <p className="text-sm text-zinc-500">불러오는 중...</p>}
      {bettings && bettings.length === 0 && (
        <p className="text-sm text-zinc-500">아직 배팅 내역이 없습니다.</p>
      )}
      {bettings && bettings.length > 0 && (
        <table className="w-full text-sm">
          <thead className="text-left text-zinc-500">
            <tr>
              <th className="pb-2">회차</th>
              <th className="pb-2">에이전트</th>
              <th className="pb-2 text-right">금액</th>
              <th className="pb-2 text-right">상태</th>
            </tr>
          </thead>
          <tbody>
            {bettings.map((betting) => (
              <tr key={betting.id} className="border-t border-black/5 dark:border-white/10">
                <td className="py-1.5">{betting.round}</td>
                <td className="py-1.5">{betting.agentName}</td>
                <td className="py-1.5 text-right">{betting.amount.toLocaleString()}</td>
                <td className={`py-1.5 text-right ${STATUS_CLASS[betting.status]}`}>
                  {STATUS_LABEL[betting.status]}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

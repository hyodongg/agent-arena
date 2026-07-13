"use client";

import { useState } from "react";
import { ApiError, createBetting, getAgents } from "@/lib/api";
import { usePolling } from "@/lib/usePolling";
import type { AgentResponse } from "@/lib/types";

function BetForm({ agent, userId }: { agent: AgentResponse; userId: number }) {
  const [open, setOpen] = useState(false);
  const [round, setRound] = useState("1");
  const [amount, setAmount] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSubmitting(true);
    try {
      await createBetting({
        userId,
        agentId: agent.id,
        round: Number(round),
        amount: Number(amount),
      });
      setSuccess("배팅 완료");
      setAmount("");
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "배팅에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  }

  if (!open) {
    return (
      <button
        onClick={() => setOpen(true)}
        className="rounded-md border border-black/10 px-2 py-1 text-xs hover:bg-black/[.04] dark:border-white/15 dark:hover:bg-[#1a1a1a]"
      >
        배팅
      </button>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-1">
      <div className="flex items-center gap-1">
        <input
          type="number"
          value={round}
          onChange={(e) => setRound(e.target.value)}
          placeholder="회차"
          min={1}
          required
          className="w-14 rounded-md border border-black/10 px-1.5 py-1 text-xs outline-none dark:border-white/15"
        />
        <input
          type="number"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="금액"
          min={1}
          required
          className="w-20 rounded-md border border-black/10 px-1.5 py-1 text-xs outline-none dark:border-white/15"
        />
        <button
          type="submit"
          disabled={submitting}
          className="rounded-md bg-foreground px-2 py-1 text-xs text-background disabled:opacity-50"
        >
          확인
        </button>
      </div>
      {error && <p className="text-xs text-red-600 dark:text-red-400">{error}</p>}
      {success && <p className="text-xs text-green-600 dark:text-green-400">{success}</p>}
    </form>
  );
}

export function AgentTable({ userId }: { userId: number }) {
  const { data: agents, error } = usePolling(getAgents);

  return (
    <section className="rounded-lg border border-black/10 p-4 dark:border-white/15">
      <h2 className="mb-3 font-semibold">에이전트</h2>
      {error && <p className="text-sm text-red-600 dark:text-red-400">{error}</p>}
      {!error && !agents && <p className="text-sm text-zinc-500">불러오는 중...</p>}
      {agents && (
        <table className="w-full text-sm">
          <thead className="text-left text-zinc-500">
            <tr>
              <th className="pb-2">이름</th>
              <th className="pb-2">소유자</th>
              <th className="pb-2 text-right">누적수익률</th>
              <th className="pb-2 text-right">현금잔고</th>
              <th className="pb-2 text-right">초기자본</th>
              <th className="pb-2"></th>
            </tr>
          </thead>
          <tbody>
            {agents.map((agent) => (
              <tr key={agent.id} className="border-t border-black/5 align-top dark:border-white/10">
                <td className="py-1.5">{agent.name}</td>
                <td className="py-1.5">{agent.ownerUsername}</td>
                <td
                  className={`py-1.5 text-right ${
                    agent.cumulativeReturn > 0
                      ? "text-red-600 dark:text-red-400"
                      : agent.cumulativeReturn < 0
                        ? "text-blue-600 dark:text-blue-400"
                        : ""
                  }`}
                >
                  {agent.cumulativeReturn.toFixed(2)}%
                </td>
                <td className="py-1.5 text-right">{agent.cashBalance.toLocaleString()}</td>
                <td className="py-1.5 text-right">{agent.initialCapital.toLocaleString()}</td>
                <td className="py-1.5 text-right">
                  <BetForm agent={agent} userId={userId} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

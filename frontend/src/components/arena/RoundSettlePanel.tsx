"use client";

import { useState } from "react";
import { ApiError, settleRound } from "@/lib/api";
import type { RoundSettleResponse } from "@/lib/types";

export function RoundSettlePanel() {
  const [round, setRound] = useState("1");
  const [result, setResult] = useState<RoundSettleResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      const response = await settleRound(Number(round));
      setResult(response);
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "정산에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="rounded-lg border border-black/10 p-4 dark:border-white/15">
      <h2 className="mb-3 font-semibold">라운드 정산</h2>
      <form onSubmit={handleSubmit} className="flex items-center gap-2">
        <input
          type="number"
          value={round}
          onChange={(e) => setRound(e.target.value)}
          min={1}
          required
          className="w-20 rounded-md border border-black/10 px-2 py-1 text-sm outline-none dark:border-white/15"
        />
        <button
          type="submit"
          disabled={submitting}
          className="rounded-md bg-foreground px-3 py-1.5 text-sm text-background disabled:opacity-50"
        >
          정산
        </button>
      </form>
      {error && <p className="mt-2 text-sm text-red-600 dark:text-red-400">{error}</p>}
      {result && (
        <p className="mt-2 text-sm text-zinc-600 dark:text-zinc-400">
          {result.round}회차 승자: <span className="font-medium">{result.winningAgentName}</span>{" "}
          (적중 {result.wonCount}건 / 미적중 {result.lostCount}건)
        </p>
      )}
    </section>
  );
}

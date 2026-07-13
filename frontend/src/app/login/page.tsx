"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { setUser } from "@/lib/auth";
import { ApiError, enterUser } from "@/lib/api";

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsernameInput] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!username.trim()) return;

    setSubmitting(true);
    setError(null);
    try {
      const user = await enterUser(username.trim());
      setUser(user);
      router.push("/arena");
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "로그인에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="flex flex-1 items-center justify-center px-6">
      <form
        onSubmit={handleSubmit}
        className="flex w-full max-w-sm flex-col gap-4 rounded-lg border border-black/10 p-8 dark:border-white/15"
      >
        <h1 className="text-xl font-semibold">가상 ID로 입장</h1>
        <p className="text-sm text-zinc-500 dark:text-zinc-400">
          실제 인증 없이 가상 ID만 입력하면 아레나에 입장합니다.
        </p>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsernameInput(e.target.value)}
          placeholder="가상 ID"
          className="rounded-md border border-black/10 px-3 py-2 outline-none focus:border-black/30 dark:border-white/15 dark:focus:border-white/40"
          autoFocus
        />
        {error && <p className="text-sm text-red-600 dark:text-red-400">{error}</p>}
        <button
          type="submit"
          disabled={submitting}
          className="rounded-md bg-foreground px-3 py-2 font-medium text-background transition-colors hover:bg-[#383838] disabled:opacity-50 dark:hover:bg-[#ccc]"
        >
          {submitting ? "입장 중..." : "입장하기"}
        </button>
      </form>
    </div>
  );
}

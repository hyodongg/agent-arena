"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { setUser } from "@/lib/auth";
import { ApiError, loginUser, signupUser } from "@/lib/api";

type Mode = "login" | "signup";

export default function LoginPage() {
  const router = useRouter();
  const [mode, setMode] = useState<Mode>("login");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!username.trim() || !password) return;

    setSubmitting(true);
    setError(null);
    try {
      const user =
        mode === "login"
          ? await loginUser(username.trim(), password)
          : await signupUser(username.trim(), password);
      setUser(user);
      router.push("/arena");
    } catch (e) {
      setError(
        e instanceof ApiError
          ? e.message
          : mode === "login"
            ? "로그인에 실패했습니다."
            : "회원가입에 실패했습니다."
      );
    } finally {
      setSubmitting(false);
    }
  }

  function switchMode(next: Mode) {
    setMode(next);
    setError(null);
  }

  return (
    <div className="flex flex-1 items-center justify-center px-6">
      <form
        onSubmit={handleSubmit}
        className="flex w-full max-w-sm flex-col gap-4 rounded-lg border border-black/10 p-8 dark:border-white/15"
      >
        <div className="flex gap-2 text-sm">
          <button
            type="button"
            onClick={() => switchMode("login")}
            className={`rounded-md px-3 py-1.5 font-medium transition-colors ${
              mode === "login"
                ? "bg-foreground text-background"
                : "text-zinc-500 hover:text-foreground dark:text-zinc-400"
            }`}
          >
            로그인
          </button>
          <button
            type="button"
            onClick={() => switchMode("signup")}
            className={`rounded-md px-3 py-1.5 font-medium transition-colors ${
              mode === "signup"
                ? "bg-foreground text-background"
                : "text-zinc-500 hover:text-foreground dark:text-zinc-400"
            }`}
          >
            회원가입
          </button>
        </div>
        <h1 className="text-xl font-semibold">
          {mode === "login" ? "로그인" : "회원가입"}
        </h1>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="아이디"
          className="rounded-md border border-black/10 px-3 py-2 outline-none focus:border-black/30 dark:border-white/15 dark:focus:border-white/40"
          autoFocus
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder={mode === "signup" ? "비밀번호 (8자 이상)" : "비밀번호"}
          className="rounded-md border border-black/10 px-3 py-2 outline-none focus:border-black/30 dark:border-white/15 dark:focus:border-white/40"
        />
        {error && <p className="text-sm text-red-600 dark:text-red-400">{error}</p>}
        <button
          type="submit"
          disabled={submitting}
          className="rounded-md bg-foreground px-3 py-2 font-medium text-background transition-colors hover:bg-[#383838] disabled:opacity-50 dark:hover:bg-[#ccc]"
        >
          {submitting
            ? mode === "login"
              ? "로그인 중..."
              : "가입 중..."
            : mode === "login"
              ? "로그인"
              : "회원가입"}
        </button>
      </form>
    </div>
  );
}

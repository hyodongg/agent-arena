"use client";

import { useEffect, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { clearUsername, getUsername } from "@/lib/auth";

function subscribe(callback: () => void) {
  window.addEventListener("storage", callback);
  return () => window.removeEventListener("storage", callback);
}

function getServerSnapshot() {
  return null;
}

export default function ArenaPage() {
  const router = useRouter();
  const username = useSyncExternalStore(subscribe, getUsername, getServerSnapshot);

  useEffect(() => {
    if (!username) {
      router.replace("/login");
    }
  }, [username, router]);

  if (!username) return null;

  return (
    <div className="flex flex-1 flex-col items-center gap-4 px-6 py-16">
      <h1 className="text-2xl font-semibold">환영합니다, {username}님</h1>
      <p className="text-zinc-500 dark:text-zinc-400">
        에이전트 목록, 뉴스, 배팅 UI는 백엔드 API 연동 이후 채워질 예정입니다.
      </p>
      <button
        onClick={() => {
          clearUsername();
          router.push("/login");
        }}
        className="rounded-md border border-black/10 px-3 py-2 text-sm transition-colors hover:bg-black/[.04] dark:border-white/15 dark:hover:bg-[#1a1a1a]"
      >
        로그아웃
      </button>
    </div>
  );
}

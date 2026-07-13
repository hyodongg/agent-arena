"use client";

import { useEffect, useMemo, useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { getRawUser } from "@/lib/auth";
import { ArenaDashboard } from "@/components/arena/ArenaDashboard";
import type { UserResponse } from "@/lib/types";

function subscribe(callback: () => void) {
  window.addEventListener("storage", callback);
  return () => window.removeEventListener("storage", callback);
}

function getServerSnapshot() {
  return null;
}

export default function ArenaPage() {
  const router = useRouter();
  const rawUser = useSyncExternalStore(subscribe, getRawUser, getServerSnapshot);
  const user: UserResponse | null = useMemo(
    () => (rawUser ? JSON.parse(rawUser) : null),
    [rawUser],
  );

  useEffect(() => {
    if (!user) {
      router.replace("/login");
    }
  }, [user, router]);

  if (!user) return null;

  return <ArenaDashboard initialUser={user} />;
}

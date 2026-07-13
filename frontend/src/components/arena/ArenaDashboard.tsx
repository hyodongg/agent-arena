"use client";

import { useRouter } from "next/navigation";
import { clearUser } from "@/lib/auth";
import { getUserById } from "@/lib/api";
import { usePolling } from "@/lib/usePolling";
import type { UserResponse } from "@/lib/types";
import { StockTable } from "./StockTable";
import { AgentTable } from "./AgentTable";
import { NewsFeed } from "./NewsFeed";
import { OrderFeed } from "./OrderFeed";
import { MyBettings } from "./MyBettings";
import { RoundSettlePanel } from "./RoundSettlePanel";

export function ArenaDashboard({ initialUser }: { initialUser: UserResponse }) {
  const router = useRouter();
  const { data: liveUser } = usePolling(() => getUserById(initialUser.id));
  const user = liveUser ?? initialUser;

  return (
    <div className="flex flex-1 flex-col gap-6 px-6 py-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">{user.username}님</h1>
          <p className="text-sm text-zinc-500 dark:text-zinc-400">
            보유 토큰: {user.tokenBalance.toLocaleString()}
          </p>
        </div>
        <button
          onClick={() => {
            clearUser();
            router.push("/login");
          }}
          className="rounded-md border border-black/10 px-3 py-2 text-sm transition-colors hover:bg-black/[.04] dark:border-white/15 dark:hover:bg-[#1a1a1a]"
        >
          로그아웃
        </button>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <StockTable />
        <AgentTable userId={user.id} />
        <NewsFeed />
        <OrderFeed />
        <MyBettings userId={user.id} />
        <RoundSettlePanel />
      </div>
    </div>
  );
}

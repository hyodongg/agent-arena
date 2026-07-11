import Link from "next/link";

export default function Home() {
  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-6 px-6 text-center">
      <h1 className="max-w-lg text-3xl font-semibold leading-tight">
        AI 에이전트들의 초단타 매매 경쟁을 관람하고 배팅하세요
      </h1>
      <p className="max-w-md text-zinc-500 dark:text-zinc-400">
        가상 토큰으로 승률 높은 에이전트에 배팅하는 AI 매니지드 엔터테인먼트
        시뮬레이션입니다.
      </p>
      <Link
        href="/login"
        className="rounded-full bg-foreground px-6 py-3 font-medium text-background transition-colors hover:bg-[#383838] dark:hover:bg-[#ccc]"
      >
        시작하기
      </Link>
    </div>
  );
}

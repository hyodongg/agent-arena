import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import Link from "next/link";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Agent Arena",
  description: "AI 에이전트 초단타 매매 배팅 시뮬레이션",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="ko"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col">
        <header className="flex items-center justify-between border-b border-black/10 px-6 py-4 dark:border-white/15">
          <Link href="/" className="font-semibold">
            Agent Arena
          </Link>
          <nav className="flex gap-4 text-sm text-zinc-500 dark:text-zinc-400">
            <Link href="/" className="hover:text-foreground">
              홈
            </Link>
            <Link href="/login" className="hover:text-foreground">
              로그인
            </Link>
            <Link href="/arena" className="hover:text-foreground">
              아레나
            </Link>
          </nav>
        </header>
        {children}
      </body>
    </html>
  );
}

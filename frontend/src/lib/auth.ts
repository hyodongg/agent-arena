import type { UserResponse } from "./types";

const USER_KEY = "agent-arena-user";

// Returns the raw string so it stays reference-stable for useSyncExternalStore
// (a freshly-parsed object would differ on every call and re-render forever).
export function getRawUser(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(USER_KEY);
}

export function getUser(): UserResponse | null {
  const raw = getRawUser();
  if (!raw) return null;
  return JSON.parse(raw);
}

export function setUser(user: UserResponse) {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearUser() {
  localStorage.removeItem(USER_KEY);
}

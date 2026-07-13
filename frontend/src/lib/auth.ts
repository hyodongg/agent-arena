const USERNAME_KEY = "agent-arena-username";

export function getUsername(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(USERNAME_KEY);
}

export function setUsername(username: string) {
  localStorage.setItem(USERNAME_KEY, username);
}

export function clearUsername() {
  localStorage.removeItem(USERNAME_KEY);
}

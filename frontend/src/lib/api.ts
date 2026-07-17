import type {
  AgentResponse,
  BettingResponse,
  ErrorResponse,
  NewsResponse,
  OrderResponse,
  RoundSettleResponse,
  StockResponse,
  UserResponse,
} from "./types";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8081";

export class ApiError extends Error {
  code: string;

  constructor(errorResponse: ErrorResponse) {
    super(errorResponse.message);
    this.code = errorResponse.code;
  }
}

async function apiFetch<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: { "Content-Type": "application/json", ...options?.headers },
  });

  if (!res.ok) {
    const errorResponse: ErrorResponse = await res.json();
    throw new ApiError(errorResponse);
  }

  if (res.status === 204) return undefined as T;
  return res.json();
}

export function signupUser(
  username: string,
  password: string
): Promise<UserResponse> {
  return apiFetch<UserResponse>("/api/users/signup", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

export function loginUser(
  username: string,
  password: string
): Promise<UserResponse> {
  return apiFetch<UserResponse>("/api/users/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

export function getUserById(id: number): Promise<UserResponse> {
  return apiFetch<UserResponse>(`/api/users/${id}`);
}

export function getAgents(): Promise<AgentResponse[]> {
  return apiFetch<AgentResponse[]>("/api/agents");
}

export function getStocks(): Promise<StockResponse[]> {
  return apiFetch<StockResponse[]>("/api/stocks");
}

export function getNews(): Promise<NewsResponse[]> {
  return apiFetch<NewsResponse[]>("/api/news");
}

export function getOrders(): Promise<OrderResponse[]> {
  return apiFetch<OrderResponse[]>("/api/orders");
}

export function getBettings(userId: number): Promise<BettingResponse[]> {
  return apiFetch<BettingResponse[]>(`/api/bettings?userId=${userId}`);
}

export function createBetting(request: {
  userId: number;
  agentId: number;
  round: number;
  amount: number;
}): Promise<BettingResponse> {
  return apiFetch<BettingResponse>("/api/bettings", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export function settleRound(round: number): Promise<RoundSettleResponse> {
  return apiFetch<RoundSettleResponse>(`/api/bettings/rounds/${round}/settle`, {
    method: "POST",
  });
}

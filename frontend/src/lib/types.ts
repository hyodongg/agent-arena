export interface UserResponse {
  id: number;
  username: string;
  tokenBalance: number;
}

export interface AgentResponse {
  id: number;
  ownerId: number;
  ownerUsername: string;
  name: string;
  investmentPrompt: string;
  cumulativeReturn: number;
  cashBalance: number;
  initialCapital: number;
}

export interface StockResponse {
  id: number;
  code: string;
  name: string;
  currentPrice: number;
  volume: number;
}

export type NewsSentiment = "POSITIVE" | "NEGATIVE";

export interface NewsResponse {
  id: number;
  relatedStockId: number;
  relatedStockCode: string;
  title: string;
  sentiment: NewsSentiment;
  publishedAt: string;
  injectedAt: string | null;
}

export type OrderType = "BUY" | "SELL";

export interface OrderResponse {
  id: number;
  agentId: number;
  agentName: string;
  stockId: number;
  stockCode: string;
  type: OrderType;
  quantity: number;
  price: number;
  executedAt: string;
}

export type BettingStatus = "IN_PROGRESS" | "WON" | "LOST";

export interface BettingResponse {
  id: number;
  userId: number;
  username: string;
  agentId: number;
  agentName: string;
  round: number;
  amount: number;
  status: BettingStatus;
}

export interface RoundSettleResponse {
  round: number;
  winningAgentId: number;
  winningAgentName: string;
  wonCount: number;
  lostCount: number;
}

export interface ErrorResponse {
  code: string;
  message: string;
}

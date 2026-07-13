package com.agentarena.backend.domain.agent;

public enum AgentStyle {

    SCALPER(1.0, 1.0),
    LONG_TERM(0.2, 2.0),
    AGGRESSIVE(1.0, 3.0),
    CONSERVATIVE(0.5, 0.5),
    NEUTRAL(1.0, 1.0);

    private final double reactionProbability;
    private final double notionalMultiplier;

    AgentStyle(double reactionProbability, double notionalMultiplier) {
        this.reactionProbability = reactionProbability;
        this.notionalMultiplier = notionalMultiplier;
    }

    public double getReactionProbability() {
        return reactionProbability;
    }

    public double getNotionalMultiplier() {
        return notionalMultiplier;
    }

    public static AgentStyle classify(String investmentPrompt) {
        if (investmentPrompt.contains("단타")) {
            return SCALPER;
        }
        if (investmentPrompt.contains("장기") || investmentPrompt.contains("존버")) {
            return LONG_TERM;
        }
        if (investmentPrompt.contains("공격")) {
            return AGGRESSIVE;
        }
        if (investmentPrompt.contains("보수") || investmentPrompt.contains("안전")) {
            return CONSERVATIVE;
        }
        return NEUTRAL;
    }
}

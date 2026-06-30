package xyz.vprolabs.sparrow.state;

public final class HudState {
    private HudState() {}

    // ── Base HudState ────────────────────────────────────────────────
    public static int currentPing = 0;
    public static long lastDesyncTime = 0;
    public static final long DESYNC_HIDE_DURATION = 3000;

    // ── FireTimerState ───────────────────────────────────────────────
    public static int lastFireTicks = -1;
    public static String lastFireText = null;
    public static int lastFireWidth = 0;
    public static boolean logged = false;

    public static void reset() {
        lastFireTicks = -1;
        lastFireText = null;
        lastFireWidth = 0;
        logged = false;
    }

    // ── CooldownResetState ───────────────────────────────────────────
    public static boolean cooldownWasReset = false;
    public static long resetTime = 0;
    public static final long RESET_DISPLAY_MS = 400;

    public static void markReset() {
        cooldownWasReset = true;
        resetTime = System.currentTimeMillis();
    }

    public static boolean isResetShowing() {
        return cooldownWasReset && (System.currentTimeMillis() - resetTime < RESET_DISPLAY_MS);
    }

    public static void tickReset() {
        if (cooldownWasReset && !isResetShowing()) {
            cooldownWasReset = false;
        }
    }

    // ── HitConfirmState ──────────────────────────────────────────────
    public static long lastHitTime = 0;
    public static boolean hitConfirmed = false;
    public static int lastAttackedEntityId = -1;
    public static final long HIT_DISPLAY_DURATION = 300;

    public static void registerAttack(int entityId) {
        lastAttackedEntityId = entityId;
    }

    public static void confirmHit(int entityId) {
        if (entityId == lastAttackedEntityId) {
            lastHitTime = System.currentTimeMillis();
            hitConfirmed = true;
            lastAttackedEntityId = -1;
        }
    }

    public static boolean isShowing() {
        return hitConfirmed && (System.currentTimeMillis() - lastHitTime < HIT_DISPLAY_DURATION);
    }

    // ── ShieldChargeState ────────────────────────────────────────────
    public static final long SHIELD_WINDUP_MS = 250;
    private static long shieldRaisedTime = 0;
    private static boolean isCharging = false;
    private static boolean isActive = false;

    public static void update(boolean isUsingItem) {
        if (isUsingItem) {
            if (shieldRaisedTime == 0) {
                shieldRaisedTime = System.currentTimeMillis();
                isCharging = true;
                isActive = false;
            } else {
                long elapsed = System.currentTimeMillis() - shieldRaisedTime;
                if (elapsed >= SHIELD_WINDUP_MS) {
                    isCharging = false;
                    if (!isActive) {
                        isActive = true;
                    }
                }
            }
        } else {
            shieldRaisedTime = 0;
            isCharging = false;
            isActive = false;
        }
    }

    public static boolean isChargingNow() {
        return isCharging;
    }

    public static boolean isActiveNow() {
        return isActive;
    }

}

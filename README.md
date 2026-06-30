<div align="center">

# Sparrow Client

[![Version](https://img.shields.io/badge/Version-#3006202616-24b47e)](https://github.com/stfulua/sparrow-client)
[![License](https://img.shields.io/badge/License-vProLabs%20General%20License-blue)](https://www.vprolabs.xyz/projects/license/raw)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Platform](https://img.shields.io/badge/Platform-Fabric%201.21.11-red)](https://fabricmc.net)

<p>Performance client for Minecraft 1.21.11 — 40+ optimizations, 20+ bug fixes, HUD overlays, and visual enhancements.</p>

</div>

---

### Features

<details open>
<summary><b>⚡ Performance</b> (21 optimizations)</summary>

- **Lighting Kill** — disables all client-side lighting computation (lightmap, block light, sky light). Zero CPU cost for lighting.
- **Entity/Item/Orb Culling** — distance-culls items (40b) and entities (128b). Aggregates nearby items+orbs into single rendered entities.
- **Beacon / Conduit Culling** — don't render beams or conduit effects beyond a distance.
- **Experience Orb Culling** — culls distant orbs before they reach render.
- **Packet Distance Cull** — drops entity spawn packets for entities past render range.
- **Section Builder Culling** — skips rebuild for distant chunk sections.
- **Distant Chunk LOD** — simplified chunk rendering at distance.
- **Cloud / Weather / Sky Kill** — disables cloud rendering, rain/snow particles, and sky completely.
- **Shader Removal** — disables all post-processing shaders (blur, bloom, etc.).
- **Goal Selector Bloat** — limits entity AI goal evaluations per tick.
- **Dynamic Uniforms** — reduces uniform update frequency in render passes.
- **Frame Pacer Control** — frametime capping for consistent frame pacing.
- **Particle Limiter** — caps particles per frame.
- **Fog Disable** — completely removes all fog (water, lava, powder snow, blindness, darkness, atmospheric).
- **Debug Renderer Skip** — eliminates debug renderer overhead.
- **Nether Render Cap** — separate configurable max render distance for the Nether.

</details>

<details open>
<summary><b>🎨 Visual</b> (20 enhancements)</summary>

- **Fullbright** — always max brightness, zero darkness.
- **Gamma Override** — forces gamma to 15.0 every frame (Sodium-compatible).
- **No Nausea** — removes portal nausea wobble and overlay.
- **No Hurt Cam** — removes damage screen shake.
- **No Fire Overlay** — removes fire overlay when burning.
- **No Vignette** — removes screen-edge darkening.
- **No Misc Overlays** — removes potion effect overlays, pumpkin blur, and screen clutter.
- **View Bob Disable** — disables head bobbing.
- **Custom Glint** — RGB-configurable enchantment glint color (default: green).
- **Old Potion Colors** — restores classic pre-1.20 potion color palette.
- **Small Totem** — reduced totem animation size.
- **Totem Pop** — subtle totem activation effect.
- **Clear Fluids** — fully transparent water and lava.
- **Old Fluid Rendering** — disables modern fluid translucency for clarity.
- **Entity Shadow Remover** — disables all entity shadows.
- **Floating Item Scale** — configurable scale for dropped item icons.
- **ViewModel** — X/Y/Z position + scale customization for first-person items.
- **Zoom** — optifine-style zoom with configurable sensitivity, min, max, and smoothness.
- **Boss Bar Skip** — hides the boss health bar overlay.
- **Block Break Overlay Removed** — removed for performance.
- **Disconnect Clear** — clean disconnect screen for faster reconnecting.

</details>

<details open>
<summary><b>🖥️ HUD</b> (13 overlays)</summary>

- **Coordinates** — real-time X/Y/Z in bottom-left.
- **Ping** — latency display in top-right.
- **Desync Detection** — alerts when server rubberbands you while moving.
- **Hit Marker** — visual + sound confirmation on hit land.
- **True Cooldown** — accurate attack cooldown bar that respects weapon swap.
- **Shield Status** — shield health remaining and active state.
- **Fire Timer** — remaining fire duration in readable format.
- **Attack Cooldown** — cooldown bar on the hotbar.
- **Cooldown Reset** — visual flash when item use cooldown resets.
- **Swap Cooldown** — cooldown indicator when switching items.
- **Storage Tooltip** — preview chest/barrel/shulker contents on hover.
- **Cooldown Reset Tracker** — server-side cooldown sync fix for hit detection.
- **Position Utility** — all HUD elements positionable via config.

</details>

<details open>
<summary><b>🔧 Bug Fixes</b> (16 fixes)</summary>

- **Sprint FOV Smoothing** — fixes MC-20302 (jumpy FOV on sprint toggle).
- **Diagonal Movement Fix** — normalizes diagonal sneak speed (MC-271065 revert).
- **Shield Desync Fix** — auto-resync shield when right-click doesn't register server-side.
- **Shield Visibility** — properly displays shield in third-person player model.
- **Block Resync** — periodic block state sync to prevent ghost blocks.
- **Ghost Block Detect** — detects blocks that are desynced between client and server.
- **Inventory Desync Fix** — auto-corrects hotbar slot when inventory gets out of sync.
- **Rubberband Recovery** — restores sprint/sneak after server position correction.
- **Knockback Predictor** — predicts knockback for accurate hit confirmation.
- **Packet Error Ignore** — suppresses benign network errors that would disconnect you.
- **Paletted Container Fix** — prevents world corruption from palette bugs (MC-267913, MC-269572).
- **Sprint Reset Fix** — prevents sprint from dropping incorrectly.
- **Double Consume Fix** — prevents eating/drinking twice from a single click.
- **Portal GUI Unlock** — allows interacting with containers while inside a nether portal.
- **Creative Feature Unlock** — enables operations on non-OP singleplayer worlds.
- **Sprite Animator Fix** — prevents texture animation memory leaks.

</details>

<details open>
<summary><b>⌨️ Movement & Input</b></summary>

- **Toggle Sneak** — toggle-crouch with intelligent interaction bypass (open chests while sneaking).
- **Better Movement** — priority-based WASD conflict resolution (last-pressed wins).
- **Smooth Elytra** — auto-stops elytra gliding on ground contact.
- **Click Relay** — queues item use clicks during cooldown, auto-executes when ready. Prevents missed pearls/food on slot switch.
- **Mouse Scroll** — scroll wheel customization for zoom and inventory interactions.

</details>

<details open>
<summary><b>🌍 World</b></summary>

- **No Mining Fatigue** — visual-only removal of mining fatigue screen effect.
- **Always Day** — forces constant daylight brightness level.
- **Disable Entity AI** — stops all entity AI processing (for singleplayer/custom maps).
- **Nether Render Cap** — independent render distance slider for the Nether.
- **Portal GUI Unlock** — interact with blocks/containers while in a portal.

</details>

<details open>
<summary><b>🛡️ Privacy</b></summary>

- **Client Brand Spoof** — custom client brand string.
- **Telemetry Killer** — blocks all telemetry transmission to Mojang.
- **Telemetry Log Disable** — prevents telemetry log file creation.
- **Window Title** — custom window title string.

</details>

---

### Console Commands

Press **`/`** (right shift) in-game to open the Sparrow console.

| Command | Description |
|---------|-------------|
| `sparrow toggle <feature>` | Enable/disable a boolean feature |
| `sparrow set <feature> <value>` | Set a numeric or string feature value |
| `sparrow list [category]` | List all features or filter by category |
| `sparrow <feature> --help` | Show help for a specific feature |

All features use kebab-case names (`smooth-elytra`, `custom-glint`, `fire-timer-pos`).

---

### Configuration

<details>
<summary><b>View configurable features</b></summary>

**Visual toggles** (13):
`small-totem`, `old-potions`, `custom-glint`, `fire-timer`, `no-misc-overlays`, `remove-shadows`, `storage-tooltip`, `coords`, `ping`, `desync`, `hitmarker`, `true-cooldown`, `shield-status`

**Visual settings** (11):
`view-x`, `view-y`, `view-z`, `view-size`, `utility-scale`, `glint-r`, `glint-g`, `glint-b`, `fire-timer-pos`, `particles`, `block-lod-mode`

**Movement** (2):
`smooth-elytra`, `better-movement`

**Tweaks** (1):
`click-relay`

**Camera** (5):
`zoom`, `zoom-smoothness`, `zoom-min`, `zoom-max`, `disable-mouse-wheel`

**World** (4):
`fullbright`, `no-mining-fatigue`, `always-day`, `disable-entity-ai`, `nether-render-cap`

**Optimization** (2):
`item-culling-distance`, `entity-culling-distance`

**Console** (1):
`console-fps`

</details>

Configuration is saved to `config/sparrow/config.json` and persists across restarts.

---

### Server Safety

Sparrow fetches a blocklist from CDN on server join. When connected to a server listed in the blocklist, flagged features are automatically disabled — no manual toggling needed.

To request server-specific feature blocking, contact us on Discord.

---

### Building

```bash
gradle build                          # full build (ProGuard obfuscation)
gradle build -PdevMode=true           # dev build (no obfuscation, fast)
```

Output: `client/build/libs/SparrowClient-<date>.jar`

---

### Links

- 🌐 **Website:** https://vprolabs.xyz
- 💬 **Discord:** [Join](https://discord.gg/SNzUYWbc5Q)
- 📦 **GitHub:** https://github.com/stfulua/sparrow-client

---

### License

This project is licensed under the **vProLabs General License**.

- Non-Commercial Use Only
- Attribution Required
- Share Alike
- [View Full License](https://www.vprolabs.xyz/projects/license/raw)

---

<div align="center">
  <sub>Made with 🔥 by <strong>vProLabs</strong></sub>
</div>

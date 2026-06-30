package xyz.vprolabs.sparrow.console;

import xyz.vprolabs.sparrow.config.ConfigReader;
import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.state.ToggleSneakState;
import xyz.vprolabs.sparrow.tweaks.SparrowGlintLayers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class FeatureRegistry {

	public record FeatureItem(String name, String category,
	                          java.util.function.Supplier<String> display) {}

	private static final List<FeatureItem> features = new ArrayList<>();

	public static List<FeatureItem> getFeatures() { return features; }

	private static void addFeature(String name, String category,
	                               java.util.function.Supplier<String> display) {
		features.add(new FeatureItem(name, category, display));
	}

	private static void save() {
		if (ConfigReader.getInstance() != null) ConfigReader.saveFromCache();
	}

	private FeatureRegistry() {}

	// ── Standalone toggle command factory ──────────────────────────────

	private static void registerToggleCmd(String name,
	                                      java.util.function.BooleanSupplier getter,
	                                      java.util.function.Consumer<Boolean> setter) {
		SparrowConsoleCommand.register(name, new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length > 1) {
					String val = args[1].toLowerCase(Locale.ROOT);
					if (val.equals("on") || val.equals("true") || val.equals("1")) {
						setter.accept(true);
						save();
						return "\u00a77" + name + ": \u00a7aON";
					}
					if (val.equals("off") || val.equals("false") || val.equals("0")) {
						setter.accept(false);
						save();
						return "\u00a77" + name + ": \u00a7cOFF";
					}
					return "\u00a7cUse on/off for '" + name + "'";
				}
				boolean cur = getter.getAsBoolean();
				setter.accept(!cur);
				save();
				boolean after = getter.getAsBoolean();
				return "\u00a77" + name + ": " + (after ? "\u00a7aON" : "\u00a7cOFF");
			}
			@Override public String getDescription() { return "Toggle " + name; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("on", "off");
				return Collections.emptyList();
			}
		});
	}

	// ── Standalone float set command factory ───────────────────────────

	private static void registerFloatCmd(String name, java.util.function.Supplier<Float> getter,
	                                     java.util.function.Consumer<Float> setter,
	                                     float min, float max) {
		SparrowConsoleCommand.register(name, new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2) return "\u00a77" + name + ": \u00a7f" + getter.get();
				try {
					float v = Float.parseFloat(args[1]);
					if (v < min || v > max)
						return "\u00a7cValue must be " + min + "-" + max;
					setter.accept(v);
					save();
					return "\u00a77" + name + ": \u00a7f" + getter.get();
				} catch (NumberFormatException e) {
					return "\u00a7cInvalid number";
				}
			}
			@Override public String getDescription() { return "Set " + name; }
		});
	}

	// ── Standalone int set command factory ─────────────────────────────

	private static void registerIntCmd(String name, java.util.function.IntSupplier getter,
	                                   java.util.function.Consumer<Integer> setter,
	                                   int min, int max) {
		SparrowConsoleCommand.register(name, new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2) return "\u00a77" + name + ": \u00a7f" + getter.getAsInt();
				try {
					int v = Integer.parseInt(args[1]);
					if (v < min || v > max)
						return "\u00a7cValue must be " + min + "-" + max;
					setter.accept(v);
					save();
					return "\u00a77" + name + ": \u00a7f" + getter.getAsInt();
				} catch (NumberFormatException e) {
					return "\u00a7cInvalid integer";
				}
			}
			@Override public String getDescription() { return "Set " + name; }
		});
	}

	// ══════════════════════════════════════════════════════════════════
	//  registerAllCommands
	// ══════════════════════════════════════════════════════════════════

	public static void registerAllCommands() {
		java.util.Set<String> groupedNames = java.util.Set.of(
			"glint-r", "glint-g", "glint-b", "custom-glint",
			"view-x", "view-y", "view-z", "view-size", "utility-scale",
			"zoom", "zoom-smoothness", "zoom-min", "zoom-max",
			"fire-timer", "fire-timer-pos",
			"particles", "block-lod-mode"
		);

		for (ConfigRegister.Entry e : ConfigRegister.getAll()) {
			if (groupedNames.contains(e.name())) continue;
			if (e instanceof ConfigRegister.Toggle t) {
				registerToggleCmd(t.name(), t::get, t::set);
			} else if (e instanceof ConfigRegister.SetEntry s) {
				if (s.name().equals("item-culling-distance")) {
					registerFloatCmd(s.name(), s::get, s::set, 5.0f, 200.0f);
				} else if (s.name().equals("entity-culling-distance")) {
					registerFloatCmd(s.name(), s::get, s::set, 5.0f, 500.0f);
				}
			} else if (e instanceof ConfigRegister.IntEntry i) {
				if (i.name().equals("nether-render-cap")) {
					registerIntCmd(i.name(), i::get, i::set, 2, 20);
				} else if (i.name().equals("console-fps")) {
					registerConsoleFpsCmd();
				}
			}
		}

		registerGlintCmd();
		registerViewCmd();
		registerZoomCmd();
		registerFireTimerCmd();
		registerParticlesCmd();
		registerBlockLodCmd();
		registerSneakCmd();
	}

	// ── console-fps (needs SparrowConsoleState.consoleFps sync) ────────

	private static void registerConsoleFpsCmd() {
		SparrowConsoleCommand.register("console-fps", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2)
					return "\u00a77console-fps: \u00a7f" + ConfigRegister.consoleFps.get();
				try {
					int v = Integer.parseInt(args[1]);
					if (v < 5) return "\u00a7cValue must be >= 5";
					ConfigRegister.consoleFps.set(v);
					SparrowConsoleState.consoleFps = v;
					save();
					return "\u00a77console-fps: \u00a7f" + ConfigRegister.consoleFps.get();
				} catch (NumberFormatException e) {
					return "\u00a7cInvalid integer";
				}
			}
			@Override public String getDescription() { return "Set console FPS"; }
		});
	}

	// ── glint ──────────────────────────────────────────────────────────

	private static void registerGlintCmd() {
		SparrowConsoleCommand.register("glint", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2) {
					return "\u00a77glint \u00a7fr=" + ConfigRegister.glintR.get()
						+ " \u00a7fg=" + ConfigRegister.glintG.get()
						+ " \u00a7fb=" + ConfigRegister.glintB.get()
						+ " \u00a77custom=" + (ConfigRegister.customGlint.get() ? "\u00a7aON" : "\u00a7cOFF");
				}
				String sub = args[1].toLowerCase(Locale.ROOT);
				switch (sub) {
				case "on":
					ConfigRegister.customGlint.set(true);
					SparrowGlintLayers.refresh();
					save();
					return "\u00a77custom-glint: \u00a7aON";
				case "off":
					ConfigRegister.customGlint.set(false);
					SparrowGlintLayers.refresh();
					save();
					return "\u00a77custom-glint: \u00a7cOFF";
				case "r": {
					if (args.length < 3) return "\u00a7cUsage: glint r <0-255>";
					try {
						int v = Integer.parseInt(args[2]);
						if (v < 0 || v > 255) return "\u00a7cValue must be 0-255";
						ConfigRegister.glintR.set(v);
						SparrowGlintLayers.refresh();
						save();
						return "\u00a77glint-r: \u00a7f" + v;
					} catch (NumberFormatException e) {
						return "\u00a7cInvalid integer";
					}
				}
				case "g": {
					if (args.length < 3) return "\u00a7cUsage: glint g <0-255>";
					try {
						int v = Integer.parseInt(args[2]);
						if (v < 0 || v > 255) return "\u00a7cValue must be 0-255";
						ConfigRegister.glintG.set(v);
						SparrowGlintLayers.refresh();
						save();
						return "\u00a77glint-g: \u00a7f" + v;
					} catch (NumberFormatException e) {
						return "\u00a7cInvalid integer";
					}
				}
				case "b": {
					if (args.length < 3) return "\u00a7cUsage: glint b <0-255>";
					try {
						int v = Integer.parseInt(args[2]);
						if (v < 0 || v > 255) return "\u00a7cValue must be 0-255";
						ConfigRegister.glintB.set(v);
						SparrowGlintLayers.refresh();
						save();
						return "\u00a77glint-b: \u00a7f" + v;
					} catch (NumberFormatException e) {
						return "\u00a7cInvalid integer";
					}
				}
				default:
					return "\u00a7cUsage: glint [on|off|r|g|b] [value]";
				}
			}
			@Override public String getDescription() { return "Custom enchant glint color"; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("on", "off", "r", "g", "b");
				return Collections.emptyList();
			}
		});
	}

	// ── view ───────────────────────────────────────────────────────────

	private static void registerViewCmd() {
		SparrowConsoleCommand.register("view", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2) {
					return "\u00a77view \u00a7fx=" + ConfigRegister.viewModelX.get()
						+ " \u00a7fy=" + ConfigRegister.viewModelY.get()
						+ " \u00a7fz=" + ConfigRegister.viewModelZ.get()
						+ " \u00a7fsize=" + ConfigRegister.viewModelSize.get()
						+ " \u00a7futility-scale=" + ConfigRegister.utilityScale.get();
				}
				String sub = args[1].toLowerCase(Locale.ROOT);
				try {
					switch (sub) {
					case "x":
						if (args.length < 3) return "\u00a7cUsage: view x <float>";
						ConfigRegister.viewModelX.set(Float.parseFloat(args[2]));
						save();
						return "\u00a77view-x: \u00a7f" + ConfigRegister.viewModelX.get();
					case "y":
						if (args.length < 3) return "\u00a7cUsage: view y <float>";
						ConfigRegister.viewModelY.set(Float.parseFloat(args[2]));
						save();
						return "\u00a77view-y: \u00a7f" + ConfigRegister.viewModelY.get();
					case "z":
						if (args.length < 3) return "\u00a7cUsage: view z <float>";
						ConfigRegister.viewModelZ.set(Float.parseFloat(args[2]));
						save();
						return "\u00a77view-z: \u00a7f" + ConfigRegister.viewModelZ.get();
					case "size":
						if (args.length < 3) return "\u00a7cUsage: view size <float (>= 0.01)>";
						{
							float v = Float.parseFloat(args[2]);
							if (v < 0.01f) return "\u00a7cValue must be >= 0.01";
							ConfigRegister.viewModelSize.set(v);
							save();
							return "\u00a77view-size: \u00a7f" + ConfigRegister.viewModelSize.get();
						}
					case "utility-scale":
						if (args.length < 3) return "\u00a7cUsage: view utility-scale <0.1-2.0>";
						{
							float v = Float.parseFloat(args[2]);
							if (v < 0.1f || v > 2.0f) return "\u00a7cValue must be 0.1-2.0";
							ConfigRegister.utilityScale.set(v);
							save();
							return "\u00a77utility-scale: \u00a7f" + ConfigRegister.utilityScale.get();
						}
					default:
						return "\u00a7cUsage: view [x|y|z|size|utility-scale] <value>";
					}
				} catch (NumberFormatException e) {
					return "\u00a7cInvalid number";
				}
			}
			@Override public String getDescription() { return "View model position/size"; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("x", "y", "z", "size", "utility-scale");
				return Collections.emptyList();
			}
		});
	}

	// ── zoom ───────────────────────────────────────────────────────────

	private static void registerZoomCmd() {
		SparrowConsoleCommand.register("zoom", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2) {
					return "\u00a77zoom \u00a7f" + ConfigRegister.zoomLevel.get() + "x"
						+ " \u00a77smoothness=" + ConfigRegister.zoomSmoothness.get()
						+ " \u00a77min=" + ConfigRegister.zoomMin.get()
						+ " \u00a77max=" + ConfigRegister.zoomMax.get();
				}
				String sub = args[1].toLowerCase(Locale.ROOT);
				try {
					switch (sub) {
					case "smoothness":
						if (args.length < 3) return "\u00a7cUsage: zoom smoothness <float (>= 1.0)>";
						{
							float v = Float.parseFloat(args[2]);
							if (v < 1.0f) return "\u00a7cValue must be >= 1.0";
							ConfigRegister.zoomSmoothness.set(v);
							save();
							return "\u00a77zoom-smoothness: \u00a7f" + ConfigRegister.zoomSmoothness.get() + "x";
						}
					case "min":
						if (args.length < 3) return "\u00a7cUsage: zoom min <0.5-10.0>";
						{
							float v = Float.parseFloat(args[2]);
							if (v < 0.5f || v > 10.0f) return "\u00a7cValue must be 0.5-10.0";
							ConfigRegister.zoomMin.set(v);
							save();
							return "\u00a77zoom-min: \u00a7f" + ConfigRegister.zoomMin.get();
						}
					case "max":
						if (args.length < 3) return "\u00a7cUsage: zoom max <5.0-50.0>";
						{
							float v = Float.parseFloat(args[2]);
							if (v < 5.0f || v > 50.0f) return "\u00a7cValue must be 5.0-50.0";
							ConfigRegister.zoomMax.set(v);
							save();
							return "\u00a77zoom-max: \u00a7f" + ConfigRegister.zoomMax.get();
						}
					default:
						{
							float v = Float.parseFloat(sub);
							if (v < 1.0f) return "\u00a7cZoom level must be >= 1.0";
							ConfigRegister.zoomLevel.set(v);
							save();
							return "\u00a77zoom: \u00a7f" + ConfigRegister.zoomLevel.get() + "x";
						}
					}
				} catch (NumberFormatException e) {
					if (args.length >= 3 || (args.length == 2 && !sub.equals("smoothness")
					    && !sub.equals("min") && !sub.equals("max"))) {
						return "\u00a7cUsage: zoom [smoothness|min|max] <value>  or  zoom <level>";
					}
					return "\u00a7cUsage: zoom [smoothness|min|max] <value>  or  zoom <level>";
				}
			}
			@Override public String getDescription() { return "Zoom settings"; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("smoothness", "min", "max");
				return Collections.emptyList();
			}
		});
	}

	// ── fire-timer ─────────────────────────────────────────────────────

	private static void registerFireTimerCmd() {
		SparrowConsoleCommand.register("fire-timer", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2) {
					return "\u00a77fire-timer: " + (ConfigRegister.fireTimer.get() ? "\u00a7aON" : "\u00a7cOFF")
						+ " \u00a77pos=" + ConfigRegister.fireTimerPos.get();
				}
				String sub = args[1].toLowerCase(Locale.ROOT);
				switch (sub) {
				case "on":
					ConfigRegister.fireTimer.set(true);
					save();
					return "\u00a77fire-timer: \u00a7aON";
				case "off":
					ConfigRegister.fireTimer.set(false);
					save();
					return "\u00a77fire-timer: \u00a7cOFF";
				case "pos":
					if (args.length < 3) return "\u00a7cUsage: fire-timer pos [TOP_LEFT|TOP_RIGHT|BOTTOM_CENTER]";
					{
						String v = args[2].toUpperCase(Locale.ROOT);
						if (v.equals("TOP_LEFT") || v.equals("TOP_RIGHT") || v.equals("BOTTOM_CENTER")) {
							ConfigRegister.fireTimerPos.set(v);
							save();
							return "\u00a77fire-timer-pos: \u00a7f" + v;
						}
						return "\u00a7cInvalid position. Use: TOP_LEFT, TOP_RIGHT, BOTTOM_CENTER";
					}
				default:
					return "\u00a7cUsage: fire-timer [on|off|pos]";
				}
			}
			@Override public String getDescription() { return "Fire timer overlay"; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("on", "off", "pos");
				if (args.length == 3 && args[1].equalsIgnoreCase("pos"))
					return Arrays.asList("TOP_LEFT", "TOP_RIGHT", "BOTTOM_CENTER");
				return Collections.emptyList();
			}
		});
	}

	// ── particles ──────────────────────────────────────────────────────

	private static void registerParticlesCmd() {
		SparrowConsoleCommand.register("particles", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2)
					return "\u00a77particles: \u00a7f" + ConfigRegister.particleMode.get();
				String v = args[1].toLowerCase(Locale.ROOT);
				if (v.equals("off") || v.equals("minimal") || v.equals("on")) {
					ConfigRegister.particleMode.set(v);
					save();
					return "\u00a77particles: \u00a7f" + v;
				}
				return "\u00a7cUsage: particles [off|minimal|on]";
			}
			@Override public String getDescription() { return "Particle mode"; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("off", "minimal", "on");
				return Collections.emptyList();
			}
		});
	}

	// ── block-lod ──────────────────────────────────────────────────────

	private static void registerBlockLodCmd() {
		SparrowConsoleCommand.register("block-lod", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length < 2)
					return "\u00a77block-lod: \u00a7f" + ConfigRegister.blockLodMode.get();
				String v = args[1].toUpperCase(Locale.ROOT);
			if (v.equals("OFF") || v.equals("LOW") || v.equals("PVP") || v.equals("AGGRESSIVE")) {
				ConfigRegister.blockLodMode.set(v);
				save();
				return "\u00a77block-lod: \u00a7f" + v;
			}
			return "\u00a7cUsage: block-lod [off|low|pvp|aggressive]";
			}
			@Override public String getDescription() { return "Block LOD mode"; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("off", "low", "pvp", "aggressive");
				return Collections.emptyList();
			}
		});
	}

	// ── sneak ──────────────────────────────────────────────────────────

	private static void registerSneakCmd() {
		SparrowConsoleCommand.register("sneak", new SparrowConsoleCommand.Command() {
			@Override public String execute(String[] args) {
				if (args.length > 1) {
					String val = args[1].toLowerCase(Locale.ROOT);
					if (val.equals("on") || val.equals("true") || val.equals("1")) {
						ToggleSneakState.enabled = true;
						save();
						return "\u00a77sneak: \u00a7aON";
					}
					if (val.equals("off") || val.equals("false") || val.equals("0")) {
						ToggleSneakState.enabled = false;
						save();
						return "\u00a77sneak: \u00a7cOFF";
					}
					return "\u00a7cUse on/off for 'sneak'";
				}
				ToggleSneakState.toggle();
				save();
				return "\u00a77sneak: " + (ToggleSneakState.enabled ? "\u00a7aON" : "\u00a7cOFF");
			}
			@Override public String getDescription() { return "Toggle-sneak"; }
			@Override public List<String> tabComplete(String[] args) {
				if (args.length == 2) return Arrays.asList("on", "off");
				return Collections.emptyList();
			}
		});
	}

	// ── FeatureItem list (for 'list' command display) ──────────────────

	static {
		for (ConfigRegister.Entry e : ConfigRegister.getAll()) {
			java.util.function.Supplier<String> display;
			if (e instanceof ConfigRegister.Toggle t) {
				display = t::display;
			} else if (e instanceof ConfigRegister.SetEntry s) {
				display = s::display;
			} else if (e instanceof ConfigRegister.IntEntry i) {
				display = i::display;
			} else if (e instanceof ConfigRegister.StringEntry s) {
				display = s::display;
			} else {
				continue;
			}
			addFeature(e.name(), e.category(), display);
		}
		addFeature("sneak", "Movement",
			() -> ToggleSneakState.enabled ? "\u00a7aON" : "\u00a7cOFF");
	}
}

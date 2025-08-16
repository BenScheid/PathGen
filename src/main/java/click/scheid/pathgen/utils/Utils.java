package click.scheid.pathgen.utils;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import click.scheid.pathgen.PathGenPlugin;
import click.scheid.pathgen.types.Coordinate;
import net.md_5.bungee.api.ChatColor;

public final class Utils {

	private Utils() {
	}

	private static final Executor MAIN_THREAD_EXECUTOR = run -> Bukkit.getScheduler().runTask(PathGenPlugin.INSTANCE,
			run);
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4); // TODO config

	public static String invalidArgs() {
		return invalidCommand("Invalid Arguments!");
	}

	public static String invalidName(String name) {
		return invalidCommand("You don't have a path with the following name: " + name + " !");
	}

	public static String invalidCommand(String message) {
		return ChatColor.RED + message;
	}

	public static String usageMessage() {
		return ChatColor.RED + Bukkit.getPluginCommand("pathgen").getUsage();
	}

	public static String getCardinalDirection(Coordinate from, Coordinate to) {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		if (from.equals(to)) {
			return "N";
		}

		double dx = to.getX() - from.getX();
		double dz = from.getZ() - to.getZ(); // flip Z-axis for Minecraft

		double angle = Math.atan2(dz, dx);
		angle = (angle + 2 * Math.PI) % (2 * Math.PI);

		final String[] directions = {"NE", "N", "NW", "W", "SW", "S", "SE", "E" };
		final double step = Math.PI / 4;
		final double offset = Math.PI / 8;

		for (int i = 0; i < 8; i++) {

			double lower = (i * step + offset) % (2 * Math.PI);
			double upper = ((i + 1) * step + offset) % (2 * Math.PI);
			if (lower < upper) {
				if (angle >= lower && angle < upper) {
					return directions[i];
				}
			} else {
				if (angle >= lower || angle < upper) {
					return directions[i];
				}
			}
		}

		throw new RuntimeException("Invalid direction.");

	}

	public static Location coordToLocation(Coordinate coord) {
		return new Location(Bukkit.getWorld(coord.getWorldName()), coord.getX() != null ? coord.getX() : 0,
				coord.getY() != null ? coord.getY() : 0, coord.getZ() != null ? coord.getZ() : 0);
	}

	public static Coordinate locationToCoord(Location loc) {
		return new Coordinate(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}

	public static boolean differentLocation(Coordinate l1, Coordinate l2) {
		// @formatter:off
		return !Objects.equals(l1.getIntX(), l2.getIntX()) || 
			   !Objects.equals(l1.getIntY(), l2.getIntY()) || 
			   !Objects.equals(l1.getIntZ(), l2.getIntZ());
		// @formatter:on
	}

	public static void async(Runnable task) {
		CompletableFuture.runAsync(task, THREAD_POOL).exceptionally(ex -> {
			defaultErrorHandler(ex);
			return null;
		});
	}

	public static void sync(Runnable task) {
		CompletableFuture.runAsync(task, MAIN_THREAD_EXECUTOR).exceptionally(ex -> {
			defaultErrorHandler(ex);
			return null;
		});
	}

	public static <T> CompletableFuture<T> asyncFutureSupply(Supplier<T> task) {
		return CompletableFuture.supplyAsync(task, THREAD_POOL);
	}

	// @formatter:off
	public static <T, R> CompletableFuture<R> runAsyncAndApply(Supplier<T> asyncTask, Function<T, R> syncTask,
			Function<Throwable, R> exceptionHandler) {
		return asyncFutureSupply(asyncTask)
					.thenApplyAsync(supply -> syncTask.apply(supply), MAIN_THREAD_EXECUTOR)
					.exceptionallyAsync(exceptionHandler, MAIN_THREAD_EXECUTOR);
	}

	public static <T> CompletableFuture<Void> runAsyncAndAccept(Supplier<T> asyncTask, Consumer<T> syncTask,
			Function<Throwable, Void> exceptionHandler) {
		return asyncFutureSupply(asyncTask)
					.thenAcceptAsync(supply -> syncTask.accept(supply), MAIN_THREAD_EXECUTOR)
					.exceptionallyAsync(exceptionHandler, MAIN_THREAD_EXECUTOR);
	}
	// @formatter:on
	public static <T, R> CompletableFuture<R> runAsyncAndApply(Supplier<T> asyncTask, Function<T, R> syncTask) {
		return runAsyncAndApply(asyncTask, syncTask, ex -> {
			defaultErrorHandler(ex);
			return null;
		});
	}

	public static <T> CompletableFuture<Void> runAsyncAndAccept(Supplier<T> asyncTask, Consumer<T> syncTask) {
		return runAsyncAndAccept(asyncTask, syncTask, ex -> {
			defaultErrorHandler(ex);
			return null;
		});
	}

	public static void defaultErrorHandler(Throwable t) {
		if (t instanceof CommandException cmdEx) {
			log("An error occurred while processing a PathGen command. Please refer to /pathgen help or the official documentation on Github.");
		} else {
			log("A PathGen error occurred. You may open an issue on GitHub.");
		}
		if (PathGenPlugin.DEBUG) {
			t.printStackTrace();
		}
	}

	public static void playerErrorHandler(Throwable t, Player p) {
		p.sendMessage(t.getMessage());
		p.sendMessage(usageMessage());
		defaultErrorHandler(t);
	}

	public static void createDistanceUpdater() {
		// update the distance each 10 ticks / 0.5s

		Bukkit.getScheduler().runTaskTimer(PathGenPlugin.INSTANCE, () -> {
			if (!BossBarManager.UPDATE_BOSS_BAR.isEmpty()) {
				BossBarManager.UPDATE_BOSS_BAR.forEach(path -> {
					PathGenPlugin.BOSS_BARS.updateBossBar(path);
				});
			}
		}, 0, 10L);
	}

	public static double distanceToSurfaceSquared(Coordinate player, Coordinate to) {
		if (!player.getWorldName().equals(to.getWorldName())) {
			throw new RuntimeException();
		}
		World world = Bukkit.getWorld(player.getWorldName());
		to.setY((double) world.getHighestBlockYAt(to.getIntX(), to.getIntZ(), HeightMap.WORLD_SURFACE_WG));
		return distanceSquared(player, to);
	}

	public static double distanceSquared(Coordinate from, Coordinate to) {
		return Math.pow(to.getX() - from.getX(), 2) + Math.pow(to.getY() - from.getY(), 2)
				+ Math.pow(to.getZ() - from.getZ(), 2);
	}

	public static Player getPlayer(UUID id) {
		Player p = Bukkit.getPlayer(id);
		if (p == null) {
			throw new RuntimeException("Player not found");
		}
		return p;
	}

	public static Player getPlayer(String id) {
		return getPlayer(UUID.fromString(id));
	}

	public static void runSyncTask(Runnable run) {
		Bukkit.getScheduler().runTask(PathGenPlugin.INSTANCE, run);
	}

	public static void log(Object msg) {
		Bukkit.getLogger().info(msg == null ? "null" : msg.toString());
	}

	public static void logAsync(Object msg) {
		runSyncTask(() -> Bukkit.getLogger().info(msg == null ? "null" : msg.toString()));
	}

	// Newton's method
	public static double fastSqrt(double in) {
		if (in < 0) {
			throw new ArithmeticException("Negative input.");
		}
		if (in == 0d) {
			return 0;
		}
		double guess = Math.scalb(1.0, Math.getExponent(in) / 2);
		for (int i = 0; i < 3; i++) {
			guess = guess - (Math.pow(guess, 2) - in) / (2 * guess);
		}
		return guess;
	}
}

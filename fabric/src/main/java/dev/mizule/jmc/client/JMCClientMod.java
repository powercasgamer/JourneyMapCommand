/*
 * This file is part of JourneyMapCommand, licensed under the MIT License.
 *
 * Copyright (c) 2024 powercas_gamer
 * Copyright (c) 2024 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.mizule.jmc.client;

import com.mojang.logging.LogUtils;
import dev.mizule.jmc.BuildParameters;
import dev.mizule.jmc.client.cloud.CloudStuff;
import dev.mizule.jmc.client.plugin.JMCJourneyMapPlugin;
import journeymap.client.api.display.Waypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.incendo.cloud.Command;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.fabric.FabricClientCommandManager;
import org.incendo.cloud.minecraft.modded.data.Coordinates;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.aggregate.AggregateParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.slf4j.Logger;

import static net.kyori.adventure.text.Component.text;

public class JMCClientMod implements ClientModInitializer {

  private static final Logger LOGGER = LogUtils.getLogger();
  private final FabricClientCommandManager<FabricClientCommandSource> commandManager;

  public JMCClientMod() {
    this.commandManager = new FabricClientCommandManager<>(
      ExecutionCoordinator.simpleCoordinator(),
      SenderMapper.identity()
    );
    final AggregateParser<FabricClientCommandSource, BlockPos> locationParser = AggregateParser
      .<FabricClientCommandSource>builder()
      .withComponent("x", CloudStuff.blockPosParser())
      .withComponent("y", CloudStuff.blockPosParser())
      .withComponent("z", CloudStuff.blockPosParser())
      .withMapper(BlockPos.class, (commandContext, aggregateCommandContext) -> {
        final int x = aggregateCommandContext.get("x");
        final int y = aggregateCommandContext.get("y");
        final int z = aggregateCommandContext.get("z");
        return ArgumentParseResult.successFuture(new BlockPos(x, y, z));
      }).build();

    final Command.Builder<FabricClientCommandSource> builder = commandManager.commandBuilder("journeymapcommand", "jmc");
    this.commandManager.command(
      builder.literal("waypoint")
        .required("name", StringParser.quotedStringParser())
        .required("location", CloudStuff.blockPosParser())
//        .required("location", CloudStuff.columnPosParser())
        .handler(context -> {
          final Audience client = FabricClientAudiences.of().audience();
          final BlockPos loc = ((Coordinates.BlockCoordinates) context.get("location")).blockPos();
          final Waypoint waypoint = new Waypoint(
            "jmc-fabric",
            context.get("name"),
            context.sender().getWorld().dimension(),
            loc
          );
          client.sendMessage(text("Created waypoint " + context.get("name") + " at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ()));
          try {
            JMCJourneyMapPlugin.instance().jmAPI.show(waypoint);
          } catch (Exception e) {
            context.sender().sendError(Component.literal("Failed to create waypoint"));
            LOGGER.error("Failed to create waypoint", e);
          }
        })
    );
    this.commandManager.command(
      builder.literal("info")
        .handler(context -> {
          final Audience client = FabricClientAudiences.of().audience();
          client.sendMessage(text("hi :3"));
          client.sendMessage(text("Version: " + dev.mizule.jmc.BuildParameters.VERSION));
          client.sendMessage(text("Git Commit: " + BuildParameters.GIT_COMMIT));
          client.sendMessage(text("Fabric Loader Version: " + BuildParameters.FABRIC_LOADER_VERSION));
          client.sendMessage(text("Fabric API Version: " + BuildParameters.FABRIC_API_VERSION));
          client.sendMessage(text("Minecraft Version: " + BuildParameters.MINECRAFT_VERSION));
        })
    );
  }

  @Override
  public void onInitializeClient() {
    ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
      System.out.println("Hello Fabric world!");
    });
  }
}

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
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.aggregate.AggregateParser;
import org.incendo.cloud.parser.standard.StringParser;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class JMCClientMod implements ClientModInitializer {

  private final FabricClientCommandManager<FabricClientCommandSource> commandManager;

  public JMCClientMod() {
    this.commandManager = new FabricClientCommandManager<>(
      ExecutionCoordinator.simpleCoordinator(),
      SenderMapper.identity()
    );
    final AggregateParser<FabricClientCommandSource, BlockPos> locationParser = AggregateParser
      .<FabricClientCommandSource>builder()
      .withComponent("x", integerParser())
      .withComponent("y", integerParser())
      .withComponent("z", integerParser())
      .withMapper(BlockPos.class, (commandContext, aggregateCommandContext) -> {
        final int x = aggregateCommandContext.get("x");
        final int y = aggregateCommandContext.get("y");
        final int z = aggregateCommandContext.get("z");
        return ArgumentParseResult.successFuture(new BlockPos(x, y, z));
      }).build();

    final Command.Builder<FabricClientCommandSource> builder = commandManager.commandBuilder("journeymapcommand", "jmc");
    this.commandManager.command(
      builder.literal("waypoint")
        .required("name", StringParser.stringParser())
        .required("location", locationParser)
        .handler(context -> {
          final Audience client = FabricClientAudiences.of().audience();
          final Waypoint waypoint = new Waypoint(
            "jmc-fabric",
            context.get("name"),
            context.sender().getWorld().dimension(),
            context.get("location")
          );
          try {
          JMCJourneyMapPlugin.instance().jmAPI.show(waypoint);
          } catch (Exception e) {
            context.sender().sendError(Component.literal("Failed to create waypoint"));
          }
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

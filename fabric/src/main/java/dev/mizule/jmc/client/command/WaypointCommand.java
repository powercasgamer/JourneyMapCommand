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
package dev.mizule.jmc.client.command;

import dev.mizule.jmc.client.JMCClientMod;
import dev.mizule.jmc.client.cloud.CloudStuff;
import dev.mizule.jmc.client.plugin.JMCJourneyMapPlugin;
import journeymap.client.api.display.Waypoint;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.minecraft.modded.data.Coordinates;
import org.incendo.cloud.minecraft.modded.parser.NamedColorParser;
import org.incendo.cloud.parser.standard.StringParser;

import static dev.mizule.jmc.client.command.CommandService.createKey;
import static net.kyori.adventure.text.Component.text;

public class WaypointCommand extends JMCCommand {

  private static final CloudKey<ChatFormatting> CHAT_FORMATTING = createKey("color", ChatFormatting.class);

  protected WaypointCommand(CommandService commands) {
    super(commands);
  }

  @Override
  public void register() {
    this.commands.registerSubcommand(builder -> builder.literal("waypoint")
        .required("name", StringParser.quotedStringParser())
        .required("location", CloudStuff.blockPosParser())
        .optional(CHAT_FORMATTING, NamedColorParser.namedColorParser())
        .handler(context -> {
          final Audience client = FabricClientAudiences.of().audience();
          final BlockPos loc = ((Coordinates.BlockCoordinates) context.get("location")).blockPos();
          final Waypoint waypoint = new Waypoint(
            "jmc-fabric",
            context.get("name"),
            context.sender().getWorld().dimension(),
            loc
          );
          context.optional(CHAT_FORMATTING).ifPresent(color -> {
            waypoint.setColor(color.getColor());
          });
          client.sendMessage(text("Created waypoint " + context.get("name") + " at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ()));
          try {
            JMCJourneyMapPlugin.instance().jmAPI.show(waypoint);
          } catch (Exception e) {
            context.sender().sendError(Component.literal("Failed to create waypoint"));
            JMCClientMod.LOGGER.error("Failed to create waypoint", e);
          }
        })
    );
  }
}

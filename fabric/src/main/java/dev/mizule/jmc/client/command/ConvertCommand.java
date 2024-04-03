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

import dev.mizule.jmc.client.cloud.CloudStuff;
import dev.mizule.jmc.client.util.CoordinatesConverter;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.chat.ChatType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.minecraft.modded.data.Coordinates;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.type.tuple.Pair;

import static dev.mizule.jmc.client.command.CommandService.createKey;
import static net.kyori.adventure.text.Component.text;

public class ConvertCommand extends JMCCommand {

  private static final CloudKey<ConvertType> FROM = createKey("from", ConvertType.class);
  private static final CloudKey<Coordinates.CoordinatesXZ> FROM_COORDS = createKey("from-coords", Coordinates.CoordinatesXZ.class);
  private static final CloudKey<ConvertType> TO = createKey("to", ConvertType.class);
  private static final CloudKey<String> NAME = createKey("name", String.class);

  protected ConvertCommand(CommandService commands) {
    super(commands);
  }

  @Override
  public void register() {
    this.commands.registerSubcommand(builder -> builder.literal("convert")
      .meta(CommandService.EXPERIMENTAL, true)
      .required(FROM, EnumParser.enumParser(ConvertType.class))
      .required(TO, EnumParser.enumParser(ConvertType.class))
      .required(FROM_COORDS, CloudStuff.vec2Parser(true))
      .optional(NAME, StringParser.greedyStringParser(), DefaultValue.dynamic(context -> "Converted-Waypoint-" + System.currentTimeMillis()))
      .handler(ctx -> {
        final var sender = ctx.sender();
        final ConvertType from = ctx.get(FROM);
        final ConvertType to = ctx.get(TO);
        final Coordinates.CoordinatesXZ coords = ctx.get(FROM_COORDS);
        if (from == to) {
          sender.sendError(Component.literal("You can't convert to the same dimension!").withStyle(ChatFormatting.RED));
          return;
        }

        final int y;
        final Pair<Double, Double> convertedCoords;
        if (to == ConvertType.OVERWORLD) {
          convertedCoords = CoordinatesConverter.toOverworldCoordinates(coords.position().x, coords.position().z);
          y = 75;
        } else {
          convertedCoords = CoordinatesConverter.toNetherCoordinates(coords.position().x, coords.position().z);
          y = 150;
        }

        final Vec3i vec = new Vec3i(convertedCoords.first().intValue(), y, convertedCoords.second().intValue());

        // Message:
        // X: 1 -> 8
        // Y: 1 -> 1
        // Z: 1 -> 8

        ctx.sender().getPlayer().sendMessage(text()
          .append(text("Converted coordinates: "))
            .appendNewline()
          .append(text("X: " + vec.getX()))
          .append(text(", Y: " + vec.getY()))
          .append(text(", Z: " + vec.getZ()))
          .build());
        ctx.sender().getPlayer().sendMessage(text()
          .append(text("X: %s -> %s".formatted(coords.position().x, vec.getX())))
          .appendNewline()
          .append(text("Y: %s -> %s".formatted(coords.position().y, vec.getY())))
          .appendNewline()
          .append(text("Z: %s -> %s".formatted(coords.position().z, vec.getZ())))
        );
        final String format = "[name: \"%s\", x:%s, y:%s, z:%s]";
        ctx.sender().getPlayer().sendMessage(text()
          .append(text("Waypoint: "))
            .append(text(format.formatted("Converted", vec.getX(), vec.getY(), vec.getZ())))
          );
        ctx.sender().getPlayer().sendMessage(text(format.formatted("test", vec.getX(), vec.getY(), vec.getZ())));
        ctx.sender().getPlayer().sendMessage(text(format.formatted("test", vec.getX(), vec.getY(), vec.getZ())), MessageType.CHAT);
        ctx.sender().getPlayer().sendMessage(text(format.formatted("test", vec.getX(), vec.getY(), vec.getZ())), ChatType.CHAT);
        System.out.println(format.formatted("test", vec.getX(), vec.getY(), vec.getZ()));
        ctx.sender().getPlayer().sendSystemMessage(Component.literal(format.formatted("test", vec.getX(), vec.getY(), vec.getZ())));
        Minecraft.getInstance().player.sendMessage(text(format.formatted("test", vec.getX(), vec.getY(), vec.getZ())));
        Minecraft.getInstance().player.sendSystemMessage(Component.literal(format.formatted("test", vec.getX(), vec.getY(), vec.getZ())));

        System.out.println(format.formatted("test", vec.getX(), vec.getY(), vec.getZ()));
//        final Waypoint waypoint = new Waypoint(
//          BuildParameters.MOD_ID,
//          ctx.get(NAME),
//          ctx.sender().getWorld().dimension(),
//          new BlockPos(vec)
//        );
//
//        ctx.sender().getPlayer().sendMessage(text("Created converted waypoint " + ctx.get(NAME) + " at " + vec.getX() + ", " + vec.getY() + ", " + vec.getZ()));
//        try {
//          JMCJourneyMapPlugin.instance().jmAPI.show(waypoint);
//        } catch (Exception e) {
//          ctx.sender().sendError(Component.literal("Failed to create waypoint"));
//          JMCClientMod.LOGGER.error("Failed to create waypoint", e);
//        }
      }));
  }

  public enum ConvertType {
    OVERWORLD,
    NETHER,
  }
}

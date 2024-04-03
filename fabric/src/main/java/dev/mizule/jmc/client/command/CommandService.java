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

import dev.mizule.jmc.BuildParameters;
import dev.mizule.jmc.client.JMCClientMod;
import io.leangen.geantyref.TypeToken;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.key.CloudKey;

import java.util.List;
import java.util.function.Function;

@DefaultQualifier(NonNull.class)
public final class CommandService {

  public static final CloudKey<Boolean> EXPERIMENTAL = createKey("experimental", Boolean.class);
  private final CommandManager<FabricClientCommandSource> commandManager;

  public CommandService(
    final CommandManager<FabricClientCommandSource> commandManager
  ) {
    this.commandManager = commandManager;

    this.commandManager.registerCommandPreProcessor(context -> {
      final boolean experimental = false; // somehow get the experimental status of the command
      if (experimental) {
        final Audience sender = FabricClientAudiences.of().audience();
        sender.sendMessage(Component.text("This is an experimental command!", NamedTextColor.RED, TextDecoration.ITALIC));
      }
    });
    this.commandManager.registerCommandPostProcessor(context -> {
      final boolean experimental = context.command().commandMeta().get(EXPERIMENTAL);
      if (experimental) {
        final Audience sender = FabricClientAudiences.of().audience();
        sender.sendMessage(Component.text("This is an experimental command!", NamedTextColor.RED, TextDecoration.ITALIC));
      }
    });

//    exceptionHandler.registerExceptionHandlers(this.commandManager);
  }

  public static <T> CloudKey<T> createKey(final String name, final Class<T> type) {
    return CloudKey.of(name, TypeToken.get(type));
  }

  public void registerCommands() {
    final List<? extends JMCCommand> commands = List.of(
      new TestCommand(this),
      new HelpCommand(this),
      new WaypointCommand(this),
      new ConvertCommand(this)
    );

    for (final JMCCommand command : commands) {
      try {
        command.register();
      } catch (final Exception e) {
        JMCClientMod.LOGGER.error("Unable to register command: " + command.getClass().getSimpleName(), e);
      }
    }
  }

  public void register(final Command.Builder<? extends FabricClientCommandSource> builder) {
    this.commandManager.command(builder);
  }

  public void registerSubcommand(final Function<Command.Builder<FabricClientCommandSource>, Command.Builder<? extends FabricClientCommandSource>> builderModifier) {
    this.register(builderModifier.apply(this.rootBuilder()));
  }

  public Command.Builder<FabricClientCommandSource> rootBuilder() {
    return this.commandManager.commandBuilder(
      "journeymapcommand",
      Description.of(String.format("journeymapcommand. '/%s help'", "journeymapcommand")),
      "jmc"
    ).handler(ctx -> {
      final Audience sender = FabricClientAudiences.of().audience();

      sender.sendMessage(Component.text()
        .append(Component.text("JourneyMapCommand v" + BuildParameters.VERSION)
          .hoverEvent(HoverEvent.showText(Component.textOfChildren(
            Component.text("Git Commit: " + BuildParameters.GIT_COMMIT),
            Component.text("Git Branch: " + BuildParameters.GIT_BRANCH),
            Component.text("Fabric Loader Version: " + BuildParameters.FABRIC_LOADER_VERSION),
            Component.text("Fabric API Version: " + BuildParameters.FABRIC_API_VERSION)
          ))))
        .append(Component.text(" - "))
        .append(Component.text("Type '/jmc help' for a list of commands."))
      );
    });
  }

  public CommandManager<FabricClientCommandSource> commandManager() {
    return this.commandManager;
  }
}

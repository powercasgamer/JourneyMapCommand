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

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.component.TypedCommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.minecraft.extras.MinecraftHelp.helpColors;

@DefaultQualifier(NonNull.class)
public final class HelpCommand extends JMCCommand {
  private final MinecraftHelp<FabricClientCommandSource> minecraftHelp;
  private final TypedCommandComponent<FabricClientCommandSource, String> helpQueryArgument;

  public HelpCommand(final CommandService commands) {
    super(commands);
    this.minecraftHelp = createMinecraftHelp(commands.commandManager());
    this.helpQueryArgument = createHelpQueryArgument(commands);
  }

  @Override
  public void register() {
    this.commands.registerSubcommand(builder ->
      builder.literal("help")
        .commandDescription(description("help"))
        .argument(this.helpQueryArgument)
        .permission("journeymapcommand.command.help")
        .handler(this::executeHelp));
  }

  private void executeHelp(final CommandContext<FabricClientCommandSource> context) {
    this.minecraftHelp.queryCommands(context.get(this.helpQueryArgument), context.sender());
  }

  private static TypedCommandComponent<FabricClientCommandSource, String> createHelpQueryArgument(final CommandService commands) {
    final var commandHelpHandler = commands.commandManager().createHelpHandler();
    final BlockingSuggestionProvider.Strings<FabricClientCommandSource> suggestions = (context, input) ->
      commandHelpHandler.queryRootIndex(context.sender()).entries().stream()
        .map(CommandEntry::syntax)
        .toList();
    return CommandComponent.<FabricClientCommandSource, String>ofType(String.class, "query")
      .parser(StringParser.greedyStringParser())
      .suggestionProvider(suggestions)
      .optional()
      .defaultValue(DefaultValue.constant(""))
      .description(description("help"))
      .build();
  }

  private static MinecraftHelp<FabricClientCommandSource> createMinecraftHelp(final CommandManager<FabricClientCommandSource> manager) {
    return MinecraftHelp.<FabricClientCommandSource>builder()
      .commandManager(manager)
      .audienceProvider(sender -> FabricClientAudiences.of().audience())
      .commandPrefix(String.format("/%s help", "journeymapcommand"))
      .colors(helpColors(
        TextColor.color(0x5B00FF),
        NamedTextColor.WHITE,
        TextColor.color(0xC028FF),
        NamedTextColor.GRAY,
        NamedTextColor.DARK_GRAY
      ))
//      .messageProvider(HelpCommand::helpMessage)
      .build();
  }
}

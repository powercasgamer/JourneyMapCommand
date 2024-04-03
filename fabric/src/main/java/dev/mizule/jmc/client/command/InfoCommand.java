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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class InfoCommand extends JMCCommand {

  protected InfoCommand(CommandService commands) {
    super(commands);
  }

  @Override
  public void register() {
    this.commands.registerSubcommand(builder -> builder.literal("info")
      .handler(ctx -> {
        final Audience sender = FabricClientAudiences.of().audience();

        sender.sendMessage(Component.text("JourneyMapCommand v" + BuildParameters.SHORT_VERSION));
        sender.sendMessage(Component.text("Full Version: " + BuildParameters.VERSION));
        sender.sendMessage(Component.text("Git Commit: " + BuildParameters.GIT_COMMIT)
          .clickEvent(ClickEvent.openUrl(
            BuildParameters.GIT_URL + "/commit/" + BuildParameters.GIT_COMMIT
          )).hoverEvent(HoverEvent.showText(Component.text("Click to view the commit on GitHub."))));
        sender.sendMessage(Component.text("Git Branch: " + BuildParameters.GIT_BRANCH)
          .clickEvent(ClickEvent.openUrl(
            BuildParameters.GIT_URL + "/tree/" + BuildParameters.GIT_BRANCH
          )).hoverEvent(HoverEvent.showText(Component.text("Click to view the branch on GitHub."))));
        sender.sendMessage(Component.text("Git Tag: " + BuildParameters.GIT_TAG)
          .clickEvent(ClickEvent.openUrl(
            BuildParameters.GIT_URL + "/releases/tag/" + BuildParameters.GIT_TAG
          )).hoverEvent(HoverEvent.showText(Component.text("Click to view the tag on GitHub."))));

        sender.sendMessage(Component.text("Fabric Loader Version: " + BuildParameters.FABRIC_LOADER_VERSION));
        sender.sendMessage(Component.text("Fabric API Version: " + BuildParameters.FABRIC_API_VERSION));
        sender.sendMessage(Component.text("Minecraft Version: " + BuildParameters.MINECRAFT_VERSION));
      }));
  }
}

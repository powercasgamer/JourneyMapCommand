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

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.util.ComponentMessageThrowable;
import net.minecraft.network.chat.ComponentUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.PrintWriter;
import java.io.StringWriter;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.copyToClipboard;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

@DefaultQualifier(NonNull.class)
public class ExceptionHandler {

  private static void decorateWithHoverStacktrace(final TextComponent.Builder message, final Throwable cause) {
    final StringWriter writer = new StringWriter();
    cause.printStackTrace(new PrintWriter(writer));
    final String stackTrace = writer.toString().replaceAll("\t", "    ");
    final TextComponent.Builder hoverText = text();
    final @Nullable Component throwableMessage = componentMessage(cause);
    if (throwableMessage != null) {
      hoverText.append(throwableMessage)
        .append(newline())
        .append(newline());
    }
    hoverText.append(text(stackTrace))
      .append(newline())
      .append(text("    "))
      .append(Component.text("Click to copy").color(GRAY).decorate(ITALIC));

    message.hoverEvent(hoverText.build());
    message.clickEvent(copyToClipboard(stackTrace));
  }

  private static @Nullable Component componentMessage(final Throwable cause) {
    if (cause instanceof ComponentMessageThrowable || !(cause instanceof CommandSyntaxException commandSyntaxException)) {
      return ComponentMessageThrowable.getOrConvertMessage(cause);
    }

    // Fallback for when CommandSyntaxException isn't a ComponentMessageThrowable
    final net.minecraft.network.chat.Component component = ComponentUtils.fromMessage(commandSyntaxException.getRawMessage());
    return GsonComponentSerializer.gson().deserializeFromTree(net.minecraft.network.chat.Component.Serializer.toJsonTree(component));
  }
}

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
package dev.mizule.jmc.client.util;

/*
MIT License
Copyright (c) 2021-2024 Jason Penilla & Contributors
Copyright (c) 2020-2021 William Blake Galbreath & Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.regex.Pattern;

@DefaultQualifier(NonNull.class)
public final class Components {
  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
  private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[^\\s\\w\\-]");

  public static MiniMessage miniMessage() {
    return MINI_MESSAGE;
  }

  public static Component miniMessage(final String miniMessage) {
    return miniMessage().deserialize(miniMessage);
  }

  public static Component miniMessage(final String miniMessage, final TagResolver... templates) {
    return miniMessage().deserialize(miniMessage, templates);
  }

  public static TagResolver.Single placeholder(final String name, final ComponentLike value) {
    return Placeholder.component(name, value);
  }

  public static TagResolver.Single placeholder(final String name, final Object value) {
    return Placeholder.unparsed(name, value.toString());
  }

  public static TagResolver.Single worldPlaceholder(final ServerLevel level) {
    return placeholder("world", level.dimension().location());
  }

  public static TagResolver.Single playerPlaceholder(final ServerPlayer player) {
    return placeholder("player", player.getGameProfile().getName());
  }

  public static Component highlightSpecialCharacters(final Component component, final TextColor highlightColor) {
    return highlight(component, SPECIAL_CHARACTERS_PATTERN, highlightColor);
  }

  public static Component highlight(final Component component, final Pattern highlight, final TextColor highlightColor) {
    return component.replaceText(config -> {
      config.match(highlight);
      config.replacement(match -> match.color(highlightColor));
    });
  }
}

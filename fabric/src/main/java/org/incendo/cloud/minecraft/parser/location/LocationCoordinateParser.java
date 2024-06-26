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
package org.incendo.cloud.minecraft.parser.location;

//
// MIT License
//
// Copyright (c) 2024 Incendo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.standard.DoubleParser;

/**
 * A single coordinate, meant to be used as an element in a position vector
 *
 * @param <C> Command sender type
 * @since 1.1.0
 */
public final class LocationCoordinateParser<C> implements ArgumentParser<C, LocationCoordinate> {

  @Override
  public @NonNull ArgumentParseResult<@NonNull LocationCoordinate> parse(
    final @NonNull CommandContext<@NonNull C> commandContext,
    final @NonNull CommandInput commandInput
  ) {
    final String input = commandInput.skipWhitespace().peekString();

    /* Determine the type */
    final LocationCoordinateType locationCoordinateType;
    if (commandInput.peek() == '^') {
      locationCoordinateType = LocationCoordinateType.LOCAL;
      commandInput.moveCursor(1);
    } else if (commandInput.peek() == '~') {
      locationCoordinateType = LocationCoordinateType.RELATIVE;
      commandInput.moveCursor(1);
    } else {
      locationCoordinateType = LocationCoordinateType.ABSOLUTE;
    }

    final double coordinate;
    try {
      final boolean empty = commandInput.peekString().isEmpty() || commandInput.peek() == ' ';
      coordinate = empty ? 0 : commandInput.readDouble();

      // You can have a prefix without a number, in which case we wouldn't consume the
      // subsequent whitespace. We do it manually.
      if (commandInput.hasRemainingInput() && commandInput.peek() == ' ') {
        commandInput.read();
      }
    } catch (final Exception e) {
      return ArgumentParseResult.failure(new DoubleParser.DoubleParseException(
        input,
        new DoubleParser<>(
          DoubleParser.DEFAULT_MINIMUM,
          DoubleParser.DEFAULT_MAXIMUM
        ),
        commandContext
      ));
    }

    return ArgumentParseResult.success(
      LocationCoordinate.of(
        locationCoordinateType,
        coordinate
      )
    );
  }
}

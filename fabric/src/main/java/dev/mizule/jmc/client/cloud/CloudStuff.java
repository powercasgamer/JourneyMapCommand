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
package dev.mizule.jmc.client.cloud;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.brigadier.parser.WrappedBrigadierParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.modded.data.Coordinates;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;

import java.util.concurrent.CompletableFuture;

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
public class CloudStuff {

  /**
   * A parser for block coordinates.
   *
   * @param <C> sender type
   * @return a parser descriptor
   */
  public static <C> @NonNull ParserDescriptor<C, Coordinates.BlockCoordinates> blockPosParser() {
    ArgumentParser<C, Coordinates.BlockCoordinates> parser = new WrappedBrigadierParser<C,
      net.minecraft.commands.arguments.coordinates.Coordinates>(BlockPosArgument.blockPos())
      .flatMapSuccess(CloudStuff::mapToCoordinates);

    return ParserDescriptor.of(parser, Coordinates.BlockCoordinates.class);
  }

  /**
   * A parser for column coordinates.
   *
   * @param <C> sender type
   * @return a parser descriptor
   */
  public static <C> @NonNull ParserDescriptor<C, Coordinates.ColumnCoordinates> columnPosParser() {
    ArgumentParser<C, Coordinates.ColumnCoordinates> parser = new WrappedBrigadierParser<C,
      net.minecraft.commands.arguments.coordinates.Coordinates>(ColumnPosArgument.columnPos())
      .flatMapSuccess(CloudStuff::mapToCoordinates);

    return ParserDescriptor.of(parser, Coordinates.ColumnCoordinates.class);
  }

  /**
   * A parser for coordinates, relative or absolute, from 2 doubles for x and z,
   * with y always defaulting to 0.
   *
   * @param centerIntegers whether to center integers at x.5
   * @param <C>            sender type
   * @return a parser descriptor
   */
  public static <C> @NonNull ParserDescriptor<C, Coordinates.CoordinatesXZ> vec2Parser(final boolean centerIntegers) {
    ArgumentParser<C, Coordinates.CoordinatesXZ> parser = new WrappedBrigadierParser<C,
      net.minecraft.commands.arguments.coordinates.Coordinates>(new Vec2Argument(centerIntegers))
      .flatMapSuccess(CloudStuff::mapToCoordinates);

    return ParserDescriptor.of(parser, Coordinates.CoordinatesXZ.class);
  }

  /**
   * A parser for coordinates, relative or absolute, from 3 doubles.
   *
   * @param centerIntegers whether to center integers at x.5
   * @param <C>            sender type
   * @return a parser descriptor
   */
  public static <C> @NonNull ParserDescriptor<C, Coordinates> vec3Parser(final boolean centerIntegers) {
    ArgumentParser<C, Coordinates> parser = new WrappedBrigadierParser<C,
      net.minecraft.commands.arguments.coordinates.Coordinates>(Vec3Argument.vec3(centerIntegers))
      .flatMapSuccess(CloudStuff::mapToCoordinates);

    return ParserDescriptor.of(parser, Coordinates.class);
  }

  @SuppressWarnings("unchecked")
  public static <C, O extends Coordinates> @NonNull CompletableFuture<@NonNull ArgumentParseResult<O>> mapToCoordinates(
    final @NonNull CommandContext<C> ctx,
    final net.minecraft.commands.arguments.coordinates.@NonNull Coordinates posArgument
  ) {
    return ArgumentParseResult.successFuture((O) new CoordinatesImpl((FabricClientCommandSource) ctx.sender(), posArgument));
//    return requireServer(  // ?????
//      ctx,
//      serverCommandSource -> ArgumentParseResult.successFuture((O) new CoordinatesImpl(
//        null, posArgument
//      ))
//    );
  }

  private record CoordinatesImpl(FabricClientCommandSource source, net.minecraft.commands.arguments.coordinates.Coordinates wrappedCoordinates)
    implements Coordinates,
    Coordinates.CoordinatesXZ,
    Coordinates.BlockCoordinates,
    Coordinates.ColumnCoordinates {

    @Override
    public @NonNull Vec3 position() {
      return this.wrappedCoordinates.getPosition(this.source.getClient().player.createCommandSourceStack());
//      return this.source.getPosition();
    }

    @Override
    public @NonNull BlockPos blockPos() {
      return BlockPos.containing(this.position());
    }

    @Override
    public boolean isXRelative() {
      return this.wrappedCoordinates.isXRelative();
    }

    @Override
    public boolean isYRelative() {
      return this.wrappedCoordinates.isYRelative();
    }

    @Override
    public boolean isZRelative() {
      return this.wrappedCoordinates.isZRelative();
    }
  }

}

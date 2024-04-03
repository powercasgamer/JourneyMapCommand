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

import com.mojang.logging.LogUtils;
import dev.mizule.jmc.BuildParameters;
import dev.mizule.jmc.client.command.CommandService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.fabric.FabricClientCommandManager;
import org.slf4j.Logger;

public class JMCClientMod implements ClientModInitializer {

  public static final Logger LOGGER = LogUtils.getLogger();
  private final CommandService commandService;
  private final FabricClientCommandManager<FabricClientCommandSource> commandManager;
  private final ModContainer modContainer;

  public JMCClientMod() {
    this.modContainer = FabricLoader.getInstance().getModContainer(BuildParameters.MOD_ID).orElseThrow(() -> new RuntimeException("Could not get the JMC mod container."));
    this.commandManager = new FabricClientCommandManager<>(
      ExecutionCoordinator.simpleCoordinator(),
      SenderMapper.identity()
    );


    commandService = new CommandService(commandManager);
    commandService.registerCommands();
  }

  public CommandService commandService() {
    return commandService;
  }

  public ModContainer modContainer() {
    return modContainer;
  }

  @Override
  public void onInitializeClient() {
    ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
      LOGGER.info("Hello Fabric world!");
    });
  }
}

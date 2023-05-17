package com.blay.blaysutils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.DedicatedServerModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import com.mojang.logging.LogUtils;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.Command;

import net.minecraft.server.command.CommandManager;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.text.Text;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

public class BlaysUtils implements DedicatedServerModInitializer {
    // Config's format
    public class JsonConfig {
        public boolean disable_info=false;
        public String[] info_text = {
            "§6INFORMATION ABOUT THE SERVER",
            "§6A SECONDARY LINE"
        };

        public boolean disable_rules=false;
        public String[] rules_text = {
            "§cTHE RULES",
            "§c1. SOMETHING"
        };

        public boolean disable_hello_message=false;
        public String[] hello_text = {
            "§4HELLO, %p!",
            "§4100%% FUN"
        };

        public String broadcast_chat_prefix = "§c[!] ";
        public String broadcast_title_prefix = "§c";
    }
    public static JsonConfig config;

    // Loading the config
    private void updateConfig() {
        try {
            File configFile = FabricLoader.getInstance().getConfigDir().resolve("blays-utils.json").toFile();

            if(!(configFile.exists() && configFile.isFile())){
                configFile.delete();
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();

                FileWriter fileWriter=new FileWriter(configFile);
                fileWriter.write("{\n    \"disable_info\": false,\n    \"info_text\": [\n        \"§6INFORMATION ABOUT THE SERVER\",\n        \"§6A SECONDARY LINE\"\n    ],\n\n    \"disable_rules\": false,\n    \"rules_text\": [\n        \"§cTHE RULES\",\n        \"§c1. SOMETHING\"\n    ],\n\n    \"disable_hello_message\": false,\n    \"hello_text\": [\n        \"§4HELLO, %p!\",\n        \"§4100%% FUN\"\n    ],\n\n    \"broadcast_chat_prefix\": \"§c[!] \",\n    \"broadcast_title_prefix\": \"§c\"\n}");
                fileWriter.close();
            }

            FileReader fileReader=new FileReader(configFile);
            config = new Gson().fromJson(fileReader, JsonConfig.class);
            fileReader.close();
        }
        catch(Exception e)
        {
            LogUtils.getLogger().error(LogUtils.FATAL_MARKER, "An error occured while loading blay's server utilities' config!", e);
        }
    }
 
    @Override
    public void onInitializeServer() {
        LogUtils.getLogger().info("Loading Blay's Server Utilities...");

        // Updating the config on startup
        updateConfig();

        // Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // Rules command
            if(!config.disable_rules) {
                dispatcher.register(
                    (LiteralArgumentBuilder)CommandManager.literal("rules")
                    .executes(context -> {
                        if(config.disable_rules) {
                            context.getSource().sendMessage(Text.literal("§cThe /rules command is disabled! Please notify the administration, that they should reset the server, or the players will be able to use the command, and get that message."));
                        } else {
                            for(String line:config.rules_text) {
                                context.getSource().sendMessage(Text.literal(line));
                            }
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                );
            }

            // Info command
            if(!config.disable_info) {
                LiteralCommandNode informationLiteralCommandNode = dispatcher.register(
                    (LiteralArgumentBuilder)CommandManager.literal("information")
                    .executes(context -> {
                        if(config.disable_info) {
                            context.getSource().sendMessage(Text.literal("§cThe /info command is disabled! Please notify the administration, that they should reset the server, or the players will be able to use the command, and get that message."));
                        } else {
                            for(String line:config.info_text) {
                                context.getSource().sendMessage(Text.literal(line));
                            }
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                );
                dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("info").redirect(informationLiteralCommandNode));
            }

            // Broadcast command
            LiteralCommandNode broadcastLiteralCommandNode = dispatcher.register(
                (LiteralArgumentBuilder)CommandManager.literal("broadcast")
                .requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.argument("message", MessageArgumentType.message()))
                .executes(context -> {
                    System.out.println("test");
                    System.out.println(MessageArgumentType.getMessage(context, "message").getString());
                    return Command.SINGLE_SUCCESS;
                })
            );
            dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("bc").redirect(broadcastLiteralCommandNode));

            // Config reload command
            LiteralCommandNode blaysUtilsReloadLiteralCommandNode = dispatcher.register(
                (LiteralArgumentBuilder)CommandManager.literal("blays-utilities-reload")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(context -> {
                    context.getSource().sendMessage(Text.literal("Reloading blay's server utilities' config..."));
                    updateConfig();
                    return Command.SINGLE_SUCCESS;
                })
            );
            dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("blays-utilities-reload").redirect(blaysUtilsReloadLiteralCommandNode));
            dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("blays-server-utils-reload").redirect(blaysUtilsReloadLiteralCommandNode));
            dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("blays-server-utilities-reload").redirect(blaysUtilsReloadLiteralCommandNode));
        });
    }
}

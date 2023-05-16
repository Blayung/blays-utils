package com.blay.blaysutils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.DedicatedServerModInitializer;

import com.mojang.logging.LogUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.Command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

public class BlaysUtils implements DedicatedServerModInitializer {
    public class JsonConfig {
        public String[] info_text = {
            "§6INFORMATION ABOUT THE SERVER",
            "§6A SECONDARY LINE"
        };

        public String[] rules_text = {
            "§cTHE RULES",
            "§c1. SOMETHING"
        };

        public String[] hello_text = {
            "§4HELLO, %p!",
            "§4A SECONDARY LINE"
        };

        public String broadcast_chat_prefix = "§c[!] ";
        public String broadcast_title_prefix = "§c";
    }
    public static JsonConfig config;

    private void updateConfig(){
        try {
            File configFile = FabricLoader.getInstance().getConfigDir().resolve("blays-utils.json").toFile();

            if(!(configFile.exists() && configFile.isFile())){
                configFile.delete();
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();

                FileWriter fileWriter=new FileWriter(configFile);
                fileWriter.write("{\n    \"info_text\": [\n        \"§6INFORMATION ABOUT THE SERVER\",\n        \"§6A SECONDARY LINE\"\n    ],\n\n    \"rules_text\": [\n        \"§cTHE RULES\",\n        \"§c1. SOMETHING\"\n    ],\n\n    \"hello_text\": [\n        \"§4HELLO, %p!\",\n        \"§4A SECONDARY LINE\"\n    ]\n\n    \"broadcast_chat_prefix\": \"§c[!] \"\n    \"broadcast_title_prefix\": \"§c\"}");
                fileWriter.close();
            }

            FileReader fileReader=new FileReader(configFile);
            config = new Gson().fromJson(fileReader, JsonConfig.class);
            fileReader.close();
        }
        catch(Exception e)
        {
            CrashReport crashReport = MinecraftClient.getInstance().addDetailsToCrashReport(new CrashReport("Blay's utils config error", e));
            LogUtils.getLogger().error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", e);
            MinecraftClient.getInstance().cleanUpAfterCrash();
            MinecraftClient.getInstance().printCrashReport(crashReport);
        }
    }
 
    @Override
    public void onInitializeServer() {
        LogUtils.getLogger().info("Loading Blay's Utils...");

        updateConfig();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("blays-utils-reload")
            .requires(source -> source.hasPermissionLevel(4))
            .executes(context -> {
                context.getSource().sendMessage(Text.literal("Reloading blay's utils' config..."));
                updateConfig();
                return Command.SINGLE_SUCCESS;
            })
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("info")
            .executes(context -> {
                for(String line:config.info){
                    context.getSource().sendMessage(Text.literal(line));
                }
                return Command.SINGLE_SUCCESS;
            })
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("rules")
            .executes(context -> {
                for(String line:config.rules){
                    context.getSource().sendMessage(Text.literal(line));
                }
                return Command.SINGLE_SUCCESS;
            })
        ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("broadcast")
            .executes(context -> {
                System.out.println("todo");
                return Command.SINGLE_SUCCESS;
            })
        ));
    }
}

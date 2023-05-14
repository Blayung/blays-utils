package com.blay.blaysutils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ServerMessageEvents;

import com.mojang.logging.LogUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

public class BlaysUtilsMain implements DedicatedServerModInitializer {
    private class JsonConfig {
        public String[] info = [
            "§6asd",
            "§6das"
        ];

        public String[] rules = [
            "§casd",
            "§cdas"
        ];

        public String[] hello = [
            "§4asd, %p",
            "§4%%, das"
        ];
    }
    private JsonConfig config;
 
    @Override
    public void onInitializeServer() {
        try {
            File configFile = FabricLoader.getInstance().getConfigDir().resolve("blays-utils.json").toFile();

            if(!(configFile.exists() && configFile.isFile())){
                configFile.delete();
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();

                FileWriter fileWriter=new FileWriter(configFile);
                fileWriter.write("{\n    \"info\": [\n        \"§6asd\",\n        \"§6das\"\n    ],\n\n    \"rules\": [\n        \"§casd\",\n        \"§cdas\"\n    ],\n\n    \"hello\": [\n        \"§4asd, %p\",\n        \"§4%%, das\"\n    ]\n}");
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

        for (String i:config.info){
            LogUtils.getLogger().info(i);
        }
        for (String i:config.rules){
            LogUtils.getLogger().info(i);
        }
        for (String i:config.hello){
            LogUtils.getLogger().info(i);
        }

        ServerMessageEvents.CHAT_MESSAGE.register(server -> {
            LogUtils.getLogger().info("test");
        });
    }
}

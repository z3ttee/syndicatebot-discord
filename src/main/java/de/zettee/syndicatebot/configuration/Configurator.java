package de.zettee.syndicatebot.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class Configurator {

    @Getter private static HashMap<String, Config> configs = new HashMap<>();
    @Getter private static Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();

    public static Config ofGuild(Guild guild){
        Config config = new Config(guild);

        if(configs.containsKey(guild.getId())) {
            return configs.getOrDefault(guild.getId(), config);
        }

        try {
            if (config.getFile().exists()) {
                config = gson.fromJson(new FileReader(config.getFile()), Config.class);
                configs.put(guild.getId(), config);
            } else {
                if(config.create()){
                    configs.put(guild.getId(), config);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return config;
    }

    public static void createInstance() {
        File configsDir = new File(System.getProperty("user.dir")+"/configs/");
        if(!configsDir.exists()) configsDir.mkdirs();
    }


    public static class Config {

        @Getter private transient String guildID;
        @Getter private String prefix = "ss ";
        @Getter private String guildName;
        @Getter private int volume = 30;
        @Getter private transient File file;

        public Config(Guild guild) {
            this.guildID = guild.getId();
            this.guildName = guild.getName();

            file = new File(System.getProperty("user.dir")+"/configs/", guildID+".json");
        }

        public boolean create(){
            if(file.exists()) return true;
            return writeToFile();
        }
        public boolean delete(){
            return file.delete();
        }
        public boolean writeToFile(){
            try {
                String s = gson.toJson(this);
                FileWriter writer = new FileWriter(getFile());

                writer.write(s);
                writer.flush();
                writer.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
            writeToFile();
        }
        public void setGuildName(String guildName) {
            this.guildName = guildName;
            writeToFile();
        }
        public void setVolume(int volume) {
            this.volume = volume;
            writeToFile();
        }
    }
}

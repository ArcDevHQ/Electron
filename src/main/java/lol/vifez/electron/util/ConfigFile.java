package lol.vifez.electron.util;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class ConfigFile {

    private final JavaPlugin plugin;
    private final File file;
    private final String defaultFileName;
    private FileConfiguration configuration;

    public ConfigFile(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.defaultFileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        ensureExists();
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public double getDouble(String path) {
        return configuration.getDouble(path);
    }

    public int getInt(String path) {
        return configuration.getInt(path);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    public long getLong(String path) {
        return configuration.getLong(path);
    }

    public String getString(String path) {
        return color(configuration.getString(path));
    }

    public String getString(String path, String fallback, boolean colorize) {
        String value = configuration.getString(path);
        if (value == null) {
            return fallback;
        }
        return colorize ? color(value) : value;
    }

    public List<String> getReversedStringList(String path) {
        List<String> list = new ArrayList<>(getStringList(path));
        Collections.reverse(list);
        return list;
    }

    public List<String> getStringList(String path) {
        List<String> values = configuration.getStringList(path);
        if (values == null) {
            return Collections.emptyList();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(this::color)
                .collect(Collectors.toList());
    }

    public List<String> getStringListOrDefault(String path, List<String> fallback) {
        List<String> values = configuration.getStringList(path);
        if (values == null || values.isEmpty()) {
            return fallback == null ? Collections.emptyList() : new ArrayList<>(fallback);
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(this::color)
                .collect(Collectors.toList());
    }

    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config file " + file.getName());
        }
    }

    public void reload() {
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    private void ensureExists() {
        if (!file.exists()) {
            plugin.saveResource(defaultFileName, false);
        }
    }

    private String color(String value) {
        return value == null ? null : ChatColor.translateAlternateColorCodes('&', value);
    }
}

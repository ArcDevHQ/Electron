package lol.vifez.electron.profile;

import com.mongodb.client.model.Filters;
import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.listener.ProfileListener;
import lol.vifez.electron.profile.repository.ProfileRepository;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileManager {

    private final ProfileRepository repository;
    private final Map<UUID, Profile> profiles = new ConcurrentHashMap<>();

    public ProfileManager(ProfileRepository repository) {
        this.repository = repository;
        new ProfileListener(JavaPlugin.getPlugin(Practice.class));
    }

    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public Profile getProfile(String name) {
        return profiles.values().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void save(Profile profile) {
        profiles.put(profile.getUuid(), profile);
    }

    public void delete(Profile profile) {
        profiles.remove(profile.getUuid());
        repository.getCollection().deleteOne(Filters.eq("_id", profile.getUuid().toString()));
    }

    public void close() {
        profiles.values().forEach(profile -> repository.saveData(profile.getUuid().toString(), profile).join());
    }
}

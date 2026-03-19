package lol.vifez.electron.profile.menu;

import lol.vifez.electron.game.elo.EloUtil;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ProfileMenu extends Menu {

    private final Profile targetProfile;

    @Override
    public String getTitle(Player player) {
        return "&7Profile (" + targetProfile.getName() + ")";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        ItemStack head = buildHead();
        buttons.put(4, new EasyButton(head, true, false, () -> {}));

        int[] kitSlots = {10, 11, 12, 14, 15, 16};
        int index = 0;

        for (String kitName : targetProfile.getKitLoadout().keySet()) {
            if (index >= kitSlots.length) {
                break;
            }

            ItemStack kitItem = new ItemBuilder(Material.DIAMOND_SWORD)
                    .name("&a" + kitName)
                    .lore("&7Click to view kit")
                    .build();

            buttons.put(kitSlots[index], new EasyButton(kitItem, true, false, () -> {}));
            index++;
        }

        return buttons;
    }

    private ItemStack buildHead() {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(targetProfile.getName());
        meta.setDisplayName(CC.translate("&b" + targetProfile.getName() + "'s Profile"));

        meta.setLore(CC.translate(new ArrayList<>(Arrays.asList(
                "&7Global ELO: &f" + EloUtil.getGlobalElo(targetProfile),
                "&7Division: &f" + targetProfile.getDivision().getPrettyName()
        ))));
        head.setItemMeta(meta);
        return head;
    }
}

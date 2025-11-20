    package org.ccoding.identitycore.commands;

    import org.bukkit.command.Command;
    import org.bukkit.command.CommandExecutor;
    import org.bukkit.command.CommandSender;
    import org.bukkit.entity.Player;
    import org.ccoding.identitycore.IdentityCore;
    import org.ccoding.identitycore.menus.ColorMenu;
    import org.ccoding.identitycore.utils.MessageUtils;
    import org.ccoding.identitycore.utils.PermissionUtils;
    import org.jetbrains.annotations.NotNull;

    // Importamos el TabModule
    import org.ccoding.identitycore.modules.tab.TabModule;
    import org.ccoding.identitycore.menus.AdvancedEditorMenu;
    import org.ccoding.identitycore.listeners.AdvancedEditorListener;

    public class NickCommand implements CommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            if (!(sender instanceof Player)) {
                MessageUtils.sendMessage(sender, "only-players");
                return true;
            }

            Player player = (Player) sender;

            if (!PermissionUtils.canUseNick(player)) {
                MessageUtils.sendMessage(player, "no-nick-permission");
                return true;
            }

            if (args.length == 0) {
                sendNickHelp(player);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "help":
                    sendNickHelp(player);
                    break;

                case "restore":
                    restoreNick(player);
                    break;

                case "settings":
                    if (!PermissionUtils.canUseBasicMenu(player)) {
                        MessageUtils.sendMessage(player, "no-menu-permission");
                        return true;
                    }
                    openColorMenu(player);
                    break;

                case "advanced":
                    if (!PermissionUtils.canUseAdvancedMenu(player)) {
                        MessageUtils.sendMessage(player, "no-advanced-permission");
                        return true;
                    }
                    openAdvancedEditor(player);
                    break;

                default:
                    setNick(player, args[0]);
                    break;
            }

            return true;
        }

        private void sendNickHelp(Player player) {
            player.sendMessage("§8§m-------------------§r §6§lNick §8§m-------------------");
            player.sendMessage(" §8▪ §7/nick <nombre> §8- §fCambiar tu nick");
            player.sendMessage(" §8▪ §7/nick restore §8- §fVolver a tu nombre original");
            player.sendMessage(" §8▪ §7/nick settings §8- §fColores y formatos");

            if (PermissionUtils.canUseAdvancedMenu(player)) {
                player.sendMessage(" §8▪ §7/nick advanced §8- §6Editor avanzado");
            }

            player.sendMessage(" §8▪ §7/nick help §8- §fEsta ayuda");
            player.sendMessage("§8§m------------------------------------------------");
        }

        private void setNick(Player player, String nick) {
            if (nick.length() < 3) {
                MessageUtils.sendMessage(player, "nick-too-short");
                return;
            }

            if (nick.length() > 16) {
                MessageUtils.sendMessage(player, "nick-too-long");
                return;
            }

            if (!isValidNick(nick)) {
                MessageUtils.sendMessage(player, "nick-invalid");
                return;
            }

            IdentityCore.getInstance().getNickManager().setNick(player, nick);
            MessageUtils.sendMessage(player, "nick-changed", "%nick%", nick);

            // Actualizar TabModule
            updateTabModule(player);

            player.sendMessage("§7Ahora tu nombre aparecerá como: §e" + nick + " §7en el chat");
        }

        private void restoreNick(Player player) {
            IdentityCore.getInstance().getNickManager().restoreOriginalName(player);
            MessageUtils.sendMessage(player, "nick-restored", "%name%", player.getName());

            // Actualizar TabModule
            updateTabModule(player);

            player.sendMessage("§7Ahora tu nombre aparecerá como: §e" + player.getName() + " §7en el chat");
        }

        private void updateTabModule(Player player) {
            try {
                TabModule tabModule = (TabModule) IdentityCore.getInstance().getModule("TabModule");

                if (tabModule != null && tabModule.isEnabled()) {
                    tabModule.updatePlayerTabName(player);
                }
            } catch (Exception e) {
                IdentityCore.getInstance().getLogger().warning("No se pudo actaulizar TabModule: " + e.getMessage());
            }
        }

        private void openColorMenu(Player player) {
            // Abrir el menú de colores
            ColorMenu colorMenu = new ColorMenu(player);

            colorMenu.openColorMenu();
        }

        private void openAdvancedEditor(Player player) {
            if (!player.hasPermission("identitycore.advanced")) {
                MessageUtils.sendMessage(player, "no-permission");
                return;
            }

            // Abrir el menú de editor avanzado
            AdvancedEditorMenu advancedMenu = new AdvancedEditorMenu(player);
            advancedMenu.openAdvancedEditor();

            // Registrar en el listener
            IdentityCore.getInstance().getAdvancedEditorListener().registerEditor(player, advancedMenu);
        }

        private boolean isValidNick(String nick) {
            return nick.matches("[a-zA-Z0-9_]+");
        }
    }
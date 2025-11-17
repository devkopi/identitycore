package org.ccoding.identitycore.modules.tab;

import org.ccoding.identitycore.modules.Module;
import org.ccoding.identitycore.IdentityCore;

public class TabModule implements Module {

    private final IdentityCore plugin;
    private boolean enabled = false;

    public TabModule(IdentityCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        enabled = true;
        plugin.getLogger().info("TabModule activado");

        // Acá registramos los eventos del tabulador
    }

    @Override
    public void disable() {
        enabled = false;
        plugin.getLogger().info("TabModule desactivado");
    }

    @Override
    public String getName() {
        return "TabModule";
    }

    public boolean isEnabled() {
        return enabled;
    }
}
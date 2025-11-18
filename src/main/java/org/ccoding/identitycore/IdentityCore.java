package org.ccoding.identitycore;

import org.ccoding.identitycore.commands.IdentityCoreCommand;
import org.ccoding.identitycore.commands.NickCommand;
import org.ccoding.identitycore.listeners.ColorListener;
import org.ccoding.identitycore.managers.NickManager;
import org.ccoding.identitycore.modules.Module;
import org.ccoding.identitycore.modules.chat.ChatModule;
import org.ccoding.identitycore.modules.tab.TabModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class IdentityCore extends JavaPlugin {

    private static IdentityCore instance;
    private Map<String, Module> modules;
    private NickManager nickManager;

    //  REGISTRAR COMANDOS EN onLoad() en lugar de onEnable()
    @Override
    public void onLoad() {
        // Los comandos se registran automáticamente desde plugin.yml
        // No necesitamos hacer nada aquí
        getLogger().info("IdentityCore cargando...");
    }

    @Override
    public void onEnable() {
        instance = this;

        // Cargar configuración
        saveDefaultConfig();

        //  REGISTRAR EXECUTORS después de que todo esté listo
        registerCommandExecutors();

        // Registrar listeners
        registerListeners();

        // Inicializar NickManager
        this.nickManager = new NickManager(this);

        // Inicializar sistema de módulos
        initializeModules();



        getLogger().info("¡IdentityCore ha sido activado correctamente!");
        getLogger().info("Version: 1.0");
    }

    @Override
    public void onDisable() {
        // Guardar datos de nicks
        if (nickManager != null) {
            nickManager.saveAllData();
        }

        // Desactivar módulos
        if (modules != null) {
            modules.values().forEach(Module::disable);
        }

        getLogger().info("¡IdentityCore ha sido desactivado!");
    }

    private void initializeModules() {
        modules = new HashMap<>();

        // Registrar módulos
        registerModule(new ChatModule(this));
        registerModule(new TabModule(this));

        // Activar módulos
        modules.values().forEach(Module::enable);

        getLogger().info("Modulos cargados: " + modules.size());
    }

    private void registerModule(Module module) {
        modules.put(module.getName(), module);
    }

    private void registerCommandExecutors() {
        try {
            getServer().getPluginManager().registerEvents(new ColorListener(), this);
            //  Solo establecer los executors, no registrar los comandos
            if (getCommand("identitycore") != null) {
                getCommand("identitycore").setExecutor(new IdentityCoreCommand());
            }
            if (getCommand("nick") != null) {
                getCommand("nick").setExecutor(new NickCommand());
            }

            getLogger().info("Executors de comandos registrados");
        } catch (Exception e) {
            getLogger().severe("Error registrando executors: " + e.getMessage());
        }
    }

    public NickManager getNickManager() {
        return nickManager;
    }

    public static IdentityCore getInstance() {
        return instance;
    }


    // Registro de listeners
    private void registerListeners() {
        try {
            getServer().getPluginManager().registerEvents(new ColorListener(), this);
            getLogger().info("Listeners registrados correctamente");
        } catch (Exception e) {
            getLogger().severe("Error registrando listeners: " + e.getMessage());
        }
    }

    // Obtener un módulo por nombre

    public Module getModule(String name) {
        if (modules != null) {
            return modules.get(name);
        }
        return null;
    }
}
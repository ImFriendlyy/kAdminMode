package vv0ta3fa9.plugin.kAdminMode.utils.Color.impl;

import vv0ta3fa9.plugin.kAdminMode.kAdminMode;
import vv0ta3fa9.plugin.kAdminMode.utils.Color.Colorizer;

public class VanillaColorizer implements Colorizer {

    private final kAdminMode plugin;

    public VanillaColorizer(kAdminMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        return plugin.getUtils().translateAlternateColorCodes('&', message);
    }
}

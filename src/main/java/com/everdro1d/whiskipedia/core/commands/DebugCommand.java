/*
 * dro1dDev - created: 2025-05-08
 */

package com.everdro1d.whiskipedia.core.commands;

import com.everdro1d.libs.commands.CommandInterface;
import com.everdro1d.libs.commands.CommandManager;
import com.everdro1d.whiskipedia.core.MainWorker;

public class DebugCommand implements CommandInterface {
    private String description = "enable debug logging to stdout and launch debug console window";

    @Override
    public void execute(CommandManager commandManager) {
        MainWorker.debug = true;
        System.out.println("Debug mode enabled.");
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}

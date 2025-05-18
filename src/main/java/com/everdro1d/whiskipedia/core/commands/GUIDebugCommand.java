/*
 * dro1dDev - created: 2025-05-08
 */

package com.everdro1d.whiskipedia.core.commands;

import com.everdro1d.libs.commands.CommandInterface;
import com.everdro1d.libs.commands.CommandManager;
import com.everdro1d.whiskipedia.core.MainWorker;

public class GUIDebugCommand implements CommandInterface {
    private String description = "color the main elements of the gui distinctly to assist in layout debugging";

    @Override
    public int getExpectedArguments() {
        return 0;
    }

    @Override
    public void execute(CommandManager commandManager) {
        MainWorker.guiDebugColoring = true;
        if (MainWorker.debug) System.out.println("GUI Debug mode enabled.");
    }

    @Override
    public void execute(CommandManager commandManager, String[] args) {
        // No arguments expected
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

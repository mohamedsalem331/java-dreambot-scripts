package main;


import Util.ScriptAction;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.FloorDecoration;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;

import java.awt.*;

@ScriptManifest(name = "WoodFireScript", description = "Ya maroooo ya alyyyyyy", author = "Zamboly",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class Main extends AbstractScript implements ChatListener {
    Color c = new Color(255, 255, 0);
    private final Area treeArea = new Area(3179, 3233, 3199, 3251);
    private Player player = Players.getLocal();

    @Override
    public void onStart() {
        logMessage("Script Started");
    }

    @Override
    public void onExit() {
        logMessage("Script Stopped");
    }

    @Override
    public void onGameMessage(Message message) {
        if (message.getMessage().contains("You can't light a fire here")) {
            Walking.walk(treeArea.getRandomTile());
        }
    }

    @Override
    public int onLoop() {
        MouseSettings.setSpeed(Calculations.random(1, 60));

        /*
        destination - The point destination you wish click.
right - The type of click you wish to preform, right click if true, otherwise left click.
         */
//        Mouse.click(pointA,false)

     ScriptAction action = Inventory.isFull() ? ScriptAction.FIREMAKE : ScriptAction.CHOPDOWN;
        //ScriptAction action = ScriptAction.FIREMAKE ;
        switch (action) {
            case CHOPDOWN:
                if (!treeArea.contains(player)) {
                    Walking.walk(treeArea.getRandomTile());
                    sleep(Calculations.random(2000, 4000));
                }

                if (treeArea.contains(player) && player.isStandingStill() && !Inventory.isItemSelected()) {
                    chopTree("Tree");
                }
                break;
            case FIREMAKE:
                if (Inventory.contains("Logs")) {
                    makeFire();
                }
                break;
            default:
                logMessage("No action to perform");
        }

        return Calculations.random(2000, 7000);
    }

    private void chopTree(String nameOfTree) {
        logMessage("chopping");
        GameObject closestTree = GameObjects.closest(obj -> obj.getName().equals(nameOfTree) && treeArea.contains(obj));
        logMessage(String.valueOf(closestTree));
        if (closestTree != null) {
            closestTree.interact("Chop down");
            Sleep.sleepUntil(() -> player.isStandingStill(), Calculations.random(3000, 8000));
        }
        if (Dialogues.canContinue()) {
            Dialogues.spaceToContinue();
            Sleep.sleep(1000);
        }
    }

    private boolean isLightingFire() {
        return player.isAnimating();
    }

    private boolean isFireUnderPlayer() {
        GameObject fire = GameObjects.closest("Fire");
        return fire != null && fire.distance() == 0;
    }

    private void makeFire() {
        logMessage("fire making");

        Item tinderBox = Inventory.get("Tinderbox");

        do {

            Item log = Inventory.get("Logs");
            Point playerPos = new Point(player.getX(), player.getY());

            if (log != null) {
                tinderBox.useOn(log);

                Sleep.sleep(3000);
                Sleep.sleepUntil(() -> !isLightingFire(), 7000);

                if (Dialogues.canContinue()) {
                    Dialogues.spaceToContinue();
                    Sleep.sleep(1000);
                }
                if (player.isStandingStill() && !isLightingFire() && isFireUnderPlayer()) {
                    Walking.walk(treeArea.getRandomTile());
//                    if (Walking.canWalk(new Tile(playerPos.x + 1, playerPos.y+1))) {
//                        Walking.walk(playerPos.x + 1, playerPos.y+1);
//                    }
                }
            }

        }
        while (Inventory.contains("Logs"));
    }

    private void logMessage(String message) {
        Logger.log(message);
    }

}


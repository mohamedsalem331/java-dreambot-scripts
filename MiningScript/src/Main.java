
import Util.ScriptAction;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
@ScriptManifest(name = "MiningScript", description = "Ya maroooo ya alyyyyyy", author = "Zamboly",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class Main extends AbstractScript implements ChatListener {
    private final Area mineArea = new Area(3076, 3423, 3085, 3417);
    private final Area bankArea = new Area(3097, 3494, 3094, 3494);

    private Player player = Players.getLocal();
    private final String rockName = "Coal rocks";
    private final String oreName = "Coal";
    @Override
    public void onStart() {
        logMessage("Script Started");
    }

    @Override
    public void onExit() {
        logMessage("Script Stopped");
    }

//    @Override
//    public void onGameMessage(Message message) {
//        if (message.getMessage().contains("You can't light a fire here")) {
//            Walking.walk(mineArea.getRandomTile());
//        }
//    }

    @Override
    public int onLoop() {
        MouseSettings.setSpeed(Calculations.random(1, 60));

        ScriptAction action = Inventory.isFull() ? ScriptAction.BANK : ScriptAction.MINE;

        switch (action) {
            case MINE:
                if (!mineArea.contains(player)) {
                    Walking.walk(mineArea.getRandomTile());
                    sleep(Calculations.random(2000, 5000));
                }
                if (mineArea.contains(player) && player.isStandingStill() && !Inventory.isItemSelected()) {
                    mineOre(rockName);
                }
                break;
            case BANK:
                if (!bankArea.contains(player)) {
                    Walking.walk(bankArea.getRandomTile());
                    sleep(Calculations.random(2000, 5000));
                }
                if (bankArea.contains(player) && player.isStandingStill()) {
                    bankOre();
                }
                break;
            case DROP:
                Inventory.dropAll();
                sleep(Calculations.random(2000, 4000));
                break;
            default:
                logMessage("No action to perform");
        }

        return Calculations.random(2000, 7000);
    }

    private void mineOre(String nameOfOre) {
        logMessage("mining");
        GameObject closestRock = GameObjects.closest(obj -> obj.getName().equals(nameOfOre) && mineArea.contains(obj));

        if (closestRock != null) {
            closestRock.interact("Mine");
            Sleep.sleepUntil(() -> player.isStandingStill() && !player.isAnimating(), Calculations.random(3000, 8000));
        }
        if (Dialogues.canContinue()) {
            Dialogues.spaceToContinue();
            Sleep.sleep(1000);
        }
    }
    private void bankOre() {
        logMessage("banking");
        NPC banker = NPCs.closest(npc -> npc != null && npc.hasAction("Bank"));
        banker.interact("Bank");
        Sleep.sleepUntil(Bank::isOpen, 5000);
        if (Bank.isOpen()) {
            Bank.depositAll(oreName);
        }

        Sleep.sleepUntil(() -> player.isStandingStill() && !player.isAnimating(),10000);
    }

    private void logMessage(String message) {
        Logger.log(message);
    }

}


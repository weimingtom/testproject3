package ozmud.world;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ozmud.commands.CommandInterpreter;
import ozmud.ticker.TickListener;
import ozmud.ticker.TickManager;


/**
 * The MUD world.
 * 
 * @author Oscar Stigter
 */
public class World implements TickListener {
    
    /** Heal tick. */
    private static final String HEAL_TICK = "HEAL";
    
    /** Combat tick. */
    private static final String COMBAT_TICK = "COMBAT";
    
    /** The singleton instance. */
    private static final World instance = new World(); 
    
    /** Randomizer. */
    private final Random random;

    /** The command interpreter. */
    private final CommandInterpreter commandInterpreter;
    
    /** Tick manager. */
    private final TickManager tickManager;

    /** The rooms mapped by their ID. */
    private final Map<Integer, Room> rooms;
    
    /** All creatures. */
    private final Set<Creature> creatures;

    /** The player characters mapped by their username (short name). */
    private final Map<String, Player> players;


    /**
     * Default constructor.
     */
    private World() {
        random = new Random();
        commandInterpreter = new CommandInterpreter();
        rooms = new HashMap<Integer, Room>();
        creatures = new HashSet<Creature>();
        players = new HashMap<String, Player>();
        tickManager = new TickManager();
    }
    
    
    public static World getInstance() {
        return instance;
    }
    
    
    public CommandInterpreter getCommandInterpreter() {
        return commandInterpreter;
    }
    
    
    public TickManager getTickManager() {
        return tickManager;
    }


    public Player getPlayer(String name) {
        return players.get(name);
    }


    public void addPlayer(Player player) {
        creatures.add(player);
        players.put(player.getShortName(), player);
    }
    
    
    public Room getStartingRoom() {
        return getRoom(0);
    }
    
    
    public Room getRoom(int id) {
        return rooms.get(id);
    }
    
    
    /**
     * Returns a random integer between 1 and the specified maximum value.
     * 
     * @param max The maximum value.
     * 
     * @return The random integer.
     */
    public int getRandomNumber(int maxValue) {
        return random.nextInt(maxValue) + 1;
    }


    /*
     * (non-Javadoc)
     * @see ozmud.ticker.TickListener#tick(java.lang.String)
     */
    public void tick(String name) {
        if (name.equals(HEAL_TICK)) {
            for (Creature creature : creatures) {
                if (creature.getOpponent() == null) {
                    int hp = creature.getHitpoints();
                    int maxHp = creature.getMaximumHitpoints(); 
                    if (hp < maxHp) {
                        // Heal up to 10% of maximum hitpoints.
                        hp = (int) Math.round(hp + (0.10 * maxHp));
                        if (hp > maxHp) {
                            hp = maxHp;
                        }
                        creature.setHitpoints(hp);
                        if (creature instanceof Player) {
                            ((Player) creature).handleCommand("hp");
                        }
                    }
                }
            }
        } else if (name.equals(COMBAT_TICK)) {
            for (Creature creature : creatures) {
                creature.doCombat();
            }
        }
    }


    public void init() {
        Room room;

        room = new Room();
        room.setId(0);
        room.setShortName("The Plaza");
        room.setDescription(
                "You are standing in the large town plaza, the natural "
                + "gathering place for town folk, merchants and adventurers "
                + "alike. You see people of all races and classes passing by "
                + "as they go about their business.");
        room.addExit(new Exit("east", 1));
        addRoom(room);
        
        NPC guard = new NPC();
        guard.setShortName("Guard");
        guard.setFullName("A strong female guard");
        guard.setDescription("The tall and heavily armoured guard "
                + "carefully watches the croud, keeping the peace. She sure "
                + "looks like she could handle herself in a fight.");
        guard.setGender(Gender.FEMALE);
        guard.setStrength(50);
        guard.setDexterity(50);
        guard.setEndurance(50);
        guard.setHitpoints(guard.getMaximumHitpoints());
        guard.setAliasses(new String[] {"woman"});
        guard.setAliasses(new String[] {"human"});
        guard.setRoom(room);
        creatures.add(guard);
        room.addCreature(guard);

        Weapon dagger = new Weapon();
        dagger.setShortName("shortsword");
        dagger.setFullName("A rusty iron shortsword");
        dagger.setDescription(
                "The blade is old, rusty and worn. It will probably do little damage.");
        dagger.setAliasses(new String[] {"sword", "blade"});
        dagger.setDamage(10);
        dagger.setMaximumHpoints(100);
        dagger.setHitpoints(20);
        dagger.setWeight(0.5);
        dagger.setValue(10);
        room.addItem(dagger);
        
        Armour leatherCuirass = new Armour();
        leatherCuirass.setShortName("leather cuirass");
        leatherCuirass.setFullName("A leather cuirass");
        leatherCuirass.setDescription(
                "The sturdy cuirass is made of high quality leather and "
                + "should offer decent protection from light attacks.");
        leatherCuirass.setAliasses(new String[] {"cuirass", "armour"});
        leatherCuirass.setEquipmentSlot(EquipmentSlot.BODY);
        leatherCuirass.setArmourValue(10);
        leatherCuirass.setWeight(5.0);
        leatherCuirass.setValue(100);
        room.addItem(leatherCuirass);
        
        Armour woodenBuckler = new Armour();
        woodenBuckler.setShortName("wooden buckler");
        woodenBuckler.setFullName("A wooden buckler");
        woodenBuckler.setDescription(
                "The light, wooden shield should offer some protection, but "
                + "you doubt it will survive many direct hits.");
        woodenBuckler.setAliasses(
                new String[] {"buckler", "shield", "armour"});
        woodenBuckler.setEquipmentSlot(EquipmentSlot.SHIELD);
        woodenBuckler.setArmourValue(5);
        woodenBuckler.setWeight(5.0);
        woodenBuckler.setValue(50);
        room.addItem(woodenBuckler);
        
        room = new Room();
        room.setId(1);
        room.setShortName("East Road");
        room.setDescription(
                "You are on a wide east-west road leading through town. "
                + "Many shops can be found here, like a general store to the "
                + "north and the town's bank to the south. To the west you "
                + "hear the sounds from many people on the town's plaza. to "
                + "the east you see the town's east gate.");
        room.addExit(new Exit("north", 2));
        room.addExit(new Exit("east",  4));
        room.addExit(new Exit("south", 3));
        room.addExit(new Exit("west",  0));
        addRoom(room);

        NPC dog = new NPC();
        dog.setShortName("Dog");
        dog.setFullName("A hungry street dog");
        dog.setDescription("The ugly, scrawny, flee-infested dog is looking "
                + "for food, begging from anyone passing by.");
        dog.setGender(Gender.MALE);
        dog.setStrength(10);
        dog.setDexterity(50);
        dog.setEndurance(10);
        dog.setHitpoints(dog.getMaximumHitpoints());
        dog.setAliasses(new String[] {"animal"});
        dog.setRoom(room);
        creatures.add(dog);
        room.addCreature(dog);

        Armour plateCuirass = new Armour();
        plateCuirass.setShortName("steel cuirass");
        plateCuirass.setFullName("A steel cuirass");
        plateCuirass.setDescription(
                "The massive steel plate cuirass is crafted from the finest "
                + "steel and should offer great protection from heavy attacks, "
                + "although it will greatly impede the wearer's flexibility "
                + "and speed.");
        plateCuirass.setAliasses(new String[] {"cuirass", "armour"});
        plateCuirass.setEquipmentSlot(EquipmentSlot.BODY);
        plateCuirass.setArmourValue(50);
        plateCuirass.setWeight(60.0);
        plateCuirass.setValue(1000);
        room.addItem(plateCuirass);
        
        Armour steelTowerShield = new Armour();
        steelTowerShield.setShortName("steel tower shield");
        steelTowerShield.setFullName("A steel tower shield");
        steelTowerShield.setDescription(
                "The large shield is made of heavy plate steel and should "
                + "offer great protection and last many hits.");
        steelTowerShield.setAliasses(
                new String[] {"tower shield", "shield", "armour"});
        steelTowerShield.setEquipmentSlot(EquipmentSlot.SHIELD);
        steelTowerShield.setArmourValue(20);
        steelTowerShield.setWeight(30.0);
        steelTowerShield.setValue(500);
        room.addItem(steelTowerShield);
        
        room = new Room();
        room.setId(2);
        room.setShortName("General store");
        room.setDescription("You are in a small general store.");
        room.addExit(new Exit("south", 1));
        addRoom(room);

        room = new Room();
        room.setId(3);
        room.setShortName("Bank");
        room.setDescription(
                "You are inside the town's large bank. The floor and high ceiling "
                + "are made of black marble, giving the place a luxureous "
                + "look. Golden decorations can be found everywhere. You feel "
                + "somewhat humble in this expensive place.");
        room.addExit(new Exit("north", 1));
        addRoom(room);

        room = new Room();
        room.setId(4);
        room.setShortName("East town gate");
        room.setDescription(
                "You stand before the town's massive eastern gate. A heavy "
                + "steel gate, currently risen, can be quickly closed to "
                + "protect the town from any invasion from the wilderness "
                + "outside. On top of the two gate towers you see bowmen "
                + "watching the passing town folk and keeping a watchful eye "
                + "out for trouble from afar.");
        room.addExit(new Exit("east", 5));
        room.addExit(new Exit("west", 1));
        addRoom(room);

        room = new Room();
        room.setId(5);
        room.setShortName("Outside the east gate");
        room.setDescription(
                "You stand just outside the town's eastern gates. The "
                + "wilderness stretches out in all directions as far as your"
                + "eyes can see.");
        room.addExit(new Exit("west", 4));
        addRoom(room);

        Player player = new Player();
        player.setShortName("Guest");
        player.setFullName("Guest the Curious Explorer");
        player.setDescription("A guest exploring the world.");
        player.setPassword("guest");
        player.setGender(Gender.MALE);
        player.setStrength(30);
        player.setDexterity(30);
        player.setEndurance(30);
        player.setHitpoints(50);
        addPlayer(player);
        
        // Generic tickers.
        tickManager.addTicker(HEAL_TICK, 30);
        tickManager.addTicker(COMBAT_TICK, 2);
        tickManager.addTickListener(this, HEAL_TICK);
        tickManager.addTickListener(this, COMBAT_TICK);
    }
    
    
    private void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }


}

package com.tree.NukaBot;

import com.google.common.collect.Lists;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.api.internal.json.objects.GameObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.tree.NukaBot.NukaBotUtils.sendEmbedWithReactions;
import static com.tree.NukaBot.NukaBotUtils.sendMessage;
import static sx.blah.discord.util.RequestBuffer.request;

public class NukaCommands implements CommandExecutor {
    ArrayList<CSVRecord> guns;
    ArrayList<EmbedObject> gunEmbeds;
    private long gunListMessageID;
    private int gunListMessagePage;

    public  NukaCommands(){

        try {
            FileReader fr = new FileReader("Fallout Weapons.csv");
            guns = new ArrayList<>(CSVFormat.RFC4180.withFirstRecordAsHeader().parse(fr).getRecords());
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        buildGunList();
    }

    private void buildGunList() {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        gunEmbeds = new ArrayList<>();

        List<List<CSVRecord>> gunNameList = Lists.partition(guns,25);

        StringBuilder stringBuilder = new StringBuilder();




        for (int i = 0; i < gunNameList.size(); i++) {
            embedBuilder.withTitle("Page"+ (i+1) + "/" + gunNameList.size());
            for (int j = 0; j < gunNameList.get(i).size(); j++) {

                stringBuilder.append(gunNameList.get(i).get(j).get("Name"));
                stringBuilder.append("\n");
            }
            embedBuilder.appendField("Guns",stringBuilder.toString(),false);

            embedBuilder.withFooterText("To learn more about a gun type \"!fallout gun name\"");

            stringBuilder.delete(0,stringBuilder.length());

            gunEmbeds.add(embedBuilder.build());

            embedBuilder.clearFields();
        }

    }


    @Command(aliases = "!help", async = true)
    public void helpCommand(IUser user){

        request(() -> {
            user.getOrCreatePMChannel().sendMessage("Current commands: \n" +
                    "1.) !race RaceName   Sets your race \n" +
                    "2.) !roll 1d100  (first number is amount of dice the second number is how many face's that dice has.) \n " +
                    "3.) !fallout guns");
        });
    }

    @Command(aliases = "!race",async = true)
    public void pickRace(IUser user, String args[], IGuild guild){
      String pickedRace = args[0].toLowerCase();

      if(pickedRace.contains("human")){
          user.addRole(guild.getRolesByName("Humans").get(0));

          user.removeRole(guild.getRolesByName("Psykers").get(0));
          user.removeRole(guild.getRolesByName("Robots").get(0));
          user.removeRole(guild.getRolesByName("Super-Mutants").get(0));
          user.removeRole(guild.getRolesByName("Ghouls").get(0));

      }

      if(pickedRace.contains("ghoul")){
          user.addRole(guild.getRolesByName("Ghouls").get(0));

          user.removeRole(guild.getRolesByName("Psykers").get(0));
          user.removeRole(guild.getRolesByName("Robots").get(0));
          user.removeRole(guild.getRolesByName("Super-Mutants").get(0));
          user.removeRole(guild.getRolesByName("Humans").get(0));


      }

      if(pickedRace.contains("mutant")){
          user.addRole(guild.getRolesByName("Super-Mutants").get(0));

          user.removeRole(guild.getRolesByName("Psykers").get(0));
          user.removeRole(guild.getRolesByName("Robots").get(0));
          user.removeRole(guild.getRolesByName("Ghouls").get(0));
          user.removeRole(guild.getRolesByName("Humans").get(0));

      }
      if(pickedRace.contains("psyker")){
          user.addRole(guild.getRolesByName("Psykers").get(0));

          user.removeRole(guild.getRolesByName("Robots").get(0));
          user.removeRole(guild.getRolesByName("Super-Mutants").get(0));
          user.removeRole(guild.getRolesByName("Ghouls").get(0));
          user.removeRole(guild.getRolesByName("Humans").get(0));

      }
      if(pickedRace.contains("robot")){
          user.addRole(guild.getRolesByName("Robots").get(0));

          user.removeRole(guild.getRolesByName("Psykers").get(0));
          user.removeRole(guild.getRolesByName("Super-Mutants").get(0));
          user.removeRole(guild.getRolesByName("Ghouls").get(0));
          user.removeRole(guild.getRolesByName("Humans").get(0));
      }

    }

    @Command(aliases = "!roll",async =  true)
    public String rollDice(String args[]){
        if(args.length < 1){
            return "";
        }else {
            try {
                ArrayList<Integer> diceToRoll = new ArrayList<>();
                int dieToRoll;

                if(args[0].contains("d")){

                    String[] dice = args[0].split("[d]");
                    int amountofDice = Integer.valueOf(dice[0]);
                    dieToRoll = Integer.valueOf(dice[1]);

                    int totalRolled = 0;
                    for (int i = 0; i < amountofDice; i++) {
                        diceToRoll.add(ThreadLocalRandom.current().nextInt(1, dieToRoll+1));
                    }
                    for (Integer aDiceToRoll : diceToRoll) {
                        totalRolled += aDiceToRoll;
                    }

                    StringBuilder mb = new StringBuilder();

                    if(args.length > 1){
                        mb.append("Rolled: ");
                        if(args[1].equals("+")){
                            int add = Integer.parseInt(args[2]);
                            if(diceToRoll.size() > 1){
                                for (int i = 0; i < diceToRoll.size(); i++) {
                                    if(i == diceToRoll.size()-1){
                                        mb.append(String.valueOf(diceToRoll.get(i)));
                                    }else {
                                        mb.append(diceToRoll.get(i) + "+");
                                    }
                                }
                                mb.append(" For a total of: " + (totalRolled+add));
                                return mb.toString();
                            }else {
                                return "Rolled: "+totalRolled+ " For a total of: "+(totalRolled+add);
                            }
                        }else if(args[1].equals("-")){
                            int sub = Integer.parseInt(args[2]);
                            if(diceToRoll.size() > 1){
                                for (int i = 0; i < diceToRoll.size(); i++) {
                                    if(i == diceToRoll.size()-1){

                                        mb.append(String.valueOf(diceToRoll.get(i)));
                                    }else {
                                        mb.append(diceToRoll.get(i) + "+");

                                    }
                                }
                                mb.append(" For a total of: " + (totalRolled+sub));
                                return mb.toString();
                            }else {

                                return "Rolled: "+totalRolled+ " For a total of: "+(totalRolled-sub);
                            }
                        }
                    }else {
                        for (int i = 0; i < diceToRoll.size(); i++) {
                            if(i == diceToRoll.size()-1){

                                mb.append(String.valueOf(diceToRoll.get(i)));
                            }else {
                                mb.append(diceToRoll.get(i) + "+");

                            }
                        }
                        mb.append(" For a total of: " + totalRolled);
                        return mb.toString();
                    }
                }else {
                    dieToRoll = Integer.parseInt(args[0]);
                    int randomNum = ThreadLocalRandom.current().nextInt(1, dieToRoll+1);
                    if(args.length > 1){
                        if(args[1].equals("+")){
                            int add = Integer.parseInt(args[2]);
                            return "Rolled: "+randomNum+ " For a total of: "+(randomNum+add);
                        }else if(args[1].equals("-")){
                            int sub = Integer.parseInt(args[2]);
                            return "Rolled: "+randomNum+ " For a total of: "+(randomNum+sub);
                        }
                    }
                    return "Rolled: "+ randomNum;
                }

            }catch (NumberFormatException e){
                return "";
            }
        }
        return "";
    }


    @Command(aliases = "!statgen", async =  true)
    public void   statGen(IChannel channel, String args[]){
        boolean breakB = false;
        if(args.length < 7){
            request(() -> {
               channel.sendMessage("Stats are wrong did you forget one or did you not put spaces in?");
            });
        }else {


            int s = 0 ;
            int p = 0 ;
            int e = 0 ;
            int c = 0 ;
            int i = 0 ;
            int a = 0 ;
            int l   = 0 ;
            try{
                s = Integer.parseInt(args[0]);
                 p= Integer.parseInt(args[1]);
                 e= Integer.parseInt(args[2]);
                 c= Integer.parseInt(args[3]);
                 i= Integer.parseInt(args[4]);
                 a= Integer.parseInt(args[5]);
                 l= Integer.parseInt(args[6]);
            }catch (NumberFormatException e1){
                breakB = true;
                 RequestBuffer.request(() -> {
                    channel.sendMessage("Did you put a extra space in there some were? Please try again.");

                });

            }



            int total = s+p+e+c+i+a+l;

            int unarmedroll = (3*s)+l;

            int rangedRoll = s+(2*p)+l;

            int minusToCrit = p;

            int hpEachLVL = 2*e;

            int lying = c + l;

            int cheaperItems = c*5;




            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.withTitle("Stat gen");
            embedBuilder.withDesc("Total: " + total);



            embedBuilder.appendField("Stuff: ",
                    3 + "x" + s +"+"+l + " = " + unarmedroll + " Unarmed or melee attack dice rolls\n\n" +
                            s + "+ (" + 2 + "x" + p + ") +" + l + " = " + rangedRoll + " ranged attack dice rolls\n\n" +
                            "Minus: " + minusToCrit + " to achieve a crit hit\n\n" +
                            2+"x"+e+" = "+hpEachLVL + " Hp gained each level\n\n" +
                            c+"+"+l+" = " + lying  + " diplomacy and lying\n\n" +
                            "Items are "+c+"x"+5+" = " +cheaperItems+" caps cheaper\n\n" +
                           i + "+" + l + " = " + (i+l)+ " working, hacking,science medicine & repairing tech\n\n" +
                            2+"x"+a+"+"+l + " = " +  (2*a+l) + " dodging or doing agility feats\n\n" +
                            e + "x2"+"+"+l +" = " + ((e*2)+l) + " cooking in a fireplace, hiding from & taming creatures, searching for drinkable water/food. " ,
                    false
            );
            if(!breakB) {
                request(() -> channel.sendMessage(embedBuilder.build()));
            }



        }

    }

    @Command(aliases = "!fallout", async = true)
    public void falloutCommands(IChannel channel, String args[]) {
        if (args.length < 1) {
            sendMessage(channel, "Try !fallout guns");
        } else {
            if (args[0].equals("guns")) {
                gunListMessageID = NukaBotUtils.sendEmbedWithReactions(channel, gunEmbeds.get(0));
                gunListMessagePage = 0;
                RequestBuffer.request(() -> channel.getMessageByID(gunListMessageID).addReaction(EmojiManager.getForAlias("arrow_left"))).get();
                RequestBuffer.request(() -> channel.getMessageByID(gunListMessageID).addReaction(EmojiManager.getForAlias("arrow_right"))).get();
                RequestBuffer.request(() -> channel.getMessageByID(gunListMessageID).addReaction(EmojiManager.getForAlias("x"))).get();
            }
            if (args[0].equals("gun")) {
                if (args.length < 2) {
                    sendMessage(channel, "You forgot to enter the name of the gun, !fallout gun name");
                } else {

                    StringBuilder stringBuilder = new StringBuilder();


                    for (int i = 1; i < args.length - 1; i++) {
                        stringBuilder.append(args[i]);

                        stringBuilder.append(" ");

                    }
                    stringBuilder.append(args[args.length - 1]);
                    for (CSVRecord gun : guns) {
                        if (gun.get("Name").equalsIgnoreCase(stringBuilder.toString())) {
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.withTitle(gun.get("Name"))
                                    .appendField("Price", gun.get("Price Caps"), true)
                                    .appendField("Clip Price", gun.get("Clip Price"), true)
                                    .appendField("Repair Price", gun.get("Repair Price"), true)
                                    .appendField("Stats", "---------------------------------------------------------------------------------------------------", false)
                                    .appendField("Clip Size", gun.get("Clip size"), true)
                                    .appendField("Attack", gun.get("Attack Normal"), true)
                                    .appendField("Reload Time", gun.get("Reload Time"), true)
                                    .appendField("Strength Required", gun.get("Strenght Required"), true);
                            if (!gun.get("Special").equals("")) {
                                embedBuilder.appendField("Special", gun.get("Special"), false);

                            }
                            sendEmbedWithReactions(channel, embedBuilder.build());
                        }


                    }
                }
            }
        }
    }
    @EventSubscriber
    public void reactionAddEvent(ReactionAddEvent reactionAddEvent){

        long messageID = reactionAddEvent.getMessageID();
        IReaction reaction = reactionAddEvent.getReaction();
        IUser user= reactionAddEvent.getUser();
        System.err.println(gunListMessageID + " MessageID");
        System.err.println(messageID + " ReactionID");
        if(messageID == gunListMessageID){
            System.err.println(reactionAddEvent.getReaction().getEmoji().getName());
        }
        if(messageID == gunListMessageID && !user.isBot()){

            if(EmojiParser.parseToAliases(reactionAddEvent.getReaction().getEmoji().toString()).equals(":arrow_left:")){

                gunListMessagePage--;
                if (gunListMessagePage < 0){
                    gunListMessagePage = gunEmbeds.size()-1;
                    RequestBuffer.request(() -> reaction.getMessage().edit(gunEmbeds.get(gunListMessagePage)));

                }else {

                    RequestBuffer.request(() -> reaction.getMessage().edit(gunEmbeds.get(gunListMessagePage)));
                }
            }
            if(EmojiParser.parseToAliases(reactionAddEvent.getReaction().getEmoji().toString()).equals(":arrow_right:")){
                gunListMessagePage++;
                if(gunListMessagePage > gunEmbeds.size()-1){
                    gunListMessagePage = 0;
                    RequestBuffer.request(() -> reaction.getMessage().edit(gunEmbeds.get(gunListMessagePage)));
                }else {
                    RequestBuffer.request(() -> reaction.getMessage().edit(gunEmbeds.get(gunListMessagePage)));
                }

            }
            if(EmojiParser.parseToAliases(reactionAddEvent.getReaction().getEmoji().toString()).equals(":x:")){
                reaction.getMessage().delete();
                //messageID = 0;
            }
            RequestBuffer.request(() -> reaction.getMessage().removeReaction(user,reaction));


        }
    }
}

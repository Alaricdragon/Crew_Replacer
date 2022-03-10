package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.impl.campaign.shared.PlayerTradeProfitabilityData;

import java.awt.*;
import java.util.ArrayList;

public class crewReplacer_Crew {
    //int ID;
    public String name;//comonidie ID
    public ArrayList<String> tags;//for user

    //public float tempcrew = 2;
    public float crewPriority = 0;
    public float crewPower = 1;

    /*public float maxLosePercent = 0;
    public float minLosePercent = 0;
    public boolean NormalLossRules = true;*/



    public boolean hasTag(String tag){
        boolean output = false;
        for(int a = 0; a < tags.size(); a++){
            if(tag.equals(tags.get(a))){
                output = true;
                break;
            }
        }
        return output;
    }
    public void AddTag(String tag){
        boolean output = false;
        for(int a = 0; a < tags.size(); a++){
            if(tag.equals(tags.get(a))){
                output = true;
                break;
            }
        }
        if(!output){
            tags.add(tag);
        }
    }
    public void RemoveTag(String tag){
        for(int a = 0; a < tags.size(); a++){
            if(tag.equals(tags.get(a))){
                tags.remove(a);
                break;
            }
        }
    }
    public float getCrewToLose(float crewUsed,float crewLost){
        return crewLost;
    }
    public void removeCrew(CampaignFleetAPI fleet,float CrewToLost){
        fleet.getCargo().removeCommodity(name, CrewToLost);
    }
    public float getCrewInFleet(CampaignFleetAPI fleet){
        return fleet.getCargo().getCommodityQuantity(name);
        //return tempcrew;
    }
    public float getCrewPowerInFleet(CampaignFleetAPI fleet){
        return getCrewInFleet(fleet) * crewPower;
    }
    /*public double getCrewLostPercent(){
        return (Math.random() * (maxLosePercent - minLosePercent)) + minLosePercent;
    }*/
    public void DisplayedCrewNumbers(float numberOfItems, TextPanelAPI text){
        String[] message = {
                "",
                "an",
        };
        String displayName;
        if(numberOfItems > 1){
            //list_commodity
            displayName = Global.getSector().getEconomy().getCommoditySpec(name).getName() + "s";
            text.appendToLastParagraph(message[0] + "" + numberOfItems + " " + displayName);
            //text.highlightInLastPara(highlight, displayName);
        }else if(numberOfItems == 1){
            displayName = name;
            text.appendToLastParagraph(message[1] + " " + displayName);
            //text.highlightInLastPara(highlight, displayName);
        }
        //return output;
        // text.appendToLastParagraph(message[1] + " " + displayName);
        //                text.highlightInLastPara(highlight, displayName);
    }





}

package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

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
    public float getCrewToLose(float crewUsed,float crewLost){//,CargoAPI cargo){
        return crewLost;
    }
    public void removeCrew(CargoAPI cargo,float CrewToLost){
        cargo.removeCommodity(name, CrewToLost);
    }
    public float getCrewInCargo(CargoAPI cargo){
        return cargo.getCommodityQuantity(name);
        //return tempcrew;
    }
    public float getCrewPowerInCargo(CargoAPI cargo){
        return getCrewInCargo(cargo) * getCrewPower();
    }

    public void DisplayedCrewNumbers(float numberOfItems, TextPanelAPI text){//,CargoAPI cargo){
        // new tooltip-based display
        //CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(name);
        String displayName = getDisplayName();

        TooltipMakerAPI tt = text.beginTooltip();
        TooltipMakerAPI iwt = tt.beginImageWithText(getCrewIcon(), 24);
        String numberStr = (int) numberOfItems + "";
        LabelAPI label = iwt.addPara(numberStr + " " + displayName, 0, Misc.getHighlightColor(), numberStr);
        tt.addImageWithText(0);
        text.addTooltip();
    }
    public String getDisplayName(){//CargoAPI cargo){
        CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(name);
        return spec.getName();
    }
    public String getCrewIcon(){//CargoAPI cargo){
        CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(name);
        return spec.getIconName();
    }
    public float getCrewPower(){//CargoAPI cargo){
        return crewPower;
    }


    /*old fleet get functions, here for backwards compatibility.*/
    public void removeCrew(CampaignFleetAPI fleet,float CrewToLost){
        removeCrew(fleet.getCargo(),CrewToLost);
        //fleet.getCargo().removeCommodity(name, CrewToLost);
    }
    public float getCrewInFleet(CampaignFleetAPI fleet){
        return getCrewInCargo(fleet.getCargo());//fleet.getCargo().getCommodityQuantity(name);
        //return tempcrew;
    }
    public float getCrewPowerInFleet(CampaignFleetAPI fleet){
        return getCrewPowerInCargo(fleet.getCargo());//getCrewInFleet(fleet) * getCrewPower();
    }

}
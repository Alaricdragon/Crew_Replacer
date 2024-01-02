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
    public String name;//comonidie ID
    public ArrayList<String> tags;//for user

    public float crewLoadPriority = 0;//higher load priority crew will not be replaced by lower priority crew if someone attempts to add the same crew to a job twice.

    public float crewPriority = 0;//order crew is used in. lower numbers first i think?
    public float crewPower = 1;//crew power. for calculation how mush a crew is worth.
    public float crewDefence = 1;//crew defence. for calculation how mush a crew is worth.

    public Object ExtraData;
    public void resetExtraData(){
        ExtraData = null;
    }
    public void setExtraData(Object newData){
        ExtraData = newData;
    }

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
    public float getCrewToLose(CargoAPI cargo,float crewUsed,float crewLost){//,CargoAPI cargo){
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
        return getCrewInCargo(cargo) * getCrewPower(cargo);
    }

    public String getDisplayName(CargoAPI cargo){//CargoAPI cargo){
        CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(name);
        return spec.getName();
    }
    public String getCrewIcon(CargoAPI cargo){//CargoAPI cargo){
        CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(name);
        return spec.getIconName();
    }
    public float getCrewPower(CargoAPI cargo){//CargoAPI cargo){
        return crewPower;
    }
    public float getCrewDefence(CargoAPI cargo){
        return crewDefence;
    }

    public void displayCrewAvailable(CargoAPI cargo, float numberOfItems, TextPanelAPI text){
        DisplayedCrewNumbers(cargo,numberOfItems,text);
    }
    public void displayCrewLost(CargoAPI cargo,float numberOfItems, TextPanelAPI text){
        DisplayedCrewNumbers(cargo,numberOfItems,text);
    }
    /*a function that is never used directly. used for genral display*/
    public void DisplayedCrewNumbers(CargoAPI cargo,float numberOfItems, TextPanelAPI text){//,CargoAPI cargo){
        // new tooltip-based display
        //CommoditySpecAPI spec = Global.getSector().getEconomy().getCommoditySpec(name);
        String displayName = getDisplayName(cargo);

        TooltipMakerAPI tt = text.beginTooltip();
        TooltipMakerAPI iwt = tt.beginImageWithText(getCrewIcon(cargo), 24);
        String numberStr = (int) numberOfItems + "";
        LabelAPI label = iwt.addPara(numberStr + " " + displayName, 0, Misc.getHighlightColor(), numberStr);
        tt.addImageWithText(0);
        text.addTooltip();
    }

    public float getCargoSpacePerItem(CargoAPI cargo){
        return Global.getSector().getEconomy().getCommoditySpec(this.name).getCargoSpace();
    }
    public float getCargoSpaceUse(CargoAPI cargo, float amountOfCrew){
        return getCargoSpacePerItem(cargo) * amountOfCrew;
    }
    public float getCargoSpaceUse(CargoAPI cargo){
        return getCargoSpacePerItem(cargo) * this.getCrewInCargo(cargo);
    }

    public float getFuelSpacePerItem(CargoAPI cargo){
        if(Global.getSector().getEconomy().getCommoditySpec(this.name).isFuel()) return 1;
        return 0;
    }
    public float getFuelSpaceUse(CargoAPI cargo, float amountOfCrew){
        return getFuelSpacePerItem(cargo) * amountOfCrew;
    }
    public float getFuelSpaceUse(CargoAPI cargo){
        return getFuelSpacePerItem(cargo) * this.getCrewInCargo(cargo);
    }

    public float getCrewSpacePerItem(CargoAPI cargo){
        if(Global.getSector().getEconomy().getCommoditySpec(this.name).isPersonnel()) return 1;
        return 0;
    }
    public float getCrewSpaceUse(CargoAPI cargo, float amountOfCrew){
        return getCrewSpacePerItem(cargo) * amountOfCrew;
    }
    public float getCrewSpaceUse(CargoAPI cargo){
        return getCrewSpacePerItem(cargo) * this.getCrewInCargo(cargo);
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
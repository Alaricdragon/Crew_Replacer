package data.scripts.modifications.crewAndPersonellChanger;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.CargoScreenListener;
import data.scripts.shadowCrew.CrewReplacer_HideShowdoCrew_2;

public class CrewReplacer_CAPChanger_listiner implements CargoScreenListener {
    public static void addListener(){
        Global.getSector().getListenerManager().addListener(new CrewReplacer_CAPChanger_listiner(), true);
    }
    @Override
    public void reportCargoScreenOpened() {
        CrewReplacer_crewAndPersonelChanger.apply(Global.getSector().getPlayerFleet());
        CrewReplacer_CAPChanger_AddHullMod.apply();
    }

    @Override
    public void reportPlayerLeftCargoPods(SectorEntityToken entity) {
        CrewReplacer_crewAndPersonelChanger.apply(Global.getSector().getPlayerFleet());
        CrewReplacer_CAPChanger_AddHullMod.apply();
    }

    @Override
    public void reportPlayerNonMarketTransaction(PlayerMarketTransaction transaction, InteractionDialogAPI dialog) {
        CrewReplacer_crewAndPersonelChanger.apply(Global.getSector().getPlayerFleet());
        CrewReplacer_CAPChanger_AddHullMod.apply();
    }

    @Override
    public void reportSubmarketOpened(SubmarketAPI submarket) {
        CrewReplacer_crewAndPersonelChanger.apply(Global.getSector().getPlayerFleet());
        CrewReplacer_CAPChanger_AddHullMod.apply();
    }
}

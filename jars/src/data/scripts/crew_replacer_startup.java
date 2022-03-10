package data.scripts;
import com.fs.starfarer.api.BaseModPlugin;
import data.scripts.crews.marine;

import java.util.ArrayList;
/*
    HERE
    THIS IS INPOSSABLE:
    as far as im able to tell, any data i save in crewReplacer_Main is saved FOREVER. i have no FUCKING CLUE how to reset said data.
    i tried crewReplacer_Main.Jobs = new ArrayList<crewReplacer_Job>(); to empty the data out, but data IM SHOULD NOT BE SAVEING RIGHT NOW is STILL SAVEING
    AAAAAAAAAAAAAAAAAAAAAAAAAAA
    (i was compieling my project wong.)(it now works perfictly.)

 */
public class crew_replacer_startup extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        //crewReplacer_Main.Jobs = new ArrayList<crewReplacer_Job>();
        startup2();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        crewReplacer_Main.organizePriority();

        super.onGameLoad(newGame);
    }
    private static void startup2(){
        crewReplacer_Job tempJob = crewReplacer_Main.getJob("salvage_main");
        tempJob.addNewCrew("crew",1,10);

        tempJob = crewReplacer_Main.getJob("salvage_Secondary");
        tempJob.addNewCrew("heavy_machinery",1,10);

        /*tempJob = crewReplacer_Main.getJob("fleet");
        tempJob.addNewCrew("crew",1,10);*/

        /*tempJob = crewReplacer_Main.getJob("survey_main");
        tempJob.addNewCrew("crew",1,10);*/

        tempJob = crewReplacer_Main.getJob("raiding_main");
        marine tempcrew = new marine();
        tempcrew.name = "marines";
        tempJob.addCrew(tempcrew);
    }
}
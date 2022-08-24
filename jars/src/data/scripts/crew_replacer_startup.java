package data.scripts;
import com.fs.starfarer.api.BaseModPlugin;
import data.scripts.crews.marine;

/*
 */
public class crew_replacer_startup extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        startup2();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        crewReplacer_Main.organizePriority();
        super.onGameLoad(newGame);
    }
    private static void startup2(){
        crewReplacer_Job tempJob = crewReplacer_Main.getJob("salvage_crew");
        tempJob.addNewCrew("crew",1,10);

        tempJob = crewReplacer_Main.getJob("salvage_heavyMachinery");
        tempJob.addNewCrew("heavy_machinery",1,10);

        /*tempJob = crewReplacer_Main.getJob("fleet");
        tempJob.addNewCrew("crew",1,10);*/

        /*tempJob = crewReplacer_Main.getJob("survey_crew");
        tempJob.addNewCrew("crew",1,10);*/

        tempJob = crewReplacer_Main.getJob("raiding_marines");
        marine tempcrew = new marine();
        tempcrew.name = "marines";
        tempJob.addCrew(tempcrew);
        //supplyDemandChangeInit();
    }
}
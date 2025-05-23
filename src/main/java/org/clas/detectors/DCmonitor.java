package org.clas.detectors;

import java.util.HashMap;
import java.util.Map;
import org.clas.viewer.DetectorMonitor;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.utils.groups.IndexedList;
import org.jlab.utils.groups.IndexedTable;

/**
 *
 * @author devita
 */

public class DCmonitor extends DetectorMonitor {

    public IndexedTable forward = null;
    public IndexedTable reverse = null;

    public DCmonitor(String name) {
        super(name);
        this.setDetectorTabNames("occupancy", "occupancyNorm", "occupancyToT", "occupancyPercent", "multiplicity", "tot", "groupMult", "tdc2d", "tdc1d_s");
        this.useSectorButtons(true);
        this.init(false);
        this.getCcdb().init("/daq/tt/dc");
    }

    @Override
    public void createHistos() {
        
        // create histograms
        this.setNumberOfEvents(0);
        H1F summary = new H1F("summary","summary",6,0.5,6.5);
        summary.setTitleX("sector");
        summary.setTitleY("DC occupancy");
        summary.setTitle("DC");
        summary.setFillColor(33);
        DataGroup sum = new DataGroup(1,1);
        sum.addDataSet(summary, 0);
        this.setDetectorSummary(sum);
        
        H1F raw_summary = new H1F("raw_summary","raw_summary",6,0.5,6.5);
        
        for(int sector=1; sector <= 6; sector++) {
            H2F raw = new H2F("raw_sec" + sector, "Sector " + sector + " Occupancy", 112, 0.5, 112.5, 36, 0.5, 36.5);
            raw.setTitleX("wire");
            raw.setTitleY("layer");
            raw.setTitle("sector "+sector);
            
            H2F occ = new H2F("occ_sec" + sector, "Sector " + sector + " Occupancy", 112, 0.5, 112.5, 36, 0.5, 36.5);
            occ.setTitleX("wire");
            occ.setTitleY("layer");
            occ.setTitle("sector "+sector);

            H2F rawTot = new H2F("raw_tot_sec" + sector, "Sector " + sector + " Occupancy", 112, 0.5, 112.5, 36, 0.5, 36.5);
            raw.setTitleX("wire");
            raw.setTitleY("layer");
            raw.setTitle("sector "+sector);
            
            H2F occToT = new H2F("occ_tot_sec" + sector, "Sector " + sector + " Occupancy", 112, 0.5, 112.5, 36, 0.5, 36.5);
            occToT.setTitleX("wire");
            occToT.setTitleY("layer");
            occToT.setTitle("sector "+sector);

            H1F reg_occ = new H1F("reg_occ_sec" + sector, "Sector " + sector + " region Occupancy", 3, 0.5, 3.5);
            reg_occ.setTitleX("region");
            reg_occ.setTitleY("occupancy %");
            reg_occ.setTitle("sector "+sector);
            reg_occ.setFillColor(3);
            
            H1F raw_reg_occ = new H1F("raw_reg_occ_sec" + sector, "Sector " + sector + " region Occupancy", 3, 0.5, 3.5);
            raw_reg_occ.setTitleX("region");
            raw_reg_occ.setTitleY("counts");
            raw_reg_occ.setTitle("sector "+sector);
            
            H2F tdc_raw = new H2F("tdc_raw" + sector, "Sector " + sector + " TDC raw distribution", 404, 0, 2020, 36, 0.5, 36.5);
            tdc_raw.setTitleX("tdc raw");
            tdc_raw.setTitleY("layer");
            tdc_raw.setTitle("sector "+sector);
            
            for(int sl=1; sl<=6; sl++) {
                H1F tdc_sl_raw = new H1F("tdc_sl_raw" + sector+ sl, "Sector " + sector + " Superlayer " + sl + " TDC spectrum", 404, 0, 2020);
                tdc_sl_raw.setFillColor(3);
                DataGroup dg_sl = new DataGroup(1,1);
                dg_sl.addDataSet(tdc_sl_raw, 0);
                this.getDataGroup().add(dg_sl, sector, sl, 0);
            }
            
            H1F mult = new H1F("multiplicity_sec"+ sector, "Multiplicity sector "+ sector, 200, 0., 1000);
            mult.setTitleX("hit multiplicity");
            mult.setTitleY("counts");
            mult.setTitle("multiplicity sector " + sector);
            mult.setFillColor(3);
            
            H1F tot = new H1F("tot"+ sector, "ToT sector "+ sector, 200, 0., 1000);
            tot.setTitleX("hit tot");
            tot.setTitleY("counts");
            tot.setTitle("tot sector " + sector);
            tot.setFillColor(3);

            H2F groupMult = new H2F("group_mult_sec" + sector, "Sector " + sector + " Occupancy", 64, 0, 64, 85*3, 0, 85*3);
            groupMult.setTitleX("multiplicity");
            groupMult.setTitleY("group");
            groupMult.setTitle("sector "+sector);
            
            DataGroup dg = new DataGroup(11,1);
            dg.addDataSet(raw, 0);
            dg.addDataSet(occ, 1);
            dg.addDataSet(reg_occ, 2);
            dg.addDataSet(raw_reg_occ, 3);
            dg.addDataSet(tdc_raw, 4);
            dg.addDataSet(mult, 5);
            dg.addDataSet(tot, 6);
            dg.addDataSet(raw_summary, 7);
            dg.addDataSet(rawTot, 8);
            dg.addDataSet(occToT, 9);
            dg.addDataSet(groupMult, 10);
            this.getDataGroup().add(dg, sector,0,0);
        }
    }

    @Override
    public void runNumberChanged() {
        forward = this.getCcdb().getConstants(runNumber, "/daq/tt/dc");
        reverse = getReverseTT(forward);
    }

    private static IndexedTable getReverseTT(IndexedTable tt) {
        System.err.print("Inverting DC translation table, this may take a few seconds ...");
        IndexedTable ret = new IndexedTable(4, "crate/I:slot/I:channel/I");
        for(Object key : tt.getList().getMap().keySet()) {
            int crate   = IndexedList.IndexGenerator.getIndex((long)key, 0);
            int slot    = IndexedList.IndexGenerator.getIndex((long)key, 1);
            int channel = IndexedList.IndexGenerator.getIndex((long)key, 2);
            int sector  = tt.getIntValue("sector",    crate,slot,channel);
            int layer   = tt.getIntValue("layer",     crate,slot,channel);
            int comp    = tt.getIntValue("component", crate,slot,channel);
            int order   = tt.getIntValue("order",     crate,slot,channel);
            ret.addEntry(sector, layer, comp, order);
            ret.setIntValue(crate,   "crate",   sector, layer, comp, order);
            ret.setIntValue(slot,    "slot",    sector, layer, comp, order);
            ret.setIntValue(channel, "channel", sector, layer, comp, order);
        }
        System.err.println("Done inverting DC translation table.");
        return ret;
    }

    @Override
    public void plotHistos() {
        // initialize canvas and plot histograms

        this.getDetectorCanvas().getCanvas("occupancy").divide(2, 3);
        this.getDetectorCanvas().getCanvas("occupancy").setGridX(false);
        this.getDetectorCanvas().getCanvas("occupancy").setGridY(false);
        this.getDetectorCanvas().getCanvas("occupancyToT").divide(2, 3);
        this.getDetectorCanvas().getCanvas("occupancyToT").setGridX(false);
        this.getDetectorCanvas().getCanvas("occupancyToT").setGridY(false);
        this.getDetectorCanvas().getCanvas("occupancyNorm").divide(2, 3);
        this.getDetectorCanvas().getCanvas("occupancyNorm").setGridX(false);
        this.getDetectorCanvas().getCanvas("occupancyNorm").setGridY(false);
//        this.getDetectorCanvas().getCanvas("Raw Occupancies").divide(2, 3);
//        this.getDetectorCanvas().getCanvas("Raw Occupancies").setGridX(false);
//        this.getDetectorCanvas().getCanvas("Raw Occupancies").setGridY(false);
        this.getDetectorCanvas().getCanvas("occupancyPercent").divide(2, 3);
        this.getDetectorCanvas().getCanvas("occupancyPercent").setGridX(false);
        this.getDetectorCanvas().getCanvas("occupancyPercent").setGridY(false);
        this.getDetectorCanvas().getCanvas("tdc2d").divide(2, 3);
        this.getDetectorCanvas().getCanvas("tdc2d").setGridX(false);
        this.getDetectorCanvas().getCanvas("tdc2d").setGridY(false);
        this.getDetectorCanvas().getCanvas("tdc1d").divide(2, 3);
        this.getDetectorCanvas().getCanvas("tdc1d").setGridX(false);
        this.getDetectorCanvas().getCanvas("tdc1d").setGridY(false);
        this.getDetectorCanvas().getCanvas("multiplicity").divide(2, 3);
        this.getDetectorCanvas().getCanvas("multiplicity").setGridX(false);
        this.getDetectorCanvas().getCanvas("multiplicity").setGridY(false);
        this.getDetectorCanvas().getCanvas("tot").divide(2, 3);
        this.getDetectorCanvas().getCanvas("tot").setGridX(false);
        this.getDetectorCanvas().getCanvas("tot").setGridY(false);
        this.getDetectorCanvas().getCanvas("groupMult").divide(2, 3);
        this.getDetectorCanvas().getCanvas("groupMult").setGridX(false);
        this.getDetectorCanvas().getCanvas("groupMult").setGridY(false);
        
        for(int sector=1; sector <=6; sector++) {
            this.getDetectorCanvas().getCanvas("occupancy").getPad(sector-1).getAxisZ().setRange(0.01, max_occ);
            this.getDetectorCanvas().getCanvas("occupancy").getPad(sector-1).getAxisZ().setLog(getLogZ());
            this.getDetectorCanvas().getCanvas("occupancy").cd(sector-1);
            this.getDetectorCanvas().getCanvas("occupancy").draw(this.getDataGroup().getItem(sector,0,0).getH2F("occ_sec"+sector));
            this.getDetectorCanvas().getCanvas("occupancyToT").getPad(sector-1).getAxisZ().setRange(0.01, max_occ);
            this.getDetectorCanvas().getCanvas("occupancyToT").getPad(sector-1).getAxisZ().setLog(!getLogZ());
            this.getDetectorCanvas().getCanvas("occupancyToT").cd(sector-1);
            this.getDetectorCanvas().getCanvas("occupancyToT").draw(this.getDataGroup().getItem(sector,0,0).getH2F("occ_tot_sec"+sector));
            this.getDetectorCanvas().getCanvas("occupancyNorm").getPad(sector-1).getAxisZ().setRange(0.01, max_occ);
            this.getDetectorCanvas().getCanvas("occupancyNorm").getPad(sector-1).getAxisZ().setLog(!getLogZ());
            this.getDetectorCanvas().getCanvas("occupancyNorm").cd(sector-1);
            this.getDetectorCanvas().getCanvas("occupancyNorm").draw(this.getDataGroup().getItem(sector,0,0).getH2F("occ_sec"+sector));
//            this.getDetectorCanvas().getCanvas("Raw Occupancies").getPad(sector-1).getAxisZ().setLog(getLogZ());
//            this.getDetectorCanvas().getCanvas("Raw Occupancies").cd(sector-1);
//            this.getDetectorCanvas().getCanvas("Raw Occupancies").draw(this.getDataGroup().getItem(sector,0,0).getH2F("raw_sec"+sector));
            this.getDetectorCanvas().getCanvas("occupancyPercent").cd(sector-1);
            this.getDetectorCanvas().getCanvas("occupancyPercent").draw(this.getDataGroup().getItem(sector,0,0).getH1F("reg_occ_sec"+sector));
            this.getDetectorCanvas().getCanvas("tdc2d").getPad(sector-1).getAxisZ().setLog(getLogZ());
            this.getDetectorCanvas().getCanvas("tdc2d").cd(sector-1);
            this.getDetectorCanvas().getCanvas("tdc2d").draw(this.getDataGroup().getItem(sector,0,0).getH2F("tdc_raw" + sector));
            this.getDetectorCanvas().getCanvas("multiplicity").cd(sector-1);
            this.getDetectorCanvas().getCanvas("multiplicity").draw(this.getDataGroup().getItem(sector,0,0).getH1F("multiplicity_sec"+ sector));
            this.getDetectorCanvas().getCanvas("tot").cd(sector-1);
            this.getDetectorCanvas().getCanvas("tot").draw(this.getDataGroup().getItem(sector,0,0).getH1F("tot"+ sector));
            this.getDetectorCanvas().getCanvas("groupMult").cd(sector-1);
            this.getDetectorCanvas().getCanvas("groupMult").getPad(sector-1).getAxisZ().setLog(getLogZ());
            this.getDetectorCanvas().getCanvas("groupMult").draw(this.getDataGroup().getItem(sector,0,0).getH2F("group_mult_sec"+ sector));
            if(getActiveSector()==sector) {
               for(int sl=1; sl <=6; sl++) {
                   this.getDetectorCanvas().getCanvas("tdc1d").cd(sl-1);
                   this.getDetectorCanvas().getCanvas("tdc1d").draw(this.getDataGroup().getItem(sector,sl,0).getH1F("tdc_sl_raw" + sector+ sl));
               }
            }
        }

        this.getDetectorCanvas().getCanvas("occupancy").update();
        this.getDetectorCanvas().getCanvas("occupancyToT").update();
        this.getDetectorCanvas().getCanvas("occupancyNorm").update();
//        this.getDetectorCanvas().getCanvas("Raw Occupancies").update();
        this.getDetectorCanvas().getCanvas("occupancyPercent").update();
        this.getDetectorCanvas().getCanvas("tdc2d").update();
        this.getDetectorCanvas().getCanvas("multiplicity").update();
        this.getDetectorCanvas().getCanvas("tot").update();
        this.getDetectorCanvas().getCanvas("groupMult").update();
        
    }

    @Override
    public void processEvent(DataEvent event) {
                
        DataBank bank;
        if (event.hasBank("DC::tot")) bank = event.getBank("DC::tot");
        else if (event.hasBank("DC::tdc")) bank = event.getBank("DC::tdc");
        else return;

        // process event info and save into data group
        int rows = bank.rows();
        int[] nHitSector = {0,0,0,0,0,0};

        Map<Integer, Integer> multiplicity = new HashMap();
        for(int i = 0; i < rows; i++){
            int    sector = bank.getByte("sector",i);
            int     layer = bank.getByte("layer",i);
            int      wire = bank.getShort("component",i);
            int     order = bank.getByte("order",i);
            int       TDC = bank.getInt("TDC",i);
            int       ToT = bank.getInt("ToT",i);
            int    region = (int) (layer-1)/12+1;
            int        sl = (int) (layer-1)/6+1;
            int crate = this.reverse.getIntValue("crate",   sector, layer, wire, order%10);
            int slot  = this.reverse.getIntValue("slot",    sector, layer, wire, order%10);
            int chan  = this.reverse.getIntValue("channel", sector, layer, wire, order%10);
            int group = chan/16;
            int hash = this.generateHashCode(crate, slot, group*16); 
            if(multiplicity.containsKey(hash))
                multiplicity.merge(hash, 1, Integer::sum);
            else
                multiplicity.put(hash, 1);            

            this.getDataGroup().getItem(sector,0,0).getH2F("raw_sec"+sector).fill(wire*1.0,layer*1.0);                
            if( ToT > minToT) {
                this.getDataGroup().getItem(sector,0,0).getH2F("raw_tot_sec"+sector).fill(wire*1.0,layer*1.0);                
            }

            this.getDataGroup().getItem(sector,0,0).getH1F("raw_reg_occ_sec"+sector).fill(region * 1.0);
            this.getDataGroup().getItem(sector,0,0).getH2F("tdc_raw"+sector).fill(TDC,layer*1.0);
            this.getDataGroup().getItem(sector,sl,0).getH1F("tdc_sl_raw" + sector+ sl).fill(TDC,layer*1.0);
            //if(TDC > 0) this.getDetectorSummary().getH1F("summary").fill(sector*1.0);
            if(TDC > 0) this.getDataGroup().getItem(sector,0,0).getH1F("raw_summary").fill(sector*1.0);
            this.getDataGroup().getItem(sector,0,0).getH1F("tot"+sector).fill(ToT);

            if(this.getDataGroup().getItem(sector,0,0).getH1F("raw_summary").getEntries()>0) {
                this.getDetectorSummary().getH1F("summary").setBinContent(sector-1, 100*this.getDataGroup().getItem(sector,0,0).getH1F("raw_summary").getBinContent(sector-1)/this.getNumberOfEvents()/112/12/3);
            }


            nHitSector[sector-1]++;
        }

        for(int sec=1; sec<=6; sec++) {
            this.getDataGroup().getItem(sec,0,0).getH1F("multiplicity_sec"+ sec).fill(nHitSector[sec-1]*1.0);
        }

        for(int key : multiplicity.keySet()) {
            int slot   = this.getL(key);
            int group  = this.getC(key)/16;
            int sector = this.forward.getIntValue("sector", this.getS(key), this.getL(key), this.getC(key));
            int layer  = this.forward.getIntValue("layer",  this.getS(key), this.getL(key), this.getC(key));
            int region = (layer-1)/12+1;
            int sc = slot<10 ? (slot-3)*6+group : (slot-7)*6+group;
            this.getDataGroup().getItem(sector,0,0).getH2F("group_mult_sec"+sector).fill(multiplicity.get(key),(region-1)*85+sc);
        }
    }

    @Override
    public void analysisUpdate() {

        if(this.getNumberOfEvents()>0) {
            for(int sector=1; sector <=6; sector++) {
                H2F raw = this.getDataGroup().getItem(sector,0,0).getH2F("raw_sec"+sector);
                H2F rawToT = this.getDataGroup().getItem(sector,0,0).getH2F("raw_tot_sec"+sector);
                for(int loop = 0; loop < raw.getDataBufferSize(); loop++){
                    this.getDataGroup().getItem(sector,0,0).getH2F("occ_sec"+sector).setDataBufferBin(loop,100*raw.getDataBufferBin(loop)/this.getNumberOfEvents());
                    this.getDataGroup().getItem(sector,0,0).getH2F("occ_tot_sec"+sector).setDataBufferBin(loop,100*rawToT.getDataBufferBin(loop)/this.getNumberOfEvents());
                }
            }
        }
        
        if(this.getNumberOfEvents()>0) {
            int entries = 0;
            for(int sector=1; sector <=6; sector++) {
              H1F raw_check = this.getDataGroup().getItem(sector,0,0).getH1F("raw_reg_occ_sec"+sector);
              entries += raw_check.getEntries();
            }
 
            for(int sector=1; sector <=6; sector++) {
                H1F raw = this.getDataGroup().getItem(sector,0,0).getH1F("raw_reg_occ_sec"+sector);
                H1F ave = this.getDataGroup().getItem(sector,0,0).getH1F("reg_occ_sec"+sector);
                if(entries>0) {
                for(int loop = 0; loop < 3; loop++){
                    ave.setBinContent(loop, 100*raw.getBinContent(loop)/this.getNumberOfEvents()/112/12);
                }
                }
                this.getDetectorCanvas().getCanvas("occupancy").getPad(sector-1).getAxisZ().setRange(0.01, max_occ);
                this.getDetectorCanvas().getCanvas("occupancyNorm").getPad(sector-1).getAxisZ().setRange(0.01, max_occ);
                this.getDetectorCanvas().getCanvas("occupancyToT").getPad(sector-1).getAxisZ().setRange(0.01, max_occ);                
            }
        }   
    }
    
}

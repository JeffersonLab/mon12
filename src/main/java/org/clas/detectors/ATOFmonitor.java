package org.clas.detectors;

import org.clas.viewer.DetectorMonitor;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author
 */

public class ATOFmonitor extends DetectorMonitor {

  // Temporary storage for Bar TDCs per event
  private Map<Integer, Integer> barTDCOrder0Map;
  private Map<Integer, Integer> barTDCOrder1Map;

  private float tdc_bin_time = 0.015625f; // ns/bin

  public ATOFmonitor(String name) {
    super(name);
    // Add new tabs: "WedgeTDC", "BarTDC", "BarSumDiff"
    this.setDetectorTabNames("module","globalwedge", "occupancy", "WedgeScalers", "BarScalers", "WedgeTDC", "BarTDC", "BarSumDiff");
    this.init(false);

    // Initialize temporary storage maps
    barTDCOrder0Map = new HashMap<>();
    barTDCOrder1Map = new HashMap<>();
  }

  @Override
  public void createHistos() {
    // Initialize canvas and create histograms
    this.setNumberOfEvents(0);

    // Module Canvas
    this.getDetectorCanvas().getCanvas("module").divide(1, 2);
    this.getDetectorCanvas().getCanvas("module").setGridX(false);
    this.getDetectorCanvas().getCanvas("module").setGridY(false);

    // Module Canvas
    this.getDetectorCanvas().getCanvas("globalwedge").divide(1, 3);
    this.getDetectorCanvas().getCanvas("globalwedge").setGridX(false);
    this.getDetectorCanvas().getCanvas("globalwedge").setGridY(false);

    // Occupancy Canvas
    this.getDetectorCanvas().getCanvas("occupancy").divide(1, 1);
    this.getDetectorCanvas().getCanvas("occupancy").setGridX(false);
    this.getDetectorCanvas().getCanvas("occupancy").setGridY(false);

    //// TDC Canvas
    //this.getDetectorCanvas().getCanvas("tdc").divide(2, 1);
    //this.getDetectorCanvas().getCanvas("tdc").setGridX(false);
    //this.getDetectorCanvas().getCanvas("tdc").setGridY(false);

    // WedgeScalers Canvas
    this.getDetectorCanvas().getCanvas("WedgeScalers").divide(1, 1);
    this.getDetectorCanvas().getCanvas("WedgeScalers").setGridX(false);
    this.getDetectorCanvas().getCanvas("WedgeScalers").setGridY(false);

    // BarScalers Canvas
    this.getDetectorCanvas().getCanvas("BarScalers").divide(1, 1);
    this.getDetectorCanvas().getCanvas("BarScalers").setGridX(false);
    this.getDetectorCanvas().getCanvas("BarScalers").setGridY(false);

    // WedgeTDC Canvas (3x5 grid for sectors 0-14)
    this.getDetectorCanvas().getCanvas("WedgeTDC").divide(3, 5);
    this.getDetectorCanvas().getCanvas("WedgeTDC").setGridX(false);
    this.getDetectorCanvas().getCanvas("WedgeTDC").setGridY(false);

    // BarTDC Canvas (3x5 grid for sectors 0-14)
    this.getDetectorCanvas().getCanvas("BarTDC").divide(3, 5);
    this.getDetectorCanvas().getCanvas("BarTDC").setGridX(false);
    this.getDetectorCanvas().getCanvas("BarTDC").setGridY(false);

    // BarSumDiff Canvas
    this.getDetectorCanvas().getCanvas("BarSumDiff").divide(2, 2);
    this.getDetectorCanvas().getCanvas("BarSumDiff").setGridX(false);
    this.getDetectorCanvas().getCanvas("BarSumDiff").setGridY(false);

    // Update summary histogram
    H1F summary = new H1F("summary", "summary", 15, -0.5, 14.5);
    summary.setTitleX("Sector");
    summary.setTitleY("ATOF Hits");
    summary.setTitle("ATOF Summary");
    summary.setFillColor(36);

    DataGroup sum = new DataGroup(1, 1);
    sum.addDataSet(summary, 0);
    this.setDetectorSummary(sum);


    // Update summary histogram
    H1F module_wedge_hits = new H1F("module_wedge_hits", "module wedge hits", 15, -0.5, 14.5);
    module_wedge_hits.setTitleX("Module");
    module_wedge_hits.setTitleY("ATOF wedge Hits");
    module_wedge_hits.setFillColor(36);

    H1F module_bar_hits = new H1F("module_bar_hits", "module bar hits", 15, -0.5, 14.5);
    module_bar_hits.setTitleX("Module");
    module_bar_hits.setTitleY("ATOF bar Hits");
    module_bar_hits.setFillColor(37);

    DataGroup module_group = new DataGroup(2, 1);
    module_group.addDataSet(module_wedge_hits, 0);
    module_group.addDataSet(module_bar_hits, 1);
    this.getDataGroup().add(module_group, 0, 0, 0);

    // Update globalwedge 
    H1F globalwedge_wedge_hits = new H1F("globalwedge_wedge_hits", "globalwedge wedge hits", 60, -0.5, 59.5);
    globalwedge_wedge_hits.setTitleX("global wedge");
    globalwedge_wedge_hits.setTitleY("ATOF wedge Hits");
    globalwedge_wedge_hits.setFillColor(36);

    H1F globalwedge_bar_hits = new H1F("globalwedge_bar_hits", "globalwedge bar hits", 60, -0.5, 59.5);
    globalwedge_bar_hits.setTitleX("global wedge");
    globalwedge_bar_hits.setTitleY("ATOF bar Hits");
    globalwedge_bar_hits.setFillColor(37);

    H2F globalwedge_wedge_vs_z_hits = new H2F("globalwedge_wedge_vs_z_hits", "globalwedge wedge hits", 60, -0.5, 59.5,10,-0.5,9.5);
    globalwedge_wedge_vs_z_hits.setTitleX("global wedge");
    globalwedge_wedge_vs_z_hits.setTitleY("Z_{wedge}");
    globalwedge_wedge_vs_z_hits.setTitle("Z vs Global Wedge");

    H2F globalwedge_all_vs_z_hits = new H2F("globalwedge_all_vs_z_hits", "globalwedge all hits", 60, -0.5, 59.5,12,-0.5,11.5);
    globalwedge_wedge_vs_z_hits.setTitleX("global wedge");
    globalwedge_wedge_vs_z_hits.setTitleY("component + order");

    DataGroup globalwedge_group = new DataGroup(3, 1);
    globalwedge_group.addDataSet(globalwedge_wedge_hits, 0);
    globalwedge_group.addDataSet(globalwedge_bar_hits, 1);
    globalwedge_group.addDataSet(globalwedge_wedge_vs_z_hits, 2);
    globalwedge_group.addDataSet(globalwedge_all_vs_z_hits, 3);

    this.getDataGroup().add(globalwedge_group, 2, 0, 0);


    // Existing histograms
    H2F rawTDC = new H2F("rawTDC", "rawTDC", 6, 0.5, 6.5, 8, 0.5, 8.5);
    rawTDC.setTitleY("Component");
    rawTDC.setTitleX("Layer");

    H2F occTDC = new H2F("occTDC", "occTDC", 6, 0.5, 6.5, 8, 0.5, 8.5);
    occTDC.setTitleY("Component");
    occTDC.setTitleX("Layer");
    occTDC.setTitle("TDC Occupancy");

    H1F occTDC1D = new H1F("occTDC1D", "occTDC1D", 10, -0.5, 9.5);
    occTDC1D.setTitleX("Scintillator (Scintillator/Layer)");
    occTDC1D.setTitleY("Counts");
    occTDC1D.setTitle("TDC Occupancy");
    occTDC1D.setFillColor(3);

    H2F tdc = new H2F("tdc", "tdc", 300, 167500, 172000, 8, 0.5, 8.5);
    tdc.setTitleX("TDC - Value");
    tdc.setTitleY("Scintillator");

    // WedgeScalers Histogram
    H2F wedgeScalers = new H2F("wedgeScalers", "Wedge Scalers", 60, -0.5, 59.5, 10, -0.5, 9.5);
    wedgeScalers.setTitleX("Wedge Row (Azimuth)");
    wedgeScalers.setTitleY("Wedge Column (z)");
    wedgeScalers.setTitle("Wedge Scalers");
    // Uncomment if you have a method to set the palette
    // wedgeScalers.setOptStat(0); // Disable statistics box

    // BarScalers Histogram
    H2F barScalers = new H2F("barScalers", "Bar Scalers", 60, -0.5, 59.5, 2, -0.5, 1.5);
    barScalers.setTitleX("Bar Row (Azimuth)");
    barScalers.setTitleY("Bar End (z)");
    barScalers.setTitle("Bar Scalers");
    // Uncomment if you have a method to set the palette
    // barScalers.setOptStat(0); // Disable statistics box

    // WedgeTDC Histograms (3x5 grid for sectors 0-14)
    DataGroup wedgeTDCGroup = new DataGroup(3, 5);
    for (int sector = 0; sector < 15; sector++) {
      String histName = "wedgeTDC_sector_" + sector;
      H1F wedgeTDC = new H1F(histName, "Wedge TDC Sector " + sector, 250, 0, 50000);
      wedgeTDC.setTitleX("TDC - Value");
      wedgeTDC.setTitleY("Counts");
      wedgeTDC.setFillColor(38); // Example color
      // Uncomment if you have a method to disable statistics box
      // wedgeTDC.setOptStat(0);
      wedgeTDCGroup.addDataSet(wedgeTDC, sector);
    }

    // BarTDC Histograms (3x5 grid for sectors 0-14)
    DataGroup barTDCGroup = new DataGroup(3, 5);
    for (int sector = 0; sector < 15; sector++) {
      String histName = "barTDC_sector_" + sector;
      H1F barTDC = new H1F(histName, "Bar TDC Sector " + sector, 250, 0, 50000);
      barTDC.setTitleX("TDC - Value");
      barTDC.setTitleY("Counts");
      barTDC.setFillColor(42); // Example color
      // Uncomment if you have a method to disable statistics box
      // barTDC.setOptStat(0);
      barTDCGroup.addDataSet(barTDC, sector);
    }

    // BarSumDiff Histograms
    int barsum_peak = 36000; // peak location used to center the histogram binning:
    H1F barSum = new H1F("barSum", "Bar TDC Sum", 200, barsum_peak-20000, barsum_peak+20000); // Assuming sum range
    barSum.setTitleX("Sum of TDCs (Order0 + Order1)");
    barSum.setTitleY("Counts");
    barSum.setFillColor(46);
    // Uncomment if you have a method to disable statistics box
    // barSum.setOptStat(0);
    float barsumtime_peak = 36000*tdc_bin_time; // peak location used to center the histogram binning:
    H1F barSumTime = new H1F("barSumTime", "Bar Sum Time", 200, barsum_peak*tdc_bin_time-20000*tdc_bin_time, barsum_peak*tdc_bin_time+20000*tdc_bin_time); // Assuming sum range
    barSumTime.setTitleX("Sum of Times (Order0 + Order1)");
    barSumTime.setTitleY("Counts");
    barSumTime.setFillColor(46);

    H1F barDiff = new H1F("barDiff", "Bar TDC Difference", 200, -20000, 20000); // Assuming difference range
    barDiff.setTitleX("Difference of TDCs (Order1 - Order0)");
    barDiff.setTitleY("Counts");
    barDiff.setFillColor(38);
    H1F barDiffTime = new H1F("barDiffTime", "Bar TDC Difference", 200, -5, 7);
    barDiffTime.setTitleX("Difference of Times (Order1 - Order0)");
    barDiffTime.setTitleY("Counts");
    barDiffTime.setFillColor(38);
    // Uncomment if you have a method to disable statistics box
    // barDiff.setOptStat(0);

    // Create data group for existing and new histograms
    DataGroup dg = new DataGroup(1, 1);
    dg.addDataSet(rawTDC, 0);
    dg.addDataSet(occTDC, 0);
    dg.addDataSet(occTDC1D, 0);
    dg.addDataSet(tdc, 0);
    dg.addDataSet(wedgeScalers, 0);
    dg.addDataSet(barScalers, 0);
    this.getDataGroup().add(dg, 1, 0, 0);

    // Create data groups for new histograms
    this.getDataGroup().add(wedgeTDCGroup, 4, 0, 0); // Tab index 4: "WedgeTDC"
    this.getDataGroup().add(barTDCGroup, 5, 0, 0);   // Tab index 5: "BarTDC"

    DataGroup barSumDiffGroup = new DataGroup(2, 1);
    barSumDiffGroup.addDataSet(barSum, 0);
    barSumDiffGroup.addDataSet(barDiff, 1);
    barSumDiffGroup.addDataSet(barSumTime, 2);
    barSumDiffGroup.addDataSet(barDiffTime, 3);
    this.getDataGroup().add(barSumDiffGroup, 6, 0, 0); // Tab index 6: "BarSumDiff"
  }

  @Override
  public void plotHistos() {

    // Plotting existing histograms
    this.getDetectorCanvas().getCanvas("module").cd(0);
    this.getDetectorCanvas().getCanvas("module").draw(this.getDataGroup().getItem(0, 0, 0).getH1F("module_bar_hits"));
    this.getDetectorCanvas().getCanvas("module").cd(1);
    this.getDetectorCanvas().getCanvas("module").draw(this.getDataGroup().getItem(0, 0, 0).getH1F("module_wedge_hits"));

    // Plotting existing histograms
    this.getDetectorCanvas().getCanvas("globalwedge").cd(0);
    this.getDetectorCanvas().getCanvas("globalwedge").draw(this.getDataGroup().getItem(2, 0, 0).getH1F("globalwedge_bar_hits"));
    this.getDetectorCanvas().getCanvas("globalwedge").getPad(0).getAxisY().setLog(true);
    this.getDetectorCanvas().getCanvas("globalwedge").cd(1);
    this.getDetectorCanvas().getCanvas("globalwedge").getPad(1).getAxisY().setLog(true);
    this.getDetectorCanvas().getCanvas("globalwedge").draw(this.getDataGroup().getItem(2, 0, 0).getH1F("globalwedge_wedge_hits"));
    this.getDetectorCanvas().getCanvas("globalwedge").cd(2);
    this.getDetectorCanvas().getCanvas("globalwedge").getPad(2).setPalette("kCool");
    this.getDetectorCanvas().getCanvas("globalwedge").getPad(2).getAxisZ().setLog(true);
    this.getDetectorCanvas().getCanvas("globalwedge").draw(this.getDataGroup().getItem(2, 0, 0).getH2F("globalwedge_wedge_vs_z_hits"));

    //this.getDetectorCanvas().getCanvas("occupancy").getPad(0).setPalette("kCool");
    //this.getDetectorCanvas().getCanvas("occupancy").getPad(0).getAxisZ().setLog(getLogZ());
    //this.getDetectorCanvas().getCanvas("occupancy").draw(this.getDataGroup().getItem(1, 0, 0).getH2F("occTDC"));

    // Plotting existing histograms
    this.getDetectorCanvas().getCanvas("occupancy").cd(0);
    // Optionally, set the palette if supported
    this.getDetectorCanvas().getCanvas("occupancy").getPad(0).setPalette("kCool");
    //this.getDetectorCanvas().getCanvas("occupancy").getPad(0).getAxisZ().setLog(true);
    this.getDetectorCanvas().getCanvas("occupancy").draw(this.getDataGroup().getItem(2, 0, 0).getH2F("globalwedge_all_vs_z_hits"));

    //this.getDetectorCanvas().getCanvas("occupancy").cd(1);
    //// Optionally, set the palette if supported
    //this.getDetectorCanvas().getCanvas("occupancy").getPad(1).setPalette("kCool");
    //this.getDetectorCanvas().getCanvas("occupancy").getPad(1).getAxisY().setLog(true);
    //this.getDetectorCanvas().getCanvas("occupancy").draw(this.getDataGroup().getItem(1, 0, 0).getH1F("occTDC1D"));
    //this.getDetectorCanvas().getCanvas("occupancy").update();

    //this.getDetectorCanvas().getCanvas("tdc").cd(0);
    //// Optionally, set the palette if supported
    //this.getDetectorCanvas().getCanvas("tdc").getPad(0).setPalette("kCool");
    //this.getDetectorCanvas().getCanvas("tdc").getPad(0).getAxisZ().setLog(getLogZ());
    //this.getDetectorCanvas().getCanvas("tdc").draw(this.getDataGroup().getItem(1, 0, 0).getH2F("tdc"));

    // Plot WedgeScalers
    this.getDetectorCanvas().getCanvas("WedgeScalers").cd(0);
    // Optionally, set the palette if supported by your GROOT version
    this.getDetectorCanvas().getCanvas("WedgeScalers").getPad(0).setPalette("kCool");
    this.getDetectorCanvas().getCanvas("WedgeScalers").draw(this.getDataGroup().getItem(1, 0, 0).getH2F("wedgeScalers"));
    this.getDetectorCanvas().getCanvas("WedgeScalers").update();

    // Plot BarScalers
    this.getDetectorCanvas().getCanvas("BarScalers").cd(0);
    // Optionally, set the palette if supported
    this.getDetectorCanvas().getCanvas("BarScalers").getPad(0).setPalette("kCool");
    this.getDetectorCanvas().getCanvas("BarScalers").draw(this.getDataGroup().getItem(1, 0, 0).getH2F("barScalers"));
    this.getDetectorCanvas().getCanvas("BarScalers").update();

    // Plot WedgeTDC Histograms
    DataGroup wedgeTDCGroup = this.getDataGroup().getItem(4, 0, 0);
    for (int sector = 0; sector < 15; sector++) {
      this.getDetectorCanvas().getCanvas("WedgeTDC").cd(sector);
      H1F wedgeTDC = wedgeTDCGroup.getH1F("wedgeTDC_sector_" + sector);
      wedgeTDC.setFillColor(38); // Ensure consistent color
      this.getDetectorCanvas().getCanvas("WedgeTDC").draw(wedgeTDC);
      this.getDetectorCanvas().getCanvas("WedgeTDC").getPad(sector).getAxisY().setLog(true);
    }
    this.getDetectorCanvas().getCanvas("WedgeTDC").update();

    // Plot BarTDC Histograms
    DataGroup barTDCGroup = this.getDataGroup().getItem(5, 0, 0);
    for (int sector = 0; sector < 15; sector++) {
      this.getDetectorCanvas().getCanvas("BarTDC").cd(sector);
      H1F barTDC = barTDCGroup.getH1F("barTDC_sector_" + sector);
      barTDC.setFillColor(42); // Ensure consistent color
      this.getDetectorCanvas().getCanvas("BarTDC").draw(barTDC);
      this.getDetectorCanvas().getCanvas("BarTDC").getPad(sector).getAxisY().setLog(true);
    }
    this.getDetectorCanvas().getCanvas("BarTDC").update();

    // Plot BarSumDiff Histograms
    DataGroup barSumDiffGroup = this.getDataGroup().getItem(6, 0, 0);
    this.getDetectorCanvas().getCanvas("BarSumDiff").cd(0);
    H1F barSum = barSumDiffGroup.getH1F("barSum");
    barSum.setFillColor(46);
    this.getDetectorCanvas().getCanvas("BarSumDiff").draw(barSum);
    this.getDetectorCanvas().getCanvas("BarSumDiff").getPad(0).getAxisY().setLog(true);

    this.getDetectorCanvas().getCanvas("BarSumDiff").cd(1);
    H1F barDiff = barSumDiffGroup.getH1F("barDiff");
    barDiff.setFillColor(38);
    this.getDetectorCanvas().getCanvas("BarSumDiff").draw(barDiff);
    this.getDetectorCanvas().getCanvas("BarSumDiff").update();
    this.getDetectorCanvas().getCanvas("BarSumDiff").getPad(1).getAxisY().setLog(true);


    // Plot BarSumDiff Histograms
    this.getDetectorCanvas().getCanvas("BarSumDiff").cd(2);
    H1F barSumTime = barSumDiffGroup.getH1F("barSumTime");
    barSumTime.setFillColor(46);
    this.getDetectorCanvas().getCanvas("BarSumDiff").draw(barSumTime);
    this.getDetectorCanvas().getCanvas("BarSumDiff").getPad(2).getAxisY().setLog(true);


    this.getDetectorCanvas().getCanvas("BarSumDiff").cd(3);
    H1F barDiffTime = barSumDiffGroup.getH1F("barDiffTime");
    barDiffTime.setFillColor(38);
    this.getDetectorCanvas().getCanvas("BarSumDiff").draw(barDiffTime);
    this.getDetectorCanvas().getCanvas("BarSumDiff").update();
    this.getDetectorCanvas().getCanvas("BarSumDiff").getPad(3).getAxisY().setLog(true);

    // Update detector view
    this.getDetectorView().getView().repaint();
    this.getDetectorView().update();
  }

  @Override
  public void processEvent(DataEvent event) {
    // Clear temporary storage at the start of each event
    barTDCOrder0Map.clear();
    barTDCOrder1Map.clear();

    // Process event info and save into data group
    if (event.hasBank("ATOF::tdc")) {
      DataBank bank = event.getBank("ATOF::tdc");
      int rows = bank.rows();
      for (int loop = 0; loop < rows; loop++) {
        int sector = bank.getByte("sector", loop);
        int layer = bank.getByte("layer", loop);
        int comp = bank.getShort("component", loop);
        int order = bank.getByte("order", loop);
        int tdc = bank.getInt("TDC", loop);
        int tot = bank.getInt("ToT", loop);
        int w = sector*4 + layer ;

        //if (tot > 1000) {
        //  System.out.println("ROW " + loop + " SECTOR = " + sector + " LAYER = " + layer + " COMPONENT = " + comp + " ORDER = " + order +
        //      " TDC = " + tdc + " ToT = " + tot);
        //}
        if (tot > 0) {
          int wire = (layer - 1) * 100 + comp;
          this.getDataGroup().getItem(1, 0, 0).getH2F("occTDC").fill(comp, layer);
          this.getDataGroup().getItem(1, 0, 0).getH1F("occTDC1D").fill(wire);
          this.getDataGroup().getItem(1, 0, 0).getH2F("tdc").fill(tdc * 1.0, wire);
          this.getDetectorSummary().getH1F("summary").fill(sector);

          if(comp <10) {
            this.getDataGroup().getItem(0, 0, 0).getH1F("module_wedge_hits").fill(sector);
          } else{
            this.getDataGroup().getItem(0, 0, 0).getH1F("module_bar_hits").fill(sector);
          }
          if(comp <10) {
            this.getDataGroup().getItem(2, 0, 0).getH1F("globalwedge_wedge_hits").fill(w);
            this.getDataGroup().getItem(2, 0, 0).getH2F("globalwedge_wedge_vs_z_hits").fill(w,comp);
          } else{
            this.getDataGroup().getItem(2, 0, 0).getH1F("globalwedge_bar_hits").fill(w);
          }
          this.getDataGroup().getItem(2, 0, 0).getH2F("globalwedge_all_vs_z_hits").fill(w,comp+order);



          int xbin = sector * 4 + layer ;
          // Fill Wedge Scalers histogram
          if (comp >= 0 && comp <= 9) {
            this.getDataGroup().getItem(1, 0, 0).getH2F("wedgeScalers").fill(xbin, comp);
            // Fill Wedge TDC histogram
            DataGroup wedgeTDCGroup = this.getDataGroup().getItem(4, 0, 0);
            if (sector < 15) { // Ensure sector is within 0-14
              H1F wedgeTDC = wedgeTDCGroup.getH1F("wedgeTDC_sector_" + sector);
              wedgeTDC.fill(tdc * 1.0);
            }
          }
          // Fill Bar Scalers histogram
          else if (comp == 10) {
            this.getDataGroup().getItem(1, 0, 0).getH2F("barScalers").fill(xbin, order);
            // Fill Bar TDC histogram
            DataGroup barTDCGroup = this.getDataGroup().getItem(5, 0, 0);
            if (sector < 15) { // Ensure sector is within 0-14
              H1F barTDC = barTDCGroup.getH1F("barTDC_sector_" + sector);
              barTDC.fill(tdc * 1.0);
            }

            // Store TDCs based on order
            if (order == 0) {
              barTDCOrder0Map.put(xbin, tdc);
            } else if (order == 1) {
              barTDCOrder1Map.put(xbin, tdc);
            }
          }
        }
      }

      // After processing all hits, compute sum and difference for bars where both orders are present
      DataGroup barSumDiffGroup = this.getDataGroup().getItem(6, 0, 0);
      H1F barSum = barSumDiffGroup.getH1F("barSum");
      H1F barDiff = barSumDiffGroup.getH1F("barDiff");
      H1F barSumTime = barSumDiffGroup.getH1F("barSumTime");
      H1F barDiffTime = barSumDiffGroup.getH1F("barDiffTime");

      for (Integer xbin : barTDCOrder0Map.keySet()) {
        if (barTDCOrder1Map.containsKey(xbin)) {
          int tdc0 = barTDCOrder0Map.get(xbin);
          int tdc1 = barTDCOrder1Map.get(xbin);
          int sum = tdc0 + tdc1;
          int diff = tdc1 - tdc0;
          barSum.fill(sum * 1.0);
          barDiff.fill(diff * 1.0);
          barSumTime.fill(sum * tdc_bin_time);
          barDiffTime.fill(diff * tdc_bin_time);
          //System.out.println("sum = " + sum + ", diff = " +  diff);
        }
      }
    }
  }

  @Override
  public void analysisUpdate() {
    if (this.getNumberOfEvents() > 0) {
      H2F raw = this.getDataGroup().getItem(1, 0, 0).getH2F("rawTDC");
      for (int loop = 0; loop < raw.getDataBufferSize(); loop++) {
        this.getDataGroup().getItem(1, 0, 0).getH2F("occTDC").setDataBufferBin(loop, 100 * raw.getDataBufferBin(loop) / this.getNumberOfEvents());
      }
    }
  }
}

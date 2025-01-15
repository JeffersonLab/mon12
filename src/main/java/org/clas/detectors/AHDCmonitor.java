package org.clas.detectors;


import org.clas.viewer.DetectorMonitor;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

/**
 *
 * @author devita
 */

public class AHDCmonitor  extends DetectorMonitor {
	
    public AHDCmonitor(String name) {
        super(name);
        
        this.setDetectorTabNames("charge", "time");
        this.init(false);

    }

    @Override
    public void createHistos() {
        // initialize canvas and create histograms
        this.setNumberOfEvents(0);
	
	this.getDetectorCanvas().getCanvas("charge").divide(1, 3);
        this.getDetectorCanvas().getCanvas("charge").setGridX(false);
        this.getDetectorCanvas().getCanvas("charge").setGridY(false);
	
	this.getDetectorCanvas().getCanvas("time").divide(1, 4);
        this.getDetectorCanvas().getCanvas("time").setGridX(false);
        this.getDetectorCanvas().getCanvas("time").setGridY(false);

	// summary
        H1F summary = new H1F("summary","summary",6,1,7);
        summary.setTitleX("sector");
        summary.setTitleY("AHDC hits");
        summary.setTitle("AHDC");
        summary.setFillColor(36);
        DataGroup sum = new DataGroup(1,1);
        sum.addDataSet(summary, 0);
        this.setDetectorSummary(sum);
	
	// charge
	H2F hist2d_occupancy = new H2F("occupancy", "occupancy", 100, 1, 100, 8, 1, 9);
        hist2d_occupancy.setTitleY("layer number");
        hist2d_occupancy.setTitleX("wire number");
	hist2d_occupancy.setTitle("< occupancy >");
        
	H2F hist2d_adcMax = new H2F("adcMax", "adcMax", 100, 1, 100, 8, 1, 9);
        hist2d_adcMax.setTitleY("layer number");
        hist2d_adcMax.setTitleX("wire number");
	hist2d_adcMax.setTitle("< adcMax >");
	
	H2F hist2d_integral = new H2F("integral", "integral", 100, 1, 100, 8, 1, 9);
        hist2d_integral.setTitleY("layer number");
        hist2d_integral.setTitleX("wire number");
	hist2d_integral.setTitle("< integral >");
        
	// time
	H2F hist2d_timeMax = new H2F("timeMax", "timeMax", 100, 1, 100, 8, 1, 9);
        hist2d_timeMax.setTitleY("layer number");
        hist2d_timeMax.setTitleX("wire number");
	hist2d_timeMax.setTitle("< timeMax >");
        
	H2F hist2d_leadingEdgeTime = new H2F("leadingEdgeTime", "leadingEdgeTime", 100, 1, 100, 8, 1, 9);
        hist2d_leadingEdgeTime.setTitleY("layer number");
        hist2d_leadingEdgeTime.setTitleX("wire number");
	hist2d_leadingEdgeTime.setTitle("< leadingEdgeTime >");
	
	H2F hist2d_timeOverThreshold = new H2F("timeOverThreshold", "timeOverThreshold", 100, 1, 100, 8, 1, 9);
        hist2d_timeOverThreshold.setTitleY("layer number");
        hist2d_timeOverThreshold.setTitleX("wire number");
	hist2d_timeOverThreshold.setTitle("< timeOverThreshold >");
	
	H2F hist2d_constantFractionTime = new H2F("constantFractionTime", "constantFractionTime", 100, 1, 100, 8, 1, 9);
        hist2d_constantFractionTime.setTitleY("layer number");
        hist2d_constantFractionTime.setTitleX("wire number");
	hist2d_constantFractionTime.setTitle("< constantFractionTime >");
	
	// add graph to DataGroup
        DataGroup dg = new DataGroup(7,1); 
        dg.addDataSet(hist2d_occupancy, 0);
        dg.addDataSet(hist2d_adcMax, 1);
        dg.addDataSet(hist2d_integral, 2);
        dg.addDataSet(hist2d_timeMax, 3);
        dg.addDataSet(hist2d_leadingEdgeTime, 4);
	dg.addDataSet(hist2d_timeOverThreshold, 5);
	dg.addDataSet(hist2d_constantFractionTime, 6);
        this.getDataGroup().add(dg,0,0,0);
    }
        
    @Override
    public void plotHistos() {
        // plotting histos
        this.getDetectorCanvas().getCanvas("charge").cd(0);
        this.getDetectorCanvas().getCanvas("charge").getPad(0).getAxisZ().setLog(getLogZ());
        this.getDetectorCanvas().getCanvas("charge").draw(this.getDataGroup().getItem(0,0,0).getH2F("occupancy"));
        this.getDetectorCanvas().getCanvas("charge").cd(1);
        this.getDetectorCanvas().getCanvas("charge").getPad(1).getAxisZ().setLog(true);
        this.getDetectorCanvas().getCanvas("charge").draw(this.getDataGroup().getItem(0,0,0).getH2F("adcMax"));
        this.getDetectorCanvas().getCanvas("charge").cd(2);
        this.getDetectorCanvas().getCanvas("charge").getPad(2).getAxisZ().setLog(true);
        this.getDetectorCanvas().getCanvas("charge").draw(this.getDataGroup().getItem(0,0,0).getH2F("integral"));
	this.getDetectorCanvas().getCanvas("charge").update();
        
        this.getDetectorCanvas().getCanvas("time").cd(0);
        this.getDetectorCanvas().getCanvas("time").getPad(0).getAxisZ().setLog(getLogZ());
        this.getDetectorCanvas().getCanvas("time").draw(this.getDataGroup().getItem(0,0,0).getH2F("timeMax"));
        this.getDetectorCanvas().getCanvas("time").cd(1);
        this.getDetectorCanvas().getCanvas("time").getPad(1).getAxisZ().setLog(getLogZ());
        this.getDetectorCanvas().getCanvas("time").draw(this.getDataGroup().getItem(0,0,0).getH2F("leadingEdgeTime"));
	this.getDetectorCanvas().getCanvas("time").cd(2);
	this.getDetectorCanvas().getCanvas("time").getPad(2).getAxisZ().setLog(getLogZ());
	this.getDetectorCanvas().getCanvas("time").draw(this.getDataGroup().getItem(0,0,0).getH2F("timeOverThreshold"));
	this.getDetectorCanvas().getCanvas("time").cd(3);
	this.getDetectorCanvas().getCanvas("time").getPad(3).getAxisZ().setLog(getLogZ());
	this.getDetectorCanvas().getCanvas("time").draw(this.getDataGroup().getItem(0,0,0).getH2F("constantFractionTime"));
	this.getDetectorCanvas().getCanvas("time").update();

        
        this.getDetectorView().getView().repaint();
        this.getDetectorView().update();
    }

    @Override
    public void processEvent(DataEvent event) {
        // process event info and save into data group
        if(event.hasBank("AHDC::adc")==true){
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>    HAs AHDC::adc event");
	    DataBank bank = event.getBank("AHDC::adc");
	    int rows = bank.rows();
	    for(int loop = 0; loop < rows; loop++){
                int sector  = bank.getByte("sector", loop);
                int layer   = bank.getByte("layer", loop);
                int comp    = bank.getShort("component", loop);
                int order   = bank.getByte("order", loop);
                int adc     = bank.getInt("ADC", loop);
                float time  = bank.getFloat("time", loop);
		float leadingEdgeTime = bank.getFloat("leadingEdgeTime", loop);
		float timeOverThreshold = bank.getFloat("timeOverThreshold", loop);
		float constantFractionTime = bank.getFloat("constantFractionTime", loop);
		int integral = bank.getInt("integral", loop);
                
                System.out.println("ROW " + loop + " SECTOR = " + sector + " LAYER = " + layer + " COMPONENT = " + comp + " ORDER + " + order +
                      " ADC = " + adc + " TIME = " + time + "leadingEdgeTime = " + leadingEdgeTime); 
                if(adc>=0 && time>0) {
                    int wire = (layer-1)*100+comp;
                    this.getDetectorSummary().getH1F("summary").fill(wire);
		    
		    int layer_number = 0;
		    switch (layer) {
			case 11 :
				layer_number = 1;
				break;
			case 21 :
				layer_number = 2;
				break;
			case 22 :
				layer_number = 3;
				break;
			case 31 :
				layer_number = 4;
				break;
			case 32 :
				layer_number = 5;
				break;
			case 41 :
				layer_number = 6;
				break;
			case 42 :
				layer_number = 7;
				break;
			case 51 :
				layer_number = 8;
				break;
		    }
		    this.getDataGroup().getItem(0,0,0).getH2F("occupancy").fill(comp, layer_number);
		    this.getDataGroup().getItem(0,0,0).getH2F("adcMax").fill(comp, layer_number, adc);
		    this.getDataGroup().getItem(0,0,0).getH2F("integral").fill(comp, layer_number, integral);
		    this.getDataGroup().getItem(0,0,0).getH2F("timeMax").fill(comp, layer_number, time);
		    this.getDataGroup().getItem(0,0,0).getH2F("leadingEdgeTime").fill(comp, layer_number, leadingEdgeTime);
		    this.getDataGroup().getItem(0,0,0).getH2F("timeOverThreshold").fill(comp, layer_number, timeOverThreshold);
		    this.getDataGroup().getItem(0,0,0).getH2F("constantFractionTime").fill(comp, layer_number, constantFractionTime);
                    
                }
	    }
    	}
    }

    @Override
    public void analysisUpdate() {
        if(this.getNumberOfEvents()>0) {
            H2F raw = this.getDataGroup().getItem(0,0,0).getH2F("rawADC");
            for(int loop = 0; loop < raw.getDataBufferSize(); loop++){
                this.getDataGroup().getItem(0,0,0).getH2F("occADC").setDataBufferBin(loop,100*raw.getDataBufferBin(loop)/this.getNumberOfEvents());
            }
        }
    }

}


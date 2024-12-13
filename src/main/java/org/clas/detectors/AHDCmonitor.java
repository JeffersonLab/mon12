package org.clas.detectors;


import org.clas.viewer.DetectorMonitor;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

// Felix : tmp
import org.jlab.geom.detector.alert.AHDC.*;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.base.ConstantProvider;
import org.jlab.groot.data.GraphErrors;

/**
 *
 * @author devita
 */

public class AHDCmonitor  extends DetectorMonitor {
        
    public AHDCmonitor(String name) {
        super(name);
        
        this.setDetectorTabNames("occupancy", "adc", "geom");
        this.init(false);
    }

    @Override
    public void createHistos() {
        // initialize canvas and create histograms
        this.setNumberOfEvents(0);
        // tab : occupancy
	this.getDetectorCanvas().getCanvas("occupancy").divide(1, 2);
        this.getDetectorCanvas().getCanvas("occupancy").setGridX(false);
        this.getDetectorCanvas().getCanvas("occupancy").setGridY(false);
        // tab : adc
	this.getDetectorCanvas().getCanvas("adc").divide(2, 1);
        this.getDetectorCanvas().getCanvas("adc").setGridX(false);
        this.getDetectorCanvas().getCanvas("adc").setGridY(false);
	// tab : geom
	this.getDetectorCanvas().getCanvas("geom").divide(1, 1);
	this.getDetectorCanvas().getCanvas("geom").setGridX(false);
	this.getDetectorCanvas().getCanvas("geom").setGridY(false);

	// used in summary tab
        H1F summary = new H1F("summary","summary",6,1,7);
        summary.setTitleX("sector");
        summary.setTitleY("AHDC hits");
        summary.setTitle("AHDC");
        summary.setFillColor(36);
        DataGroup sum = new DataGroup(1,1);
        sum.addDataSet(summary, 0);
        this.setDetectorSummary(sum);
	// used in occupancy tab
	H2F rawADC = new H2F("rawADC", "rawADC", 16, 0.5, 16.5, 80, 0.5, 80.5);
        rawADC.setTitleY("component");
        rawADC.setTitleX("layer");
        H2F occADC = new H2F("occADC", "occADC", 16, 0.5, 16.5, 80, 0.5, 80.5);
        occADC.setTitleY("component");
        occADC.setTitleX("layer");
        occADC.setTitle("ADC Occupancy");
        H1F occADC1D = new H1F("occADC1D", "occADC1D", 5000, 0.5, 5000.5);
        occADC1D.setTitleX("Wire");
        occADC1D.setTitleY("Counts");
        occADC1D.setTitle("ADC Occupancy");
        occADC1D.setFillColor(3);
        // used in adc tab
        H2F adc = new H2F("adc", "adc", 150, 0, 15000, 8, 0.5, 8000.5);
        adc.setTitleX("ADC - value");
        adc.setTitleY("Wire");
        H2F time = new H2F("time", "time", 80, 0, 400, 8, 0.5, 8000.5);
        time.setTitleX("Time (ns)");
        time.setTitleY("Wire");
        // used in geom tab
	AHDCview ahdc = new AHDCview();
	GraphErrors view2D = ahdc.graph2D;
	view2D.setTitle("view2D");
	view2D.setTitleX("x (mm)");
	view2D.setTitleX("y (mm)");
	view2D.setMarkerStyle(0);
	view2D.setMarkerSize(7);
	view2D.setMarkerColor(2);

	// add graph to DataGroup
        DataGroup dg = new DataGroup(5,1); // <-- Felix 5 au lieu de 4
        dg.addDataSet(rawADC, 0);
        dg.addDataSet(occADC, 0);
        dg.addDataSet(occADC1D, 1);
        dg.addDataSet(adc, 2);
        dg.addDataSet(time, 3);
	dg.addDataSet(view2D, 4); // --> Felix
        this.getDataGroup().add(dg,0,0,0);
    }
        
    @Override
    public void plotHistos() {
        // plotting histos
        this.getDetectorCanvas().getCanvas("occupancy").cd(0);
        this.getDetectorCanvas().getCanvas("occupancy").getPad(0).getAxisZ().setLog(getLogZ());
        this.getDetectorCanvas().getCanvas("occupancy").draw(this.getDataGroup().getItem(0,0,0).getH2F("occADC"));
        this.getDetectorCanvas().getCanvas("occupancy").cd(1);
        this.getDetectorCanvas().getCanvas("occupancy").getPad(1).getAxisY().setLog(true);
        this.getDetectorCanvas().getCanvas("occupancy").draw(this.getDataGroup().getItem(0,0,0).getH1F("occADC1D"));
        this.getDetectorCanvas().getCanvas("occupancy").update();
        
        this.getDetectorCanvas().getCanvas("adc").cd(0);
        this.getDetectorCanvas().getCanvas("adc").getPad(0).getAxisZ().setLog(getLogZ());
        this.getDetectorCanvas().getCanvas("adc").draw(this.getDataGroup().getItem(0,0,0).getH2F("adc"));
        this.getDetectorCanvas().getCanvas("adc").cd(1);
        this.getDetectorCanvas().getCanvas("adc").getPad(0).getAxisZ().setLog(getLogZ());
        this.getDetectorCanvas().getCanvas("adc").draw(this.getDataGroup().getItem(0,0,0).getH2F("time"));
        this.getDetectorCanvas().getCanvas("adc").update();

	this.getDetectorCanvas().getCanvas("geom").cd(0);
	this.getDetectorCanvas().getCanvas("geom").getPad(0).getAxisZ().setLog(getLogZ());
	this.getDetectorCanvas().getCanvas("geom").draw(this.getDataGroup().getItem(0,0,0).getGraph("view2D"));
	this.getDetectorCanvas().getCanvas("geom").update();
        
        
        this.getDetectorView().getView().repaint();
        this.getDetectorView().update();
    }

    @Override
    public void processEvent(DataEvent event) {

        // process event info and save into data group
        if(event.hasBank("AHDC::adc")==true){
	    DataBank bank = event.getBank("AHDC::adc");
	    int rows = bank.rows();
	    for(int loop = 0; loop < rows; loop++){
                int sector  = bank.getByte("sector", loop);
                int layer   = bank.getByte("layer", loop);
                int comp    = bank.getShort("component", loop);
                int order   = bank.getByte("order", loop);
                int adc     = bank.getInt("ADC", loop);
                float time  = bank.getFloat("time", loop);
                               
//                System.out.println("ROW " + loop + " SECTOR = " + sector + " LAYER = " + layer + " COMPONENT = " + comp + " ORDER + " + order +
//                      " ADC = " + adc + " TIME = " + time); 
                if(adc>=0 && time>0) {
                    int wire = (layer-1)*100+comp;
                    this.getDataGroup().getItem(0,0,0).getH2F("occADC").fill(comp, layer);
                    this.getDataGroup().getItem(0,0,0).getH1F("occADC1D").fill(wire);
                    this.getDataGroup().getItem(0,0,0).getH2F("adc").fill(adc*1.0,wire);
                    this.getDataGroup().getItem(0,0,0).getH2F("time").fill(time,wire);
                    this.getDetectorSummary().getH1F("summary").fill(wire);
                    
                    
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

class AHDCview {
	public AlertDCDetector ahdc;
	public GraphErrors graph2D;
	/** Default constructor */
	AHDCview () {
		//ahdc = new AlertDCFactory().createDetectorCLAS(new ConstantProvider());
		ahdc = new AlertDCFactory().createDetectorCLAS(new AlertConstantProvider());
		// To use the method get***, the numerotation must start at 0 !!!
		Point3D midpoint1 = ahdc.getSector(0).getSuperlayer(0).getLayer(0).getComponent(0).getMidpoint();
		System.out.println("***********************  FELIX  *****************************");
		System.out.println(">>>> TEST AHDC_VIEW");
		System.out.println(midpoint1.toString());
		System.out.println("********************** END FELIX  *****************************");
	
		// ************
		graph2D = new GraphErrors("view2D");
		for (int sectorId = 0; sectorId < ahdc.getNumSectors(); sectorId++){
			for (int superlayerId = 0; superlayerId < ahdc.getSector(sectorId).getNumSuperlayers(); superlayerId++){
				for (int layerId = 0; layerId < ahdc.getSector(sectorId).getSuperlayer(superlayerId).getNumLayers(); layerId++){
					for (int wireId = 0; wireId < ahdc.getSector(sectorId).getSuperlayer(superlayerId).getLayer(layerId).getNumComponents(); wireId++){
						Point3D midpoint = ahdc.getSector(sectorId).getSuperlayer(superlayerId).getLayer(layerId).getComponent(wireId).getMidpoint();
						graph2D.addPoint(midpoint.x(), midpoint.y(), 0.0, 0.0);
					}
				}
			}
		}
	}
}

class AlertConstantProvider implements ConstantProvider {
	// empty
	public boolean hasConstant(String name) {return false;}
	public int length(String name) {return 0;}
	public double getDouble(String name, int row) {return 0;}
	public int getInteger(String name, int row) {return 0;}
}

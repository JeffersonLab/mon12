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
import org.jlab.groot.math.Axis;

/**
 *
 * @author devita
 */

public class AHDCmonitor  extends DetectorMonitor {
	
    AhdcView ahdc; ///< AHDC geometry, possibility to use method such as get{Sector, Superlayer, Layer, Component}   
    //AlertDCDetector ahdc; ///< AHDC geometry, possibility to use method such as get{Sector, Superlayer, Layer, Component}

    public AHDCmonitor(String name) {
        super(name);
        
        this.setDetectorTabNames("occupancy", "adc", "geom", "geom_bis");
        this.init(false);

	//ahdc = new AhdcView();
	//ahdc = new AhdcView().ahdc;
    }
    { ahdc = new AhdcView(); }

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
	// tab : geom_bis
	this.getDetectorCanvas().getCanvas("geom_bis").divide(1, 1);
	this.getDetectorCanvas().getCanvas("geom_bis").setGridX(false);
	this.getDetectorCanvas().getCanvas("geom_bis").setGridY(false);

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
	GraphErrors view2D = ahdc.graph2D;
	view2D.setTitle("view2D");
	view2D.setTitleX("x (mm)");
	view2D.setTitleX("y (mm)");
	view2D.setMarkerStyle(0);
	view2D.setMarkerSize(7);
	view2D.setMarkerColor(2);
	// used in geom_bis tab
	AhdcH2F hist2d_occ = new AhdcH2F("hist2d_occ",1000,-80,80,1000,-80,80); // occupancy
	hist2d_occ.setTitle("hist2d_occ");
	hist2d_occ.setTitleX("x (mm)");
	hist2d_occ.setTitleY("y (mm)");

	// add graph to DataGroup
        DataGroup dg = new DataGroup(6,1); 
        dg.addDataSet(rawADC, 0);
        dg.addDataSet(occADC, 0);
        dg.addDataSet(occADC1D, 1);
        dg.addDataSet(adc, 2);
        dg.addDataSet(time, 3);
	dg.addDataSet(view2D, 4); // --> Felix
	dg.addDataSet(hist2d_occ, 5);
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

	this.getDetectorCanvas().getCanvas("geom_bis").cd(0);
	this.getDetectorCanvas().getCanvas("geom_bis").getPad(0).getAxisZ().setLog(getLogZ());
	this.getDetectorCanvas().getCanvas("geom_bis").draw(this.getDataGroup().getItem(0,0,0).getH2F("hist2d_occ"));
	this.getDetectorCanvas().getCanvas("geom_bis").update();
        
        
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
		    
		    int sectorId = sector;
		    int superlayerId = layer/10;
		    int layerId = layer - superlayerId*10;
		    int componentId = comp;
		    // when using the getter methods, numerotations start at 0 !!!
		    Point3D midpoint = ahdc.ahdc.getSector(sectorId-1).getSuperlayer(superlayerId-1).getLayer(layerId-1).getComponent(componentId-1).getMidpoint();
		    this.getDataGroup().getItem(0,0,0).getH2F("hist2d_occ").fill(midpoint.x(),midpoint.y());
                    
                    
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

class AhdcView {
	public AlertDCDetector ahdc;
	public GraphErrors graph2D;
	/** Default constructor */
	AhdcView () {
		//ahdc = new AlertDCFactory().createDetectorCLAS(new ConstantProvider());
		ahdc = new AlertDCFactory().createDetectorCLAS(new AhdcConstantProvider());
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

/** Empty class : do nothing, useful for class AHDCview */
class AhdcConstantProvider implements ConstantProvider {
	public boolean hasConstant(String name) {return false;}
	public int length(String name) {return 0;}
	public double getDouble(String name, int row) {return 0.0;}
	public int getInteger(String name, int row) {return 0;}
}

class AhdcH2F extends H2F {
	private double radius; ///< AHDC wire radius for representation, must be <= 1
	
	/** Constructor */
	public AhdcH2F(String name, int bx, double xmin, double xmax, int by, double ymin, double ymax) {
		super(name,bx,xmin,xmax,by,ymin,ymax);
		radius = 1;
	}
	
	/** 
	 * Main purpose of this class
	 * 
	 * x,y are the location of the sense wire determined
	 * from sector, superlayer, layer, componenent ids
	 * As the plane is discretised, we fill all the "rectangle"
	 * comprised in the circle of center (x,y) and of radius this.radius
	 * 
	 */
	@Override
	public void fill(double x, double y) {
		Axis xAxis = super.getXAxis();
		Axis yAxis = super.getYAxis();
		int binx = xAxis.getBin(x);
		int biny = yAxis.getBin(y);
		double xc = xAxis.getBinCenter(binx);
		double yc = yAxis.getBinCenter(biny);
		double deltax = xAxis.getBinWidth(binx);
		double deltay = yAxis.getBinWidth(biny);	
		int xRange = new Double(radius/deltax).intValue(); 
		int yRange = new Double(radius/deltay).intValue();
		for (int nx = -xRange; nx <= xRange; nx++) {
			for (int ny = -yRange; ny <= yRange; ny++) {
				double X = xc + nx*deltax;
				double Y = yc + ny*deltay;
				if (Math.sqrt((X-xc)*(X-xc)+(Y-yc)*(Y-yc)) < radius) {
					super.fill(X,Y);
				}
			}
		}
	}
	
	/** This method must set just after the instanciation or before using the first fill() method */
	public void SetWireRadius(double _radius){
		radius = _radius;
	}
}

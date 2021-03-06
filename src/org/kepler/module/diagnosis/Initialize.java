package org.kepler.module.diagnosis;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.kepler.build.modules.Module;
import org.kepler.configuration.ConfigurationManager;
import org.kepler.configuration.ConfigurationProperty;
import org.kepler.diagnosis.DiagnosisManager;
import org.kepler.diagnosis.gui.DiagnosisAction;
import org.kepler.gui.KeplerGraphFrame;
import org.kepler.gui.KeplerGraphFrame.Components;
import org.kepler.gui.KeplerGraphFrameUpdater;
import org.kepler.gui.ViewManager;
import org.kepler.module.ModuleInitializer;
import org.kepler.util.ProvenanceStore;
import org.kepler.workflowrunmanager.WRMDefaults;

import ptolemy.kernel.util.NamedObj;

public class Initialize implements KeplerGraphFrameUpdater, ModuleInitializer
{

	public int compareTo(KeplerGraphFrameUpdater o)
	{
		// we currently like view menu always on far-right
    	// setting this high:
		return 3;
	}

	/** Perform any module-specific initializations. */
	public void initializeModule()
	{
		KeplerGraphFrame.addUpdater(this);
		
		ConfigurationManager cm = ConfigurationManager.getInstance();
		        
        ConfigurationProperty commonProperty = cm.getProperty(ConfigurationManager.getModule("common"));
        ConfigurationProperty diagnosisProperty = cm.getProperty(ConfigurationManager.getModule("diagnosis"));
        ConfigurationProperty guiProperty = cm.getProperty(ConfigurationManager.getModule("gui"));
        
        //we need to override the common tab pane properties with the ones from diagnosis
        ConfigurationProperty commonViewPaneTabPanesProp = commonProperty.getProperty("viewPaneTabPanes");
        ConfigurationProperty diagnosisViewPaneTabPanesProp = diagnosisProperty.getProperty("viewPaneTabPanes");
        commonProperty.overrideProperty(commonViewPaneTabPanesProp, diagnosisViewPaneTabPanesProp, true);
        
        ConfigurationProperty commonCanvasViewPaneLocationProp = commonProperty.getProperty("canvasViewPaneLocation");
        ConfigurationProperty diagnosisCanvasViewPaneLocationProp = diagnosisProperty.getProperty("canvasViewPaneLocation");
        commonProperty.overrideProperty(commonCanvasViewPaneLocationProp, diagnosisCanvasViewPaneLocationProp, true);
        
      //override the viewPaneFactory
        ConfigurationProperty guiViewPaneFactory = guiProperty.getProperty("viewPaneFactory");
        ConfigurationProperty diagnosisViewPaneFactory = diagnosisProperty.getProperty("viewPaneFactory");
        guiProperty.overrideProperty(guiViewPaneFactory, diagnosisViewPaneFactory, true);
        
        //override the tabPaneFactory
        ConfigurationProperty guiTabPaneFactory = guiProperty.getProperty("tabPaneFactory");
        ConfigurationProperty diagnosisTabPaneFactory = diagnosisProperty.getProperty("tabPaneFactory");
        guiProperty.overrideProperty(guiTabPaneFactory, diagnosisTabPaneFactory, true);
        
        System.out.println("common tabpane configuration overridden by diagnosis");
        
        // connect diagnosis to default prov store
        // use workflow run manager module's provenance default properties to set diagnosis module
        ProvenanceStore provenanceStore = new ProvenanceStore(WRMDefaults.provenanceDefaultsProperty);
        DiagnosisManager dm = DiagnosisManager.getInstance();
        dm.setProvenanceStore(provenanceStore);
        dm.connect();
	}

	public void updateFrameComponents(Components components)
	{
		DiagnosisAction action = new DiagnosisAction("diagnosis");
		JToolBar toolbar = components.getToolBar();
		diva.gui.GUIUtilities.addToolBarButton(toolbar, action);
	}

	public void dispose(KeplerGraphFrame frame)
	{
	}

}

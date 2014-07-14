package org.kepler.module.diagnosis;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.kepler.build.modules.Module;
import org.kepler.configuration.ConfigurationManager;
import org.kepler.configuration.ConfigurationProperty;
import org.kepler.gui.KeplerGraphFrame;
import org.kepler.gui.KeplerGraphFrame.Components;
import org.kepler.gui.KeplerGraphFrameUpdater;
import org.kepler.gui.ViewManager;
import org.kepler.module.ModuleInitializer;

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
        Module dm = ConfigurationManager.getModule("diagnosis");
        ConfigurationProperty diagnosisProperty = cm.getProperty(ConfigurationManager.getModule("diagnosis"));
        ConfigurationProperty guiProperty = cm.getProperty(ConfigurationManager.getModule("gui"));
        
        //we need to override the common tab pane properties with the ones from reporting
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
        
	}

	public void updateFrameComponents(Components components)
	{
		final NamedObj model = components.getFrame().getModel().toplevel();
		
		JToolBar toolbar = components.getToolBar();
		
		final KeplerGraphFrame frame = components.getFrame();
		
		ViewManager vman = ViewManager.getInstance();
		JComponent vsel = vman.getViewSelector(frame);
		
		
	}

	public void dispose(KeplerGraphFrame frame)
	{
	}

}

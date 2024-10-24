/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.processor.supplier.massshiftdetector.ui.wizards;

import java.util.Date;

import jakarta.xml.bind.JAXBException;

import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.INewWizard;

import net.openchrom.chromatogram.msd.processor.supplier.massshiftdetector.core.MassShiftDetector;
import net.openchrom.chromatogram.msd.processor.supplier.massshiftdetector.io.ProcessorModelWriter;
import net.openchrom.chromatogram.msd.processor.supplier.massshiftdetector.model.IProcessorModel;

public class WizardProcessor extends AbstractFileWizard implements INewWizard {

	private IProcessorWizardElements wizardElements = new ProcessorWizardElements();
	//
	private PageFileSelection pageFileSelection;
	private PageSettings pageSettings;

	public WizardProcessor() {
		super("MassShiftDetector" + new Date().getTime(), MassShiftDetector.PROCESSOR_FILE_EXTENSION);
	}

	@Override
	public void addPages() {

		super.addPages();
		/*
		 * Pages must implement IExtendedWizardPage / extend AbstractExtendedWizardPage
		 */
		pageFileSelection = new PageFileSelection(wizardElements);
		pageSettings = new PageSettings(wizardElements);
		//
		addPage(pageFileSelection);
		addPage(pageSettings);
	}

	@Override
	public boolean canFinish() {

		boolean canFinish = pageFileSelection.canFinish();
		if(canFinish) {
			canFinish = pageSettings.canFinish();
		}
		return canFinish;
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("MassShiftDetector", IProgressMonitor.UNKNOWN);
		final IFile file = super.prepareProject(monitor);
		//
		try {
			IProcessorModel processorModel = wizardElements.getProcessorModel();
			ProcessorModelWriter processorModelWriter = new ProcessorModelWriter();
			processorModelWriter.write(file.getLocation().toFile(), processorModel, monitor);
		} catch(JAXBException e) {
			System.out.println(e);
		}
		/*
		 * Refresh
		 */
		super.refreshWorkspace(monitor);
		super.runOpenEditor(file, monitor);
	}
}

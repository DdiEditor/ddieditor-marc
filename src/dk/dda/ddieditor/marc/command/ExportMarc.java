package dk.dda.ddieditor.marc.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.xml.transform.Transformer;

import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.model.userid.UserIdType;
import org.ddialliance.ddieditor.ui.util.PrintUtil;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import dk.dda.ddieditor.marc.dialog.ExportMarcDialog;

public class ExportMarc extends org.eclipse.core.commands.AbstractHandler {

	private String baseName;
	private File tmpDdilFile;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// select resource to export and options
		final ExportMarcDialog marcWizard = new ExportMarcDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		int returnCode = marcWizard.open();
		if (returnCode == Window.CANCEL) {
			return null;
		}
		if (marcWizard.ddiResource == null) {
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator
					.trans("PrintDDI3Action.tooltip.PrintDDI3"), Translator
					.trans("PrintDDI3Action.mess.ResourceNotSpecified"));
			return null;
		}

		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								monitor.beginTask(
										Translator
												.trans("PrintDDI3Action.tooltip.PrintDDI3"),
										1);

								PlatformUI.getWorkbench().getDisplay()
										.syncExec(new Runnable() {
											@Override
											public void run() {
												try {
													// base name
													int index = marcWizard.ddiResource
															.getOrgName()
															.indexOf(".");
													if (index > -1) {
														baseName = marcWizard.ddiResource
																.getOrgName()
																.substring(0,
																		index);
													} else {
														baseName = marcWizard.ddiResource
																.getOrgName();
													}

													// export the resource
													tmpDdilFile = File
															.createTempFile(
																	baseName,
																	".xml");
													tmpDdilFile.deleteOnExit();
													PersistenceManager
															.getInstance()
															.exportResoure(
																	marcWizard.ddiResource
																			.getOrgName(),
																	tmpDdilFile,
																	org.ddialliance.ddieditor.ui.Activator
																			.getDefault()
																			.getPreferenceStore()
																			.getString(
																					UserIdType.DDI_EDITOR_VERSION
																							.getType()));

													// transform
													PrintUtil printUtil = new PrintUtil();

													// ddil to marc
													File marcFile = createFile(
															marcWizard.exportPath,
															"MarcXML-"
																	+ baseName
																	+ ".xml");
													Transformer transformer = new PrintUtil()
															.getDdilToMarcTransformer();
													transformer
															.setParameter(
																	"lang",
																	DdiEditorConfig
																			.get(DdiEditorConfig.DDI_LANGUAGE));
													PrintUtil.doTranformation(
															transformer,
															tmpDdilFile,
															marcFile);

													// marc to english html
													if (marcWizard.marc2EnglishHtmlBoolean) {
														File marctoenglishhtmlFile = createFile(
																marcWizard.exportPath,
																"MarcReadable-"
																		+ baseName
																		+ ".html");
														transformer = printUtil
																.getMarcToEnglishHtmlTransformer();
														PrintUtil
																.doTranformation(
																		transformer,
																		marcFile,
																		marctoenglishhtmlFile);
													}

													// march to spec html
													if (marcWizard.marc2SpecHtmlBoolean) {
														File marc2SpecHtmlFile = createFile(
																marcWizard.exportPath,
																"MarcSpec-"
																		+ baseName
																		+ ".html");
														transformer = printUtil
																.getMarcToSpecTransformer();
														PrintUtil
																.doTranformation(
																		transformer,
																		marcFile,
																		marc2SpecHtmlFile);
													}

													// marc to validate marc xml
													if (marcWizard.marc2ValidateMarcXmlBoolean) {
														File marc2ValidateMarcXmlFile = createFile(
																marcWizard.exportPath,
																"MarcValidate-"
																		+ baseName
																		+ ".xml");
														transformer = printUtil
																.getMarcToValidationXmlTransformer();
														PrintUtil
																.doTranformation(
																		transformer,
																		marcFile,
																		marc2ValidateMarcXmlFile);
													}
												} catch (Exception e) {
													MessageDialog
															.openError(
																	PlatformUI
																			.getWorkbench()
																			.getDisplay()
																			.getActiveShell(),
																	Translator
																			.trans("PrintDDI3Action.mess.PrintDDI3Error"),
																	e.getMessage());
												}
											}
										});
								monitor.worked(1);
							} catch (Exception e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (Exception e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(),
					Translator.trans("PrintDDI3Action.mess.PrintDDI3Error"),
					e.getMessage());
		}
		return null;
	}

	private File createFile(String dir, String fileName) {
		File file = new File(dir + File.separator + fileName);
		if (file.exists()) {
			file.delete();
		}
		return file;
	}
}

package dk.dda.ddieditor.marc.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.preference.PreferenceUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ExportMarcDialog extends Dialog {
	List<DDIResourceType> resources = new ArrayList<DDIResourceType>();
	public DDIResourceType ddiResource;

	public boolean marc2EnglishHtmlBoolean = true;
	public boolean marc2SpecHtmlBoolean = true;
	public boolean marc2ValidateMarcXmlBoolean = true;
	public String exportPath = null;

	public ExportMarcDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent,
				Translator.trans("marc.properties"));
		this.getShell().setText(
				Translator.trans("marc.menu.export"));

		// save as zip package
		editor.createLabel(group,
				Translator.trans("marc.filepath"));

		exportPath = PreferenceUtil.getLastBrowsedPath()
				.getAbsolutePath();
		Button pathBrowse = editor.createButton(group,
				Translator.trans("marc.filepath.select"));
		pathBrowse.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1));
		pathBrowse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirChooser = new DirectoryDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell());
				dirChooser.setText(Translator
						.trans("ExportDDI3Action.filechooser.title"));
				PreferenceUtil.setPathFilter(dirChooser);
				exportPath = dirChooser.open();
				if (exportPath != null) {
					PreferenceUtil.setLastBrowsedPath(exportPath);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// selectable resources
		try {
			resources = PersistenceManager.getInstance().getResources();
		} catch (DDIFtpException e) {
			String errMess = MessageFormat
					.format(Translator
							.trans("marc.mess.error"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator.trans("ErrorTitle"), errMess);
		}

		// label
		editor.createLabel(group,
				Translator.trans("marc.resource.choose"));

		// resource combo
		String[] comboOptions = new String[resources.size()];
		for (int i = 0; i < comboOptions.length; i++) {
			comboOptions[i] = resources.get(i).getOrgName();
		}
		final Combo resouceCombo = editor.createCombo(group, comboOptions);

		// resource selection
		if (comboOptions.length == 1) {
			resouceCombo.select(0);
			ddiResource = resources.get(0);
		} else {
			resouceCombo.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = ((Combo) e.getSource()).getSelectionIndex();
					ddiResource = resources.get(selection);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing
				}
			});
		}

		// create marc to english html
		Button marc2EnglishHtmlButton = editor.createCheckBox(group, "",
				Translator.trans("marc.select.marctoenglishhtml"));
		marc2EnglishHtmlButton.setSelection(true);
		marc2EnglishHtmlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				marc2EnglishHtmlBoolean = ((Button) e.widget).getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// create march to spec html
		Button march2SpecHtmlButton = editor.createCheckBox(group, "",
				Translator.trans("marc.select.marchtospechtml"));
		march2SpecHtmlButton.setSelection(true);
		march2SpecHtmlButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				marc2SpecHtmlBoolean = ((Button) e.widget).getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// create march validate marc xml
		Button march2ValidateMarcXmlButton = editor.createCheckBox(group, "",
				Translator.trans("marc.select.marchvalidatemarcxml"));
		march2ValidateMarcXmlButton.setSelection(true);
		march2ValidateMarcXmlButton
				.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						marc2ValidateMarcXmlBoolean = ((Button) e.widget)
								.getSelection();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// do nothing
					}
				});

		return null;
	}
}

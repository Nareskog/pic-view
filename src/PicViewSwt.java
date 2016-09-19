import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class PicViewSwt {
	// ========================================
	// Inner class to represent a child Shell
	// ========================================

	// Widget size
	int width = 98;

	// Shells
	final Shell mainShell;
	final MonitorShell monitor;

	// GUI widgets
	private FileList dirList;
	private FileList fileList;

	private File parentDirectory;
	
	private Display display = null;

	public PicViewSwt() {

		// ======================================================
		// Create the main Display object that represents the UI
		// subsystem and contains the single UI handling thread
		// ======================================================
		display = Display.getDefault();

		mainShell = new Shell(display, SWT.BORDER | SWT.APPLICATION_MODAL	);
		mainShell.setSize(200, 150);
		mainShell.setLocation(AppConfig.getInstance().MainLeft, AppConfig.getInstance().MainTop);
		mainShell.open();

		// =====================
		// Set the Window Title
		// =====================
		mainShell.setText("Pic View");

		// Create the monitor window
		monitor = new MonitorShell(display);

		setupDirectoryList();
		setupFileList();

		String lastOpenedImagePath = "C:/Documents and Settings/All Users/Documents/My Pictures/Sample Pictures/Sunset.jpg";
		try {
			File lastImageFile = new File(lastOpenedImagePath);
			setSubDirectory(lastImageFile.getParentFile().getParentFile());
			fileList.selectFile(lastImageFile.getParentFile());
			monitor.loadImage(lastImageFile);
		} catch (Exception e) {
			setSubDirectory(new File(System.getProperty("user.dir")));
		}

		fileList.list.setEnabled(false);
		dirList.list.setEnabled(true);
		dirList.list.setFocus();

		mainShell.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Logger.d("Shell Key down");
				windowKeyEvent(e);
			}
		});

		// =====================================
		// Main UI event dispatch loop
		// that handles all UI events from all
		// SWT components created as children of
		// the main Display object
		// =====================================
		while (!display.isDisposed()) {
			// ===================================================
			// Wrap each event dispatch in an exception handler
			// so that if any event causes an exception it does
			// not break the main UI loop
			// ===================================================
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Main Display event handler loop has exited");
	}

	private void windowKeyEvent(Event e) {
		if (e.keyCode == SWT.ESC) {
			monitor.handleKeyEvent(e);
			mainShell.setActive();
		} else if (e.keyCode == 'm') {
			e.doit = false; // Inhibit default event
			monitor.handleKeyEvent(e);
			mainShell.setActive();
		} else if (e.keyCode == 'h') {
			e.doit = false; // Inhibit default event
			/// Todo display help text
		}else if (e.keyCode == 'x') {
			e.doit = false; // Inhibit default event
			
			AppConfig.getInstance().saveConfig(); 
			display.dispose();
		}
	}

	private void setSubDirectory(File dir) {
		parentDirectory = dir.getParentFile();
		updateDirectoryList(parentDirectory);

		dirList.selectFile(dir);
		updateImageList(dir);
	}

	private void RightArrow() {
		if (fileList.list.isFocusControl() == true) {
			File selectedDir = fileList.getSelectedFile();
			if (selectedDir != null) {
				if (selectedDir.isDirectory() == true) {
					Logger.d("Going up!");

					setSubDirectory(selectedDir);
				}
			}
		} else {
			if (fileList.getCount() > 0) {

				dirList.list.setEnabled(false);
				fileList.list.setEnabled(true);
				fileList.list.setFocus();

				if (fileList.list.getSelectionIndex() < 0) {
					fileList.list.select(0);
				}
			}
		}

		displaySelectedImage();
	}

	private void LeftArrow() {
		if (fileList.list.isFocusControl() == true) {

			fileList.list.setEnabled(false);
			dirList.list.setEnabled(true);
			dirList.list.setFocus();

		} else {
			Logger.d("Going down!");

			File oldParent = parentDirectory;
			File oldSubdir = dirList.getSelectedFile();

			setSubDirectory(oldParent);

			updateImageList(oldParent);
			fileList.selectFile(oldSubdir);
		}
	}

	private void displaySelectedImage() {
		File selectedFile = fileList.getSelectedFile();
		if (selectedFile != null) {
			if (selectedFile.isFile() == true) {
				monitor.loadImage(selectedFile);
			}
		}
	}

	private void setupFileList() {

		fileList = new FileList(mainShell);
		fileList.list.setBounds(width - 1, -1, width,
				mainShell.getClientArea().height);
		// Selection change listener
		fileList.list.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Logger.d("Image selection changed");

				displaySelectedImage();
			}
		});
		// Button pressed
		fileList.list.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) {
				Logger.d("Filelist key down");
				if (e.keyCode == SWT.ARROW_RIGHT) {
					e.doit = false; // Inhibit default event

					RightArrow();
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					e.doit = false; // Inhibit default event

					LeftArrow();
				} else {
					windowKeyEvent(e);
				}
			}
		});
	}

	private void setupDirectoryList() {

		dirList = new FileList(mainShell);

		dirList.list.setBounds(0, 0, mainShell.getClientArea().width/2, mainShell.getClientArea().height);
		// Selection change listener
		dirList.list.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Logger.d("Sub directory selection changed");

				if (dirList.getSelectedFile() != null) {
					updateImageList(dirList.getSelectedFile());
				}
			}
		});
		// Button pressed
		dirList.list.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) {
				Logger.d("Directorylist key down");
				if (e.keyCode == SWT.ARROW_RIGHT) {
					e.doit = false; // Inhibit default event

					RightArrow();
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					e.doit = false; // Inhibit default event

					LeftArrow();
				}
				else {
					windowKeyEvent(e);
				}
			}
		});
	}

	private void updateImageList(File directory) {
		fileList.clear();

		// get all the sub directories from the directory
		for (File file : directory.listFiles()) {
			if (file.isDirectory() == true) {
				fileList.add(file);
			}
		}

		// get all the image files from the parent directory
		for (File file : directory.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".jpg") == true) {
				fileList.add(file);
			}
		}

		fileList.list.setSelection(0);
	}

	private void updateDirectoryList(File parentDir) {
		dirList.clear();

		// get all sub directories from the parent directory
		File[] dList = parentDirectory.listFiles();
		for (File dir : dList) {
			if (dir.isDirectory() == true) {
				dirList.add(dir);
			}
		}

		dirList.list.setSelection(0);
	}

	// Main routine
	public static void main(String[] args) {
		new PicViewSwt();
	}
}
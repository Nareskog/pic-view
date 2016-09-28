package se.tmn.picview;
import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class FileList {
	private List list;
	private ArrayList<File> files = new ArrayList<File>();

	public FileList(Shell shell) {
		list = new List(shell, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);

		list.setBackground(new Color(null, 0, 0, 0));
		list.setForeground(new Color(null, 255, 255, 255));
	}
	
	public void addListener(int eventType, Listener listener) {
		list.addListener(eventType, listener);
	}

	public boolean isFocusControl() {
		return list.isFocusControl();
	}
	
	public void setBounds(int x, int y, int width, int height) {
		list.setBounds(x, y, width, height);
	}
	public void select(int index) {
		list.select(index);
	}
	public int getSelectionIndex() {
		return list.getSelectionIndex();
	}
	public void enable() {
		list.setEnabled(true);
		list.setFocus();
	}
	public void disable() {
		list.setEnabled(false);
	}

	public int getCount() {
		return list.getItemCount();
	}

	public File getFile(int index) {
		return files.get(index);
	}

	public void add(File file) {
		list.add(file.getName());
		files.add(file);
	}

	public void clear() {
		files.clear();
		list.removeAll();
		list.setSelection(-1);
	}

	public void selectFile(File file) {
		for (int i = 0; i < files.size(); i++) {
			if (file.getAbsolutePath().equals(files.get(i).getAbsolutePath()) == true) {
				list.setSelection(i);
			}
		}
	}

	public File getSelectedFile() {
		File result = null;
		if (list.getSelectionIndex() >= 0) {
			result = files.get(list.getSelectionIndex());
		}
		return result;
	}
}

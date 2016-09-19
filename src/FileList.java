import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class FileList {
	public List list;
	private ArrayList<File> files = new ArrayList<File>();

	public FileList(Shell shell) {
		list = new List(shell, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		files = new ArrayList<File>();

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

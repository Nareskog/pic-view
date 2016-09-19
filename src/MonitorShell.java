import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MonitorShell {
	private Display display;
	private Canvas canvas;
	final private Shell monitorShell;

	private Rectangle monitorBounds;

	private Image image = null;

	private String lastFilePath = "";

	public MonitorShell(final Display d) {
		display = d;

		System.out.println("Creating new child Shell");

		// =========================================
		// Create a Shell (window) from the Display
		// =========================================
		monitorShell = new Shell(display, SWT.TITLE | SWT.RESIZE);
		monitorShell.setBounds(AppConfig.getInstance().MonitorLeft,
				AppConfig.getInstance().MonitorTop,
				AppConfig.getInstance().MonitorWidth,
				AppConfig.getInstance().MonitorHeight);

		monitorShell.setText("PicView");
		monitorShell.open();

		monitorShell.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				resizeCanvas();
			}
		});
		monitorShell.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Logger.d("Monitor Key down");
				handleKeyEvent(e);
			}
		});

		canvas = new Canvas(monitorShell, SWT.NONE);
		canvas.setBackground(canvas.getDisplay()
				.getSystemColor(SWT.COLOR_BLACK));

		canvas.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Logger.d("Canvas Key down");
				handleKeyEvent(e);
			}
		});
		resizeCanvas();
	}

	private void resizeCanvas() {
		canvas.setBounds(0, 0, monitorShell.getClientArea().width,
				monitorShell.getClientArea().height);
	}

	public void handleKeyEvent(Event e) {
		if (e.keyCode == SWT.ESC) {
			if (monitorShell.getMaximized() == true) {
				monitorShell.setBounds(monitorBounds);
				monitorShell.setMaximized(false);
			}
		} else if (e.keyCode == 'm') {
			monitorBounds = monitorShell.getBounds();
			monitorShell.setMaximized(true);
		}
	}

	public void loadImage(final File file) {
		Logger.d("Loads image: " + file.getName());

		if (lastFilePath != file.getAbsolutePath()) {
			lastFilePath = file.getAbsolutePath();

			AppConfig.getInstance().CurrentPath = lastFilePath;
			Logger.d("Loads image: " + lastFilePath);

			canvas.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {

					try {
						image = new Image(display, new FileInputStream(
								lastFilePath));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						image = null;
					}
					if (image != null) {
						float imageDAR = (float) image.getBounds().width
								/ (float) image.getBounds().height;
						float canvasDAR = (float) canvas.getBounds().width
								/ (float) canvas.getBounds().height;
						if (imageDAR < canvasDAR) {
							int offset = canvas.getBounds().width
									- (int) (canvas.getBounds().height * imageDAR);
							e.gc.drawImage(
									image,
									0,
									0,
									image.getBounds().width,
									image.getBounds().height,
									offset / 2,
									0,
									(int) (canvas.getBounds().height * imageDAR),
									canvas.getBounds().height);
						} else {
							int offset = canvas.getBounds().height
									- (int) (canvas.getBounds().width / imageDAR);
							e.gc.drawImage(image, 0, 0,
									image.getBounds().width,
									image.getBounds().height, 0, offset / 2,
									canvas.getBounds().width,
									(int) (canvas.getBounds().width / imageDAR));
						}

						image.dispose();
					}
				}
			});
			canvas.redraw();
			// canvas.update();
		}
	}
}

package se.tmn.picview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MonitorShell {
	final static String TextFileName = "picview.txt";

	private Display display;
	private Canvas canvas;
	final private Shell monitorShell;

	private Rectangle monitorBounds;

	private Image image = null;

	private Label topLabel = null;
	private Label bottomLabel = null;

	private String lastFilePath = "";

	public MonitorShell(final Display d) {
		display = d;

		System.out.println("Creating new child Shell");

		// =========================================
		// Create a Shell (window) from the Display
		// =========================================
		monitorShell = new Shell(display, SWT.BORDER | SWT.RESIZE);
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

		topLabel = new Label(canvas, SWT.CENTER);
		topLabel.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		topLabel.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		bottomLabel = new Label(canvas, SWT.CENTER);
		bottomLabel.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		bottomLabel.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

		// topLabel.setText("Teststräng");
		// bottomLabel.setText("Kollar storleken");

		resizeCanvas();
	}

	private void resizeCanvas() {
		canvas.setBounds(0, 0, monitorShell.getClientArea().width,
				monitorShell.getClientArea().height);

		updateLabel();
	}

	private void updateLabel() {
		int textHeight = canvas.getSize().y / 20;

		for (Label label : new Label[] { topLabel, bottomLabel }) {
			FontData[] fD = label.getFont().getFontData();
			fD[0].setHeight(textHeight);
			label.setFont(new Font(display, fD[0]));

			GC gc = new GC(label);
			Point textSize = gc.textExtent(label.getText());
			gc.dispose();

			int margin = (10 * textSize.y / 30);
			Rectangle textRect = new Rectangle(
					(canvas.getSize().x - (textSize.x + margin)) / 2,
					canvas.getSize().y - (textSize.y * 13 / 10), textSize.x
							+ margin, textSize.y);

			if (label == topLabel) {
				textRect.y = textRect.y - (textSize.y * 13 / 10);
			}
			label.setBounds(textRect);

			label.setVisible(label.getText().equals("") == false);
		}
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

			lookForText(file);

			AppConfig.getInstance().CurrentPath = lastFilePath;
			Logger.d("Loads image: " + lastFilePath);

			canvas.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {

					try {
						image = new Image(display, new FileInputStream(
								lastFilePath));
					} catch (FileNotFoundException e1) {
						Logger.e("File not found: " + lastFilePath);
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
		}
	}

	private static String openedTextPath = "";
	private static ArrayList<String> textFileContent = new ArrayList<String>();

	public void lookForText(File picFile) {
		topLabel.setText("");
		bottomLabel.setText("");

		File textFile = new File(picFile.getParent() + "/" + TextFileName);
		// Does the file exist?
		if (textFile.exists() == true) {
			// Is it already loaded?
			if (textFile.getAbsolutePath() != openedTextPath) {
				openedTextPath = textFile.getAbsolutePath();
				textFileContent = new ArrayList<String>();
				BufferedReader in;
				try {
					in = new BufferedReader(new InputStreamReader(
							new FileInputStream(textFile), "UTF-8"));

					String str;
					while ((str = in.readLine()) != null) {
						textFileContent.add(str);
					}
				} catch (Exception ex) {
					Logger.e("Failed loading picutre texts: " + openedTextPath,
							ex);
				}
			}

			int pictureTextFound = 0;
			for (int i = 0; i < textFileContent.size(); i++) {
				String s = textFileContent.get(i);
				if (s.toLowerCase().equals(picFile.getName().toLowerCase()) == true) {
					pictureTextFound = 1;
				} else if (s == "") {
					pictureTextFound = 0;
				} else if (pictureTextFound == 1) {
					topLabel.setText(s);
					pictureTextFound = 2;
				} else if (pictureTextFound == 2) {
					bottomLabel.setText(s);
					pictureTextFound = 0;
				}
			}
		}

		if ((topLabel.getText().equals("") == false)
				&& (bottomLabel.getText().equals("") == true)) {
			String temp = bottomLabel.getText();
			bottomLabel.setText(topLabel.getText());
			topLabel.setText(temp);
		}
		updateLabel();
	}
}


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

/**
 * This is an example of how to replace a URL in a PDF document. This will only
 * replace the URL that the text refers to and not the text itself.
 *
 * @author Ben Litchfield
 */
public final class PdfRemoveAnno {
	/**
	 * Constructor.
	 */
	private PdfRemoveAnno() {
		// utility class
	}

	private static void doRemoveAnnotation(File file) throws IOException {
		PDDocument doc = null;

		try {

			String newFile = file.getPath() + ".noanno.pdf";
			File fnewFile = new File(newFile);
			if (fnewFile.exists() && !fnewFile.isDirectory()) {
				// do something

				int dialogResult = JOptionPane.showConfirmDialog(null,
						String.format("Do you want to overwrite %s?", newFile), "Warning", JOptionPane.YES_NO_OPTION);

				if (dialogResult != JOptionPane.YES_OPTION) {
					return;
				}
			}
			doc = PDDocument.load(file);

			// int pageNum = 0;
			for (PDPage page : doc.getPages()) {
				// pageNum++;
				List<PDAnnotation> annotations = page.getAnnotations();
				// List<PDAnnotation> newanno = page.getAnnotations();
				page.setAnnotations(null);
				for (PDAnnotation annotation : annotations) {
					PDAnnotation annot = annotation;
					if (annot instanceof PDAnnotationLink) {

						PDAnnotationLink link = (PDAnnotationLink) annot;
						PDAction action = link.getAction();
						if (action instanceof PDActionURI) {
							// PDActionURI uri = (PDActionURI)action;
							// String oldURI = uri.getURI();
							// String newURI = null; //
							// "http://pdfbox.apache.org";
							// System.out.println( "Page " + pageNum +":
							// Replacing " + oldURI + " with " + newURI );
							// uri.setURI( newURI );
						}
					}
				}
			}
			doc.save(newFile);

		} finally {
			if (doc != null) {
				doc.close();
			}
		}
	}

	/**
	 * This will read in a document and replace all of the urls with
	 * http://pdfbox.apache.org. <br />
	 * see usage() for commandline
	 *
	 * @param args
	 *            Command line arguments.
	 *
	 * @throws IOException
	 *             If there is an error during the process.
	 */
	public static void main(String[] args) {
		try {
			File file;
			if (args.length == 0)
				file = getFileFromUser();
			else
				file = new File(args[0]);

			if (file != null) {
				// label.setText(file.getPath());

				doRemoveAnnotation(file);

				// label.setText("done!");
			}
			System.exit(0);
		} catch (Exception ex) {
			setWarningMsg(ex.getMessage());
		}
		System.exit(0);
	}

	// https://stackoverflow.com/a/34176153
	public static void setWarningMsg(String text) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane optionPane = new JOptionPane(text, JOptionPane.WARNING_MESSAGE);
		JDialog dialog = optionPane.createDialog("PDFRemoveAnno");
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}

	/**
	 * This will print out a message telling how to use this example.
	 */
	private static void usage() {
		// System.err.println("usage: " + PdfRemoveAnno.class.getName() + "
		// <input-file> <output-file>");
	}

	private static File getFileFromUser() {
		JFrame frame = new JFrame("PDF Remove Annotation");
		JLabel label = new JLabel("PDF Remove Annotation");
		frame.getContentPane().add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		FileSystemView filesys = FileSystemView.getFileSystemView();
		// File[] roots = filesys.getRoots();
		File df = filesys.getHomeDirectory();

		JFileChooser filechooser = new JFileChooser(df.getPath());
		filechooser.setDialogTitle("Select PDF to Remove Annotations");
		FileFilter filter = new FileFilter() {

			@Override
			public String getDescription() {
				return "*.pdf";
			}

			@Override
			public boolean accept(File f) {
				String name = f.getName();
				name = name.toLowerCase();
				return name.endsWith(".pdf");
			}
		};
		filechooser.setFileFilter(filter);

		int selected = filechooser.showOpenDialog(frame);
		if (selected != JFileChooser.APPROVE_OPTION) {
			usage();
			return null;
		}
		return filechooser.getSelectedFile();

	}
}

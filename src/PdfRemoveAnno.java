
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

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;

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
	public static void main(String[] args) throws IOException {
		PDDocument doc = null;
		try {

			JFrame frame = new JFrame("PDF Remove Annotation");
			JLabel label = new JLabel("PDF Remove Annotation");
			frame.getContentPane().add(label);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);

			JFileChooser filechooser = new JFileChooser();
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
				return;
			}
			File file = filechooser.getSelectedFile();

			doc = PDDocument.load(file);
			label.setText(file.getPath());
			String newFile = file.getPath() + ".noanno.pdf";

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
			label.setText("done!");

		} finally {
			if (doc != null) {
				doc.close();
			}

			System.exit(0);
		}
	}

	/**
	 * This will print out a message telling how to use this example.
	 */
	private static void usage() {
		System.err.println("usage: " + PdfRemoveAnno.class.getName() + " <input-file> <output-file>");
	}
}

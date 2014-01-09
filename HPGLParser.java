package org.golovin.jtcad.format;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.golovin.jtcad.geometry.Figure;
import org.golovin.jtcad.geometry.Pad;
import org.golovin.jtcad.geometry.Trace;

public class HPGLParser {
	public HPGLParser() {
		// TODO Auto-generated constructor stub
	}

	public List<Figure> parse(String filename) {
		String all_cmd = "";

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(filename),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < lines.size(); i++) {
			String str = lines.get(i);

			str = str.replaceAll("[\t\n\r ]", "");
			str = str.toUpperCase();

			all_cmd += str;
		}

		String[] arr_cmd = all_cmd.split(";");

		int[] last_pupa = { 0, 0 };
		List<Figure> figs = new ArrayList<Figure>();
		Figure cur_fig = null;

		for (int i = 0; i < arr_cmd.length; i++) {
			// System.out.print(arr_cmd[i]);

			if (arr_cmd[i].equals("PU")) {
				// System.out.println("\t: pen up");

				while (!arr_cmd[i].equals("PD") && i < arr_cmd.length - 1) {
					i++;
					if (arr_cmd[i].startsWith("PA") && arr_cmd[i].contains(",")) {
						// System.out.print("\t\tPU : PA");

						String[] coordinates = arr_cmd[i]
								.replaceFirst("PA", "").split(",");

						int x = Integer.parseInt(coordinates[0]);
						int y = Integer.parseInt(coordinates[1]);

						// System.out.println(" " + x + " " + y);

						last_pupa = new int[] { x, y };
					} else {
						// System.out.println("\tPU : " + arr_cmd[i]
						// + "\t: ignoring");
					}
				}
				if (arr_cmd[i].equals("PD")) {
					i--;
				}
			} else if (arr_cmd[i].equals("PD")) {
				// System.out.println("\t: pen down");

				cur_fig = new Trace();

				cur_fig.addPoint(last_pupa);

				while (!arr_cmd[i].equals("PU") && i < arr_cmd.length - 1) {
					i++;
					if (arr_cmd[i].startsWith("PA") && arr_cmd[i].contains(",")) {
						// System.out.print("\t\tPD : PA");
						String[] coordinates = arr_cmd[i]
								.replaceFirst("PA", "").split(",");

						int x = Integer.parseInt(coordinates[0]);
						int y = Integer.parseInt(coordinates[1]);

						cur_fig.addPoint(new int[] { x, y });

						// System.out.println(" " + x + " " + y);
					} else {
						// System.out.println("\tPD : " + arr_cmd[i]
						// + "\t: ignoring");
					}
				}
				
				if (cur_fig != null) {
					figs.add(cur_fig);
				}
				
				if (arr_cmd[i].equals("PU")) {
					i--; // some kind of black magic
					// I can not remember why do I need this
				}
			} else {
				// System.out.println("\t: ignoring");
			}
		}

		List<Pad> pads = getPads(figs);
		//figs = cleanup(figs);
		//figs = stroke(figs);
		figs.addAll(pads);

		return figs;
	}

	private List<Pad> getPads(List<Figure> input) {

		List<Pad> pads = new ArrayList<Pad>();

		for (Figure f : input) {
			/*
			// zero length (including pads)
			if (f.getPoints().get(0)[0] == f.getPoints().get(
					f.getPoints().size() - 1)[0]
					&& f.getPoints().get(0)[1] == f.getPoints().get(
							f.getPoints().size() - 1)[1]
					&& f.getPoints().size() == 5) {
				// pads.add(f);

				int increment = 15;
				int[][] points = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 },
						{ 0, 0 } };

				for (int i = 0; i < 2; i++) {
					int x0 = f.getPoints().get(0 + i)[0];
					int y0 = f.getPoints().get(0 + i)[1];
					int x1 = f.getPoints().get(2 + i)[0];
					int y1 = f.getPoints().get(2 + i)[1];

					int x = x1 - x0, y = y1 - y0;

					double angle = Math.atan2(y, x);

					int length = (int) Math.sqrt(x * x + y * y);

					int dx = (int) Math.abs((length + increment)
							* Math.cos(angle)), dy = (int) Math
							.abs((length + increment) * Math.sin(angle));

					points[0 + i][0] = x0 - dx / 2;
					points[0 + i][1] = (i % 2 == 0) ? y0 + dy : y0 - dy;
					points[2 + i][0] = x1 + dx / 2;
					points[2 + i][1] = (i % 2 == 0) ? y1 - dy : y1 + dy;
				}

				points[4] = points[0];

				Figure fig = new Pad();
				for (int i = 0; i < 5; i++) {
					fig.addPoint(points[i]);
				}*/
				pads.add(new Pad(f));
			//}
		}

		return pads;
	}
}

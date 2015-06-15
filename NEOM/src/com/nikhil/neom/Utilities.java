package com.nikhil.neom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Utilities {
	final String states[] = { "ESTBLSH", "SYNSENT", "SYNRECV", "FWAIT1",
			"FWAIT2", "TMEWAIT", "CLOSED", "CLSWAIT", "LASTACK", "LISTEN",
			"CLOSING", "NSYNRECV" };

	public class Connection {
		String src;
		String spt;
		String dst;
		String dpt;
		String uid;
		String pro;
		String stat; // Chnage
	}

	private final String getAddress(final String hexa) {
		try {
			final long v = Long.parseLong(hexa, 16);
			final long adr = (v >>> 24) | (v << 24) | ((v << 8) & 0x00FF0000)
					| ((v >> 8) & 0x0000FF00);
			return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "."
					+ ((adr >> 8) & 0xff) + "." + (adr & 0xff);
		} catch (Exception e) {
			Log.w("NEOM", e.toString(), e);
			return "-1.-1.-1.-1";
		}
	}

	private final String getAddress6(final String hexa) {
		try {
			final String ip4[] = hexa.split("0000000000000000FFFF0000");

			if (ip4.length == 2) {
				final long v = Long.parseLong(ip4[1], 16);
				final long adr = (v >>> 24) | (v << 24)
						| ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
				return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "."
						+ ((adr >> 8) & 0xff) + "." + (adr & 0xff);
			} else {
				return "-2.-2.-2.-2";
			}
		} catch (Exception e) {
			Log.w("NEOM", e.toString(), e);
			return "-1.-1.-1.-1";
		}
	}

	private final int getInt16(final String hexa) {
		try {
			return Integer.parseInt(hexa, 16);
		} catch (Exception e) {
			Log.w("NEOM", e.toString(), e);
			return -1;
		}
	}

	public ArrayList<Connection> getPIDConnections(String PID) {
		ArrayList<Connection> connections = new ArrayList<Connection>();

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/"
					+ PID + "/net/tcp"));
			String line;

			while ((line = in.readLine()) != null) {
				line = line.trim();
				// LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);// split by white space
				int fieldn = 0;

				// for (String field : fields) {
				// LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				// }

				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();

				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);

				connection.src = getAddress(src[0]);
				connection.spt = String.valueOf(getInt16(src[1]));
				connection.dst = getAddress(dst[0]);
				connection.dpt = String.valueOf(getInt16(dst[1]));
				connection.uid = fields[7];
				connection.pro = "TCP";
				Integer conStat = getInt16(fields[3]);
				connection.stat = states[conStat - 1];
				connections.add(connection);
			}

			in.close();

			in = new BufferedReader(new FileReader("/proc/" + PID + "/net/udp"));

			while ((line = in.readLine()) != null) {
				line = line.trim();
				// LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);
				int fieldn = 0;

				// for (String field : fields) {
				// LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				// }

				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();
				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);
				connection.src = getAddress(src[0]);
				connection.spt = String.valueOf(getInt16(src[1]));
				connection.dst = getAddress(dst[0]);
				connection.dpt = String.valueOf(getInt16(dst[1]));
				connection.uid = fields[7];
				connection.pro = "UDP";
				Integer conStat = getInt16(fields[3]);
				connection.stat = states[conStat - 1];
				connections.add(connection);
			}

			in.close();

			in = new BufferedReader(
					new FileReader("/proc/" + PID + "/net/tcp6"));

			while ((line = in.readLine()) != null) {
				line = line.trim();
				// LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);
				int fieldn = 0;

				// for (String field : fields) {
				// LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				// }

				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();

				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);

				connection.src = getAddress6(src[0]);
				connection.spt = String.valueOf(getInt16(src[1]));
				connection.dst = getAddress6(dst[0]);
				connection.dpt = String.valueOf(getInt16(dst[1]));
				connection.uid = fields[7];
				connection.pro = "TCP6";
				Integer conStat = getInt16(fields[3]);
				connection.stat = states[conStat - 1];
				connections.add(connection);
			}

			in.close();

			in = new BufferedReader(
					new FileReader("/proc/" + PID + "/net/udp6"));

			while ((line = in.readLine()) != null) {
				line = line.trim();
				// LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);
				int fieldn = 0;

				// for (String field : fields) {
				// LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				// }

				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();

				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);

				connection.src = getAddress6(src[0]);
				connection.spt = String.valueOf(getInt16(src[1]));
				connection.dst = getAddress6(dst[0]);
				connection.dpt = String.valueOf(getInt16(dst[1]));
				connection.uid = fields[7];
				connection.pro = "UDP6";

				Integer conStat = getInt16(fields[3]);
				connection.stat = states[conStat - 1];
				connections.add(connection);
			}

			in.close();
		} catch (Exception e) {
			Log.w("NEOM", e.toString(), e);
		}

		return connections;
	}

	private void getAllPID() {
		try {
			ExecuteCMD execmd = new ExecuteCMD();
			String regex = "\\d+";
			Pattern p = Pattern.compile(regex);
			File proc_dir = new File("/proc");
			String[] content_list = proc_dir.list();
			String cmdOut;
			for (String name : content_list) {
				if (new File("/proc/" + name).isDirectory()) {
					Matcher m = p.matcher(name);
					if (m.matches()) {
						String[] cmd = { "ls -l " + "/proc/" + name + "/exe" };
						cmdOut = execmd.RunAsRoot(cmd);
						if (cmdOut == null) {
							cmdOut = "Kernel Process";
							Log.i("NEOM in getPID", "NULL");
						} else
							Log.i("NEOM in getPID", cmdOut);

						// getProcessName(name)
					}
				}
			}
		} catch (Exception ex) {
			Log.i("NEOM in getPID", ex.toString());
		}

	}

}

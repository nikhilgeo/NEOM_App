package com.nikhil.neom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import android.util.Log;

public class Utilities {
	final String states[] = { "ESTBLSH", "SYNSENT", "SYNRECV", "FWAIT1",
			"FWAIT2", "TMEWAIT", "CLOSED", "CLSWAIT", "LASTACK", "LISTEN",
			"CLOSING", "UNKNOWN" };

	public class Connection {
		String src;
		String spt;
		String dst;
		String dpt;
		String uid;
		String pro;
	}

	private final String getAddress(final String hexa) {
		try {
			final long v = Long.parseLong(hexa, 16);
			final long adr = (v >>> 24) | (v << 24) | ((v << 8) & 0x00FF0000)
					| ((v >> 8) & 0x0000FF00);
			return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "."
					+ ((adr >> 8) & 0xff) + "." + (adr & 0xff);
		} catch (Exception e) {
			Log.w("NetworkLog", e.toString(), e);
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
			Log.w("NetworkLog", e.toString(), e);
			return "-1.-1.-1.-1";
		}
	}

	private final int getInt16(final String hexa) {
		try {
			return Integer.parseInt(hexa, 16);
		} catch (Exception e) {
			Log.w("NetworkLog", e.toString(), e);
			return -1;
		}
	}

	public ArrayList<Connection> getConnections(String PID) {
		ArrayList<Connection> connections = new ArrayList<Connection>();

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/"
					+ PID + "/net/tcp"));
			String line;

			while ((line = in.readLine()) != null) {
				line = line.trim();
				LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);
				int fieldn = 0;

				for (String field : fields) {
					LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				}

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

				connections.add(connection);
			}

			in.close();

			in = new BufferedReader(new FileReader("/proc/" + PID + "/net/udp"));

			while ((line = in.readLine()) != null) {
				line = line.trim();
				LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);
				int fieldn = 0;

				for (String field : fields) {
					LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				}

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

				connections.add(connection);
			}

			in.close();

			in = new BufferedReader(
					new FileReader("/proc/" + PID + "/net/tcp6"));

			while ((line = in.readLine()) != null) {
				line = line.trim();
				LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);
				int fieldn = 0;

				for (String field : fields) {
					LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				}

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

				connections.add(connection);
			}

			in.close();

			in = new BufferedReader(
					new FileReader("/proc/" + PID + "/net/udp6"));

			while ((line = in.readLine()) != null) {
				line = line.trim();
				LogCat.d("Netstat: " + line);
				String[] fields = line.split("\\s+", 10);
				int fieldn = 0;

				for (String field : fields) {
					LogCat.d("Field " + (fieldn++) + ": [" + field + "]");
				}

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

				connections.add(connection);
			}

			in.close();
		} catch (Exception e) {
			Log.w("NetworkLog", e.toString(), e);
		}

		return connections;
	}
    
	
}

/**
 * 
 */
package com.nikhil.neom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import android.util.Log;

/**
 * @author nikhil
 * 
 */
public class ExecuteCMD {
	public String RunAsRoot(String[] cmds) {
		String cmdOutput = null;
		String read_line = null;
		try {
			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(
					suProcess.getOutputStream());

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					suProcess.getInputStream()));
			for (String tmpCmd : cmds) {
				if (tmpCmd != null)
					os.writeBytes(tmpCmd + "\n");
			}
			os.writeBytes("exit\n");
			os.flush();

			while ((read_line = stdInput.readLine()) != null) {

				cmdOutput = cmdOutput + read_line + "\n";
			}

		} catch (Exception e) {
			Log.i("NEOM:", e.getMessage());
			cmdOutput = "Error";
		}
		return cmdOutput;
	}

}

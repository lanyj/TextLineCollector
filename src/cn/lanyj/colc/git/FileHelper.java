package cn.lanyj.colc.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHelper {

	public static boolean isTextFile(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			int size = Math.min(1024, (int) (file.length()));
			char[] buf = new char[size];
			reader.read(buf);
			for(int i = 0; i < size; i++) {
				if(buf[i] == 0) {
					return false;
				}
			}
			return true;
//			return !reader.lines().anyMatch((line) -> {
//				return line.chars().anyMatch((c) -> {
//					return c == 0;
//				});
//			});
		} catch (IOException e) {
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static String readFile(File file) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			reader.lines().forEach((t) -> {
				sb.append(t + "\n");
			});
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		return null;
	}
	
}

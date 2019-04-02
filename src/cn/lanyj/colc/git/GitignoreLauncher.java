package cn.lanyj.colc.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.lanyj.colc.core.interfaces.Filter;

public class GitignoreLauncher {
	static final Pattern COMMENT = Pattern.compile("^\\s*#");
	static final Pattern BLANK = Pattern.compile("^\\s*$");

	List<GitignoreFilter> filters = new ArrayList<>();
	File ignore;

	boolean logDetail = false;

	String pwd;

	int txtFileCount = 0;
	int binFileCount = 0;

	long txtLineCount = 0;
	long txtSize = 0;
	long binSize = 0;

	public GitignoreLauncher(boolean logDetail) throws Exception {
		this(".gitignore", logDetail);
	}

	public GitignoreLauncher(String path, boolean logDetail) throws Exception {
		this.logDetail = logDetail;
		if (path != null) {
			this.ignore = new File(path);
			if(this.ignore == null || !this.ignore.exists()) {
				throw new FileNotFoundException("File '" + path + "' not found");
			}

			GitignoreProcessor gitignoreProcessor = new GitignoreProcessor(getIgnoreContent());
			List<String> patterns = gitignoreProcessor.getPatterns();
			detail("Patterns");
			for (String p : patterns) {
				detail(p);
				filters.add(new GitignoreFilter(p));
			}
			detail("");
		} else {
			File[] cs = new File(System.getProperty("user.dir")).listFiles();
			if (cs.length > 0) {
				this.ignore = cs[0];
			} else {
				this.ignore = new File(System.getProperty("user.dir"));
			}
		}

		run();
	}

	public void run() throws IOException {
		String path = ignore.getCanonicalPath();
		int index = path.lastIndexOf(File.separatorChar);
		if (index == -1) {
			return;
		} else {
			path = path.substring(0, index + 1);
			this.pwd = path;
			File root = new File(path);
			for (File file : root.listFiles()) {
				if (file.isDirectory()) {
					colc(new File(file.getName() + File.separatorChar));
				} else {
					colc(new File(file.getName()));
				}
			}
		}
	}

	private String getIgnoreContent() throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.ignore)));
			reader.lines().filter((t) -> {
				if (BLANK.matcher(t).find()) {
					return false;
				}
				if (COMMENT.matcher(t).find()) {
					return false;
				}
				return true;
			}).distinct().forEach((t) -> {
				sb.append(t + "\n");
			});
			return sb.toString();
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}

	private void detail(String msg) {
		if (logDetail) {
			System.out.println(msg);
		}
	}

	private String fileSize(long size) {
		float tmp = 0;
		char suffix = 'B';
		if (size > 1024L * 1024 * 1024 * 1024) {
			suffix = 'T';
			tmp = size / (1024f * 1024 * 1024 * 1024);
		} else if (size > 1024L * 1024 * 1024) {
			suffix = 'G';
			tmp = size / (1024f * 1024 * 1024);
		} else if (size > 1024L * 1024) {
			suffix = 'M';
			tmp = size / (1024f * 1024);
		} else if (size > 1024L) {
			suffix = 'K';
			tmp = size / (1024f);
		} else {
			suffix = 'B';
			tmp = size;
		}
		return String.format("%10.4f %c", tmp, suffix);
	}

	@Override
	public String toString() {
		return "------------------------------------------------\n" + "Colc on '" + this.pwd + "'\n"
				+ "------------------------------------------------\n"
				+ String.format("txt file count     = %12d\ntxt line count     = %12d\ntxt file size      = %12d\n",
						txtFileCount, txtLineCount, txtSize)
				+ "------------------------------------------------\n"
				+ String.format("bin file count     = %12d\nbin file size      = %s\n", binFileCount, fileSize(binSize))
				+ "------------------------------------------------\n";
	}

	private void colc(File file) {
		if (!file.exists()) {
			return;
		}
		for (Filter filter : filters) {
			if (filter.filter(file)) {
				if (logDetail) {
					if (file.isDirectory()) {
						detail(String.format("[PASS] <%-32s> \"%s\"", filter.toString(),
								file.getPath() + File.separatorChar));
					} else {
						detail(String.format("[PASS] <%-32s> \"%s\"", filter.toString(), file.getPath()));
					}
				}
				return;
			}
		}

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				colc(child);
			}
		} else {
			boolean txt = FileHelper.isTextFile(file);
			if (txt) {
				long lC = getLineCount(file);
				txtFileCount++;
				txtLineCount += lC;
				txtSize += file.length();

				if (logDetail) {
					detail(String.format("[COLC] <TEXT>, %14d,            \"%s\"", lC, file.getPath()));
				}
			} else {
				binFileCount++;
				binSize += file.length();

				if (logDetail) {
					detail(String.format("[COLC] <BINY>,   %s,            \"%s\"", fileSize(file.length()),
							file.getPath()));
				}
			}
		}
	}

	private static long getLineCount(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			return reader.lines().count();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return 0;
	}

	private static void help() {
		System.out.println("Gitignore line collector, https://github.com/lanyj/TextLineCollector");
		System.out.println("-i: input file, default \".gitignore\"");
		System.out.println("-ni: no input file, for colc all files");
		System.out.println("-d: output detail, default false");
		System.out.println("-h: help doc");
	}

	public static void main(String[] args) throws Exception {
		String path = ".gitignore";
		boolean logDetail = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].trim().equals("-d")) {
				logDetail = true;
			} else if (args[i].trim().equals("-i")) {
				if (args.length <= i + 1) {
					System.out.println("invalid input parameter. for -i");
					return;
				}
				path = args[++i];
			} else if (args[i].trim().equals("-ni")) {
				path = null;
			} else {
				help();
				return;
			}
		}
		GitignoreLauncher launcher = new GitignoreLauncher(path, logDetail);
		System.out.println(launcher);
	}

}

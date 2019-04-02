package cn.lanyj.colc.core.interfaces.impl;
import java.io.File;
import java.util.regex.Pattern;

import cn.lanyj.colc.core.interfaces.Filter;

public class PatternFilter implements Filter {
	
	Pattern pattern;
	
	public PatternFilter(String regex) {
		this(regex, 0);
	}
	
	public PatternFilter(String regex, int flags) {
		this.pattern = Pattern.compile(regex, flags);
	}
	
	@Override
	public boolean filter(File entry) {
		if(entry.isDirectory()) {
			return pattern.matcher(entry.getPath() + File.separatorChar).find();
		} else {
			return pattern.matcher(entry.getPath()).find();
		}
	}
	
	@Override
	public String toString() {
		return pattern.pattern();
	}
	
}

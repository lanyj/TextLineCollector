package cn.lanyj.colc.git;

import java.io.File;

import cn.lanyj.colc.core.interfaces.impl.PatternFilter;

public class GitignoreFilter extends PatternFilter {
	
	public GitignoreFilter(String regex) {
		super(regex);
	}

	public boolean filter(File entry) {
		return super.filter(entry);
	}
	
}
